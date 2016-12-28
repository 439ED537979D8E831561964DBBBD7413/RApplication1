package com.rawalinfocom.rcontact.contacts;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileDetailActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;

    @BindView(R.id.text_joining_date)
    TextView textJoiningDate;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_cloud_name)
    TextView textCloudName;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_view_all_organization)
    TextView textViewAllOrganization;
    @BindView(R.id.linear_basic_detail)
    LinearLayout linearBasicDetail;
    @BindView(R.id.relative_basic_detail)
    RelativeLayout relativeBasicDetail;
    @BindView(R.id.image_call)
    ImageView imageCall;
    @BindView(R.id.text_label_phone)
    TextView textLabelPhone;
    @BindView(R.id.recycler_view_contact_number)
    RecyclerView recyclerViewContactNumber;
    @BindView(R.id.linear_phone)
    LinearLayout linearPhone;
    @BindView(R.id.image_email)
    ImageView imageEmail;
    @BindView(R.id.text_label_email)
    TextView textLabelEmail;
    @BindView(R.id.recycler_view_email)
    RecyclerView recyclerViewEmail;
    @BindView(R.id.linear_email)
    LinearLayout linearEmail;
    @BindView(R.id.image_website)
    ImageView imageWebsite;
    @BindView(R.id.text_label_website)
    TextView textLabelWebsite;
    @BindView(R.id.recycler_view_website)
    RecyclerView recyclerViewWebsite;
    @BindView(R.id.linear_website)
    LinearLayout linearWebsite;
    @BindView(R.id.image_address)
    ImageView imageAddress;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.recycler_view_address)
    RecyclerView recyclerViewAddress;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;
    @BindView(R.id.image_social_contact)
    ImageView imageSocialContact;
    @BindView(R.id.text_label_social_contact)
    TextView textLabelSocialContact;
    @BindView(R.id.recycler_view_social_contact)
    RecyclerView recyclerViewSocialContact;
    @BindView(R.id.linear_social_contact)
    LinearLayout linearSocialContact;
    @BindView(R.id.card_contact_details)
    CardView cardContactDetails;
    @BindView(R.id.image_event)
    ImageView imageEvent;
    @BindView(R.id.text_label_event)
    TextView textLabelEvent;
    @BindView(R.id.recycler_view_event)
    RecyclerView recyclerViewEvent;
    @BindView(R.id.linear_event)
    LinearLayout linearEvent;
    @BindView(R.id.image_gender)
    ImageView imageGender;
    @BindView(R.id.text_label_gender)
    TextView textLabelGender;
    @BindView(R.id.image_icon_gender)
    ImageView imageIconGender;
    @BindView(R.id.text_gender)
    TextView textGender;
    @BindView(R.id.linear_gender)
    LinearLayout linearGender;
    @BindView(R.id.card_other_details)
    CardView cardOtherDetails;
    @BindView(R.id.button_view_more)
    Button buttonViewMore;
    @BindView(R.id.ripple_view_more)
    RippleView rippleViewMore;
    @BindView(R.id.relative_section_view_more)
    RelativeLayout relativeSectionViewMore;
    @BindView(R.id.relative_root_profile_detail)
    RelativeLayout relativeRootProfileDetail;
    @BindView(R.id.linear_organization_detail)
    LinearLayout linearOrganizationDetail;
    @BindView(R.id.rating_user)
    RatingBar ratingUser;
    @BindView(R.id.linear_call_sms)
    LinearLayout linearCallSms;
    @BindView(R.id.button_call_log)
    Button buttonCallLog;
    @BindView(R.id.button_sms)
    Button buttonSms;

    String pmId, phoneBookId, contactName = "", cloudContactName = null;
    boolean displayOwnProfile = false;

    PhoneBookContacts phoneBookContacts;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        ButterKnife.bind(this);

        phoneBookContacts = new PhoneBookContacts(this);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
            } else {
                pmId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PHONE_BOOK_ID)) {
                phoneBookId = intent.getStringExtra(AppConstants.EXTRA_PHONE_BOOK_ID);
            } else {
                phoneBookId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            } else {
                contactName = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME)) {
                cloudContactName = intent.getStringExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME);
            }
        }

        if (pmId.equalsIgnoreCase(getUserPmId())) {
            displayOwnProfile = true;
        }

        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_view_more:
                if (relativeSectionViewMore.getVisibility() == View.VISIBLE) {
                    relativeSectionViewMore.setVisibility(View.GONE);
                    buttonViewMore.setText("View More");
                } else {
                    relativeSectionViewMore.setVisibility(View.VISIBLE);
                    buttonViewMore.setText("View Less");
                }
                break;

            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_GET_PROFILE_DETAIL">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DETAIL)) {
                WsResponseObject profileDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (profileDetailResponse != null && StringUtils.equalsIgnoreCase
                        (profileDetailResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    final ProfileDataOperation profileDetail = profileDetailResponse
                            .getProfileDetail();
                    setUpView(profileDetail);

                } else {
                    if (profileDetailResponse != null) {
                        Log.e("error response", profileDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textJoiningDate.setTypeface(Utils.typefaceRegular(this));
        textName.setTypeface(Utils.typefaceSemiBold(this));
        textCloudName.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));

        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);

        if (!StringUtils.equalsIgnoreCase(pmId, "-1")) {
            // RC Profile
            getProfileDetail();
        } else {
            // Non-RC Profile
            textJoiningDate.setVisibility(View.GONE);
            setUpView(null);
        }

        textName.setText(contactName);
        if (StringUtils.length(cloudContactName) > 0) {
            textCloudName.setText(cloudContactName);
        } else {
            textCloudName.setVisibility(View.GONE);
        }

        if (displayOwnProfile) {
            textToolbarTitle.setText(getString(R.string.title_my_profile));
            linearCallSms.setVisibility(View.GONE);
        } else {
            textToolbarTitle.setText("Profile Detail");
            linearCallSms.setVisibility(View.VISIBLE);
        }

    }

    private void setUpView(final ProfileDataOperation profileDetail) {

        //<editor-fold desc="Joining Date">
        if (profileDetail != null) {
            String joiningDate = StringUtils.defaultString(Utils.convertDateFormat(profileDetail
                    .getJoiningDate(), "yyyy-MM-dd HH:mm:ss", "dd'th' MMM, yyyy"), "-");
            textJoiningDate.setText("Joining Date:- " + joiningDate);
        }
        //</editor-fold>

        //<editor-fold desc="User Name">
        /*if (profileDetail != null) {
            textName.setText(profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
        }*/
        //</editor-fold>

        //<editor-fold desc="Organization Detail">

        // From Cloud
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                .getPbOrganization())) {
            arrayListOrganization.addAll(profileDetail.getPbOrganization());
        }

        // From PhoneBook
        Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(phoneBookId);
        ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganization = new
                ArrayList<>();

        if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
            while (contactOrganizationCursor.moveToNext()) {

                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();

                organization.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organization.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organization.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organization.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organization.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));

                if (!arrayListOrganization.contains(organization)) {
                    arrayListPhoneBookOrganization.add(organization);
                }

            }
            contactOrganizationCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListOrganization) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookOrganization)) {

            final ArrayList<ProfileDataOperationOrganization> tempOrganization = new ArrayList<>();
            tempOrganization.addAll(arrayListOrganization);
            tempOrganization.addAll(arrayListPhoneBookOrganization);

            if (tempOrganization.size() == 1) {
                textViewAllOrganization.setVisibility(View.GONE);
            } else {
                textViewAllOrganization.setVisibility(View.VISIBLE);
            }
            textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());
            textOrganization.setText(tempOrganization.get(0).getOrgName());

            textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAllOrganizations(tempOrganization);
                }
            });

        } else {
            linearOrganizationDetail.setVisibility(View.INVISIBLE);
        }
        //</editor-fold>

        //<editor-fold desc="User Rating">
        if (profileDetail != null) {
            textUserRating.setText(profileDetail.getTotalProfileRateUser());
            ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
        } else {
            textUserRating.setText("0");
            ratingUser.setRating(0);
            ratingUser.setEnabled(false);
        }
        //</editor-fold>

        //<editor-fold desc="Phone Number">

        // From Cloud
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
        ArrayList<String> arrayListCloudNumber = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbPhoneNumber
                ())) {
            arrayListPhoneNumber.addAll(profileDetail.getPbPhoneNumber());
            for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                String number = Utils.getFormattedNumber(this, arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                arrayListCloudNumber.add(number);
            }
        }

        // From PhoneBook
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(phoneBookId);
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumber = new ArrayList<>();

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            while (contactNumberCursor.moveToNext()) {

                ProfileDataOperationPhoneNumber phoneNumber = new
                        ProfileDataOperationPhoneNumber();

                phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, contactNumberCursor
                        .getString(contactNumberCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER))));
                phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));

                if (!arrayListCloudNumber.contains(phoneNumber.getPhoneNumber())) {
                    arrayListPhoneBookNumber.add(phoneNumber);
                }

            }
            contactNumberCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookNumber)) {
            ArrayList<Object> tempPhoneNumber = new ArrayList<>();
            tempPhoneNumber.addAll(arrayListPhoneNumber);
            tempPhoneNumber.addAll(arrayListPhoneBookNumber);

            linearPhone.setVisibility(View.VISIBLE);
            ProfileDetailAdapter phoneDetailAdapter = new ProfileDetailAdapter(this,
                    tempPhoneNumber, AppConstants.PHONE_NUMBER);
            recyclerViewContactNumber.setAdapter(phoneDetailAdapter);
        } else {
            linearPhone.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Email Id">

        // From Cloud
        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
        ArrayList<String> arrayListCloudEmail = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            arrayListEmail.addAll(profileDetail.getPbEmailId());
            for (int i = 0; i < arrayListEmail.size(); i++) {
                String email = arrayListEmail.get(i).getEmEmailId();
                arrayListCloudEmail.add(email);
            }
        }

        // From PhoneBook
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(phoneBookId);
        ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmail = new ArrayList<>();

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            while (contactEmailCursor.moveToNext()) {

                ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));

                if (!arrayListCloudEmail.contains(emailId.getEmEmailId())) {
                    arrayListPhoneBookEmail.add(emailId);
                }

            }
            contactEmailCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEmail) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEmail)) {
            ArrayList<Object> tempEmail = new ArrayList<>();
            tempEmail.addAll(arrayListEmail);
            tempEmail.addAll(arrayListPhoneBookEmail);
            linearEmail.setVisibility(View.VISIBLE);
            ProfileDetailAdapter emailDetailAdapter = new ProfileDetailAdapter(this, tempEmail,
                    AppConstants.EMAIL);
            recyclerViewEmail.setAdapter(emailDetailAdapter);
        } else {
            linearEmail.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Website">

        // From Cloud
        ArrayList<String> arrayListWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
        }

        // From PhoneBook
        Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(phoneBookId);
        ArrayList<String> arrayListPhoneBookWebsite = new ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            while (contactWebsiteCursor.moveToNext()) {

                String website = contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

                if (!arrayListWebsite.contains(website)) {
                    arrayListPhoneBookWebsite.add(website);
                }

            }
            contactWebsiteCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite)) {
            ArrayList<Object> tempWebsite = new ArrayList<>();
            tempWebsite.addAll(arrayListWebsite);
            tempWebsite.addAll(arrayListPhoneBookWebsite);

            linearWebsite.setVisibility(View.VISIBLE);
            ProfileDetailAdapter websiteDetailAdapter = new ProfileDetailAdapter(this,
                    tempWebsite, AppConstants.WEBSITE);
            recyclerViewWebsite.setAdapter(websiteDetailAdapter);
        } else {
            linearWebsite.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Address">

        // From Cloud
        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();
        ArrayList<String> arrayListCloudAddress = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            arrayListAddress.addAll(profileDetail.getPbAddress());
            for (int i = 0; i < arrayListAddress.size(); i++) {
                String address = arrayListAddress.get(i).getFormattedAddress();
                arrayListCloudAddress.add(address);
            }
        }

        // From PhoneBook
        Cursor contactAddressCursor = phoneBookContacts.getContactAddress(phoneBookId);
        ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddress = new ArrayList<>();

        if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
            while (contactAddressCursor.moveToNext()) {

                ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                address.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                address.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                address.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                address.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                        contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));

                if (!arrayListCloudAddress.contains(address.getFormattedAddress())) {
                    arrayListPhoneBookAddress.add(address);
                }

            }
            contactAddressCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookAddress)) {
            ArrayList<Object> tempAddress = new ArrayList<>();
            tempAddress.addAll(arrayListAddress);
            tempAddress.addAll(arrayListPhoneBookAddress);
            linearAddress.setVisibility(View.VISIBLE);
            ProfileDetailAdapter addressDetailAdapter = new ProfileDetailAdapter(this,
                    tempAddress, AppConstants.ADDRESS);
            recyclerViewAddress.setAdapter(addressDetailAdapter);
        } else {
            linearAddress.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account">

        // From Cloud
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();
        ArrayList<String> arrayListCloudImAccount = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts
                ())) {
            arrayListImAccount.addAll(profileDetail.getPbIMAccounts());
            for (int i = 0; i < arrayListImAccount.size(); i++) {
                String imAccount = arrayListImAccount.get(i).getIMAccountProtocol();
                arrayListCloudImAccount.add(imAccount);
            }
        }

        // From PhoneBook
        Cursor contactImAccountCursor = phoneBookContacts.getContactIm(phoneBookId);
        ArrayList<ProfileDataOperationImAccount> arrayListPhoneBookImAccount = new ArrayList<>();

        if (contactImAccountCursor != null && contactImAccountCursor.getCount() > 0) {
            while (contactImAccountCursor.moveToNext()) {

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                imAccount.setIMAccountDetails(contactImAccountCursor.getString
                        (contactImAccountCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                imAccount.setIMAccountType(phoneBookContacts.getImAccountType
                        (contactImAccountCursor,
                                contactImAccountCursor.getInt(contactImAccountCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Im.TYPE))));

                imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                        (contactImAccountCursor.getInt((contactImAccountCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                if (!arrayListCloudImAccount.contains(imAccount.getIMAccountDetails())) {
                    arrayListPhoneBookImAccount.add(imAccount);
                }

            }
            contactImAccountCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookImAccount)) {
            ArrayList<Object> tempImAccount = new ArrayList<>();
            tempImAccount.addAll(arrayListImAccount);
            tempImAccount.addAll(arrayListPhoneBookImAccount);
            linearSocialContact.setVisibility(View.VISIBLE);
            ProfileDetailAdapter imAccountDetailAdapter = new ProfileDetailAdapter(this,
                    tempImAccount, AppConstants.IM_ACCOUNT);
            recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
        } else {
            linearSocialContact.setVisibility(View.GONE);
        }
        //</editor-fold>

        if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookAddress))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookImAccount))
                ) {
            rippleViewMore.setVisibility(View.VISIBLE);
        } else {
            rippleViewMore.setVisibility(View.GONE);
        }

        // <editor-fold desc="Event">

        // From Cloud
        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            arrayListEvent.addAll(profileDetail.getPbEvent());
        }

        // From PhoneBook
        Cursor contactEventCursor = phoneBookContacts.getContactEvent(phoneBookId);
        ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEvent = new ArrayList<>();

        if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
            while (contactEventCursor.moveToNext()) {

                ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                event.setEventDate(contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE)));

                if (!arrayListEvent.contains(event)) {
                    arrayListPhoneBookEvent.add(event);
                }

            }
            contactEventCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEvent) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)) {
            ArrayList<Object> tempEvent = new ArrayList<>();
            tempEvent.addAll(arrayListEvent);
            tempEvent.addAll(arrayListPhoneBookEvent);
            linearEvent.setVisibility(View.VISIBLE);
            ProfileDetailAdapter eventDetailAdapter = new ProfileDetailAdapter(this, tempEvent,
                    AppConstants.EVENT);
            recyclerViewEvent.setAdapter(eventDetailAdapter);
        } else {
            linearEvent.setVisibility(View.GONE);
        }
        //</editor-fold>

//        linearGender.setVisibility(View.GONE);

        if (Utils.isArraylistNullOrEmpty(arrayListEvent)
//                && Utils.isArraylistNullOrEmpty(arrayListAddress)
                ) {
            cardOtherDetails.setVisibility(View.GONE);
        } else {
            cardOtherDetails.setVisibility(View.VISIBLE);
        }

    }


    //    private void showAllOrganizations(ProfileDataOperation profileDetail) {
    private void showAllOrganizations(ArrayList<ProfileDataOperationOrganization>
                                              arraylistOrganization) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_close);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        /*OrganizationListAdapter adapter = new OrganizationListAdapter(this, profileDetail
                .getPbOrganization());*/
        OrganizationListAdapter adapter = new OrganizationListAdapter(this, arraylistOrganization);
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    public RelativeLayout getRelativeRootProfileDetail() {
        return relativeRootProfileDetail;
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void getProfileDetail() {

        WsRequestObject profileDetailObject = new WsRequestObject();
        profileDetailObject.setPmId(pmId);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DETAIL, getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_PROFILE_DETAIL);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    //</editor-fold>
}
