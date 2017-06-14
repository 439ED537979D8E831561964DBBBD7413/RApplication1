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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
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

public class EditProfileActivity extends BaseActivity implements WsResponseListener{

    private final String EVENT_DATE_FORMAT = "dd'th' MMM, yyyy";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 500;
    private static final String IMAGE_DIRECTORY_NAME = "RContactImages";
    public static String TEMP_PHOTO_FILE_NAME = "";

    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_label_profile_image)
    TextView textLabelProfileImage;
    @BindView(R.id.text_label_name)
    TextView textLabelName;
    @BindView(R.id.image_name_expand)
    ImageView imageNameExpand;
    @BindView(R.id.input_first_name)
    EditText inputFirstName;
    @BindView(R.id.input_last_name)
    EditText inputLastName;
    @BindView(R.id.button_name_update)
    Button buttonNameUpdate;
    @BindView(R.id.button_name_cancel)
    Button buttonNameCancel;
    @BindView(R.id.linear_name)
    LinearLayout linearName;
    @BindView(R.id.text_label_phone)
    TextView textLabelPhone;
    @BindView(R.id.image_phone_expand)
    ImageView imagePhoneExpand;
    @BindView(R.id.linear_phone_details)
    LinearLayout linearPhoneDetails;
    @BindView(R.id.button_phone_add_field)
    Button buttonPhoneAddField;
    @BindView(R.id.button_phone_update)
    Button buttonPhoneUpdate;
    @BindView(R.id.button_phone_cancel)
    Button buttonPhoneCancel;
    @BindView(R.id.linear_phone)
    LinearLayout linearPhone;
    @BindView(R.id.text_label_email)
    TextView textLabelEmail;
    @BindView(R.id.image_email_expand)
    ImageView imageEmailExpand;
    @BindView(R.id.linear_email_details)
    LinearLayout linearEmailDetails;
    @BindView(R.id.button_email_add_field)
    Button buttonEmailAddField;
    @BindView(R.id.button_email_update)
    Button buttonEmailUpdate;
    @BindView(R.id.button_email_cancel)
    Button buttonEmailCancel;
    @BindView(R.id.linear_email)
    LinearLayout linearEmail;
    @BindView(R.id.text_label_organization)
    TextView textLabelOrganization;
    @BindView(R.id.image_organization_expand)
    ImageView imageOrganizationExpand;
    @BindView(R.id.linear_organization_details)
    LinearLayout linearOrganizationDetails;
    @BindView(R.id.button_organization_add_field)
    Button buttonOrganizationAddField;
    @BindView(R.id.button_organization_update)
    Button buttonOrganizationUpdate;
    @BindView(R.id.button_organization_cancel)
    Button buttonOrganizationCancel;
    @BindView(R.id.linear_organization)
    LinearLayout linearOrganization;
    @BindView(R.id.text_label_website)
    TextView textLabelWebsite;
    @BindView(R.id.image_website_expand)
    ImageView imageWebsiteExpand;
    @BindView(R.id.linear_website_details)
    LinearLayout linearWebsiteDetails;
    @BindView(R.id.button_website_add_field)
    Button buttonWebsiteAddField;
    @BindView(R.id.button_website_update)
    Button buttonWebsiteUpdate;
    @BindView(R.id.button_website_cancel)
    Button buttonWebsiteCancel;
    @BindView(R.id.linear_website)
    LinearLayout linearWebsite;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.image_address_expand)
    ImageView imageAddressExpand;
    @BindView(R.id.linear_address_details)
    LinearLayout linearAddressDetails;
    @BindView(R.id.button_address_add_field)
    Button buttonAddressAddField;
    @BindView(R.id.button_address_update)
    Button buttonAddressUpdate;
    @BindView(R.id.button_address_cancel)
    Button buttonAddressCancel;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;
    @BindView(R.id.text_label_social_contact)
    TextView textLabelSocialContact;
    @BindView(R.id.image_social_contact_expand)
    ImageView imageSocialContactExpand;
    @BindView(R.id.linear_social_contact_details)
    LinearLayout linearSocialContactDetails;
    @BindView(R.id.button_social_contact_add_field)
    Button buttonSocialContactAddField;
    @BindView(R.id.button_social_contact_update)
    Button buttonSocialContactUpdate;
    @BindView(R.id.button_social_contact_cancel)
    Button buttonSocialContactCancel;
    @BindView(R.id.linear_social_contact)
    LinearLayout linearSocialContact;
    @BindView(R.id.text_label_event)
    TextView textLabelEvent;
    @BindView(R.id.image_event_expand)
    ImageView imageEventExpand;
    @BindView(R.id.linear_event_details)
    LinearLayout linearEventDetails;
    @BindView(R.id.button_event_add_field)
    Button buttonEventAddField;
    @BindView(R.id.button_event_update)
    Button buttonEventUpdate;
    @BindView(R.id.button_event_cancel)
    Button buttonEventCancel;
    @BindView(R.id.linear_event)
    LinearLayout linearEvent;
    @BindView(R.id.text_label_gender)
    TextView textLabelGender;
    @BindView(R.id.image_gender_expand)
    ImageView imageGenderExpand;
    @BindView(R.id.linear_gender_details)
    LinearLayout linearGenderDetails;
    @BindView(R.id.button_gender_update)
    Button buttonGenderUpdate;
    @BindView(R.id.button_gender_cancel)
    Button buttonGenderCancel;
    @BindView(R.id.linear_gender)
    LinearLayout linearGender;
    @BindView(R.id.relative_root_edit_profile)
    RelativeLayout relativeRootEditProfile;

    EditText inputCountry;
    EditText inputState;
    EditText inputCity;
    EditText inputStreet;
    EditText inputNeighborhood;
    EditText inputPinCode;

    ArrayAdapter<String> spinnerPhoneAdapter, spinnerEmailAdapter, spinnerWebsiteAdapter,
            spinnerImAccountAdapter, spinnerEventAdapter;

    ArrayList<ProfileDataOperation> arrayListProfile;
    boolean isStorageFromSettings = false, isCameraFromSettings = false;

    Bitmap selectedBitmap = null;
    private File mFileTemp;
    private Uri fileUri;

    int clickedPosition = -1;
    String formattedAddress;

    ArrayList<Object> arrayListPhoneNumberObject;
    ArrayList<Object> arrayListEmailObject;
    ArrayList<Object> arrayListWebsiteObject;
    ArrayList<Object> arrayListSocialContactObject;
    ArrayList<Object> arrayListAddressObject;
    ArrayList<Object> arrayListEventObject;
    ArrayList<Object> arrayListOrganizationObject;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_temp);
        ButterKnife.bind(this);
        arrayListProfile = new ArrayList<>();
        init();
    }

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

        }
       /* else if (requestCode == AppConstants.REQUEST_CODE_MAP_LOCATION_SELECTION) {
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
                        *//*inputCountry.setText("India");
                        inputState.setText("Gujarat");
                        inputCity.setText("Surat");
                        inputStreet.setText("");
                        inputPinCode.setText("");
                        inputNeighborhood.setText("");*//*
                        inputCountry.setText(inputCountry.getText().toString());
                        inputState.setText(inputState.getText().toString());
                        inputCity.setText(inputCity.getText().toString());
                        inputStreet.setText(inputStreet.getText().toString());
                        inputPinCode.setText(inputPinCode.getText().toString());
                        inputNeighborhood.setText(inputNeighborhood.getText().toString());
                    }
                }
            }
        }*/
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

                    /*ProfileDataOperation profileDetail = editProfileResponse.getProfileDetail();
                    Utils.setObjectPreference(EditProfileTempActivity.this, AppConstants
                            .PREF_REGS_USER_OBJECT, profileDetail);

                    storeProfileDataToDb(profileDetail);*/

                    Utils.showSuccessSnackBar(this, relativeRootEditProfile, "Profile Updated " +
                            "Successfully! ");

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

    //</editor-fold>

    //<editor-fold desc="Onclick">

    @OnClick({R.id.button_phone_add_field, R.id.button_email_add_field, R.id
            .button_address_add_field, R.id.button_organization_add_field, R.id
            .button_website_add_field, R.id.button_social_contact_add_field, R.id
            .button_event_add_field})
    public void onClick(View view) {

        switch (view.getId()) {

            //<editor-fold desc="button_phone_add_field">
            case R.id.button_phone_add_field:
                checkBeforeViewAdd(AppConstants.PHONE_NUMBER, linearPhoneDetails);
                break;
            //</editor-fold>

            //<editor-fold desc="button_email_add_field">
            case R.id.button_email_add_field:
                checkBeforeViewAdd(AppConstants.EMAIL, linearEmailDetails);
                break;
            //</editor-fold>

            //<editor-fold desc="button_website_add_field">
            case R.id.button_website_add_field:
                checkBeforeViewAdd(AppConstants.WEBSITE, linearWebsiteDetails);
                break;
            //</editor-fold>

            // <editor-fold desc="button_social_contact_add_field">
            case R.id.button_social_contact_add_field:
                checkBeforeViewAdd(AppConstants.IM_ACCOUNT, linearSocialContactDetails);
                break;
            //</editor-fold>

            // <editor-fold desc="button_address_add_field">
            case R.id.button_address_add_field:
                checkBeforeAddressViewAdd();
                break;
            //</editor-fold>

            // <editor-fold desc="button_event_add_field">
            case R.id.button_event_add_field:
                checkBeforeViewAdd(AppConstants.EVENT, linearEventDetails);
                break;
            //</editor-fold>

            // <editor-fold desc="button_organization_add_field">
            case R.id.button_organization_add_field:
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

    @OnClick({R.id.button_name_update})
    public void onUpdateClick(View view) {

        ProfileDataOperation profileDataOperation;

        switch (view.getId()) {

            //<editor-fold desc="button_name_update">
            case R.id.button_name_update:
                profileDataOperation = new ProfileDataOperation();
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                if (StringUtils.length(firstName) > 0 && StringUtils.length(firstName) > 0) {
                    profileDataOperation.setPbNameFirst(firstName);
                    profileDataOperation.setPbNameLast(lastName);
                    editProfile(profileDataOperation);
                }
                break;
            //</editor-fold>

        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        profileDetails();
        phoneNumberDetails();
        emailDetails();
        websiteDetails();
        socialContactDetails();
        addressDetails();
        eventDetails();
        organizationDetails();

    }

    private void profileDetails() {

        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt(getUserPmId()));
        if (userProfile != null) {
            inputFirstName.setText(userProfile.getPmFirstName());
            inputLastName.setText(userProfile.getPmLastName());

           /* if (StringUtils.endsWithIgnoreCase(userProfile.getPmGender(), "Female")) {
                radioGroupGender.check(R.id.radio_female);
            } else if (StringUtils.endsWithIgnoreCase(userProfile.getPmGender(), "Male")) {
                radioGroupGender.check(R.id.radio_male);
            }*/

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
            event.setIsYearHidden(arrayListEvent.get(i).getEvmIsYearHidden());
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
          /*  address.setGoogleLatitude(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLongitude(arrayListAddress.get(i).getAmGoogleLongitude());*/
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
        for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
            View linearView = linearOrganizationDetails.getChildAt(i);
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

    private void addOrganizationView(Object detailObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_organization,
                null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        TextView textLabelCheckbox = (TextView) view.findViewById(R.id.text_label_checkbox);
        final EditText inputCompanyName = (EditText) view.findViewById(R.id.input_company_name);
        final EditText inputDesignationName = (EditText) view.findViewById(R.id
                .input_designation_name);
        CheckBox checkboxOrganization = (CheckBox) view.findViewById(R.id.checkbox_organization);

        checkboxOrganization.setTag(linearOrganizationDetails.getChildCount());

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        inputCompanyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);
        inputDesignationName.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);

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
                    for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
                        View view = linearOrganizationDetails.getChildAt(i);
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
                if (linearOrganizationDetails.getChildCount() > 1) {
                    linearOrganizationDetails.removeView(relativeRowEditProfile);
                } else if (linearOrganizationDetails.getChildCount() == 1) {
                    inputCompanyName.setText("");
                    inputDesignationName.setText("");
                }
            }
        });

        linearOrganizationDetails.addView(view);
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
          /*  textLatitude.setText(address.getGoogleLatitude());
            textLongitude.setText(address.getGoogleLongitude());*/
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
//                startActivityIntent(EditProfileTempActivity.this, MapsActivity.class, null);
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

    private void selectImageFromGallery() {
        mFileTemp = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);
    }

    private void selectImageFromCamera() {
        mFileTemp = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void copyStream(InputStream inputStream, FileOutputStream fileOutputStream) throws
            IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void editProfile(ProfileDataOperation editProfile) {

        /*WsRequestObject editProfileObject = new WsRequestObject();
        editProfileObject.setProfileEdit(editProfile);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    editProfileObject, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_UPDATE, getResources().getString(R.string.msg_please_wait),
                    true).execute(WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_UPDATE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }

    //</editor-fold>

}
