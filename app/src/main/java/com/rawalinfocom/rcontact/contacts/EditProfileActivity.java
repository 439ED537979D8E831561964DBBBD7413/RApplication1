package com.rawalinfocom.rcontact.contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.EnterPriseOrganizationListActivity;
import com.rawalinfocom.rcontact.LinkedinLoginActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.RSpinnerAdapter;
import com.rawalinfocom.rcontact.adapters.SocialConnectListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableCountryMaster;
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
import com.rawalinfocom.rcontact.helper.imgcrop.CropImage;
import com.rawalinfocom.rcontact.helper.imgcrop.CropImageView;
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
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;
import com.rawalinfocom.rcontact.model.State;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity implements WsResponseListener, RippleView
        .OnRippleCompleteListener, GoogleApiClient.OnConnectionFailedListener {

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

    ArrayAdapter<String> spinnerPhoneAdapter, spinnerEmailAdapter, spinnerAddressAdapter,
            spinnerCountryAdapter, spinnerWebsiteAdapter, spinnerEventAdapter;

    ArrayList<ProfileDataOperation> arrayListProfile;
    boolean isStorageFromSettings = false, isCameraFromSettings = false;

    Bitmap selectedBitmap = null;

    private File mFileTemp;
    private Uri fileUri;

    int clickedPosition = -1;
    String formattedAddress;

    private ArrayList<Object> arrayListPhoneNumberObject;
    private ArrayList<Object> arrayListEmailObject;
    private ArrayList<ProfileDataOperationEmail> arrayListSocialEmail;
    private ArrayList<Object> arrayListWebsiteObject;
    private ArrayList<Object> arrayListSocialContactObject;
    private ArrayList<Object> arrayListAddressObject;
    private ArrayList<Object> arrayListEventObject;
    private ArrayList<Object> arrayListOrganizationObject;
    DatePickerDialog.OnDateSetListener dataPicker;
    EditText editTextEvent;
    //    EditText inputValue;
//    EditText inputCompanyName, inputDesignationName;

    boolean isBirthday = false;
    boolean isEvent = false;
    boolean isOrganization = false;
    boolean isMale = false, isFemale = false;
    boolean isUpdated = false;
    boolean isAdd = true;

    UserProfile userProfile;
    MaterialDialog backConfirmationDialog;

    ColorStateList defaultMarkerColor;

    // Social Login
    private final int RC_SIGN_IN = 7;
    private final int RC_LINKEDIN_SIGN_IN = 8;

    // Google API Client
    private GoogleApiClient googleApiClient;

    private static final int FACEBOOK_LOGIN_PERMISSION = 21;
    private static final int GOOGLE_LOGIN_PERMISSION = 22;
    private static final int LINKEDIN_LOGIN_PERMISSION = 23;
    // Facebook Callback Manager
    CallbackManager callbackManager;
    // End

    private ArrayList<String> socialTypeList;
    private SocialConnectListAdapter socialConnectListAdapter;
    private String socialId = "";
    int organisationPosition;
    private TextView organizationDateView;

    private String[] requiredPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest
            .permission.WRITE_EXTERNAL_STORAGE};

    //<editor-fold desc="Override Methods">
    /*String imageurl = "https://static.pexels
    .com/photos/87452/flowers-background-butterflies-beautiful-87452.jpeg";
    @BindView(R.id.btn_share)
    Button btnShare;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        arrayListProfile = new ArrayList<>();

        FacebookSdk.sdkInitialize(getApplicationContext());

        // Google+ Registration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi
                (Auth.GOOGLE_SIGN_IN_API, gso).build();

        // REALM
      /*  realm = Realm.getDefaultInstance();
        try {
            long countryCount = realm.where(DbCountry.class).count();
            if (countryCount <= 0) {
                getCountryList();
            }
        } finally {
            realm.close();
        }*/

//        SocialEmailList = new ArrayList<>();
        arrayListSocialEmail = new ArrayList<>();

        TableCountryMaster tableCountryMaster = new TableCountryMaster(databaseHandler);
        if (tableCountryMaster.getCountryCount() <= 0 || databaseHandler.getWritableDatabase()
                .getVersion() < 5) {
            getCountryList();
            init(false);
        } else {
            init(true);
        }
    }


    /*private void onShareClick() {

        PackageManager pm = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text*//*");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook
            .katana")
                    || packageName.contains("com.linkedin.android")) {
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                if (packageName.contains("com.twitter.android")) {
                    intent.putExtra(Intent.EXTRA_TEXT, imageurl);
                } else if (packageName.contains("com.facebook.katana")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended
                    for users to express themselves. Pre-filling these fields erodes the
                    authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow
                    the user to choose how they want to share. We can also make a custom landing
                    page, and the link
                    // will show the <meta content ="..."> text from that page with our link in
                    Facebook.
//                    intent.putExtra(Intent.EXTRA_TEXT, imageurl);
                    *//*ShareDialog shareDialog = new ShareDialog(this);
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("How to integrate Facebook from your app")
                            .setImageUrl(Uri.parse(imageurl))
                            .setContentDescription(
                                    "simple Fb Image share integration")
                            .setContentUrl(Uri.parse(imageurl))
                            .build();

                    shareDialog.show(linkContent);  // Show facebook ShareDialog*//*

                    *//*try {
                        Intent mIntentFacebook = new Intent();
                        mIntentFacebook.setClassName("com.facebook.katana", "com.facebook
                        .composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");
                        mIntentFacebook.setAction("android.intent.action.SEND");
                        mIntentFacebook.setType("text/plain");
                        mIntentFacebook.putExtra("android.intent.extra.TEXT", imageurl);
                        startActivity(mIntentFacebook);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent mIntentFacebookBrowser = new Intent(Intent.ACTION_SEND);
                        String mStringURL = "https://www.facebook.com/sharer/sharer.php?u=" +
                        imageurl;
                        mIntentFacebookBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse
                        (mStringURL));
                        startActivity(mIntentFacebookBrowser);
                    }*//*

                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", imageurl);


                } else if (packageName.contains("com.linkedin.android")) {
                    // If Gmail shows up twice, try removing this else-if clause and the
                    reference to "android.gm" above
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, imageurl);
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }

        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        Intent openInChooser = Intent.createChooser(intent, "Share rating via Social Media");
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }*/

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //<editor-fold desc="Image Camera">
        // handle result of pick image chooser
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            // For API >= 23 we need to check specifically that we have permissions to read
            // external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(EditProfileActivity.this,
                    fileUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(fileUri);
            }
        }
        //</editor-fold>

        //<editor-fold desc="Image Gallery">
        if (requestCode == GALLERY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            fileUri = data.getData();

            // For API >= 23 we need to check specifically that we have permissions to read
            // external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(EditProfileActivity.this,
                    fileUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(fileUri);
            }
        }
        //</editor-fold>

        //<editor-fold desc="Image Crop">
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File file = new File(getRealPathFromURI(result.getUri()));
                Bitmap bitmap = Utils.decodeFile(file, 512, 512);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

