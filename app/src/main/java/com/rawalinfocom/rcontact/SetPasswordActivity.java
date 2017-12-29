package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
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
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableRelationMaster;
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
        textConfigurePassword.setTypeface(Utils.typefaceBold(this));
        textMsgSetPassword.setTypeface(Utils.typefaceRegular(this));
        inputSetPassword.setTypeface(Utils.typefaceRegular(this));
        inputSetConfirmPassword.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceRegular(this));
        textTip.setTypeface(Utils.typefaceRegular(this));

        Utils.setRoundedCornerBackground(buttonSubmit, ContextCompat.getColor
                (SetPasswordActivity.this, R.color.colorAccent), 5, 0, ContextCompat
                .getColor(SetPasswordActivity.this, R.color.colorAccent));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            isFrom = bundle.getString(AppConstants.EXTRA_IS_FROM);
        }

        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD)) {
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
                "<=>?@\\\\[\\\\]^_{|}~])(?=\\S+$).{6,}$";
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

                    if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD)) {
                        Utils.hideProgressDialog();
                        // Redirect to MobileNumberRegistrationActivity
                        Intent intent = new Intent(this, ReLoginEnterPasswordActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra(AppConstants.EXTRA_IS_FROM, AppConstants
                                .EXTRA_IS_FROM_FORGOT_PASSWORD);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                    } else {

                        // set launch screen as MainActivity
                        Utils.setIntegerPreference(this,
                                AppConstants.PREF_LAUNCH_SCREEN_INT, IntegerConstants
                                        .LAUNCH_MAIN_ACTIVITY);

                        Long date_firstLaunch = System.currentTimeMillis();
                        Utils.setLongPreference(this, AppConstants.PREF_RATE_APP_DATE, date_firstLaunch);

                        ProfileDataOperation profileDetail = setPasswordResponse.getProfileDetail();
                        Utils.setObjectPreference(this, AppConstants
                                .PREF_REGS_USER_OBJECT, profileDetail);

                        Utils.setStringPreference(this, AppConstants.PREF_USER_PM_ID,
                                profileDetail.getRcpPmId());

                        Utils.setStringPreference(this, AppConstants.PREF_USER_PM_BADGE,
                                profileDetail.getPmBadge());

                        Utils.setStringPreference(this, AppConstants.PREF_USER_NAME,
                                profileDetail.getPbNameFirst() + " " + profileDetail
                                        .getPbNameLast());

                        Utils.setStringPreference(this, AppConstants.PREF_USER_FIRST_NAME,
                                profileDetail.getPbNameFirst());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_LAST_NAME,
                                profileDetail.getPbNameLast());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_JOINING_DATE,
                                profileDetail.getJoiningDate());

                        Utils.setStringPreference(this, AppConstants.PREF_USER_NUMBER,
                                profileDetail.getVerifiedMobileNumber());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING,
                                profileDetail.getTotalProfileRateUser());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_RATING,
                                profileDetail.getProfileRating());
                        Utils.setStringPreference(this, AppConstants.PREF_USER_PHOTO,
                                profileDetail.getPbProfilePhoto());

                        Utils.setBooleanPreference(SetPasswordActivity.this, AppConstants
                                .PREF_DISABLE_PUSH, false);
                        Utils.setBooleanPreference(SetPasswordActivity.this, AppConstants
                                .PREF_DISABLE_EVENT_PUSH, false);
                        Utils.setBooleanPreference(SetPasswordActivity.this, AppConstants
                                .PREF_DISABLE_POPUP, false);

                        Utils.storeProfileDataToDb(SetPasswordActivity.this, profileDetail, databaseHandler);
                        Utils.setStringPreference(RContactApplication.getInstance(), AppConstants.PREF_SYNC_FIRST_TIME,
                                "first");
                        Utils.setBooleanPreference(RContactApplication.getInstance(), AppConstants.PREF_SYNC_RUNNING,
                                true);

                        deviceDetail();
                    }

                } else {

                    Utils.hideProgressDialog();

                    if (setPasswordResponse != null) {
                        Log.e("error response", setPasswordResponse.getMessage());
                        Utils.showErrorSnackBar(this, layoutRoot, setPasswordResponse.getMessage());
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

                    TableRelationMaster tableRelationMaster = new TableRelationMaster(databaseHandler);
                    tableRelationMaster.insertData();

                    Utils.setStringPreference(this, AppConstants.EXTRA_LOGIN_TYPE, "password");

                    Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String
                            .valueOf(System.currentTimeMillis()));
                    Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, true);
                    Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE, true);

                    Utils.setBooleanPreference(this, AppConstants.PREF_IS_LOGIN, true);
                    Utils.setContactArrayListPreference(RContactApplication.getInstance(), AppConstants.PREF_ALL_CONTACT,
                            new ArrayList());

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
                        Utils.showErrorSnackBar(this, layoutRoot, setPasswordResponse.getMessage());
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

                if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD)) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFrom.equals(AppConstants.EXTRA_IS_FROM_FORGOT_PASSWORD)) {
            startActivity(new Intent(SetPasswordActivity.this, OtpVerificationActivity
                    .class));
            finish();
        } else {
            startActivity(new Intent(SetPasswordActivity.this,
                    ProfileRegistrationActivity.class));
            finish();
        }
    }

    private void registerButtonClicked() {

        String password = inputSetPassword.getText().toString();
        String confirmPassword = inputSetConfirmPassword.getText().toString();

        if (password.startsWith(" ") || confirmPassword.startsWith(" ")
                || password.endsWith(" ") || confirmPassword.endsWith(" ")) {
            Utils.showErrorSnackBar(this, layoutRoot, getResources().getString(R.string
                    .err_msg_password_validation));
            return;
        }

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

            if (password.length() > 3) {
                SavePassword(password.trim(), confirmPassword.trim());
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
//        setPassWordObject.setDeviceId(getDeviceId());
        setPassWordObject.setCreatedBy("2"); // For Android Devices

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    setPassWordObject, null, WsResponseObject.class, WsConstants.REQ_SAVE_PASSWORD,
                    getString(R.string.msg_please_wait), true).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                    WsConstants.REQ_SAVE_PASSWORD);
        } else {
            Utils.showErrorSnackBar(this, layoutRoot, getResources()
                    .getString(R.string.msg_no_network));
        }
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
                    BuildConfig.WS_ROOT + WsConstants.REQ_STORE_DEVICE_DETAILS);
        }
        /*else {
            Utils.showErrorSnackBar(this, relativeRootProfileRegistration, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }
}