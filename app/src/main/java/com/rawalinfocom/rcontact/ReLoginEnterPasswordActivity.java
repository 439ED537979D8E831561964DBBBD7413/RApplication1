package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReLoginEnterPasswordActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.text_number)
    TextView textNumber;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_set_password_logo)
    ImageView imageSetPasswordLogo;
    @BindView(R.id.text_password_protected)
    TextView textPasswordProtected;
    @BindView(R.id.text_msg_enter_password)
    TextView textMsgEnterPassword;
    @BindView(R.id.input_enter_password)
    EditText inputEnterPassword;
    @BindView(R.id.linear_layout_edit_box)
    LinearLayout linearLayoutEditBox;
    @BindView(R.id.button_login)
    Button buttonLogin;
    @BindView(R.id.ripple_login)
    RippleView rippleLogin;
    @BindView(R.id.button_forgot_password)
    Button buttonForgotPassword;
    @BindView(R.id.ripple_forget_password)
    RippleView rippleForgetPassword;
    @BindView(R.id.relativeRootEnterPassword)
    RelativeLayout relativeRootEnterPassword;
    private String mobileNumber, isFrom = "";
    private Country selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_enter_password);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        rippleForgetPassword.setOnRippleCompleteListener(this);
        rippleLogin.setOnRippleCompleteListener(this);

        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textPasswordProtected.setTypeface(Utils.typefaceRegular(this));
        textMsgEnterPassword.setTypeface(Utils.typefaceRegular(this));
        inputEnterPassword.setTypeface(Utils.typefaceRegular(this));
        buttonLogin.setTypeface(Utils.typefaceRegular(this));
        buttonForgotPassword.setTypeface(Utils.typefaceRegular(this));

        mobileNumber = Utils.getStringPreference(ReLoginEnterPasswordActivity.this, AppConstants
                .PREF_REGS_MOBILE_NUMBER, "");
        selectedCountry = (Country) Utils.getObjectPreference(ReLoginEnterPasswordActivity.this,
                AppConstants.PREF_SELECTED_COUNTRY_OBJECT, Country.class);

        if (selectedCountry != null) {
            textNumber.setText(mobileNumber);
        }

        isFrom = getIntent().getStringExtra(AppConstants.PREF_IS_FROM);

        if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD) || isFrom.equals(AppConstants
                .PREF_RE_LOGIN)) {
            textToolbarTitle.setText(getResources().getString(R.string.password_verification));
        } else {
            textToolbarTitle.setText(getResources().getString(R.string.str_enter_password));
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_SEND_OTP">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_CHECK_NUMBER)) {
                WsResponseObject otpDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (otpDetailResponse != null && StringUtils.equalsIgnoreCase(otpDetailResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // set launch screen as OtpVerificationActivity
                    Utils.setIntegerPreference(ReLoginEnterPasswordActivity.this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MOBILE_REGISTRATION);

                    // Redirect to OtpVerificationActivity
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EXTRA_IS_FROM, AppConstants.PREF_FORGOT_PASSWORD);
                    startActivityIntent(ReLoginEnterPasswordActivity.this,
                            OtpVerificationActivity.class, bundle);
                    overridePendingTransition(R.anim.enter, R.anim.exit);

                } else {
                    if (otpDetailResponse != null) {
                        Log.e("error response", otpDetailResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                                otpDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_CHECK_LOGIN)) {
                WsResponseObject enterPassWordResponse = (WsResponseObject) data;

                if (enterPassWordResponse != null && StringUtils.equalsIgnoreCase
                        (enterPassWordResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // set launch screen as MainActivity
                    Utils.setIntegerPreference(this,
                            AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                    .LAUNCH_MAIN_ACTIVITY);

                    ProfileDataOperation profileDetail = enterPassWordResponse.getProfileDetail();
                    Utils.setObjectPreference(this, AppConstants
                            .PREF_REGS_USER_OBJECT, profileDetail);

                    Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID, profileDetail
                            .getRcpPmId());
                    storeProfileDataToDb(profileDetail);

                    Utils.setStringPreference(this, AppConstants.PREF_CALL_LOG_SYNC_TIME, profileDetail.getCallLogTimestamp());
                    Utils.setStringPreference(this, AppConstants.PREF_SMS_SYNC_TIME, profileDetail.getSmsLogTimestamp());

                    Utils.setStringPreference(this, AppConstants.PREF_USER_NAME, profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_FIRST_NAME,
                            profileDetail.getPbNameFirst());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_LAST_NAME,
                            profileDetail.getPbNameLast());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_JOINING_DATE,
                            profileDetail.getJoiningDate());

                    Utils.setStringPreference(this, AppConstants.PREF_USER_NUMBER, profileDetail.getVerifiedMobileNumber());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, profileDetail.getTotalProfileRateUser());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, profileDetail.getProfileRating());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_PHOTO, profileDetail.getPbProfilePhoto());

                    if (MoreObjects.firstNonNull(enterPassWordResponse.getReSync(), 0).equals(1)) {
                        Utils.setBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false);
                        Utils.setBooleanPreference(this, AppConstants.PREF_CALL_LOG_SYNCED, false);
                        Utils.setBooleanPreference(this, AppConstants.PREF_SMS_SYNCED, false);
                    }

                    Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));

                    // Redirect to MainActivity
                    if (isFrom.equals(AppConstants.PREF_RE_LOGIN)) {
                        Utils.hideProgressDialog();
                        Utils.setBooleanPreference(ReLoginEnterPasswordActivity.this,
                                AppConstants.PREF_TEMP_LOGOUT, false);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else {
                        deviceDetail();
                    }

                } else {

                    Utils.hideProgressDialog();

                    if (enterPassWordResponse != null) {
                        Log.e("error response", enterPassWordResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, enterPassWordResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "enterPassWordResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }

            if (serviceType.equalsIgnoreCase(WsConstants.REQ_STORE_DEVICE_DETAILS)) {
                WsResponseObject enterPassWordResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (enterPassWordResponse != null && StringUtils.equalsIgnoreCase
                        (enterPassWordResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();

                } else {

                    if (enterPassWordResponse != null) {
                        Log.e("error response", enterPassWordResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, enterPassWordResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "enterPassWordResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEnterPassword, getString(R
                                .string.msg_try_later));
                    }
                }
            }

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, "" + error
                    .getLocalizedMessage());
        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.ripple_login:

                String password = inputEnterPassword.getText().toString();
                if (StringUtils.isEmpty(password)) {
                    Utils.showErrorSnackBar(this, relativeRootEnterPassword,
                            getResources().getString(R.string.err_msg_please_enter_password));
                } else {
                    checkLogin(password);
                }

                break;

            case R.id.ripple_forget_password:
                sendOtp();
                break;
        }
    }

    private void sendOtp() {

        WsRequestObject otpObject = new WsRequestObject();
        otpObject.setCountryCode(selectedCountry.getCountryCodeNumber());
        otpObject.setMobileNumber(mobileNumber.replace("+91", ""));
        otpObject.setForgotPassword(1);
        otpObject.setDeviceId(getDeviceId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), otpObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_NUMBER, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                            WsConstants.REQ_CHECK_NUMBER);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void checkLogin(String password) {

        WsRequestObject enterPassWordObject = new WsRequestObject();
        enterPassWordObject.setMobileNumber(mobileNumber.replace("+", ""));
        enterPassWordObject.setPassword(StringUtils.trimToEmpty(password));
        if (isFrom.equals(AppConstants.PREF_RE_LOGIN) || Utils.getBooleanPreference
                (ReLoginEnterPasswordActivity.this,
                        AppConstants.PREF_TEMP_LOGOUT, false)) {
            enterPassWordObject.setReAuthenticate(1); // For Android Devices
        }
        enterPassWordObject.setCreatedBy("2"); // For Android Devices
        enterPassWordObject.setGcmToken(getDeviceTokenId());
//        enterPassWordObject.setDeviceId(getDeviceId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    enterPassWordObject,
                    null, WsResponseObject.class, WsConstants.REQ_CHECK_LOGIN, getString(R.string
                    .msg_please_wait), false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                            WsConstants.REQ_CHECK_LOGIN);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEnterPassword, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void storeProfileDataToDb(ProfileDataOperation profileDetail) {

        //<editor-fold desc="Basic Details">
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        UserProfile userProfile = new UserProfile();
        userProfile.setPmRcpId(profileDetail.getRcpPmId());
        userProfile.setPmFirstName(profileDetail.getPbNameFirst());
        userProfile.setPmLastName(profileDetail.getPbNameLast());
        userProfile.setProfileRating(profileDetail.getProfileRating());
        userProfile.setTotalProfileRateUser(profileDetail.getTotalProfileRateUser());
        userProfile.setPmProfileImage(profileDetail.getPbProfilePhoto());
        userProfile.setPmGender(profileDetail.getPbGender());

        tableProfileMaster.addProfile(userProfile);
        //</editor-fold>

        //<editor-fold desc="Mobile Number">
        TableMobileMaster tableMobileMaster = new TableMobileMaster(databaseHandler);

        ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber =
                profileDetail.getPbPhoneNumber();
        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber)) {
            for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                MobileNumber mobileNumber = new MobileNumber();
                mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(i)
                        .getPhoneId());
                mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(i)
                        .getPhoneType());
                mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber
                        .get(i).getPhonePublic()));
                mobileNumber.setMnmIsPrimary(String.valueOf(arrayListPhoneNumber.get(i).getPbRcpType()));
                mobileNumber.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                arrayListMobileNumber.add(mobileNumber);
            }
            tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);
        }
        //</editor-fold>

        //<editor-fold desc="Email Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileDetail.getPbEmailId();
            ArrayList<Email> arrayListEmail = new ArrayList<>();
            ArrayList<String> listOfVerifiedEmailIds = new ArrayList<>();
            for (int i = 0; i < arrayListEmailId.size(); i++) {
                Email email = new Email();
                email.setEmRecordIndexId(arrayListEmailId.get(i).getEmId());
                email.setEmEmailAddress(arrayListEmailId.get(i).getEmEmailId());
                email.setEmEmailType(arrayListEmailId.get(i).getEmType());
                email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(i).getEmPublic()));
                email.setEmIsVerified(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
//                email.setEmIsPrimary(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
                if (String.valueOf(arrayListEmailId.get(i).getEmRcpType()).equalsIgnoreCase("1")) {
                    listOfVerifiedEmailIds.add(arrayListEmailId.get(i).getEmEmailId());
                    Utils.setArrayListPreference(this, AppConstants.PREF_USER_VERIFIED_EMAIL,
                            listOfVerifiedEmailIds);
                }
                email.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                arrayListEmail.add(email);
            }

            TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);
            tableEmailMaster.addArrayEmail(arrayListEmail);
        }
        //</editor-fold>

        //<editor-fold desc="Organization Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbOrganization())) {
            ArrayList<ProfileDataOperationOrganization> arrayListOrganization = profileDetail
                    .getPbOrganization();
            ArrayList<Organization> organizationList = new ArrayList<>();
            for (int i = 0; i < arrayListOrganization.size(); i++) {
                Organization organization = new Organization();
                organization.setOmRecordIndexId(arrayListOrganization.get(i).getOrgId());
                organization.setOmOrganizationCompany(arrayListOrganization.get(i).getOrgName
                        ());
                organization.setOmOrganizationDesignation(arrayListOrganization.get(i)
                        .getOrgJobTitle());
                organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(i)
                        .getIsCurrent()));
                organization.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                organizationList.add(organization);
            }

            TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                    (databaseHandler);
            tableOrganizationMaster.addArrayOrganization(organizationList);
        }
        //</editor-fold>

        // <editor-fold desc="Website Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress())) {
