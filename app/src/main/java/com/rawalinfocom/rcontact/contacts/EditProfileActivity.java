package com.rawalinfocom.rcontact.contacts;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.common.base.MoreObjects;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity implements WsResponseListener, RippleView
        .OnRippleCompleteListener {

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
    @BindView(R.id.text_male_icon)
    TextView textMaleIcon;
    @BindView(R.id.text_male)
    TextView textMale;
    @BindView(R.id.linear_male)
    LinearLayout linearMale;
    @BindView(R.id.text_female_icon)
    TextView textFemaleIcon;
    @BindView(R.id.text_female)
    TextView textFemale;
    @BindView(R.id.linear_female)
    LinearLayout linearFemale;
    @BindView(R.id.relativeName)
    RelativeLayout relativeName;
    @BindView(R.id.relativeOrganization)
    RelativeLayout relativeOrganization;
    @BindView(R.id.relativePhone)
    RelativeLayout relativePhone;
    @BindView(R.id.relativeEmail)
    RelativeLayout relativeEmail;
    @BindView(R.id.relativeWebsite)
    RelativeLayout relativeWebsite;
    @BindView(R.id.relativeAddress)
    RelativeLayout relativeAddress;
    @BindView(R.id.relativeSocialConnect)
    RelativeLayout relativeSocialConnect;
    @BindView(R.id.relativeEvent)
    RelativeLayout relativeEvent;
    @BindView(R.id.relativeGender)
    RelativeLayout relativeGender;
    @BindView(R.id.linearOrganizationButtons)
    LinearLayout linearOrganizationButtons;
    @BindView(R.id.linearPhoneButtons)
    LinearLayout linearPhoneButtons;
    @BindView(R.id.linearEmailButtons)
    LinearLayout linearEmailButtons;
    @BindView(R.id.linearWebsiteButtons)
    LinearLayout linearWebsiteButtons;
    @BindView(R.id.linearAddressButtons)
    LinearLayout linearAddressButtons;
    @BindView(R.id.linearSocialButtons)
    LinearLayout linearSocialButtons;
    @BindView(R.id.linearEventButtons)
    LinearLayout linearEventButtons;
    @BindView(R.id.linearGenderButtons)
    LinearLayout linearGenderButtons;
    @BindView(R.id.relativeNameDetails)
    RelativeLayout relativeNameDetails;
    @BindView(R.id.linearNameBottom)
    LinearLayout linearNameBottom;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;

    EditText inputCountry;
    EditText inputState;
    EditText inputCity;
    EditText inputStreet;
    EditText inputNeighborhood;
    EditText inputPinCode;
    TextView textImageMapMarker;

    ArrayAdapter<String> spinnerPhoneAdapter, spinnerEmailAdapter, spinnerAddressAdapter,
            spinnerWebsiteAdapter,
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
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dataPicker;
    EditText editTextEvent;
    //    EditText inputValue;
    EditText inputCompanyName, inputDesignationName;

    boolean isBirthday = false;
    boolean isExpanded = false;
    boolean isEvent = false;
    boolean isMale = false, isFemale = false;
    boolean isUpdated = false;

    UserProfile userProfile;
    MaterialDialog backConfirmationDialog;

    ColorStateList defaultMarkerColor;
    boolean isAddressModified = false;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            selectedBitmap = BitmapFactory.decodeFile(fileUri.getPath());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //4, 8, etc. the more value, the worst quality of image
            try {
                selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream
                        (fileUri), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Glide.with(this)
                    .load(fileUri)
                    .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                    .override(512, 512)
                    .into(imageProfile);

            if (selectedBitmap != null) {
                String bitmapString = Utils.convertBitmapToBase64(selectedBitmap, fileUri.getPath
                        ());
                ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                profileDataOperation.setPbProfilePhoto(bitmapString);
                editProfile(profileDataOperation, AppConstants.PROFILE_IMAGE);
            }


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
                        .override(512, 512)
                        .into(imageProfile);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; //4, 8, etc. the more value, the worst quality of image
                try {
                    selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream
                            (selectedImage), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (selectedBitmap != null) {
                    String bitmapString = Utils.convertBitmapToBase64(selectedBitmap, picturePath);
                    ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                    profileDataOperation.setPbProfilePhoto(bitmapString);
                    editProfile(profileDataOperation, AppConstants.PROFILE_IMAGE);
                }

            } catch (Exception e) {
                Log.e("TAG", "Error while creating temp file", e);
            }

        } else if (requestCode == AppConstants.REQUEST_CODE_MAP_LOCATION_SELECTION) {
            if (data != null) {
//                String locationString = data.getStringExtra(AppConstants.EXTRA_OBJECT_LOCATION);
  /*              if (data.hasExtra(AppConstants.EXTRA_OBJECT_ADDRESS_PLACE)) {
                    Place place = (Place) data
                            .getSerializableExtra(AppConstants.EXTRA_OBJECT_ADDRESS_PLACE);
                    Toast.makeText(this, place.getName() + ", " + place
                            .getAddress(), Toast.LENGTH_SHORT).show();
                } else if (data.hasExtra(AppConstants.EXTRA_OBJECT_ADDRESS)) {*/
                ReverseGeocodingAddress objAddress = (ReverseGeocodingAddress) data
                        .getSerializableExtra(AppConstants.EXTRA_OBJECT_ADDRESS);
                View linearView = linearAddressDetails.getChildAt(clickedPosition);
                TextView textLatitude = (TextView) linearView.findViewById(R.id.input_latitude);
                TextView textLongitude = (TextView) linearView.findViewById(R.id
                        .input_longitude);
                TextView textImageMapMarker = (TextView) linearView.findViewById(R.id
                        .text_image_map_marker);
                TextView textGoogleAddress = (TextView) linearView.findViewById(R.id
                        .input_google_address);
                TextView inputIsAddressModified = (TextView) linearView.findViewById(R.id
                        .input_is_address_modified);
                textLatitude.setText(objAddress.getLatitude());
                textLongitude.setText(objAddress.getLongitude());
                textGoogleAddress.setText(objAddress.getAddress());
                textImageMapMarker.setTextColor(defaultMarkerColor);
                isAddressModified = false;
//                inputIsAddressModified.setText("false");
//                }

            }
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_PROFILE_UPDATE">
            if (serviceType.contains(WsConstants.REQ_PROFILE_UPDATE)) {
                WsResponseObject editProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (editProfileResponse != null && StringUtils.equalsIgnoreCase
                        (editProfileResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.hideProgressDialog();

                    ProfileDataOperation profileDetail = editProfileResponse.getProfileDetail();
                    Utils.setObjectPreference(EditProfileActivity.this, AppConstants
                            .PREF_REGS_USER_OBJECT, profileDetail);

                    Utils.setStringPreference(this, AppConstants.PREF_USER_NAME, profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_NUMBER, profileDetail.getVerifiedMobileNumber());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, profileDetail.getTotalProfileRateUser());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, profileDetail.getProfileRating());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_PHOTO, profileDetail.getPbProfilePhoto());

                    storeProfileDataToDb(profileDetail);

                    String[] serviceTypes = StringUtils.split(serviceType, ":");
                    int type = Integer.parseInt(serviceTypes[1]);
                    switch (type) {
                        case AppConstants.NAME:
                            profileDetails(true, false, false);
                            break;
                        case AppConstants.PHONE_NUMBER:
                            linearPhoneDetails.removeAllViews();
                            phoneNumberDetails();
                            break;
                        case AppConstants.EMAIL:
                            linearEmailDetails.removeAllViews();
                            emailDetails();
                            break;
                        case AppConstants.IM_ACCOUNT:
                            linearSocialContactDetails.removeAllViews();
                            socialContactDetails();
                            break;
                        case AppConstants.WEBSITE:
                            linearWebsiteDetails.removeAllViews();
                            websiteDetails();
                            break;
                        case AppConstants.ORGANIZATION:
                            linearOrganizationDetails.removeAllViews();
                            organizationDetails();
                            break;
                        case AppConstants.EVENT:
                            linearEventDetails.removeAllViews();
                            eventDetails();
                            break;
                        case AppConstants.ADDRESS:
                            linearAddressDetails.removeAllViews();
                            addressDetails();
                            break;
                        case AppConstants.GENDER:
                            profileDetails(false, true, false);
                            break;
                    }

                    Utils.showSuccessSnackBar(this, relativeRootEditProfile, "Profile Updated " +
                            "Successfully!");
                    isUpdated = false;

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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showChooseImageIntent();
            }
        }
        if (isCameraFromSettings) {
            isCameraFromSettings = false;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
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
            .button_event_add_field, R.id.linear_male, R.id.linear_female, R.id.image_profile})
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
                showChooseImageIntent();
                break;
            //</editor-fold>

            //<editor-fold desc="linear_male">
            case R.id.linear_male:
                if (isFemale) {
                    isUpdated = true;
                    selectGenderMale();
                }
                break;
            //</editor-fold>

            // <editor-fold desc="linear_female">
            case R.id.linear_female:
                if (isMale) {
                    isUpdated = true;
                    selectGenderFemale();
                }
                break;
            //</editor-fold>

        }
    }

    @OnClick({R.id.button_name_update, R.id.button_phone_update, R.id.button_email_update, R.id
            .button_website_update, R.id.button_social_contact_update, R.id
            .button_organization_update, R.id.button_gender_update, R.id.button_event_update, R
            .id.button_address_update})
    public void onUpdateClick(View view) {

        ProfileDataOperation profileDataOperation = new ProfileDataOperation();
        boolean isValid;

        switch (view.getId()) {

            //<editor-fold desc="button_name_update">
            case R.id.button_name_update:
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                if (StringUtils.length(firstName) > 0 && StringUtils.length(lastName) > 0) {
                    profileDataOperation.setPbNameFirst(firstName);
                    profileDataOperation.setPbNameLast(lastName);
                    editProfile(profileDataOperation, AppConstants.NAME);
                } else {
                    if (StringUtils.length(firstName) <= 0) {
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, "Please add First " +
                                "name!");
                    } else {
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, "Please add Last " +
                                "name!");
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_gender_update">
            case R.id.button_gender_update:
                if (isMale) {
                    profileDataOperation.setPbGender("Male");
                    editProfile(profileDataOperation, AppConstants.GENDER);
                } else if (isFemale) {
                    profileDataOperation.setPbGender("Female");
                    editProfile(profileDataOperation, AppConstants.GENDER);
                } else {
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, "Select any gender!");
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_phone_update">
            case R.id.button_phone_update:
                ArrayList<ProfileDataOperationPhoneNumber> arrayListNewPhone = new ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearPhoneDetails.getChildCount(); i++) {
                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();
                    View linearPhone = linearPhoneDetails.getChildAt(i);
                    EditText emailId = (EditText) linearPhone.findViewById(R.id.input_value);
                    Spinner emailType = (Spinner) linearPhone.findViewById(R.id.spinner_type);
                    TextView textIsPublic = (TextView) linearPhone.findViewById(R.id
                            .text_is_public);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearPhone
                            .findViewById(R.id.relative_row_edit_profile);
                    phoneNumber.setPhoneNumber(emailId.getText().toString());
                    phoneNumber.setPhoneType((String) emailType.getSelectedItem());
                    phoneNumber.setPhoneId((String) relativeRowEditProfile.getTag());
                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        phoneNumber.setPhonePublic(Integer.parseInt(textIsPublic.getText()
                                .toString()));
                    } else {
                        phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }

                    if (StringUtils.length(phoneNumber.getPhoneNumber()) > 0) {
                        arrayListNewPhone.add(phoneNumber);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Number is required!");
                        }
                    }
                }
                if (isValid) {
                    if (arrayListNewPhone.size() > 0) {
                        profileDataOperation.setPbPhoneNumber(arrayListNewPhone);
                        editProfile(profileDataOperation, AppConstants.PHONE_NUMBER);
                    } else {
                        if (arrayListPhoneNumberObject.size() > 0) {
                            profileDataOperation.setPbPhoneNumber(arrayListNewPhone);
                            editProfile(profileDataOperation, AppConstants.PHONE_NUMBER);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing to " +
                                    "Update");
                        }
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_email_update">
            case R.id.button_email_update:
                ArrayList<ProfileDataOperationEmail> arrayListNewEmail = new ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearEmailDetails.getChildCount(); i++) {
                    ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                    View linearEmail = linearEmailDetails.getChildAt(i);
                    EditText emailId = (EditText) linearEmail.findViewById(R.id.input_value);
                    Spinner emailType = (Spinner) linearEmail.findViewById(R.id.spinner_type);
                    TextView textIsPublic = (TextView) linearEmail.findViewById(R.id
                            .text_is_public);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearEmail
                            .findViewById(R.id.relative_row_edit_profile);
                    email.setEmEmailId(StringUtils.trim(emailId.getText().toString()));
                    email.setEmType((String) emailType.getSelectedItem());
                    email.setEmId((String) relativeRowEditProfile.getTag());
                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        email.setEmPublic(Integer.parseInt(textIsPublic.getText().toString()));
                    } else {
                        email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }

                    if (StringUtils.length(email.getEmEmailId()) > 0) {
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getEmEmailId())
                                .matches()) {
                            arrayListNewEmail.add(email);
                        } else {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Invalid EmailId!");
                        }
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Email is required!");
                        }
                    }

                }
                if (isValid) {
                    if (arrayListNewEmail.size() > 0) {
                        profileDataOperation.setPbEmailId(arrayListNewEmail);
                        editProfile(profileDataOperation, AppConstants.EMAIL);
                    } else {
                        if (arrayListEmailObject.size() > 0) {
                            profileDataOperation.setPbEmailId(arrayListNewEmail);
                            editProfile(profileDataOperation, AppConstants.EMAIL);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing to " +
                                    "Update");
                        }
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_social_contact_update">
            case R.id.button_social_contact_update:
                ArrayList<ProfileDataOperationImAccount> arrayListNewImAccount = new ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearSocialContactDetails.getChildCount(); i++) {
                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                    View linearSocialContact = linearSocialContactDetails.getChildAt(i);
                    EditText imAccountName = (EditText) linearSocialContact.findViewById(R.id
                            .input_value);
                    Spinner imAccountProtocol = (Spinner) linearSocialContact.findViewById(R.id
                            .spinner_type);
                    TextView textIsPublic = (TextView) linearSocialContact.findViewById(R.id
                            .text_is_public);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearSocialContact
                            .findViewById(R.id.relative_row_edit_profile);
                    imAccount.setIMAccountDetails(imAccountName.getText().toString());
                    imAccount.setIMAccountProtocol((String) imAccountProtocol.getSelectedItem());
                    imAccount.setIMId((String) relativeRowEditProfile.getTag());

                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        imAccount.setIMAccountPublic(Integer.parseInt(textIsPublic.getText()
                                .toString()));
                    } else {
                        imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }

                    if (StringUtils.length(imAccount.getIMAccountDetails()) > 0) {
                        arrayListNewImAccount.add(imAccount);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Account is required!");
                        }
                    }

                }
                if (isValid) {
                    if (arrayListNewImAccount.size() > 0) {
                        profileDataOperation.setPbIMAccounts(arrayListNewImAccount);
                        editProfile(profileDataOperation, AppConstants.IM_ACCOUNT);
                    } else {
                        if (arrayListSocialContactObject.size() > 0) {
                            profileDataOperation.setPbIMAccounts(arrayListNewImAccount);
                            editProfile(profileDataOperation, AppConstants.IM_ACCOUNT);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing to " +
                                    "Update");
                        }
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_website_update">
            case R.id.button_website_update:
                ArrayList<ProfileDataOperationWebAddress> arrayListNewWebAddress = new
                        ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearWebsiteDetails.getChildCount(); i++) {
                    ProfileDataOperationWebAddress webAddress = new
                            ProfileDataOperationWebAddress();
                    View linearWebsite = linearWebsiteDetails.getChildAt(i);
                    EditText website = (EditText) linearWebsite.findViewById(R.id.input_value);
                    Spinner websiteType = (Spinner) linearWebsite.findViewById(R.id.spinner_type);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearWebsite
                            .findViewById(R.id.relative_row_edit_profile);
                    webAddress.setWebAddress(website.getText().toString());
                    webAddress.setWebType((String) websiteType.getSelectedItem());
                    webAddress.setWebId((String) relativeRowEditProfile.getTag());

                    webAddress.setWebPublic(IntegerConstants.PRIVACY_EVERYONE);

                    if (StringUtils.length(webAddress.getWebAddress()) > 0) {
                        arrayListNewWebAddress.add(webAddress);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Web address is required!");
                        }
                    }

                }
                if (isValid) {
                    if (arrayListNewWebAddress.size() > 0) {
                        profileDataOperation.setPbWebAddress(arrayListNewWebAddress);
                        editProfile(profileDataOperation, AppConstants.WEBSITE);
                    } else {
                        if (arrayListWebsiteObject.size() > 0) {
                            profileDataOperation.setPbWebAddress(arrayListNewWebAddress);
                            editProfile(profileDataOperation, AppConstants.WEBSITE);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing to " +
                                    "Update");
                        }
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_organization_update">
            case R.id.button_organization_update:
                ArrayList<ProfileDataOperationOrganization> arrayListNewOrganization = new
                        ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();
                    View linearOrganization = linearOrganizationDetails.getChildAt(i);
                    EditText inputCompanyName = (EditText) linearOrganization.findViewById(R.id
                            .input_company_name);
                    EditText inputDesignationName = (EditText) linearOrganization.findViewById(R.id
                            .input_designation_name);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearOrganization
                            .findViewById(R.id.relative_row_edit_profile);
                    CheckBox checkboxOrganization = (CheckBox) linearOrganization.findViewById(R
                            .id.checkbox_organization);
                    organization.setOrgName(inputCompanyName.getText().toString());
                    organization.setOrgJobTitle(inputDesignationName.getText().toString());
                    organization.setOrgId((String) relativeRowEditProfile.getTag());
                    organization.setIsCurrent(checkboxOrganization.isChecked() ? 1 : 0);

                    organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);

                    if (StringUtils.length(organization.getOrgName()) > 0 ||
                            StringUtils.length(organization.getOrgJobTitle()) > 0) {
                        if (StringUtils.length(organization.getOrgName()) > 0) {
                            if (StringUtils.length(organization.getOrgJobTitle()) > 0) {
                                arrayListNewOrganization.add(organization);
                            } else {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                        "Organization designation is required!");
                                isValid = false;
                                break;
                            }
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Organization name is required!");
                            isValid = false;
                            break;
                        }
                    } else {
                        if (i != 0) {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Organization name is required!");
                            isValid = false;
                        }
                        break;
                    }
                }
                boolean isCurrentSelected = false;
                for (int i = 0; i < arrayListNewOrganization.size(); i++) {
                    if ((MoreObjects.firstNonNull(arrayListNewOrganization.get(i).getIsCurrent(),
                            0)) == 1) {
                        isCurrentSelected = true;
                        break;
                    }
                }
                if (isValid) {
                     /*   profileDataOperation.setPbOrganization(arrayListNewOrganization);
                        editProfile(profileDataOperation, AppConstants.ORGANIZATION);*/
                    if (arrayListNewOrganization.size() > 0) {
                        if (!isCurrentSelected) {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Select " +
                                    "current Organization!");
                        } else {
                            profileDataOperation.setPbOrganization(arrayListNewOrganization);
                            editProfile(profileDataOperation, AppConstants.ORGANIZATION);
                        }
                    } else {
                        if (arrayListOrganizationObject.size() > 0) {
                            profileDataOperation.setPbOrganization(arrayListNewOrganization);
                            editProfile(profileDataOperation, AppConstants.ORGANIZATION);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing to " +
                                    "Update");
                        }
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_event_update">
            case R.id.button_event_update:
                ArrayList<ProfileDataOperationEvent> arrayListNewEvent = new ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearEventDetails.getChildCount(); i++) {
                    ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                    View linearEvent = linearEventDetails.getChildAt(i);
                    EditText eventDate = (EditText) linearEvent.findViewById(R.id.input_value);
                    Spinner eventType = (Spinner) linearEvent.findViewById(R.id.spinner_type);
                    TextView textIsPublic = (TextView) linearEvent.findViewById(R.id
                            .text_is_public);
                    CheckBox checkboxHideYear = (CheckBox) linearEvent.findViewById(R.id
                            .checkbox_hide_year);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearEvent
                            .findViewById(R.id.relative_row_edit_profile);
                    event.setEventType((String) eventType.getSelectedItem());
                    event.setIsYearHidden(checkboxHideYear.isChecked() ? 1 : 0);
                    event.setEventId((String) relativeRowEditProfile.getTag());
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
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Event date is required!");
                        }
                    }
                }
                ArrayList<String> valueTypes = new ArrayList<>();
                for (int i = 0; i < arrayListNewEvent.size(); i++) {
                    valueTypes.add(StringUtils.lowerCase(arrayListNewEvent.get(i).getEventType()));
                }
                int birthDayCount = Collections.frequency(valueTypes, "birthday");
                int anniversaryCount = Collections.frequency(valueTypes, "anniversary");
                if (birthDayCount > 1 || anniversaryCount > 1) {
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, "You can add Birthday " +
                            "and Anniversary only once!");
                } else {
                    if (isValid) {
                        if (arrayListNewEvent.size() > 0) {
                            profileDataOperation.setPbEvent(arrayListNewEvent);
                            editProfile(profileDataOperation, AppConstants.EVENT);
                        } else {
                            if (arrayListEventObject.size() > 0) {
                                profileDataOperation.setPbEvent(arrayListNewEvent);
                                editProfile(profileDataOperation, AppConstants.EVENT);
                            } else {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing " +
                                        "to " +
                                        "Update");
                            }
                        }
                    }

                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_address_update">
            case R.id.button_address_update:
