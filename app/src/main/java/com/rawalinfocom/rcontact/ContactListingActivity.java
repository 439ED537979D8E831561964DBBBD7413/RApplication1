package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.PhoneBookContactListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ContactReceiver;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactListingActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    ImageView imageRightLeft;
    EditText inputSearch;

    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.spinner_share_via)
    Spinner spinnerShareVia;
    @BindView(R.id.relative_select_options)
    RelativeLayout relativeSelectOptions;
    @BindView(R.id.recycler_view_contacts)
    RecyclerView recyclerViewContacts;
    @BindView(R.id.activity_contact_listing)
    RelativeLayout activityContactListing;
    @BindView(R.id.text_select_all)
    TextView textSelectAll;
    @BindView(R.id.checkbox_select_all)
    CheckBox checkboxSelectAll;

    PhoneBookContacts phoneBookContacts;
    ArrayList<UserProfile> arrayListUserProfile;
    ArrayList<UserProfile> arrayListNumberUserProfile;
    ArrayList<UserProfile> arrayListEmailUserProfile;
    ArrayList<UserProfile> arrayListFilteredUserProfile;

    PhoneBookContactListAdapter phoneBookContactListAdapter;

//    public ArrayList<UserProfile> arrayListTempUserProfile;

    String pmId = "-1";
    ProfileDataOperation profileDataOperationVcard;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_listing);
        ButterKnife.bind(this);

        phoneBookContacts = new PhoneBookContacts(this);
        arrayListUserProfile = new ArrayList<>();
        arrayListNumberUserProfile = new ArrayList<>();
        arrayListEmailUserProfile = new ArrayList<>();

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
            } else {
                pmId = "-1";
            }
            if (intent.hasExtra(AppConstants.EXTRA_OBJECT_CONTACT)) {
                profileDataOperationVcard = (ProfileDataOperation) intent.getSerializableExtra
                        (AppConstants.EXTRA_OBJECT_CONTACT);
            } else {
                profileDataOperationVcard = null;
            }
        }

        init();
        getContactData();
        setupView();

    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_center:
               /* Log.i("onComplete", phoneBookContactListAdapter.getArrayListCheckedPositions()
                        .toString());*/
                if (phoneBookContactListAdapter.getArrayListCheckedPositions().size() > 0) {

                    ContactReceiver receiver = new ContactReceiver();
                    ArrayList<String> mobileNumbers = new ArrayList<>();
                    ArrayList<String> emailIds = new ArrayList<>();

                    for (int i = 0; i < phoneBookContactListAdapter.getArrayListCheckedPositions
                            ().size(); i++) {
                        int position = phoneBookContactListAdapter.getArrayListCheckedPositions()
                                .get(i);
                        if (StringUtils.length(arrayListFilteredUserProfile.get(position)
                                .getMobileNumber()) > 0) {
                            mobileNumbers.add(arrayListFilteredUserProfile.get(position)
                                    .getMobileNumber());
                        } else if (StringUtils.length(arrayListFilteredUserProfile.get(position)
                                .getEmailId()) > 0) {
                            emailIds.add(arrayListFilteredUserProfile.get(position)
                                    .getEmailId());
                        }
                    }
                    receiver.setEmailId(emailIds);
                    receiver.setMobileNumber(mobileNumbers);

                    if (!pmId.equalsIgnoreCase("-1")) {
                        shareContactRcp(receiver);
                    } else if (profileDataOperationVcard == null) {
                        inviteContact(mobileNumbers, emailIds);
                    } else {
                        shareContactNonRcp(receiver);
                    }
                } else {
                    Utils.showErrorSnackBar(this, activityContactListing, "Please select at least" +
                            " one contact");
                }

                break;
        }

    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            // <editor-fold desc="REQ_RCP_PROFILE_SHARING">
            if (serviceType.contains(WsConstants.REQ_RCP_PROFILE_SHARING)) {
                WsResponseObject shareResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (shareResponse != null && StringUtils.equalsIgnoreCase
                        (shareResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(this, activityContactListing, "Invitation Shared");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    }, 500);
                } else {
                    if (shareResponse != null) {
                        Log.e("error response", shareResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "shareResponse null");
                        Utils.showErrorSnackBar(this, activityContactListing, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_SEND_INVITATION">
            if (serviceType.contains(WsConstants.REQ_SEND_INVITATION)) {
                WsResponseObject inviteResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (inviteResponse != null && StringUtils.equalsIgnoreCase
                        (inviteResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(this, activityContactListing, "Invitation Sent " +
                            "successfully!");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    }, 500);
                } else {
                    if (inviteResponse != null) {
                        Log.e("error response", inviteResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "shareResponse null");
                        Utils.showErrorSnackBar(this, activityContactListing, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, activityContactListing, "" + error
                    .getLocalizedMessage());
        }

    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageRightLeft = ButterKnife.findById(includeToolbar, R.id.image_right_left);
//        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id
// .ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        inputSearch = ButterKnife.findById(includeToolbar, R.id.input_search);

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightCenter.setOnRippleCompleteListener(this);

        spinnerShareVia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrayListFilteredUserProfile.clear();
                phoneBookContactListAdapter.getArrayListCheckedPositions().clear();
                phoneBookContactListAdapter.isSelectAll(false);
                checkboxSelectAll.setChecked(false);
                inputSearch.getText().clear();
                Utils.hideSoftKeyboard(ContactListingActivity.this, inputSearch);
                if (position == 0) {
                    arrayListFilteredUserProfile.addAll(arrayListUserProfile);
                } else if (position == 1) {
                    arrayListFilteredUserProfile.addAll(arrayListNumberUserProfile);
                } else if (position == 2) {
                    arrayListFilteredUserProfile.addAll(arrayListEmailUserProfile);
                }
                phoneBookContactListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkboxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneBookContactListAdapter.isSelectAll(isChecked);
                if (!isChecked) {
                    phoneBookContactListAdapter.getArrayListCheckedPositions().clear();
                }

            }
        });

    }

    private void getContactData() {

      /*  ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(this, AppConstants
                .PREF_CONTACT_ID_SET);*/

      /*  for (int i = 0; i < arrayListContactIds.size(); i++) {

            //<editor-fold desc="Contact Number">
            Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(arrayListContactIds
                    .get(i));

            if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                while (contactNumberCursor.moveToNext()) {

                    UserProfile userProfile = new UserProfile();


                    userProfile.setPmFirstName(contactNumberCursor.getString(contactNumberCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

                    userProfile.setMobileNumber(Utils.getFormattedNumber(this,
                            contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER))));

                    arrayListUserProfile.add(userProfile);
                    arrayListNumberUserProfile.add(userProfile);

                }
                contactNumberCursor.close();
            }
            //</editor-fold>

            //<editor-fold desc="Email Id">
            Cursor contactEmailCursor = phoneBookContacts.getContactEmail(arrayListContactIds.get
                    (i));

            if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                while (contactEmailCursor.moveToNext()) {

                    UserProfile userProfile = new UserProfile();


                    userProfile.setPmFirstName(contactEmailCursor.getString(contactEmailCursor
                            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                    userProfile.setEmailId(contactEmailCursor.getString(contactEmailCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));

                    arrayListUserProfile.add(userProfile);
                    arrayListEmailUserProfile.add(userProfile);

                }
                contactEmailCursor.close();
            }
            //</editor-fold>
        }*/
    }

    private void setupView() {

        ArrayAdapter<String> spinnerAdapter;
        if (!pmId.equalsIgnoreCase("-1")) {
            spinnerAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, getResources()
                    .getStringArray(R.array.phonebook_contact_array_share));
        } else if (profileDataOperationVcard == null) {
            spinnerAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, getResources()
                    .getStringArray(R.array.phonebook_contact_array_invite));
        } else {
            spinnerAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, getResources()
                    .getStringArray(R.array.phonebook_contact_array_share));
        }

        spinnerShareVia.setAdapter(spinnerAdapter);

        arrayListFilteredUserProfile = new ArrayList<>();
        arrayListUserProfile.addAll(arrayListFilteredUserProfile);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

        /*arrayListTempUserProfile = new ArrayList<>();
        arrayListTempUserProfile.addAll(arrayListFilteredUserProfile);*/
        phoneBookContactListAdapter = new PhoneBookContactListAdapter(this,
                arrayListFilteredUserProfile);

        recyclerViewContacts.setAdapter(phoneBookContactListAdapter);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    phoneBookContactListAdapter.filter(s.toString());
                    recyclerViewContacts.setAdapter(phoneBookContactListAdapter);
                    if (phoneBookContactListAdapter.getItemCount() < 1) {
//                        textEmptyCountry.setVisibility(View.VISIBLE);
                        recyclerViewContacts.setVisibility(View.GONE);
                    } else {
//                        textEmptyCountry.setVisibility(View.GONE);
                        recyclerViewContacts.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    /*Utils.showErrorSnackBar(ContactListingActivity.this,
                            root, "No Country Found");*/
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void shareContactRcp(ContactReceiver receiver) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(Integer.parseInt(getUserPmId()));
        uploadContactObject.setSendProfileType(IntegerConstants.SEND_PROFILE_RCP);
        uploadContactObject.setPmIdWhose(Integer.parseInt(pmId));
        uploadContactObject.setReceiver(receiver);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING + "_RCP", getResources().getString(R.string
                    .msg_please_wait),
                    true).execute(WsConstants.WS_ROOT + WsConstants.REQ_RCP_PROFILE_SHARING);
        } else {
            Utils.showErrorSnackBar(this, activityContactListing, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void shareContactNonRcp(ContactReceiver receiver) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(Integer.parseInt(getUserPmId()));
        uploadContactObject.setSendProfileType(IntegerConstants.SEND_PROFILE_NON_RCP);
        uploadContactObject.setReceiver(receiver);
        uploadContactObject.setContactData(profileDataOperationVcard);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING + "_NON", getResources().getString(R.string
                    .msg_please_wait),
                    true).execute(WsConstants.WS_ROOT + WsConstants.REQ_RCP_PROFILE_SHARING);
        } else {
            Utils.showErrorSnackBar(this, activityContactListing, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void inviteContact(ArrayList<String> arrayListContactNumber, ArrayList<String>
            arrayListEmail) {

        WsRequestObject inviteContactObject = new WsRequestObject();
        inviteContactObject.setArrayListContactNumber(arrayListContactNumber);
        inviteContactObject.setArrayListEmailAddress(arrayListEmail);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    inviteContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_SEND_INVITATION, "Sending Invitation...", true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_SEND_INVITATION);
        } else {
            Utils.showErrorSnackBar(this, activityContactListing, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>

}