//            ArrayList<String> arrayListWebsite = profileDetail.getPbWebAddress();
            ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = profileDetail
                    .getPbWebAddress();
            ArrayList<Website> websiteList = new ArrayList<>();
            for (int j = 0; j < arrayListWebsite.size(); j++) {
                Website website = new Website();
                website.setWmRecordIndexId(arrayListWebsite.get(j).getWebId());
                website.setWmWebsiteUrl(arrayListWebsite.get(j).getWebAddress());
                website.setWmWebsiteType(arrayListWebsite.get(j).getWebType());
                website.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                websiteList.add(website);
            }

            TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster(databaseHandler);
            tableWebsiteMaster.addArrayWebsite(websiteList);
        }
        //</editor-fold>

        //<editor-fold desc="Address Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            ArrayList<ProfileDataOperationAddress> arrayListAddress = profileDetail.getPbAddress();
            ArrayList<Address> addressList = new ArrayList<>();
            for (int j = 0; j < arrayListAddress.size(); j++) {
                Address address = new Address();
                address.setAmRecordIndexId(arrayListAddress.get(j).getAddId());
                address.setAmCity(arrayListAddress.get(j).getCity());
                address.setAmState(arrayListAddress.get(j).getState());
                address.setAmCountry(arrayListAddress.get(j).getCountry());
                address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress());
                address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                address.setAmStreet(arrayListAddress.get(j).getStreet());
                address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                if (arrayListAddress.get(j).getGoogleLatLong() != null && arrayListAddress.get(j)
                        .getGoogleLatLong().size() == 2) {
                    address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatLong().get(1));
                    address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLatLong().get(0));
                }
                address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j).getAddPublic()));
                address.setAmGoogleAddress(arrayListAddress.get(j).getGoogleAddress());
                address.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                addressList.add(address);
            }

            TableAddressMaster tableAddressMaster = new TableAddressMaster(databaseHandler);
            tableAddressMaster.addArrayAddress(addressList);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts())) {
            ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileDetail
                    .getPbIMAccounts();
            ArrayList<ImAccount> imAccountsList = new ArrayList<>();
            for (int j = 0; j < arrayListImAccount.size(); j++) {
                ImAccount imAccount = new ImAccount();
                imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                        .getIMAccountPublic()));
                imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
