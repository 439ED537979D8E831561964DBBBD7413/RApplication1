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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    Bitmap selectedBitmap = null;
    private File mFileTemp;
    private Uri fileUri;

    boolean isStorageFromSettings = false, isCameraFromSettings = false;

    MaterialDialog permissionConfirmationDialog;

    ArrayList<ProfileDataOperation> arrayListProfile;
//    ArrayList<ProfileDataOperation> arrayListMergedProfile;

//    ProfileDataOperation userOldProfile;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
//        userOldProfile = new ProfileDataOperation();
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

                Gson gson = new Gson();
                String oldObjectJson, newObjectJson;

                //<editor-fold desc="Email">
                ArrayList<ProfileDataOperationEmail> arrayListNewEmail = new ArrayList<>();
                for (int i = 0; i < linearEmailDetails.getChildCount(); i++) {
                    ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                    View view = linearEmailDetails.getChildAt(i);
                    EditText emailId = (EditText) view.findViewById(R.id.input_value);
                    Spinner emailType = (Spinner) view.findViewById(R.id.spinner_type);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                            .id.relative_row_edit_profile);
                    email.setEmEmailId(emailId.getText().toString());
                    email.setEmType((String) emailType.getSelectedItem());
                    email.setEmId((String) relativeRowEditProfile.getTag());

                    arrayListNewEmail.add(email);

                }

                oldObjectJson = gson.toJson(arrayListEmailObject);
                newObjectJson = gson.toJson(arrayListNewEmail);

                ArrayList<ProfileDataOperation> updatedEmails = new ArrayList<>();
                updatedEmails.addAll(compareJsonStructure(AppConstants.EMAIL, oldObjectJson,
                        newObjectJson));
                //</editor-fold>

                //<editor-fold desc="Phone Number">
                ArrayList<ProfileDataOperationPhoneNumber> arrayListNewPhone = new ArrayList<>();
                for (int i = 0; i < linearPhoneDetails.getChildCount(); i++) {
                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();
                    View view = linearPhoneDetails.getChildAt(i);
                    EditText emailId = (EditText) view.findViewById(R.id.input_value);
                    Spinner emailType = (Spinner) view.findViewById(R.id.spinner_type);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                            .id.relative_row_edit_profile);
                    phoneNumber.setPhoneNumber(emailId.getText().toString());
                    phoneNumber.setPhoneType((String) emailType.getSelectedItem());
                    phoneNumber.setPhoneId((String) relativeRowEditProfile.getTag());

                    arrayListNewPhone.add(phoneNumber);

                }

                oldObjectJson = gson.toJson(arrayListPhoneNumberObject);
                newObjectJson = gson.toJson(arrayListNewPhone);

                ArrayList<ProfileDataOperation> updatedNumbers = new ArrayList<>();
                updatedNumbers.addAll(compareJsonStructure(AppConstants.PHONE_NUMBER,
                        oldObjectJson, newObjectJson));
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

                    arrayListNewWebAddress.add(webAddress);

                }

                oldObjectJson = gson.toJson(arrayListWebsiteObject);
                newObjectJson = gson.toJson(arrayListNewWebAddress);

                ArrayList<ProfileDataOperation> updatedWebAddress = new ArrayList<>();
                updatedWebAddress.addAll(compareJsonStructure(AppConstants.WEBSITE,
                        oldObjectJson, newObjectJson));
                //</editor-fold>

                // <editor-fold desc="Event">
                ArrayList<ProfileDataOperationEvent> arrayListNewEvent = new ArrayList<>();
                for (int i = 0; i < linearEventDetails.getChildCount(); i++) {
                    ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                    View view = linearEventDetails.getChildAt(i);
                    EditText eventDate = (EditText) view.findViewById(R.id.input_value);
                    Spinner eventType = (Spinner) view.findViewById(R.id.spinner_type);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                            .id.relative_row_edit_profile);
                    event.setEventDateTime(eventDate.getText().toString());
                    event.setEventType((String) eventType.getSelectedItem());
                    event.setEventId((String) relativeRowEditProfile.getTag());

                    arrayListNewEvent.add(event);

                }

                oldObjectJson = gson.toJson(arrayListEventObject);
                newObjectJson = gson.toJson(arrayListNewEvent);

                ArrayList<ProfileDataOperation> updatedEvent = new ArrayList<>();
                updatedEvent.addAll(compareJsonStructure(AppConstants.EVENT, oldObjectJson,
                        newObjectJson));
                //</editor-fold>

                // <editor-fold desc="Organization">
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
                    organization.setOrgName(inputCompanyName.getText().toString());
                    organization.setOrgJobTitle(inputDesignationName.getText().toString());
                    organization.setOrgId((String) relativeRowEditProfile.getTag());

                    arrayListNewOrganization.add(organization);

                }

                oldObjectJson = gson.toJson(arrayListOrganizationObject);
                newObjectJson = gson.toJson(arrayListNewOrganization);

                ArrayList<ProfileDataOperation> updatedOrganization = new ArrayList<>();
                updatedOrganization.addAll(compareJsonStructure(AppConstants.ORGANIZATION,
                        oldObjectJson, newObjectJson));

                //</editor-fold>

                // <editor-fold desc="Address">
              /*  ArrayList<ProfileDataOperationAddress> arrayListNewAddress = new ArrayList<>();
                for (int i = 0; i < linearAddressDetails.getChildCount(); i++) {
                    ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                    View view = linearAddressDetails.getChildAt(i);
                    Spinner addressType = (Spinner) view.findViewById(R.id.spinner_type);
                    EditText country = (EditText) view.findViewById(R.id.input_country);
                    EditText state = (EditText) view.findViewById(R.id.input_state);
                    EditText city = (EditText) view.findViewById(R.id.input_city);
                    EditText street = (EditText) view.findViewById(R.id.input_street);
                    EditText neighborhood = (EditText) view.findViewById(R.id.input_neighborhood);
                    EditText poBox = (EditText) view.findViewById(R.id.input_po_box);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R
                            .id.relative_row_edit_profile);

                    address.setCountry(country.getText().toString());
                    address.setState(state.getText().toString());
                    address.setCity(city.getText().toString());
                    address.setStreet(street.getText().toString());
                    address.setNeighborhood(neighborhood.getText().toString());
                    address.setPoBox(poBox.getText().toString());
                    address.setAddressType((String) addressType.getSelectedItem());
                    address.setAddId((String) relativeRowEditProfile.getTag());

                    arrayListNewAddress.add(address);

                }

                oldObjectJson = gson.toJson(arrayListAddressObject);
                newObjectJson = gson.toJson(arrayListNewAddress);

                ArrayList<ProfileDataOperation> updatedAddress = new ArrayList<>();
                updatedAddress.addAll(compareJsonStructure(AppConstants.ADDRESS,
                        oldObjectJson, newObjectJson));*/

                //</editor-fold>

                ArrayList<ArrayList<ProfileDataOperation>> arrayList = new ArrayList<>();
                arrayList.add(updatedNumbers);
                arrayList.add(updatedEmails);
                arrayList.add(updatedWebAddress);
                arrayList.add(updatedEvent);
                arrayList.add(updatedOrganization);