//                if (!isAddressModified) {
                ArrayList<ProfileDataOperationAddress> arrayListNewAddress = new ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearAddressDetails.getChildCount(); i++) {
                    ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                    View linearAddress = linearAddressDetails.getChildAt(i);
                    Spinner addressType = (Spinner) linearAddress.findViewById(R.id
                            .spinner_type);

                    EditText country = (EditText) linearAddress.findViewById(R.id
                            .input_country);
                    EditText state = (EditText) linearAddress.findViewById(R.id.input_state);
                    EditText city = (EditText) linearAddress.findViewById(R.id.input_city);
                    EditText street = (EditText) linearAddress.findViewById(R.id.input_street);
                    EditText neighborhood = (EditText) linearAddress.findViewById(R.id
                            .input_neighborhood);
                    EditText pinCode = (EditText) linearAddress.findViewById(R.id
                            .input_pin_code);
                    RelativeLayout relativeRowEditProfile = (RelativeLayout) linearAddress
                            .findViewById(R.id.relative_row_edit_profile);
                    TextView textLatitude = (TextView) linearAddress.findViewById(R.id
                            .input_latitude);
                    TextView textLongitude = (TextView) linearAddress.findViewById(R.id
                            .input_longitude);
                    TextView textGoogleAddress = (TextView) linearAddress.findViewById(R.id
                            .input_google_address);
                    TextView textIsPublic = (TextView) linearAddress.findViewById(R.id
                            .text_is_public);

                    String countryName = country.getText().toString();
                    String stateName = state.getText().toString();
                    String cityName = city.getText().toString();
                    String streetName = street.getText().toString();
                    String neighborhoodName = neighborhood.getText().toString();
                    String pinCodeName = pinCode.getText().toString();

                    address.setCountry(countryName);
                    address.setState(stateName);
                    address.setCity(cityName);
                    address.setStreet(streetName);
                    address.setNeighborhood(neighborhoodName);
                    address.setPostCode(pinCodeName);
                    address.setFormattedAddress(Utils.setFormattedAddress(streetName,
                            neighborhoodName, cityName, stateName, countryName, pinCodeName));
                    address.setAddressType((String) addressType.getSelectedItem());
                    address.setGoogleAddress(textGoogleAddress.getText().toString());
                    ArrayList<String> arrayListLatLong = new ArrayList<>();
                    arrayListLatLong.add(textLongitude.getText().toString());
                    arrayListLatLong.add(textLatitude.getText().toString());
                    address.setGoogleLatLong(arrayListLatLong);
                    address.setAddId((String) relativeRowEditProfile.getTag());
                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        address.setAddPublic(Integer.parseInt(textIsPublic.getText().toString
                                ()));
                    } else {
                        address.setAddPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }

                    if (StringUtils.length(address.getCountry()) > 0 || StringUtils.length
                            (address.getState()) > 0 || StringUtils.length(address.getCity())
                            > 0 || StringUtils.length(address.getStreet()) > 0) {
                        if (StringUtils.length(address.getCountry()) > 0) {
                            if (StringUtils.length(address.getState()) > 0) {
                                if (StringUtils.length(address.getCity()) > 0) {
                                    if (StringUtils.length(address.getStreet()) > 0) {
                                        if (StringUtils.length(address.getGoogleLatLong().get(0))
                                                > 0 && StringUtils.length(address
                                                .getGoogleLatLong().get(1)) > 0) {
                                            if (!isAddressModified) {
                                                arrayListNewAddress.add(address);
                                            } else {
                                                Utils.showErrorSnackBar(this,
                                                        relativeRootEditProfile,
                                                        "Address mapping on Map is required!");
                                                textImageMapMarker.setTextColor(ContextCompat
                                                        .getColor(EditProfileActivity
                                                                .this, R.color
                                                                .colorSnackBarNegative));
                                                isValid = false;
                                                break;
                                            }
                                        } else {
                                            Utils.showErrorSnackBar(this,
                                                    relativeRootEditProfile,
                                                    "Address mapping on Map is required!");
                                            textImageMapMarker.setTextColor(ContextCompat
                                                    .getColor(this, R.color
                                                            .colorSnackBarNegative));
                                            isValid = false;
                                            break;
                                        }
                                    } else {
                                        Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                                "Street is required!");
                                        street.requestFocus();
                                        isValid = false;
                                        break;
                                    }
                                } else {
                                    Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                            "City is required!");
                                    city.requestFocus();
                                    isValid = false;
                                    break;
                                }
                            } else {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                        "State is required!");
                                state.requestFocus();
                                isValid = false;
                                break;
                            }
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Country is required!");
                            country.requestFocus();
                            isValid = false;
                            break;
                        }
                    } else {
                        if (i != 0) {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    "Country name is required!");
                            country.requestFocus();
                            isValid = false;
                        }
                        break;
                    }
                }
                if (isValid) {
                    /*if (isAddressModified) {
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, "Address mapping on "
                                + "Map is required!");
                        textImageMapMarker.setTextColor(ContextCompat.getColor(EditProfileActivity
                                .this, R.color.colorSnackBarNegative));
                        break;
                    } else {*/
                    if (arrayListNewAddress.size() > 0) {
                        profileDataOperation.setPbAddress(arrayListNewAddress);
                        editProfile(profileDataOperation, AppConstants.ADDRESS);
                    } else {
                        if (arrayListAddressObject.size() > 0) {
                            profileDataOperation.setPbAddress(arrayListNewAddress);
                            editProfile(profileDataOperation, AppConstants.ADDRESS);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, "Nothing " +
                                    "to Update");
                        }
                    }