//                selectedBitmap = BitmapFactory.decodeFile(fileUri.getPath());

                Glide.with(this)
                        .load(Uri.fromFile(file))
                        .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                        .override(512, 512)
                        .into(imageProfile);

                if (bitmap != null) {

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

                    String bitmapString = Base64.encodeToString(outputStream.toByteArray(),
                            Base64.DEFAULT);

                    ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                    profileDataOperation.setPbProfilePhoto(bitmapString);
                    editProfile(profileDataOperation, AppConstants.PROFILE_IMAGE);
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(EditProfileActivity.this, " " + getString(R.string.crop_failed) +
                        result.getError(), Toast.LENGTH_LONG).show();
            }
        }
        //</editor-fold>

        //<editor-fold desc="Map Location">
        if (requestCode == AppConstants.REQUEST_CODE_MAP_LOCATION_SELECTION) {
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
                TextView textLatitude = linearView.findViewById(R.id.input_latitude);
                TextView textLongitude = linearView.findViewById(R.id
                        .input_longitude);
                TextView textImageMapMarker = linearView.findViewById(R.id
                        .text_image_map_marker);
                TextView textGoogleAddress = linearView.findViewById(R.id
                        .input_google_address);
                TextView inputIsAddressModified = linearView.findViewById(R.id
                        .input_is_address_modified);
                textLatitude.setText(objAddress.getLatitude());
                textLongitude.setText(objAddress.getLongitude());
                textGoogleAddress.setText(objAddress.getAddress());
                textImageMapMarker.setTextColor(defaultMarkerColor);
                inputIsAddressModified.setText("false");
//                }

            }
        }
        //</editor-fold>

        //<editor-fold desc="Facebook">
        if (IntegerConstants.REGISTRATION_VIA == IntegerConstants.REGISTRATION_VIA_FACEBOOK) {
            // Facebook Callback
            if (callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        //</editor-fold>

//        else if (IntegerConstants.REGISTRATION_VIA == IntegerConstants
//                .REGISTRATION_VIA_LINED_IN) {
//            // LinkedIn Callback
//            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,
//                    requestCode, resultCode, data);
//        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        //<editor-fold desc="Sign In">
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        //</editor-fold>

        //<editor-fold desc="Linked In">
        if (resultCode == RESULT_OK && requestCode == RC_LINKEDIN_SIGN_IN) {

            if (data != null) {
                if (data.getStringExtra("isBack").equalsIgnoreCase("0")) {
                    //If everything went Ok, change to another activity.
                    socialId = data.getStringExtra("url");
                    isAdd = true;

                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                    imAccount.setIMAccountProtocol(getString(R.string.linked_in));
                    imAccount.setIMAccountFirstName(data.getStringExtra("first_name"));
                    imAccount.setIMAccountLastName(data.getStringExtra("last_name"));
                    imAccount.setIMAccountProfileImage(data.getStringExtra("profileImage"));
                    imAccount.setIMAccountDetails(socialId);
                    imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    arrayListSocialContactObject.add(imAccount);

                    for (int i = 0; i < arrayListSocialEmail.size(); i++) {
                        ProfileDataOperationEmail operationEmail = (ProfileDataOperationEmail)
                                arrayListSocialEmail.get(i);
                        if (operationEmail.getEmEmailId().equalsIgnoreCase(
                                StringUtils.trim(data.getStringExtra("email")))) {

                            ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                            email.setEmEmailId(StringUtils.trim(data.getStringExtra("email")));
                            email.setEmType("Work");
                            email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                            email.setEmIsSocial(2);
                            if (!operationEmail.getEmSocialType().equalsIgnoreCase("")) {
                                email.setEmSocialType(operationEmail.getEmSocialType() + "," +
                                        "linkedin");
                                email.setEmId(operationEmail.getEmId());
                                email.setEmRcpType(operationEmail.getEmRcpType());
                            } else {
                                email.setEmSocialType("linkedin");
                            }

                            arrayListSocialEmail.set(i, email);

                            isAdd = false;
                            break;
                        }
                    }

                    if (isAdd) {

                        ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                        email.setEmEmailId(StringUtils.trim(data.getStringExtra("email")));
                        email.setEmType("Work");
                        email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                        email.setEmIsSocial(2);
                        email.setEmSocialType("linkedin");

                        arrayListSocialEmail.add(email);
                    }

                    socialTypeList.remove(getString(R.string.linked_in));

                    addSocialConnectView(arrayListSocialContactObject.get
                            (arrayListSocialContactObject.size() - 1), "");

                } else {
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, "Login cancelled!");
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootEditProfile, "Login cancelled!");
            }
        }
        //</editor-fold>

        //<editor-fold desc="Organization">
        if (resultCode == RESULT_OK && requestCode == 201) {
            if (data != null) {
                if (data.getStringExtra("isBack").equalsIgnoreCase("0")) {
                    //If everything went Ok, change to another activity.

                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();
                    if (organisationPosition == (arrayListOrganizationObject.size())) {
                        organization.setOrgName(data.getStringExtra("organizationName"));
                        organization.setOrgIndustryType(data.getStringExtra("organizationType"));
                        organization.setOrgLogo(data.getStringExtra("logo"));
                        organization.setOrgFromDate("");
                        organization.setOrgToDate("");
                        organization.setOrgJobTitle("");
                        organization.setOrgId("");
                        organization.setIsVerify(0);
                        organization.setOrgEntId(data.getStringExtra("orgId"));
                        organization.setIsCurrent(1);

                        arrayListOrganizationObject.add(organization);

                    } else {

                        ProfileDataOperationOrganization operationOrganization =
                                (ProfileDataOperationOrganization)
                                        arrayListOrganizationObject.get(organisationPosition);

                        organization.setOrgName(data.getStringExtra("organizationName"));
                        organization.setOrgIndustryType(data.getStringExtra("organizationType"));
                        organization.setOrgLogo(data.getStringExtra("logo"));
                        organization.setOrgFromDate(operationOrganization.getOrgFromDate());
                        organization.setOrgToDate(operationOrganization.getOrgToDate());
                        organization.setOrgJobTitle(operationOrganization.getOrgJobTitle());
                        organization.setOrgId(operationOrganization.getOrgId());
                        organization.setOrgEntId(data.getStringExtra("orgId"));
                        organization.setIsVerify(0);
                        organization.setIsCurrent(operationOrganization.getIsCurrent());

                        arrayListOrganizationObject.set(organisationPosition, organization);
                    }

                    linearOrganizationDetails.removeAllViews();

                    for (int i = 0; i < arrayListOrganizationObject.size(); i++) {
                        addOrganizationView(i, arrayListOrganizationObject.get(i));
                    }

                } else {
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, "You didn't select any" +
                            " Organization!");
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootEditProfile, "You didn't select any " +
                        "Organization!");
            }
        }
        //</editor-fold>
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String[] permissions, int requestCode) {
        boolean READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
                (EditProfileActivity
                        .this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        boolean WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
                (EditProfileActivity
                        .this, permissions[1]) !=
                PackageManager.PERMISSION_GRANTED;
        if (READ_EXTERNAL_STORAGE || WRITE_EXTERNAL_STORAGE) {
            requestPermissions(permissions, requestCode);
        } else {
            prepareToLoginUsingSocialMedia(requestCode);
        }
    }

    private void prepareToLoginUsingSocialMedia(int requestCode) {
        switch (requestCode) {
            case FACEBOOK_LOGIN_PERMISSION:
                facebookLogin();
                break;
            case GOOGLE_LOGIN_PERMISSION:
                googleSignIn();
                break;
            case LINKEDIN_LOGIN_PERMISSION:
                linkedInSignIn();
                break;
        }
    }

    private void facebookLogin() {
        // Facebook Initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        registerFacebookCallback();

        LoginManager.getInstance().logInWithReadPermissions(EditProfileActivity
                .this, Arrays.asList(getString(R.string.str_public_profile),
                getString(R.string.str_small_cap_email)));
    }

    private void registerFacebookCallback() {

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {

                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult
                                .getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse
                                    graphResponse) {

                                try {

                                    if (!jsonObject.getString("email").equalsIgnoreCase("")) {
                                        socialId = jsonObject.getString("id");
                                        isAdd = true;

                                        ProfileDataOperationImAccount imAccount = new
                                                ProfileDataOperationImAccount();

                                        imAccount.setIMAccountFirstName(jsonObject.getString
                                                ("first_name"));
                                        imAccount.setIMAccountLastName(jsonObject.getString
                                                ("last_name"));

                                        imAccount.setIMAccountProtocol(getString(R.string
                                                .facebook));
                                        imAccount.setIMAccountProfileImage("https://graph" +
                                                ".facebook" +
                                                ".com/" + socialId +
                                                "/picture?width=200&height=150");
                                        imAccount.setIMAccountPublic(IntegerConstants
                                                .PRIVACY_MY_CONTACT);
                                        imAccount.setIMAccountDetails(socialId);
                                        arrayListSocialContactObject.add(imAccount);

                                        for (int i = 0; i < arrayListSocialEmail.size(); i++) {
                                            ProfileDataOperationEmail operationEmail =
                                                    (ProfileDataOperationEmail)
                                                            arrayListSocialEmail.get(i);
                                            if (operationEmail.getEmEmailId().equalsIgnoreCase(
                                                    StringUtils.trim(jsonObject.getString
                                                            ("email")))) {

                                                ProfileDataOperationEmail email = new
                                                        ProfileDataOperationEmail();
                                                email.setEmEmailId(StringUtils.trim(jsonObject
                                                        .getString("email")));
                                                email.setEmType("Work");
                                                email.setEmPublic(IntegerConstants
                                                        .PRIVACY_MY_CONTACT);
                                                email.setEmIsSocial(2);
                                                if (!operationEmail.getEmSocialType()
                                                        .equalsIgnoreCase("")) {
                                                    email.setEmSocialType(operationEmail
                                                            .getEmSocialType() + ",facebook");
                                                    email.setEmId(operationEmail.getEmId());
                                                    email.setEmRcpType(operationEmail
                                                            .getEmRcpType());
                                                } else {
                                                    email.setEmSocialType("facebook");
                                                }

                                                arrayListSocialEmail.set(i, email);

                                                isAdd = false;
                                                break;
                                            }
                                        }

                                        if (isAdd) {
                                            ProfileDataOperationEmail email = new
                                                    ProfileDataOperationEmail();
                                            email.setEmEmailId(StringUtils.trim(jsonObject
                                                    .getString("email")));
                                            email.setEmType("Work");
                                            email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                                            email.setEmIsSocial(2);
                                            email.setEmSocialType("facebook");

                                            arrayListSocialEmail.add(email);
                                        }

                                        socialTypeList.remove(getString(R.string.facebook));

                                        addSocialConnectView(arrayListSocialContactObject.get
                                                        (arrayListSocialContactObject.size() - 1)
                                                , "");
                                    } else {
                                        Utils.showErrorSnackBar(EditProfileActivity.this,
                                                relativeRootEditProfile, "Email address not " +
                                                        "found!!");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name, email,gender, " +
                                "birthday, location");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Utils.showErrorSnackBar(EditProfileActivity.this,
                                relativeRootEditProfile, getString(R.string
                                        .error_facebook_login_cancelled));
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Utils.showErrorSnackBar(EditProfileActivity.this,
                                relativeRootEditProfile, exception.getMessage());
                    }
                });
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i("Sign In Result", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {

                socialId = acct.getId();
                isAdd = true;

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                imAccount.setIMAccountProtocol(getString(R.string.google_plus));

                imAccount.setIMAccountFirstName(acct.getGivenName());
                imAccount.setIMAccountLastName(acct.getFamilyName());
                imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                imAccount.setIMAccountProfileImage(String.valueOf(acct.getPhotoUrl()));
                imAccount.setIMAccountDetails(socialId);
                arrayListSocialContactObject.add(imAccount);

                for (int i = 0; i < arrayListSocialEmail.size(); i++) {
                    ProfileDataOperationEmail operationEmail = (ProfileDataOperationEmail)
                            arrayListSocialEmail.get(i);
                    if (operationEmail.getEmEmailId().equalsIgnoreCase(
                            StringUtils.trim(acct.getEmail()))) {

                        ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                        email.setEmEmailId(StringUtils.trim(acct.getEmail()));
                        email.setEmType("Work");
                        email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                        email.setEmIsSocial(2);
                        if (!operationEmail.getEmSocialType().equalsIgnoreCase("")) {
                            email.setEmSocialType(operationEmail.getEmSocialType() + ",google");
                            email.setEmId(operationEmail.getEmId());
                            email.setEmRcpType(operationEmail.getEmRcpType());
                        } else {
                            email.setEmSocialType("google");
                        }

                        arrayListSocialEmail.set(i, email);

                        isAdd = false;
                        break;
                    }
                }

                if (isAdd) {

                    ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                    email.setEmEmailId(StringUtils.trim(acct.getEmail()));
                    email.setEmType("Work");
                    email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    email.setEmIsSocial(2);
                    email.setEmSocialType("google");

                    arrayListSocialEmail.add(email);
                }

                socialTypeList.remove(getString(R.string.google_plus));

                addSocialConnectView(arrayListSocialContactObject.get
                        (arrayListSocialContactObject.size() - 1), "");
            }

        } else {
            // Signed out.
            Utils.showErrorSnackBar(EditProfileActivity.this,
                    relativeRootEditProfile, getString(R.string.error_retrieving_details));
        }
    }

    public void linkedInSignIn() {

        Intent intent = new Intent(EditProfileActivity.this, LinkedinLoginActivity.class);
        intent.putExtra("from", "profile");
        startActivityForResult(intent, RC_LINKEDIN_SIGN_IN);// Activity is started with requestCode
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

                    Utils.setStringPreference(this, AppConstants.PREF_USER_NAME, profileDetail
                            .getPbNameFirst() + " " + profileDetail.getPbNameLast());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_FIRST_NAME,
                            profileDetail.getPbNameFirst());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_LAST_NAME,
                            profileDetail.getPbNameLast());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_NUMBER, profileDetail
                            .getVerifiedMobileNumber());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING,
                            profileDetail.getTotalProfileRateUser());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, profileDetail
                            .getProfileRating());
                    Utils.setStringPreference(this, AppConstants.PREF_USER_PHOTO, profileDetail
                            .getPbProfilePhoto());
                    Utils.setBooleanPreference(this, AppConstants.PREF_USER_PROFILE_UPDATE, true);

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
                            linearEmailDetails.removeAllViews();
                            emailDetails();
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

                    Utils.showSuccessSnackBar(this, relativeRootEditProfile, getString(R.string
                            .success_profile_update));
                    isUpdated = false;

                } else {
                    if (editProfileResponse != null) {
                        Log.e("error response", editProfileResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                editProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_COUNTRY_CODE_DETAIL">
            if (serviceType.contains(WsConstants.REQ_COUNTRY_CODE_DETAIL)) {
                WsResponseObject countryCodeResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (countryCodeResponse != null && StringUtils.equalsIgnoreCase
                        (countryCodeResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<Country> countryList = countryCodeResponse.getArrayListCountry();

                    // REALM
                    /*if (countryList.size() > 0) {
                        realm = Realm.getDefaultInstance();
                        realm.beginTransaction();  //open the database database operation

                        for (int i = 0; i < countryList.size(); i++) {
                            DbCountry country = realm.createObject(DbCountry.class);  //this will
                            // create a information object which will be inserted in database

                            country.setCountryId(countryList.get(i).getCountryId());
                            country.setCountryCode(countryList.get(i).getCountryCode());
                            country.setCountryCodeNumber(countryList.get(i).getCountryCodeNumber());
                            country.setCountryName(countryList.get(i).getCountryName());
                            country.setCountryNumberMaxDigits(countryList.get(i)
                                    .getCountryNumberMaxDigits());
                            country.setCountryNumberMinDigits(countryList.get(i)
                                    .getCountryNumberMinDigits());
                        }

                        realm.commitTransaction(); //close the database
                    }*/

                    if (countryList.size() > 0) {
                        ArrayList<Country> arrayListCountry = new ArrayList<>();
                        TableCountryMaster tableCountryMaster = new TableCountryMaster
                                (databaseHandler);
                        for (int i = 0; i < countryList.size(); i++) {
                            Country country = new Country();
                            country.setCountryId(countryList.get(i).getCountryId());
                            country.setCountryCode(countryList.get(i).getCountryCode());
                            country.setCountryCodeNumber(countryList.get(i).getCountryCodeNumber());
                            country.setCountryName(countryList.get(i).getCountryName());
                            country.setCountryNumberMaxDigits(countryList.get(i)
                                    .getCountryNumberMaxDigits());
                            country.setCountryNumberMinDigits(countryList.get(i)
                                    .getCountryNumberMinDigits());
                            arrayListCountry.add(country);
                        }
                        tableCountryMaster.addArrayCountry(arrayListCountry);
                    }

                    addressDetails();

                } else {
                    if (countryCodeResponse != null) {
                        Log.e("error response", countryCodeResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                countryCodeResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_STATE_DETAILS">
            if (serviceType.contains(WsConstants.REQ_STATE_DETAILS)) {
                WsResponseObject stateResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (stateResponse != null && StringUtils.equalsIgnoreCase
                        (stateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    final String[] positionSplit = serviceType.split(":");
                    final int position = Integer.parseInt(positionSplit[1]);
                   /* String stateName = "";
                    if (positionSplit.length > 2) {
                        stateName = positionSplit[2];
                    }*/


                    View linearAddress = linearAddressDetails.getChildAt(position);
                    final Spinner spinnerState = linearAddress.findViewById(R.id.spinner_state);
                    final EditText street = linearAddress.findViewById(R.id.input_street);
                    final TextView textImageMapMarker = linearAddress.findViewById(R.id
                            .text_image_map_marker);
                    final TextView inputIsAddressModified = linearAddress.findViewById(R.id
                            .input_is_address_modified);

                    final ArrayList<State> stateList = stateResponse.getArrayListState();

                    final ArrayList<String> arrayListState = new ArrayList<>();
                    final ArrayList<String> arrayListStateId = new ArrayList<>();
                    arrayListState.add(getString(R.string.hint_state_required));
                    for (int i = 0; i < stateList.size(); i++) {
                        arrayListState.add(stateList.get(i).getStateName());
                        arrayListStateId.add(stateList.get(i).getStateId());
                    }

                    int selectedIndex = 0;
                    if (spinnerState.getTag(R.id.spinner_state_id) != null && !StringUtils
                            .isEmpty(spinnerState.getTag(R.id.spinner_state_id).toString())) {
                        if (arrayListStateId.contains(spinnerState.getTag(R.id.spinner_state_id)
                                .toString())) {
                            selectedIndex = arrayListStateId.indexOf(spinnerState.getTag(R.id
                                    .spinner_state_id).toString());
                        }
                    }

                    RSpinnerAdapter stateSpinnerAdapter = new RSpinnerAdapter(this, R.layout
                            .header_spinner_call_log, arrayListState, ContextCompat.getColor(this, R
                            .color.colorAccent), ContextCompat.getColor(this, R.color
                            .regularFontColor));
                    stateSpinnerAdapter.setDropDownViewResource(R.layout
                            .list_item_spinner_call_log);
                    stateSpinnerAdapter.setHintColor(street.getHintTextColors());
                    spinnerState.setAdapter(stateSpinnerAdapter);

                    spinnerState.setSelection(selectedIndex + 1);

                   /* if (!StringUtils.isBlank(stateName)) {
                        int statePosition = stateSpinnerAdapter.getPosition(stateName);
                        if (statePosition != -1) {
                            spinnerState.setSelection(statePosition);
                        } else {
                            spinnerState.setSelection(0);
                        }
                    }*/

                    spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener
                            () {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i,
                                                   long l) {
                            if (i != 0) {

                                String stateId = stateList.get(i - 1).getStateId();

                                if (spinnerState.getTag(R.id.spinner_state_id) != null
                                        && !StringUtils.equalsAnyIgnoreCase(spinnerState.getTag(R.id
                                        .spinner_state_id).toString(), stateId)) {
                                    textImageMapMarker.setTextColor(defaultMarkerColor);
                                    inputIsAddressModified.setText("true");
                                    isUpdated = true;
                                }


                                spinnerState.setTag(R.id.spinner_state_id, stateId);
                                /*getCityList(stateId, String.valueOf
                                        (position));*/

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                } else {
                    if (stateResponse != null) {
                        Log.e("error response", stateResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                stateResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_CITY_DETAILS">
            /*if (serviceType.contains(WsConstants.REQ_CITY_DETAILS)) {
                Utils.hideProgressDialog();
                WsResponseObject cityResponse = (WsResponseObject) data;
                if (cityResponse != null && StringUtils.equalsIgnoreCase
                        (cityResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    String[] positionSplit = serviceType.split(":");
                    final int position = Integer.parseInt(positionSplit[1]);
                    *//*String cityName = "";
                    if (positionSplit.length > 2) {
                        cityName = positionSplit[2];
                    }*//*

                    View linearAddress = linearAddressDetails.getChildAt(position);
//                    final Spinner spinnerCity = linearAddress.findViewById(R.id.spinner_city);
                    final EditText street = linearAddress.findViewById(R.id.input_street);

                    final ArrayList<City> cityList = cityResponse.getArrayListCity();

                    final ArrayList<String> arrayListCity = new ArrayList<>();
                    arrayListCity.add(getString(R.string.hint_city_town_required));
                    for (int i = 0; i < cityList.size(); i++) {
                        arrayListCity.add(cityList.get(i).getCityName());
                    }

                    RSpinnerAdapter citySpinnerAdapter = new RSpinnerAdapter(this, R.layout
                            .header_spinner_call_log, arrayListCity, ContextCompat.getColor(this, R
                            .color.colorAccent), ContextCompat.getColor(this, R.color
                            .regularFontColor));
                    citySpinnerAdapter.setDropDownViewResource(R.layout
                            .list_item_spinner_call_log);
                    citySpinnerAdapter.setHintColor(street.getHintTextColors());
                    spinnerCity.setAdapter(citySpinnerAdapter);

                    *//*if (!StringUtils.isBlank(cityName)) {
                        int cityPosition = citySpinnerAdapter.getPosition(cityName);
                        if (cityPosition != -1) {
                            spinnerCity.setSelection(cityPosition);
                        } else {
                            spinnerCity.setSelection(0);
                        }
                    }*//*

                    *//*spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener
                            () {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i,
                                                   long l) {
                            if (i != 0) {
                                String cityId = cityList.get(i - 1).getCityId();
                                spinnerCity.setTag(R.id.spinner_city_id, cityId);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });*//*

                } else {
                    if (cityResponse != null) {
                        Log.e("error response", cityResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                cityResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                .string.msg_try_later));
                    }
                }
            }*/
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                SocialDialog();
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

    private void SocialDialog() {

        final Dialog dialog = new Dialog(EditProfileActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        View dialogView = LayoutInflater.from(EditProfileActivity.this).inflate(R.layout
                .dialog_social_list, null);
        dialog.setContentView(dialogView);

        TextView txtTitle = dialogView.findViewById(R.id.tvDialogTitle);
        txtTitle.setText(getString(R.string.str_social_connect));

        RecyclerView recycleViewSocialList = dialogView.findViewById(R.id.recycle_view_social_list);
        recycleViewSocialList.setLayoutManager(new LinearLayoutManager(EditProfileActivity.this,
                LinearLayoutManager.VERTICAL, false));

        socialConnectListAdapter = new SocialConnectListAdapter(socialTypeList
                , new SocialConnectListAdapter.onClickListener() {
            @Override
            public void onClick(String socialName) {

                dialog.dismiss();

                if (socialName.equalsIgnoreCase(getString(R.string.facebook))) {

                    IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_FACEBOOK;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermissionToExecute(requiredPermissions, FACEBOOK_LOGIN_PERMISSION);
                    } else {
                        // Facebook Initialization
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        callbackManager = CallbackManager.Factory.create();

                        // Callback registration
                        registerFacebookCallback();

                        LoginManager.getInstance().logInWithReadPermissions(EditProfileActivity
                                        .this,
                                Arrays.asList(getString(R.string.str_public_profile), getString(R
                                        .string.str_small_cap_email)));

                    }

                } else if (socialName.equalsIgnoreCase(getString(R.string.google_plus))) {

                    IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_GOOGLE;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermissionToExecute(requiredPermissions, GOOGLE_LOGIN_PERMISSION);
                    } else {
                        googleSignIn();
                    }

                } else if (socialName.equalsIgnoreCase("Linkedin")) {


                    IntegerConstants.REGISTRATION_VIA = IntegerConstants.REGISTRATION_VIA_LINED_IN;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermissionToExecute(requiredPermissions, LINKEDIN_LOGIN_PERMISSION);
                    } else {
                        linkedInSignIn();
                    }

                } else if (socialName.equalsIgnoreCase("Custom") || socialName.equalsIgnoreCase
                        ("કસ્ટમ")
                        || socialName.equalsIgnoreCase("कस्टम")) {
                    showCustomTypeDialogForSocial();
                } else {
                    addSocialConnectView(null, socialName);
                }
            }
        });
        recycleViewSocialList.setAdapter(socialConnectListAdapter);

        dialog.show();
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
                String firstName = inputFirstName.getText().toString().trim();
                String lastName = inputLastName.getText().toString().trim();
                if (StringUtils.length(firstName) > 2 && StringUtils.length(firstName) < 51 &&
                        isNameValid(firstName)) {
                    if (StringUtils.length(lastName) > 2 && StringUtils.length(lastName) < 51 &&
                            isNameValid(lastName)) {
                        profileDataOperation.setPbNameFirst(firstName);
                        profileDataOperation.setPbNameLast(lastName);
                        editProfile(profileDataOperation, AppConstants.NAME);
                    } else {
                        Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R.string
                                .error_required_last_name));
                    }
                } else {
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R.string
                            .error_required_first_name));
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
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R.string
                            .error_select_gender));
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
                    EditText inputPhoneNumber = linearPhone.findViewById(R.id.input_value);
                    Spinner phoneNumberType = linearPhone.findViewById(R.id.spinner_type);
                    TextView textIsPublic = linearPhone.findViewById(R.id
                            .text_is_public);
                    TextView textIsVerified = linearPhone.findViewById(R.id
                            .text_is_verified);
                    RelativeLayout relativeRowEditProfile = linearPhone
                            .findViewById(R.id.relative_row_edit_profile);
                    phoneNumber.setPhoneNumber(inputPhoneNumber.getText().toString().trim());
                    phoneNumber.setPhoneType((String) phoneNumberType.getSelectedItem());
                    phoneNumber.setPhoneId((String) relativeRowEditProfile.getTag());
                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        phoneNumber.setPhonePublic(Integer.parseInt(textIsPublic.getText()
                                .toString().trim()));
                    } else {
                        phoneNumber.setPhonePublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }
                    if (StringUtils.length(textIsVerified.getText().toString()) > 0) {
                        phoneNumber.setPbRcpType(Integer.parseInt(textIsVerified.getText()
                                .toString().trim()));
                    }

                    if (!StringUtils.isBlank(phoneNumber.getPhoneNumber())) {
                        arrayListNewPhone.add(phoneNumber);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_number));
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
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                    .string.error_no_update));
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
                    EditText emailId = linearEmail.findViewById(R.id.input_value);
                    Spinner emailType = linearEmail.findViewById(R.id.spinner_type);
                    TextView textIsPublic = linearEmail.findViewById(R.id
                            .text_is_public);
                    TextView textIsVerified = linearEmail.findViewById(R.id
                            .text_is_verified);
                    TextView textIsSocial = linearEmail.findViewById(R.id.text_is_social);
                    RelativeLayout relativeRowEditProfile = linearEmail
                            .findViewById(R.id.relative_row_edit_profile);
                    email.setEmEmailId(StringUtils.trim(emailId.getText().toString().trim()));
                    email.setEmType((String) emailType.getSelectedItem());
                    email.setEmId((String) relativeRowEditProfile.getTag());
                    email.setEmSocialType(textIsSocial.getText().toString().trim());
                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        email.setEmPublic(Integer.parseInt(textIsPublic.getText().toString().trim
                                ()));
                    } else {
                        email.setEmPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }
                    if (StringUtils.length(textIsVerified.getText().toString()) > 0) {
                        email.setEmRcpType(Integer.parseInt(textIsVerified.getText().toString()
                                .trim()));
                    } else {
                        email.setEmRcpType(IntegerConstants.RCP_TYPE_SECONDARY);
                    }

                    if (!StringUtils.isBlank(email.getEmEmailId())) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email.getEmEmailId())
                                .matches()) {
                            arrayListNewEmail.add(email);
                        } else if (!emailId.isEnabled()) {
                            arrayListNewEmail.add(email);
                        } else {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_invalid_email));
                        }
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_email));
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
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                    .string.error_no_update));
                        }
                    }
                }
                break;
            //</editor-fold>

            // <editor-fold desc="button_social_contact_update">
            case R.id.button_social_contact_update:

                ArrayList<ProfileDataOperationEmail> arrayListFinalEmailAccount = new ArrayList<>();

                ArrayList<ProfileDataOperationImAccount> arrayListNewImAccount = new ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearSocialContactDetails.getChildCount(); i++) {
                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
                    View linearSocialContact = linearSocialContactDetails.getChildAt(i);
                    EditText imAccountName = linearSocialContact.findViewById(R.id
                            .input_value);
                    TextView textIsPublic = linearSocialContact.findViewById(R.id
                            .text_is_public);
                    TextView imAccountProtocol = linearSocialContact.findViewById(R.id
                            .input_protocol);
                    TextView imAccountProfileImage = linearSocialContact.findViewById(R.id
                            .text_profile_image);
                    RelativeLayout relativeRowEditProfileSocial = linearSocialContact
                            .findViewById(R.id.relative_row_edit_profile_social);
                    TextView textFirstName = linearSocialContact.findViewById(R.id.text_first_name);
                    TextView textLastName = linearSocialContact.findViewById(R.id.text_last_name);

                    imAccount.setIMAccountDetails(imAccountName.getText().toString().trim());
                    imAccount.setIMUserId(imAccountName.getText().toString().trim());
                    imAccount.setIMAccountProtocol(imAccountProtocol.getText().toString().trim());
                    imAccount.setIMId((String) relativeRowEditProfileSocial.getTag());
                    imAccount.setIMAccountProfileImage(imAccountProfileImage.getText().toString()
                            .trim());
                    imAccount.setIMAccountFirstName(textFirstName.getText().toString().trim());
                    imAccount.setIMAccountLastName(textLastName.getText().toString().trim());

                    if (StringUtils.length(textIsPublic.getText().toString()) > 0) {
                        imAccount.setIMAccountPublic(Integer.parseInt(textIsPublic.getText()
                                .toString().trim()));
                    } else {
                        imAccount.setIMAccountPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }

                    if (!StringUtils.isBlank(imAccount.getIMAccountDetails())) {
                        arrayListNewImAccount.add(imAccount);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_account));
                        }
                    }

                }
                if (isValid) {
                    if (arrayListNewImAccount.size() > 0) {

                        arrayListFinalEmailAccount.addAll(arrayListSocialEmail);

                        profileDataOperation.setPbIMAccounts(arrayListNewImAccount);
                        profileDataOperation.setPbEmailId(arrayListFinalEmailAccount);
                        editProfile(profileDataOperation, AppConstants.IM_ACCOUNT);

                    } else {
                        if (arrayListSocialContactObject.size() > 0) {
                            profileDataOperation.setPbIMAccounts(arrayListNewImAccount);
                            editProfile(profileDataOperation, AppConstants.IM_ACCOUNT);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                    .string.error_no_update));
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
                    EditText website = linearWebsite.findViewById(R.id.input_value);
                    Spinner websiteType = linearWebsite.findViewById(R.id.spinner_type);
                    RelativeLayout relativeRowEditProfile = linearWebsite
                            .findViewById(R.id.relative_row_edit_profile);
                    webAddress.setWebAddress(website.getText().toString().trim());
                    webAddress.setWebType((String) websiteType.getSelectedItem());
                    webAddress.setWebId((String) relativeRowEditProfile.getTag());

                    webAddress.setWebPublic(IntegerConstants.PRIVACY_EVERYONE);

                    if (!StringUtils.isBlank(webAddress.getWebAddress())) {
                        arrayListNewWebAddress.add(webAddress);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_web_address));
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
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                    .string.error_no_update));
                        }
                    }
                }
                break;
            //</editor-fold>

            // TODO : Hardik
            // <editor-fold desc="button_organization_update">
            case R.id.button_organization_update:
                ArrayList<ProfileDataOperationOrganization> arrayListNewOrganization = new
                        ArrayList<>();
                isValid = true;
                for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();
                    View linearOrganization = linearOrganizationDetails.getChildAt(i);
                    EditText inputCompanyName = linearOrganization.findViewById(R.id
                            .input_company_name);
                    EditText inputDesignationName = linearOrganization.findViewById(R.id
                            .input_designation_name);

                    // TextView textOrgLogo = linearOrganization.findViewById(R.id.text_org_logo);
                    TextView textEnterpriseOrgId = linearOrganization.findViewById(R.id
                            .text_enterprise_org_id);
                    TextView textOrgName = linearOrganization.findViewById(R.id.text_org_name);

                    EditText inputFromDate = linearOrganization.findViewById(R.id.input_from_date);
                    EditText inputToDate = linearOrganization.findViewById(R.id.input_to_date);

                    RelativeLayout relativeRowEditProfile = linearOrganization
                            .findViewById(R.id.relative_row_edit_profile);
                    CheckBox checkboxOrganization = linearOrganization.findViewById(R
                            .id.checkbox_organization);
                    organization.setOrgName(textOrgName.getText().toString().trim());
                    organization.setOrgJobTitle(inputDesignationName.getText().toString().trim());
                    organization.setOrgId((String) relativeRowEditProfile.getTag());
                    organization.setIsCurrent(checkboxOrganization.isChecked() ? 1 : 0);
                    organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);
                    organization.setOrgEntId(textEnterpriseOrgId.getText().toString().trim());
                    // organization.setOrgLogo(textOrgLogo.getText().toString().trim());

                    ColorStateList colorStateList = ColorStateList.valueOf(ContextCompat.getColor
                            (EditProfileActivity
                                    .this, R.color.colorDarkGray));
                    ViewCompat.setBackgroundTintList(inputFromDate, colorStateList);
                    ViewCompat.setBackgroundTintList(inputToDate, colorStateList);

                    if (checkboxOrganization.isChecked()) {

                        ColorStateList colorState = ColorStateList.valueOf(ContextCompat.getColor
                                (EditProfileActivity
                                        .this, R.color.grayishMagenta));
                        ViewCompat.setBackgroundTintList(inputToDate, colorState);
                    }

                    if (!StringUtils.isBlank(inputToDate.getText().toString().trim())) {
                        organization.setOrgToDate(
                                Utils.convertDateFormat(inputToDate.getText()
                                        .toString().trim(), getEventDateFormatForUpdate
                                        (inputToDate.getText()
                                                .toString().trim()), "yyyy-MM-dd HH:mm:ss"));

                    } else {
                        organization.setOrgToDate(inputToDate.getText().toString().trim());
                    }

                    organization.setOrgFromDate(
                            Utils.convertDateFormat(inputFromDate.getText()
                                    .toString().trim(), getEventDateFormatForUpdate(inputFromDate
                                    .getText()
                                    .toString().trim()), "yyyy-MM-dd HH:mm:ss"));

                    if (!StringUtils.isBlank(organization.getOrgName()) ||
                            !StringUtils.isBlank(organization.getOrgJobTitle()) ||
                            !StringUtils.isBlank(organization.getOrgFromDate())) {
                        if (!StringUtils.isBlank(organization.getOrgName())) {
                            if (StringUtils.isBlank(organization.getOrgJobTitle())) {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                        getString(R.string.error_required_org_designation));
                                isValid = false;
                            } else if (StringUtils.isBlank(organization.getOrgFromDate())) {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                        getString(R.string.error_required_org_from_date));
//                                inputFromDate.setHighlightColor(ContextCompat.getColor
// (EditProfileActivity
//                                        .this, R.color.colorSnackBarNegative));

                                ColorStateList colorStateList1 = ColorStateList.valueOf
                                        (ContextCompat.getColor(EditProfileActivity
                                                .this, R.color.colorSnackBarNegative));
                                ViewCompat.setBackgroundTintList(inputFromDate, colorStateList1);

                                isValid = false;
                                break;
                            } else if (!checkboxOrganization.isChecked() && StringUtils.isBlank
                                    (organization.getOrgToDate())) {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                        getString(R.string.error_required_org_to_date));