//                arrayList.add(updatedAddress);

                Log.i("onComplete", new Gson().toJson(combineResult(arrayList)));
                editProfile(combineResult(arrayList));

                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            selectedBitmap = BitmapFactory.decodeFile(fileUri.getPath());
//            imageProfile.setImageBitmap(bitmap);

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
//                imageProfile.setImageBitmap(bitmap);

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
                inputCountry.setText(objAddress.getCountry());
                inputState.setText(objAddress.getState());
                inputCity.setText(objAddress.getCity());
                inputStreet.setText(objAddress.getAddressLine());
                inputPinCode.setText(objAddress.getPostalCode());
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
//                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
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

                    Log.i("onDeliveryResponse", editProfileResponse.getMessage());

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

//            userOldProfile.setPbNameFirst(userProfile.getPmFirstName());
//            userOldProfile.setPbNameLast(userProfile.getPmLastName());
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
        ArrayList<ProfileDataOperationWebAddress> arrayListProfileDataWebAddress = new
                ArrayList<>();
        for (int i = 0; i < arrayListWebsite.size(); i++) {
            ProfileDataOperationWebAddress webAddress = new ProfileDataOperationWebAddress();
            webAddress.setWebAddress(arrayListWebsite.get(i).getWmWebsiteUrl());
            webAddress.setWebType(arrayListWebsite.get(i).getWmWebsiteType());
            webAddress.setWebId(arrayListWebsite.get(i).getWmRecordIndexId());
            arrayListWebsiteObject.add(webAddress);
            arrayListProfileDataWebAddress.add(webAddress);
        }

        if (arrayListWebsiteObject.size() > 0) {
            for (int i = 0; i < arrayListWebsiteObject.size(); i++) {
                addView(AppConstants.WEBSITE, linearWebsiteDetails, arrayListWebsiteObject.get(i)
                        , i);
            }
//            userOldProfile.setPbWebAddress(arrayListProfileDataWebAddress);
        } else {
            addView(AppConstants.WEBSITE, linearWebsiteDetails, null, -1);
        }

    }

    private void socialContactDetails() {
        TableImMaster tableImMaster = new TableImMaster(databaseHandler);

        ArrayList<ImAccount> arrayListImAccount = tableImMaster.getImAccountFromPmId(Integer
                .parseInt(getUserPmId()));
        arrayListSocialContactObject = new ArrayList<>();
        ArrayList<ProfileDataOperationImAccount> arrayListProfileDataImAccount = new ArrayList<>();
        for (int i = 0; i < arrayListImAccount.size(); i++) {
            ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
            imAccount.setIMAccountProtocol(arrayListImAccount.get(i).getImImProtocol());
//            imAccount.setIMAccountType(arrayListImAccount.get(i).getImImType());
            imAccount.setIMAccountDetails(arrayListImAccount.get(i).getImImDetail());
            imAccount.setIMId(arrayListImAccount.get(i).getImRecordIndexId());
            arrayListSocialContactObject.add(imAccount);
            arrayListProfileDataImAccount.add(imAccount);
        }

        if (arrayListSocialContactObject.size() > 0) {
            for (int i = 0; i < arrayListSocialContactObject.size(); i++) {
                addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails,
                        arrayListSocialContactObject.get(i), i);
            }
//            userOldProfile.setPbIMAccounts(arrayListProfileDataImAccount);
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
            address.setNeighborhood(arrayListAddress.get(i).getAmNeighborhood());
            address.setPostCode(arrayListAddress.get(i).getAmPostCode());
            address.setAddressType(arrayListAddress.get(i).getAmAddressType());
            address.setAddId(arrayListAddress.get(i).getAmRecordIndexId());
            arrayListAddressObject.add(address);
        }

        if (arrayListAddressObject.size() > 0) {
            for (int i = 0; i < arrayListAddressObject.size(); i++) {
                addAddressView(arrayListAddressObject.get(i));
            }
        } else {
            addAddressView(null);
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
                    ProfileDataOperationEvent event = (ProfileDataOperationEvent) detailObject;
                    inputValue.setText(event.getEventDateTime());
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

    private void addAddressView(Object detailObject) {
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

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

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
            int spinnerPosition;
            if (Arrays.asList(getResources().getStringArray(R.array.types_email_address))
                    .contains(StringUtils.defaultString(address.getAddressType()))) {
                spinnerPosition = spinnerPhoneAdapter.getPosition(address.getAddressType());
            } else {
                spinnerPhoneAdapter.add(address.getAddressType());
                spinnerPhoneAdapter.notifyDataSetChanged();
                spinnerPosition = spinnerPhoneAdapter.getPosition(address.getAddressType());
            }
            spinnerType.setSelection(spinnerPosition);
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
            /*if (inputCountry.getText().length() < 1 || inputState.getText().length() < 1 ||
                    inputCity.getText().length() < 1 || inputStreet.getText().length() < 1) {*/
                toAdd = false;
                break;
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            addAddressView(null);
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

    private ArrayList<ProfileDataOperation> compareJsonStructure(int objectType, String
            oldObjectJson, String newObjectJson) {

        ArrayList<Integer> arrayListAddedPositions = new ArrayList<>();
        ArrayList<Integer> arrayListRemovedPositions = new ArrayList<>();
        ArrayList<Integer> arrayListReplacedPositions = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode tree1 = mapper.readTree(oldObjectJson);
            JsonNode tree2 = mapper.readTree(newObjectJson);

            Log.i("onComplete", String.valueOf(tree1.equals(tree2)));

            if (!tree1.equals(tree2)) {
                JsonNode patchNode = JsonDiff.asJson(tree1, tree2);
                String diff = patchNode.toString();
                Log.i("diff", diff);
                JSONArray jsonArray = new JSONArray(diff);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String operation = jsonObject.optString("op");
                    if (operation.equalsIgnoreCase("add")) {
                        int position;
                        if (StringUtils.countMatches(jsonObject.optString("path"),
                                StringUtils.defaultString("/")) > 1) {
                            position = Integer.valueOf(StringUtils.substring(jsonObject
                                    .optString("path"), 1, StringUtils.indexOf(jsonObject
                                    .getString("path"), StringUtils.defaultString
                                    ("/"), 1)));
                        } else {
                            position = Integer.valueOf(StringUtils.substring(jsonObject
                                    .optString("path"), 1));
                        }
                        arrayListAddedPositions.add(position);
                    } else if (operation.equalsIgnoreCase("replace")) {
                        int position;
                        if (StringUtils.countMatches(jsonObject.optString("path"),
                                StringUtils.defaultString("/")) > 1) {
                            position = Integer.valueOf(StringUtils.substring(jsonObject
                                    .optString("path"), 1, StringUtils.indexOf(jsonObject
                                    .getString("path"), StringUtils.defaultString
                                    ("/"), 1)));
                        } else {
                            position = Integer.valueOf(StringUtils.substring(jsonObject
                                    .optString("path"), 1));
                        }
                        arrayListReplacedPositions.add(position);
                    } else if (operation.equalsIgnoreCase("remove")) {
                        int position;
                        if (StringUtils.countMatches(jsonObject.optString("path"),
                                StringUtils.defaultString("/")) > 1) {
                            position = Integer.valueOf(StringUtils.substring(jsonObject
                                    .optString("path"), 1, StringUtils.indexOf(jsonObject
                                    .getString("path"), StringUtils.defaultString
                                    ("/"), 1)));
                        } else {
                            position = Integer.valueOf(StringUtils.substring(jsonObject
                                    .optString("path"), 1));
                        }
                        if (!arrayListReplacedPositions.contains(position)) {
                            arrayListRemovedPositions.add(position);
                        }
                    }
                    Log.i("operation", operation);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("onComplete", "Exception");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ArrayList<ProfileDataOperation> arrayListTempProfile = new ArrayList<>();
        Gson gson = new Gson();

        if (arrayListAddedPositions.size() > 0) {
            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
            profileDataOperation.setFlag(String.valueOf(getResources().getInteger
                    (R.integer.sync_update_insert)));
            switch (objectType) {
                case AppConstants.EMAIL:
                    ArrayList<ProfileDataOperationEmail> arrayListNewEmail = gson.fromJson
                            (newObjectJson, new TypeToken<ArrayList<ProfileDataOperationEmail>>() {
                            }.getType());
                    ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                    for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                        ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                        email.setEmType(arrayListNewEmail.get(arrayListAddedPositions.get
                                (i)).getEmType());
                        email.setEmEmailId(arrayListNewEmail.get(arrayListAddedPositions
                                .get(i)).getEmEmailId());
                        emails.add(email);
                    }
                    profileDataOperation.setPbEmailId(emails);
                    break;
                case AppConstants.PHONE_NUMBER:
                    ArrayList<ProfileDataOperationPhoneNumber> arrayListNewPhone = gson.fromJson
                            (newObjectJson, new
                                    TypeToken<ArrayList<ProfileDataOperationPhoneNumber>>() {
                                    }.getType());
                    ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();
                    for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();
                        phoneNumber.setPhoneType(arrayListNewPhone.get(arrayListAddedPositions.get
                                (i)).getPhoneType());
                        phoneNumber.setPhoneNumber(arrayListNewPhone.get(arrayListAddedPositions
                                .get(i)).getPhoneNumber());
                        phoneNumbers.add(phoneNumber);
                    }
                    profileDataOperation.setPbPhoneNumber(phoneNumbers);
                    break;
                case AppConstants.WEBSITE:
                    ArrayList<ProfileDataOperationWebAddress> arrayListNewWebsite = gson.fromJson
                            (newObjectJson, new
                                    TypeToken<ArrayList<ProfileDataOperationWebAddress>>() {
                                    }.getType());
                    ArrayList<ProfileDataOperationWebAddress> webAddresses = new ArrayList<>();
                    for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                        if (arrayListNewWebsite.get(arrayListAddedPositions
                                .get(i)).getWebAddress().length() > 0) {
                            ProfileDataOperationWebAddress webAddress = new
                                    ProfileDataOperationWebAddress();
                            webAddress.setWebType(arrayListNewWebsite.get(arrayListAddedPositions
                                    .get(i)).getWebType());
                            webAddress.setWebAddress(arrayListNewWebsite.get(arrayListAddedPositions
                                    .get(i)).getWebAddress());
                            webAddresses.add(webAddress);
                        }
                    }
                    if (webAddresses.size() > 0) {
                        profileDataOperation.setPbWebAddress(webAddresses);
                    }
                    break;
                case AppConstants.ORGANIZATION:
                    ArrayList<ProfileDataOperationOrganization> arrayListNewOrganization = gson
                            .fromJson
                                    (newObjectJson, new
                                            TypeToken<ArrayList<ProfileDataOperationOrganization>>() {
                                            }.getType());
                    ArrayList<ProfileDataOperationOrganization> organizations = new ArrayList<>();
                    for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                        if (arrayListNewOrganization.get(arrayListAddedPositions
                                .get(i)).getOrgName().length() > 0) {
                            ProfileDataOperationOrganization organization = new
                                    ProfileDataOperationOrganization();
                            organization.setOrgName(arrayListNewOrganization.get
                                    (arrayListAddedPositions.get(i)).getOrgName());
                            organization.setOrgJobTitle(arrayListNewOrganization.get
                                    (arrayListAddedPositions.get(i)).getOrgJobTitle());
                            organizations.add(organization);
                        }
                    }
                    if (organizations.size() > 0) {
                        profileDataOperation.setPbOrganization(organizations);
                    }
                    break;
                case AppConstants.EVENT:
                    ArrayList<ProfileDataOperationEvent> arrayListNewEvent = gson.fromJson
                            (newObjectJson, new
                                    TypeToken<ArrayList<ProfileDataOperationEvent>>() {
                                    }.getType());
                    ArrayList<ProfileDataOperationEvent> events = new ArrayList<>();
                    for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                        ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                        event.setEventType(arrayListNewEvent.get(arrayListAddedPositions.get
                                (i)).getEventType());
                        event.setEventDateTime(arrayListNewEvent.get(arrayListAddedPositions
                                .get(i)).getEventDateTime());
                        events.add(event);
                    }
                    profileDataOperation.setPbEvent(events);
                    break;
                case AppConstants.ADDRESS:
                    ArrayList<ProfileDataOperationAddress> arrayListNewAddress = gson.fromJson
                            (newObjectJson, new TypeToken<ArrayList<ProfileDataOperationAddress>>
                                    () {
                            }.getType());
                    ArrayList<ProfileDataOperationAddress> addresses = new ArrayList<>();
                    for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                        ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                        address.setCountry(arrayListNewAddress.get(arrayListAddedPositions.get
                                (i)).getCountry());
                        address.setState(arrayListNewAddress.get(arrayListAddedPositions
                                .get(i)).getState());
                        address.setCity(arrayListNewAddress.get(arrayListAddedPositions
                                .get(i)).getCity());
                        address.setStreet(arrayListNewAddress.get(arrayListAddedPositions
                                .get(i)).getStreet());
                        address.setNeighborhood(arrayListNewAddress.get(arrayListAddedPositions
                                .get(i)).getNeighborhood());
                        address.setPostCode(arrayListNewAddress.get(arrayListAddedPositions
                                .get(i)).getPostCode());
                        address.setAddressType(arrayListNewAddress.get(arrayListAddedPositions
                                .get(i)).getAddressType());
                        addresses.add(address);
                    }
                    profileDataOperation.setPbAddress(addresses);
                    break;
            }
            arrayListTempProfile.add(profileDataOperation);
        }

        if (arrayListRemovedPositions.size() > 0) {
            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
            profileDataOperation.setFlag(String.valueOf(getResources().getInteger
                    (R.integer.sync_update_delete)));
            switch (objectType) {
                case AppConstants.EMAIL:
                    ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                    for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                        ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                        email.setEmId(((ProfileDataOperationEmail) arrayListEmailObject
                                .get(i)).getEmId());
                       /* email.setEmId(String.valueOf(arrayListRemovedPositions.get(i)));*/
                        emails.add(email);
                    }
                    profileDataOperation.setPbEmailId(emails);
                    break;

                case AppConstants.PHONE_NUMBER:
                    ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();
                    for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();
                        phoneNumber.setPhoneId(((ProfileDataOperationPhoneNumber)
                                arrayListPhoneNumberObject
                                        .get(i)).getPhoneId());
                        /*phoneNumber.setPhoneId(String.valueOf(arrayListRemovedPositions.get(i))
                        );*/
                        phoneNumbers.add(phoneNumber);
                    }
                    profileDataOperation.setPbPhoneNumber(phoneNumbers);
                    break;

                case AppConstants.WEBSITE:
                    ArrayList<ProfileDataOperationWebAddress> webAddresses = new ArrayList<>();
                    for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                        ProfileDataOperationWebAddress webAddress = new
                                ProfileDataOperationWebAddress();
                        webAddress.setWebId(((ProfileDataOperationWebAddress)
                                arrayListWebsiteObject.get(i)).getWebId());
                        /*webAddress.setWebId(String.valueOf(arrayListRemovedPositions.get(i)));*/
                        webAddresses.add(webAddress);
                    }
                    profileDataOperation.setPbWebAddress(webAddresses);
                    break;

                case AppConstants.ORGANIZATION:
                    ArrayList<ProfileDataOperationOrganization> organizations = new ArrayList<>();
                    for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                        ProfileDataOperationOrganization organization = new
                                ProfileDataOperationOrganization();
                        organization.setOrgId(((ProfileDataOperationOrganization)
                                arrayListOrganizationObject.get(i)).getOrgId());
                        /*webAddress.setWebId(String.valueOf(arrayListRemovedPositions.get(i)));*/
                        organizations.add(organization);
                    }
                    profileDataOperation.setPbOrganization(organizations);
                    break;

                case AppConstants.EVENT:
                    ArrayList<ProfileDataOperationEvent> events = new ArrayList<>();
                    for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                        ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                        event.setEventId(((ProfileDataOperationEvent)
                                arrayListEventObject.get(i)).getEventId());
                        /*event.setEventId(String.valueOf(arrayListRemovedPositions.get(i)));*/
                        events.add(event);
                    }
                    profileDataOperation.setPbEvent(events);
                    break;
                case AppConstants.ADDRESS:
                    ArrayList<ProfileDataOperationAddress> addresses = new ArrayList<>();
                    for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                        ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                        address.setAddId(((ProfileDataOperationAddress)
                                arrayListAddressObject.get(i)).getAddId());
                        /*event.setEventId(String.valueOf(arrayListRemovedPositions.get(i)));*/
                        addresses.add(address);
                    }
                    profileDataOperation.setPbAddress(addresses);
                    break;
            }
            arrayListTempProfile.add(profileDataOperation);
        }

        if (arrayListReplacedPositions.size() > 0) {
            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
            profileDataOperation.setFlag(String.valueOf(getResources().getInteger
                    (R.integer.sync_update_update)));
            switch (objectType) {
                case AppConstants.EMAIL:
                    ArrayList<ProfileDataOperationEmail> arrayListNewEmail = gson.fromJson
                            (newObjectJson, new TypeToken<ArrayList<ProfileDataOperationEmail>>() {
                            }.getType());
                    ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                    for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                        ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                        /*email.setEmId(((ProfileDataOperationEmail) arrayListEmailObject
                                .get(i)).getEmId());*/
                        email.setEmId(arrayListNewEmail.get(arrayListReplacedPositions.get
                                (i)).getEmId());
                        email.setEmType(arrayListNewEmail.get(arrayListReplacedPositions.get
                                (i)).getEmType());
                        email.setEmEmailId(arrayListNewEmail.get(arrayListReplacedPositions
                                .get(i)).getEmEmailId());
                        emails.add(email);
                    }
                    profileDataOperation.setPbEmailId(emails);
                    break;
                case AppConstants.PHONE_NUMBER:
                    ArrayList<ProfileDataOperationPhoneNumber> arrayListNewPhoneNumber = gson
                            .fromJson
                                    (newObjectJson, new
                                            TypeToken<ArrayList<ProfileDataOperationPhoneNumber>>
                                                    () {
                                            }.getType());
                    ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();
                    for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();
                        /*phoneNumber.setPhoneId(((ProfileDataOperationPhoneNumber)
                                arrayListPhoneNumberObject.get(i)).getPhoneId());*/
                        phoneNumber.setPhoneId(arrayListNewPhoneNumber.get
                                (arrayListReplacedPositions.get(i)).getPhoneId());
                        phoneNumber.setPhoneType(arrayListNewPhoneNumber.get
                                (arrayListReplacedPositions.get(i)).getPhoneType());
                        phoneNumber.setPhoneNumber(arrayListNewPhoneNumber.get
                                (arrayListReplacedPositions.get(i)).getPhoneNumber());
                        phoneNumbers.add(phoneNumber);
                    }
                    profileDataOperation.setPbPhoneNumber(phoneNumbers);
                    break;
                case AppConstants.WEBSITE:
                    ArrayList<ProfileDataOperationWebAddress> arrayListNewWebAddress = gson
                            .fromJson
                                    (newObjectJson, new
                                            TypeToken<ArrayList<ProfileDataOperationWebAddress>>
                                                    () {
                                            }.getType());
                    ArrayList<ProfileDataOperationWebAddress> webAddresses = new ArrayList<>();
                    for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                        ProfileDataOperationWebAddress webAddress = new
                                ProfileDataOperationWebAddress();
                        /*webAddress.setWebId(((ProfileDataOperationWebAddress)
                                arrayListWebsiteObject.get(i)).getWebId());*/
                        webAddress.setWebId(arrayListNewWebAddress.get
                                (arrayListReplacedPositions.get(i)).getWebId());
                        webAddress.setWebType(arrayListNewWebAddress.get
                                (arrayListReplacedPositions.get(i)).getWebType());
                        webAddress.setWebAddress(arrayListNewWebAddress.get
                                (arrayListReplacedPositions.get(i)).getWebAddress());
                        webAddresses.add(webAddress);
                    }
                    profileDataOperation.setPbWebAddress(webAddresses);
                    break;
                case AppConstants.ORGANIZATION:
                    ArrayList<ProfileDataOperationOrganization> arrayListNewOrganization = gson
                            .fromJson
                                    (newObjectJson, new
                                            TypeToken<ArrayList<ProfileDataOperationOrganization>>() {
                                            }.getType());
                    ArrayList<ProfileDataOperationOrganization> organizations = new ArrayList<>();
                    for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                        ProfileDataOperationOrganization organization = new
                                ProfileDataOperationOrganization();
                       /* event.setEventId(((ProfileDataOperationEvent)
                                arrayListEventObject.get(i)).getEventId());*/
                        organization.setOrgId(arrayListNewOrganization.get
                                (arrayListReplacedPositions.get(i)).getOrgId());
                        organization.setOrgName(arrayListNewOrganization.get
                                (arrayListReplacedPositions.get(i)).getOrgName());
                        organization.setOrgJobTitle(arrayListNewOrganization.get
                                (arrayListReplacedPositions.get(i)).getOrgJobTitle());
                        organizations.add(organization);
                    }
                    profileDataOperation.setPbOrganization(organizations);
                    break;
                case AppConstants.EVENT:
                    ArrayList<ProfileDataOperationEvent> arrayListNewEvent = gson.fromJson
                            (newObjectJson, new TypeToken<ArrayList<ProfileDataOperationEvent>>() {
                            }.getType());
                    ArrayList<ProfileDataOperationEvent> events = new ArrayList<>();
                    for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                        ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                       /* event.setEventId(((ProfileDataOperationEvent)
                                arrayListEventObject.get(i)).getEventId());*/
                        event.setEventId(arrayListNewEvent.get
                                (arrayListReplacedPositions.get(i)).getEventId());
                        event.setEventType(arrayListNewEvent.get
                                (arrayListReplacedPositions.get(i)).getEventType());
                        event.setEventDateTime(arrayListNewEvent.get
                                (arrayListReplacedPositions.get(i)).getEventDateTime());
                        events.add(event);
                    }
                    profileDataOperation.setPbEvent(events);
                    break;
                case AppConstants.ADDRESS:
                    ArrayList<ProfileDataOperationAddress> arrayListNewAddress = gson.fromJson
                            (newObjectJson, new TypeToken<ArrayList<ProfileDataOperationAddress>>
                                    () {
                            }.getType());
                    ArrayList<ProfileDataOperationAddress> addresses = new ArrayList<>();
                    for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                        ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                       /* event.setEventId(((ProfileDataOperationEvent)
                                arrayListEventObject.get(i)).getEventId());*/
                        address.setAddId(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getAddId());
                        address.setAddressType(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getAddressType());
                        address.setCountry(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getCountry());
                        address.setState(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getState());
                        address.setCity(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getCity());
                        address.setStreet(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getStreet());
                        address.setNeighborhood(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getNeighborhood());
                        address.setPostCode(arrayListNewAddress.get
                                (arrayListReplacedPositions.get(i)).getPostCode());
                        addresses.add(address);
                    }
                    profileDataOperation.setPbAddress(addresses);
                    break;
            }
            arrayListTempProfile.add(profileDataOperation);
        }