//                    }
                }
              /*  }
                else {
                    textImageMapMarker.setTextColor(ContextCompat.getColor(EditProfileActivity
                            .this, R.color.colorSnackBarNegative));
                }*/
                break;
            //</editor-fold>

        }
    }

    @OnClick({R.id.button_name_cancel, R.id.button_phone_cancel, R.id.button_email_cancel, R.id
            .button_website_cancel, R.id.button_social_contact_cancel, R.id
            .button_organization_cancel, R.id.button_gender_cancel, R.id.button_event_cancel, R
            .id.button_address_cancel})
    public void onCancelClick(View view) {

        switch (view.getId()) {

            //<editor-fold desc="button_name_cancel">
            case R.id.button_name_cancel:
                profileDetails(true, false, false);
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_gender_cancel">
            case R.id.button_gender_cancel:
                profileDetails(false, true, false);
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_phone_cancel">
            case R.id.button_phone_cancel:
                linearPhoneDetails.removeAllViews();
                phoneNumberDetails();
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_email_cancel">
            case R.id.button_email_cancel:
                linearEmailDetails.removeAllViews();
                emailDetails();
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_social_contact_cancel">
            case R.id.button_social_contact_cancel:
                linearSocialContactDetails.removeAllViews();
                socialContactDetails();
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_website_cancel">
            case R.id.button_website_cancel:
                linearWebsiteDetails.removeAllViews();
                websiteDetails();
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_organization_cancel">
            case R.id.button_organization_cancel:
                linearOrganizationDetails.removeAllViews();
                organizationDetails();
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_event_cancel">
            case R.id.button_event_cancel:
                linearEventDetails.removeAllViews();
                eventDetails();
                isUpdated = false;
                break;
            //</editor-fold>

            // <editor-fold desc="button_address_cancel">
            case R.id.button_address_cancel:
                linearAddressDetails.removeAllViews();
                addressDetails();
                isUpdated = false;
                break;
            //</editor-fold>

        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
        /*        if (isUpdated) {
                    showBackConfirmationDialog();
                } else {
                    onBackPressed();
                }*/
                onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (isUpdated) {
            showBackConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {
        initToolbar();
        setFonts();
        profileDetails(true, true, true);
        phoneNumberDetails();
        emailDetails();
        organizationDetails();
        websiteDetails();
        addressDetails();
        socialContactDetails();
        eventDetails();
        genderDetails();
        clickEvents();
        firstExpandableView();
        expandCollapse();

        inputFirstName.addTextChangedListener(valueTextWatcher);
        inputLastName.addTextChangedListener(valueTextWatcher);

    }

    TextWatcher valueTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            isUpdated = true;
        }
    };

    TextWatcher addressTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            textImageMapMarker.setTextColor(defaultMarkerColor);
            isAddressModified = true;
            isUpdated = true;
        }
    };

    private void setFonts() {

        textMale.setTypeface(Utils.typefaceRegular(this));
        textFemale.setTypeface(Utils.typefaceRegular(this));
        inputFirstName.setTypeface(Utils.typefaceRegular(this));
        inputLastName.setTypeface(Utils.typefaceRegular(this));

    }

    private void initToolbar() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(getResources().getString(R.string.myprofile_toolbar_title));
    }

    private void firstExpandableView() {
        imageNameExpand.setImageResource(R.drawable.ic_arrow_up);
        linearName.setVisibility(View.VISIBLE);
        collapseAll();
    }

    private void expandCollapse() {
       /* imageNameExpand.setImageResource(R.drawable.ic_arrow_up);
        linearName.setVisibility(View.VISIBLE);*/

        relativeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    relativeNameDetails.setVisibility(View.GONE);
                    linearNameBottom.setVisibility(View.GONE);

                }else{*/
                imageNameExpand.setImageResource(R.drawable.ic_arrow_up);
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                linearName.setVisibility(View.VISIBLE);
                relativeNameDetails.setVisibility(View.VISIBLE);
                linearNameBottom.setVisibility(View.VISIBLE);
                collapseAll();
//                    isExpanded = true;
//                }

            }
        });

        relativeOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearOrganizationDetails.setVisibility(View.GONE);
                    linearOrganizationButtons.setVisibility(View.GONE);

                }else{*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_up);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.VISIBLE);
                linearOrganizationButtons.setVisibility(View.VISIBLE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded =true;
//                }


            }
        });

        relativePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearPhoneDetails.setVisibility(View.GONE);
                    linearPhoneButtons.setVisibility(View.GONE);

                }else{*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_up);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.VISIBLE);
                linearPhoneButtons.setVisibility(View.VISIBLE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded = true;
//                }

            }
        });

        relativeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearEmailDetails.setVisibility(View.GONE);
                    linearEmailButtons.setVisibility(View.GONE);

                }else {*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_up);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.VISIBLE);
                linearEmailButtons.setVisibility(View.VISIBLE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded = true;
//                }

            }
        });

        relativeWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearWebsiteDetails.setVisibility(View.GONE);
                    linearWebsiteButtons.setVisibility(View.GONE);
                }else{*/
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_up);
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.VISIBLE);
                linearWebsiteButtons.setVisibility(View.VISIBLE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded =  true;
//                }

            }
        });

        relativeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearAddressDetails.setVisibility(View.GONE);
                    linearAddressButtons.setVisibility(View.GONE);
                }else{*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_up);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.VISIBLE);
                linearAddressButtons.setVisibility(View.VISIBLE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded = true;
//                }

            }
        });

        relativeSocialConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearSocialContactDetails.setVisibility(View.GONE);
                    linearSocialButtons.setVisibility(View.GONE);
                }else{*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_up);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.VISIBLE);
                linearSocialButtons.setVisibility(View.VISIBLE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded = true;
//                }

            }
        });

        relativeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearEventDetails.setVisibility(View.GONE);
                    linearEventButtons.setVisibility(View.GONE);
                }else{*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_up);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.VISIBLE);
                linearEventButtons.setVisibility(View.VISIBLE);
                linearGenderDetails.setVisibility(View.GONE);
                linearGenderButtons.setVisibility(View.GONE);