//                                inputToDate.setHighlightColor(ContextCompat.getColor
// (EditProfileActivity
//                                        .this, R.color.colorSnackBarNegative));

                                ColorStateList colorStateList1 = ColorStateList.valueOf
                                        (ContextCompat.getColor(EditProfileActivity
                                                .this, R.color.colorSnackBarNegative));
                                ViewCompat.setBackgroundTintList(inputToDate, colorStateList1);

                                isValid = false;
                                break;
                            } else {
                                arrayListNewOrganization.add(organization);
                            }
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_org_name));
                            isValid = false;
                        }
                    } else {
                        if (i != 0) {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_org_name));
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
                        profileDataOperation.setPbOrganization(arrayListNewOrganization);
                        editProfile(profileDataOperation, AppConstants.ORGANIZATION);
//                        if (!isCurrentSelected) {
//                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
//                                    .string.error_current_organization));
//                        } else {
//                            profileDataOperation.setPbOrganization(arrayListNewOrganization);
//                            editProfile(profileDataOperation, AppConstants.ORGANIZATION);
//                        }
                    } else {
                        if (arrayListOrganizationObject.size() > 0) {
                            profileDataOperation.setPbOrganization(arrayListNewOrganization);
                            editProfile(profileDataOperation, AppConstants.ORGANIZATION);
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                    .string.error_no_update));
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
                    EditText eventDate = linearEvent.findViewById(R.id.input_value);
                    Spinner eventType = linearEvent.findViewById(R.id.spinner_type);
                    TextView textIsPublic = linearEvent.findViewById(R.id
                            .text_is_public);
                    CheckBox checkboxHideYear = linearEvent.findViewById(R.id
                            .checkbox_hide_year);
                    RelativeLayout relativeRowEditProfile = linearEvent
                            .findViewById(R.id.relative_row_edit_profile);
                    event.setEventType((String) eventType.getSelectedItem());
                    event.setIsYearHidden(checkboxHideYear.isChecked() ? 1 : 0);
                    event.setEventId((String) relativeRowEditProfile.getTag());
                    if (StringUtils.length(textIsPublic.getText().toString().trim()) > 0) {
                        event.setEventPublic(Integer.parseInt(textIsPublic.getText().toString()
                                .trim()));
                    } else {
                        event.setEventPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }
                    if (!StringUtils.isBlank(eventDate.getText().toString())) {

                        event.setEventDateTime(Utils.convertDateFormat(eventDate.getText()
                                .toString().trim(), getEventDateFormatForUpdate(eventDate.getText()
                                .toString().trim()), "yyyy-MM-dd HH:mm:ss"));
                        event.setEventDate(Utils.convertDateFormat(eventDate.getText().toString()
                                        .trim(),
                                getEventDateFormatForUpdate(eventDate.getText()
                                        .toString().trim()), "yyyy-MM-dd HH:mm:ss"));
                        arrayListNewEvent.add(event);
                    } else {
                        if (i != 0) {
                            isValid = false;
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_event_date));
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
                    Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R.string
                            .error_once_birthday_anniversary));
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
                                Utils.showErrorSnackBar(this, relativeRootEditProfile, getString
                                        (R.string.error_no_update));
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
                    Spinner addressType = linearAddress.findViewById(R.id
                            .spinner_type);

                   /* EditText country = linearAddress.findViewById(R.id
                            .input_country);
                    EditText state = linearAddress.findViewById(R.id.input_state);*/
                    EditText city = linearAddress.findViewById(R.id.input_city);
                    Spinner country = linearAddress.findViewById(R.id.spinner_country);
                    Spinner state = linearAddress.findViewById(R.id.spinner_state);
