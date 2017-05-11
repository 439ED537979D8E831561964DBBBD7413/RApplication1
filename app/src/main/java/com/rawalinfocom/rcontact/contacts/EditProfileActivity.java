package com.rawalinfocom.rcontact.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
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
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
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
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    private final String EVENT_DATE_FORMAT = "dd'th' MMM, yyyy";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 500;
    private static final String IMAGE_DIRECTORY_NAME = "RContactImages";
    public static String TEMP_PHOTO_FILE_NAME = "";

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.text_label_name)
    TextView textLabelName;
    @BindView(R.id.input_first_name)
    EditText inputFirstName;
    @BindView(R.id.input_last_name)
    EditText inputLastName;
    @BindView(R.id.linear_section_left)
    LinearLayout linearSectionLeft;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.linear_section_top)
    LinearLayout linearSectionTop;
    @BindView(R.id.image_add_phone)
    ImageView imageAddPhone;
    @BindView(R.id.text_label_phone)
    TextView textLabelPhone;
    @BindView(R.id.relative_contact_details)
    RelativeLayout relativeContactDetails;
    @BindView(R.id.linear_phone_details)
    LinearLayout linearPhoneDetails;
    @BindView(R.id.image_add_email)
    ImageView imageAddEmail;
    @BindView(R.id.text_label_email)
    TextView textLabelEmail;
    @BindView(R.id.relative_email_details)
    RelativeLayout relativeEmailDetails;
    @BindView(R.id.linear_email_details)
    LinearLayout linearEmailDetails;
    @BindView(R.id.image_add_website)
    ImageView imageAddWebsite;
    @BindView(R.id.text_label_website)
    TextView textLabelWebsite;
    @BindView(R.id.relative_website_details)
    RelativeLayout relativeWebsiteDetails;
    @BindView(R.id.linear_website_details)
    LinearLayout linearWebsiteDetails;
    @BindView(R.id.image_add_address)
    ImageView imageAddAddress;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.relative_address_details)
    RelativeLayout relativeAddressDetails;
    @BindView(R.id.linear_address_details)
    LinearLayout linearAddressDetails;
    @BindView(R.id.image_add_social_contact)
    ImageView imageAddSocialContact;
    @BindView(R.id.text_label_social_contact)
    TextView textLabelSocialContact;
    @BindView(R.id.relative_social_contact_details)
    RelativeLayout relativeSocialContactDetails;
    @BindView(R.id.linear_social_contact_details)
    LinearLayout linearSocialContactDetails;
    @BindView(R.id.image_add_organization)
    ImageView imageAddOrganization;
    @BindView(R.id.text_label_organization)
    TextView textLabelOrganization;
    @BindView(R.id.relative_organization_details)
    RelativeLayout relativeOrganizationDetails;
    @BindView(R.id.linear_organization_detail)
    LinearLayout linearOrganizationDetail;
    @BindView(R.id.image_add_event)
    ImageView imageAddEvent;
    @BindView(R.id.text_label_event)
    TextView textLabelEvent;
    @BindView(R.id.relative_event_details)
    RelativeLayout relativeEventDetails;
    @BindView(R.id.linear_event_details)
    LinearLayout linearEventDetails;
    @BindView(R.id.text_label_gender)
    TextView textLabelGender;
    @BindView(R.id.radio_male)
    RadioButton radioMale;
    @BindView(R.id.radio_female)
    RadioButton radioFemale;
    @BindView(R.id.radio_group_gender)
    RadioGroup radioGroupGender;
    @BindView(R.id.relative_root_edit_profile)
    RelativeLayout relativeRootEditProfile;

    EditText inputCountry;
    EditText inputState;
    EditText inputCity;
    EditText inputStreet;
    EditText inputNeighborhood;
    EditText inputPinCode;

    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;

    ArrayList<Object> arrayListPhoneNumberObject;
    ArrayList<Object> arrayListEmailObject;
    ArrayList<Object> arrayListWebsiteObject;
    ArrayList<Object> arrayListSocialContactObject;
    ArrayList<Object> arrayListAddressObject;
    ArrayList<Object> arrayListEventObject;
    ArrayList<Object> arrayListOrganizationObject;

    ArrayAdapter<String> spinnerPhoneAdapter, spinnerEmailAdapter, spinnerWebsiteAdapter,
            spinnerImAccountAdapter, spinnerEventAdapter;

    UserProfile userProfile;

    String formattedAddress;

    Bitmap selectedBitmap = null;
    private File mFileTemp;
    private Uri fileUri;

    boolean isStorageFromSettings = false, isCameraFromSettings = false;

    MaterialDialog permissionConfirmationDialog;

    ArrayList<ProfileDataOperation> arrayListProfile;
    int clickedPosition = -1;

    double mapLatitude = 0, mapLongitude = 0;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        arrayListProfile = new ArrayList<>();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStorageFromSettings) {
            isStorageFromSettings = false;
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showChooseImageIntent();
            }
        }
        if (isCameraFromSettings) {
            isCameraFromSettings = false;
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                selectImageFromCamera();
            }
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_left:

                if (StringUtils.length(inputFirstName.getText().toString()) < 1 || StringUtils
                        .length(inputLastName.getText().toString()) < 1) {
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, "Please add valid " +
                            "name!");
                } else {
                    getUpdatedProfile();
                }

                break;


        }
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 1);
        setResult(AppConstants.RESULT_CODE_EDIT_PROFILE, returnIntent);
        finish();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            selectedBitmap = BitmapFactory.decodeFile(fileUri.getPath());

            Glide.with(this)
                    .load(fileUri)
                    .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                    .into(imageProfile);


        } else if (requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK)
                return;

            if (null == data)
                return;
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            String picturePath = "";
            if (c != null) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
            }

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                if (inputStream != null) {
                    copyStream(inputStream, fileOutputStream);
                    inputStream.close();
                }
                fileOutputStream.close();

                selectedBitmap = BitmapFactory.decodeFile(picturePath);

                Glide.with(this)
                        .load(mFileTemp)
                        .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                        .into(imageProfile);

            } catch (Exception e) {
                Log.e("TAG", "Error while creating temp file", e);
            }

        } else if (requestCode == AppConstants.REQUEST_CODE_MAP_LOCATION_SELECTION) {
            if (data != null) {
                String locationString = data.getStringExtra(AppConstants.EXTRA_OBJECT_LOCATION);
                ReverseGeocodingAddress objAddress = (ReverseGeocodingAddress) data
                        .getSerializableExtra(AppConstants.EXTRA_OBJECT_ADDRESS);
                View linearView = linearAddressDetails.getChildAt(clickedPosition);
                EditText inputCountry = (EditText) linearView.findViewById(R.id.input_country);
                EditText inputState = (EditText) linearView.findViewById(R.id.input_state);
                EditText inputCity = (EditText) linearView.findViewById(R.id.input_city);
                EditText inputStreet = (EditText) linearView.findViewById(R.id.input_street);
                EditText inputNeighborhood = (EditText) linearView.findViewById(R.id
                        .input_neighborhood);
                EditText inputPinCode = (EditText) linearView.findViewById(R.id.input_pin_code);
                TextView textLatitude = (TextView) linearView.findViewById(R.id.input_latitude);
                TextView textLongitude = (TextView) linearView.findViewById(R.id.input_longitude);
                TextView textGoogleAddress = (TextView) linearView.findViewById(R.id
                        .input_google_address);
                try {
                    mapLatitude = Double.parseDouble(objAddress.getLatitude());
                    mapLongitude = Double.parseDouble(objAddress.getLongitude());
                } catch (Exception ignore) {
                }
                textLatitude.setText(objAddress.getLatitude());
                textLongitude.setText(objAddress.getLongitude());
                textGoogleAddress.setText(locationString);
                if (resultCode == AppConstants.RESULT_CODE_MAP_LOCATION_SELECTION) {
                    inputCountry.setText(objAddress.getCountry());
                    inputState.setText(objAddress.getState());
                    inputCity.setText(objAddress.getCity());
                    inputStreet.setText(objAddress.getAddressLine());
                    inputPinCode.setText(objAddress.getPostalCode());
                } else if (resultCode == AppConstants.RESULT_CODE_MY_LOCATION_SELECTION) {
                    if (arrayListAddressObject.size() > clickedPosition) {
                        inputCountry.setText(((ProfileDataOperationAddress)
                                arrayListAddressObject.get(clickedPosition)).getCountry());
                        inputState.setText(((ProfileDataOperationAddress) arrayListAddressObject
                                .get(clickedPosition)).getState());
                        inputCity.setText(((ProfileDataOperationAddress) arrayListAddressObject
                                .get(clickedPosition)).getCity());
                        inputStreet.setText(((ProfileDataOperationAddress) arrayListAddressObject
                                .get(clickedPosition)).getStreet());
                        inputNeighborhood.setText(((ProfileDataOperationAddress)
                                arrayListAddressObject.get(clickedPosition)).getNeighborhood());
                        inputPinCode.setText(((ProfileDataOperationAddress)
                                arrayListAddressObject.get(clickedPosition)).getPostCode());
                    } else {
                        /*inputCountry.setText("India");
                        inputState.setText("Gujarat");
                        inputCity.setText("Surat");
                        inputStreet.setText("");
                        inputPinCode.setText("");
                        inputNeighborhood.setText("");*/
                        inputCountry.setText(inputCountry.getText().toString());
                        inputState.setText(inputState.getText().toString());
                        inputCity.setText(inputCity.getText().toString());
                        inputStreet.setText(inputStreet.getText().toString());
                        inputPinCode.setText(inputPinCode.getText().toString());
                        inputNeighborhood.setText(inputNeighborhood.getText().toString());
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    showChooseImageIntent();

                } else {
                    showPermissionConfirmationDialog("Storage permission is required for " +
                            "uploading profile image. Do you want to try again?", true);
                }
            }
            break;

            case AppConstants.MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    selectImageFromCamera();

                } else {
                    showPermissionConfirmationDialog("Camera permission is required for taking " +
                            "photo. Do you want to try again?", false);
                }
            }
            break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_PROFILE_UPDATE">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_UPDATE)) {
                WsResponseObject editProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (editProfileResponse != null && StringUtils.equalsIgnoreCase
                        (editProfileResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ProfileDataOperation profileDetail = editProfileResponse.getProfileDetail();
                    Utils.setObjectPreference(EditProfileActivity.this, AppConstants
                            .PREF_REGS_USER_OBJECT, profileDetail);

                    storeProfileDataToDb(profileDetail);

                    Utils.showSuccessSnackBar(this, relativeRootEditProfile, "Profile Updated " +
                            "Successfully! ");
//                    Log.i("onDeliveryResponse", editProfileResponse.getMessage());

                } else {
                    if (editProfileResponse != null) {
                        Log.e("error response", editProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootEditProfile, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Onclick">

    @OnClick({R.id.image_add_phone, R.id.image_add_email, R.id.image_add_website,
            R.id.image_add_social_contact, R.id.image_add_address, R.id.image_add_event, R.id
            .image_add_organization, R.id.image_profile})
    public void onClick(View view) {

        switch (view.getId()) {

            //<editor-fold desc="image_add_phone">
            case R.id.image_add_phone:
                checkBeforeViewAdd(AppConstants.PHONE_NUMBER, linearPhoneDetails);
                break;
            //</editor-fold>

            //<editor-fold desc="image_add_email">
            case R.id.image_add_email:
                checkBeforeViewAdd(AppConstants.EMAIL, linearEmailDetails);
                break;
            //</editor-fold>

            //<editor-fold desc="image_add_website">
            case R.id.image_add_website:
                checkBeforeViewAdd(AppConstants.WEBSITE, linearWebsiteDetails);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_social_contact">
            case R.id.image_add_social_contact:
                checkBeforeViewAdd(AppConstants.IM_ACCOUNT, linearSocialContactDetails);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_address">
            case R.id.image_add_address:
                checkBeforeAddressViewAdd();
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_event">
            case R.id.image_add_event:
                checkBeforeViewAdd(AppConstants.EVENT, linearEventDetails);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_organization">
            case R.id.image_add_organization:
                checkBeforeOrganizationViewAdd();
                break;
            //</editor-fold>

            // <editor-fold desc="image_profile">
            case R.id.image_profile:
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest
                            .permission.WRITE_EXTERNAL_STORAGE}, AppConstants
                            .MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    showChooseImageIntent();
                }
                break;
            //</editor-fold>
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);

        profileDetails();
        phoneNumberDetails();
        emailDetails();
        websiteDetails();
        socialContactDetails();
        addressDetails();
        eventDetails();
        organizationDetails();

        rippleActionRightLeft.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);

    }

    private void profileDetails() {

        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt(getUserPmId()));
        if (userProfile != null) {
            inputFirstName.setText(userProfile.getPmFirstName());
            inputLastName.setText(userProfile.getPmLastName());

            if (StringUtils.endsWithIgnoreCase(userProfile.getPmGender(), "Female")) {
                radioGroupGender.check(R.id.radio_female);
            } else if (StringUtils.endsWithIgnoreCase(userProfile.getPmGender(), "Male")) {
                radioGroupGender.check(R.id.radio_male);
            }

            Glide.with(this)
                    .load(userProfile.getPmProfileImage())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                    .override(200, 200)
                    .into(imageProfile);

        }


    }

    private void phoneNumberDetails() {

        TableMobileMaster tableMobileMaster = new TableMobileMaster(databaseHandler);

        ArrayList<MobileNumber> arrayListMobileNumber = tableMobileMaster
                .getMobileNumbersFromPmId(Integer.parseInt(getUserPmId()));
        arrayListPhoneNumberObject = new ArrayList<>();
        for (int i = 0; i < arrayListMobileNumber.size(); i++) {
            ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
            phoneNumber.setPhoneNumber(arrayListMobileNumber.get(i).getMnmMobileNumber());
            phoneNumber.setPhoneType(arrayListMobileNumber.get(i).getMnmNumberType());
            phoneNumber.setPhoneId(arrayListMobileNumber.get(i).getMnmRecordIndexId());
            phoneNumber.setPhonePublic(Integer.parseInt(arrayListMobileNumber.get(i)
                    .getMnmNumberPrivacy()));
            arrayListPhoneNumberObject.add(phoneNumber);
        }
        if (arrayListPhoneNumberObject.size() > 0) {
            for (int i = 0; i < arrayListPhoneNumberObject.size(); i++) {
                addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, arrayListPhoneNumberObject
                        .get(i), i);
            }
        } else {
            addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, null, -1);
        }
    }

    private void emailDetails() {
        TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);

        ArrayList<Email> arrayListEmail = tableEmailMaster.getEmailsFromPmId(Integer.parseInt
                (getUserPmId()));
        arrayListEmailObject = new ArrayList<>();
        for (int i = 0; i < arrayListEmail.size(); i++) {
            ProfileDataOperationEmail email = new ProfileDataOperationEmail();
            email.setEmEmailId(arrayListEmail.get(i).getEmEmailAddress());
            email.setEmType(arrayListEmail.get(i).getEmEmailType());
            email.setEmId(arrayListEmail.get(i).getEmRecordIndexId());
            email.setEmPublic(Integer.parseInt(arrayListEmail.get(i).getEmEmailPrivacy()));
            arrayListEmailObject.add(email);
        }
        if (arrayListEmailObject.size() > 0) {
            for (int i = 0; i < arrayListEmailObject.size(); i++) {
                addView(AppConstants.EMAIL, linearEmailDetails, arrayListEmailObject.get(i), i);
            }
        } else {
            addView(AppConstants.EMAIL, linearEmailDetails, null, -1);
        }

    }

    private void websiteDetails() {
        TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster(databaseHandler);

        ArrayList<Website> arrayListWebsite = tableWebsiteMaster.getWebsiteFromPmId(Integer
                .parseInt(getUserPmId()));
        arrayListWebsiteObject = new ArrayList<>();
        for (int i = 0; i < arrayListWebsite.size(); i++) {
            ProfileDataOperationWebAddress webAddress = new ProfileDataOperationWebAddress();
            webAddress.setWebAddress(arrayListWebsite.get(i).getWmWebsiteUrl());
            webAddress.setWebType(arrayListWebsite.get(i).getWmWebsiteType());
            webAddress.setWebId(arrayListWebsite.get(i).getWmRecordIndexId());
            arrayListWebsiteObject.add(webAddress);
        }

        if (arrayListWebsiteObject.size() > 0) {
            for (int i = 0; i < arrayListWebsiteObject.size(); i++) {
                addView(AppConstants.WEBSITE, linearWebsiteDetails, arrayListWebsiteObject.get(i)
                        , i);
            }
        } else {
            addView(AppConstants.WEBSITE, linearWebsiteDetails, null, -1);
        }

    }

    private void socialContactDetails() {
        TableImMaster tableImMaster = new TableImMaster(databaseHandler);

        ArrayList<ImAccount> arrayListImAccount = tableImMaster.getImAccountFromPmId(Integer
                .parseInt(getUserPmId()));
        arrayListSocialContactObject = new ArrayList<>();
        for (int i = 0; i < arrayListImAccount.size(); i++) {
            ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
            imAccount.setIMAccountProtocol(arrayListImAccount.get(i).getImImProtocol());
//            imAccount.setIMAccountType(arrayListImAccount.get(i).getImImType());
            imAccount.setIMAccountDetails(arrayListImAccount.get(i).getImImDetail());
            imAccount.setIMId(arrayListImAccount.get(i).getImRecordIndexId());
            imAccount.setIMAccountPublic(Integer.parseInt(arrayListImAccount.get(i)
                    .getImImPrivacy()));
            arrayListSocialContactObject.add(imAccount);
        }

        if (arrayListSocialContactObject.size() > 0) {
            for (int i = 0; i < arrayListSocialContactObject.size(); i++) {
                addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails,
                        arrayListSocialContactObject.get(i), i);
            }
        } else {
            addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails, null, -1);
        }
    }

    private void eventDetails() {
        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        ArrayList<Event> arrayListEvent = tableEventMaster.getEventsFromPmId(Integer.parseInt
                (getUserPmId()));

        arrayListEventObject = new ArrayList<>();
        for (int i = 0; i < arrayListEvent.size(); i++) {

            String formattedDate = Utils.convertDateFormat(arrayListEvent.get(i).getEvmStartDate
                    (), "yyyy-MM-dd hh:mm:ss", EVENT_DATE_FORMAT);

            ProfileDataOperationEvent event = new ProfileDataOperationEvent();
            event.setEventDateTime(formattedDate);
            event.setEventType(arrayListEvent.get(i).getEvmEventType());
            event.setEventId(arrayListEvent.get(i).getEvmRecordIndexId());
            event.setEventPublic(Integer.parseInt(arrayListEvent.get(i).getEvmEventPrivacy()));
            arrayListEventObject.add(event);
        }

        if (arrayListEventObject.size() > 0) {
            for (int i = 0; i < arrayListEventObject.size(); i++) {
                addView(AppConstants.EVENT, linearEventDetails, arrayListEventObject.get(i), i);
            }
        } else {
            addView(AppConstants.EVENT, linearEventDetails, null, -1);
        }
    }

    private void organizationDetails() {
        TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                (databaseHandler);

        ArrayList<Organization> arrayListOrganization = tableOrganizationMaster
                .getOrganizationsFromPmId(Integer.parseInt(getUserPmId()));
        arrayListOrganizationObject = new ArrayList<>();
        for (int i = 0; i < arrayListOrganization.size(); i++) {
            ProfileDataOperationOrganization organization = new ProfileDataOperationOrganization();
            organization.setOrgName(arrayListOrganization.get(i).getOmOrganizationCompany());
            organization.setOrgJobTitle(arrayListOrganization.get(i).getOmOrganizationDesignation
                    ());
            organization.setOrgId(arrayListOrganization.get(i).getOmRecordIndexId());
            organization.setIsCurrent(Integer.parseInt(arrayListOrganization.get(i)
                    .getOmIsCurrent()));
            arrayListOrganizationObject.add(organization);
        }

        if (arrayListOrganizationObject.size() > 0) {
            for (int i = 0; i < arrayListOrganizationObject.size(); i++) {
                addOrganizationView(arrayListOrganizationObject.get(i));
            }
        } else {
            addOrganizationView(null);
        }
    }

    private void addressDetails() {
        TableAddressMaster tableAddressMaster = new TableAddressMaster(databaseHandler);

        ArrayList<Address> arrayListAddress = tableAddressMaster.getAddressesFromPmId(Integer
                .parseInt(getUserPmId()));
        arrayListAddressObject = new ArrayList<>();
        for (int i = 0; i < arrayListAddress.size(); i++) {
            ProfileDataOperationAddress address = new ProfileDataOperationAddress();
            address.setCountry(arrayListAddress.get(i).getAmCountry());
            address.setState(arrayListAddress.get(i).getAmState());
            address.setCity(arrayListAddress.get(i).getAmCity());
            address.setStreet(arrayListAddress.get(i).getAmStreet());
            address.setFormattedAddress(arrayListAddress.get(i).getAmFormattedAddress());
            address.setNeighborhood(arrayListAddress.get(i).getAmNeighborhood());
            address.setPostCode(arrayListAddress.get(i).getAmPostCode());
            address.setAddressType(arrayListAddress.get(i).getAmAddressType());
            address.setGoogleLatitude(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLongitude(arrayListAddress.get(i).getAmGoogleLongitude());
            address.setAddId(arrayListAddress.get(i).getAmRecordIndexId());
            address.setAddPublic(Integer.parseInt(arrayListAddress.get(i).getAmAddressPrivacy()));
            arrayListAddressObject.add(address);
        }

        if (arrayListAddressObject.size() > 0) {
            for (int i = 0; i < arrayListAddressObject.size(); i++) {
                addAddressView(arrayListAddressObject.get(i), i);
            }
        } else {
            addAddressView(null, -1);
        }
    }

    private void addView(int viewType, final LinearLayout linearLayout, Object detailObject, int
            position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile, null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        final Spinner spinnerType = (Spinner) view.findViewById(R.id.spinner_type);
        final EditText inputValue = (EditText) view.findViewById(R.id.input_value);
        LinearLayout linerCheckbox = (LinearLayout) view.findViewById(R.id.liner_checkbox);
        final CheckBox checkboxHideYear = (CheckBox) view.findViewById(R.id.checkbox_hide_year);
        TextView textLabelCheckbox = (TextView) view.findViewById(R.id.text_label_checkbox);
        TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);
        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputValue.setTypeface(Utils.typefaceRegular(this));
        textLabelCheckbox.setTypeface(Utils.typefaceLight(this));

        List<String> typeList;

        switch (viewType) {
            case AppConstants.PHONE_NUMBER:
                linerCheckbox.setVisibility(View.GONE);
                textImageCross.setTag(AppConstants.PHONE_NUMBER);
                spinnerType.setTag(AppConstants.PHONE_NUMBER);
                inputValue.setHint("Number");
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_phone_number)));
                spinnerPhoneAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerPhoneAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerPhoneAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_PHONE);
                if (detailObject != null) {
                    if (position == 0) {
                        inputValue.setEnabled(false);
                        spinnerType.setVisibility(View.GONE);
                        textImageCross.setVisibility(View.INVISIBLE);
                    }
                    ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                            detailObject;
                    inputValue.setText(phoneNumber.getPhoneNumber());
                    textIsPublic.setText(String.valueOf(phoneNumber.getPhonePublic()));
                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(phoneNumber.getPhoneType()))) {
                        spinnerPosition = spinnerPhoneAdapter.getPosition(phoneNumber.getPhoneType
                                ());
                    } else {
                        spinnerPhoneAdapter.add(phoneNumber.getPhoneType());
                        spinnerPhoneAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerPhoneAdapter.getPosition(phoneNumber
                                .getPhoneType());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    relativeRowEditProfile.setTag(phoneNumber.getPhoneId());
                }
                break;

            case AppConstants.EMAIL:
                linerCheckbox.setVisibility(View.GONE);
                textImageCross.setTag(AppConstants.EMAIL);
                spinnerType.setTag(AppConstants.EMAIL);
                inputValue.setHint("Email");
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_email_address)));
                spinnerEmailAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerType.setAdapter(spinnerEmailAdapter);
                inputValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                if (detailObject != null) {
                    ProfileDataOperationEmail email = (ProfileDataOperationEmail) detailObject;
                    inputValue.setText(email.getEmEmailId());
                    textIsPublic.setText(String.valueOf(email.getEmPublic()));
                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(email.getEmType()))) {
                        spinnerPosition = spinnerEmailAdapter.getPosition(email.getEmType());
                    } else {
//                        spinnerPosition = spinnerEmailAdapter.getPosition("Other");
                        spinnerEmailAdapter.add(email.getEmType());
                        spinnerEmailAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerEmailAdapter.getPosition(email.getEmType());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    relativeRowEditProfile.setTag(email.getEmId());
                }
                break;

            case AppConstants.WEBSITE:
                linerCheckbox.setVisibility(View.GONE);
                textImageCross.setTag(AppConstants.WEBSITE);
                spinnerType.setTag(AppConstants.WEBSITE);
                inputValue.setHint("Website");
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_email_address)));
                spinnerWebsiteAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerWebsiteAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerWebsiteAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress)
                            detailObject;
                    inputValue.setText(webAddress.getWebAddress());
                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(webAddress.getWebType()))) {
                        spinnerPosition = spinnerWebsiteAdapter.getPosition(webAddress.getWebType
                                ());
                    } else {
//                        spinnerPosition = spinnerWebsiteAdapter.getPosition("Other");
                        spinnerWebsiteAdapter.add(webAddress.getWebType());
                        spinnerWebsiteAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerWebsiteAdapter.getPosition(webAddress.getWebType
                                ());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    relativeRowEditProfile.setTag(webAddress.getWebId());
                }
                break;

            case AppConstants.IM_ACCOUNT:
                linerCheckbox.setVisibility(View.GONE);
                textImageCross.setTag(AppConstants.IM_ACCOUNT);
                spinnerType.setTag(AppConstants.IM_ACCOUNT);
                inputValue.setHint("Link");
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_social_media)));
                spinnerImAccountAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerImAccountAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerImAccountAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount)
                            detailObject;
                    inputValue.setText(imAccount.getIMAccountDetails());
                    textIsPublic.setText(String.valueOf(imAccount.getIMAccountPublic()));
                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(imAccount
                            .getIMAccountProtocol()))) {
                        spinnerPosition = spinnerImAccountAdapter.getPosition(imAccount
                                .getIMAccountProtocol());
                    } else {
//                        spinnerPosition = spinnerImAccountAdapter.getPosition("Other");
                        spinnerImAccountAdapter.add(imAccount.getIMAccountProtocol());
                        spinnerImAccountAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerImAccountAdapter.getPosition(imAccount
                                .getIMAccountProtocol());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    relativeRowEditProfile.setTag(imAccount.getIMId());
                }
                break;

            case AppConstants.EVENT:
                linerCheckbox.setVisibility(View.VISIBLE);
                textImageCross.setTag(AppConstants.EVENT);
                spinnerType.setTag(AppConstants.EVENT);
                inputValue.setHint("Event");
                inputValue.setFocusable(false);
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_Event)));
                spinnerEventAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerEventAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerEventAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationEvent event = (ProfileDataOperationEvent)
                            detailObject;
                    inputValue.setText(event.getEventDateTime());
                    textIsPublic.setText(String.valueOf(event.getEventPublic()));
                    checkboxHideYear.setChecked(event.getIsYearHidden() == 1);
                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(event.getEventType()))) {
                        spinnerPosition = spinnerEventAdapter.getPosition(event.getEventType());
                    } else {
//                        spinnerPosition = spinnerEventAdapter.getPosition("Other");
                        spinnerEventAdapter.add(event.getEventType());
                        spinnerEventAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerEventAdapter.getPosition(event.getEventType());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    relativeRowEditProfile.setTag(event.getEventId());
                }
                inputValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker((EditText) v);
                    }
                });
                break;

            default:
                getResources().getStringArray(R.array.types_email_address);
                break;
        }

        textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((Integer) v.getTag()) {

                    case AppConstants.PHONE_NUMBER:
                        if (linearPhoneDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearPhoneDetails.getChildCount() == 1) {
                            inputValue.setText("");
                        }
                        break;

                    case AppConstants.EMAIL:
                        if (linearEmailDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearEmailDetails.getChildCount() == 1) {
                            inputValue.setText("");
                        }
                        break;

                    case AppConstants.WEBSITE:
                        if (linearWebsiteDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearWebsiteDetails.getChildCount() == 1) {
                            inputValue.setText("");
                        }
                        break;

                    case AppConstants.EVENT:
                        if (linearEventDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearEventDetails.getChildCount() == 1) {
                            inputValue.setText("");
                        }
                        break;

                    case AppConstants.IM_ACCOUNT:
                        if (linearSocialContactDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearSocialContactDetails.getChildCount() == 1) {
                            inputValue.setText("");
                        }
                        break;
                }
            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String items = spinnerType.getSelectedItem().toString();
                if (items.equalsIgnoreCase("Custom")) {
                    showCustomTypeDialog(spinnerType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        linearLayout.addView(view);
    }

    private void addAddressView(final Object detailObject, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_address,
                null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        TextView textImageMapMarker = (TextView) view.findViewById(R.id.text_image_map_marker);
        Spinner spinnerType = (Spinner) view.findViewById(R.id.spinner_type);
        inputCountry = (EditText) view.findViewById(R.id.input_country);
        inputState = (EditText) view.findViewById(R.id.input_state);
        inputCity = (EditText) view.findViewById(R.id.input_city);
        inputStreet = (EditText) view.findViewById(R.id.input_street);
        inputNeighborhood = (EditText) view.findViewById(R.id.input_neighborhood);
        inputPinCode = (EditText) view.findViewById(R.id.input_pin_code);
        final EditText inputPoBox = (EditText) view.findViewById(R.id.input_po_box);
        final TextView textLatitude = (TextView) view.findViewById(R.id.input_latitude);
        final TextView textLongitude = (TextView) view.findViewById(R.id.input_longitude);
        final TextView textGoogleAddress = (TextView) view.findViewById(R.id.input_google_address);
        final TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        inputCountry.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputState.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputCity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputStreet.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputNeighborhood.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);
        inputPinCode.setInputType(InputType.TYPE_CLASS_NUMBER);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        textImageMapMarker.setTypeface(Utils.typefaceIcons(this));
        inputCountry.setTypeface(Utils.typefaceRegular(this));
        inputState.setTypeface(Utils.typefaceRegular(this));
        inputCity.setTypeface(Utils.typefaceRegular(this));
        inputStreet.setTypeface(Utils.typefaceRegular(this));
        inputNeighborhood.setTypeface(Utils.typefaceRegular(this));
        inputPinCode.setTypeface(Utils.typefaceRegular(this));
        inputPoBox.setTypeface(Utils.typefaceRegular(this));

        inputCountry.setHint("Country (Required)");
        inputState.setHint("State (Required)");
        inputCity.setHint("City (Required)");
        inputStreet.setHint("Street (Required)");
        inputNeighborhood.setHint("Neighborhood");
        inputPinCode.setHint("Pincode");
        inputPoBox.setHint("Po. Box No.");

        inputPoBox.setVisibility(View.GONE);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout
                .list_item_spinner, getResources().getStringArray(R.array.types_email_address));
        spinnerType.setAdapter(spinnerAdapter);

        if (detailObject != null) {
            ProfileDataOperationAddress address = (ProfileDataOperationAddress) detailObject;
            inputCountry.setText(address.getCountry());
            inputState.setText(address.getState());
            inputCity.setText(address.getCity());
            inputStreet.setText(address.getStreet());
            inputNeighborhood.setText(address.getNeighborhood());
            inputPinCode.setText(address.getPostCode());
            textLatitude.setText(address.getGoogleLatitude());
            textLongitude.setText(address.getGoogleLongitude());
            textIsPublic.setText(String.valueOf(address.getAddPublic()));
            formattedAddress = address.getFormattedAddress();
            /*int spinnerPosition;
            if (Arrays.asList(getResources().getStringArray(R.array.types_email_address))
                    .contains(StringUtils.defaultString(address.getAddressType()))) {
                spinnerPosition = spinnerPhoneAdapter.getPosition(address.getAddressType());
            } else {
                spinnerPhoneAdapter.add(address.getAddressType());
                spinnerPhoneAdapter.notifyDataSetChanged();
                spinnerPosition = spinnerPhoneAdapter.getPosition(address.getAddressType());
            }
            spinnerType.setSelection(spinnerPosition);*/
            relativeRowEditProfile.setTag(address.getAddId());
        }


        textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearAddressDetails.getChildCount() > 1) {
                    linearAddressDetails.removeView(relativeRowEditProfile);
                } else if (linearAddressDetails.getChildCount() == 1) {
                    inputCountry.setText("");
                    inputState.setText("");
                    inputCity.setText("");
                    inputStreet.setText("");
                    inputNeighborhood.setText("");
                    inputPinCode.setText("");
                    inputPoBox.setText("");
                }
            }
        });

        textImageMapMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityIntent(EditProfileActivity.this, MapsActivity.class, null);
                Intent intent = new Intent(EditProfileActivity.this, MapsActivity.class);

                if (position != -1) {
                  /*  mapLatitude = Double.parseDouble(((ProfileDataOperationAddress)
                            arrayListAddressObject.get(position)).getGoogleLatitude());
                    mapLongitude = Double.parseDouble(((ProfileDataOperationAddress)
                            arrayListAddressObject.get(position)).getGoogleLongitude());
                    intent.putExtra(AppConstants.EXTRA_LATITUDE, mapLatitude);
                    intent.putExtra(AppConstants.EXTRA_LONGITUDE, mapLongitude);*/
                    View view = linearAddressDetails.getChildAt(position);
                    TextView textLatitude = (TextView) view.findViewById(R.id.input_latitude);
                    TextView textLongitude = (TextView) view.findViewById(R.id.input_longitude);
                    EditText country = (EditText) view.findViewById(R.id.input_country);
                    EditText state = (EditText) view.findViewById(R.id.input_state);
                    EditText city = (EditText) view.findViewById(R.id.input_city);
                    EditText street = (EditText) view.findViewById(R.id.input_street);
                    EditText neighborhood = (EditText) view.findViewById(R.id.input_neighborhood);
                    EditText pinCode = (EditText) view.findViewById(R.id.input_pin_code);

                    String countryName = country.getText().toString();
                    String stateName = state.getText().toString();
                    String cityName = city.getText().toString();
                    String streetName = street.getText().toString();
                    String neighborhoodName = neighborhood.getText().toString();
                    String pinCodeName = pinCode.getText().toString();

                    intent.putExtra(AppConstants.EXTRA_LATITUDE, Double.parseDouble(StringUtils
                            .defaultIfEmpty(textLatitude.getText().toString(), "0")));
                    intent.putExtra(AppConstants.EXTRA_LONGITUDE, Double.parseDouble(StringUtils
                            .defaultIfEmpty(textLongitude.getText().toString(), "0")));
                    String formattedAddress = Utils.setFormattedAddress(streetName,
                            neighborhoodName, cityName, stateName, countryName, pinCodeName);
                    if (StringUtils.length(formattedAddress) > 0) {
                        intent.putExtra(AppConstants.EXTRA_FORMATTED_ADDRESS, formattedAddress);
                    }
                }
                if (detailObject != null && position != -1) {
                    intent.putExtra(AppConstants.EXTRA_FORMATTED_ADDRESS, (
                            (ProfileDataOperationAddress) arrayListAddressObject.get(position))
                            .getFormattedAddress());
                }
                if (position == -1) {
                    clickedPosition = 0;
                } else {
                    clickedPosition = position;
                }
                startActivityForResult(intent, AppConstants.REQUEST_CODE_MAP_LOCATION_SELECTION);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        linearAddressDetails.addView(view);
    }

    private void addOrganizationView(Object detailObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_organization,
                null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        TextView textLabelCheckbox = (TextView) view.findViewById(R.id.text_label_checkbox);
        final EditText inputCompanyName = (EditText) view.findViewById(R.id.input_company_name);
        final EditText inputDesignationName = (EditText) view.findViewById(R.id
                .input_designation_name);
        CheckBox checkboxOrganization = (CheckBox) view.findViewById(R.id.checkbox_organization);

        checkboxOrganization.setTag(linearOrganizationDetail.getChildCount());

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        inputCompanyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputDesignationName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputCompanyName.setTypeface(Utils.typefaceRegular(this));
        inputDesignationName.setTypeface(Utils.typefaceRegular(this));
        textLabelCheckbox.setTypeface(Utils.typefaceLight(this));

        ProfileDataOperationOrganization organization = (ProfileDataOperationOrganization)
                detailObject;

        if (detailObject != null) {
            relativeRowEditProfile.setTag(organization.getOrgId());
            inputCompanyName.setText(organization.getOrgName());
            inputDesignationName.setText(organization.getOrgJobTitle());
            checkboxOrganization.setChecked(organization.getIsCurrent() == 1);
        }

        checkboxOrganization.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < linearOrganizationDetail.getChildCount(); i++) {
                        View view = linearOrganizationDetail.getChildAt(i);
                        CheckBox checkbox = (CheckBox) view.findViewById(R.id
                                .checkbox_organization);
                        if (!(checkbox.getTag() == buttonView.getTag())) {
                            checkbox.setChecked(false);
                        }
                    }
                }
            }
        });

        textImageCross.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (linearOrganizationDetail.getChildCount() > 1) {
                    linearOrganizationDetail.removeView(relativeRowEditProfile);
                } else if (linearOrganizationDetail.getChildCount() == 1) {
                    inputCompanyName.setText("");
                    inputDesignationName.setText("");
                }
            }
        });

        linearOrganizationDetail.addView(view);
    }

    private void showDatePicker(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(EVENT_DATE_FORMAT, Locale.US);
                editText.setText(sdf.format(calendar.getTime()));
            }

        };
        new DatePickerDialog(this, datePickerDialog, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showChooseImageIntent() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_share_invite);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        TextView textFromContact = (TextView) dialog.findViewById(R.id.text_from_contact);
        TextView textFromSocialMedia = (TextView) dialog.findViewById(R.id.text_from_social_media);

        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFromContact.setTypeface(Utils.typefaceRegular(this));
        textFromSocialMedia.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText("Upload Via");
        textFromContact.setText("Take Photo");
        textFromSocialMedia.setText("Choose Photo");

        buttonLeft.setText(R.string.action_cancel);

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        textFromSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectImageFromGallery();
            }
        });

        textFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest
                        .permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(EditProfileActivity.this, new
                            String[]{Manifest.permission.CAMERA}, AppConstants
                            .MY_PERMISSIONS_REQUEST_CAMERA);

                } else {
                    selectImageFromCamera();
                }
            }
        });

        dialog.show();
    }

    private void selectImageFromGallery() {
        mFileTemp = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " " +
                        "directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format
                (new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {

            TEMP_PHOTO_FILE_NAME = "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + TEMP_PHOTO_FILE_NAME);

        } else {
            return null;
        }

        return mediaFile;
    }

    private void copyStream(InputStream inputStream, FileOutputStream fileOutputStream) throws
            IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
    }

    private void selectImageFromCamera() {
        mFileTemp = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void checkBeforeViewAdd(int viewType, LinearLayout linearLayout) {
        boolean toAdd = false;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View linearView = linearLayout.getChildAt(i);
            EditText editText = (EditText) linearView.findViewById(R.id.input_value);
            if (StringUtils.length(StringUtils.trimToEmpty(editText.getText().toString())) < 1) {
//            if (editText.getText().length() < 1) {
                toAdd = false;
                break;
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            addView(viewType, linearLayout, null, -1);
        }
    }

    private void checkBeforeAddressViewAdd() {
        boolean toAdd = false;
        for (int i = 0; i < linearAddressDetails.getChildCount(); i++) {
            View linearView = linearAddressDetails.getChildAt(i);
            EditText inputCountry = (EditText) linearView.findViewById(R.id.input_country);
            EditText inputState = (EditText) linearView.findViewById(R.id.input_state);
            EditText inputCity = (EditText) linearView.findViewById(R.id.input_city);
            EditText inputStreet = (EditText) linearView.findViewById(R.id.input_street);
            if (StringUtils.length(StringUtils.trimToEmpty(inputCountry.getText().toString())) <
                    1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputState.getText().toString()))
                            < 1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputCity.getText().toString())) <
                            1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputStreet.getText().toString()))
                            < 1) {
                toAdd = false;
                break;
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            addAddressView(null, linearAddressDetails.getChildCount());
        }
    }

    private void checkBeforeOrganizationViewAdd() {
        boolean toAdd = false;
        for (int i = 0; i < linearOrganizationDetail.getChildCount(); i++) {
            View linearView = linearOrganizationDetail.getChildAt(i);
            EditText inputCompanyName = (EditText) linearView.findViewById(R.id.input_company_name);
            EditText inputDesignationName = (EditText) linearView.findViewById(R.id
                    .input_designation_name);
            if (StringUtils.length(StringUtils.trimToEmpty(inputCompanyName.getText().toString())
            ) < 1 || StringUtils.length(StringUtils.trimToEmpty(inputDesignationName.getText()
                    .toString())) < 1) {
                toAdd = false;
                break;
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            addOrganizationView(null);
        }
    }

    private void showCustomTypeDialog(final Spinner spinnerType) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_type);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        final EditText inputCustomName = (EditText) dialog.findViewById(R.id.input_custom_name);

        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        inputCustomName.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setTypeface(Utils.typefaceSemiBold(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText("Custom Label Name");

        buttonLeft.setText(R.string.action_cancel);
        buttonRight.setText("OK");

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Utils.hideSoftKeyboard(EditProfileActivity.this, inputCustomName);
                dialog.dismiss();
                switch ((Integer) spinnerType.getTag()) {
                    case AppConstants.PHONE_NUMBER:
                        spinnerPhoneAdapter.add(inputCustomName.getText().toString());
                        spinnerPhoneAdapter.notifyDataSetChanged();
                        spinnerType.setSelection(spinnerPhoneAdapter.getPosition(inputCustomName
                                .getText().toString()));
                        break;

                    case AppConstants.EMAIL:
                        spinnerEmailAdapter.add(inputCustomName.getText().toString());
                        spinnerEmailAdapter.notifyDataSetChanged();
                        spinnerType.setSelection(spinnerEmailAdapter.getPosition(inputCustomName
                                .getText().toString()));
                        break;

                    case AppConstants.WEBSITE:
                        spinnerWebsiteAdapter.add(inputCustomName.getText().toString());
                        spinnerWebsiteAdapter.notifyDataSetChanged();
                        spinnerType.setSelection(spinnerWebsiteAdapter.getPosition(inputCustomName
                                .getText().toString()));
                        break;

                    case AppConstants.EVENT:
                        spinnerEventAdapter.add(inputCustomName.getText().toString());
                        spinnerEventAdapter.notifyDataSetChanged();
                        spinnerType.setSelection(spinnerEventAdapter.getPosition(inputCustomName
                                .getText().toString()));
                        break;

                    case AppConstants.IM_ACCOUNT:
                        spinnerImAccountAdapter.add(inputCustomName.getText().toString());
                        spinnerImAccountAdapter.notifyDataSetChanged();
                        spinnerType.setSelection(spinnerImAccountAdapter.getPosition(inputCustomName
                                .getText().toString()));
                        break;
                }
            }
        });


        dialog.show();
    }

    private void storeProfileDataToDb(ProfileDataOperation profileDetail) {

        //<editor-fold desc="Basic Details">
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        UserProfile userProfile = new UserProfile();
        userProfile.setPmRcpId(getUserPmId());
        userProfile.setPmPrefix(profileDetail.getPbNamePrefix());
        userProfile.setPmFirstName(profileDetail.getPbNameFirst());
        userProfile.setPmMiddleName(profileDetail.getPbNameMiddle());
        userProfile.setPmLastName(profileDetail.getPbNameLast());
        userProfile.setPmSuffix(profileDetail.getPbNameSuffix());
        userProfile.setPmNickName(profileDetail.getPbNickname());
        userProfile.setPmPhoneticFirstName(profileDetail.getPbPhoneticNameFirst());
        userProfile.setPmPhoneticMiddleName(profileDetail.getPbPhoneticNameMiddle());
        userProfile.setPmPhoneticLastName(profileDetail.getPbPhoneticNameLast());
        userProfile.setPmNotes(profileDetail.getPbNote());
        userProfile.setProfileRating(profileDetail.getProfileRating());
        userProfile.setTotalProfileRateUser(profileDetail.getTotalProfileRateUser());
        userProfile.setPmIsFavourite(profileDetail.getIsFavourite());
        userProfile.setPmNosqlMasterId(profileDetail.getNoSqlMasterId());
        userProfile.setPmJoiningDate(profileDetail.getJoiningDate());
        userProfile.setPmGender(profileDetail.getPbGender());
        userProfile.setPmProfileImage(profileDetail.getPbProfilePhoto());

        tableProfileMaster.updateUserProfile(userProfile);
        //</editor-fold>

        // <editor-fold desc="Mobile Number">
        TableMobileMaster tableMobileMaster = new TableMobileMaster(databaseHandler);

        // Remove Existing Number
        tableMobileMaster.deleteMobileNumber(getUserPmId());

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
                mobileNumber.setMnmMobileNumber(arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber
                        .get(i).getPhonePublic()));
                mobileNumber.setMnmIsPrimary(arrayListPhoneNumber.get(i).getPbRcpType());
                mobileNumber.setRcProfileMasterPmId(getUserPmId());
                arrayListMobileNumber.add(mobileNumber);
            }
            tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);
        }
        //</editor-fold>

        //<editor-fold desc="Email Master">

        TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);

        // Remove Existing Number
        tableEmailMaster.deleteEmail(getUserPmId());

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
                email.setRcProfileMasterPmId(getUserPmId());
                arrayListEmail.add(email);
            }


            tableEmailMaster.addArrayEmail(arrayListEmail);
        }
        //</editor-fold>

        //<editor-fold desc="Organization Master">

        TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                (databaseHandler);

        // Remove Existing Number
        tableOrganizationMaster.deleteOrganization(getUserPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbOrganization())) {
            ArrayList<ProfileDataOperationOrganization> arrayListOrganization = profileDetail
                    .getPbOrganization();
            ArrayList<Organization> organizationList = new ArrayList<>();
            for (int i = 0; i < arrayListOrganization.size(); i++) {
                Organization organization = new Organization();
                organization.setOmRecordIndexId(arrayListOrganization.get(i).getOrgId());
                organization.setOmOrganizationCompany(arrayListOrganization.get(i).getOrgName());
                organization.setOmOrganizationDesignation(arrayListOrganization.get(i)
                        .getOrgJobTitle());
                organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(i)
                        .getIsCurrent()));
                organization.setRcProfileMasterPmId(getUserPmId());
                organizationList.add(organization);
            }

            tableOrganizationMaster.addArrayOrganization(organizationList);
        }
        //</editor-fold>

        // <editor-fold desc="Website Master">

        TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster(databaseHandler);

        // Remove Existing Number
        tableWebsiteMaster.deleteWebsite(getUserPmId());

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
                website.setRcProfileMasterPmId(getUserPmId());
                websiteList.add(website);
            }

            tableWebsiteMaster.addArrayWebsite(websiteList);
        }
        //</editor-fold>

        //<editor-fold desc="Address Master">

        TableAddressMaster tableAddressMaster = new TableAddressMaster(databaseHandler);

        // Remove Existing Number
        tableAddressMaster.deleteAddress(getUserPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            ArrayList<ProfileDataOperationAddress> arrayListAddress = profileDetail.getPbAddress();
            ArrayList<Address> addressList = new ArrayList<>();
            for (int j = 0; j < arrayListAddress.size(); j++) {
                Address address = new Address();
                address.setAmRecordIndexId(arrayListAddress.get(j).getAddId());
                address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j).getAddPublic()));
                address.setAmState(arrayListAddress.get(j).getState());
                address.setAmCity(arrayListAddress.get(j).getCity());
                address.setAmCountry(arrayListAddress.get(j).getCountry());
                address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress());
                address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                address.setAmStreet(arrayListAddress.get(j).getStreet());
                address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatitude());
                address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLongitude());
                address.setRcProfileMasterPmId(getUserPmId());
                addressList.add(address);
            }

            tableAddressMaster.addArrayAddress(addressList);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account Master">
        TableImMaster tableImMaster = new TableImMaster(databaseHandler);

        // Remove Existing Number
        tableImMaster.deleteImAccount(getUserPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts())) {
            ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileDetail
                    .getPbIMAccounts();
            ArrayList<ImAccount> imAccountsList = new ArrayList<>();
            for (int j = 0; j < arrayListImAccount.size(); j++) {
                ImAccount imAccount = new ImAccount();
                imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
                imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
//                imAccount.setImImType(arrayListImAccount.get(j).getIMAccountType());
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                        .getIMAccountPublic()));
                imAccount.setRcProfileMasterPmId(getUserPmId());
                imAccountsList.add(imAccount);
            }

            tableImMaster.addArrayImAccount(imAccountsList);
        }
        //</editor-fold>

        // <editor-fold desc="Event Master">

        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        // Remove Existing Number
        tableEventMaster.deleteEvent(getUserPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            ArrayList<ProfileDataOperationEvent> arrayListEvent = profileDetail
                    .getPbEvent();
            ArrayList<Event> eventList = new ArrayList<>();
            for (int j = 0; j < arrayListEvent.size(); j++) {
                Event event = new Event();
                event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                event.setEvmEventType(arrayListEvent.get(j).getEventType());
                event.setEvmIsYearHidden(arrayListEvent.get(j).getIsYearHidden());
                event.setEvmEventPrivacy(String.valueOf(arrayListEvent.get(j).getEventPublic()));
                event.setRcProfileMasterPmId(getUserPmId());
                eventList.add(event);
            }

            tableEventMaster.addArrayEvent(eventList);
        }
        //</editor-fold>

    }

    private void showPermissionConfirmationDialog(String message, final boolean isStorage) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        if (isStorage) {
                            isStorageFromSettings = true;
                        } else {
                            isCameraFromSettings = true;
                        }
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };

        permissionConfirmationDialog = new MaterialDialog(this, cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText("Cancel");
        permissionConfirmationDialog.setRightButtonText("OK");
        permissionConfirmationDialog.setDialogBody(message);

        permissionConfirmationDialog.showDialog();

    }

    private void getUpdatedProfile() {

        boolean isValid = true;
        String message = "";

        //<editor-fold desc="Email">
        ArrayList<ProfileDataOperationEmail> arrayListNewEmail = new ArrayList<>();
        for (int i = 0; i < linearEmailDetails.getChildCount(); i++) {
            ProfileDataOperationEmail email = new ProfileDataOperationEmail();
            View view = linearEmailDetails.getChildAt(i);
            EditText emailId = (EditText) view.findViewById(R.id.input_value);
            Spinner emailType = (Spinner) view.findViewById(R.id.spinner_type);
            TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
            email.setEmEmailId(emailId.getText().toString());
            email.setEmType((String) emailType.getSelectedItem());
            email.setEmId((String) relativeRowEditProfile.getTag());
            if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                email.setEmPublic(Integer.parseInt(textIsPublic.getText().toString()));
            } else {
                email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
            }

            if (StringUtils.length(email.getEmEmailId()) > 0) {
                arrayListNewEmail.add(email);
            }

        }

        //</editor-fold>

        //<editor-fold desc="Phone Number">
        ArrayList<ProfileDataOperationPhoneNumber> arrayListNewPhone = new ArrayList<>();
        for (int i = 0; i < linearPhoneDetails.getChildCount(); i++) {
            ProfileDataOperationPhoneNumber phoneNumber = new
                    ProfileDataOperationPhoneNumber();
            View view = linearPhoneDetails.getChildAt(i);
            EditText emailId = (EditText) view.findViewById(R.id.input_value);
            Spinner emailType = (Spinner) view.findViewById(R.id.spinner_type);
            TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
            phoneNumber.setPhoneNumber(emailId.getText().toString());
            phoneNumber.setPhoneType((String) emailType.getSelectedItem());
            phoneNumber.setPhoneId((String) relativeRowEditProfile.getTag());
            if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                phoneNumber.setPhonePublic(Integer.parseInt(textIsPublic.getText().toString()));
            } else {
                phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_MY_CONTACT);
            }

            if (StringUtils.length(phoneNumber.getPhoneNumber()) > 0) {
                arrayListNewPhone.add(phoneNumber);
            }

        }

        //</editor-fold>

        // <editor-fold desc="Website">
        ArrayList<ProfileDataOperationWebAddress> arrayListNewWebAddress = new
                ArrayList<>();

        for (int i = 0; i < linearWebsiteDetails.getChildCount(); i++) {
            ProfileDataOperationWebAddress webAddress = new
                    ProfileDataOperationWebAddress();
            View view = linearWebsiteDetails.getChildAt(i);
            EditText website = (EditText) view.findViewById(R.id.input_value);
            Spinner websiteType = (Spinner) view.findViewById(R.id.spinner_type);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
            webAddress.setWebAddress(website.getText().toString());
            webAddress.setWebType((String) websiteType.getSelectedItem());
            webAddress.setWebId((String) relativeRowEditProfile.getTag());

            webAddress.setWebPublic(IntegerConstants.PRIVACY_EVERYONE);

            if (StringUtils.length(webAddress.getWebAddress()) > 0) {
                arrayListNewWebAddress.add(webAddress);
            }

        }

        //</editor-fold>

        // <editor-fold desc="Event">
        ArrayList<ProfileDataOperationEvent> arrayListNewEvent = new ArrayList<>();
        for (int i = 0; i < linearEventDetails.getChildCount(); i++) {
            ProfileDataOperationEvent event = new ProfileDataOperationEvent();
            View view = linearEventDetails.getChildAt(i);
            EditText eventDate = (EditText) view.findViewById(R.id.input_value);
            Spinner eventType = (Spinner) view.findViewById(R.id.spinner_type);
            TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);
            CheckBox checkboxHideYear = (CheckBox) view.findViewById(R.id
                    .checkbox_hide_year);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