//                    isExpanded = true;
//                }

            }
        });

        relativeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(isExpanded){
                    isExpanded = false;
                    imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);
                    linearGenderDetails.setVisibility(View.GONE);
                    linearGenderButtons.setVisibility(View.GONE);
                }else{*/
                imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageNameExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
                imageGenderExpand.setImageResource(R.drawable.ic_arrow_up);
//                linearName.setVisibility(View.GONE);
                relativeNameDetails.setVisibility(View.GONE);
                linearNameBottom.setVisibility(View.GONE);
                linearOrganizationDetails.setVisibility(View.GONE);
                linearOrganizationButtons.setVisibility(View.GONE);
                linearPhoneDetails.setVisibility(View.GONE);
                linearPhoneButtons.setVisibility(View.GONE);
                linearEmailDetails.setVisibility(View.GONE);
                linearEmailButtons.setVisibility(View.GONE);
                linearWebsiteDetails.setVisibility(View.GONE);
                linearWebsiteButtons.setVisibility(View.GONE);
                linearAddressDetails.setVisibility(View.GONE);
                linearAddressButtons.setVisibility(View.GONE);
                linearSocialContactDetails.setVisibility(View.GONE);
                linearSocialButtons.setVisibility(View.GONE);
                linearEventDetails.setVisibility(View.GONE);
                linearEventButtons.setVisibility(View.GONE);
                linearGenderDetails.setVisibility(View.VISIBLE);
                linearGenderButtons.setVisibility(View.VISIBLE);
//                    isExpanded = true;
//                }

            }

        });

    }

    private void collapseAll() {
//        linearName.setVisibility(View.GONE);
        imageOrganizationExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imagePhoneExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imageEmailExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imageWebsiteExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imageAddressExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imageSocialContactExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imageEventExpand.setImageResource(R.drawable.ic_arrow_bottom);
        imageGenderExpand.setImageResource(R.drawable.ic_arrow_bottom);

        linearOrganizationDetails.setVisibility(View.GONE);
        linearOrganizationButtons.setVisibility(View.GONE);
        linearPhoneDetails.setVisibility(View.GONE);
        linearPhoneButtons.setVisibility(View.GONE);
        linearEmailDetails.setVisibility(View.GONE);
        linearEmailButtons.setVisibility(View.GONE);
        linearWebsiteDetails.setVisibility(View.GONE);
        linearWebsiteButtons.setVisibility(View.GONE);
        linearAddressDetails.setVisibility(View.GONE);
        linearAddressButtons.setVisibility(View.GONE);
        linearSocialContactDetails.setVisibility(View.GONE);
        linearSocialButtons.setVisibility(View.GONE);
        linearEventDetails.setVisibility(View.GONE);
        linearEventButtons.setVisibility(View.GONE);
        linearGenderDetails.setVisibility(View.GONE);
        linearGenderButtons.setVisibility(View.GONE);
    }

    private void clickEvents() {

        myCalendar = Calendar.getInstance();

        dataPicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                updateLabel();
                if (isBirthday) {
                    updateBirthdayEditText(myCalendar);
                } else if (isEvent) {
                    updateEditTextEvent(myCalendar);
                } else {
                    updateAnniversaryEditText(myCalendar);
                }
            }

        };
    }

    private void selectGenderMale() {
        textFemaleIcon.setTextColor(ContextCompat.getColor(this, R.color.darkGray));
        textFemale.setTextColor(ContextCompat.getColor(this, R.color.darkGray));
        textMaleIcon.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        textMale.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        isMale = true;
        isFemale = false;
    }

    private void selectGenderFemale() {
        textFemaleIcon.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        textFemale.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        textMaleIcon.setTextColor(ContextCompat.getColor(this, R.color.darkGray));
        textMale.setTextColor(ContextCompat.getColor(this, R.color.darkGray));
        isMale = false;
        isFemale = true;
    }

    private void updateBirthdayEditText(Calendar myCalendar) {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMMM, yyyy");
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMMM, yyyy");
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMMM, yyyy");
        else
            format = new SimpleDateFormat("d'th' MMMM, yyyy");

        String yourDate = format.format(myCalendar.getTime());

    }

    private void updateAnniversaryEditText(Calendar myCalendar) {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMMM, yyyy");
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMMM, yyyy");
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMMM, yyyy");
        else
            format = new SimpleDateFormat("d'th' MMMM, yyyy");

        String yourDate = format.format(myCalendar.getTime());

    }

    private void updateEventEditText(EditText editText) {
        editTextEvent = editText;
        new DatePickerDialog(EditProfileActivity.this, dataPicker, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//        updateEditTextEvent(myCalendar);
    }

    private void updateEditTextEvent(Calendar myCalendar) {
        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat("d'st' MMMM, yyyy");
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat("d'nd' MMMM, yyyy");
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat("d'rd' MMMM, yyyy");
        else
            format = new SimpleDateFormat("d'th' MMMM, yyyy");

        String yourDate = format.format(myCalendar.getTime());
        if (!TextUtils.isEmpty(yourDate))
            editTextEvent.setText(yourDate);

//        isEvent = false;
    }

    private void genderDetails() {
        textFemaleIcon.setTypeface(Utils.typefaceIcons(this));
        textMaleIcon.setTypeface(Utils.typefaceIcons(this));
        textFemaleIcon.setText(getResources().getString(R.string.im_female));
        textMaleIcon.setText(getResources().getString(R.string.im_gender_male));

    }

    private void profileDetails(boolean setNames, boolean setGender, boolean setProfileImage) {

        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt(getUserPmId()));
        if (userProfile != null) {
            if (setNames) {
                inputFirstName.setText(userProfile.getPmFirstName());
                inputLastName.setText(userProfile.getPmLastName());
            }

            if (setGender) {
                if (StringUtils.endsWithIgnoreCase(userProfile.getPmGender(), "Female")) {
                    selectGenderFemale();
                }
//                else if (StringUtils.endsWithIgnoreCase(userProfile.getPmGender(), "Male")) {
                else {
                    selectGenderMale();
                }
            }

            if (setProfileImage) {
                Glide.with(this)
                        .load(userProfile.getPmProfileImage())
                        .placeholder(R.drawable.home_screen_profile)
                        .error(R.drawable.home_screen_profile)
                        .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                        .override(512, 512)
                        .into(imageProfile);
            }

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
            for (int i = 0; i < linearPhoneDetails.getChildCount(); i++) {
                View linearPhone = linearPhoneDetails.getChildAt(i);
                EditText emailId = (EditText) linearPhone.findViewById(R.id.input_value);
                emailId.addTextChangedListener(valueTextWatcher);
            }
//            inputValue.addTextChangedListener(valueTextWatcher);
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
            for (int i = 0; i < linearEmailDetails.getChildCount(); i++) {
                View linearEmail = linearEmailDetails.getChildAt(i);
                EditText emailId = (EditText) linearEmail.findViewById(R.id.input_value);
                emailId.addTextChangedListener(valueTextWatcher);
            }
//            inputValue.addTextChangedListener(valueTextWatcher);
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
            for (int i = 0; i < linearWebsiteDetails.getChildCount(); i++) {
                View linearWebsite = linearWebsiteDetails.getChildAt(i);
                EditText Website = (EditText) linearWebsite.findViewById(R.id.input_value);
                Website.addTextChangedListener(valueTextWatcher);
            }
//            inputValue.addTextChangedListener(valueTextWatcher);
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
            for (int i = 0; i < linearSocialContactDetails.getChildCount(); i++) {
                View linearSocialContact = linearSocialContactDetails.getChildAt(i);
                EditText socialContact = (EditText) linearSocialContact.findViewById(R.id
                        .input_value);
                socialContact.addTextChangedListener(valueTextWatcher);
            }
//            inputValue.addTextChangedListener(valueTextWatcher);
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
            for (int i = 0; i < linearEventDetails.getChildCount(); i++) {
                View linearEvent = linearEventDetails.getChildAt(i);
                EditText event = (EditText) linearEvent.findViewById(R.id.input_value);
                event.addTextChangedListener(valueTextWatcher);
            }
//            inputValue.addTextChangedListener(valueTextWatcher);
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
            inputCompanyName.addTextChangedListener(valueTextWatcher);
            inputDesignationName.addTextChangedListener(valueTextWatcher);
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
            ArrayList<String> arrayListLatLong = new ArrayList<>();
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLongitude());
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLatLong(arrayListLatLong);
            /*address.setGoogleLatitude(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLongitude(arrayListAddress.get(i).getAmGoogleLongitude());*/
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLongitude());
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLatLong(arrayListLatLong);
            address.setAddId(arrayListAddress.get(i).getAmRecordIndexId());
            address.setAddPublic(Integer.parseInt(arrayListAddress.get(i).getAmAddressPrivacy()));
            arrayListAddressObject.add(address);
        }

        if (arrayListAddressObject.size() > 0) {
            for (int i = 0; i < arrayListAddressObject.size(); i++) {
                addAddressView(arrayListAddressObject.get(i), i);
            }
           /* inputCountry.addTextChangedListener(valueTextWatcher);
            inputState.addTextChangedListener(valueTextWatcher);
            inputCity.addTextChangedListener(valueTextWatcher);
            inputStreet.addTextChangedListener(valueTextWatcher);
            inputNeighborhood.addTextChangedListener(valueTextWatcher);
            inputPinCode.addTextChangedListener(valueTextWatcher);*/
            inputCountry.addTextChangedListener(addressTextWatcher);
            inputState.addTextChangedListener(addressTextWatcher);
            inputCity.addTextChangedListener(addressTextWatcher);
            inputStreet.addTextChangedListener(addressTextWatcher);
            inputNeighborhood.addTextChangedListener(addressTextWatcher);
            inputPinCode.addTextChangedListener(addressTextWatcher);
        } else {
            addAddressView(null, 0);
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
            TextView inputLatitude = (TextView) linearView.findViewById(R.id.input_latitude);
            TextView inputLongitude = (TextView) linearView.findViewById(R.id.input_longitude);
            if (StringUtils.length(StringUtils.trimToEmpty(inputCountry.getText().toString())) <
                    1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputState.getText().toString()))
                            < 1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputCity.getText().toString())) <
                            1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputStreet.getText().toString()))
                            < 1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputLatitude.getText().toString()))
                            < 1 ||
                    StringUtils.length(StringUtils.trimToEmpty(inputLongitude.getText().toString()))
                            < 1) {
                toAdd = false;
                break;
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            isUpdated = true;
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
            isUpdated = true;
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
            isUpdated = true;
            addView(viewType, linearLayout, null, -1);
        }
    }

    private void addView(int viewType, final LinearLayout linearLayout, Object detailObject, int
            position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile, null);