//                    Spinner city = linearAddress.findViewById(R.id.spinner_city);
                    EditText street = linearAddress.findViewById(R.id.input_street);
                    EditText neighborhood = linearAddress.findViewById(R.id.input_neighborhood);
                    EditText pinCode = linearAddress.findViewById(R.id.input_pin_code);
                    RelativeLayout relativeRowEditProfile = linearAddress
                            .findViewById(R.id.relative_row_edit_profile);
                    TextView textLatitude = linearAddress.findViewById(R.id.input_latitude);
                    TextView textLongitude = linearAddress.findViewById(R.id.input_longitude);
                    TextView textGoogleAddress = linearAddress.findViewById(R.id
                            .input_google_address);
                    TextView textIsPublic = linearAddress.findViewById(R.id.text_is_public);
                    TextView inputIsAddressModified = linearAddress.findViewById(R.id
                            .input_is_address_modified);
                    TextView textImageMapMarker = linearAddress.findViewById(R.id
                            .text_image_map_marker);

                    String countryName = country.getSelectedItem().toString().trim();
                    String stateName = state.getSelectedItem().toString().trim();
                    String cityName = city.getText().toString().trim();
                    String streetName = street.getText().toString().trim();
                    String neighborhoodName = neighborhood.getText().toString().trim();
                    String pinCodeName = pinCode.getText().toString().trim();

                    address.setCountry(countryName);
                    address.setState(stateName);
                    address.setCity(cityName);

                    if (country.getTag(R.id.spinner_country_id) != null) {
                        address.setCountryId(Integer.valueOf(country.getTag(R.id
                                .spinner_country_id).toString()));
                    }
                    if (state.getTag(R.id.spinner_state_id) != null) {
                        address.setStateId(Integer.valueOf(state.getTag(R.id.spinner_state_id)
                                .toString()));
                    }
                    /*if (city.getTag(R.id.spinner_city_id) != null) {
                        address.setCityId(Integer.valueOf(city.getTag(R.id.spinner_city_id).toString
                                ()));
                    }*/

                    address.setStreet(streetName);
                    address.setNeighborhood(neighborhoodName);
                    address.setPostCode(pinCodeName);
                    address.setFormattedAddress(Utils.setFormattedAddress(streetName,
                            neighborhoodName, cityName, stateName, countryName, pinCodeName));
                    address.setAddressType((String) addressType.getSelectedItem());
                    address.setGoogleAddress(textGoogleAddress.getText().toString().trim());
                    ArrayList<String> arrayListLatLong = new ArrayList<>();
                    arrayListLatLong.add(textLongitude.getText().toString().trim());
                    arrayListLatLong.add(textLatitude.getText().toString().trim());
                    address.setGoogleLatLong(arrayListLatLong);
                    address.setAddId((String) relativeRowEditProfile.getTag());
                    if (StringUtils.length(textIsPublic.getText().toString().trim()) > 0) {
                        address.setAddPublic(Integer.parseInt(textIsPublic.getText().toString
                                ().trim()));
                    } else {
                        address.setAddPublic(IntegerConstants.PRIVACY_MY_CONTACT);
                    }

                    if (country.getSelectedItemPosition() != 0 || state.getSelectedItemPosition()
                            != 0 || !StringUtils.isBlank(address.getCity())
                            || !StringUtils.isBlank(address.getStreet())) {
                        if (country.getSelectedItemPosition() != 0) {
                            if (state.getSelectedItemPosition() != 0) {
                                if (!StringUtils.isBlank(address.getCity())) {
                                    if (!StringUtils.isBlank(address.getStreet())) {
                                        if (!StringUtils.isBlank(address.getGoogleLatLong().get(0))
                                                && !StringUtils.isBlank(address.getGoogleLatLong
                                                ().get(1))) {
//                                            if (!isAddressModified) {
                                            if (!StringUtils.equalsAnyIgnoreCase
                                                    (inputIsAddressModified.getText().toString()
                                                                    .trim(),
                                                            "true")) {
                                                arrayListNewAddress.add(address);
                                            } else {
                                                Utils.hideSoftKeyboard(EditProfileActivity.this,
                                                        pinCode);
                                                Utils.showErrorSnackBar(this,
                                                        relativeRootEditProfile, getString(R.string
                                                                .error_required_address_mapping));
                                                Animation animShake = AnimationUtils
                                                        .loadAnimation(this, R.anim.shake);
                                                textImageMapMarker.startAnimation(animShake);
                                                textImageMapMarker.setTextColor(ContextCompat
                                                        .getColor(EditProfileActivity
                                                                .this, R.color
                                                                .colorSnackBarNegative));
                                                isValid = false;
                                                break;
                                            }
                                        } else {
                                            Utils.hideSoftKeyboard(EditProfileActivity.this,
                                                    pinCode);
                                            Utils.showErrorSnackBar(this,
                                                    relativeRootEditProfile, getString(R.string
                                                            .error_required_address_mapping));
                                            Animation animShake = AnimationUtils.loadAnimation
                                                    (this, R.anim.shake);
                                            textImageMapMarker.startAnimation(animShake);
                                            textImageMapMarker.setTextColor(ContextCompat
                                                    .getColor(this, R.color
                                                            .colorSnackBarNegative));
                                            isValid = false;
                                            break;
                                        }
                                    } else {
                                        Utils.hideSoftKeyboard(EditProfileActivity.this,
                                                street);
                                        Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                                getString(R.string.error_required_street));
                                        street.requestFocus();
                                        isValid = false;
                                        break;
                                    }
                                } else {
                                    Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                            getString(R.string.error_required_city));
                                    isValid = false;
                                    break;
                                }
                            } else {
                                Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                        getString(R.string.error_required_state));
                                isValid = false;
                                break;
                            }
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_country));
                            isValid = false;
                            break;
                        }
                    } else {
                        if (i != 0) {
                            Utils.showErrorSnackBar(this, relativeRootEditProfile,
                                    getString(R.string.error_required_country));
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
//                            Utils.hideKeyBoard(EditProfileActivity.this);
                            Utils.showErrorSnackBar(this, relativeRootEditProfile, getString(R
                                    .string.error_no_update));
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

    private void init(boolean showAddress) {

        initToolbar();
        setFonts();
        profileDetails(true, true, true);
        phoneNumberDetails();
        emailDetails();
        organizationDetails();
        websiteDetails();
        if (showAddress) {
            addressDetails();
        }
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

    private void setAddressTextWatcher(EditText applyWatcher, final TextView textImageMapMarker,
                                       final TextView inputIsAddressModified) {

        TextWatcher addressTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                textImageMapMarker.setTextColor(defaultMarkerColor);
                inputIsAddressModified.setText("true");
                isUpdated = true;
            }
        };

        applyWatcher.addTextChangedListener(addressTextWatcher);

    }

    private boolean isNameValid(String name) {
        String pattern = ".*[a-zA-Z]+.*";
        return name.matches(pattern);
    }

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

        final Calendar myCalendar = Calendar.getInstance();

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
                    updateEditTextEvent(myCalendar, "event");
                } else if (isOrganization) {
                    updateEditTextEvent(myCalendar, "organization");
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

    @SuppressLint("SimpleDateFormat")
    private void updateBirthdayEditText(Calendar myCalendar) {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat(AppConstants.EVENT_ST_DATE_FORMAT);
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat(AppConstants.EVENT_ND_DATE_FORMAT);
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat(AppConstants.EVENT_RD_DATE_FORMAT);
        else
            format = new SimpleDateFormat(AppConstants.EVENT_GENERAL_DATE_FORMAT);

        format.format(myCalendar.getTime());

    }

    @SuppressLint("SimpleDateFormat")
    private void updateAnniversaryEditText(Calendar myCalendar) {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat(AppConstants.EVENT_ST_DATE_FORMAT);
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat(AppConstants.EVENT_ND_DATE_FORMAT);
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat(AppConstants.EVENT_RD_DATE_FORMAT);
        else
            format = new SimpleDateFormat(AppConstants.EVENT_GENERAL_DATE_FORMAT);

        format.format(myCalendar.getTime());

    }

    private void updateEventEditText(EditText editText) {
        final Calendar myCalendar = Calendar.getInstance();
        editTextEvent = editText;
        new DatePickerDialog(EditProfileActivity.this, dataPicker, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//        updateEditTextEvent(myCalendar);
    }

    private void updateOrganizationText(TextView textView) {
        final Calendar myCalendar = Calendar.getInstance();
        organizationDateView = textView;
        new DatePickerDialog(EditProfileActivity.this, dataPicker, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//        updateEditTextEvent(myCalendar);
    }

    @SuppressLint("SimpleDateFormat")
    private void updateEditTextEvent(Calendar myCalendar, String type) {

        SimpleDateFormat format = new SimpleDateFormat("d");
        String date = format.format(myCalendar.getTime());

        if (date.endsWith("1") && !date.endsWith("11"))
            format = new SimpleDateFormat(AppConstants.EVENT_ST_DATE_FORMAT);
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = new SimpleDateFormat(AppConstants.EVENT_ND_DATE_FORMAT);
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = new SimpleDateFormat(AppConstants.EVENT_RD_DATE_FORMAT);
        else
            format = new SimpleDateFormat(AppConstants.EVENT_GENERAL_DATE_FORMAT);

        String yourDate = format.format(myCalendar.getTime());

        if (type.equalsIgnoreCase("event")) {
            if (!TextUtils.isEmpty(yourDate))
                editTextEvent.setText(yourDate);
            isEvent = false;
        } else {
            if (!TextUtils.isEmpty(yourDate))
                organizationDateView.setText(yourDate);
            isOrganization = false;
        }
    }

    private void genderDetails() {
        textFemaleIcon.setTypeface(Utils.typefaceIcons(this));
        textMaleIcon.setTypeface(Utils.typefaceIcons(this));
        textFemaleIcon.setText(getResources().getString(R.string.im_icon_female));
        textMaleIcon.setText(getResources().getString(R.string.im_icon_male));

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
            phoneNumber.setPbRcpType(Integer.parseInt(arrayListMobileNumber.get(i)
                    .getMnmIsPrimary()));
            arrayListPhoneNumberObject.add(phoneNumber);
        }
        if (arrayListPhoneNumberObject.size() > 0) {
            for (int i = 0; i < arrayListPhoneNumberObject.size(); i++) {
                addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, arrayListPhoneNumberObject
                        .get(i), i);
            }
            for (int i = 0; i < linearPhoneDetails.getChildCount(); i++) {
                View linearPhone = linearPhoneDetails.getChildAt(i);
                EditText emailId = linearPhone.findViewById(R.id.input_value);
                emailId.addTextChangedListener(valueTextWatcher);
            }
//            inputValue.addTextChangedListener(valueTextWatcher);
        } else {
            addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, null, -1);
        }
    }

    private void emailDetails() {

        arrayListSocialEmail = new ArrayList<>();

        TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);

        ArrayList<Email> arrayListEmail = tableEmailMaster.getEmailsFromPmId(Integer.parseInt
                (getUserPmId()));
        arrayListEmailObject = new ArrayList<>();
        for (int i = 0; i < arrayListEmail.size(); i++) {
            ProfileDataOperationEmail email = new ProfileDataOperationEmail();
            email.setEmEmailId(arrayListEmail.get(i).getEmEmailAddress());
            email.setEmSocialType(arrayListEmail.get(i).getEmSocialType());
            email.setEmType(arrayListEmail.get(i).getEmEmailType());
            email.setEmId(arrayListEmail.get(i).getEmRecordIndexId());
            email.setEmPublic(Integer.parseInt(arrayListEmail.get(i).getEmEmailPrivacy()));
            email.setEmRcpType(Integer.parseInt(arrayListEmail.get(i).getEmIsVerified()));
            arrayListEmailObject.add(email);
            arrayListSocialEmail.add(email);
        }
        if (arrayListEmailObject.size() > 0) {
            for (int i = 0; i < arrayListEmailObject.size(); i++) {
                addView(AppConstants.EMAIL, linearEmailDetails, arrayListEmailObject.get(i), i);
            }
            for (int i = 0; i < linearEmailDetails.getChildCount(); i++) {
                View linearEmail = linearEmailDetails.getChildAt(i);
                EditText emailId = linearEmail.findViewById(R.id.input_value);
                emailId.addTextChangedListener(valueTextWatcher);
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
            for (int i = 0; i < linearWebsiteDetails.getChildCount(); i++) {
                View linearWebsite = linearWebsiteDetails.getChildAt(i);
                EditText Website = linearWebsite.findViewById(R.id.input_value);
                Website.addTextChangedListener(valueTextWatcher);
            }
        } else {
            addView(AppConstants.WEBSITE, linearWebsiteDetails, null, -1);
        }

    }

    private void socialContactDetails() {

        socialTypeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                .array.types_social_media)));

        TableImMaster tableImMaster = new TableImMaster(databaseHandler);

        ArrayList<ImAccount> arrayListImAccount = tableImMaster.getImAccountFromPmId(Integer
                .parseInt(getUserPmId()));
        arrayListSocialContactObject = new ArrayList<>();
        for (int i = 0; i < arrayListImAccount.size(); i++) {
            ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
            imAccount.setIMAccountProtocol(arrayListImAccount.get(i).getImImProtocol());
            imAccount.setIMAccountDetails(arrayListImAccount.get(i).getImImDetail());
            imAccount.setIMAccountFirstName(arrayListImAccount.get(i).getImImFirstName());
            imAccount.setIMAccountLastName(arrayListImAccount.get(i).getImImLastName());
            imAccount.setIMAccountProfileImage(arrayListImAccount.get(i).getImImProfileImage());
            imAccount.setIMId(arrayListImAccount.get(i).getImRecordIndexId());
            imAccount.setIMAccountPublic(Integer.parseInt(arrayListImAccount.get(i)
                    .getImImPrivacy()));
            arrayListSocialContactObject.add(imAccount);

            if (arrayListImAccount.get(i).getImImProtocol().equalsIgnoreCase(getString(R.string
                    .facebook))
                    || arrayListImAccount.get(i).getImImProtocol().equalsIgnoreCase(getString(R
                    .string.google_plus))
                    || arrayListImAccount.get(i).getImImProtocol().equalsIgnoreCase(getString(R
                    .string.linked_in))) {
                socialTypeList.remove(arrayListImAccount.get(i).getImImProtocol());
            }
        }

        if (arrayListSocialContactObject.size() > 0) {
            for (int i = 0; i < arrayListSocialContactObject.size(); i++) {
                addSocialConnectView(arrayListSocialContactObject.get(i), "");
            }
            for (int i = 0; i < linearSocialContactDetails.getChildCount(); i++) {
                View linearSocialContact = linearSocialContactDetails.getChildAt(i);
                EditText socialContact = linearSocialContact.findViewById(R.id
                        .input_value);
                socialContact.addTextChangedListener(valueTextWatcher);
            }
        }