//                        editProfile(arrayListProfile);
        /*Log.i("onComplete", arrayListProfile.toString());*/

        return arrayListTempProfile;

    }

    private ArrayList<ProfileDataOperation> combineResult(ArrayList<ArrayList<ProfileDataOperation>>
                                                                  arrayList) {
        ArrayList<ProfileDataOperation> arrayListMergedProfile = new ArrayList<>();
        ProfileDataOperation profileDataInsert = new ProfileDataOperation();
        profileDataInsert.setFlag(String.valueOf(getResources().getInteger(R.integer
                .sync_update_insert)));
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.get(i).size(); j++) {
                if (Integer.parseInt(arrayList.get(i).get(j).getFlag()) == getResources()
                        .getInteger(R.integer.sync_update_insert)) {
                    switch (i) {
                        case 0:
                            profileDataInsert.setPbPhoneNumber(arrayList.get(i).get(j)
                                    .getPbPhoneNumber());
                            break;

                        case 1:
                            profileDataInsert.setPbEmailId(arrayList.get(i).get(j).getPbEmailId());
                            break;

                        case 2:
                            profileDataInsert.setPbWebAddress(arrayList.get(i).get(j)
                                    .getPbWebAddress());
                            break;

                        case 3:
                            profileDataInsert.setPbEvent(arrayList.get(i).get(j).getPbEvent());
                            break;

                        case 4:
                            profileDataInsert.setPbOrganization(arrayList.get(i).get(j)
                                    .getPbOrganization());
                            break;

                        case 5:
                            profileDataInsert.setPbAddress(arrayList.get(i).get(j).getPbAddress());
                            break;
                    }
                }
            }
        }
        arrayListMergedProfile.add(profileDataInsert);

        ProfileDataOperation profileDataUpdate = new ProfileDataOperation();
        profileDataUpdate.setFlag(String.valueOf(getResources().getInteger(R.integer
                .sync_update_update)));
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.get(i).size(); j++) {
                if (Integer.parseInt(arrayList.get(i).get(j).getFlag()) == getResources()
                        .getInteger(R.integer.sync_update_update)) {
                    switch (i) {
                        case 0:
                            profileDataUpdate.setPbPhoneNumber(arrayList.get(i).get(j)
                                    .getPbPhoneNumber());
                            break;
                        case 1:
                            profileDataUpdate.setPbEmailId(arrayList.get(i).get(j).getPbEmailId());
                            break;

                        case 2:
                            profileDataUpdate.setPbWebAddress(arrayList.get(i).get(j)
                                    .getPbWebAddress());
                            break;

                        case 3:
                            profileDataUpdate.setPbEvent(arrayList.get(i).get(j).getPbEvent());
                            break;

                        case 4:
                            profileDataUpdate.setPbOrganization(arrayList.get(i).get(j)
                                    .getPbOrganization());
                            break;

                        case 5:
                            profileDataUpdate.setPbAddress(arrayList.get(i).get(j).getPbAddress());
                            break;
                    }
                }
            }
        }
        arrayListMergedProfile.add(profileDataUpdate);

        ProfileDataOperation profileDataDelete = new ProfileDataOperation();
        profileDataDelete.setFlag(String.valueOf(getResources().getInteger(R.integer
                .sync_update_delete)));
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = 0; j < arrayList.get(i).size(); j++) {
                if (Integer.parseInt(arrayList.get(i).get(j).getFlag()) == getResources()
                        .getInteger(R.integer.sync_update_delete)) {
                    switch (i) {
                        case 0:
                            profileDataDelete.setPbPhoneNumber(arrayList.get(i).get(j)
                                    .getPbPhoneNumber());
                            break;
                        case 1:
                            profileDataDelete.setPbEmailId(arrayList.get(i).get(j).getPbEmailId());
                            break;

                        case 2:
                            profileDataDelete.setPbWebAddress(arrayList.get(i).get(j)
                                    .getPbWebAddress());
                            break;

                        case 3:
                            profileDataDelete.setPbEvent(arrayList.get(i).get(j).getPbEvent());
                            break;

                        case 4:
                            profileDataDelete.setPbOrganization(arrayList.get(i).get(j)
                                    .getPbOrganization());
                            break;

                        case 5:
                            profileDataDelete.setPbAddress(arrayList.get(i).get(j).getPbAddress());
                            break;
                    }
                }
            }
        }
        arrayListMergedProfile.add(profileDataDelete);

        ProfileDataOperation profileNameGender = new ProfileDataOperation();
        boolean isUpdated = false;
        profileNameGender.setFlag(String.valueOf(getResources().getInteger(R.integer
                .sync_update_update)));
        if (userProfile != null) {
            if (!StringUtils.equals(userProfile.getPmFirstName(), inputFirstName.getText()
                    .toString())) {
                isUpdated = true;
                profileNameGender.setPbNameFirst(inputFirstName.getText().toString());
            }
            if (!StringUtils.equals(userProfile.getPmLastName(), inputLastName.getText()
                    .toString())) {
                isUpdated = true;
                profileNameGender.setPbNameLast(inputLastName.getText().toString());
            }


            int radioButtonID = radioGroupGender.getCheckedRadioButtonId();
            View radioButton = radioGroupGender.findViewById(radioButtonID);
            int index = radioGroupGender.indexOfChild(radioButton);

            RadioButton r = (RadioButton) radioGroupGender.getChildAt(index);
            String selectedText = r.getText().toString();

            if (!StringUtils.equals(userProfile.getPmGender(), selectedText)) {
                isUpdated = true;
                profileNameGender.setPbGender(selectedText);
            }

            if (selectedBitmap != null) {
                isUpdated = true;
                String imageToBase64 = Utils.convertBitmapToBase64(selectedBitmap);
                profileNameGender.setPbProfilePhoto(imageToBase64);
            }
        }
        if (isUpdated) {
            arrayListMergedProfile.add(profileNameGender);
        }

        return arrayListMergedProfile;

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
//                email.setEmIsPrimary(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
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
               /* organization.setOmOrganizationType(arrayListOrganization.get(i).getOrgType());
                organization.setOmOrganizationTitle(arrayListOrganization.get(i).getOrgName());
                organization.setOmOrganizationDepartment(arrayListOrganization.get(i)
                        .getOrgDepartment());
                organization.setOmJobDescription(arrayListOrganization.get(i)
                        .getOrgJobTitle());
                organization.setOmOfficeLocation(arrayListOrganization.get(i)
                        .getOrgOfficeLocation());*/
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
//                imAccount.setImImType(arrayListImAccount.get(j).getIMAccountType());
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(arrayListImAccount.get(j).getIMAccountPublic());
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
            ArrayList<ProfileDataOperationEvent> arrayListEvent = profileDetail.getPbEvent();
            ArrayList<Event> eventList = new ArrayList<>();
            for (int j = 0; j < arrayListEvent.size(); j++) {
                Event event = new Event();
                event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                event.setEvmEventType(arrayListEvent.get(j).getEventType());
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