//        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        ImageView imageViewDelete = (ImageView) view.findViewById(R.id.image_delete);
        final Spinner spinnerType = (Spinner) view.findViewById(R.id.spinner_type);
//        final EditText inputValue = (EditText) view.findViewById(R.id.input_value);
        final EditText inputValue = (EditText) view.findViewById(R.id.input_value);
        LinearLayout linerCheckbox = (LinearLayout) view.findViewById(R.id.liner_checkbox);
        final CheckBox checkboxHideYear = (CheckBox) view.findViewById(R.id.checkbox_hide_year);
        TextView textLabelCheckbox = (TextView) view.findViewById(R.id.text_label_checkbox);
        TextView textIsPublic = (TextView) view.findViewById(R.id.text_is_public);
        ImageView imageViewCalender = (ImageView) view.findViewById(R.id.image_calender);
        imageViewCalender.setVisibility(View.GONE);
        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

//        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputValue.setTypeface(Utils.typefaceRegular(this));
        textLabelCheckbox.setTypeface(Utils.typefaceLight(this));

        List<String> typeList;

        switch (viewType) {
            case AppConstants.PHONE_NUMBER:
                linerCheckbox.setVisibility(View.GONE);
                imageViewDelete.setTag(AppConstants.PHONE_NUMBER);
                inputValue.setHint("Number");
                spinnerType.setTag(AppConstants.PHONE_NUMBER);
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_phone_number)));
                spinnerPhoneAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner,
                        typeList);
                spinnerPhoneAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerPhoneAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_PHONE);
                if (detailObject != null) {
                    if (position == 0) {
                        inputValue.setEnabled(false);
                        spinnerType.setVisibility(View.GONE);
//                        textImageCross.setVisibility(View.INVISIBLE);
                        imageViewDelete.setVisibility(View.INVISIBLE);
                    }
                    ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                            detailObject;
                    if (position == 0) {
                        inputValue.setText(phoneNumber.getPhoneNumber());
//                        inputValue.setText(phoneNumber.getPhoneNumber() + " ◊");
                    } else {
                        inputValue.setText(phoneNumber.getPhoneNumber());
                    }
                    textIsPublic.setText(String.valueOf(phoneNumber.getPhonePublic()));
                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(phoneNumber.getPhoneType()))) {
                        spinnerPosition = spinnerPhoneAdapter.getPosition(phoneNumber
                                .getPhoneType());
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
                imageViewDelete.setTag(AppConstants.EMAIL);
                inputValue.setHint("Email");
                spinnerType.setTag(AppConstants.EMAIL);
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_email_address)));
                spinnerEmailAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerType.setAdapter(spinnerEmailAdapter);
                inputValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType
                        .TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
