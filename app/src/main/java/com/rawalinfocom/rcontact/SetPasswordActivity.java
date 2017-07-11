package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class SetPasswordActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.image_set_password_logo)
    ImageView imageSetPasswordLogo;
    @BindView(R.id.text_configure_password)
    TextView textConfigurePassword;
    @BindView(R.id.text_msg_set_password)
    TextView textMsgSetPassword;
    @BindView(R.id.input_set_password)
    EditText inputSetPassword;
    @BindView(R.id.input_set_confirm_password)
    EditText inputSetConfirmPassword;
    @BindView(R.id.linear_layout_edit_box)
    LinearLayout linearLayoutEditBox;
    @BindView(R.id.button_submit)
    Button buttonSubmit;
    @BindView(R.id.ripple_register)
    RippleView rippleRegister;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;
    @BindView(R.id.text_tip)
    TextView textTip;

    private String isFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleRegister.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(getResources().getString(R.string.set_password));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textConfigurePassword.setTypeface(Utils.typefaceRegular(this));
        textMsgSetPassword.setTypeface(Utils.typefaceRegular(this));
        inputSetPassword.setTypeface(Utils.typefaceRegular(this));
        inputSetConfirmPassword.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceRegular(this));
        textTip.setTypeface(Utils.typefaceRegular(this));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.EXTRA_IS_FROM);
        }

        if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD)) {
            textToolbarTitle.setGravity(Gravity.CENTER);
            rippleActionBack.setVisibility(View.GONE);
            buttonSubmit.setText(getString(R.string.action_submit));
        } else {
            textToolbarTitle.setGravity(Gravity.CENTER | Gravity.START);
            rippleActionBack.setVisibility(View.VISIBLE);
            buttonSubmit.setText(getString(R.string.action_register));
        }
    }

    private boolean isPasswordValid(String password) {
//        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^!&+=])(?=\\S+$).{8,}$";
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\\\\\\\!\\\"#$%&()*+,./:;" +
                "<=>?@\\\\[\\\\]^_{|}~])(?=\\S+$).{8,}$";
        return password.matches(pattern);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="SET_PASSWORD">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_SAVE_PASSWORD)) {
                WsResponseObject setPasswordResponse = (WsResponseObject) data;

                if (setPasswordResponse != null && StringUtils.equalsIgnoreCase(setPasswordResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD)) {
                        Utils.hideProgressDialog();
                        // Redirect to MobileNumberRegistrationActivity
                        Intent intent = new Intent(this, ReLoginEnterPasswordActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra(AppConstants.PREF_IS_FROM, AppConstants.PREF_FORGOT_PASSWORD);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                    } else {

                        // set launch screen as MainActivity
                        Utils.setIntegerPreference(this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                        .LAUNCH_MAIN_ACTIVITY);

                        ProfileDataOperation profileDetail = setPasswordResponse.getProfileDetail();
                        Utils.setObjectPreference(this, AppConstants
                                .PREF_REGS_USER_OBJECT, profileDetail);

                        Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID,
                                profileDetail.getRcpPmId());

                        Utils.setStringPreference(this, AppConstants.PREF_USER_NAME, profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_NUMBER, profileDetail.getVerifiedMobileNumber());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, profileDetail.getTotalProfileRateUser());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, profileDetail.getProfileRating());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_PHOTO, profileDetail.getPbProfilePhoto());

                        storeProfileDataToDb(profileDetail);

                        deviceDetail();
                    }

                } else {

                    Utils.hideProgressDialog();

                    if (setPasswordResponse != null) {
                        Log.e("error response", setPasswordResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "setPassword null");
                        Utils.showErrorSnackBar(this, layoutRoot, getString(R.string
                                .msg_try_later));
                    }
                }
            }
            //</editor-fold>
            // <editor-fold desc="SET_PASSWORD">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_STORE_DEVICE_DETAILS)) {
                WsResponseObject setPasswordResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (setPasswordResponse != null && StringUtils.equalsIgnoreCase(setPasswordResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // Redirect to MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    finish();

                } else {
                    if (setPasswordResponse != null) {
                        Log.e("error response", setPasswordResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "setPassword null");
                        Utils.showErrorSnackBar(this, layoutRoot, getString(R.string
                                .msg_try_later));
                    }
                }
            }
            //</editor-fold>
        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, layoutRoot, "" + error.getLocalizedMessage());
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:

                if (isFrom.equals(AppConstants.PREF_FORGOT_PASSWORD)) {
                    startActivity(new Intent(SetPasswordActivity.this, OtpVerificationActivity
                            .class));
                    finish();
                } else {
                    startActivity(new Intent(SetPasswordActivity.this,
                            ProfileRegistrationActivity.class));
                    finish();
                }
                break;
            case R.id.ripple_register:
                registerButtonClicked();
                break;
        }
    }

    private void registerButtonClicked() {
        String password = inputSetPassword.getText().toString();
        String confirmPassword = inputSetConfirmPassword.getText().toString();
        if (StringUtils.isEmpty(password)) {
            Utils.showErrorSnackBar(this, layoutRoot, getResources().getString(R.string
                    .err_msg_please_enter_password));
            return;
        }
        if (StringUtils.isEmpty(confirmPassword)) {
            Utils.showErrorSnackBar(this, layoutRoot, getResources().getString(R.string
                    .err_msg_please_confirm_password));
            return;
        }
        if (!password.equalsIgnoreCase(confirmPassword)) {
            Utils.showErrorSnackBar(this, layoutRoot, getResources().getString(R.string
                    .err_msg_password_not_match));
            return;
        }
        if (password.equalsIgnoreCase(confirmPassword)) {
            if (isPasswordValid(password)) {
                SavePassword(password, confirmPassword);
            } else {
                Utils.showErrorSnackBar(this, layoutRoot, getResources().getString(R.string
                        .msg_tip_password));
            }
        }
    }

    private void SavePassword(String password, String confirmPassword) {

        WsRequestObject setPassWordObject = new WsRequestObject();
        setPassWordObject.setPassword(StringUtils.trimToEmpty(password));
        setPassWordObject.setPassword_confirmation(StringUtils.trimToEmpty(confirmPassword));
        setPassWordObject.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        setPassWordObject.setCreatedBy("2"); // For Android Devices

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    setPassWordObject, null, WsResponseObject.class, WsConstants.REQ_SAVE_PASSWORD,
                    getString(R.string.msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                    WsConstants.REQ_SAVE_PASSWORD);
        } else {
            Utils.showErrorSnackBar(this, layoutRoot, getResources()
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
                mobileNumber.setMnmIsPrimary(arrayListPhoneNumber.get(i).getPbRcpType());
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
            for (int i = 0; i < arrayListEmailId.size(); i++) {
                Email email = new Email();
                email.setEmRecordIndexId(arrayListEmailId.get(i).getEmId());
                email.setEmEmailAddress(arrayListEmailId.get(i).getEmEmailId());
                email.setEmEmailType(arrayListEmailId.get(i).getEmType());
                email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(i).getEmPublic()));
                email.setEmIsVerified(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
//                email.setEmIsPrimary(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
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
                    .REQ_STORE_DEVICE_DETAILS, null, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT + WsConstants.REQ_STORE_DEVICE_DETAILS);
        }
        /*else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }
}