//        else {
//            addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails, null, -1);
//        }
    }

    private void eventDetails() {
        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        ArrayList<Event> arrayListEvent = tableEventMaster.getEventsFromPmId(Integer.parseInt
                (getUserPmId()));

        arrayListEventObject = new ArrayList<>();
        for (int i = 0; i < arrayListEvent.size(); i++) {

            /*String formattedDate = Utils.convertDateFormat(arrayListEvent.get(i).getEvmStartDate
                    (), "yyyy-MM-dd hh:mm:ss", getEventDateFormat(arrayListEvent.get(i)
                    .getEvmStartDate()));*/
            String formattedDate = Utils.convertDateFormat(arrayListEvent.get(i).getEvmStartDate
                    (), "yyyy-MM-dd hh:mm:ss", getEventDateFormat(arrayListEvent.get(i)
                    .getEvmStartDate()));

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
                EditText event = linearEvent.findViewById(R.id.input_value);
                event.addTextChangedListener(valueTextWatcher);
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

            String formattedFromDate = "", formattedToDate = "";

            ProfileDataOperationOrganization organization = new ProfileDataOperationOrganization();
            organization.setOrgName(arrayListOrganization.get(i).getOmOrganizationCompany());

            if (!StringUtils.isEmpty(arrayListOrganization.get(i).getOmOrganizationFromDate())) {
                formattedFromDate = Utils.convertDateFormat(arrayListOrganization.get(i)
                                .getOmOrganizationFromDate(),
                        "yyyy-MM-dd", getEventDateFormat(arrayListOrganization.get(i)
                                .getOmOrganizationFromDate()));
            }
            if (!StringUtils.isEmpty(arrayListOrganization.get(i).getOmOrganizationToDate())) {
                formattedToDate = Utils.convertDateFormat(arrayListOrganization.get(i)
                                .getOmOrganizationToDate(),
                        "yyyy-MM-dd", getEventDateFormat(arrayListOrganization.get(i)
                                .getOmOrganizationToDate()));
            }

            organization.setOrgFromDate(formattedFromDate);
            organization.setOrgToDate(formattedToDate);
            organization.setOrgJobTitle(arrayListOrganization.get(i).getOmOrganizationDesignation
                    ());
            organization.setOrgId(arrayListOrganization.get(i).getOmRecordIndexId());
            organization.setOrgEntId(arrayListOrganization.get(i).getOmEnterpriseOrgId());
            organization.setOrgLogo(arrayListOrganization.get(i).getOmOrganizationLogo());
            organization.setOrgRcpType(arrayListOrganization.get(i).getOmOrganizationLogo());
            organization.setIsCurrent(Integer.parseInt(arrayListOrganization.get(i)
                    .getOmIsCurrent()));
            organization.setOrgIndustryType(arrayListOrganization.get(i).getOmOrganizationType());

            if (StringUtils.length(arrayListOrganization.get(i).getOmIsVerified()) > 0) {
                organization.setIsVerify(Integer.parseInt(arrayListOrganization.get(i)
                        .getOmIsVerified()));
            } else {
                organization.setIsVerify(0);
            }

            arrayListOrganizationObject.add(organization);
        }

        if (arrayListOrganizationObject.size() > 0) {
            for (int i = 0; i < arrayListOrganizationObject.size(); i++) {
                addOrganizationView(i, arrayListOrganizationObject.get(i));
            }
            EditText inputCompanyName = linearOrganizationDetails.findViewById(R.id
                    .input_company_name);
            EditText inputDesignationName = linearOrganizationDetails.findViewById(R.id
                    .input_designation_name);
            inputCompanyName.addTextChangedListener(valueTextWatcher);
            inputDesignationName.addTextChangedListener(valueTextWatcher);
        } else {
            addOrganizationView((arrayListOrganizationObject.size()), null);
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
            address.setCountryId(arrayListAddress.get(i).getAmCountryId());
            address.setState(arrayListAddress.get(i).getAmState());
            address.setStateId(arrayListAddress.get(i).getAmStateId());
            address.setCity(arrayListAddress.get(i).getAmCity());
            address.setCityId(arrayListAddress.get(i).getAmCityId());
            address.setStreet(arrayListAddress.get(i).getAmStreet());
            address.setFormattedAddress(arrayListAddress.get(i).getAmFormattedAddress());
            address.setNeighborhood(arrayListAddress.get(i).getAmNeighborhood());
            address.setPostCode(arrayListAddress.get(i).getAmPostCode());
            address.setAddressType(arrayListAddress.get(i).getAmAddressType());
            ArrayList<String> arrayListLatLong = new ArrayList<>();
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLongitude());
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLatLong(arrayListLatLong);
            /*arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLongitude());
            arrayListLatLong.add(arrayListAddress.get(i).getAmGoogleLatitude());
            address.setGoogleLatLong(arrayListLatLong);*/
            address.setGoogleAddress(arrayListAddress.get(i).getAmGoogleAddress());
            address.setAddId(arrayListAddress.get(i).getAmRecordIndexId());
            address.setAddPublic(Integer.parseInt(arrayListAddress.get(i).getAmAddressPrivacy()));
            arrayListAddressObject.add(address);
        }

        if (arrayListAddressObject.size() > 0) {
            for (int i = 0; i < arrayListAddressObject.size(); i++) {
                addAddressView(arrayListAddressObject.get(i), i);
            }
            for (int i = 0; i < linearAddressDetails.getChildCount(); i++) {
                View linearAddress = linearAddressDetails.getChildAt(i);
                /*EditText inputCountry = linearAddress.findViewById(R.id.input_country);
                EditText inputState = linearAddress.findViewById(R.id.input_state);*/
                EditText inputCity = linearAddress.findViewById(R.id.input_city);
                EditText inputStreet = linearAddress.findViewById(R.id.input_street);
                EditText inputNeighborhood = linearAddress.findViewById(R.id.input_neighborhood);
                EditText inputPinCode = linearAddress.findViewById(R.id.input_pin_code);
                TextView inputIsAddressModified = linearAddress.findViewById(R.id
                        .input_is_address_modified);
                TextView textImageMapMarker = linearAddress.findViewById(R.id
                        .text_image_map_marker);
                /*setAddressTextWatcher(inputCountry, textImageMapMarker, inputIsAddressModified);
                setAddressTextWatcher(inputState, textImageMapMarker, inputIsAddressModified);*/
                setAddressTextWatcher(inputCity, textImageMapMarker, inputIsAddressModified);
                setAddressTextWatcher(inputStreet, textImageMapMarker, inputIsAddressModified);
                setAddressTextWatcher(inputNeighborhood, textImageMapMarker,
                        inputIsAddressModified);
                setAddressTextWatcher(inputPinCode, textImageMapMarker, inputIsAddressModified);
            }
        } else {
            addAddressView(null, 0);
        }
    }

    private void checkBeforeAddressViewAdd() {
        boolean toAdd = false;
        for (int i = 0; i < linearAddressDetails.getChildCount(); i++) {
            View linearView = linearAddressDetails.getChildAt(i);
            /*EditText inputCountry = linearView.findViewById(R.id.input_country);
            EditText inputState = linearView.findViewById(R.id.input_state);*/
            EditText inputCity = linearView.findViewById(R.id.input_city);
            EditText inputStreet = linearView.findViewById(R.id.input_street);
            TextView inputLatitude = linearView.findViewById(R.id.input_latitude);
            TextView inputLongitude = linearView.findViewById(R.id.input_longitude);
            if (StringUtils.length(StringUtils.trimToEmpty(inputCity.getText().toString()))
                    < 1 ||
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
        boolean toAdd = false, isChecked;
        for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
            View linearView = linearOrganizationDetails.getChildAt(i);
            EditText inputCompanyName = linearView.findViewById(R.id.input_company_name);
            EditText inputDesignationName = linearView.findViewById(R.id.input_designation_name);
            CheckBox checkboxOrganization = linearView.findViewById(R.id.checkbox_organization);
            EditText inputFromDate = linearView.findViewById(R.id.input_from_date);
            EditText inputToDate = linearView.findViewById(R.id.input_to_date);

            isChecked = checkboxOrganization.isChecked();

            if (StringUtils.length(StringUtils.trimToEmpty(inputCompanyName.getText()
                    .toString())) < 1 || StringUtils.length(StringUtils.trimToEmpty
                    (inputDesignationName.getText()
                            .toString())) < 1 || StringUtils.length(StringUtils.trimToEmpty
                    (inputFromDate
                            .getText()
                            .toString())) < 1) {
                if (isChecked && StringUtils.length(StringUtils.trimToEmpty(inputToDate.getText()
                        .toString())) < 1) {
                    toAdd = false;
                    break;
                } else {
                    toAdd = false;
                    break;
                }
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            isUpdated = true;
            addOrganizationDetailsToList();
            addOrganizationView((arrayListOrganizationObject.size()), null);
        }
    }

    // TODO : Hardik
    private void addOrganizationDetailsToList() {

        arrayListOrganizationObject.clear();

        for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
            ProfileDataOperationOrganization organization = new ProfileDataOperationOrganization();

            View linearOrganization = linearOrganizationDetails.getChildAt(i);
            EditText inputCompanyName = linearOrganization.findViewById(R.id
                    .input_company_name);
            EditText inputDesignationName = linearOrganization.findViewById(R.id
                    .input_designation_name);

            TextView textOrgLogo = linearOrganization.findViewById(R.id.text_org_logo);
            TextView textEnterpriseOrgId = linearOrganization.findViewById(R.id
                    .text_enterprise_org_id);
            TextView textIsVerified = linearOrganization.findViewById(R.id.text_is_verified);
            TextView textOrgName = linearOrganization.findViewById(R.id.text_org_name);
            TextView textOrgType = linearOrganization.findViewById(R.id.text_org_type);

            EditText inputFromDate = linearOrganization.findViewById(R.id.input_from_date);
            EditText inputToDate = linearOrganization.findViewById(R.id.input_to_date);

            RelativeLayout relativeRowEditProfile = linearOrganization
                    .findViewById(R.id.relative_row_edit_profile);
            CheckBox checkboxOrganization = linearOrganization.findViewById(R
                    .id.checkbox_organization);

            organization.setOrgIndustryType(textOrgType.getText().toString().trim());
            organization.setOrgName(textOrgName.getText().toString().trim());
            organization.setOrgJobTitle(inputDesignationName.getText().toString().trim());
            organization.setOrgEntId(textEnterpriseOrgId.getText().toString().trim());
            organization.setOrgLogo(textOrgLogo.getText().toString().trim());

            if (StringUtils.length(textIsVerified.getText().toString()) > 0) {
                organization.setIsVerify(Integer.parseInt(textIsVerified.getText()
                        .toString().trim()));
            } else {
                organization.setIsVerify(0);
            }

            organization.setOrgId((String) relativeRowEditProfile.getTag());
            organization.setIsCurrent(checkboxOrganization.isChecked() ? 1 : 0);
            organization.setOrgPublic(IntegerConstants.PRIVACY_EVERYONE);

            if (!StringUtils.isBlank(inputToDate.getText().toString().trim())) {
                organization.setOrgToDate(inputToDate.getText().toString().trim());

            } else {
                organization.setOrgToDate("");
            }

            if (!StringUtils.isBlank(inputFromDate.getText().toString().trim())) {
                organization.setOrgFromDate(inputFromDate.getText().toString().trim());
            } else {
                organization.setOrgFromDate("");
            }

            arrayListOrganizationObject.add(organization);
        }
    }

    private void checkBeforeSocialViewAdd(String imAccountProtocol) {
        boolean toAdd = false;
        for (int i = 0; i < linearSocialContactDetails.getChildCount(); i++) {
            View linearView = linearSocialContactDetails.getChildAt(i);
            EditText editText = linearView.findViewById(R.id.input_value);

            if (StringUtils.length(StringUtils.trimToEmpty(editText.getText().toString())) < 1) {
                toAdd = false;
                break;
            } else {
                toAdd = true;
            }
        }
        if (toAdd) {
            isUpdated = true;
            addSocialConnectView(null, imAccountProtocol);
        }
    }

    private void checkBeforeViewAdd(int viewType, LinearLayout linearLayout) {
        boolean toAdd = false;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View linearView = linearLayout.getChildAt(i);
            EditText editText = linearView.findViewById(R.id.input_value);
            if (StringUtils.length(StringUtils.trimToEmpty(editText.getText().toString())) < 1) {
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

    @SuppressLint("InflateParams")
    private void addView(int viewType, final LinearLayout linearLayout, Object detailObject, int
            position) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile, null);
        ImageView imageViewDelete = view.findViewById(R.id.image_delete);
        final Spinner spinnerType = view.findViewById(R.id.spinner_type);
//        final EditText inputValue = (EditText) view.findViewById(R.id.input_value);
        final EditText inputValue = view.findViewById(R.id.input_value);
        LinearLayout linerCheckbox = view.findViewById(R.id.liner_checkbox);
        final CheckBox checkboxHideYear = view.findViewById(R.id.checkbox_hide_year);
        TextView textLabelCheckbox = view.findViewById(R.id.text_label_checkbox);
        TextView textIsPublic = view.findViewById(R.id.text_is_public);
        TextView textIsSocial = view.findViewById(R.id.text_is_social);
        TextView textIsVerified = view.findViewById(R.id.text_is_verified);
        ImageView imageViewCalender = view.findViewById(R.id.image_calender);
        imageViewCalender.setVisibility(View.GONE);
        final RelativeLayout relativeRowEditProfile = view.findViewById(R.id
                .relative_row_edit_profile);

        inputValue.setTypeface(Utils.typefaceRegular(this));
        textLabelCheckbox.setTypeface(Utils.typefaceLight(this));

        List<String> typeList;

        switch (viewType) {

            //<editor-fold desc="PHONE_NUMBER">
            case AppConstants.PHONE_NUMBER:
                linerCheckbox.setVisibility(View.GONE);
                imageViewDelete.setTag(AppConstants.PHONE_NUMBER);
                inputValue.setHint(R.string.hint_number);
                spinnerType.setTag(R.id.spinner_type, AppConstants.PHONE_NUMBER);
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
                        inputValue.setTypeface(Utils.typefaceIcons(EditProfileActivity.this));
                        inputValue.setEnabled(false);
                        spinnerType.setVisibility(View.GONE);
//                        textImageCross.setVisibility(View.INVISIBLE);
                        imageViewDelete.setVisibility(View.GONE);
                    }
                    ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                            detailObject;
                    if (position == 0) {
                        inputValue.setText(Utils.setMultipleTypeface(EditProfileActivity.this,
                                phoneNumber.getPhoneNumber() + " " + getString(R.string
                                        .im_icon_verify), 0, (StringUtils.length(phoneNumber
                                        .getPhoneNumber()) + 1), ((StringUtils.length(phoneNumber
                                        .getPhoneNumber()) + 1) + 1)));
                    } else {
                        inputValue.setText(phoneNumber.getPhoneNumber());
                    }
                    textIsPublic.setText(String.valueOf(phoneNumber.getPhonePublic()));
                    textIsVerified.setText(String.valueOf(phoneNumber.getPbRcpType()));
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
                    spinnerType.setTag(R.id.spinner_position, spinnerPosition);
                    relativeRowEditProfile.setTag(phoneNumber.getPhoneId());
                }
                break;
            //</editor-fold>

            //<editor-fold desc="EMAIL">
            case AppConstants.EMAIL:
                linerCheckbox.setVisibility(View.GONE);
                imageViewDelete.setTag(AppConstants.EMAIL);
                inputValue.setHint(R.string.hint_email);
                spinnerType.setTag(R.id.spinner_type, AppConstants.EMAIL);
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

                    if (email.getEmRcpType() == IntegerConstants.RCP_TYPE_PRIMARY) {
                        inputValue.setTypeface(Utils.typefaceIcons(EditProfileActivity.this));
                        inputValue.setEnabled(false);
                        spinnerType.setVisibility(View.GONE);
                        imageViewDelete.setVisibility(View.GONE);
                       /* inputValue.setText(String.format("%s %s", email.getEmEmailId(),
                                getString(R.string.im_icon_verify)));*/
                        inputValue.setText(Utils.setMultipleTypeface(EditProfileActivity.this,
                                email.getEmEmailId() + " " + getString(R.string.im_icon_verify),
                                0, (StringUtils.length(email.getEmEmailId()) + 1), ((StringUtils
                                        .length(email.getEmEmailId()) + 1) + 1)));

                    } else if (email.getEmRcpType() == IntegerConstants.RCP_TYPE_SECONDARY &&
                            !email.getEmSocialType().equalsIgnoreCase("")) {
                        inputValue.setTypeface(Utils.typefaceIcons(EditProfileActivity.this));
                        inputValue.setEnabled(false);
                        spinnerType.setVisibility(View.GONE);
                        imageViewDelete.setVisibility(View.GONE);
                       /* inputValue.setText(String.format("%s %s", email.getEmEmailId(),
                                getString(R.string.im_icon_verify)));*/

                        String s = Utils.setMultipleTypeface(EditProfileActivity.this, email
                                        .getEmEmailId() + " <font color" +
                                        "='#00bfff'>" + getString(R.string.im_icon_verify) +
                                        "</font>",
                                0, (StringUtils.length
                                        (email.getEmEmailId()) + 1), ((StringUtils.length(email
                                        .getEmEmailId()) + 1) + 1)).toString();

                        inputValue.setText(Html.fromHtml(s));
                    } else {
                        inputValue.setText(email.getEmEmailId());
                    }

                    textIsPublic.setText(String.valueOf(email.getEmPublic()));
                    textIsVerified.setText(String.valueOf(email.getEmRcpType()));
                    textIsSocial.setText(String.valueOf(email.getEmSocialType()));

                    int spinnerPosition;
                    if (typeList.contains(StringUtils.defaultString(email.getEmType()))) {
                        spinnerPosition = spinnerEmailAdapter.getPosition(email.getEmType());
                    } else {
                        spinnerEmailAdapter.add(email.getEmType());
                        spinnerEmailAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerEmailAdapter.getPosition(email.getEmType());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    spinnerType.setTag(R.id.spinner_position, spinnerPosition);
                    relativeRowEditProfile.setTag(email.getEmId());
                }
                break;
            //</editor-fold>

            //<editor-fold desc="WEBSITE">
            case AppConstants.WEBSITE:
                linerCheckbox.setVisibility(View.GONE);
                imageViewDelete.setTag(AppConstants.WEBSITE);
                inputValue.setHint(getString(R.string.hint_web_address));
                spinnerType.setTag(R.id.spinner_type, AppConstants.WEBSITE);
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
                        spinnerWebsiteAdapter.add(webAddress.getWebType());
                        spinnerWebsiteAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerWebsiteAdapter.getPosition(webAddress.getWebType
                                ());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    spinnerType.setTag(R.id.spinner_position, spinnerPosition);
                    relativeRowEditProfile.setTag(webAddress.getWebId());
                }
                break;
            //</editor-fold>

            //<editor-fold desc="EVENT">
            case AppConstants.EVENT:
                linerCheckbox.setVisibility(View.GONE);
                imageViewDelete.setTag(AppConstants.EVENT);
                inputValue.setHint(R.string.hint_choose_date);
                inputValue.setFocusable(false);
                spinnerType.setTag(R.id.spinner_type, AppConstants.EVENT);
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
                        spinnerEventAdapter.add(event.getEventType());
                        spinnerEventAdapter.notifyDataSetChanged();
                        spinnerPosition = spinnerEventAdapter.getPosition(event.getEventType());
                    }
                    spinnerType.setSelection(spinnerPosition);
                    spinnerType.setTag(R.id.spinner_position, spinnerPosition);
                    relativeRowEditProfile.setTag(event.getEventId());
                }

                imageViewCalender.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isEvent = true;
                        updateEventEditText(inputValue);
                    }
                });
                break;
            //</editor-fold>

            default:
                getResources().getStringArray(R.array.types_email_address);
                break;
        }

        //<editor-fold desc="imageViewDelete Click">
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isUpdated = true;

                switch ((Integer) v.getTag()) {

                    case AppConstants.PHONE_NUMBER:
                        if (linearPhoneDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearPhoneDetails.getChildCount() == 1) {
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.EMAIL:
                        if (linearEmailDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearEmailDetails.getChildCount() == 1) {
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.WEBSITE:
                        if (linearWebsiteDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearWebsiteDetails.getChildCount() == 1) {
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.EVENT:
                        if (linearEventDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearEventDetails.getChildCount() == 1) {
                            inputValue.getText().clear();
                        }
                        break;

                    case AppConstants.IM_ACCOUNT:
                        if (linearSocialContactDetails.getChildCount() > 1) {
                            linearLayout.removeView(relativeRowEditProfile);
                        } else if (linearSocialContactDetails.getChildCount() == 1) {
                            inputValue.getText().clear();
                        }
                        break;
                }
            }
        });
        //</editor-fold>

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (((Integer) spinnerType.getTag(R.id.spinner_position)) == position) {
                if (spinnerType.getTag(R.id.spinner_position) != null && ((Integer) spinnerType
                        .getTag(R.id.spinner_position)) == position) {
                    return;
                }
                spinnerType.setTag(R.id.spinner_position, position);
                String items = spinnerType.getSelectedItem().toString();
                if (items.equalsIgnoreCase(getString(R.string.text_custom))) {
                    showCustomTypeDialog(spinnerType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        linearLayout.addView(view);
    }

    @SuppressLint("InflateParams")
    private void addSocialConnectView(Object detailObject, String imAccountProtocol) {

        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_social, null);
        ImageView imageViewDelete = view.findViewById(R.id.image_delete);
        ImageView imageViewSocialIcon = view.findViewById(R.id.image_social_icon);
        ImageView imageViewSocialProfile = view.findViewById(R.id.image_social_profile);
        imageViewSocialProfile.setVisibility(View.GONE);

        LinearLayout linearContent = view.findViewById(R.id.linear_content);

        final EditText inputValue = view.findViewById(R.id.input_value);
        TextView textFirstName = view.findViewById(R.id.text_first_name);
        TextView textLastName = view.findViewById(R.id.text_last_name);
        TextView textIsVerified = view.findViewById(R.id.text_is_verified);
        TextView textProtocol = view.findViewById(R.id.input_protocol);
        TextView imAccountProfileImage = view.findViewById(R.id.text_profile_image);
        TextView textIsPublic = view.findViewById(R.id.text_is_public);

        textIsVerified.setText(R.string.verify_now);
        textIsVerified.setVisibility(View.GONE);

        final RelativeLayout relativeRowEditProfileSocial = view.findViewById(R.id
                .relative_row_edit_profile_social);

        imageViewDelete.setTag(AppConstants.IM_ACCOUNT);
        inputValue.setHint(R.string.hint_account_name);
        inputValue.setTypeface(Utils.typefaceIcons(EditProfileActivity.this));

        inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
        if (detailObject != null) {
            ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount)
                    detailObject;

            inputValue.setText(imAccount.getIMAccountDetails());
            textProtocol.setText(imAccount.getIMAccountProtocol());
            textFirstName.setText(imAccount.getIMAccountFirstName());
            textLastName.setText(imAccount.getIMAccountLastName());
            textIsPublic.setText(String.valueOf(imAccount.getIMAccountPublic()));

            if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string.facebook))
                    || imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .google_plus))
                    || imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .linked_in))) {

                inputValue.setEnabled(false);
                linearContent.setVisibility(View.GONE);
                imageViewSocialProfile.setVisibility(View.VISIBLE);

                imAccountProfileImage.setText(imAccount.getIMAccountProfileImage());

                Glide.with(EditProfileActivity.this)
                        .load(imAccount.getIMAccountProfileImage())
                        .error(R.drawable.home_screen_profile)
                        .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                        .override(120, 120)
                        .into(imageViewSocialProfile);

            } else {
                linearContent.setVisibility(View.VISIBLE);
            }

            if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string.facebook))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_facebook_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .google_plus))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_google_plus_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .linked_in))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_linkedin_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .twitter))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_twitter_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .instagram))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_instagram_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .pinterest))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_pinterest_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .other))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_other_svg);
            } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase(getString(R.string
                    .custom))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_other_svg);
            } else {
                imageViewSocialIcon.setImageResource(R.drawable.ico_other_svg);
            }

            relativeRowEditProfileSocial.setTag(imAccount.getIMId());
        } else {

            textProtocol.setText(imAccountProtocol);
            imAccountProfileImage.setText("");
            imageViewSocialProfile.setVisibility(View.GONE);

            if (imAccountProtocol.equalsIgnoreCase(getString(R.string.facebook))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_facebook_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.google_plus))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_google_plus_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.linked_in))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_linkedin_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.twitter))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_twitter_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.instagram))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_instagram_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.pinterest))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_pinterest_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.other))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_other_svg);
            } else if (imAccountProtocol.equalsIgnoreCase(getString(R.string.custom))) {
                imageViewSocialIcon.setImageResource(R.drawable.ico_other_svg);
            } else {
                imageViewSocialIcon.setImageResource(R.drawable.ico_other_svg);
            }
        }

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isUpdated = true;

                if (linearSocialContactDetails.getChildCount() > 1) {
                    linearSocialContactDetails.removeView(relativeRowEditProfileSocial);

                    TextView textProtocol = relativeRowEditProfileSocial.findViewById(R.id
                            .input_protocol);

                    if (textProtocol != null) {
                        if (textProtocol.getText().toString().trim().equalsIgnoreCase(getString(R
                                .string.facebook))) {
                            socialTypeList.add(getString(R.string.facebook));
                        } else if (textProtocol.getText().toString().trim().equalsIgnoreCase
                                (getString(R.string.google_plus))) {
                            socialTypeList.add(getString(R.string.google_plus));
                        } else if (textProtocol.getText().toString().trim().equalsIgnoreCase
                                (getString(R.string.linked_in))) {
                            socialTypeList.add(getString(R.string.linked_in));
                        }
                    }

                } else if (linearSocialContactDetails.getChildCount() == 1) {
                    inputValue.getText().clear();

                    View view = linearSocialContactDetails.getChildAt(0);
                    TextView textProtocol = view.findViewById(R.id.input_protocol);

                    if (textProtocol != null) {
                        if (textProtocol.getText().toString().trim().equalsIgnoreCase(getString(R
                                .string.facebook))) {
                            socialTypeList.add(getString(R.string.facebook));
                        } else if (textProtocol.getText().toString().trim().equalsIgnoreCase
                                (getString(R.string.google_plus))) {
                            socialTypeList.add(getString(R.string.google_plus));
                        } else if (textProtocol.getText().toString().trim().equalsIgnoreCase
                                (getString(R.string.linked_in))) {
                            socialTypeList.add(getString(R.string.linked_in));
                        }
                    }
                }
            }
        });
        //</editor-fold>

        linearSocialContactDetails.addView(view);
    }

    @SuppressLint("InflateParams")
    private void addOrganizationView(int position, Object detailObject) {
        View view = LayoutInflater.from(this).inflate(R.layout
                .list_item_my_profile_edit_organization, null);
        ImageView deleteOrganization = view.findViewById(R.id.deleteOrganization);
        final EditText inputCompanyName = view.findViewById(R.id.input_company_name);
        final EditText inputDesignationName = view.findViewById(R.id.input_designation_name);
        final CheckBox checkboxOrganization = view.findViewById(R.id
                .checkbox_organization);

        final ImageView imageOrgProfile = view.findViewById(R.id.image_org_profile);
        final TextView textOrgLogo = view.findViewById(R.id.text_org_logo);
        final TextView textEnterpriseOrgId = view.findViewById(R.id.text_enterprise_org_id);
        TextView textIsVerified = view.findViewById(R.id.text_is_verified);
        final TextView textOrgName = view.findViewById(R.id.text_org_name);
        TextView textOrgType = view.findViewById(R.id.text_org_type);

        final EditText inputFromDate = view.findViewById(R.id.input_from_date);
        final EditText inputToDate = view.findViewById(R.id.input_to_date);

        inputFromDate.setHint(R.string.hint_choose_from_date);
        inputFromDate.setFocusable(false);

        inputToDate.setHint(R.string.hint_choose_to_date);
        inputToDate.setFocusable(false);

        final ImageView imageFromDate = view.findViewById(R.id.image_from_date);
        final ImageView imageToDate = view.findViewById(R.id.image_to_date);

        checkboxOrganization.setTag(linearOrganizationDetails.getChildCount());

        final RelativeLayout relativeRowEditProfile = view.findViewById(R.id
                .relative_row_edit_profile);

        inputCompanyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);
        inputDesignationName.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);

        inputCompanyName.setTypeface(Utils.typefaceIcons(this));
        inputDesignationName.setTypeface(Utils.typefaceRegular(this));
        inputCompanyName.setFocusable(false);

        inputCompanyName.setTag(position);
        inputCompanyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                organisationPosition = (int) view.getTag();
                addOrganizationDetailsToList();

                Intent intent = new Intent(EditProfileActivity.this,
                        EnterPriseOrganizationListActivity.class);
                startActivityForResult(intent, 201);// Activity is started with requestCode
            }
        });

        ProfileDataOperationOrganization organization = (ProfileDataOperationOrganization)
                detailObject;

        if (detailObject != null) {

            relativeRowEditProfile.setTag(organization.getOrgId());
            textOrgName.setText(organization.getOrgName());
            textOrgType.setText(organization.getOrgIndustryType());
            inputDesignationName.setText(organization.getOrgJobTitle());
            checkboxOrganization.setChecked(organization.getIsCurrent() == 1);
            textOrgLogo.setText(organization.getOrgLogo());
            textEnterpriseOrgId.setText(organization.getOrgEntId());

            if (MoreObjects.firstNonNull(organization.getIsVerify(), 0) == IntegerConstants
                    .RCP_TYPE_PRIMARY) {

                String s = Utils.setMultipleTypeface(EditProfileActivity.this, organization
                                .getOrgName() + " <font color" +
                                "='#00796B'>" + getString(R.string.im_icon_verify) +
                                "</font><br>", 0,
                        (StringUtils.length
                                (organization.getOrgName()) + 1), ((StringUtils.length(organization
                                .getOrgName()) + 1) + 1)).toString();

                inputCompanyName.setText(Html.fromHtml(s));
                textIsVerified.setText(String.valueOf(organization.getIsVerify()));

            } else {
                inputCompanyName.setText(Html.fromHtml("<font color='#00796B'> " + organization
                        .getOrgName() + "</font>"));
//                inputCompanyName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable
// .ico_relation_single_tick_svg, 0);
                textIsVerified.setText(String.valueOf(0));
            }

            Glide.with(EditProfileActivity.this)
                    .load(organization.getOrgLogo())
                    .placeholder(R.drawable.default_org)
                    .error(R.drawable.default_org)
                    .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                    .override(120, 120)
                    .into(imageOrgProfile);

            if (organization.getIsCurrent() == 1) {
                inputToDate.setEnabled(false);
                imageToDate.setEnabled(false);
                inputFromDate.setText(organization.getOrgFromDate());
            } else {
                inputFromDate.setText(organization.getOrgFromDate());
                inputToDate.setText(organization.getOrgToDate());
            }

        } else {
            checkboxOrganization.setChecked(true);
            inputToDate.setEnabled(false);
            imageToDate.setEnabled(false);

            Glide.with(EditProfileActivity.this)
                    .load(R.drawable.default_org)
                    .bitmapTransform(new CropCircleTransformation(EditProfileActivity.this))
                    .override(120, 120)
                    .into(imageOrgProfile);
        }

        checkboxOrganization.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < linearOrganizationDetails.getChildCount(); i++) {
                        View view = linearOrganizationDetails.getChildAt(i);
                        CheckBox checkbox = view.findViewById(R.id
                                .checkbox_organization);
                        if (checkbox != null) {

                            inputToDate.setEnabled(false);
                            imageToDate.setEnabled(false);

                            inputToDate.setText("");
                            inputToDate.setHint(R.string.hint_choose_to_date);
                        }
                    }
                } else {

                    inputToDate.setEnabled(true);
                    imageToDate.setEnabled(true);
                }
            }
        });

        deleteOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdated = true;
                if (linearOrganizationDetails.getChildCount() > 1) {
                    linearOrganizationDetails.removeView(relativeRowEditProfile);
                    addOrganizationDetailsToList();
                } else if (linearOrganizationDetails.getChildCount() == 1) {
                    inputCompanyName.getText().clear();
                    inputDesignationName.getText().clear();
                    inputFromDate.getText().clear();
                    inputToDate.getText().clear();
                    textOrgName.setText("");
                    checkboxOrganization.setChecked(true);
//                    arrayListOrganizationObject.clear();
                }
            }
        });

        imageFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOrganization = true;
                updateOrganizationText(inputFromDate);
            }
        });

        imageToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOrganization = true;
                updateOrganizationText(inputToDate);
            }
        });

        linearOrganizationDetails.addView(view);
    }

    @SuppressLint("InflateParams")
    private void addAddressView(final Object detailObject, final int position) {

        List<String> typeList;
        final View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_address,
                null);
        ImageView imageViewDelete = view.findViewById(R.id.image_delete);
        final TextView textImageMapMarker = view.findViewById(R.id
                .text_image_map_marker);
        final Spinner spinnerType = view.findViewById(R.id.spinner_type);
        final EditText inputCountry = view.findViewById(R.id.input_country);
        final Spinner spinnerCountry = view.findViewById(R.id.spinner_country);
        final EditText inputState = view.findViewById(R.id.input_state);
        final Spinner spinnerState = view.findViewById(R.id.spinner_state);
        final EditText inputCity = view.findViewById(R.id.input_city);