//                textImageCross.setTag(AppConstants.WEBSITE);
                imageViewDelete.setTag(AppConstants.WEBSITE);
                inputValue.setHint("Web Address");
                spinnerType.setTag(AppConstants.WEBSITE);
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_email_address)));
                spinnerWebsiteAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerWebsiteAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerWebsiteAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                        .TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
//                textImageCross.setTag(AppConstants.IM_ACCOUNT);
                imageViewDelete.setTag(AppConstants.IM_ACCOUNT);
                inputValue.setHint("Account Name");
                spinnerType.setTag(AppConstants.IM_ACCOUNT);
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_social_media)));
                spinnerImAccountAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerImAccountAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerImAccountAdapter);
                spinnerType.setSelection(1);
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
//                textImageCross.setTag(AppConstants.EVENT);
                imageViewDelete.setTag(AppConstants.EVENT);
                inputValue.setHint("Choose date");
                inputValue.setFocusable(false);
                spinnerType.setTag(AppConstants.EVENT);
                imageViewCalender.setVisibility(View.VISIBLE);
                typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                        .array.types_Event)));
                spinnerEventAdapter = new ArrayAdapter<>(this, R.layout
                        .list_item_spinner, typeList);
                spinnerEventAdapter.setDropDownViewResource(android.R.layout
                        .simple_spinner_dropdown_item);
                spinnerType.setAdapter(spinnerEventAdapter);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                inputValue.setTextSize((float) 13.5);
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
                /*inputValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker((EditText) v);
                    }
                });*/

                imageViewCalender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isEvent = true;
                        updateEventEditText(inputValue);


                    }
                });
                break;

            default:
                getResources().getStringArray(R.array.types_email_address);
                break;
        }

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isUpdated = true;

                switch ((Integer) v.getTag()) {

                    case AppConstants.PHONE_NUMBER:
                        if (linearPhoneDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearPhoneDetails.getChildCount() == 1) {
//                            inputValue.setText("");
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.EMAIL:
                        if (linearEmailDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearEmailDetails.getChildCount() == 1) {
//                            inputValue.setText("");
                            inputValue.getText().clear();
                            ;
                        }
                        break;

                    case AppConstants.WEBSITE:
                        if (linearWebsiteDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearWebsiteDetails.getChildCount() == 1) {
//                            inputValue.setText("");
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.EVENT:
                        if (linearEventDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearEventDetails.getChildCount() == 1) {
//                            inputValue.setText("");
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.IM_ACCOUNT:
                        if (linearSocialContactDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearSocialContactDetails.getChildCount() == 1) {
//                            inputValue.setText("");
                            inputValue.getText().clear();
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
//        View view = LayoutInflater.from(this).inflate(R.layout
// .list_item_edit_profile_organization,
//                null);
        View view = LayoutInflater.from(this).inflate(R.layout
                .list_item_my_profile_edit_organization, null);
//        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
//        TextView textLabelCheckbox = (TextView) view.findViewById(R.id.text_label_checkbox);
        ImageView deleteOrganization = (ImageView) view.findViewById(R.id.deleteOrganization);
        inputCompanyName = (EditText) view.findViewById(R.id.input_company_name);
        inputDesignationName = (EditText) view.findViewById(R.id.input_designation_name);
        final CheckBox checkboxOrganization = (CheckBox) view.findViewById(R.id
                .checkbox_organization);

        checkboxOrganization.setTag(linearOrganizationDetails.getChildCount());

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        inputCompanyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);
        inputDesignationName.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);

//        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputCompanyName.setTypeface(Utils.typefaceRegular(this));
        inputDesignationName.setTypeface(Utils.typefaceRegular(this));
//        textLabelCheckbox.setTypeface(Utils.typefaceLight(this));

        ProfileDataOperationOrganization organization = (ProfileDataOperationOrganization)
                detailObject;

        if (detailObject != null) {
            relativeRowEditProfile.setTag(organization.getOrgId());
            inputCompanyName.setText(organization.getOrgName());
            inputDesignationName.setText(organization.getOrgJobTitle());
            checkboxOrganization.setChecked(organization.getIsCurrent() == 1);
        } else {
            if (linearOrganizationDetails.getChildCount() == 0) {
                checkboxOrganization.setChecked(true);
            }
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
                        if (checkbox != null) {
                            if (!(checkbox.getTag() == buttonView.getTag())) {
                                checkbox.setChecked(false);
                            }
                        }

                    }

                }
            }
        });

        deleteOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdated = true;
                if (linearOrganizationDetails.getChildCount() > 1) {
                    linearOrganizationDetails.removeView(relativeRowEditProfile);
                } else if (linearOrganizationDetails.getChildCount() == 1) {
                    /*inputCompanyName.setText("");
                    inputDesignationName.setText("");*/
                    inputCompanyName.getText().clear();
                    inputDesignationName.getText().clear();
                    checkboxOrganization.setChecked(true);
                }
            }
        });


        linearOrganizationDetails.addView(view);
        Log.i("addOrganizationView", linearOrganizationDetails.getChildCount() + "");
    }

    private void addAddressView(final Object detailObject, final int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_address,
                null);