//                imAccount.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                imAccount.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                imAccountsList.add(imAccount);
            }

            TableImMaster tableImMaster = new TableImMaster(databaseHandler);
            tableImMaster.addArrayImAccount(imAccountsList);
        }
        //</editor-fold>

        // <editor-fold desc="Event Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            ArrayList<ProfileDataOperationEvent> arrayListEvent = profileDetail.getPbEvent();
            ArrayList<Event> eventList = new ArrayList<>();
            for (int j = 0; j < arrayListEvent.size(); j++) {
                Event event = new Event();
                event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                event.setEvmEventType(arrayListEvent.get(j).getEventType());
                event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j).getEventPublic()));
//                event.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                event.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                eventList.add(event);
            }

            TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);
            tableEventMaster.addArrayEvent(eventList);
        }
        //</editor-fold>
    }

    private void deviceDetail() {

        String model = android.os.Build.MODEL;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String brand = android.os.Build.BRAND;
        String device = android.os.Build.DEVICE;
        String secureAndroidId = Settings.Secure.getString(getContentResolver(), Settings.Secure
                .ANDROID_ID);

        WsRequestObject deviceDetailObject = new WsRequestObject();
        deviceDetailObject.setDmModel(StringUtils.defaultString(model));
        deviceDetailObject.setDmVersion(StringUtils.defaultString(androidVersion));
        deviceDetailObject.setDmBrand(StringUtils.defaultString(brand));
        deviceDetailObject.setDmDevice(StringUtils.defaultString(device));
        deviceDetailObject.setDmUniqueid(StringUtils.defaultString(secureAndroidId));
//        deviceDetailObject.setDmLocation(StringUtils.defaultString(locationString));

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_STORE_DEVICE_DETAILS, null, true).executeOnExecutor(AsyncTask
                            .THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT + WsConstants.REQ_STORE_DEVICE_DETAILS);
        }
        /*else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }
}
