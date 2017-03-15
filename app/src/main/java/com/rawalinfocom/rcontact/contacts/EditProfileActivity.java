package com.rawalinfocom.rcontact.contacts;

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
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.gson.Gson;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
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
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

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

    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;

    ArrayList<Object> arrayListPhoneNumberObject;
    ArrayList<Object> arrayListEmailObject;
    ArrayList<Object> arrayListWebsiteObject;
    ArrayList<Object> arrayListSocialContactObject;
    ArrayList<Object> arrayListEventObject;
    ArrayList<Object> arrayListOrganizationObject;

    Bitmap selectedBitmap = null;
    private File mFileTemp;
    private Uri fileUri;

//    ProfileDataOperation userOldProfile;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
//        userOldProfile = new ProfileDataOperation();
        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_left:

                ArrayList<ProfileDataOperationEmail> arrayListNewValue = new ArrayList<>();
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

                    arrayListNewValue.add(email);

                }

                Gson gson = new Gson();
                String oldObjectJson = gson.toJson(arrayListEmailObject);
                String newObjectJson = gson.toJson(arrayListNewValue);

                ObjectMapper mapper = new ObjectMapper();

                ArrayList<Integer> arrayListAddedPositions = new ArrayList<>();
                ArrayList<Integer> arrayListRemovedPositions = new ArrayList<>();
                ArrayList<Integer> arrayListReplacedPositions = new ArrayList<>();

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
                                arrayListAddedPositions.add(Integer.valueOf(StringUtils.substring
                                        (jsonObject.optString("path"), 1)));
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

                        ArrayList<ProfileDataOperation> arrayListProfile = new ArrayList<>();

                        if (arrayListAddedPositions.size() > 0) {
                            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                            profileDataOperation.setFlag(String.valueOf(getResources().getInteger
                                    (R.integer.sync_update_insert)));
                            ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                            for (int i = 0; i < arrayListAddedPositions.size(); i++) {
                                ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                                email.setEmType(arrayListNewValue.get(arrayListAddedPositions.get
                                        (i)).getEmType());
                                email.setEmEmailId(arrayListNewValue.get(arrayListAddedPositions
                                        .get(i)).getEmEmailId());
                                emails.add(email);
                            }
                            profileDataOperation.setPbEmailId(emails);
                            arrayListProfile.add(profileDataOperation);
                        }
                        if (arrayListRemovedPositions.size() > 0) {
                            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                            profileDataOperation.setFlag(String.valueOf(getResources().getInteger
                                    (R.integer.sync_update_delete)));
                            ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                            for (int i = 0; i < arrayListRemovedPositions.size(); i++) {
                                ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                                email.setEmId(((ProfileDataOperationEmail) arrayListEmailObject
                                        .get(i)).getEmId());
                                emails.add(email);
                            }
                            profileDataOperation.setPbEmailId(emails);
                            arrayListProfile.add(profileDataOperation);
                        }
                        if (arrayListReplacedPositions.size() > 0) {
                            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                            profileDataOperation.setFlag(String.valueOf(getResources().getInteger
                                    (R.integer.sync_update_update)));
                            ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                            for (int i = 0; i < arrayListReplacedPositions.size(); i++) {
                                ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                                email.setEmId(((ProfileDataOperationEmail) arrayListEmailObject
                                        .get(i)).getEmId());
                                email.setEmType(arrayListNewValue.get(arrayListReplacedPositions.get
                                        (i)).getEmType());
                                email.setEmEmailId(arrayListNewValue.get(arrayListReplacedPositions
                                        .get(i)).getEmEmailId());
                                emails.add(email);
                            }
                            profileDataOperation.setPbEmailId(emails);
                            arrayListProfile.add(profileDataOperation);
                        }
//                        editProfile(arrayListProfile);
                        Log.i("onComplete", arrayListProfile.toString());
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("onComplete", "Exception");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                Log.i("onComplete", oldObjectJson);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            selectedBitmap = bitmap;
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
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();

                // Bitmap bitmap =
                // BitmapFactory.decodeFile(mFileTemp.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                selectedBitmap = bitmap;