//        final Spinner spinnerCity = view.findViewById(R.id.spinner_city);
        final EditText inputStreet = view.findViewById(R.id.input_street);
        final EditText inputNeighborhood = view.findViewById(R.id.input_neighborhood);
        final EditText inputPinCode = view.findViewById(R.id.input_pin_code);
        final EditText inputPoBox = view.findViewById(R.id.input_po_box);
        final TextView textLatitude = view.findViewById(R.id.input_latitude);
        final TextView textLongitude = view.findViewById(R.id.input_longitude);
        final TextView textGoogleAddress = view.findViewById(R.id.input_google_address);
        final TextView textIsPublic = view.findViewById(R.id.text_is_public);
        final TextView inputIsAddressModified = view.findViewById(R.id
                .input_is_address_modified);

        final RelativeLayout relativeRowEditProfile = view.findViewById(R.id
                .relative_row_edit_profile);

        inputCity.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputStreet.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        inputNeighborhood.setInputType(InputType.TYPE_CLASS_TEXT | InputType
                .TYPE_TEXT_FLAG_CAP_WORDS);
        inputPinCode.setInputType(InputType.TYPE_CLASS_TEXT);

        textImageMapMarker.setTypeface(Utils.typefaceIcons(this));
        inputStreet.setTypeface(Utils.typefaceRegular(this));
        inputNeighborhood.setTypeface(Utils.typefaceRegular(this));
        inputPinCode.setTypeface(Utils.typefaceRegular(this));
        inputPoBox.setTypeface(Utils.typefaceRegular(this));