//        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        ImageView imageViewDelete = (ImageView) view.findViewById(R.id.image_delete);
        textImageMapMarker = (TextView) view.findViewById(R.id.text_image_map_marker);
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

//        textImageCross.setTypeface(Utils.typefaceIcons(this));
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
        inputCity.setHint("City / Town (Required)");
        inputStreet.setHint("Address Line 1 (Required)");
        inputNeighborhood.setHint("Address Line 2 (Optional)");
        inputPinCode.setHint("Pincode (Required)");
        inputPoBox.setHint("Po. Box No.");

        inputPoBox.setVisibility(View.GONE);
        /*textImageMapMarker.setTextColor(ContextCompat.getColor(this, R.color
                .colorSnackBarNegative));*/

        defaultMarkerColor = textImageMapMarker.getTextColors();

       /* inputCountry.addTextChangedListener(addressTextWatcher);
        inputState.addTextChangedListener(addressTextWatcher);
        inputCity.addTextChangedListener(addressTextWatcher);
        inputStreet.addTextChangedListener(addressTextWatcher);
        inputNeighborhood.addTextChangedListener(addressTextWatcher);
        inputPinCode.addTextChangedListener(addressTextWatcher);*/

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
            textLatitude.setText(address.getGoogleLatLong().get(1));
            textLongitude.setText(address.getGoogleLatLong().get(0));
            textIsPublic.setText(String.valueOf(address.getAddPublic()));
            formattedAddress = address.getFormattedAddress();
            textImageMapMarker.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            int spinnerPosition = 0;
            spinnerAddressAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, new
                    ArrayList<>(Arrays.asList(getResources().getStringArray(R.array
                    .types_email_address))));
            spinnerType.setAdapter(spinnerAddressAdapter);
            if (Arrays.asList(getResources().getStringArray(R.array.types_email_address))
                    .contains(StringUtils.defaultString(address.getAddressType()))) {
                spinnerPosition = spinnerAddressAdapter.getPosition(address.getAddressType());
            } else {
                spinnerAddressAdapter.add(address.getAddressType());
                spinnerAddressAdapter.notifyDataSetChanged();
                spinnerPosition = spinnerAddressAdapter.getPosition(address.getAddressType());
            }
            spinnerType.setSelection(spinnerPosition);
            relativeRowEditProfile.setTag(address.getAddId());
        } else {
            inputCountry.addTextChangedListener(addressTextWatcher);
            inputState.addTextChangedListener(addressTextWatcher);
            inputCity.addTextChangedListener(addressTextWatcher);
            inputStreet.addTextChangedListener(addressTextWatcher);
            inputNeighborhood.addTextChangedListener(addressTextWatcher);
            inputPinCode.addTextChangedListener(addressTextWatcher);
        }


        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdated = true;
                if (linearAddressDetails.getChildCount() > 1) {
                    linearAddressDetails.removeView(relativeRowEditProfile);
                } else if (linearAddressDetails.getChildCount() == 1) {
                    /*inputCountry.setText("");
                    inputState.setText("");
                    inputCity.setText("");
                    inputStreet.setText("");
                    inputNeighborhood.setText("");
                    inputPinCode.setText("");
                    inputPoBox.setText("");*/
                    inputCountry.getText().clear();
                    inputState.getText().clear();
                    inputCity.getText().clear();
                    inputStreet.getText().clear();
                    inputNeighborhood.getText().clear();
                    inputPinCode.getText().clear();
                    inputPoBox.getText().clear();
                }
            }
        });

        textImageMapMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                if (StringUtils.length(countryName) > 0 && StringUtils.length(stateName) > 0 &&
                        StringUtils.length(cityName) > 0 && StringUtils.length(streetName) > 0 &&
                        StringUtils.length(pinCodeName) > 0) {

                    Intent intent = new Intent(EditProfileActivity.this, MapsActivity.class);

                    if (position != -1) {
                  /*  mapLatitude = Double.parseDouble(((ProfileDataOperationAddress)
                            arrayListAddressObject.get(position)).getGoogleLatitude());
                    mapLongitude = Double.parseDouble(((ProfileDataOperationAddress)
                            arrayListAddressObject.get(position)).getGoogleLongitude());
                    intent.putExtra(AppConstants.EXTRA_LATITUDE, mapLatitude);
                    intent.putExtra(AppConstants.EXTRA_LONGITUDE, mapLongitude);*/

                        if (!isAddressModified) {
                            intent.putExtra(AppConstants.EXTRA_LATITUDE, Double.parseDouble
                                    (StringUtils.defaultIfEmpty(textLatitude.getText().toString(),
                                            "0")));
                            intent.putExtra(AppConstants.EXTRA_LONGITUDE, Double.parseDouble
                                    (StringUtils.defaultIfEmpty(textLongitude.getText().toString(),
                                            "0")));
                        }
                        String formattedAddress = Utils.setFormattedAddress(streetName,
                                neighborhoodName, cityName, stateName, countryName, pinCodeName);
                        if (StringUtils.length(formattedAddress) > 0) {
                            intent.putExtra(AppConstants.EXTRA_FORMATTED_ADDRESS, formattedAddress);
                        }
                    }
                   /* if (detailObject != null && position != -1) {
                        intent.putExtra(AppConstants.EXTRA_FORMATTED_ADDRESS, (
                                (ProfileDataOperationAddress) arrayListAddressObject.get(position))
                                .getFormattedAddress());
                    }*/
                    if (position == -1) {
                        clickedPosition = 0;
                    } else {
                        clickedPosition = position;
                    }
                    startActivityForResult(intent, AppConstants
                            .REQUEST_CODE_MAP_LOCATION_SELECTION);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    country.requestFocus();
                    textImageMapMarker.setTextColor(ContextCompat.getColor(EditProfileActivity
                            .this, R.color.colorSnackBarNegative));
                    Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile,
                            "Please add address!");
                }
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
        TextView textRemovePhoto = (TextView) dialog.findViewById(R.id.text_remove_photo);

        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFromContact.setTypeface(Utils.typefaceRegular(this));
        textFromSocialMedia.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText("Upload Via");
        textFromContact.setText("Take Photo");
        textFromSocialMedia.setText("Choose Photo");
        textRemovePhoto.setText("Remove Photo");

        if (userProfile.getPmProfileImage().length() > 0) {
            textRemovePhoto.setVisibility(View.VISIBLE);
        } else {
            textRemovePhoto.setVisibility(View.GONE);
        }

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
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(EditProfileActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstants
                            .MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    selectImageFromGallery();
                }
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

        textRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                Glide.with(EditProfileActivity.this)
                        .load(R.drawable.home_screen_profile)
                        .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