//                imageProfile.setImageBitmap(bitmap);

                Glide.with(this)
                        .load(mFileTemp)
                        .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                        .into(imageProfile);

            } catch (Exception e) {
                Log.e("TAG", "Error while creating temp file", e);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    showChooseImageIntent();

                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
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
                addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, null, -1);
                break;
            //</editor-fold>

            //<editor-fold desc="image_add_email">
            case R.id.image_add_email:
                addView(AppConstants.EMAIL, linearEmailDetails, null, -1);
                break;
            //</editor-fold>

            //<editor-fold desc="image_add_website">
            case R.id.image_add_website:
                addView(AppConstants.WEBSITE, linearWebsiteDetails, null, -1);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_social_contact">
            case R.id.image_add_social_contact:
                addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails, null, -1);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_address">
            case R.id.image_add_address:
                addAddressView(null);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_event">
            case R.id.image_add_event:
                addView(AppConstants.EVENT, linearEventDetails, null, -1);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_organization">
            case R.id.image_add_organization:
                addOrganizationView(null);
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

        UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                .parseInt(getUserPmId()));
        if (userProfile != null) {
            inputFirstName.setText(userProfile.getPmFirstName());
            inputLastName.setText(userProfile.getPmLastName());

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
            imAccount.setIMAccountType(arrayListImAccount.get(i).getImImType());
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

        ArrayList<Event> arrayListEvent = tableEventMaster.getEventssFromPmId(Integer.parseInt
                (getUserPmId()));
        arrayListEventObject = new ArrayList<>();
        for (int i = 0; i < arrayListEvent.size(); i++) {
            ProfileDataOperationEvent event = new ProfileDataOperationEvent();
            event.setEventDate(arrayListEvent.get(i).getEvmStartDate());
            event.setEventType(arrayListEvent.get(i).getEvmEventType());
            event.setEventId(arrayListEvent.get(i).getEvmRecordIndexId());
            arrayListEventObject.add(event);
        }

        if (arrayListEventObject.size() > 0) {
            for (int i = 0; i < arrayListEventObject.size(); i++) {
                addView(AppConstants.EVENT, linearEventDetails, arrayListEventObject.get(i),
                        i);
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
            organization.setOrgJobTitle(arrayListOrganization.get(i).getOmJobDescription());
            organization.setOrgId(arrayListOrganization.get(i).getOmRecordIndexId());
            arrayListOrganizationObject.add(organization);
        }

        if (arrayListOrganizationObject.size() > 0) {
            for (int i = 0; i < arrayListOrganizationObject.size(); i++) {
                addOrganizationView(arrayListEventObject.get(i));
            }
        } else {
            addOrganizationView(null);
        }
    }

    private void addressDetails() {
   /*     TableImMaster tableImMaster = new TableImMaster(databaseHandler);

        ArrayList<ImAccount> arrayListImAccount = tableImMaster.getImAccountFromPmId(Integer
                .parseInt(getUserPmId()));
        arrayListSocialContactObject = new ArrayList<>();
        for (int i = 0; i < arrayListImAccount.size(); i++) {
            ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
            imAccount.setIMAccountProtocol(arrayListImAccount.get(i).getImImProtocol());
            imAccount.setIMAccountType(arrayListImAccount.get(i).getImImType());
            imAccount.setIMId(arrayListImAccount.get(i).getImRecordIndexId());
            arrayListSocialContactObject.add(imAccount);
        }

        if (arrayListSocialContactObject.size() > 0) {
            for (int i = 0; i < arrayListSocialContactObject.size(); i++) {
                addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails,
                        arrayListSocialContactObject.get(i), i);
            }
        } else {*/
        addAddressView(null);
//        }

    }

    private void addView(int viewType, final LinearLayout linearLayout, Object detailObject, int
            position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile, null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        Spinner spinnerType = (Spinner) view.findViewById(R.id.spinner_type);
        EditText inputValue = (EditText) view.findViewById(R.id.input_value);
        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputValue.setTypeface(Utils.typefaceRegular(this));

        String[] spinnerArrayId = new String[0];

        switch (viewType) {
            case AppConstants.PHONE_NUMBER:
                inputValue.setHint("Number");
                spinnerArrayId = getResources().getStringArray(R.array.types_phone_number);
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
                    relativeRowEditProfile.setTag(phoneNumber.getPhoneId());
                }
                break;

            case AppConstants.EMAIL:
                inputValue.setHint("Email");
                spinnerArrayId = getResources().getStringArray(R.array.types_email_address);
                inputValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                if (detailObject != null) {
                    ProfileDataOperationEmail email = (ProfileDataOperationEmail) detailObject;
                    inputValue.setText(email.getEmEmailId());
                    relativeRowEditProfile.setTag(email.getEmId());
                }
                break;

            case AppConstants.WEBSITE:
                inputValue.setHint("Website");
                spinnerArrayId = getResources().getStringArray(R.array.types_email_address);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress)
                            detailObject;
                    inputValue.setText(webAddress.getWebAddress());
                }
                break;

            case AppConstants.IM_ACCOUNT:
                inputValue.setHint("Link");
                spinnerArrayId = getResources().getStringArray(R.array.types_social_media);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount)
                            detailObject;
                    inputValue.setText(imAccount.getIMAccountDetails());
                }
                break;

            case AppConstants.EVENT:
                inputValue.setHint("Event");
                inputValue.setFocusable(false);
                spinnerArrayId = getResources().getStringArray(R.array.types_Event);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationEvent event = (ProfileDataOperationEvent) detailObject;
                    inputValue.setText(event.getEventDate());
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

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout
                .list_item_spinner, spinnerArrayId);
        spinnerType.setAdapter(spinnerAdapter);

        textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                relativeRowEditProfile.setVisibility(View.GONE);
                linearLayout.removeView(relativeRowEditProfile);
            }
        });

        linearLayout.addView(view);
    }

    private void addAddressView(Object detailObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_address,
                null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        Spinner spinnerType = (Spinner) view.findViewById(R.id.spinner_type);
        EditText inputCountry = (EditText) view.findViewById(R.id.input_country);
        EditText inputState = (EditText) view.findViewById(R.id.input_state);
        EditText inputCity = (EditText) view.findViewById(R.id.input_city);
        EditText inputStreet = (EditText) view.findViewById(R.id.input_street);
        EditText inputNeighborhood = (EditText) view.findViewById(R.id.input_neighborhood);
        EditText inputPinCode = (EditText) view.findViewById(R.id.input_pin_code);
        EditText inputPoBox = (EditText) view.findViewById(R.id.input_po_box);

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputCountry.setTypeface(Utils.typefaceRegular(this));
        inputState.setTypeface(Utils.typefaceRegular(this));
        inputCity.setTypeface(Utils.typefaceRegular(this));
        inputStreet.setTypeface(Utils.typefaceRegular(this));
        inputNeighborhood.setTypeface(Utils.typefaceRegular(this));
        inputPinCode.setTypeface(Utils.typefaceRegular(this));
        inputPoBox.setTypeface(Utils.typefaceRegular(this));

        inputCountry.setHint("Country");
        inputState.setHint("State");
        inputCity.setHint("City");
        inputStreet.setHint("Street");
        inputNeighborhood.setHint("Neighborhood");
        inputPinCode.setHint("Pincode");
        inputPoBox.setHint("Po. Box No.");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout
                .list_item_spinner, getResources().getStringArray(R.array.types_email_address));
        spinnerType.setAdapter(spinnerAdapter);

        textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeRowEditProfile.setVisibility(View.GONE);
            }
        });

        linearAddressDetails.addView(view);
    }

    private void addOrganizationView(Object detailObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_organization,
                null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        TextView textLabelCheckbox = (TextView) view.findViewById(R.id.text_label_checkbox);
        EditText inputCompanyName = (EditText) view.findViewById(R.id.input_company_name);
        EditText inputDesignationName = (EditText) view.findViewById(R.id.input_designation_name);
        CheckBox checkboxOrganization = (CheckBox) view.findViewById(R.id.checkbox_organization);

        checkboxOrganization.setTag(linearOrganizationDetail.getChildCount());

        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputCompanyName.setTypeface(Utils.typefaceRegular(this));
        inputDesignationName.setTypeface(Utils.typefaceRegular(this));
        textLabelCheckbox.setTypeface(Utils.typefaceLight(this));

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
                relativeRowEditProfile.setVisibility(View.GONE);
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
                String myFormat = "dd'th' MMM, yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
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
                selectImageFromCamera();
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

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void editProfile(ArrayList<ProfileDataOperation> editProfile) {

        WsRequestObject editProfileObject = new WsRequestObject();
        editProfileObject.setProfileEdit(editProfile);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    editProfileObject, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_UPDATE, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_UPDATE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>
}