//                    event.setEventDateTime(eventDate.getText().toString());
            event.setEventType((String) eventType.getSelectedItem());
            event.setIsYearHidden(checkboxHideYear.isChecked() ? 1 : 0);
            event.setEventId((String) relativeRowEditProfile.getTag());
//            event.setEventPublic(Integer.parseInt(textIsPublic.getText().toString()));
            if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                event.setEventPublic(Integer.parseInt(textIsPublic.getText().toString()));
            } else {
                event.setEventPublic(IntegerConstants.PRIVACY_MY_CONTACT);
            }
            if (eventDate.getText().toString().length() > 0) {
                event.setEventDateTime(Utils.convertDateFormat(eventDate.getText()
                        .toString(), EVENT_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss"));
                event.setEventDate(Utils.convertDateFormat(eventDate.getText().toString(),
                        EVENT_DATE_FORMAT, "yyyy-MM-dd HH:mm:ss"));
                arrayListNewEvent.add(event);
            }


        }

        //</editor-fold>

        // <editor-fold desc="Organization">.
        ArrayList<ProfileDataOperationOrganization> arrayListNewOrganization = new
                ArrayList<>();
        for (int i = 0; i < linearOrganizationDetail.getChildCount(); i++) {
            ProfileDataOperationOrganization organization = new
                    ProfileDataOperationOrganization();
            View view = linearOrganizationDetail.getChildAt(i);
            EditText inputCompanyName = (EditText) view.findViewById(R.id
                    .input_company_name);
            EditText inputDesignationName = (EditText) view.findViewById(R.id
                    .input_designation_name);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
            CheckBox checkboxOrganization = (CheckBox) view.findViewById(R.id
                    .checkbox_organization);
            organization.setOrgName(inputCompanyName.getText().toString());
            organization.setOrgJobTitle(inputDesignationName.getText().toString());
            organization.setOrgId((String) relativeRowEditProfile.getTag());
            organization.setIsCurrent(checkboxOrganization.isChecked() ? 1 : 0);

            organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);

            if (isValid && (StringUtils.length(organization.getOrgName()) > 0 || StringUtils.length
                    (organization.getOrgJobTitle()) > 0)) {
//                arrayListNewOrganization.add(organization);
                if (StringUtils.length(organization.getOrgName()) > 0) {
                    if (StringUtils.length(organization.getOrgJobTitle()) > 0) {
                        arrayListNewOrganization.add(organization);
                    } else {
                        isValid = false;
                        message = "Designation in organization is required!";
                    }
                } else {
                    isValid = false;
                    message = "Company name in organization is required!";
                }
            }


        }

        //</editor-fold>

        // <editor-fold desc="Social Contact">.
        ArrayList<ProfileDataOperationImAccount> arrayListNewImAccount = new ArrayList<>();
        for (int i = 0; i < linearSocialContactDetails.getChildCount(); i++) {
            ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
            View view = linearSocialContactDetails.getChildAt(i);
            EditText imAccountName = (EditText) view.findViewById(R.id.input_value);
            Spinner imAccountProtocol = (Spinner) view.findViewById(R.id.spinner_type);
            TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
            imAccount.setIMAccountDetails(imAccountName.getText().toString());
            imAccount.setIMAccountProtocol((String) imAccountProtocol.getSelectedItem());
            imAccount.setIMId((String) relativeRowEditProfile.getTag());

            if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                imAccount.setIMAccountPublic(Integer.parseInt(textIsPublic.getText().toString()));
            } else {
                imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);
            }

            if (StringUtils.length(imAccount.getIMAccountDetails()) > 0) {
                arrayListNewImAccount.add(imAccount);
            }

        }

        //</editor-fold>

        // <editor-fold desc="Address">
        ArrayList<ProfileDataOperationAddress> arrayListNewAddress = new ArrayList<>();
        for (int i = 0; i < linearAddressDetails.getChildCount(); i++) {
            ProfileDataOperationAddress address = new ProfileDataOperationAddress();
            View view = linearAddressDetails.getChildAt(i);
            Spinner addressType = (Spinner) view.findViewById(R.id.spinner_type);
            EditText country = (EditText) view.findViewById(R.id.input_country);
            EditText state = (EditText) view.findViewById(R.id.input_state);
            EditText city = (EditText) view.findViewById(R.id.input_city);
            EditText street = (EditText) view.findViewById(R.id.input_street);
            EditText neighborhood = (EditText) view.findViewById(R.id.input_neighborhood);
            EditText pinCode = (EditText) view.findViewById(R.id.input_pin_code);
            RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                    .id.relative_row_edit_profile);
            TextView textLatitude = (TextView) view.findViewById(R.id.input_latitude);
            TextView textLongitude = (TextView) view.findViewById(R.id.input_longitude);
            TextView textGoogleAddress = (TextView) view.findViewById(R.id
                    .input_google_address);

            TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);

            String countryName = country.getText().toString();
            String stateName = state.getText().toString();
            String cityName = city.getText().toString();
            String streetName = street.getText().toString();
            String neighborhoodName = neighborhood.getText().toString();
            String pinCodeName = pinCode.getText().toString();

            /*String[] addressStrings = {streetName, neighborhoodName, cityName, stateName,
                    countryName, pinCodeName};

            String formattedAddress = "";

            for (int j = 0; j < addressStrings.length; j++) {
                if (j != addressStrings.length - 1) {
                    if (StringUtils.length(addressStrings[j]) > 0) {
                        formattedAddress = formattedAddress + StringUtils.appendIfMissing
                                (addressStrings[j], ", ");
                    }
                } else {
                    formattedAddress = formattedAddress + pinCodeName;
                }
            }*/

            address.setCountry(countryName);
            address.setState(stateName);
            address.setCity(cityName);
            address.setStreet(streetName);
            address.setNeighborhood(neighborhoodName);
            address.setPostCode(pinCodeName);