//                        .override(400, 400)
                        .into(imageProfile);

                ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                profileDataOperation.setPbProfilePhoto("");
                editProfile(profileDataOperation, AppConstants.PROFILE_IMAGE);
            }
        });

        dialog.show();
    }

    private void showBackConfirmationDialog() {
        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        backConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        backConfirmationDialog.dismissDialog();
                        finish();
                        break;
                }
            }
        };

        backConfirmationDialog = new MaterialDialog(this, cancelListener);
        backConfirmationDialog.setTitleVisibility(View.GONE);
        backConfirmationDialog.setLeftButtonText("Cancel");
        backConfirmationDialog.setRightButtonText("OK");
        backConfirmationDialog.setDialogBody("Some details are left unsaved. Are you sure you " +
                "want to proceed?");

        backConfirmationDialog.showDialog();
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

    private void storeProfileDataToDb(ProfileDataOperation profileDetail) {

        //<editor-fold desc="Basic Details">
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        UserProfile userProfile = new UserProfile();
        userProfile.setPmRcpId(getUserPmId());
//        userProfile.setPmPrefix(profileDetail.getPbNamePrefix());
        userProfile.setPmFirstName(profileDetail.getPbNameFirst());
//        userProfile.setPmMiddleName(profileDetail.getPbNameMiddle());
        userProfile.setPmLastName(profileDetail.getPbNameLast());
//        userProfile.setPmSuffix(profileDetail.getPbNameSuffix());
//        userProfile.setPmNickName(profileDetail.getPbNickname());
//        userProfile.setPmPhoneticFirstName(profileDetail.getPbPhoneticNameFirst());
//        userProfile.setPmPhoneticMiddleName(profileDetail.getPbPhoneticNameMiddle());
//        userProfile.setPmPhoneticLastName(profileDetail.getPbPhoneticNameLast());
//        userProfile.setPmNotes(profileDetail.getPbNote());
        userProfile.setProfileRating(profileDetail.getProfileRating());
        userProfile.setTotalProfileRateUser(profileDetail.getTotalProfileRateUser());
        userProfile.setPmIsFavourite(profileDetail.getIsFavourite());
        userProfile.setPmNosqlMasterId(profileDetail.getNoSqlMasterId());
//        userProfile.setPmJoiningDate(profileDetail.getJoiningDate());
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
                mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i).getPhoneNumber());
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
                address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatLong().get(1));
                address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLatLong().get(0));
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

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void editProfile(ProfileDataOperation editProfile, int type) {

        WsRequestObject editProfileObject = new WsRequestObject();
        editProfileObject.setProfileEdit(editProfile);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    editProfileObject, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_UPDATE + ":" + type, getResources().getString(R.string
                    .msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants
                    .REQ_PROFILE_UPDATE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>

}
