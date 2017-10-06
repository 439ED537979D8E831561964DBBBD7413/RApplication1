package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.rawalinfocom.rcontact.adapters.PhoneBookContactListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.ContactStorageConstants;
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
        .OnRippleCompleteListener, WsResponseListener, LoaderManager.LoaderCallbacks<Cursor> {


    PhoneBookContacts phoneBookContacts;
    ArrayList<UserProfile> arrayListUserProfile;
    ArrayList<UserProfile> arrayListNumberUserProfile;
    ArrayList<UserProfile> arrayListEmailUserProfile;
    //    ArrayList<UserProfile> arrayListFilteredUserProfile;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.image_right_center)
    ImageView imageRightCenter;
    @BindView(R.id.ripple_action_right_center)
    RippleView rippleActionRightCenter;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.recycler_view_contacts)
    RecyclerView recyclerViewContacts;
    @BindView(R.id.spinner_share_via)
    Spinner spinnerShareVia;
    @BindView(R.id.text_select_all)
    TextView textSelectAll;
    @BindView(R.id.checkbox_select_all)
    CheckBox checkboxSelectAll;
    @BindView(R.id.relative_select_options)
    RelativeLayout relativeSelectOptions;
    @BindView(R.id.activity_contact_listing)
    RelativeLayout activityContactListing;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    private String filterType = "";
//    public ArrayList<Object> arrayListPhoneBookContacts;

    private ProfileMobileMapping profileMobileMapping;
    private TableProfileMobileMapping tableProfileMobileMapping;

    private PhoneBookContactListAdapter phoneBookContactListAdapter;

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

        rippleActionRightCenter.setVisibility(View.VISIBLE);

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

                if (inputSearch.getVisibility() == View.VISIBLE) {

                    Utils.hideKeyBoard(ContactListingActivity.this);

                    inputSearch.setVisibility(View.GONE);
                    textToolbarTitle.setVisibility(View.VISIBLE);
                    recyclerViewContacts.setVisibility(View.VISIBLE);
                    imageRightCenter.setImageResource(R.drawable.ic_action_search);

                    if (filterType.equals("all")) {
                        phoneBookContactListAdapter.filter("");
                        phoneBookContactListAdapter.updateList(arrayListUserProfile);
                    } else {
                        setFilterList();
                    }

                } else {

                    textToolbarTitle.setVisibility(View.GONE);
                    inputSearch.setVisibility(View.VISIBLE);
                    inputSearch.requestFocus();
                    Utils.showKeyBoard(ContactListingActivity.this);

                    imageRightCenter.setImageResource(R.drawable.ic_close);
                }

                break;