//            address.setFormattedAddress(StringUtils.removeEnd(formattedAddress, ", "));
            address.setFormattedAddress(Utils.setFormattedAddress(streetName, neighborhoodName,
                    cityName, stateName, countryName, pinCodeName));
            address.setAddressType((String) addressType.getSelectedItem());
            address.setGoogleAddress(textGoogleAddress.getText().toString());
            address.setGoogleLatitude(textLatitude.getText().toString());
            address.setGoogleLongitude(textLongitude.getText().toString());
//            address.setAddPublic(Integer.parseInt(textIsPublic.getText().toString()));
            address.setAddId((String) relativeRowEditProfile.getTag());
            if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                address.setAddPublic(Integer.parseInt(textIsPublic.getText().toString()));
            } else {
                address.setAddPublic(IntegerConstants.PRIVACY_MY_CONTACT);
            }

            if (isValid && (StringUtils.length(address.getCountry()) > 0 || StringUtils.length
                    (address.getState()) > 0 || StringUtils.length(address.getCity()) > 0 ||
                    StringUtils.length(address.getStreet()) > 0)) {
//                arrayListNewAddress.add(address);
                if (StringUtils.length(address.getCountry()) > 0) {
                    if (StringUtils.length(address.getState()) > 0) {
                        if (StringUtils.length(address.getCity()) > 0) {
                            if (StringUtils.length(address.getStreet()) > 0) {
                                arrayListNewAddress.add(address);
                            } else {
                                isValid = false;
                                message = "Street in address is required!";
                            }
                        } else {
                            isValid = false;
                            message = "City in address is required!";
                        }
                    } else {
                        isValid = false;
                        message = "State in address is required!";
                    }
                } else {
                    isValid = false;
                    message = "Country in address is required!";
                }
            }


        }

        //</editor-fold>*/

        ArrayList<ProfileDataOperation> arrayList = new ArrayList<>();

        int radioButtonID = radioGroupGender.getCheckedRadioButtonId();
        View radioButton = radioGroupGender.findViewById(radioButtonID);
        int index = radioGroupGender.indexOfChild(radioButton);

        RadioButton r = (RadioButton) radioGroupGender.getChildAt(index);
        String selectedText = r.getText().toString();

        String bitmapString = "";
        if (selectedBitmap != null) {
            bitmapString = Utils.convertBitmapToBase64(selectedBitmap);
        }

        ProfileDataOperation profileDataOperation = new ProfileDataOperation();

        profileDataOperation.setPbNameFirst(inputFirstName.getText().toString());
        profileDataOperation.setPbNameLast(inputLastName.getText().toString());
        profileDataOperation.setPbGender(selectedText);
        profileDataOperation.setPbProfilePhoto(bitmapString);

        profileDataOperation.setPbEmailId(arrayListNewEmail);
        profileDataOperation.setPbPhoneNumber(arrayListNewPhone);
        profileDataOperation.setPbWebAddress(arrayListNewWebAddress);
        profileDataOperation.setPbEvent(arrayListNewEvent);
        profileDataOperation.setPbAddress(arrayListNewAddress);
        profileDataOperation.setPbOrganization(arrayListNewOrganization);
        profileDataOperation.setPbIMAccounts(arrayListNewImAccount);

        arrayList.add(profileDataOperation);
//                arrayList.add(updatedAddress);

        if (isValid) {
            editProfile(arrayList);
        } else {
            Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile, message);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void editProfile(ArrayList<ProfileDataOperation> editProfile) {

        WsRequestObject editProfileObject = new WsRequestObject();
        editProfileObject.setProfileEdit(editProfile);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    editProfileObject, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_UPDATE, getResources().getString(R.string.msg_please_wait),
                    true).execute(WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_UPDATE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>
}