//        inputCountry.setHint(R.string.hint_country_required);
//        inputState.setHint(R.string.hint_state_required);
        inputCity.setHint(R.string.hint_city_town_required);
        spinnerCountry.setPromptId(R.string.hint_country_required);
        spinnerState.setPromptId(R.string.hint_state_required);
//        spinnerCity.setPromptId(R.string.hint_city_town_required);
        inputStreet.setHint(R.string.hint_address_line_1_required);
        inputNeighborhood.setHint(R.string.hint_address_line_2_optional);
        inputPinCode.setHint(R.string.hint_pincode_optional);
        inputPoBox.setHint("Po. Box No.");

        inputPoBox.setVisibility(View.GONE);

        defaultMarkerColor = textImageMapMarker.getTextColors();

        spinnerType.setTag(R.id.spinner_type, AppConstants.ADDRESS);
        spinnerCountry.setTag(R.id.spinner_country_position, position);
        spinnerState.setTag(R.id.spinner_state_position, position);

        typeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
                .array.types_email_address)));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout
                .list_item_spinner, typeList);
        spinnerType.setAdapter(spinnerAdapter);

        // REALM
        /*RealmResults<DbCountry> countries = realm.where(DbCountry.class).findAll();
        //fetching the data
        if (countries.load()) {
            Log.i("addAddressView", countries.toString());
        }*/

        final TableCountryMaster tableCountryMaster = new TableCountryMaster(databaseHandler);

        ArrayList<String> countries = new ArrayList<>();
        countries.add(getString(R.string.hint_country_required));
        countries.addAll(tableCountryMaster.getAllCountryName());

        RSpinnerAdapter countrySpinnerAdapter = new RSpinnerAdapter(this, R.layout
                .header_spinner_call_log, countries, ContextCompat
                .getColor(this, R.color.colorAccent), ContextCompat
                .getColor(this, R.color.regularFontColor));
        countrySpinnerAdapter.setDropDownViewResource(R.layout.list_item_spinner_call_log);
        countrySpinnerAdapter.setHintColor(inputStreet.getHintTextColors());
        spinnerCountry.setAdapter(countrySpinnerAdapter);

        final ArrayList<String> arrayListState = new ArrayList<>();
        arrayListState.add(getString(R.string.hint_state_required));

        RSpinnerAdapter stateSpinnerAdapter = new RSpinnerAdapter(this, R.layout
                .header_spinner_call_log, arrayListState, ContextCompat.getColor(this, R
                .color.colorAccent), ContextCompat.getColor(this, R.color
                .regularFontColor));
        stateSpinnerAdapter.setDropDownViewResource(R.layout.list_item_spinner_call_log);
        stateSpinnerAdapter.setHintColor(inputStreet.getHintTextColors());
        spinnerState.setAdapter(stateSpinnerAdapter);

        /*final ArrayList<String> arrayListCity = new ArrayList<>();
        arrayListCity.add(getString(R.string.hint_city_town_required));

        RSpinnerAdapter citySpinnerAdapter = new RSpinnerAdapter(this, R.layout
                .header_spinner_call_log, arrayListCity, ContextCompat.getColor(this, R
                .color.colorAccent), ContextCompat.getColor(this, R.color
                .regularFontColor));
        citySpinnerAdapter.setDropDownViewResource(R.layout.list_item_spinner_call_log);
        citySpinnerAdapter.setHintColor(inputStreet.getHintTextColors());
        spinnerCity.setAdapter(citySpinnerAdapter);*/

        if (detailObject != null) {
            ProfileDataOperationAddress address = (ProfileDataOperationAddress) detailObject;
//            inputCountry.setText(address.getCountry());
//            inputState.setText(address.getState());
            inputCity.setText(address.getCity());

            ArrayList<String> countryList = new ArrayList<>();
            countryList.addAll(tableCountryMaster.getAllCountryName());

            int countryPosition = 0;
            if (countryList.contains(StringUtils.defaultString(address.getCountry()))) {
                countryPosition = countrySpinnerAdapter.getPosition(address.getCountry());
                spinnerCountry.setTag(R.id.spinner_country_id, address.getCountryId());
            }
            spinnerCountry.setSelection(countryPosition);

            arrayListState.add(address.getState());
            spinnerState.setTag(R.id.spinner_state_id, address.getStateId());
            spinnerState.setSelection(stateSpinnerAdapter.getPosition(address.getState()));

//            arrayListCity.add(address.getCity());
//            spinnerCity.setTag(R.id.spinner_city_id, address.getCityId());
//            spinnerCity.setSelection(citySpinnerAdapter.getPosition(address.getCity()));

            inputStreet.setText(address.getStreet());
            inputNeighborhood.setText(address.getNeighborhood());
            inputPinCode.setText(address.getPostCode());
            textLatitude.setText(address.getGoogleLatLong().get(1));
            textLongitude.setText(address.getGoogleLatLong().get(0));
            textGoogleAddress.setText(address.getGoogleAddress());
            textIsPublic.setText(String.valueOf(address.getAddPublic()));
            formattedAddress = address.getFormattedAddress();
            textImageMapMarker.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            int spinnerPosition;
            spinnerAddressAdapter = new ArrayAdapter<>(this, R.layout.list_item_spinner, typeList);
            spinnerType.setAdapter(spinnerAddressAdapter);
            if (typeList.contains(StringUtils.defaultString(address.getAddressType()))) {
                spinnerPosition = spinnerAddressAdapter.getPosition(address.getAddressType());
            } else {
                spinnerAddressAdapter.add(address.getAddressType());
                spinnerAddressAdapter.notifyDataSetChanged();
                spinnerPosition = spinnerAddressAdapter.getPosition(address.getAddressType());
            }
            spinnerType.setSelection(spinnerPosition);
            spinnerType.setTag(R.id.spinner_position, spinnerPosition);
            relativeRowEditProfile.setTag(address.getAddId());
        } else {
//            setAddressTextWatcher(inputCountry, textImageMapMarker, inputIsAddressModified);
//            setAddressTextWatcher(inputState, textImageMapMarker, inputIsAddressModified);
            setAddressTextWatcher(inputCity, textImageMapMarker, inputIsAddressModified);
            setAddressTextWatcher(inputStreet, textImageMapMarker, inputIsAddressModified);
            setAddressTextWatcher(inputNeighborhood, textImageMapMarker, inputIsAddressModified);
            setAddressTextWatcher(inputPinCode, textImageMapMarker, inputIsAddressModified);

        }

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0 && detailObject == null) {
                    /*String state = "", city = "";
                    if (detailObject != null) {
                        ProfileDataOperationAddress address = (ProfileDataOperationAddress)
                                detailObject;
                        state = address.getState();
                        city = address.getCity();
                    }*/
                    String countryId = tableCountryMaster.getCountryIdFromName(spinnerCountry
                            .getSelectedItem().toString()).getCountryId();
                    spinnerCountry.setTag(R.id.spinner_country_id, countryId);
                    /*getStateList(countryId, String.valueOf(spinnerCountry.getTag(R.id
                            .spinner_country_position)));*/
                    textImageMapMarker.setTextColor(defaultMarkerColor);
                    inputIsAddressModified.setText("true");
                    isUpdated = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerState.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (spinnerCountry.getSelectedItemPosition() != 0) {
                        getStateList(tableCountryMaster.getCountryIdFromName(spinnerCountry
                                .getSelectedItem().toString()).getCountryId(), String.valueOf
                                (spinnerCountry.getTag(R.id.spinner_country_position)));
                    }
                }
                return false;
            }
        });

        /*spinnerCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (spinnerState.getSelectedItemPosition() != 0) {
                        getCityList(String.valueOf(spinnerState.getTag(R.id.spinner_state_id)),
                                String.valueOf(spinnerCountry.getTag(R.id
                                        .spinner_country_position)));
                    }
                }
                return false;
            }
        });*/

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdated = true;
                if (linearAddressDetails.getChildCount() > 1) {
                    linearAddressDetails.removeView(relativeRowEditProfile);
                } else if (linearAddressDetails.getChildCount() == 1) {
                   /* inputCountry.getText().clear();
                    inputState.getText().clear();*/
                    inputCity.getText().clear();
                    spinnerCountry.setSelection(0);
                    spinnerState.setSelection(0);
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
                TextView textLatitude = view.findViewById(R.id.input_latitude);
                TextView textLongitude = view.findViewById(R.id.input_longitude);
                /*EditText country = view.findViewById(R.id.input_country);
                EditText state = view.findViewById(R.id.input_state);*/
                EditText city = view.findViewById(R.id.input_city);
                Spinner country = view.findViewById(R.id.spinner_country);
                Spinner state = view.findViewById(R.id.spinner_state);
//                Spinner city = view.findViewById(R.id.spinner_city);
                EditText street = view.findViewById(R.id.input_street);
                EditText neighborhood = view.findViewById(R.id.input_neighborhood);
                EditText pinCode = view.findViewById(R.id.input_pin_code);

                String countryName = country.getSelectedItem().toString();
                String stateName = state.getSelectedItem().toString();
                String cityName = city.getText().toString();
                String streetName = street.getText().toString();
                String neighborhoodName = neighborhood.getText().toString();
                String pinCodeName = pinCode.getText().toString();

                if (!StringUtils.isBlank(countryName) && !StringUtils.isBlank(stateName) &&
                        !StringUtils.isBlank(cityName) && !StringUtils.isBlank(streetName)) {

                    Intent intent = new Intent(EditProfileActivity.this, MapsActivity.class);

                    if (position != -1) {
                  /*  mapLatitude = Double.parseDouble(((ProfileDataOperationAddress)
                            arrayListAddressObject.get(position)).getGoogleLatitude());
                    mapLongitude = Double.parseDouble(((ProfileDataOperationAddress)
                            arrayListAddressObject.get(position)).getGoogleLongitude());
                    intent.putExtra(AppConstants.EXTRA_LATITUDE, mapLatitude);
                    intent.putExtra(AppConstants.EXTRA_LONGITUDE, mapLongitude);*/

//                        if (!isAddressModified) {
                        if (!StringUtils.equalsAnyIgnoreCase(inputIsAddressModified.getText()
                                .toString(), "true")) {
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
                            intent.putExtra(AppConstants.EXTRA_CITY, cityName);
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
                    textImageMapMarker.setTextColor(ContextCompat.getColor(EditProfileActivity
                            .this, R.color.colorSnackBarNegative));
                    Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile,
                            getString(R.string.str_valid_address));
                }
            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerType.getTag(R.id.spinner_position) != null && ((Integer) spinnerType
                        .getTag(R.id.spinner_position)) == position) {
                    return;
                }
                spinnerType.setTag(R.id.spinner_position, position);
                String items = spinnerType.getSelectedItem().toString();
                if (items.equalsIgnoreCase(getString(R.string.text_custom))) {
                    showCustomTypeDialog(spinnerType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        TextView textFromContact = dialog.findViewById(R.id.text_from_contact);
        TextView textFromSocialMedia = dialog.findViewById(R.id.text_from_social_media);
        TextView textRemovePhoto = dialog.findViewById(R.id.text_remove_photo);

        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = dialog.findViewById(R.id.button_left);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFromContact.setTypeface(Utils.typefaceRegular(this));
        textFromSocialMedia.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText(R.string.str_upload_via);
        textFromContact.setText(R.string.str_take_photo);
        textFromSocialMedia.setText(R.string.str_choose_photo);
        textRemovePhoto.setText(R.string.str_remove_photo);

        if (Utils.getStringPreference(this, AppConstants.PREF_USER_PHOTO, "").equals("")) {
            textRemovePhoto.setVisibility(View.GONE);
        } else {
            textRemovePhoto.setVisibility(View.VISIBLE);
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
                if (checkPermissionStorage()) {
                    selectImageFromGallery();
                }
            }
        });

        textFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkPermission()) {
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
                        .into(imageProfile);

                ProfileDataOperation profileDataOperation = new ProfileDataOperation();
                profileDataOperation.setPbProfilePhoto("");
                editProfile(profileDataOperation, AppConstants.PROFILE_IMAGE);
            }
        });

        dialog.show();
    }

    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(EditProfileActivity.this);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = EditProfileActivity.this.getContentResolver().query(contentURI, null,
                null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private boolean checkPermissionStorage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readPermission = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded
                        .size()]), 2);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]
            grantResults) {
        switch (requestCode) {
            case 1:

                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager
                        .PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager
                        .PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                boolean isCamera = perms.get(Manifest.permission.CAMERA) == PackageManager
                        .PERMISSION_GRANTED;
                boolean isStorage = perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
                boolean isStorageWrite = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;

                if (isCamera && isStorage && isStorageWrite)
                    selectImageFromCamera();
                else
                    Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile,
                            getString(R.string.camera_permission));
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    selectImageFromGallery();
                else
                    Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile,
                            getString(R.string.storage_permission));
                break;

            case 3:

                if (fileUri != null && grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(fileUri);
                }
                break;

            case FACEBOOK_LOGIN_PERMISSION:
                facebookLogin();
                break;

            case GOOGLE_LOGIN_PERMISSION:
                googleSignIn();
                break;

            case LINKEDIN_LOGIN_PERMISSION:
                linkedInSignIn();
                break;
        }
    }


    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCamera = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA);
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        1);
                return false;
            } else {
                return true;
            }
        }
        return true;
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
        backConfirmationDialog.setLeftButtonText(getString(R.string.cancel));
        backConfirmationDialog.setRightButtonText(getString(R.string.action_ok));
        backConfirmationDialog.setDialogBody(getString(R.string.confirmation_unsaved_back));

        backConfirmationDialog.showDialog();
    }

    @SuppressWarnings("unused")
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
                SimpleDateFormat sdf = new SimpleDateFormat(AppConstants
                        .EVENT_GENERAL_DATE_FORMAT, Locale.US);
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

        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        final EditText inputCustomName = dialog.findViewById(R.id.input_custom_name);

        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = dialog.findViewById(R.id.button_left);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        Button buttonRight = dialog.findViewById(R.id.button_right);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        inputCustomName.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setTypeface(Utils.typefaceSemiBold(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText(getString(R.string.str_custom_label));

        buttonLeft.setText(R.string.action_cancel);
        buttonRight.setText(R.string.action_ok);

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (!StringUtils.isBlank(inputCustomName.getText().toString())) {
                    Utils.hideSoftKeyboard(EditProfileActivity.this, inputCustomName);
                    dialog.dismiss();
                    ArrayAdapter<String> tempSpinnerAdapter = (ArrayAdapter<String>) spinnerType
                            .getAdapter();
                    switch ((Integer) spinnerType.getTag(R.id.spinner_type)) {
                        case AppConstants.PHONE_NUMBER:
                            /*spinnerPhoneAdapter.add(inputCustomName.getText().toString());
                            spinnerPhoneAdapter.notifyDataSetChanged();
                            spinnerType.setSelection(spinnerPhoneAdapter.getPosition(inputCustomName
                                    .getText().toString()));
                            spinnerType.setTag(R.id.spinner_position, spinnerPhoneAdapter
                                    .getPosition(inputCustomName.getText().toString()));*/

                            tempSpinnerAdapter.add(inputCustomName.getText().toString());
                            tempSpinnerAdapter.notifyDataSetChanged();
                            spinnerType.setSelection(tempSpinnerAdapter.getPosition
                                    (inputCustomName
                                            .getText().toString()));
                            spinnerType.setTag(R.id.spinner_position, tempSpinnerAdapter
                                    .getPosition(inputCustomName.getText().toString()));
                            break;

                        case AppConstants.EMAIL:
                            tempSpinnerAdapter.add(inputCustomName.getText().toString());
                            tempSpinnerAdapter.notifyDataSetChanged();
                            spinnerType.setSelection(tempSpinnerAdapter.getPosition(inputCustomName
                                    .getText().toString()));
                            spinnerType.setTag(R.id.spinner_position, tempSpinnerAdapter
                                    .getPosition(inputCustomName.getText().toString()));
                            break;

                        case AppConstants.WEBSITE:
                            tempSpinnerAdapter.add(inputCustomName.getText().toString());
                            tempSpinnerAdapter.notifyDataSetChanged();
                            spinnerType.setSelection(tempSpinnerAdapter.getPosition
                                    (inputCustomName
                                            .getText().toString()));
                            spinnerType.setTag(R.id.spinner_position, tempSpinnerAdapter
                                    .getPosition(inputCustomName.getText().toString()));
                            break;

                        case AppConstants.EVENT:
                            tempSpinnerAdapter.add(inputCustomName.getText().toString());
                            tempSpinnerAdapter.notifyDataSetChanged();
                            spinnerType.setSelection(tempSpinnerAdapter.getPosition(inputCustomName
                                    .getText().toString()));
                            spinnerType.setTag(R.id.spinner_position, tempSpinnerAdapter
                                    .getPosition(inputCustomName.getText().toString()));
                            break;

                        case AppConstants.IM_ACCOUNT:
                            socialTypeList.add(inputCustomName.getText().toString());

                            if (socialConnectListAdapter != null)
                                socialConnectListAdapter.notifyDataSetChanged();

//                            tempSpinnerAdapter.add(inputCustomName.getText().toString());
//                            tempSpinnerAdapter.notifyDataSetChanged();
//                            spinnerType.setSelection(tempSpinnerAdapter.getPosition
//                                    (inputCustomName.getText().toString()));
//                            spinnerType.setTag(R.id.spinner_position, tempSpinnerAdapter
//                                    .getPosition(inputCustomName.getText().toString()));
                            break;

                        case AppConstants.ADDRESS:
                            tempSpinnerAdapter.add(inputCustomName.getText().toString());
                            tempSpinnerAdapter.notifyDataSetChanged();
                            spinnerType.setSelection(tempSpinnerAdapter.getPosition
                                    (inputCustomName.getText().toString()));
                            spinnerType.setTag(R.id.spinner_position, tempSpinnerAdapter
                                    .getPosition(inputCustomName.getText().toString()));
                            break;
                    }
                } else {
                    Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile,
                            getString(R.string.error_custom_type));
                }
            }
        });
        dialog.show();
    }

    private void showCustomTypeDialogForSocial() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom_type);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        final EditText inputCustomName = dialog.findViewById(R.id.input_custom_name);

        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = dialog.findViewById(R.id.button_left);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        Button buttonRight = dialog.findViewById(R.id.button_right);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        inputCustomName.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setTypeface(Utils.typefaceSemiBold(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText(getString(R.string.str_custom_label));

        buttonLeft.setText(R.string.action_cancel);
        buttonRight.setText(R.string.action_ok);

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (!StringUtils.isBlank(inputCustomName.getText().toString())) {
                    Utils.hideSoftKeyboard(EditProfileActivity.this, inputCustomName);
                    dialog.dismiss();

                    socialTypeList.add(inputCustomName.getText().toString());

                    if (socialConnectListAdapter != null)
                        socialConnectListAdapter.notifyDataSetChanged();

                    addSocialConnectView(null, inputCustomName.getText().toString());

                } else {
                    Utils.showErrorSnackBar(EditProfileActivity.this, relativeRootEditProfile,
                            getString(R.string.error_custom_type));
                }
            }
        });
        dialog.show();
    }

    private void selectImageFromGallery() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);
    }

    private void selectImageFromCamera() {

        File photoFile = getOutputMediaFile();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoFile != null) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                fileUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            } else {
                fileUri = FileProvider.getUriForFile(getApplicationContext(),
                        getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
        }
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private File getOutputMediaFile() {

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

        TEMP_PHOTO_FILE_NAME = "IMG_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + TEMP_PHOTO_FILE_NAME);

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

    private void storeProfileDataToDb(ProfileDataOperation profileDetail) {

        //<editor-fold desc="Basic Details">
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

        UserProfile userProfile = new UserProfile();
        userProfile.setPmRcpId(getUserPmId());
        userProfile.setPmFirstName(profileDetail.getPbNameFirst());
        userProfile.setPmLastName(profileDetail.getPbNameLast());
        userProfile.setProfileRating(profileDetail.getProfileRating());
        userProfile.setTotalProfileRateUser(profileDetail.getTotalProfileRateUser());
        userProfile.setPmIsFavourite(profileDetail.getIsFavourite());
        userProfile.setPmNosqlMasterId(profileDetail.getNoSqlMasterId());
        userProfile.setPmGender(profileDetail.getPbGender());
        userProfile.setPmBadge(profileDetail.getPmBadge());
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
                mobileNumber.setMnmIsPrimary(String.valueOf(arrayListPhoneNumber.get(i)
                        .getPbRcpType()));
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
                email.setEmSocialType(arrayListEmailId.get(i).getEmSocialType());
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
                organization.setOmOrganizationFromDate(arrayListOrganization.get(i)
                        .getOrgFromDate());
                organization.setOmOrganizationToDate(arrayListOrganization.get(i).getOrgToDate());
                organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(i)
                        .getIsCurrent()));
                if (arrayListOrganization.get(i).getIsVerify() != null)
                    if (arrayListOrganization.get(i).getIsVerify() == IntegerConstants
                            .RCP_TYPE_PRIMARY) {
                        organization.setOmOrganizationType(arrayListOrganization.get(i)
                                .getOrgIndustryType());
                        organization.setOmEnterpriseOrgId(arrayListOrganization.get(i)
                                .getOrgEntId());
                        organization.setOmOrganizationLogo(arrayListOrganization.get(i)
                                .getOrgLogo());
                    } else {
                        organization.setOmOrganizationType("");
                        organization.setOmEnterpriseOrgId("");
                        organization.setOmOrganizationLogo("");
                    }
                else {
                    organization.setOmOrganizationType("");
                    organization.setOmEnterpriseOrgId("");
                    organization.setOmOrganizationLogo("");
                }
                organization.setOmIsVerified(String.valueOf(arrayListOrganization.get(i)
                        .getIsVerify()));
                organization.setRcProfileMasterPmId(profileDetail.getRcpPmId());
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
                address.setAmStateId(arrayListAddress.get(j).getStateId());
                address.setAmCity(arrayListAddress.get(j).getCity());
                address.setAmCityId(arrayListAddress.get(j).getCityId());
                address.setAmCountry(arrayListAddress.get(j).getCountry());
                address.setAmCountryId(arrayListAddress.get(j).getCountryId());
                address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress());
                address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                address.setAmPostCode(arrayListAddress.get(j).getPostCode());
                address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                address.setAmStreet(arrayListAddress.get(j).getStreet());
                address.setAmAddressType(arrayListAddress.get(j).getAddressType());
                address.setAmGoogleLatitude(arrayListAddress.get(j).getGoogleLatLong().get(1));
                address.setAmGoogleLongitude(arrayListAddress.get(j).getGoogleLatLong().get(0));
                address.setAmGoogleAddress(arrayListAddress.get(j).getGoogleAddress());
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
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                        .getIMAccountPublic()));
                imAccount.setImImFirstName(arrayListImAccount.get(j).getIMAccountFirstName());
                imAccount.setImImLastName(arrayListImAccount.get(j).getIMAccountLastName());
                imAccount.setImImProfileImage(arrayListImAccount.get(j).getIMAccountProfileImage());
                imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
                imAccount.setRcProfileMasterPmId(profileDetail.getRcpPmId());
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

    private String getEventDateFormatForUpdate(String date) {

        date = StringUtils.substring(date, 0, 2);
        if (!StringUtils.isNumeric(date)) {
            date = StringUtils.substring(date, 0, 1);
        }

        String format;
        if (date.endsWith("1") && !date.endsWith("11"))
//            format = "d'st' MMMM, yyyy";
            format = AppConstants.EVENT_ST_DATE_FORMAT;
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = AppConstants.EVENT_ND_DATE_FORMAT;
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = AppConstants.EVENT_RD_DATE_FORMAT;
        else
            format = AppConstants.EVENT_GENERAL_DATE_FORMAT;
        return format;
    }

    private String getEventDateFormat(String date) {

        date = StringUtils.substring(date, 8, 10);
        if (!StringUtils.isNumeric(date)) {
            date = StringUtils.substring(date, 0, 1);
        }

        String format;
        if (date.endsWith("1") && !date.endsWith("11"))
//            format = "d'st' MMMM, yyyy";
            format = AppConstants.EVENT_ST_DATE_FORMAT;
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = AppConstants.EVENT_ND_DATE_FORMAT;
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = AppConstants.EVENT_RD_DATE_FORMAT;
        else
            format = AppConstants.EVENT_GENERAL_DATE_FORMAT;
        return format;
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
                    .msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT_V2 + WsConstants
                            .REQ_PROFILE_UPDATE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

//    private void editProfileForSocial(ProfileDataOperation editProfile, int type) {
//
//        WsRequestObject editProfileObject = new WsRequestObject();
//        editProfileObject.setProfileUpdate(editProfile);
//
//        if (Utils.isNetworkAvailable(this)) {
//            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
//                    editProfileObject, null, WsResponseObject.class, WsConstants
//                    .REQ_PROFILE_UPDATE + ":" + type, getResources().getString(R.string
//                    .msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
//                    WsConstants.WS_ROOT_V2 + WsConstants
//                            .REQ_PROFILE_UPDATE);
//        } else {
//            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
//                    .getString(R.string.msg_no_network));
//        }
//    }

    private void getCountryList() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), null, null,
                    WsResponseObject.class, WsConstants.REQ_COUNTRY_CODE_DETAIL, getString(R.string
                    .msg_please_wait), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT_V2 + WsConstants.REQ_COUNTRY_CODE_DETAIL);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }

    }

    private void getStateList(String countryId, String
            position) {
        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), null, null,
                    WsResponseObject.class, WsConstants.REQ_STATE_DETAILS + ":" + position, null,
                    false)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT_V2 +
                            WsConstants.REQ_STATE_DETAILS + "/" + countryId);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }

    }

    /*private void getCityList(String stateId, String position) {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), null, null,
                    WsResponseObject.class, WsConstants.REQ_CITY_DETAILS + ":" + position, null,
                    false).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT_V2 + WsConstants.REQ_CITY_DETAILS
                    + "/" +
                    stateId);
        } else {
            Utils.showErrorSnackBar(this, relativeRootEditProfile, getResources()
                    .getString(R.string.msg_no_network));
        }

    }*/

    //</editor-fold>

}