//            case R.id.ripple_action_right_center:
//               /* Log.i("onComplete", phoneBookContactListAdapter.getArrayListCheckedPositions()
//                        .toString());*/
//                if (phoneBookContactListAdapter.getArrayListCheckedPositions().size() > 0) {
//
//                    ContactReceiver receiver = new ContactReceiver();
//                    ArrayList<String> mobileNumbers = new ArrayList<>();
//                    ArrayList<String> emailIds = new ArrayList<>();
//
//                    for (int i = 0; i < phoneBookContactListAdapter.getArrayListCheckedPositions
//                            ().size(); i++) {
//                        int position = phoneBookContactListAdapter.getArrayListCheckedPositions()
//                                .get(i);
//                        if (StringUtils.length(arrayListFilteredUserProfile.get(position)
//                                .getMobileNumber()) > 0) {
//                            mobileNumbers.add(arrayListFilteredUserProfile.get(position)
//                                    .getMobileNumber());
//                        } else if (StringUtils.length(arrayListFilteredUserProfile.get(position)
//                                .getEmailId()) > 0) {
//                            emailIds.add(arrayListFilteredUserProfile.get(position)
//                                    .getEmailId());
//                        }
//                    }
//                    receiver.setEmailId(emailIds);
//                    receiver.setMobileNumber(mobileNumbers);
//
//                    if (!pmId.equalsIgnoreCase("-1")) {
//                        shareContactRcp(receiver);
//                    } else if (profileDataOperationVcard == null) {
//                        inviteContact(mobileNumbers, emailIds);
//                    } else {
//                        shareContactNonRcp(receiver);
//                    }
//                } else {
//                    Utils.showErrorSnackBar(this, activityContactListing, getString(R.string
//                            .please_select_one_contact));
//                }
//
//                break;
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
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onBackPressed();
//                        }
//                    }, 500);
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
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onBackPressed();
//                        }
//                    }, 500);
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

        textToolbarTitle.setText(getString(R.string.str_invite));

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightCenter.setOnRippleCompleteListener(this);
    }

    //<editor-fold desc="Private Methods">
    private void setupView() {

//        arrayListFilteredUserProfile = new ArrayList<>();
//        arrayListUserProfile.addAll(arrayListFilteredUserProfile);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this));

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
//                arrayListFilteredUserProfile.clear();
                inputSearch.getText().clear();
                Utils.hideSoftKeyboard(ContactListingActivity.this, inputSearch);
                if (position == 0) {
                    filterType = "all";
                    if (arrayListUserProfile.size() > 0) {
//                        arrayListFilteredUserProfile.addAll(arrayListUserProfile);
                        phoneBookContactListAdapter.updateList(arrayListUserProfile);
                    } else {
                        loadContacts();
                    }
                } else if (position == 1) {
                    filterType = "sms";
                    setFilterList();
//                    arrayListFilteredUserProfile.addAll(arrayListNumberUserProfile);
                } else if (position == 2) {
                    filterType = "email";
                    setFilterList();
//                    arrayListFilteredUserProfile.addAll(arrayListEmailUserProfile);
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
//                    recyclerViewContacts.setAdapter(phoneBookContactListAdapter);
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

    private void loadContacts() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void setFilterList() {

        arrayListNumberUserProfile.clear();
        arrayListEmailUserProfile.clear();

        for (int i = 0; i < arrayListUserProfile.size(); i++) {

            UserProfile userProfile = new UserProfile();

            if (filterType.equals("sms")) {
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

        if (filterType.equals("sms")) {
            if (arrayListNumberUserProfile != null && arrayListNumberUserProfile.size() > 0) {
                phoneBookContactListAdapter.updateList(arrayListNumberUserProfile);

            }
        } else {
            if (arrayListEmailUserProfile != null && arrayListEmailUserProfile.size() > 0) {
                phoneBookContactListAdapter.updateList(arrayListEmailUserProfile);

            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Utils.showProgressDialog(ContactListingActivity.this, "Please wait...", false);

        Set<String> set = new HashSet<>();
        set.add(ContactsContract.Data.MIMETYPE);
        set.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
        set.add(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        set.add(ContactsContract.CommonDataKinds.Email.ADDRESS);
        set.add(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);

        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = set.toArray(new String[0]);

        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)" +
                " and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " in (" + ContactStorageConstants.CONTACT_STORAGE + ")";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
        };
        String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + ") ASC";

        return new CursorLoader(ContactListingActivity.this, uri, projection, selection, selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        getContactsFromPhoneBook(cursor);
        setRCPUser();
        cursor.close();
        setRecyclerViewLayoutManager();

        Utils.hideProgressDialog();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void getContactsFromPhoneBook(Cursor cursor) {

        String mobileNumber = "";

        try {

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
                            case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                                userProfile.setMobileNumber(mobileNumber);
                                break;
                            case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                                userProfile.setEmailId(cursor.getString(cursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Email.ADDRESS)));
                                userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
                                        (ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)));
                                break;
                        }

                        if (!cursor.getString(cursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).equals(cursor.getString(mobile))) {
                            userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                        }

                        arrayListUserProfile.add(userProfile);

                    } catch (Exception e) {
                        Log.i("AllContacts", "Crash occurred when displaying contacts" + e
                                .toString());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void setRecyclerViewLayoutManager() {
        phoneBookContactListAdapter = new PhoneBookContactListAdapter(ContactListingActivity.this,
                arrayListUserProfile, new PhoneBookContactListAdapter.OnClickListener() {
            @Override
            public void onClick(String number, String email) {

                if (!number.equalsIgnoreCase("")) {

                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("sms_body", AppConstants.PLAY_STORE_LINK + getPackageName());
                    smsIntent.setData(Uri.parse("sms:" + number));
                    startActivity(smsIntent);

                } else {

                    ArrayList<String> mobileNumbers = new ArrayList<>();
                    ArrayList<String> emailIds = new ArrayList<>();

                    mobileNumbers.add(number);
                    emailIds.add(email);

                    inviteContact(mobileNumbers, emailIds);

                }
            }
        });
        recyclerViewContacts.setAdapter(phoneBookContactListAdapter);
    }

//    private class GetContactData extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Utils.showProgressDialog(ContactListingActivity.this, "Please wait...", false);
////            System.out.println("RContact start --> " + System.currentTimeMillis());
//        }
//
//        protected Void doInBackground(Void... urls) {
//
//            Cursor cursor = null;
//            String mobileNumber = "";
//
//            try {
//
//                Set<String> set = new HashSet<>();
//                set.add(ContactsContract.Data.MIMETYPE);
//                set.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                set.add(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//                set.add(ContactsContract.CommonDataKinds.Email.ADDRESS);
//                set.add(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);
//
//                Uri uri = ContactsContract.Data.CONTENT_URI;
//                String[] projection = set.toArray(new String[0]);
//
//                String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)" +
//                        " and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " in (" + ContactStorageConstants.CONTACT_STORAGE + ")";
//                String[] selectionArgs = {
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
//                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
//                };
//                String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
//                        + ") ASC";
//
//                cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
//                        sortOrder);
//
//                if (cursor != null) {
//
//                    final int mobile = cursor.getColumnIndex(ContactsContract.CommonDataKinds
//                            .Phone.NUMBER);
//
//                    while (cursor.moveToNext()) {
//                        try {
//
//                            if (cursor.getString(mobile) != null) {
//                                mobileNumber = Utils.getFormattedNumber(getApplicationContext(),
//                                        cursor.getString(mobile));
//                            }
//
//                            UserProfile userProfile = new UserProfile();
//
//                            String mimeType = cursor.getString(cursor.getColumnIndex
//                                    (ContactsContract.Data.MIMETYPE));
//                            switch (mimeType) {
//                                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
//                                    userProfile.setMobileNumber(mobileNumber);
//                                    break;
//                                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
//                                    userProfile.setEmailId(cursor.getString(cursor.getColumnIndex
//                                            (ContactsContract.CommonDataKinds.Email.ADDRESS)));
//                                    userProfile.setPmProfileImage(cursor.getString(cursor.getColumnIndex
//                                            (ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI)));
//                                    break;
//                            }
//
//                            if (!cursor.getString(cursor.getColumnIndex
//                                    (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).equals(cursor.getString(mobile))) {
//                                userProfile.setPmFirstName(cursor.getString(cursor.getColumnIndex
//                                        (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
//                            }
//
//                            arrayListUserProfile.add(userProfile);
//
//                        } catch (Exception e) {
//                            Log.i("AllContacts", "Crash occurred when displaying contacts" + e
//                                    .toString());
//                        }
//                    }
//                    cursor.close();
//                }
//
//                setRCPUser();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (cursor != null) cursor.close();
//            }
//
//            return null;
//        }
//
//        protected void onPostExecute(Void result) {
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Utils.hideProgressDialog();
//                    phoneBookContactListAdapter = new PhoneBookContactListAdapter(ContactListingActivity.this,
//                            arrayListUserProfile, new PhoneBookContactListAdapter.OnClickListener() {
//                        @Override
//                        public void onClick(String number, String email) {
//
//                            ArrayList<String> mobileNumbers = new ArrayList<>();
//                            ArrayList<String> emailIds = new ArrayList<>();
//
//                            mobileNumbers.add(number);
//                            emailIds.add(email);
//
//                            inviteContact(mobileNumbers, emailIds);
//
//                        }
//                    });
//                    recyclerViewContacts.setAdapter(phoneBookContactListAdapter);
//                }
//            });
//        }
//    }

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
