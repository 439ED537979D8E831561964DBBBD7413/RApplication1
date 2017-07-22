package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ContactReceiver;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
//    public ArrayList<Object> arrayListPhoneBookContacts;

    private ProfileMobileMapping profileMobileMapping;
    private TableProfileMobileMapping tableProfileMobileMapping;

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

        tableProfileMobileMapping = new TableProfileMobileMapping(getDatabaseHandler());

        init();
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
                    Utils.showErrorSnackBar(this, activityContactListing, getString(R.string
                            .please_select_one_contact));
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
                    Utils.showSuccessSnackBar(this, activityContactListing, getString(R.string
                            .invitation_shared));
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
                        Utils.showErrorSnackBar(this, activityContactListing, shareResponse
                                .getMessage());
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
                    Utils.showSuccessSnackBar(this, activityContactListing, getString(R.string
                            .invitation_sent));
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
                        Utils.showErrorSnackBar(this, activityContactListing, inviteResponse
                                .getMessage());
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

    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageRightLeft = ButterKnife.findById(includeToolbar, R.id.image_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        inputSearch = ButterKnife.findById(includeToolbar, R.id.input_search);

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightCenter.setOnRippleCompleteListener(this);

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

    //<editor-fold desc="Private Methods">
    private void setupView() {

        arrayListFilteredUserProfile = new ArrayList<>();
//        arrayListUserProfile.addAll(arrayListFilteredUserProfile);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));
        phoneBookContactListAdapter = new PhoneBookContactListAdapter(this,
                arrayListFilteredUserProfile);
        recyclerViewContacts.setAdapter(phoneBookContactListAdapter);

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
                    if (arrayListUserProfile.size() > 0) {
                        arrayListFilteredUserProfile.addAll(arrayListUserProfile);
                        phoneBookContactListAdapter.notifyDataSetChanged();
                    } else {
                        new GetContactData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else if (position == 1) {
                    setFilterList("sms");
                    arrayListFilteredUserProfile.addAll(arrayListNumberUserProfile);
                    phoneBookContactListAdapter.notifyDataSetChanged();
                } else if (position == 2) {
                    setFilterList("email");
                    arrayListFilteredUserProfile.addAll(arrayListEmailUserProfile);
                    phoneBookContactListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    phoneBookContactListAdapter.filter(s.toString());
                    recyclerViewContacts.setAdapter(phoneBookContactListAdapter);
                    if (phoneBookContactListAdapter.getItemCount() < 1) {
                        recyclerViewContacts.setVisibility(View.GONE);
                    } else {
                        recyclerViewContacts.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void setFilterList(String type) {

        arrayListNumberUserProfile.clear();
        arrayListEmailUserProfile.clear();

        for (int i = 0; i < arrayListUserProfile.size(); i++) {

            UserProfile userProfile = new UserProfile();

            if (type.equals("sms")) {
                if (!arrayListUserProfile.get(i).getMobileNumber().equals("")) {
                    userProfile.setMobileNumber(arrayListUserProfile.get(i).getMobileNumber());
                    userProfile.setPmFirstName(arrayListUserProfile.get(i).getPmFirstName());
                    arrayListNumberUserProfile.add(userProfile);
                }
            } else {
                if (!arrayListUserProfile.get(i).getEmailId().equals("")) {
                    userProfile.setEmailId(arrayListUserProfile.get(i).getEmailId());
                    userProfile.setPmFirstName(arrayListUserProfile.get(i).getPmFirstName());
                    arrayListEmailUserProfile.add(userProfile);
                }
            }
        }
    }

    private class GetContactData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(ContactListingActivity.this, "Please wait...", false);
            System.out.println("RContact start --> " + System.currentTimeMillis());
        }

        protected Void doInBackground(Void... urls) {

            Cursor cursor = null;
            String mobileNumber = "";

            try {

                Set<String> set = new HashSet<>();
                set.add(ContactsContract.Data.MIMETYPE);
                set.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
                set.add(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                set.add(ContactsContract.CommonDataKinds.Email.ADDRESS);

                Uri uri = ContactsContract.Data.CONTENT_URI;
                String[] projection = set.toArray(new String[0]);

                String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)" +
                        " and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " in (" + AppConstants
                        .CONTACT_STORAGES + ")";
                String[] selectionArgs = {
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                };
                String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        + ") ASC";

                cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                        sortOrder);

                if (cursor != null) {

                    final int mobile = cursor.getColumnIndex(ContactsContract.CommonDataKinds
                            .Phone.NUMBER);

                    while (cursor.moveToNext()) {
                        try {

                            if (cursor.getString(mobile) != null) {
                                mobileNumber = Utils.getFormattedNumber(getApplicationContext(),
                                        cursor.getString(mobile));
                            }

                            UserProfile userProfile = new UserProfile();

                            String mimeType = cursor.getString(cursor.getColumnIndex
                                    (ContactsContract.Data.MIMETYPE));
                            switch (mimeType) {
                                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                                    userProfile.setEmailId(cursor.getString(cursor.getColumnIndex
                                            (ContactsContract.CommonDataKinds.Email.ADDRESS)));
                                    break;
                                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                                    userProfile.setMobileNumber(mobileNumber);
                                    break;
                            }

                            userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));

                            arrayListUserProfile.add(userProfile);

                        } catch (Exception e) {
                            Log.i("AllContacts", "Crash occurred when displaying contacts" + e
                                    .toString());
                        }
                    }
                    cursor.close();
                }

                System.out.println("RContact half --> " + System.currentTimeMillis());

                setRCPUser();

            } catch (Exception e) {
                e.printStackTrace();
                if (cursor != null) cursor.close();
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    arrayListFilteredUserProfile.addAll(arrayListUserProfile);
                    Utils.hideProgressDialog();
                    phoneBookContactListAdapter.notifyDataSetChanged();
                    System.out.println("RContact end --> " + System.currentTimeMillis());
                }
            });
        }
    }

    private void setRCPUser() {

        for (int i = 0; i < arrayListUserProfile.size(); i++) {
            if (!arrayListUserProfile.get(i).getMobileNumber().equals("")) {
                boolean what = tableProfileMobileMapping.getPmIdFromNumber(arrayListUserProfile
                        .get(i).getMobileNumber());

                if (what)
                    arrayListUserProfile.remove(i);
            }
        }
    }

    private void isRCPUser() {

        for (int i = 0; i < arrayListUserProfile.size(); i++) {
            if (!arrayListUserProfile.get(i).getMobileNumber().equals("")) {
                profileMobileMapping = tableProfileMobileMapping
                        .getCloudPmIdFromProfileMappingFromNumber(arrayListUserProfile.get(i)
                                .getMobileNumber());

                if (profileMobileMapping != null) {
                    String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                    if (!StringUtils.isEmpty(cloudPmId)) {

                    }
                }
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void shareContactRcp(ContactReceiver receiver) {

        WsRequestObject uploadContactObject = new WsRequestObject();
//        uploadContactObject.setPmId(Integer.parseInt(getUserPmId()));
        uploadContactObject.setSendProfileType(IntegerConstants.SEND_PROFILE_RCP);
        uploadContactObject.setPmIdWhose(Integer.parseInt(pmId));
        uploadContactObject.setReceiver(receiver);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING + "_RCP", getResources().getString(R.string
                    .msg_please_wait),
                    true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                    WsConstants.REQ_RCP_PROFILE_SHARING);
        } else {
            Utils.showErrorSnackBar(this, activityContactListing, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void shareContactNonRcp(ContactReceiver receiver) {

        WsRequestObject uploadContactObject = new WsRequestObject();
//        uploadContactObject.setPmId(Integer.parseInt(getUserPmId()));
        uploadContactObject.setSendProfileType(IntegerConstants.SEND_PROFILE_NON_RCP);
        uploadContactObject.setReceiver(receiver);
        uploadContactObject.setContactData(profileDataOperationVcard);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING + "_NON", getResources().getString(R.string
                    .msg_please_wait),
                    true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                    WsConstants.REQ_RCP_PROFILE_SHARING);
        } else {
            Utils.showErrorSnackBar(this, activityContactListing, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void inviteContact
            (ArrayList<String> arrayListContactNumber, ArrayList<String>
                    arrayListEmail) {

        WsRequestObject inviteContactObject = new WsRequestObject();
        inviteContactObject.setArrayListContactNumber(arrayListContactNumber);
        inviteContactObject.setArrayListEmailAddress(arrayListEmail);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    inviteContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_SEND_INVITATION, getString(R.string.invitation_sending), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            WsConstants.WS_ROOT + WsConstants.REQ_SEND_INVITATION);
        } else {
            Utils.showErrorSnackBar(this, activityContactListing, getResources()
                    .getString(R.string.msg_no_network));
        }
    }
    //</editor-fold>
}


//    private void getContactData() {

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
//    }
