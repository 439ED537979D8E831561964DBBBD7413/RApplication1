package com.rawalinfocom.rcontact.contacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.ContactListingActivity;
import com.rawalinfocom.rcontact.MainActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.CallHistoryListAdapter;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.PhoneBookContactDetailAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.calllog.CallHistoryDetailsActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableAadharMaster;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEducationMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.CallConfirmationListDialog;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MyProfileShareDialog;
import com.rawalinfocom.rcontact.helper.ProfileMenuOptionDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.circleprogressview.CircleProgressView;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.helper.instagram.util.StringUtil;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.CallLogHistoryType;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Education;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.PrivacyDataItem;
import com.rawalinfocom.rcontact.model.PrivacyEntityItem;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAadharNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEducation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileVisit;
import com.rawalinfocom.rcontact.model.Rating;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.relation.ExistingRelationActivity;
import com.rawalinfocom.rcontact.relation.RCPExistingRelationActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileDetailActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    private final String TAG_IMAGE_EDIT = "tag_edit";
    private final String TAG_IMAGE_FAVOURITE = "tag_favourite";
    private final String TAG_IMAGE_UN_FAVOURITE = "tag_un_favourite";
    private final String TAG_IMAGE_CALL = "tag_call";
    private final String TAG_IMAGE_SHARE = "tag_share";

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    //    Toolbar toolbarProfileDetail;
    RippleView rippleActionBack;
    RippleView rippleActionRelation;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;
    ImageView imageRelation;
    ImageView imageRightLeft;
    ImageView imageRightCenter;
    ImageView imageRightRight;

    //    @BindView(R.id.nestedScrollView)
//    ScrollView nestedScrollView;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.relative_contact_details)
    RelativeLayout relativeContactDetails;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.text_name)
    TextView textName;
    /*  @BindView(R.id.text_cloud_name)
      TextView textCloudName;*/
    @BindView(R.id.text_full_screen_text)
    TextView textFullScreenText;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_time)
    TextView textTime;
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
    @Nullable
    @BindView(R.id.recycler_view_education)
    RecyclerView recyclerViewEducation;
    @BindView(R.id.linear_email)
    LinearLayout linearEmail;
    @Nullable
    @BindView(R.id.linear_education)
    LinearLayout linearEducation;
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
    @Nullable
    @BindView(R.id.image_expand_collapse)
    ImageView imageExpandCollapse;
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
    @Nullable
    @BindView(R.id.relative_call_history)
    RelativeLayout relativeCallHistory;
    /* @BindView(R.id.text_icon_history)
     TextView textIconHistory;*/
    @Nullable
    @BindView(R.id.recycler_call_history)
    RecyclerView recyclerCallHistory;
    @BindView(R.id.ripple_call_log)
    RippleView rippleCallLog;
    @BindView(R.id.ripple_sms)
    RippleView rippleSms;
    @Nullable
    @BindView(R.id.text_no_history_to_show)
    TextView textNoHistoryToShow;
    @Nullable
    @BindView(R.id.text_text_call_history)
    TextView textCallHistory;
    @BindView(R.id.ripple_invite)
    RippleView rippleInvite;
    @BindView(R.id.button_invite)
    Button buttonInvite;

    @BindView(R.id.image_enlarge)
    ImageView imageEnlarge;
    @BindView(R.id.frame_image_enlarge)
    FrameLayout frameImageEnlarge;

    @BindView(R.id.frame_container)
    FrameLayout frameContainer;

    @Nullable
    @BindView(R.id.relative_profile_percentage)
    RelativeLayout relativeProfilePercentage;
    @Nullable
    @BindView(R.id.text_complete_profile)
    TextView textCompleteProfile;
    @Nullable
    @BindView(R.id.text_complete_profile_description)
    TextView textCompleteProfileDescription;
    @Nullable
    @BindView(R.id.progress_percentage)
    CircleProgressView progressPercentage;
    @Nullable
    @BindView(R.id.include_elevation)
    View includeElevation;
    @Nullable
    @BindView(R.id.include_elevation_top)
    View includeElevationTop;

    @Nullable
    @BindView(R.id.text_tap_continue)
    TextView textTapContinue;
    @Nullable
    @BindView(R.id.frame_tutorial)
    FrameLayout frameTutorial;
    @Nullable
    @BindView(R.id.image_tutorial_edit)
    ImageView imageTutorialEdit;
    @Nullable
    @BindView(R.id.button_request_rating)
    Button buttonRequestRating;
    @Nullable
    @BindView(R.id.button_privacy_rating)
    ImageView buttonPrivacyRating;

    String callLogCloudName;
    boolean isCallLogRcpUser, isRatingUpdate = false;
    boolean isDialogCallLogInstance;

    RelativeLayout relativeRootRatingDialog;

    ProfileDataOperation profileDataOperationVcard;

    String pmId = "", phoneBookId, contactName = "", contactNumber = "", cloudContactName = null,
            checkNumberFavourite =
                    null, thumbnailUrl = "";
    boolean displayOwnProfile = false, isHideFavourite = false, isFromFavourite = false;
    int isFavourite = 0;

    PhoneBookContacts phoneBookContacts;
    QueryManager queryManager;
    int listClickedPosition = -1;

    ProfileDetailAdapter phoneDetailAdapter;
    MaterialDialog callConfirmationDialog;

    ArrayList<String> arrayListPBPhoneNumber;
    ArrayList<String> arrayListPBEmailAddress;
    ArrayList<String> arrayListFavouriteContacts;

    RContactApplication rContactApplication;
    boolean profileActivityCallInstance = false;
    ArrayList<CallLogType> arrayListHistory;
    String historyNumber = "";
    String historyName = "";
    long historyDate;
    CallHistoryListAdapter callHistoryListAdapter;
    String profileContactNumber;
    String hashMapKey = "";
    String uniqueContactId = "";
    @Nullable
    @BindView(R.id.ripple_view_old_records)
    RippleView rippleViewOldRecords;
    @Nullable
    @BindView(R.id.progressBarLoadCallLogs)
    ProgressBar progressBarLoadCallLogs;
    @Nullable
    @BindView(R.id.button_view_old_records)
    Button buttonViewOldRecords;
    LinearLayoutManager mLinearLayoutManager;
    String profileThumbnail = "";
    MaterialDialog permissionConfirmationDialog;

    ArrayList<Object> tempPhoneNumber;
    ArrayList<Object> tempEmail;
    ArrayList<Object> tempEducation;

    boolean isFromReceiver = false;
    boolean isContactEdited = false;

    public String callNumber = "";
    String callLogRcpVerfiedId = "";
    boolean isFromNotification;
    AsyncWebServiceCall asyncGetProfileDetails;
    @Nullable
    @BindView(R.id.image_aadhar_card)
    ImageView imageAadharCard;
    @Nullable
    @BindView(R.id.text_label_aadhar_card)
    TextView textLabelAadharCard;
    @Nullable
    @BindView(R.id.button_privacy)
    ImageView buttonPrivacy;
    @Nullable
    @BindView(R.id.text_aadhar_number)
    TextView textAadharNumber;
    @Nullable
    @BindView(R.id.relative_aadhar_number)
    RelativeLayout relativeAadharNumber;
    @Nullable
    @BindView(R.id.text_label_UIDAI_number)
    TextView textLabelUIDAINumber;
    @Nullable
    @BindView(R.id.linear_aadhar_card)
    LinearLayout linearAadharCard;
    @Nullable
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @Nullable
    @BindView(R.id.button_request)
    AppCompatButton buttonRequest;
    @BindView(R.id.img_user_rating)
    ImageView imgUserRating;

    @Nullable
    @BindView(R.id.image_last_seen)
    ImageView imageLastSeen;
    @Nullable
    @BindView(R.id.text_label_last_seen)
    TextView textLabelLastSeen;
    @Nullable
    @BindView(R.id.button_request_all)
    AppCompatButton buttonRequestAll;
    //    @Nullable
//    @BindView(R.id.linear_last_seen)
//    RelativeLayout linearLastSeen;
    @Nullable
    @BindView(R.id.linear_last_seen)
    LinearLayout linearLastSeen;
    @Nullable
    @BindView(R.id.card_last_seen_details)
    CardView cardLastSeenDetails;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, false)) {
            setContentView(R.layout.activity_call_history_details);
        } else {
            setContentView(R.layout.activity_profile_detail);
        }

        rContactApplication = (RContactApplication) getApplicationContext();
        ButterKnife.bind(this);

        phoneBookContacts = new PhoneBookContacts(this);
        queryManager = new QueryManager(databaseHandler);
        Intent intent = getIntent();

        getIntentDetails(intent);

        if (pmId.equalsIgnoreCase(getUserPmId())) {
            displayOwnProfile = true;
        }

        arrayListFavouriteContacts = new ArrayList<>();
        if (!Utils.isArraylistNullOrEmpty(Utils.getArrayListPreference(this, AppConstants
                .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL))) {
            arrayListFavouriteContacts.addAll(Utils.getArrayListPreference(this, AppConstants
                    .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL));
        }

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isContactEdited) {
            isContactEdited = false;
            recreate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE);
        localBroadcastManager.registerReceiver(localBroadcastReceiver, intentFilter);

        LocalBroadcastManager localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter1 = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_DIALOG);
        localBroadcastManager1.registerReceiver(localBroadcastReceiverDialog, intentFilter1);

        if (profileActivityCallInstance) {
//            fetchCallLogHistoryDateWise(historyNumber);
//            fetchAllCallLogHistory(historyNumber);
            /*if (!TextUtils.isEmpty(historyName)) {
                fetchAllCallLogHistory(historyName);

            } else {
//        fetchCallLogHistoryDateWise(historyNumber);
                fetchAllCallLogHistory(historyNumber);

            }*/
            new GetRCPNameAndProfileImage().execute();
            if (isFromReceiver) {
                isFromReceiver = false;
                Utils.setBooleanPreference(ProfileDetailActivity.this, AppConstants
                        .PREF_CALL_LOG_STARTS_FIRST_TIME, true);
                AppConstants.isFromReceiver = false;
            }

        }
//        else {
//            if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase("[Unknown]")) {
//                fetchAllCallLogHistory(contactName);
//            } else {
//                if (!TextUtils.isEmpty(profileContactNumber)) {
//                    fetchAllCallLogHistory(profileContactNumber);
//                }
//            }
//        }
        if (displayOwnProfile) {
            if (pmId != null) {
                ProfileDataOperation profileDataOperation = queryManager.getRcProfileDetail
                        (this, pmId);
                layoutVisibility();
                setUpView(profileDataOperation);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiver);

        LocalBroadcastManager localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
        localBroadcastManager1.unregisterReceiver(localBroadcastReceiverDialog);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            //<editor-fold desc="View More">
            case R.id.ripple_view_more:
                if (relativeSectionViewMore.getVisibility() == View.VISIBLE) {
                    relativeSectionViewMore.setVisibility(View.GONE);
                    buttonViewMore.setText(getString(R.string.str_view_more));
                    imageExpandCollapse.setImageResource(R.drawable.ico_arrow_down_svg);
                } else {
                    relativeSectionViewMore.setVisibility(View.VISIBLE);
                    buttonViewMore.setText(getString(R.string.str_view_less));
                    imageExpandCollapse.setImageResource(R.drawable.ic_arrow_up_svg);
                }
                break;
            //</editor-fold>

            //<editor-fold desc="Back">
            case R.id.ripple_action_back:
                onBackPressed();
                break;
            //</editor-fold>

            //<editor-fold desc="View Old Records">
            case R.id.ripple_view_old_records:
                progressBarLoadCallLogs.setVisibility(View.VISIBLE);
                rippleViewOldRecords.setVisibility(View.GONE);
                getOldCallHistory();
                break;
            //</editor-fold>

            //<editor-fold desc="Invite">
            case R.id.ripple_invite:
                ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();
                for (int i = 0; i < tempPhoneNumber.size(); i++) {
                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();
                    ProfileDataOperationPhoneNumber number = (ProfileDataOperationPhoneNumber)
                            tempPhoneNumber.get(i);
                    phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, number
                            .getPhoneNumber()));
                    phoneNumber.setPhoneType(number.getPhoneType());
                    phoneNumbers.add(phoneNumber);
                }

                ArrayList<ProfileDataOperationEmail> emails = new ArrayList<>();
                for (int i = 0; i < tempEmail.size(); i++) {
                    ProfileDataOperationEmail email = new ProfileDataOperationEmail();
                    ProfileDataOperationEmail emailId = (ProfileDataOperationEmail) tempEmail.get
                            (i);
                    email.setEmEmailId(emailId.getEmEmailId());
                    email.setEmType(emailId.getEmType());
                    emails.add(email);
                }

                if (phoneNumbers.size() + emails.size() > 1) {
                    selectContactDialog(phoneNumbers, emails);
                } else {
                    if (phoneNumbers.size() > 0) {
//                        ArrayList<String> numbers = new ArrayList<>();
//                        for (int i = 0; i < phoneNumbers.size(); i++) {
//                            numbers.add(phoneNumbers.get(i).getPhoneNumber());
//                        }

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("sms_body", AppConstants.PLAY_STORE_LINK +
                                getPackageName());
                        smsIntent.setData(Uri.parse("sms:" + phoneNumbers.get(0).getPhoneNumber()));
                        startActivity(smsIntent);


//                        inviteContact(numbers, null);
                    } else if (emails.size() > 0) {
                        ArrayList<String> aryEmails = new ArrayList<>();
                        for (int i = 0; i < emails.size(); i++) {
                            aryEmails.add(emails.get(i).getEmEmailId());
                        }
                        inviteContact(null, aryEmails);
                    }
                }
                break;
            //</editor-fold>

            //<editor-fold desc="Call Log">
            case R.id.ripple_call_log:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission
                            .READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},
                                AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    } else {
                        openCallLogHistoryDetailsActivity();
                    }
                } else {
                    openCallLogHistoryDetailsActivity();
                }
                break;
            //</editor-fold>

            //<editor-fold desc="SMS">
            case R.id.ripple_sms:

                if (tempPhoneNumber != null && tempPhoneNumber.size() > 1) {
                    if (tempPhoneNumber != null && tempPhoneNumber.size() > 0) {
                        int count = tempPhoneNumber.size();
                        ArrayList<String> listPhoneNumber = new ArrayList<>();
                        if (count > 1) {
                            for (int i = 0; i < tempPhoneNumber.size(); i++) {
                                ProfileDataOperationPhoneNumber phoneNumber =
                                        (ProfileDataOperationPhoneNumber) tempPhoneNumber
                                                .get(i);

                                String number = phoneNumber.getPhoneNumber();

                                if (!number.startsWith("+XX")) {
                                    listPhoneNumber.add(number);
                                }
                            }

                            if (listPhoneNumber.size() == 1) {
                                if (!TextUtils.isEmpty(listPhoneNumber.get(0))) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts
                                            ("sms", listPhoneNumber.get(0), null));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        intent.setPackage(Telephony.Sms.getDefaultSmsPackage(this));
                                    }
                                    intent.putExtra("finishActivityOnSaveCompleted", true);
                                    startActivity(intent);
                                }
                            } else {
                                if (listPhoneNumber.size() > 0) {
                                    CallConfirmationListDialog callConfirmationListDialog = new
                                            CallConfirmationListDialog(this, listPhoneNumber,
                                            false);
                                    callConfirmationListDialog.setDialogTitle(getString(R.string
                                            .please_select_number_view_sms_log));
                                    callConfirmationListDialog.showDialog();
                                }

                            }

                        } else {
                            dialCall(profileContactNumber);
//                                    showCallConfirmationDialog(profileContactNumber);
                        }
                    }
                } else {

                    if (!profileActivityCallInstance) {
                        if (tempPhoneNumber != null) {
                            if (tempPhoneNumber.size() == 1) {
                                ProfileDataOperationPhoneNumber phoneNumber =
                                        (ProfileDataOperationPhoneNumber) tempPhoneNumber
                                                .get(0);
                                if (phoneNumber != null) {
                                    historyNumber = phoneNumber.getPhoneNumber();
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(historyNumber)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts
                                ("sms", historyNumber, null));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            intent.setPackage(Telephony.Sms.getDefaultSmsPackage(this));
                        }
                        intent.putExtra("finishActivityOnSaveCompleted", true);
                        startActivity(intent);
                    }
                }

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (ContextCompat.checkSelfPermission(this, Manifest.permission
//                            .READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.READ_SMS},
//                                AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
//                    } else {
//
//                        if (tempPhoneNumber != null && tempPhoneNumber.size() > 1) {
//                            if (tempPhoneNumber != null && tempPhoneNumber.size() > 0) {
//                                int count = tempPhoneNumber.size();
//                                ArrayList<String> listPhoneNumber = new ArrayList<>();
//                                if (count > 1) {
//                                    for (int i = 0; i < tempPhoneNumber.size(); i++) {
//                                        ProfileDataOperationPhoneNumber phoneNumber =
//                                                (ProfileDataOperationPhoneNumber) tempPhoneNumber
//                                                        .get(i);
//
//                                        String number = phoneNumber.getPhoneNumber();
//
//                                        if (!number.startsWith("+XX")) {
//                                            listPhoneNumber.add(number);
//                                        }
//                                    }
//
//                                    CallConfirmationListDialog callConfirmationListDialog = new
//                                            CallConfirmationListDialog(this, listPhoneNumber,
//                                            false);
//                                    callConfirmationListDialog.setDialogTitle(getString(R.string
//                                            .please_select_number_view_sms_log));
//                                    callConfirmationListDialog.showDialog();
//
//                                } else {
//                                    dialCall(profileContactNumber);
////                                    showCallConfirmationDialog(profileContactNumber);
//                                }
//                            }
//                        } else {
//
//                            if (!profileActivityCallInstance) {
//                                if (tempPhoneNumber != null) {
//                                    if (tempPhoneNumber.size() == 1) {
//                                        ProfileDataOperationPhoneNumber phoneNumber =
//                                                (ProfileDataOperationPhoneNumber) tempPhoneNumber
//                                                        .get(0);
//                                        if (phoneNumber != null) {
//                                            historyNumber = phoneNumber.getPhoneNumber();
//                                        }
//                                    }
//                                }
//                            }
//                            if (!TextUtils.isEmpty(historyNumber)) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts
//                                        ("sms", historyNumber, null));
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                    intent.setPackage(Telephony.Sms.getDefaultSmsPackage(this));
//                                }
//                                intent.putExtra("finishActivityOnSaveCompleted", true);
//                                startActivity(intent);
//                            }
//                        }
//
//                    }
//                } else {
//                    if (tempPhoneNumber != null && tempPhoneNumber.size() > 1) {
//                        if (tempPhoneNumber != null && tempPhoneNumber.size() > 0) {
//                            int count = tempPhoneNumber.size();
//                            ArrayList<String> listPhoneNumber = new ArrayList<>();
//                            if (count > 1) {
//                                for (int i = 0; i < tempPhoneNumber.size(); i++) {
//                                    ProfileDataOperationPhoneNumber phoneNumber =
//                                            (ProfileDataOperationPhoneNumber) tempPhoneNumber.get
//                                                    (i);
//
//                                    String number = phoneNumber.getPhoneNumber();
//
//                                    if (!number.startsWith("+XX")) {
//                                        listPhoneNumber.add(number);
//                                    }
//                                }
//
//                                CallConfirmationListDialog callConfirmationListDialog = new
//                                        CallConfirmationListDialog(this, listPhoneNumber, false);
//                                callConfirmationListDialog.setDialogTitle(getString(R.string
//                                        .please_select_number_view_sms_log));
//                                callConfirmationListDialog.showDialog();
//
//                            } else {
//                                dialCall(profileContactNumber);
////                                showCallConfirmationDialog(profileContactNumber);
//                            }
//                        }
//                    } else {
//                        if (!profileActivityCallInstance) {
//                            if (tempPhoneNumber != null) {
//                                if (tempPhoneNumber.size() == 1) {
//                                    ProfileDataOperationPhoneNumber phoneNumber =
//                                            (ProfileDataOperationPhoneNumber) tempPhoneNumber.get
//                                                    (0);
//                                    if (phoneNumber != null) {
//                                        historyNumber = phoneNumber.getPhoneNumber();
//                                    }
//                                }
//                            }
//                        }
//
//                        if (!TextUtils.isEmpty(historyNumber)) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",
//                                    historyNumber, null));
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                intent.setPackage(Telephony.Sms.getDefaultSmsPackage(this));
//                            }
//                            intent.putExtra("finishActivityOnSaveCompleted", true);
//                            startActivity(intent);
//                        }
//
//                    }
//                }


                break;
            //</editor-fold>

            //<editor-fold desc="Right Center">
            case R.id.ripple_action_right_center:

                if (StringUtils.equals(imageRightCenter.getTag().toString(), TAG_IMAGE_CALL)) {
                    dialCall(historyNumber);
//                    showCallConfirmationDialog(historyNumber);
                    isFromReceiver = true;

                } else if (StringUtils.equals(imageRightCenter.getTag().toString(),
                        TAG_IMAGE_SHARE)) {

//                    Toast.makeText(this, "Currently Unavailable!", Toast.LENGTH_SHORT).show();

                    /*if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                                .parseInt(pmId));
                        showChooseShareOption(StringUtils.trimToEmpty(userProfile.getPmFirstName()),
                                StringUtils.trimToEmpty(userProfile.getPmLastName()));
                    } else {
                        showChooseShareOption(null, null);
                    }*/

                    if (displayOwnProfile) {
                        ArrayList arrayList = new ArrayList(Arrays.asList(getString(R.string
                                        .my_profile_share),
                                getString(R.string.average_rate_sharing)));
                        MyProfileShareDialog myProfileShareDialog = new
                                MyProfileShareDialog(this, arrayList, pmId,
                                profileDataOperationVcard, contactName,
                                ProfileDetailActivity.this);
                        myProfileShareDialog.showDialog();

                    } else {
                        if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                            TableProfileMaster tableProfileMaster = new TableProfileMaster
                                    (databaseHandler);
                            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId
                                    (Integer.parseInt(pmId));
                            TableMobileMaster tableMobileMaster = new TableMobileMaster
                                    (databaseHandler);
                            String number = tableMobileMaster.getUserMobileNumber(pmId);

                            if (StringUtils.startsWith(number, "+")) {
                                number = StringUtils.substring(number, 1);
                            }

                            /*Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody;
                            if (StringUtils.isBlank(userProfile.getPmBadge())) {
                                shareBody = WsConstants.WS_PROFILE_VIEW_BADGE_ROOT + number;
                            } else {
                                shareBody = WsConstants.WS_PROFILE_VIEW_BADGE_ROOT + userProfile
                                        .getPmBadge();
                            }
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, getString(R.string
                                    .str_share_contact_via)));*/

                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/x-vcard");
                            List<ResolveInfo> listVCardResInfo = getPackageManager()
                                    .queryIntentActivities(shareIntent, 0);

                            List<Intent> targetedShareIntents = new ArrayList<>();
                            if (!listVCardResInfo.isEmpty()) {
                                for (ResolveInfo resolveInfo : listVCardResInfo) {
                                    String packageName = resolveInfo.activityInfo.packageName;
                                    Intent targetedShareIntent = new Intent(
                                            Intent.ACTION_SEND);
                                    targetedShareIntent.setType("text/plain");
                                    String shareBody;
                                    if (StringUtils.isBlank(userProfile.getPmBadge())) {
                                        shareBody = BuildConfig.WS_PROFILE_VIEW_BADGE_ROOT + number;
                                    } else {
                                        shareBody = BuildConfig.WS_PROFILE_VIEW_BADGE_ROOT +
                                                userProfile.getPmBadge();
                                    }
                                    targetedShareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                    targetedShareIntent.setPackage(packageName);
                                    targetedShareIntents.add(targetedShareIntent);

                                }

                                Intent chooserIntent = Intent.createChooser(targetedShareIntents
                                                .remove(0),
                                        getString(R.string.str_share_contact_via));
                                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                                        targetedShareIntents.toArray(new
                                                Parcelable[targetedShareIntents.size()]));
                                startActivity(chooserIntent);
                            }

                        } else {
                            shareContact();
                        }
                    }
                }
                break;
            //</editor-fold>

            //<editor-fold desc="Favourites">
            case R.id.ripple_action_relation:

//                Utils.setBooleanPreference(ProfileDetailActivity.this,
//                        AppConstants.PREF_GET_RELATION, true);

                if (displayOwnProfile) {
                    startActivity(new Intent(ProfileDetailActivity.this, ExistingRelationActivity
                            .class));
                } else {

                    Intent intent = new Intent(ProfileDetailActivity.this,
                            RCPExistingRelationActivity.class);
                    intent.putExtra(AppConstants.EXTRA_CONTACT_NAME, contactName);
                    intent.putExtra(AppConstants.EXTRA_CONTACT_NUMBER, contactNumber);
                    intent.putExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
                    intent.putExtra(AppConstants.EXTRA_PM_ID, pmId);
                    startActivity(intent);
                }

                break;
            //</editor-fold>

            //<editor-fold desc="Favourites">
            case R.id.ripple_action_right_left:
                if (StringUtils.equals(imageRightLeft.getTag().toString(), TAG_IMAGE_FAVOURITE)
                        || StringUtils.equals(imageRightLeft.getTag().toString(),
                        TAG_IMAGE_UN_FAVOURITE)) {
                    int favStatus;
                    String favStatusRawId;
                    if (profileActivityCallInstance) {
                        favStatusRawId = hashMapKey;
                    } else {
                        favStatusRawId = phoneBookId;
                    }
                    if (StringUtils.equals(imageRightLeft.getTag().toString(),
                            TAG_IMAGE_FAVOURITE)) {
                        favStatus = PhoneBookContacts.STATUS_UN_FAVOURITE;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                        imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
                    } else {
                        favStatus = PhoneBookContacts.STATUS_FAVOURITE;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                        imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
                    }

                    int updateStatus = phoneBookContacts.setFavouriteStatus(favStatusRawId,
                            favStatus);

                    if (updateStatus != 1) {
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.error_update_favorite_status));
                    }
                    ArrayList<ProfileData> arrayListFavourites = new ArrayList<>();
                    ProfileData favouriteStatus = new ProfileData();
                    favouriteStatus.setLocalPhoneBookId(favStatusRawId);
                    favouriteStatus.setIsFavourite(String.valueOf(favStatus));
                    arrayListFavourites.add(favouriteStatus);
                    setFavouriteStatus(arrayListFavourites);

//                    rContactApplication.setFavouriteModified(true);
                    if (favStatus == PhoneBookContacts.STATUS_FAVOURITE) {
                        rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_ADDED);
                        isFavourite = 1;
                    } else {
                        rContactApplication.setFavouriteStatus(RContactApplication
                                .FAVOURITE_REMOVED);
                        isFavourite = 0;
                    }

                } else if (StringUtils.equals(imageRightLeft.getTag().toString(), TAG_IMAGE_EDIT)) {
                    /*startActivityIntent(ProfileDetailActivity.this, EditProfileActivity.class,
                            null);*/
                    startActivityIntent(ProfileDetailActivity.this, EditProfileActivity.class,
                            null);
                }
                break;
            //</editor-fold>

            //<editor-fold desc="3 Dot option Menu">
            case R.id.ripple_action_right_right:
                if (profileActivityCallInstance) {
                    ProfileMenuOptionDialog profileMenuOptionDialog;
                    String blockedNumber = "";
                    ArrayList<CallLogType> callLogTypeList = new ArrayList<>();
                    HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                            Utils.getHashMapPreferenceForBlock(this, AppConstants
                                    .PREF_BLOCK_CONTACT_LIST);

                    if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                        if (blockProfileHashMapList.containsKey(hashMapKey))
                            callLogTypeList.addAll(blockProfileHashMapList.get(hashMapKey));

                    }
                    for (int j = 0; j < callLogTypeList.size(); j++) {
                        // Log.i("value", callLogTypeList.get(j) + "");
                        String tempNumber = callLogTypeList.get(j).getNumber();
                        if (tempNumber.equalsIgnoreCase(historyNumber)) {
                            blockedNumber = tempNumber;
                        }
                    }

                    if (!TextUtils.isEmpty(blockedNumber)) {
                        if (!TextUtils.isEmpty(historyName)) {
                            ArrayList<String> arrayListName = new ArrayList<>(Arrays.asList(this
                                            .getString(R.string.edit), getString(R.string
                                            .view_in_ac),
                                    /*this.getString(R.string.view_in_ac), this.getString(R
                                    .string.view_in_rc),
                                    this.getString(R.string.call_reminder),
                                    this.getString(R.string.unblock),*/ this.getString(R.string
                                            .delete),
                                    this.getString(R.string.clear_call_log)));
                            profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                    arrayListName, historyNumber, historyDate, true,
                                    arrayListHistory, historyName, "", hashMapKey,
                                    profileThumbnail, pmId, isCallLogRcpUser, "", callLogCloudName);
                            profileMenuOptionDialog.showDialog();

                        } else {
                            if (!TextUtils.isEmpty(historyNumber)) {
                                ArrayList<String> arrayListNumber = new ArrayList<>(Arrays.asList
                                        (this.getString(R.string.add_to_contact),
                                                this.getString(R.string.add_to_existing_contact),
                                                this
                                                        .getString(R.string.view_profile),
                                                this.getString(R.string.copy_phone_number),
                                        /*this.getString(R.string.call_reminder), this.getString
                                        (R.string.unblock),*/
                                                this.getString(R.string.delete), this.getString(R
                                                        .string
                                                        .clear_call_log)));
                                profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                        arrayListNumber, historyNumber, historyDate,
                                        true, arrayListHistory, "", uniqueContactId,
                                        hashMapKey, profileThumbnail, pmId, isCallLogRcpUser,
                                        callLogRcpVerfiedId, callLogCloudName);
                                profileMenuOptionDialog.showDialog();
                            }
                        }
                    } else {

                        if (!TextUtils.isEmpty(historyName)) {
                            ArrayList<String> arrayListName = new ArrayList<>(Arrays.asList(this
                                            .getString(R.string.edit),
                                    getString(R.string.view_in_ac),
                                    /*this.getString(R.string.view_in_ac), this.getString(R
                                    .string.view_in_rc),
                                    this.getString(R.string.call_reminder),
                                    this.getString(R.string.block),*/ this.getString(R.string
                                            .delete),
                                    this.getString(R.string.clear_call_log)));
                            profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                    arrayListName, historyNumber, historyDate, true,
                                    arrayListHistory, historyName, "", phoneBookId,
                                    profileThumbnail, pmId, isCallLogRcpUser, "", callLogCloudName);
                            //11/07/2017 : hashMapKey replaced with phoneBookId to solve edit
                            // option problem
                            profileMenuOptionDialog.showDialog();

                        } else {
                            if (!TextUtils.isEmpty(historyNumber)) {
                                ArrayList<String> arrayListNumber = new ArrayList<>(Arrays.asList
                                        (this.getString(R.string.add_to_contact),
                                                this.getString(R.string.add_to_existing_contact),
                                                /*this.getString(R.string.view_profile),*/
                                                this.getString(R.string.copy_phone_number),
                                        /*this.getString(R.string.call_reminder), this.getString
                                        (R.string.block),*/
                                                this.getString(R.string.delete), this.getString(R
                                                        .string
                                                        .clear_call_log)));
                                profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                                        arrayListNumber, historyNumber, historyDate,
                                        true, arrayListHistory, "", uniqueContactId,
                                        "", profileThumbnail, pmId, isCallLogRcpUser,
                                        callLogRcpVerfiedId, callLogCloudName);
                                profileMenuOptionDialog.showDialog();
                            }
                        }
                    }
                } else {
                    int menuType;

                    if (!displayOwnProfile) {
                        if (isHideFavourite) {
                            // RCP Contact from RCP tab
                            menuType = OptionMenuDialog.R_CONTACT_RCP;
                        } else if (StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                            // PB Contact
                            menuType = OptionMenuDialog.ALL_CONTACT_NON_RCP;
                        } else {
                            // RCP Contact
                            menuType = OptionMenuDialog.ALL_CONTACT_RCP;
                        }

                        String rawId;
                        if (checkNumberFavourite == null) {
                            rawId = phoneBookId;
                        } else {
                            rawId = checkNumberFavourite;
                        }

                        if (isFromNotification) {
                            if (!StringUtils.isBlank(contactName)) {
                                if (contactName.startsWith("+91")) {
                                    String contactNumber = contactName.substring(0, 13);
                                    String isContactName = getNameFromNumber(contactNumber);
                                    if (StringUtils.isBlank(isContactName)) {
                                        ArrayList<String> arrayListNumber = new ArrayList<>
                                                (Arrays.asList
                                                        (this.getString(R.string.add_to_contact),
                                                                this.getString(R.string
                                                                        .add_to_existing_contact)));
                                        ProfileMenuOptionDialog profileMenuOptionDialog = new
                                                ProfileMenuOptionDialog(this,
                                                arrayListNumber, contactNumber, 0,
                                                false, null, "", "",
                                                "", "", "", false,
                                                "", "");
                                        profileMenuOptionDialog.showDialog();
                                    }
                                } else {
                                    OptionMenuDialog optionMenu = new OptionMenuDialog
                                            (ProfileDetailActivity
                                                    .this, rawId, menuType, isFavourite == 1,
                                                    isFromFavourite,
                                                    isCallLogRcpUser);

                                    optionMenu.showDialog();
                                }

                            }
                        } else {
                            OptionMenuDialog optionMenu = new OptionMenuDialog(ProfileDetailActivity
                                    .this, rawId, menuType, isFavourite == 1, isFromFavourite,
                                    isCallLogRcpUser);

                            optionMenu.showDialog();
                        }


                    }
                }
                break;
            //</editor-fold>
        }
    }

    @Override
    public void onBackPressed() {
//        if (!Utils.getBooleanPreference(this, AppConstants.PREF_SHOW_WALK_THROUGH, true)) {
        if (isRatingUpdate) {

            Intent localBroadcastIntent1 = new Intent(AppConstants
                    .ACTION_LOCAL_BROADCAST_RATING_UPDATE);
            localBroadcastIntent1.putExtra(AppConstants.EXTRA_RCONTACT_POSITION, getIntent()
                    .getIntExtra(AppConstants.EXTRA_RCONTACT_POSITION, 0));
            localBroadcastIntent1.putExtra(AppConstants.EXTRA_RATING_UPDATE, isRatingUpdate);
            LocalBroadcastManager.getInstance(ProfileDetailActivity.this).sendBroadcast
                    (localBroadcastIntent1);
        }

        Intent backIntent = getIntent();
        setResult(RESULT_OK, backIntent);
        finish();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {

                    // Permission Granted
                    openCallLogHistoryDetailsActivity();

                } else {

                    // Permission Denied
                    showPermissionConfirmationDialog();

                }
                break;

            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    // Permission Granted
                    Utils.callIntent(ProfileDetailActivity.this, callNumber);
                }
                /*else {
                    // Permission Denied
                    showPermissionConfirmationDialog(AppConstants
                            .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                }*/
                break;
        }
    }

    @Override
    public void
    onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            Utils.hideProgressDialog();
            // <editor-fold desc="REQ_GET_PROFILE_DETAILS">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DETAILS)) {
                WsResponseObject getProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (getProfileResponse != null && StringUtils.equalsIgnoreCase(getProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    getUserData();

                    ProfileDataOperation profileDetail = getProfileResponse.getProfileDetail();
                    storeProfileDataToDb(profileDetail);

                    getDataFromDB();

                    ArrayList<ProfileVisit> profileVisits = new ArrayList<>();
                    ProfileVisit profileVisit = new ProfileVisit();
                    profileVisit.setVisitorPmId(Integer.parseInt(pmId));
                    profileVisit.setVisitCount(1);
                    profileVisits.add(profileVisit);
                    profileVisit(profileVisits);

                }
                /*else {
                    Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                    Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                            .string.msg_try_later));
                }*/
            }
            //</editor-fold>

            // <editor-fold desc="REQ_MARK_AS_FAVOURITE">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_MARK_AS_FAVOURITE)) {
                WsResponseObject favouriteStatusResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (favouriteStatusResponse == null || !StringUtils.equalsIgnoreCase
                        (favouriteStatusResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    if (favouriteStatusResponse != null) {
                        Log.e("error response", favouriteStatusResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                favouriteStatusResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_RCP_PROFILE_SHARING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_RCP_PROFILE_SHARING)) {
                WsResponseObject profileSharingResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (profileSharingResponse != null && StringUtils.equalsIgnoreCase
                        (profileSharingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    File vcfFile = new File(this.getExternalFilesDir(null), contactName + ".vcf");
                    FileWriter fw;
                    try {
                        fw = new FileWriter(vcfFile);
                        fw.write(profileSharingResponse.getProfileSharingData());
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
//                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                        StrictMode.setVmPolicy(builder.build());
//                    }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/x-vcard");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile
                            (ProfileDetailActivity.this,
                                    BuildConfig.APPLICATION_ID + ".provider",
                                    vcfFile));
                    startActivity(sendIntent);

                } else {
                    if (profileSharingResponse != null) {
                        Log.e("error response", profileSharingResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                profileSharingResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }

            }
            //</editor-fold>

            // <editor-fold desc="REQ_PROFILE_RATING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_RATING)) {
                WsResponseObject profileRatingResponse = (WsResponseObject) data;
                if (profileRatingResponse != null && StringUtils.equalsIgnoreCase
                        (profileRatingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, getString(R.string
                            .rating_submit));

                    if (profileRatingResponse.getProfileRating() != null) {

                        Rating responseRating = profileRatingResponse.getProfileRating();

                        Comment comment = new Comment();
                        comment.setRcProfileMasterPmId(responseRating.getPrToPmId());
                        comment.setCrmStatus(responseRating.getPrStatus());
                        comment.setCrmRating(responseRating.getPrRatingStars());
                        comment.setCrmType(TableCommentMaster.COMMENT_TYPE_RATING);
                        comment.setCrmCloudPrId(String.valueOf(responseRating.getPrId()));
                        comment.setCrmComment(responseRating.getPrComment());
                        comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(responseRating
                                .getCreatedAt()));
                        comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(responseRating
                                .getCreatedAt()));

                        TableCommentMaster tableCommentMaster = new TableCommentMaster
                                (databaseHandler);
                        tableCommentMaster.addComment(comment);

                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        tableProfileMaster.updateUserProfileRating(pmId, responseRating
                                .getProfileRating(), responseRating.getTotalProfileRateUser());

                        ratingUser.setRating(Float.parseFloat(responseRating.getProfileRating()));
                        textUserRating.setText(responseRating.getTotalProfileRateUser());
                    }
                } else {
                    if (profileRatingResponse != null) {
                        Log.e("error response", profileRatingResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                profileRatingResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_SEND_INVITATION">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_SEND_INVITATION)) {
                WsResponseObject inviteContactResponse = (WsResponseObject) data;
                if (inviteContactResponse != null && StringUtils.equalsIgnoreCase
                        (inviteContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail,
                            getString(R.string.invitation_sent));
                } else {
                    if (inviteContactResponse != null) {
                        Log.e("error response", inviteContactResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                inviteContactResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "inviteContactResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                getString(R.string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_GET_CALL_LOG_HISTORY_REQUEST">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_CALL_LOG_HISTORY_REQUEST)) {
                // Log.i("HistoryServiceCalled", "Call received");
                WsResponseObject callHistoryResponse = (WsResponseObject) data;
                progressBarLoadCallLogs.setVisibility(View.GONE);
                if (callHistoryResponse != null && StringUtils.equalsIgnoreCase
                        (callHistoryResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<CallLogType> oldHistoryList = callHistoryResponse
                            .getArrayListCallLogHistory();
                    if (oldHistoryList != null && oldHistoryList.size() > 0) {
                        // Log.i("HistoryServiceCalled", "Data Received");
                        rippleViewOldRecords.setVisibility(View.VISIBLE);
                        ArrayList<CallLogType> listToAppend = new ArrayList<>();
                        for (int i = 0; i < oldHistoryList.size(); i++) {
                            CallLogType callLogType = oldHistoryList.get(i);
                            String number = callLogType.getNumber();
                            if (number.startsWith("91"))
                                number = "+" + number;
                            callLogType.setHistoryNumber(number);
                            callLogType.setHistoryNumberType(callLogType.getNumberType());
                            callLogType.setHistoryDate(Long.parseLong(callLogType
                                    .getCallDateAndTime()));
                            callLogType.setHistoryType(Integer.parseInt(callLogType.getTypeOfCall
                                    ()));
                            callLogType.setWebDuration(callLogType.getDurationToPass());
                            listToAppend.add(callLogType);
                        }
                        arrayListHistory.addAll(listToAppend);
                        if (callHistoryListAdapter != null) {
                            callHistoryListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Log.i("HistoryServiceCalled", "Message Received");
                        rippleViewOldRecords.setVisibility(View.GONE);
                        Utils.showSuccessSnackBar(this, relativeRootProfileDetail,
                                callHistoryResponse.getMessage());
                    }

                } else {
                    progressBarLoadCallLogs.setVisibility(View.GONE);
                    if (callHistoryResponse != null) {
                        Log.e("error response", callHistoryResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                callHistoryResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "Callhistoryapi null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                        progressBarLoadCallLogs.setVisibility(View.GONE);
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_SET_PRIVACY_SETTING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_SET_PRIVACY_SETTING)) {

                WsResponseObject editProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();

                if (editProfileResponse != null && StringUtils.equalsIgnoreCase
                        (editProfileResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ProfileDataOperation profileDetail = editProfileResponse.getProfileDetail();
                    savePrivacySettingToDb(profileDetail);

                    getDataFromDB();

                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, getString(R.string
                            .str_privacy_setting_update));
                } else {
                    if (editProfileResponse != null) {
                        Log.e("error response", editProfileResponse.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
                                editProfileResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, "Privacy Setting" +
                                " Update failed");
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_PROFILE_PRIVACY_REQUEST">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_PRIVACY_REQUEST)) {
                WsResponseObject editProfileResponse = (WsResponseObject) data;

                Utils.hideProgressDialog();

                if (editProfileResponse != null && StringUtils.equalsIgnoreCase
                        (editProfileResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, editProfileResponse.getMessage());
                } else {
                    if (editProfileResponse != null)
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, editProfileResponse.getMessage());
                    else
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R.string.str_request_sending_fail));
                }
            }
            // </editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.linear_basic_detail_rating)
    public void onRatingClick() {

        if (!StringUtils.equalsIgnoreCase(pmId, "-1") && !displayOwnProfile) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_submit_rating);
            dialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

            TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
            relativeRootRatingDialog = dialog.findViewById(R.id.relative_root_rating_dialog);
            final RatingBar ratingUser = dialog.findViewById(R.id.rating_user);
            TextView textComment = dialog.findViewById(R.id.text_comment);
            final TextView textRemainingCharacters = dialog.findViewById(R.id
                    .text_remaining_characters);
            final EditText inputComment = dialog.findViewById(R.id.input_comment);
            RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);
            Button buttonLeft = dialog.findViewById(R.id.button_left);
            RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
            Button buttonRight = dialog.findViewById(R.id.button_right);

            textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
            textComment.setTypeface(Utils.typefaceRegular(this));
            textRemainingCharacters.setTypeface(Utils.typefaceLight(this));
            inputComment.setTypeface(Utils.typefaceRegular(this));
            buttonLeft.setTypeface(Utils.typefaceRegular(this));
            buttonRight.setTypeface(Utils.typefaceRegular(this));

            Utils.setRatingColor(ProfileDetailActivity.this, ratingUser);

            textDialogTitle.setText(String.format("%s %s", StringUtils.upperCase(getString(R
                    .string.text_rate)), StringUtils.upperCase(contactName)));
            textRemainingCharacters.setText(String.format(Locale.ENGLISH, "%d %s", getResources()
                    .getInteger(R.integer.max_comment_length), getString(R.string
                    .characters_left)));

            buttonRight.setText(R.string.action_submit);
            buttonLeft.setText(R.string.action_cancel);

            rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    dialog.dismiss();
                }
            });

            rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (ratingUser.getRating() > 0) {
                        submitRating(String.valueOf(ratingUser.getRating()), inputComment.getText
                                ().toString());
                        dialog.dismiss();

                        isRatingUpdate = true;

                    } else {
                        Utils.showErrorSnackBar(ProfileDetailActivity.this,
                                relativeRootRatingDialog, getString(R.string.please_fill_stars));
                    }
                }
            });

            inputComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int characters = getResources().getInteger(R.integer.max_comment_length) -
                            charSequence.toString().length();
                    textRemainingCharacters.setText(String.format(Locale.getDefault(), "%d%s",
                            characters, characters == 1 ?
                                    " " + getString(R.string.text_character) :
                                    " " + getString(R.string.characters_left)));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            dialog.show();

        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    @SuppressWarnings("unused")
    private void profileLayoutVisibility() {
        relativeContactDetails.setVisibility(View.GONE);
        relativeCallHistory.setVisibility(View.VISIBLE);
//        textIconHistory.setTypeface(Utils.typefaceIcons(this));

    }

    private void getIntentDetails(Intent intent) {
        if (intent != null) {

            if (intent.hasExtra(AppConstants.EXTRA_FROM_NOTI_PROFILE)) {
                isFromNotification = intent.getBooleanExtra(AppConstants.EXTRA_FROM_NOTI_PROFILE,
                        false);
            }
            if (intent.hasExtra(AppConstants.EXTRA_DIALOG_CALL_LOG_INSTANCE)) {
                isDialogCallLogInstance = intent.getBooleanExtra(AppConstants
                        .EXTRA_DIALOG_CALL_LOG_INSTANCE, false);
            }

            if (intent.hasExtra(AppConstants.EXTRA_RCP_VERIFIED_ID)) {
                callLogRcpVerfiedId = intent.getStringExtra(AppConstants.EXTRA_RCP_VERIFIED_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER)) {
                historyNumber = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NAME)) {
                historyName = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NAME);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE)) {
                profileActivityCallInstance = intent.getBooleanExtra(AppConstants
                        .EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, false);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_DATE)) {
                historyDate = intent.getLongExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, 0);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_UNIQUE_ID)) {
                hashMapKey = intent.getStringExtra(AppConstants.EXTRA_CALL_UNIQUE_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID)) {
                uniqueContactId = intent.getStringExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE)) {
                profileThumbnail = intent.getStringExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME)) {
                callLogCloudName = intent.getStringExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME);
            }

            if (intent.hasExtra(AppConstants.EXTRA_IS_RCP_USER)) {
                isCallLogRcpUser = intent.getBooleanExtra(AppConstants.EXTRA_IS_RCP_USER, false);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
                if (!StringUtils.isEmpty(pmId)) {
                    if (!pmId.equalsIgnoreCase("-1") && !pmId.equalsIgnoreCase(getUserPmId())) {
                        if (!Utils.isNetworkAvailable(this)) {

                            HashMap<String, String> mapProfileViews = new HashMap<>();
                            if (Utils.getHashMapPreference(this, AppConstants
                                    .PREF_PROFILE_VIEWS) != null) {
                                mapProfileViews.putAll(Utils.getHashMapPreference(this, AppConstants
                                        .PREF_PROFILE_VIEWS));
                            }
                            if (mapProfileViews.containsKey(pmId)) {
                                int count = Integer.parseInt(mapProfileViews.get(pmId));
                                mapProfileViews.put(pmId, String.valueOf(++count));
                            } else {
                                mapProfileViews.put(pmId, "1");
                            }
                            Utils.setHashMapPreference(this, AppConstants.PREF_PROFILE_VIEWS,
                                    mapProfileViews);
                        }
                    }
                } else {
                    pmId = "-1";
                }
            } else {
                pmId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PHONE_BOOK_ID)) {
                phoneBookId = intent.getStringExtra(AppConstants.EXTRA_PHONE_BOOK_ID);
            } else {
                phoneBookId = "-1";
            }
            Log.i("phonebookId", phoneBookId);

            if (intent.hasExtra(AppConstants.EXTRA_IS_FROM_FAVOURITE)) {
                isFromFavourite = intent.getBooleanExtra(AppConstants.EXTRA_IS_FROM_FAVOURITE,
                        false);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            } else {
                contactName = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL)) {
                thumbnailUrl = intent.getStringExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL);
            } else {
                thumbnailUrl = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME)) {
                cloudContactName = intent.getStringExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME);
                cloudContactName = StringUtils.substring(cloudContactName, 2, cloudContactName
                        .length() - 1);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE)) {
                isHideFavourite = true;
                checkNumberFavourite = intent.getStringExtra(AppConstants
                        .EXTRA_CHECK_NUMBER_FAVOURITE);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_POSITION)) {
                listClickedPosition = intent.getIntExtra(AppConstants.EXTRA_CONTACT_POSITION, -1);
            }
        }

        if (phoneBookId.equals("-1"))
            phoneBookId = getStarredStatusFromNumber(historyNumber);
    }

    private String getStarredStatusFromNumber(String phoneNumber) {
        String numberId, rawId = "";
        try {

            ContentResolver contentResolver = getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = contentResolver.query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            rawId = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rawId;
    }

    private void layoutVisibility() {
        if (profileActivityCallInstance) {
//            new GetRCPNameAndProfileImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            if (buttonViewOldRecords != null) {
                buttonViewOldRecords.setTypeface(Utils.typefaceRegular(this));
            }

            if (rippleViewOldRecords != null) {
                rippleViewOldRecords.setVisibility(View.GONE);
                rippleViewOldRecords.setOnRippleCompleteListener(this);
            }

            if (recyclerCallHistory != null) {
                recyclerCallHistory.setLayoutManager(mLinearLayoutManager);
                recyclerCallHistory.setNestedScrollingEnabled(false);
            }

            relativeContactDetails.setVisibility(View.GONE);
            if (relativeCallHistory != null) {
                relativeCallHistory.setVisibility(View.VISIBLE);
            }
            rippleCallLog.setVisibility(View.GONE);
            setCallLogHistoryDetails();

        } else {

            relativeContactDetails.setVisibility(View.VISIBLE);
            if (relativeCallHistory != null) {
                relativeCallHistory.setVisibility(View.GONE);
            }

            if (displayOwnProfile) {

                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                final UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                        .parseInt(pmId));
                textFullScreenText.setText(userProfile.getPmFirstName() + " " + userProfile
                        .getPmLastName());

                if (!TextUtils.isEmpty(userProfile.getPmProfileImage())) {
                    Glide.with(this)
                            .load(userProfile.getPmProfileImage())
                            .placeholder(R.drawable.home_screen_profile)
                            .error(R.drawable.home_screen_profile)
                            .bitmapTransform(new CropCircleTransformation(this))
//                        .override(400, 400)
                            .override(512, 512)
                            .into(imageProfile);

                } else {
                    imageProfile.setImageResource(R.drawable.home_screen_profile);
                }

                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (StringUtils.length(userProfile.getPmProfileImage()) > 0) {
                            Utils.zoomImageFromThumb(ProfileDetailActivity.this, imageProfile,
                                    userProfile.getPmProfileImage(),
                                    frameImageEnlarge, imageEnlarge, frameContainer);
                        }

                    }
                });

            } else {
                textFullScreenText.setText(contactName);
                if (StringUtils.length(thumbnailUrl) > 0) {
                    Glide.with(this)
                            .load(thumbnailUrl)
                            .placeholder(R.drawable.home_screen_profile)
                            .error(R.drawable.home_screen_profile)
                            .bitmapTransform(new CropCircleTransformation(this))
//                        .override(400, 400)
                            .override(512, 512)
                            .into(imageProfile);

                } else {
                    imageProfile.setImageResource(R.drawable.home_screen_profile);
                }

                imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (StringUtils.length(thumbnailUrl) > 0) {
                            Utils.zoomImageFromThumb(ProfileDetailActivity.this, imageProfile,
                                    thumbnailUrl,
                                    frameImageEnlarge, imageEnlarge, frameContainer);
                        }
                    }
                });

            }
            if (StringUtils.length(cloudContactName) > 0) {
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textName.setText(cloudContactName);
                linearBasicDetailRating.setVisibility(View.VISIBLE);
                rippleInvite.setVisibility(View.GONE);
            } else if (StringUtils.length(callLogCloudName) > 0) {
                String phoneBookName = getNameFromNumber(historyNumber);
                if (!StringUtils.equalsIgnoreCase(phoneBookName, contactName)) {
                    textFullScreenText.setText(phoneBookName);
                }
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                if (!StringUtils.isEmpty(callLogCloudName)) {
                    textName.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    textName.setText(callLogCloudName);
                }
                linearBasicDetailRating.setVisibility(View.VISIBLE);
                rippleInvite.setVisibility(View.GONE);
            } else {
                if (StringUtils.equalsIgnoreCase(pmId, "-1")) {
                    String phoneBookName = getNameFromNumber(historyNumber);
                    if (StringUtils.length(phoneBookName) > 0) {
                        if (!StringUtils.equalsIgnoreCase(phoneBookName, contactName)) {
                            textFullScreenText.setText(phoneBookName);
                        }
                    } else {
                        /*// Updated on 17/08/2019
                        if (historyNumber.startsWith("+91")) {

                        } else if (historyNumber.startsWith("0")) {
                            historyNumber = Utils.getFormattedNumber(ProfileDetailActivity.this,
                            historyNumber);
                        } else {
                            historyNumber = Utils.getFormattedNumber(ProfileDetailActivity.this,
                            historyNumber);
                        }
                        textFullScreenText.setText(historyNumber);*/
                    }
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorBlack));
                    linearBasicDetailRating.setVisibility(View.GONE);
                    if (Utils.getBooleanPreference(this, AppConstants.PREF_CONTACT_SYNCED, false)) {
                        rippleInvite.setVisibility(View.VISIBLE);
                    } else {
                        rippleInvite.setVisibility(View.GONE);
                    }
                } else {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorAccent));
                    linearBasicDetailRating.setVisibility(View.VISIBLE);
                    rippleInvite.setVisibility(View.GONE);
                }
                textName.setVisibility(View.GONE);
            }

            imageRightCenter.setImageResource(R.drawable.ic_action_share);
            imageRightCenter.setTag(TAG_IMAGE_SHARE);

         /*   if (displayOwnProfile) {
                textToolbarTitle.setText(getString(R.string.title_my_profile));
                linearCallSms.setVisibility(View.GONE);
                imageRightLeft.setImageResource(R.drawable.ic_action_edit);
                rippleActionRightRight.setVisibility(View.GONE);
                imageRightLeft.setTag(TAG_IMAGE_EDIT);
                imageRightLeft.setVisibility(View.VISIBLE);
            } else {
                textToolbarTitle.setText(getString(R.string.str_profile_deails));
                linearCallSms.setVisibility(View.VISIBLE);
            }*/

            if (isHideFavourite) {
                if (displayOwnProfile) {
                    rippleActionRightLeft.setEnabled(true);
                } else {
                    rippleActionRightLeft.setEnabled(false);
                }
                /*if (checkNumberFavourite != null && arrayListFavouriteContacts.contains
                        (checkNumberFavourite)) {*/
                if (checkNumberFavourite != null) {
                    if (phoneBookContacts.getStarredStatusFromRawId(checkNumberFavourite)) {
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                        rippleActionRightLeft.setVisibility(View.VISIBLE);
                    } else {
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                        rippleActionRightLeft.setVisibility(View.GONE);
                    }
                } else {
                    imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                    rippleActionRightLeft.setVisibility(View.GONE);
                }
            }

            if (displayOwnProfile) {
                textToolbarTitle.setText(getString(R.string.title_my_profile));
                linearCallSms.setVisibility(View.GONE);
                relativeProfilePercentage.setVisibility(View.VISIBLE);
                imageRightLeft.setImageResource(R.drawable.ic_action_edit);
                rippleActionRightRight.setVisibility(View.GONE);
                imageRightLeft.setTag(TAG_IMAGE_EDIT);
                imageRightLeft.setVisibility(View.VISIBLE);
            } else {
                textToolbarTitle.setText(getString(R.string.str_profile_deails));
                linearCallSms.setVisibility(View.VISIBLE);
                relativeProfilePercentage.setVisibility(View.GONE);
            }

        }
    }

    private void displayWalkThrough() {

        frameTutorial.setVisibility(View.VISIBLE);

        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(FrameLayout
                .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        final LinearLayout linearDescription = new LinearLayout(this);
        linearDescription.setLayoutParams(layoutParamsFrame);
        linearDescription.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout.LayoutParams descriptionLayoutParam = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descriptionLayoutParam.leftMargin = (int) getResources().getDimension(R.dimen
                .activity_horizontal_margin);
        descriptionLayoutParam.rightMargin = (int) getResources().getDimension(R.dimen
                .activity_horizontal_margin);

        final LinearLayout.LayoutParams headerLayoutParam = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headerLayoutParam.leftMargin = (int) getResources().getDimension(R.dimen
                .activity_horizontal_margin);
        headerLayoutParam.rightMargin = (int) getResources().getDimension(R.dimen
                .activity_horizontal_margin);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                descriptionLayoutParam.topMargin = includeToolbar.getHeight() + (int)
                        getResources().getDimension(R.dimen.nav_header_height);
            }
        }, 2000);*/

        descriptionLayoutParam.topMargin = (int) (Utils.getDeviceHeight(ProfileDetailActivity
                .this) / 2.5);

        textTapContinue.setText("TAP ON THE EDIT ICON");

        TextView textHeader = new TextView(this);
        textHeader.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        textHeader.setTypeface(Utils.typefaceBold(this));
        textHeader.setLayoutParams(descriptionLayoutParam);
        textHeader.setTextSize(18);
        textHeader.setPaintFlags(textHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textHeader.setText("Edit Profile");
        linearDescription.addView(textHeader);

        TextView textDescription = new TextView(this);
        textDescription.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        textDescription.setTypeface(Utils.typefaceRegular(this));
        textDescription.setLayoutParams(headerLayoutParam);
        textDescription.setTextSize(14);
        textDescription.setText("Add your info for your Contacts\nto know you better!");
        linearDescription.addView(textDescription);

        imageTutorialEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frameTutorial.setVisibility(View.GONE);
                    }
                }, 700);
                startActivityIntent(ProfileDetailActivity.this, EditProfileActivity.class, null);
            }
        });

        frameTutorial.addView(linearDescription);
    }

    private void init() {
//        toolbarProfileDetail = ButterKnife.findById(includeToolbar, R.id.toolbar_profile_detail);
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageRelation = ButterKnife.findById(includeToolbar, R.id.image_relation);
        imageRightLeft = ButterKnife.findById(includeToolbar, R.id.image_right_left);
        imageRightCenter = ButterKnife.findById(includeToolbar, R.id.image_right_center);
        imageRightRight = ButterKnife.findById(includeToolbar, R.id.image_right_right);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRelation = ButterKnife.findById(includeToolbar, R.id.ripple_action_relation);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        Utils.setRoundedCornerBackground(buttonInvite, ContextCompat.getColor
                (ProfileDetailActivity.this, R.color.colorAccent), 5, 0, ContextCompat.getColor
                (ProfileDetailActivity.this, R.color.colorAccent));

        imageRightRight.setImageResource(R.drawable.ic_action_more_vertical);

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        if (recyclerViewEducation != null) {
            recyclerViewEducation.setLayoutManager(new LinearLayoutManager(this));
        }
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewContactNumber.setNestedScrollingEnabled(false);
        recyclerViewEmail.setNestedScrollingEnabled(false);
        recyclerViewWebsite.setNestedScrollingEnabled(false);
        if (recyclerViewEducation != null) {
            recyclerViewEducation.setNestedScrollingEnabled(false);
        }
        recyclerViewAddress.setNestedScrollingEnabled(false);
        recyclerViewEvent.setNestedScrollingEnabled(false);
        recyclerViewSocialContact.setNestedScrollingEnabled(false);

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFullScreenText.setTypeface(Utils.typefaceSemiBold(this));
        textName.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceIcons(this));
        textTime.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));

        textFullScreenText.setSelected(true);
        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRelation.setOnRippleCompleteListener(this);
        rippleActionRightLeft.setOnRippleCompleteListener(this);
        rippleActionRightCenter.setOnRippleCompleteListener(this);
        rippleActionRightRight.setOnRippleCompleteListener(this);
        rippleCallLog.setOnRippleCompleteListener(this);
        rippleSms.setOnRippleCompleteListener(this);
        rippleInvite.setOnRippleCompleteListener(this);


        buttonInvite.setTypeface(Utils.typefaceRegular(this));

        mLinearLayoutManager = new LinearLayoutManager(this);

        Utils.setRatingColor(ProfileDetailActivity.this, ratingUser);

//        LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
//        // Filled stars
//        Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(this, R.color
//                .vivid_yellow));
//        // half stars
//        Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(this, android.R
//                .color.darker_gray));
//        // Empty stars
//        Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(this, android.R
//                .color.darker_gray));

        layoutVisibility();

        if (!displayOwnProfile) {
            if (!StringUtils.equalsIgnoreCase(pmId, "-1")) {
                // RC Profile
//                getDataFromDB();
                if (Utils.isNetworkAvailable(ProfileDetailActivity.this) &&
                        !profileActivityCallInstance) {
                    //call service
                    cardContactDetails.setVisibility(View.GONE);
                    cardOtherDetails.setVisibility(View.GONE);
                    getProfileDetails();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Utils.hideProgressDialog();
                            if (asyncGetProfileDetails != null) {
                                asyncGetProfileDetails.cancel(true);
                            }
                            getDataFromDB();
                        }
                    }, 4000);

                } else {
                    getDataFromDB();
                }
//            }
            } else {
                // Non-RC Profile
                setUpView(null);
                rippleActionRelation.setVisibility(View.GONE);
            }
        } else {
            /*if (Utils.getBooleanPreference(ProfileDetailActivity.this, AppConstants
                    .PREF_SHOW_WALK_THROUGH, true)) {
                displayWalkThrough();
            } else {
                frameTutorial.setVisibility(View.GONE);
            }*/
            if (frameTutorial != null) {
                frameTutorial.setVisibility(View.GONE);
            }
        }

        initSwipe();

    }

    private void getDataFromDB() {
        ProfileDataOperation profileDataOperation = queryManager.getRcProfileDetail
                (this, pmId);
        setUpView(profileDataOperation);
    }

    private void setCallLogHistoryDetails() {

        textNoHistoryToShow.setTypeface(Utils.typefaceRegular(ProfileDetailActivity.this));
        textCallHistory.setTypeface(Utils.typefaceRegular(ProfileDetailActivity.this));


        if (!TextUtils.isEmpty(historyName)) {
            if (StringUtils.containsOnly(historyName, "\\d+")) {
//                textToolbarTitle.setText("Unknown number");
//                textToolbarTitle.setText(historyName);
                //17/06/2017 : toolBarTitle text is changed for Call-logs as per Avijit Sir's
                // suggestion
//                textToolbarTitle.setText(getString(R.string.str_profile_deails));

                // 11/07/2017 : toolBarTitle text is changed again for Call-logs as per Avijit
                // Sir's suggestion
                textToolbarTitle.setText(getString(R.string.call_history_toolbar_title));

            } else {
//                textToolbarTitle.setText(historyName);
                //17/06/2017 : toolBarTitle text is changed for Call-logs as per Avijit Sir's
                // suggestion
//                textToolbarTitle.setText(getString(R.string.str_profile_deails));
                // 11/07/2017 : toolBarTitle text is changed again for Call-logs as per Avijit
                // Sir's suggestion
                textToolbarTitle.setText(getString(R.string.call_history_toolbar_title));
            }
            textFullScreenText.setTypeface(Utils.typefaceBold(this));
            if (isCallLogRcpUser) {
                rippleInvite.setVisibility(View.GONE);
                linearBasicDetailRating.setVisibility(View.VISIBLE);
                if (StringUtils.isEmpty(callLogCloudName)) {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorAccent));
                    textFullScreenText.setText(historyName);
                } else {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorBlack));
//                    textFullScreenText.setText(historyName);
                    // updated on 17/08/2017
                    String phoneBookName = getNameFromNumber(historyNumber);
                    if (StringUtils.length(phoneBookName) > 0) {
                        textFullScreenText.setText(phoneBookName);
                    } else {
                        if (StringUtils.length(historyName) > 0) {
                            textFullScreenText.setText(historyName);
                        } else {
                            //Updated on 17/08/2017
                            if (historyNumber.startsWith("+91")) {

                            } else if (historyNumber.startsWith("0")) {
                                historyNumber = Utils.getFormattedNumber(ProfileDetailActivity
                                        .this, historyNumber);
                            } else {
                                historyNumber = Utils.getFormattedNumber(ProfileDetailActivity
                                        .this, historyNumber);
                            }
                            textFullScreenText.setText(historyNumber);
                        }
                    }
                    textName.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    textName.setText(callLogCloudName);
                }
            } else {
                rippleInvite.setVisibility(View.VISIBLE);
                linearBasicDetailRating.setVisibility(View.GONE);
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
//                textFullScreenText.setText(historyName);
                String phoneBookName = getNameFromNumber(historyNumber);
                if (StringUtils.length(phoneBookName) > 0) {
                    textFullScreenText.setText(phoneBookName);
                } else {
                    if (StringUtils.length(historyName) > 0) {
                        textFullScreenText.setText(historyName);
                    } else {
                        //Updated on 17/08/2017
                        if (historyNumber.startsWith("+91")) {

                        } else if (historyNumber.startsWith("0")) {
                            historyNumber = Utils.getFormattedNumber(ProfileDetailActivity.this,
                                    historyNumber);
                        } else {
                            historyNumber = Utils.getFormattedNumber(ProfileDetailActivity.this,
                                    historyNumber);
                        }
                        textFullScreenText.setText(historyNumber);
                    }
                }
            }

        } else {
            if (!TextUtils.isEmpty(historyNumber)) {
                rippleInvite.setVisibility(View.GONE);
                linearBasicDetailRating.setVisibility(View.GONE);
                textFullScreenText.setTypeface(Utils.typefaceBold(this));
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textFullScreenText.setText(historyNumber);
//                textToolbarTitle.setText("Unknown number");
//                textToolbarTitle.setText(historyNumber);
                //17/06/2017 : toolBarTitle text is changed for Call-logs as per Avijit Sir's
                // suggestion
//                textToolbarTitle.setText(getString(R.string.str_profile_deails));
                // 11/07/2017 : toolBarTitle text is changed again for Call-logs as per Avijit
                // Sir's suggestion
                textToolbarTitle.setText(getString(R.string.call_history_toolbar_title));
                if (StringUtils.equalsIgnoreCase(callLogRcpVerfiedId, "0")) {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .textColorBlue));
                    if (!StringUtils.isEmpty(callLogCloudName)) {
                        textName.setTextColor(ContextCompat.getColor(this, R.color.textColorBlue));
                        textName.setText(callLogCloudName);
                    }
                } else if (StringUtils.equalsIgnoreCase(callLogRcpVerfiedId, "1")) {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorAccent));
                    if (!StringUtils.isEmpty(callLogCloudName)) {
                        textName.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                        textName.setText(callLogCloudName);
                    }
                } else {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorBlack));
                    textName.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                    if (!StringUtils.isEmpty(callLogCloudName)) {
                        textName.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                    }
                }
            }

        }

        if (!TextUtils.isEmpty(profileThumbnail)) {
            Glide.with(this)
                    .load(profileThumbnail)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(ProfileDetailActivity.this))
                    .override(512, 512)
                    .into(imageProfile);
        } else {
            imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.length(profileThumbnail) > 0) {
                    Utils.zoomImageFromThumb(ProfileDetailActivity.this, imageProfile,
                            profileThumbnail,
                            frameImageEnlarge, imageEnlarge, frameContainer);
                }

            }
        });

        imageRightCenter.setImageResource(R.drawable.ic_phone);
        imageRightCenter.setTag(TAG_IMAGE_CALL);


        if (!TextUtils.isEmpty(historyName)) {
            imageRightLeft.setVisibility(View.VISIBLE);
            Cursor contactFavouriteCursor = phoneBookContacts.getStarredStatus(hashMapKey);

            if (contactFavouriteCursor != null && contactFavouriteCursor.getCount() > 0) {
                while (contactFavouriteCursor.moveToNext()) {
                    isFavourite = contactFavouriteCursor.getInt(contactFavouriteCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED));
                }
                contactFavouriteCursor.close();
            }

            if (isFavourite == 0) {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
            } else {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
            }
        } else {
            imageRightLeft.setVisibility(View.GONE);
        }
    }

    private void setUpView(final ProfileDataOperation profileDetail) {

        try {
            Utils.hideProgressDialog();
            cardContactDetails.setVisibility(View.VISIBLE);
            cardOtherDetails.setVisibility(View.VISIBLE);

            profileDataOperationVcard = new ProfileDataOperation();

            profileDataOperationVcard.setPbNameFirst(contactName);

            //<editor-fold desc="Favourite">

            if (!displayOwnProfile && !isHideFavourite) {

                Cursor contactFavouriteCursor = phoneBookContacts.getStarredStatus(phoneBookId);

                if (contactFavouriteCursor != null && contactFavouriteCursor.getCount() > 0) {
                    while (contactFavouriteCursor.moveToNext()) {
                        isFavourite = contactFavouriteCursor.getInt(contactFavouriteCursor
                                .getColumnIndex(ContactsContract.Contacts.STARRED));
                    }
                    contactFavouriteCursor.close();
                }

                if (isFavourite == 0) {
                    imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                    imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
                } else {
                    imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                    imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
                }
            }

            //</editor-fold>

            //<editor-fold desc="Joining Date">
       /* if (profileDetail != null) {
            String joiningDate = StringUtils.defaultString(Utils.convertDateFormat(profileDetail
                    .getJoiningDate(), "yyyy-MM-dd HH:mm:ss", "dd'th' MMM, yyyy"), "-");
            textJoiningDate.setText("Joining Date:- " + joiningDate);
        }*/
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
            ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganization = new
                    ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization
                        (phoneBookId);
                ArrayList<ProfileDataOperationOrganization>
                        arrayListPhoneBookOrganizationOperation =
                        new ArrayList<>();

                if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
                    while (contactOrganizationCursor.moveToNext()) {

                        ProfileDataOperationOrganization organization = new
                                ProfileDataOperationOrganization();
                        ProfileDataOperationOrganization organizationOperation = new
                                ProfileDataOperationOrganization();

                        organization.setOrgName(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.COMPANY)));
                        organization.setOrgJobTitle(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.TITLE)));
                        organization.setOrgRcpType(String.valueOf(IntegerConstants
                                .RCP_TYPE_LOCAL_PHONE_BOOK));
                        organizationOperation.setOrgName(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.COMPANY)));
                        organizationOperation.setOrgJobTitle(contactOrganizationCursor.getString
                                (contactOrganizationCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Organization.TITLE)));
                        organization.setIsVerify(0);

                        if (!arrayListOrganization.contains(organization)) {
                            arrayListPhoneBookOrganization.add(organization);
                        }

                        arrayListPhoneBookOrganizationOperation.add(organizationOperation);
                    }
                    contactOrganizationCursor.close();
                    profileDataOperationVcard.setPbOrganization
                            (arrayListPhoneBookOrganizationOperation);
                }
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListOrganization) || !Utils
                    .isArraylistNullOrEmpty(arrayListPhoneBookOrganization)) {

                final ArrayList<ProfileDataOperationOrganization> tempOrganization = new
                        ArrayList<>();
                tempOrganization.addAll(arrayListOrganization);
                tempOrganization.addAll(arrayListPhoneBookOrganization);

                linearOrganizationDetail.setVisibility(View.VISIBLE);

                if (tempOrganization.size() == 1) {
                    textViewAllOrganization.setVisibility(View.GONE);
                    textTime.setVisibility(View.VISIBLE);
                } else {
                    textViewAllOrganization.setVisibility(View.VISIBLE);
                    textTime.setVisibility(View.GONE);
                }

                if (arrayListOrganization.size() > 0) {
                    textDesignation.setTextColor(ContextCompat.getColor(ProfileDetailActivity
                            .this, R.color.colorAccent));
                    textOrganization.setTextColor(ContextCompat.getColor(ProfileDetailActivity
                            .this, R.color.colorAccent));
                    textTime.setTextColor(ContextCompat.getColor(ProfileDetailActivity
                            .this, R.color.colorAccent));
                } else {
                    textDesignation.setTextColor(ContextCompat.getColor(ProfileDetailActivity
                            .this, R.color.colorBlack));
                    textOrganization.setTextColor(ContextCompat.getColor(ProfileDetailActivity
                            .this, R.color.colorBlack));
                    textTime.setVisibility(View.GONE);
                }

                if (MoreObjects.firstNonNull(tempOrganization.get(0).getIsVerify(), 0) ==
                        IntegerConstants.RCP_TYPE_PRIMARY) {

                    textOrganization.setText(Utils.setMultipleTypeface(ProfileDetailActivity.this,
                            tempOrganization.get(0).getOrgName() + " " + getString(R.string
                                    .im_icon_unverify), 0, (StringUtils.length(tempOrganization.get(0)
                                    .getOrgName()) + 1), ((StringUtils.length(tempOrganization.get(0).
                                    getOrgName()) + 1) + 1)));

//                    textOrganization.setText(tempOrganization.get(0).getOrgName());
//                    textOrganization.setCompoundDrawablesWithIntrinsicBounds(0, 0,
//                            R.drawable.ico_relation_single_tick_green_svg, 0);

                } else {
                    textOrganization.setText(tempOrganization.get(0).getOrgName());
                }

                textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());

                if (StringUtils.equalsIgnoreCase(tempOrganization.get(0).getOrgToDate(), "")) {
                    if (!StringUtils.isEmpty(tempOrganization.get(0).getOrgFromDate())) {
                        String formattedFromDate = Utils.convertDateFormat(tempOrganization.get
                                        (0).getOrgFromDate(),
                                "yyyy-MM-dd", Utils.getEventDateFormat(tempOrganization
                                        .get(0).getOrgFromDate()));

                        textTime.setText(String.format("%s to Present ", formattedFromDate));
                    } else {
                        textTime.setVisibility(View.GONE);
                    }
                } else {
                    if (!StringUtils.isEmpty(tempOrganization.get(0).getOrgFromDate()) &&
                            !StringUtils.isEmpty(tempOrganization.get(0).getOrgToDate())) {
                        String formattedFromDate = Utils.convertDateFormat(tempOrganization.get
                                        (0).getOrgFromDate(),
                                "yyyy-MM-dd", Utils.getEventDateFormat(tempOrganization
                                        .get(0).getOrgFromDate()));
                        String formattedToDate = Utils.convertDateFormat(tempOrganization.get(0)
                                        .getOrgToDate(),
                                "yyyy-MM-dd", Utils.getEventDateFormat(tempOrganization
                                        .get(0).getOrgToDate()));

                        textTime.setText(String.format("%s to %s ", formattedFromDate,
                                formattedToDate));
                    }
                }

                textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAllOrganizations(tempOrganization);
                    }
                });

                textOrganization.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tempOrganization.get(0).getIsVerify() == 1) {
                            String orgPublicLink = BuildConfig.ORANISATION_PUBLIC_LINK + tempOrganization.get(0).getOrgEntId();
                            if (!StringUtils.isEmpty(orgPublicLink)) {
                                String url = orgPublicLink;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }
                    }
                });

            } else {
                linearOrganizationDetail.setVisibility(View.INVISIBLE);
            }
            //</editor-fold>

            //<editor-fold desc="User Rating">
            if (profileDetail != null) {

                if (displayOwnProfile) {

                    buttonRequestRating.setVisibility(View.GONE);
                    buttonPrivacyRating.setVisibility(View.VISIBLE);
                    textUserRating.setText(profileDetail.getTotalProfileRateUser());
                    ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));

                    switch (profileDetail.getProfileRatingPrivacy()) {
                        case 1:
                            //everyone
                            buttonPrivacyRating.setImageResource(R.drawable.ico_privacy_public);
                            break;
                        case 2:
                            //my contacts
                            buttonPrivacyRating.setImageResource(R.drawable.ico_privacy_my_contact);
                            break;
                        case 3:
                            //only me
                            buttonPrivacyRating.setImageResource(R.drawable.ico_privacy_onlyme);
                            break;

                    }

                    buttonPrivacyRating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PrivacySettingPopupDialog privacySettingPopupDialog = new
                                    PrivacySettingPopupDialog(null, ProfileDetailActivity.this,
                                    new PrivacySettingPopupDialog.DialogCallback() {
                                        @Override
                                        public void onSettingSaved(ProfileDetailAdapter
                                                                           .ProfileDetailViewHolder view, int whichItem, int newPrivacy, int
                                                                           itemPosition, int
                                                                           oldPrivacy, String
                                                                           cloudId) {

                                            if (oldPrivacy == newPrivacy + 1) {
                                                return;
                                            }

                                            WsRequestObject wsRequestObject = new WsRequestObject();

                                            PrivacyEntityItem privacyEntityItem = new
                                                    PrivacyEntityItem();
                                            privacyEntityItem.setId(cloudId);
                                            privacyEntityItem.setValue(newPrivacy + 1);
                                            ArrayList<PrivacyEntityItem> privacyEntityItems = new
                                                    ArrayList<>();
                                            privacyEntityItems.add(privacyEntityItem);
                                            ArrayList<PrivacyDataItem> privacyItems = new
                                                    ArrayList<>();
                                            PrivacyDataItem privacyDataItem = new PrivacyDataItem();
                                            switch (whichItem) {
                                                case AppConstants.RATING:
                                                    privacyDataItem.setPbRating(privacyEntityItems);
                                                    break;
                                            }
                                            privacyItems.add(privacyDataItem);
                                            wsRequestObject.setPrivacyData(privacyItems);
//        wsRequestObject.setPmId(pmId);
                                            if (Utils.isNetworkAvailable(ProfileDetailActivity
                                                    .this)) {
                                                new AsyncWebServiceCall(ProfileDetailActivity.this,
                                                        WSRequestType.REQUEST_TYPE_JSON.getValue(),
                                                        wsRequestObject, null, WsResponseObject
                                                        .class,
                                                        WsConstants
                                                                .REQ_SET_PRIVACY_SETTING,
                                                        ProfileDetailActivity
                                                                .this.getResources().getString(R
                                                                .string
                                                                .msg_please_wait), true)
                                                        .executeOnExecutor
                                                                (AsyncTask.THREAD_POOL_EXECUTOR,
                                                                        BuildConfig.WS_ROOT +
                                                                                WsConstants
                                                                                        .REQ_SET_PRIVACY_SETTING);
                                            } else {
                                                //show no toast
                                                Toast.makeText(ProfileDetailActivity.this,
                                                        ProfileDetailActivity.this.getResources()
                                                                .getString(R.string.msg_no_network),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, AppConstants.RATING, 0, profileDetail.getProfileRatingPrivacy(), "1");
                            privacySettingPopupDialog.setDialogTitle(ProfileDetailActivity.this
                                    .getResources().getString(R
                                            .string.privacy_dialog_title));
                            privacySettingPopupDialog.showDialog();

                        }
                    });
                } else {

                    buttonPrivacyRating.setVisibility(View.GONE);

                    textUserRating.setText(profileDetail.getTotalProfileRateUser());

                    if ((MoreObjects.firstNonNull(profileDetail.getProfileRatingPrivacy(), 0)) == IntegerConstants
                            .PRIVACY_EVERYONE) {

                        linearBasicDetailRating.setEnabled(true);

                        ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
                        buttonRequestRating.setVisibility(View.GONE);

                    } else {

                        linearBasicDetailRating.setEnabled(false);

                        ratingUser.setRating(0);
//                        ratingUser.setEnabled(false);
                        buttonRequestRating.setVisibility(View.VISIBLE);

                        buttonRequestRating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pmTo = Integer.parseInt(pmId);
                                // sendAccessRequest(int toPMId, String carFiledType, String
                                // recordIndexId)
                                sendAccessRequest(pmTo, "profile_rating", "1");
                            }
                        });
                    }
                }
            } else {
                textUserRating.setText("0");
                ratingUser.setRating(0);
//                ratingUser.setEnabled(false);
            }
            //</editor-fold>

            //<editor-fold desc="Phone Number">

            // From Cloud
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
            ArrayList<String> arrayListCloudNumber = new ArrayList<>();

            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                    .getPbPhoneNumber())) {
                arrayListPhoneNumber.addAll(profileDetail.getPbPhoneNumber());
                for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                    String number = Utils.getFormattedNumber(this, arrayListPhoneNumber.get(i)
                            .getPhoneNumber());
                    arrayListCloudNumber.add(number);

                    if (arrayListPhoneNumber.get(i).getPbRcpType() == IntegerConstants
                            .RCP_TYPE_PRIMARY) {
                        contactNumber = arrayListPhoneNumber.get(i).getPhoneNumber();
                    }
                }
            }

            // From PhoneBook
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumber = new
                    ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(phoneBookId);
                ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumberOperation = new
                        ArrayList<>();

                if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                    while (contactNumberCursor.moveToNext()) {

                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();
                        ProfileDataOperationPhoneNumber phoneNumberOperation = new
                                ProfileDataOperationPhoneNumber();

                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this,
                                contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                        phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                (contactNumberCursor, contactNumberCursor.getInt
                                        (contactNumberCursor.getColumnIndex(ContactsContract
                                                .CommonDataKinds.Phone.TYPE))));
                        phoneNumber.setPbRcpType(IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK);

                        if (!arrayListCloudNumber.contains(phoneNumber.getPhoneNumber())) {
                            arrayListPhoneBookNumber.add(phoneNumber);
                        }
                        arrayListPhoneBookNumberOperation.add(phoneNumberOperation);
                        profileContactNumber = phoneNumber.getPhoneNumber();

                    }
                    contactNumberCursor.close();
                    profileDataOperationVcard.setPbPhoneNumber(arrayListPhoneBookNumberOperation);
                }
            }

            tempPhoneNumber = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookNumber)) {
                tempPhoneNumber.addAll(arrayListPhoneNumber);
                tempPhoneNumber.addAll(arrayListPhoneBookNumber);

                linearPhone.setVisibility(View.VISIBLE);
                phoneDetailAdapter = new ProfileDetailAdapter(this,
                        tempPhoneNumber, AppConstants.PHONE_NUMBER, displayOwnProfile, pmId, buttonRequestAll);
                recyclerViewContactNumber.setAdapter(phoneDetailAdapter);
            } else {
                linearPhone.setVisibility(View.GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Email Id">

            // From Cloud
            ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
            ArrayList<String> arrayListCloudEmail = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId
                    ())) {
                arrayListEmail.addAll(profileDetail.getPbEmailId());
                for (int i = 0; i < arrayListEmail.size(); i++) {
                    arrayListCloudEmail.add(arrayListEmail.get(i).getEmEmailId());
                }
            }

            // From PhoneBook
            ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmail = new ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactEmailCursor = phoneBookContacts.getContactEmail(phoneBookId);
                ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmailOperation = new
                        ArrayList<>();

                if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                    while (contactEmailCursor.moveToNext()) {

                        ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();
                        ProfileDataOperationEmail emailIdOperation = new
                                ProfileDataOperationEmail();

                        emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                        emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                                contactEmailCursor.getInt
                                        (contactEmailCursor.getColumnIndex(ContactsContract
                                                .CommonDataKinds.Email.TYPE))));
                        emailId.setEmRcpType(IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK);

                        emailIdOperation.setEmEmailId(contactEmailCursor.getString
                                (contactEmailCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email
                                                .ADDRESS)));
                        emailIdOperation.setEmType(phoneBookContacts.getEmailType
                                (contactEmailCursor,
                                        contactEmailCursor.getInt
                                                (contactEmailCursor.getColumnIndex(ContactsContract
                                                        .CommonDataKinds.Email.TYPE))));

                        if (!arrayListCloudEmail.contains(emailId.getEmEmailId())) {
                            arrayListPhoneBookEmail.add(emailId);
                        }
                        arrayListPhoneBookEmailOperation.add(emailIdOperation);
                    }
                    contactEmailCursor.close();
                    profileDataOperationVcard.setPbEmailId(arrayListPhoneBookEmailOperation);
                }
            }

            tempEmail = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(arrayListEmail) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookEmail)) {
                tempEmail.addAll(arrayListEmail);
                tempEmail.addAll(arrayListPhoneBookEmail);
                linearEmail.setVisibility(View.VISIBLE);
                ProfileDetailAdapter emailDetailAdapter = new ProfileDetailAdapter(this, tempEmail,
                        AppConstants.EMAIL, displayOwnProfile, pmId, buttonRequestAll);
                recyclerViewEmail.setAdapter(emailDetailAdapter);
            } else {
                linearEmail.setVisibility(View.GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Education">

            // From Cloud
            ArrayList<ProfileDataOperationEducation> arrayListEducation = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                    .getPbEducation())) {
                arrayListEducation.addAll(profileDetail.getPbEducation());
            }

            tempEducation = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(arrayListEducation)) {
                tempEducation.addAll(arrayListEducation);
                linearEducation.setVisibility(View.VISIBLE);
                ProfileDetailAdapter educationDetailAdapter = new ProfileDetailAdapter(this,
                        tempEducation, AppConstants.EDUCATION, displayOwnProfile, pmId, buttonRequestAll);
                recyclerViewEducation.setAdapter(educationDetailAdapter);
            } else {
                linearEducation.setVisibility(View.GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Website">

            // From Cloud
            ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
            ArrayList<String> arrayListCloudWebsite = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                    ())) {
                arrayListWebsite.addAll(profileDetail.getPbWebAddress());
                for (int i = 0; i < arrayListWebsite.size(); i++) {
                    String website = arrayListWebsite.get(i).getWebAddress();
                    arrayListCloudWebsite.add(website);
                }
            }
       /* ArrayList<String> arrayListWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
        }*/

            // From PhoneBook
            ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsite = new ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(phoneBookId);
                ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsiteOperation = new
                        ArrayList<>();

                if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
                    while (contactWebsiteCursor.moveToNext()) {

                        ProfileDataOperationWebAddress webAddress = new
                                ProfileDataOperationWebAddress();
                        ProfileDataOperationWebAddress webAddressOperation = new
                                ProfileDataOperationWebAddress();

                        webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                        webAddress.setWebRcpType(String.valueOf(IntegerConstants
                                .RCP_TYPE_LOCAL_PHONE_BOOK));

                        webAddressOperation.setWebAddress(contactWebsiteCursor.getString
                                (contactWebsiteCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Website
                                                .URL)));

                        if (!arrayListCloudWebsite.contains(webAddress.getWebAddress())) {
                            arrayListPhoneBookWebsite.add(webAddress);
                        }
                        arrayListPhoneBookWebsiteOperation.add(webAddressOperation);
                    }
                    contactWebsiteCursor.close();
//            profileDataOperationVcard.setPbWebAddress(arrayListPhoneBookWebsiteOperation);
                }
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookWebsite)) {
                ArrayList<Object> tempWebsite = new ArrayList<>();
                tempWebsite.addAll(arrayListWebsite);
                tempWebsite.addAll(arrayListPhoneBookWebsite);

                linearWebsite.setVisibility(View.VISIBLE);
                ProfileDetailAdapter websiteDetailAdapter = new ProfileDetailAdapter(this,
                        tempWebsite, AppConstants.WEBSITE, displayOwnProfile, pmId, buttonRequestAll);
                recyclerViewWebsite.setAdapter(websiteDetailAdapter);
            } else {
                linearWebsite.setVisibility(View.GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Address">

            // From Cloud
            ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();
            ArrayList<String> arrayListCloudAddress = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress
                    ())) {
                arrayListAddress.addAll(profileDetail.getPbAddress());
                for (int i = 0; i < arrayListAddress.size(); i++) {
                    String address = arrayListAddress.get(i).getFormattedAddress();
                    arrayListCloudAddress.add(address);
                }
            }

            // From PhoneBook
            ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddress = new ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactAddressCursor = phoneBookContacts.getContactAddress(phoneBookId);
                ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddressOperation = new
                        ArrayList<>();

                if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
                    while (contactAddressCursor.moveToNext()) {

                        ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                        ProfileDataOperationAddress addressOperation = new
                                ProfileDataOperationAddress();

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
                        address.setAddressType(phoneBookContacts.getAddressType
                                (contactAddressCursor,
                                        contactAddressCursor.getInt(contactAddressCursor
                                                .getColumnIndex

                                                        (ContactsContract.CommonDataKinds
                                                                .StructuredPostal.TYPE))));
                        address.setRcpType(String.valueOf(IntegerConstants
                                .RCP_TYPE_LOCAL_PHONE_BOOK));

                        addressOperation.setFormattedAddress(contactAddressCursor.getString
                                (contactAddressCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                        addressOperation.setCity(contactAddressCursor.getString(contactAddressCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                        .CITY)));
                        addressOperation.setCountry(contactAddressCursor.getString
                                (contactAddressCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .StructuredPostal
                                                .COUNTRY)));
                        addressOperation.setNeighborhood(contactAddressCursor.getString
                                (contactAddressCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .StructuredPostal
                                                .NEIGHBORHOOD)));
                        addressOperation.setPostCode(contactAddressCursor.getString
                                (contactAddressCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .StructuredPostal
                                                .POSTCODE)));
                        addressOperation.setPoBox(contactAddressCursor.getString
                                (contactAddressCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .StructuredPostal
                                                .POBOX)));
                        addressOperation.setStreet(contactAddressCursor.getString
                                (contactAddressCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .StructuredPostal
                                                .STREET)));
                        addressOperation.setAddressType(phoneBookContacts.getAddressType
                                (contactAddressCursor,
                                        contactAddressCursor.getInt(contactAddressCursor
                                                .getColumnIndex
                                                        (ContactsContract.CommonDataKinds
                                                                .StructuredPostal
                                                                .TYPE))));

                        if (!arrayListCloudAddress.contains(address.getFormattedAddress())) {
                            arrayListPhoneBookAddress.add(address);
                        }
                        arrayListPhoneBookAddressOperation.add(addressOperation);
                    }
                    contactAddressCursor.close();
                    profileDataOperationVcard.setPbAddress(arrayListPhoneBookAddressOperation);
                }
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookAddress)) {
                ArrayList<Object> tempAddress = new ArrayList<>();
                tempAddress.addAll(arrayListAddress);
                tempAddress.addAll(arrayListPhoneBookAddress);
                linearAddress.setVisibility(View.VISIBLE);
                ProfileDetailAdapter addressDetailAdapter = new ProfileDetailAdapter(this,
                        tempAddress, AppConstants.ADDRESS, displayOwnProfile, pmId, buttonRequestAll);
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
            ArrayList<ProfileDataOperationImAccount> arrayListPhoneBookImAccount = new
                    ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactImAccountCursor = phoneBookContacts.getContactIm(phoneBookId);

                if (contactImAccountCursor != null && contactImAccountCursor.getCount() > 0) {
                    while (contactImAccountCursor.moveToNext()) {

                        ProfileDataOperationImAccount imAccount = new
                                ProfileDataOperationImAccount();

                        imAccount.setIMAccountDetails(contactImAccountCursor.getString
                                (contactImAccountCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Im
                                                .DATA1)));

                        imAccount.setIMAccountType(phoneBookContacts.getImAccountType
                                (contactImAccountCursor,
                                        contactImAccountCursor.getInt(contactImAccountCursor
                                                .getColumnIndex
                                                        (ContactsContract.CommonDataKinds.Im
                                                                .TYPE))));

                        imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                                (contactImAccountCursor, contactImAccountCursor.getInt(
                                        (contactImAccountCursor.getColumnIndex(ContactsContract
                                                .CommonDataKinds.Im.PROTOCOL)))));

                        imAccount.setIMRcpType(String.valueOf(IntegerConstants
                                .RCP_TYPE_LOCAL_PHONE_BOOK));

                        if (!arrayListCloudImAccount.contains(imAccount.getIMAccountProtocol())) {
                            arrayListPhoneBookImAccount.add(imAccount);
                        }

                    }
                    contactImAccountCursor.close();
                }
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookImAccount)) {
                ArrayList<Object> tempImAccount = new ArrayList<>();
                tempImAccount.addAll(arrayListImAccount);
                tempImAccount.addAll(arrayListPhoneBookImAccount);
                linearSocialContact.setVisibility(View.VISIBLE);
                ProfileDetailAdapter imAccountDetailAdapter = new ProfileDetailAdapter(this,
                        tempImAccount, AppConstants.IM_ACCOUNT, displayOwnProfile, pmId, buttonRequestAll);
                recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
            } else {
                linearSocialContact.setVisibility(View.GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Aadhar card details">
            if (profileDetail != null) {
                final ProfileDataOperationAadharNumber aadharDetails;
                if (profileDetail.getPbAadhar() != null) {
                    linearAadharCard.setVisibility(View.VISIBLE);
                    aadharDetails = profileDetail.getPbAadhar();

                    if (displayOwnProfile) {
                        buttonRequest.setVisibility(View.GONE);
                        if (aadharDetails.getAadharPublic() != 3) {
                            if (aadharDetails.getAadharPublic() == 1) {
                                buttonPrivacy.setImageResource(R.drawable.ico_privacy_public);
                            } else if (aadharDetails.getAadharPublic() == 2) {
                                buttonPrivacy.setImageResource(R.drawable.ico_privacy_my_contact);
                            }
                        } else {
                            buttonPrivacy.setImageResource(R.drawable.ico_privacy_onlyme);
                        }
                    } else {
                        if ((MoreObjects.firstNonNull(aadharDetails.getAadharPublic(), 3)) == IntegerConstants.PRIVACY_PRIVATE
                                && aadharDetails.getAadharNumber().startsWith("XXXX")) {
                            buttonRequest.setVisibility(View.VISIBLE);
                            buttonPrivacy.setVisibility(View.GONE);
                        }
//                        else {
//                            if ((MoreObjects.firstNonNull(aadharDetails.getAadharPublic(), 3)) !=
//                                    IntegerConstants
//                                            .PRIVACY_PRIVATE && aadharDetails.getAadharNumber()
//                                    .equalsIgnoreCase("0")) {
//                                buttonRequest.setVisibility(View.VISIBLE);
//                                buttonPrivacy.setVisibility(View.GONE);
//                            }
//                        }
                    }

                    buttonRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pmTo = Integer.parseInt(pmId);
                            // sendAccessRequest(int toPMId, String carFiledType, String
                            // recordIndexId)
                            sendAccessRequest(pmTo, "pb_aadhaar", String.valueOf(aadharDetails
                                    .getAadharId()));
                        }
                    });

                    textAadharNumber.setTypeface(Utils.typefaceRegular(this));
                    if (aadharDetails.getAadharNumber().equalsIgnoreCase("0")) {
                        textAadharNumber.setText("XXXX-XXXX-XXXX");
                    } else
                        textAadharNumber.setText(aadharDetails.getAadharNumber() + "");

                    buttonPrivacy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PrivacySettingPopupDialog privacySettingPopupDialog = new
                                    PrivacySettingPopupDialog(null, ProfileDetailActivity.this,
                                    new PrivacySettingPopupDialog.DialogCallback() {
                                        @Override
                                        public void onSettingSaved(ProfileDetailAdapter
                                                                           .ProfileDetailViewHolder view, int whichItem, int newPrivacy, int
                                                                           itemPosition, int
                                                                           oldPrivacy, String
                                                                           cloudId) {

                                            if (oldPrivacy == newPrivacy + 1) {
                                                return;
                                            }

                                            WsRequestObject wsRequestObject = new WsRequestObject();

                                            PrivacyEntityItem privacyEntityItem = new
                                                    PrivacyEntityItem();
                                            privacyEntityItem.setId(cloudId);
                                            privacyEntityItem.setValue(newPrivacy + 1);
                                            ArrayList<PrivacyEntityItem> privacyEntityItems = new
                                                    ArrayList<>();
                                            privacyEntityItems.add(privacyEntityItem);
                                            ArrayList<PrivacyDataItem> privacyItems = new
                                                    ArrayList<>();
                                            PrivacyDataItem privacyDataItem = new PrivacyDataItem();
                                            switch (whichItem) {
                                                case AppConstants.AADHAR_NUMBER:
                                                    privacyDataItem.setPbAadhaar
                                                            (privacyEntityItems);
                                                    break;
                                            }
                                            privacyItems.add(privacyDataItem);
                                            wsRequestObject.setPrivacyData(privacyItems);
//        wsRequestObject.setPmId(pmId);
                                            if (Utils.isNetworkAvailable(ProfileDetailActivity
                                                    .this)) {
                                                new AsyncWebServiceCall(ProfileDetailActivity.this,
                                                        WSRequestType.REQUEST_TYPE_JSON.getValue(),
                                                        wsRequestObject, null, WsResponseObject
                                                        .class,
                                                        WsConstants
                                                                .REQ_SET_PRIVACY_SETTING,
                                                        ProfileDetailActivity
                                                                .this.getResources().getString(R
                                                                .string
                                                                .msg_please_wait), true)
                                                        .executeOnExecutor
                                                                (AsyncTask.THREAD_POOL_EXECUTOR,
                                                                        BuildConfig.WS_ROOT +
                                                                                WsConstants
                                                                                        .REQ_SET_PRIVACY_SETTING);
                                            } else {
                                                //show no toast
                                                Toast.makeText(ProfileDetailActivity.this,
                                                        ProfileDetailActivity.this.getResources()
                                                                .getString(R.string.msg_no_network),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, AppConstants
                                    .AADHAR_NUMBER,
                                    0, aadharDetails.getAadharPublic(), aadharDetails.getAadharId
                                    () + "");
                            privacySettingPopupDialog.setDialogTitle(ProfileDetailActivity.this
                                    .getResources().getString(R
                                            .string.privacy_dialog_title));
                            privacySettingPopupDialog.showDialog();
                        }
                    });

                } else {
                    linearAadharCard.setVisibility(View.GONE);
                }
            } else {
                linearAadharCard.setVisibility(View.GONE);
            }

            textAadharNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!textAadharNumber.getText().toString().contains("X")) {
                        Utils.copyToClipboard(ProfileDetailActivity.this,
                                getResources().getString(R.string.str_copy_aadhar_number),
                                textAadharNumber.getText().toString());
                        Toast.makeText(rContactApplication, "Aadhar number copied.", Toast
                                .LENGTH_SHORT).show();
                        String url = "https://resident.uidai.gov.in/aadhaarverification";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
            });
            //</editor-fold>

            // <editor-fold desc="Last seen details">

            if (!displayOwnProfile) {

                buttonRequestAll.setVisibility(View.VISIBLE);

                if (pmId.equalsIgnoreCase("-1")) {
                    cardLastSeenDetails.setVisibility(View.GONE);
                } else {

                    if (profileDetail != null) {

                        if (!StringUtils.isBlank(profileDetail.getPmLastSeen())) {

                            linearLastSeen.setVisibility(View.VISIBLE);

                            long elapsedDays = 0;

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                                    "hh:mm:ss", Locale.getDefault());

//                        String startDate = simpleDateFormat.format(profileDetail.getPmLastSeen());
                            String endDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));

                            try {

                                long difference = simpleDateFormat.parse(endDate).getTime() -
                                        simpleDateFormat.parse(profileDetail.getPmLastSeen()).getTime();

                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;
                                long hoursInMilli = minutesInMilli * 60;
                                long daysInMilli = hoursInMilli * 24;

                                elapsedDays = difference / daysInMilli;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (elapsedDays > 1) {
                                textLabelLastSeen.setText(String.format("Last seen on %s days ago ",
                                        String.valueOf(elapsedDays)));
                            } else if (elapsedDays > 0) {
                                textLabelLastSeen.setText(String.format("Last seen on %s day ago ",
                                        String.valueOf(elapsedDays)));
                            } else {

                                String date = Utils.convertDateFormat(profileDetail.getPmLastSeen(),
                                        "yyyy-MM-dd hh:mm:ss", "HH:mm a");

                                textLabelLastSeen.setText(String.format("Last seen today at %s", date));
                            }

                        } else {
                            textLabelLastSeen.setText("Last seen N/A");
                            textLabelLastSeen.setVisibility(View.VISIBLE);
                        }

                    } else {
                        linearLastSeen.setVisibility(View.GONE);
                    }
                }
            } else {
                cardLastSeenDetails.setVisibility(View.GONE);
            }

            // </editor-fold>

            if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookWebsite))
                    ||
                    (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils
                            .isArraylistNullOrEmpty(arrayListPhoneBookAddress))
                    ||
                    (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils
                            .isArraylistNullOrEmpty(arrayListPhoneBookImAccount))
                    || (profileDetail != null && profileDetail.getPbAadhar() != null)) {
                rippleViewMore.setVisibility(View.VISIBLE);
            } else {
                rippleViewMore.setVisibility(View.GONE);
            }

            // <editor-fold desc="Event">

            // From Cloud
            ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent()
            )) {
                arrayListEvent.addAll(profileDetail.getPbEvent());
            }

            // From PhoneBook Event
            ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEvent = new ArrayList<>();
            if (!isHideFavourite) {
                Cursor contactEventCursor = phoneBookContacts.getContactEvent(phoneBookId);
                ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEventOperation = new
                        ArrayList<>();

                if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
                    while (contactEventCursor.moveToNext()) {

                        ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                        ProfileDataOperationEvent eventOperation = new ProfileDataOperationEvent();

                        event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                                contactEventCursor
                                        .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                                .CommonDataKinds.Event.TYPE))));

                        event.setEventDateTime(contactEventCursor.getString(contactEventCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                        .START_DATE)));

                        event.setEventRcType(String.valueOf(IntegerConstants
                                .RCP_TYPE_LOCAL_PHONE_BOOK));

                        eventOperation.setEventType(phoneBookContacts.getEventType
                                (contactEventCursor,
                                        contactEventCursor
                                                .getInt(contactEventCursor.getColumnIndex
                                                        (ContactsContract
                                                                .CommonDataKinds.Event.TYPE))));

                        eventOperation.setEventDateTime(contactEventCursor.getString
                                (contactEventCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                                .START_DATE)));

                        if (!arrayListEvent.contains(event)) {
                            arrayListPhoneBookEvent.add(event);
                        }
                        arrayListPhoneBookEventOperation.add(eventOperation);
                    }
                    contactEventCursor.close();
                    profileDataOperationVcard.setPbEvent(arrayListPhoneBookEventOperation);
                }
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListEvent) || !Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookEvent)) {
                ArrayList<Object> tempEvent = new ArrayList<>();
                tempEvent.addAll(arrayListEvent);
                tempEvent.addAll(arrayListPhoneBookEvent);
                linearEvent.setVisibility(View.VISIBLE);
                ProfileDetailAdapter eventDetailAdapter = new ProfileDetailAdapter(this, tempEvent,
                        AppConstants.EVENT, displayOwnProfile, pmId, buttonRequestAll);
                recyclerViewEvent.setAdapter(eventDetailAdapter);
            } else {
                linearEvent.setVisibility(View.GONE);
                cardOtherDetails.setVisibility(View.GONE);
            }
            //</editor-fold>

            if (displayOwnProfile && StringUtils.length(StringUtils.defaultString(profileDetail
                    != null ? profileDetail.getPbGender() : "")) > 0) {
                textGender.setText(profileDetail.getPbGender());

                if (textGender.getText().toString().trim().equals("Male"))
                    imageIconGender.setImageResource(R.drawable.ico_male_svg);
                else
                    imageIconGender.setImageResource(R.drawable.ico_female_svg);

            } else {
                linearGender.setVisibility(View.GONE);
            }

            if (Utils.isArraylistNullOrEmpty(arrayListEvent) && Utils.isArraylistNullOrEmpty
                    (arrayListPhoneBookEvent)
                    && StringUtils.length(StringUtils.defaultString(profileDetail != null ?
                    profileDetail.getPbGender() : null)) <= 0
                    ) {
                cardOtherDetails.setVisibility(View.GONE);
            } else {
                cardOtherDetails.setVisibility(View.VISIBLE);
            }

            if (displayOwnProfile && profileDetail != null) {
                showProfilePercentage(profileDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAccessRequest(int toPMId, String carFiledType, String recordIndexId) {

        WsRequestObject requestObj = new WsRequestObject();
        requestObj.setCarPmIdTo(toPMId);
        requestObj.setCarFiledType(carFiledType);
        requestObj.setCarStatus(0);
        requestObj.setCarMongoDbRecordIndex(recordIndexId);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObj, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_PRIVACY_REQUEST, this.getResources().getString(R.string
                    .msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    BuildConfig.WS_ROOT + WsConstants.REQ_PROFILE_PRIVACY_REQUEST);
        } else {
            //show no net
            Toast.makeText(this, this.getResources().getString(R.string.msg_no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showProfilePercentage(ProfileDataOperation profileDetail) {
        if (progressPercentage != null) {
            progressPercentage.setBarColor(Color.parseColor("#CCE4E1"), Color.parseColor
                    ("#00796B"));

            int percentage = 5;
            ArrayList<String> arrayListRemainingFields = new ArrayList<>();
            if (Utils.hasSharedPreference(ProfileDetailActivity.this, AppConstants
                    .PREF_PROFILE_REMAINING_FIELDS)) {
                arrayListRemainingFields.addAll(Utils.getArrayListPreference(ProfileDetailActivity
                        .this, AppConstants.PREF_PROFILE_REMAINING_FIELDS));
            }

            //<editor-fold desc="Gender">
            if (!StringUtils.isBlank(profileDetail.getPbGender())) {
                percentage += 5;
                if (arrayListRemainingFields.contains(getString(R.string.str_gender))) {
                    arrayListRemainingFields.remove(getString(R.string.str_gender));
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_gender));
            }
            //</editor-fold>

            //<editor-fold desc="Profile Photo">
            if (!StringUtils.isBlank(profileDetail.getPbProfilePhoto())) {
                percentage += 5;
                if (arrayListRemainingFields.contains(getString(R.string.str_profile_photo))) {
                    arrayListRemainingFields.remove(getString(R.string.str_profile_photo));
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_profile_photo));
            }
            //</editor-fold>

            //<editor-fold desc="Organization">
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbOrganization())) {
               /* percentage += 15;
                if (arrayListRemainingFields.contains(getString(R.string.str_organization))) {
                    arrayListRemainingFields.remove(getString(R.string.str_organization));
                }*/
                percentage += 5;
                if (arrayListRemainingFields.contains(getString(R.string.str_organization))) {
                    arrayListRemainingFields.remove(getString(R.string.str_organization));
                }
                boolean hasVerifiedOrganization = false;
                for (int i = 0; i < profileDetail.getPbOrganization().size(); i++) {
                    if (profileDetail.getPbOrganization().get(i).getIsVerify() == 1) {
                        percentage += 10;
                        hasVerifiedOrganization = true;
                        break;
                    }
                }
                if (hasVerifiedOrganization) {
                    if (arrayListRemainingFields.contains("Verified Organization")) {
                        arrayListRemainingFields.remove("Verified Organization");
                    }
                } else {
                    arrayListRemainingFields.add("Verified Organization");
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_organization));
            }
            //</editor-fold>

            //<editor-fold desc="Web Address">
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress())) {
                percentage += 5;
                if (arrayListRemainingFields.contains(getString(R.string.str_website))) {
                    arrayListRemainingFields.remove(getString(R.string.str_website));
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_website));
            }
            //</editor-fold>

            //<editor-fold desc="Address">
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
                percentage += 10;
                if (arrayListRemainingFields.contains(getString(R.string.str_address))) {
                    arrayListRemainingFields.remove(getString(R.string.str_address));
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_address));
            }
            //</editor-fold>

            //<editor-fold desc="Event">
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
                percentage += 5;
                if (arrayListRemainingFields.contains(getString(R.string.str_event))) {
                    arrayListRemainingFields.remove(getString(R.string.str_event));
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_event));
            }
            //</editor-fold>

            //<editor-fold desc="Email Id">
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
                percentage += 5;
                if (arrayListRemainingFields.contains(getString(R.string.str_email))) {
                    arrayListRemainingFields.remove(getString(R.string.str_email));
                }
                boolean hasVerifiedEmail = false;
                for (int i = 0; i < profileDetail.getPbEmailId().size(); i++) {
                    if (profileDetail.getPbEmailId().get(i).getEmRcpType() == IntegerConstants
                            .RCP_TYPE_PRIMARY) {
                        percentage += 15;
                        hasVerifiedEmail = true;
                        break;
                    } else if (profileDetail.getPbEmailId().get(i).getEmRcpType() ==
                            IntegerConstants
                                    .RCP_TYPE_SECONDARY) {
                        if (!profileDetail.getPbEmailId().get(i).getEmSocialType()
                                .equalsIgnoreCase("")) {
                            percentage += 15;
                            hasVerifiedEmail = true;
                            break;
                        }
                    }
                }
                if (hasVerifiedEmail) {
                    if (arrayListRemainingFields.contains("Verified Email")) {
                        arrayListRemainingFields.remove("Verified Email");
                    }
                } else {
                    arrayListRemainingFields.add("Verified Email");
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_email));
            }
            //</editor-fold>

            //<editor-fold desc="Im Account">
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts())) {
                if (arrayListRemainingFields.contains(getString(R.string.str_social_contact))) {
                    arrayListRemainingFields.remove(getString(R.string.str_social_contact));
                }
                ArrayList<String> savedImAccount = new ArrayList<>();
                for (int i = 0; i < profileDetail.getPbIMAccounts().size(); i++) {
//                    savedImAccount.add(profileDetail.getPbIMAccounts().get(i)
//                            .getIMAccountProtocol());
                    String protocol = profileDetail.getPbIMAccounts().get(i)
                            .getIMAccountProtocol();
                    if (protocol.contains(getString(R.string.facebook)) || protocol.contains
                            (getString(R.string.google_plus)) || protocol.contains(getString(R
                            .string.linked_in))) {
                        savedImAccount.add(protocol);
                    } else {
                        savedImAccount.add("Other");
                    }

                }
                if (savedImAccount.contains(getString(R.string.facebook))) {
                    percentage += 5;
                    if (arrayListRemainingFields.contains("Facebook Account")) {
                        arrayListRemainingFields.remove("Facebook Account");
                    }
                } else {
                    arrayListRemainingFields.add("Facebook Account");
                }
                if (savedImAccount.contains(getString(R.string.google_plus))) {
                    percentage += 5;
                    if (arrayListRemainingFields.contains("Google Plus Account")) {
                        arrayListRemainingFields.remove("Google Plus Account");
                    }
                } else {
                    arrayListRemainingFields.add("Google Plus Account");
                }
                if (savedImAccount.contains(getString(R.string.linked_in))) {
                    percentage += 5;
                    if (arrayListRemainingFields.contains("Linked In Account")) {
                        arrayListRemainingFields.remove("Linked In Account");
                    }
                } else {
                    arrayListRemainingFields.add("Linked In Account");
                }
                if (savedImAccount.contains("Other")) {
                    percentage += 5;
                    if (arrayListRemainingFields.contains(getString(R.string.str_social_contact))) {
                        arrayListRemainingFields.remove(getString(R.string.str_social_contact));
                    }
                } else {
                    arrayListRemainingFields.add(getString(R.string.str_social_contact));
                }

            } else {
                arrayListRemainingFields.add(getString(R.string.str_social_contact));
            }
            //</editor-fold>

            //<editor-fold desc="Adhaar Card">
            if (profileDetail.getPbAadhar() != null) {
                percentage += 10;
                if (arrayListRemainingFields.contains(getString(R.string.str_aadhar_card))) {
                    arrayListRemainingFields.remove(getString(R.string.str_aadhar_card));
                }
            } else {
                arrayListRemainingFields.add(getString(R.string.str_aadhar_card));
            }
            //</editor-fold>

            Utils.setArrayListPreference(ProfileDetailActivity.this, AppConstants
                    .PREF_PROFILE_REMAINING_FIELDS, arrayListRemainingFields);

            if (percentage < 100) {
                relativeProfilePercentage.setVisibility(View.VISIBLE);

                textCompleteProfile.setTypeface(Utils.typefaceSemiBold(ProfileDetailActivity.this));
                textCompleteProfileDescription.setTypeface(Utils.typefaceRegular
                        (ProfileDetailActivity.this));

                includeElevation.setRotation(180);
                includeElevation.setAlpha(0.6f);
                includeElevationTop.setAlpha(0.6f);

                progressPercentage.setValueAnimated(percentage);

                if (arrayListRemainingFields.size() > 0) {
                    Random random = new Random();

                    textCompleteProfileDescription.setText(String.format(getString(R.string
                            .str_complete_profile_description), arrayListRemainingFields.get
                            (random.nextInt(arrayListRemainingFields.size()))));
                }

            } else {
                relativeProfilePercentage.setVisibility(View.GONE);
            }
        }
    }

    private void showAllOrganizations(ArrayList<ProfileDataOperationOrganization>
                                              arrayListOrganization) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = dialog.findViewById(R.id.button_right);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);

        rippleLeft.setVisibility(View.GONE);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_close);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationListAdapter adapter = new OrganizationListAdapter(this, arrayListOrganization);
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                @SuppressWarnings("unused")
                int position = viewHolder.getAdapterPosition();
                String actionNumber;
                if (displayOwnProfile) {
                    actionNumber = StringUtils.defaultString(((ProfileDetailAdapter
                            .ProfileDetailViewHolder) viewHolder).textMain.getText()
                            .toString());
                } else {
                    actionNumber = StringUtils.defaultString(((ProfileDetailAdapter
                            .ProfileDetailViewHolder) viewHolder).textMain.getText()
                            .toString());
                }
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    dialCall(actionNumber);
//                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (phoneDetailAdapter != null) {
                            phoneDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ProfileDetailAdapter.ProfileDetailViewHolder &&
                        StringUtils.startsWithIgnoreCase(((ProfileDetailAdapter
                                .ProfileDetailViewHolder) viewHolder).textMain.getText()
                                .toString(), "+XX")) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(ProfileDetailActivity.this, R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(ProfileDetailActivity.this, R.color
                                .brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_sms);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width, (float) itemView.getRight() -
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewContactNumber);
    }

    private void dialCall(String number) {

        String finalNumber = Utils.getFormattedNumber(ProfileDetailActivity.this, number);

        if (ContextCompat.checkSelfPermission(ProfileDetailActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager
                .PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission
                    .CALL_PHONE}, AppConstants
                    .MY_PERMISSIONS_REQUEST_PHONE_CALL);
        else {
            Utils.callIntent(ProfileDetailActivity.this, finalNumber);
        }
    }

    public RelativeLayout getRelativeRootProfileDetail() {
        return relativeRootProfileDetail;
    }

    @SuppressWarnings("unused")
    public int getListClickedPosition() {
        return listClickedPosition;
    }

    @SuppressWarnings("unused")
    private void showChooseShareOption(final String firstName, final String lastName) {
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

        textDialogTitle.setText(getString(R.string.str_share) + " " + contactName + getString(R
                .string.str_s_profile));

        buttonLeft.setText(R.string.action_cancel);
        textRemovePhoto.setVisibility(View.GONE);

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
                if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                    // RCP profile or Own Profile
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = BuildConfig.WS_PROFILE_VIEW_ROOT + firstName
                            + "." + lastName + "." + pmId;
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string
                            .str_share_contact_via)));
                } else {
                    // Non-Rcp profile
                    shareContact();

                }
            }
        });

        textFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                    bundle.putString(AppConstants.EXTRA_PM_ID, pmId);
                } else {
                    bundle.putSerializable(AppConstants.EXTRA_OBJECT_CONTACT,
                            profileDataOperationVcard);
                }
                startActivityIntent(ProfileDetailActivity.this, ContactListingActivity.class,
                        bundle);
            }
        });

        dialog.show();
    }

    @SuppressWarnings("unused")
    private void fetchCallLogHistoryDateWise(String value) {
        ArrayList<CallLogType> tempList = new ArrayList<>();
        arrayListHistory = new ArrayList<>();
        if (!TextUtils.isEmpty(value)) {
            tempList = callLogHistory(value);
            // Log.i("History size  ", tempList.size() + "" + " of  " + value);
        }
        for (int i = 0; i < tempList.size(); i++) {
            CallLogType callLogTypeHistory = tempList.get(i);
            long date = callLogTypeHistory.getHistoryDate();
            Date objDate1 = new Date(date);
            String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format(objDate1);
            String intentDate = "";
            if (historyDate > 0) {
                Date intentDate1 = new Date(historyDate);
                intentDate = new SimpleDateFormat("yyyy-MM-dd").format(intentDate1);
            }
        /*    else {
                intentDate = new SimpleDateFormat("yyyy-MM-dd").format(CallLogFragment
                        .callLogTypeReceiver.getCallReceiverDate());
            }*/
            if (intentDate.equalsIgnoreCase(arrayDate)) {
                arrayListHistory.add(callLogTypeHistory);
            }

        }
        setHistoryAdapter();
    }

    private void fetchAllCallLogHistory(String value) {
        if (!TextUtils.isEmpty(value)) {
            arrayListHistory = callLogHistory(value);
            // Log.i("History size  ", arrayListHistory.size() + "" + " of  " + value);
        }
    }

    private void setHistoryAdapter() {
        if (arrayListHistory != null && arrayListHistory.size() > 0) {
            textNoHistoryToShow.setVisibility(View.GONE);
            rippleViewOldRecords.setVisibility(View.VISIBLE);
            recyclerCallHistory.setVisibility(View.VISIBLE);
            callHistoryListAdapter = new CallHistoryListAdapter(getApplicationContext(),
                    arrayListHistory);
            recyclerCallHistory.setAdapter(callHistoryListAdapter);
            recyclerCallHistory.setFocusable(false);
        } else {
            recyclerCallHistory.setVisibility(View.GONE);
            rippleViewOldRecords.setVisibility(View.GONE);
            textNoHistoryToShow.setVisibility(View.VISIBLE);
            textNoHistoryToShow.setText(getResources().getString(R.string.text_no_history));
        }


    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog
                    .Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByName(String name) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog
                    .Calls.CACHED_NAME + " =?", new String[]{name}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    private ArrayList callLogHistory(String number) {

        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor;
//        Pattern numberPat = Pattern.compile("\\d+");
        Pattern numberPat = Pattern.compile("[+][0-9]+");
        Matcher matcher1 = numberPat.matcher(number);
        if (matcher1.find() || number.matches("[0-9]+")) {
            cursor = getCallHistoryDataByNumber(number);
        } else {
            cursor = getCallHistoryDataByName(number);
            if (cursor != null && cursor.getCount() == 0) {
                if (!StringUtils.isEmpty(historyNumber))
                    cursor = getCallHistoryDataByNumber(historyNumber);
            }
        }
        try {
            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int callLogId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
                int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int account = -1;
                int account_id = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    account = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
                    //for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }
              /*  else {
                        account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }*/

                while (cursor.moveToNext()) {
                    String phNum = cursor.getString(number1);
                    int callType = Integer.parseInt(cursor.getString(type));
                    String callDate = cursor.getString(date);
                    long dateOfCall = Long.parseLong(callDate);
                    String callDuration = cursor.getString(duration);
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    String accountId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);
                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);
//                        String userImage = cursor.getString(profileImage);
//                        if (userImage != null)
//                            Log.e("User Image", userImage);
                    }
                    String userName = getNameFromNumber(Utils.getFormattedNumber(this, phNum));
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    logObject.setHistoryCallSimNumber(accountId);
                    logObject.setHistoryId(histroyId);
                    logObject.setHistoryNumberType(numberTypeLog);
                    if (isCallLogRcpUser) {
                        logObject.setIsHistoryRcpVerifiedId("1");
                    } else if (!StringUtils.isEmpty(callLogRcpVerfiedId)) {
                        if ((StringUtils.isEmpty(userName)))
                            logObject.setIsHistoryRcpVerifiedId(callLogRcpVerfiedId);
                    } else {
                        logObject.setIsHistoryRcpVerifiedId("");
                    }
                    callDetails.add(logObject);
                }
                cursor.close();
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null)
                cursor.close();
        }

        return callDetails;
    }


    private String getNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }

    private String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return getString(R.string.type_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return getString(R.string.type_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return getString(R.string.type_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return getString(R.string.type_fax_work);

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return getString(R.string.type_fax_home);

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return getString(R.string.type_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return getString(R.string.type_other);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return getString(R.string.type_callback);

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return getString(R.string.type_car);

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return getString(R.string.type_company_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return getString(R.string.type_isdn);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return getString(R.string.type_main);

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return getString(R.string.type_other_fax);

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return getString(R.string.type_radio);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return getString(R.string.type_telex);

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return getString(R.string.type_tty_tdd);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return getString(R.string.type_work_mobile);

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return getString(R.string.type_work_pager);

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return getString(R.string.type_assistant);

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return getString(R.string.type_mms);

        }
        return getString(R.string.type_other);
    }

    private class GetRCPNameAndProfileImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(ProfileDetailActivity.this, getString(R.string
                    .msg_please_wait), false);
            rippleViewOldRecords.setVisibility(View.GONE);
        }

        protected Void doInBackground(Void... urls) {

            if (profileActivityCallInstance) {
                if (!TextUtils.isEmpty(historyNumber)) {
                    fetchAllCallLogHistory(historyNumber);
                } else {
                    if (!TextUtils.isEmpty(historyName)) {
                        fetchAllCallLogHistory(historyName);
                    }
                }
            } else {
                if (!TextUtils.isEmpty(historyName)) {
                    fetchAllCallLogHistory(historyName);
                } else {
                    fetchAllCallLogHistory(historyNumber);
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.hideProgressDialog();
                }
            }, 1500);

            setHistoryAdapter();
        }
    }

    private void showPermissionConfirmationDialog() {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        finish();
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
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
        permissionConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        permissionConfirmationDialog.setRightButtonText(getString(R.string.action_ok));
        permissionConfirmationDialog.setDialogBody(getString(R.string.call_log_permission));

        permissionConfirmationDialog.showDialog();

    }

    private void selectContactDialog(final ArrayList<ProfileDataOperationPhoneNumber>
                                             phoneNumbers, ArrayList<ProfileDataOperationEmail>
                                             emailIds) {

        final ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.addAll(phoneNumbers);
        arrayList.addAll(emailIds);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        final LinearLayout relativeRootDialogList = dialog.findViewById(R.id
                .relative_root_dialog_list);
        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(String.format("%s %s", getString(R.string.str_invite),
                contactName));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = dialog.findViewById(R.id.button_right);
        Button buttonLeft = dialog.findViewById(R.id.button_left);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);

        rippleRight.setVisibility(View.GONE);
        rippleLeft.setVisibility(View.GONE);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_cancel);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(getString(R.string.str_invite));

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        final PhoneBookContactDetailAdapter adapter = new PhoneBookContactDetailAdapter(this,
                arrayList, new PhoneBookContactDetailAdapter.onClickListener() {
            @Override
            public void onPhoneNumberClick(String number) {

                dialog.dismiss();

                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("sms_body", AppConstants.PLAY_STORE_LINK + getPackageName());
                smsIntent.setData(Uri.parse("sms:" + number));
                startActivity(smsIntent);

            }

            @Override
            public void onEmailClick(String email) {

                dialog.dismiss();

                ArrayList<String> numbers = new ArrayList<>();
                ArrayList<String> emails = new ArrayList<>();

                emails.add(email);

                inviteContact(numbers, emails);

            }
        });
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            arrayListHistory.clear();
            recyclerCallHistory.setVisibility(View.GONE);
            setHistoryAdapter();

        }
    };

    private void openCallLogHistoryDetailsActivity() {
        Intent intent = new Intent(ProfileDetailActivity.this, CallHistoryDetailsActivity.class);
        if (!StringUtils.isEmpty(historyNumber)) {
            historyNumber = historyNumber.replace(" ", "").replace("-", "");
            intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, historyNumber);
        } else {
            if (intent.hasExtra(AppConstants.EXTRA_FROM_NOTI_PROFILE)) {
                if (intent.getBooleanExtra(AppConstants.EXTRA_FROM_NOTI_PROFILE, false)) {
                    if (tempPhoneNumber != null) {
                        ProfileDataOperationPhoneNumber phoneNumber =
                                (ProfileDataOperationPhoneNumber) tempPhoneNumber.get(0);
                        String profilePrimaryNumber = "";
                        if (phoneNumber != null) {
                            profilePrimaryNumber = phoneNumber.getPhoneNumber();
                        }
                        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER,
                                profilePrimaryNumber);
                        intent.putExtra(AppConstants.EXTRA_RCP_FROM_NOTI, true);
                    }
                }
            }
        }
        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, historyName);
        intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, historyDate);
        intent.putExtra(AppConstants.EXTRA_PM_ID, pmId);
        String rawId;
        if (checkNumberFavourite == null) {
            rawId = phoneBookId;
        } else {
            rawId = checkNumberFavourite;
        }
        intent.putExtra(AppConstants.EXTRA_PHONE_BOOK_ID, rawId);
//        intent.putExtra(AppConstants.EXTRA_PHONE_BOOK_ID, phoneBookId);
        intent.putExtra(AppConstants.EXTRA_CONTACT_NAME, contactName);
        if (!StringUtils.isEmpty(cloudContactName))
            intent.putExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME, cloudContactName);
        else {
            if (!StringUtils.isEmpty(callLogCloudName))
                intent.putExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME, callLogCloudName);
            else {
                intent.putExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME, "");
            }

        }
        intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE,
                profileActivityCallInstance);
        intent.putExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE, checkNumberFavourite);
        intent.putExtra(AppConstants.EXTRA_CONTACT_POSITION, listClickedPosition);
        intent.putExtra(AppConstants.EXTRA_CALL_UNIQUE_ID, hashMapKey);
        intent.putExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID, uniqueContactId);
        intent.putExtra(AppConstants.EXTRA_IS_RCP_USER, isCallLogRcpUser);
        if (!StringUtils.isEmpty(profileThumbnail)) {
            intent.putExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, profileThumbnail);
        } else {
            intent.putExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, thumbnailUrl);
        }
        intent.putExtra(AppConstants.EXTRA_DIALOG_CALL_LOG_INSTANCE, isDialogCallLogInstance);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private void getOldCallHistory() {
        ArrayList<CallLogHistoryType> arrayListToSend = new ArrayList<>();
        if (arrayListHistory != null && arrayListHistory.size() > 0) {
            CallLogType callLogType = arrayListHistory.get(arrayListHistory.size() - 1);
//            String number = callLogType.getHistoryNumber();
            String number = Utils.getFormattedNumber(this, callLogType.getHistoryNumber());
            if (!StringUtils.isEmpty(number)) {
                if (number.startsWith("+91"))
                    number = number.replace("+", "");
                else
                    number = "91" + number;
            }
            long date = callLogType.getHistoryDate();
                /*Date date1 = new Date(date);
                String finalDate = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(date1);*/
            CallLogHistoryType callLogHistoryType = new CallLogHistoryType();
            if (!StringUtils.isEmpty(number))
                callLogHistoryType.setHistoryNumber(number);
            callLogHistoryType.setHistoryDate(date);
            arrayListToSend.add(callLogHistoryType);

            fetchOldRecordsServiceCall(arrayListToSend);

        } else {
            rippleViewOldRecords.setVisibility(View.GONE);
        }
    }

    private ArrayList<CallLogType> getNumbersFromName(String name) {
        Cursor cursor;
        ArrayList<CallLogType> listNumber = new ArrayList<>();
        try {
            final Uri Person = Uri.withAppendedPath(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                    Uri.encode(name));

            cursor = this.getContentResolver().query(Person, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " =?", new
                            String[]{name}, null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    int number1 = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone
                            .NUMBER);

                    while (cursor.moveToNext()) {
                        CallLogType callLogType = new CallLogType();
                        String profileNumber = cursor.getString(number1);
                        String formattedNumber = Utils.getFormattedNumber(this, profileNumber);
                        callLogType.setName(name);
                        callLogType.setNumber(formattedNumber);
                        listNumber.add(callLogType);
                    }
                }
                cursor.close();
            }


        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return listNumber;
    }

    private void savePrivacySettingToDb(ProfileDataOperation profileDetail) {

        //<editor-fold desc="Basic Details">
        TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
        tableProfileMaster.updateRatingPrivacy(profileDetail.getRcpPmId(), String.valueOf(profileDetail.getProfileRatingPrivacy()));
        //</editor-fold>

        // <editor-fold desc="Aadhar card details">
        TableAadharMaster tableAadharMaster = new TableAadharMaster(databaseHandler);
        tableAadharMaster.deleteAadharDetails(getUserPmId());
        if (profileDetail != null) {
            ProfileDataOperationAadharNumber aadharDetails = profileDetail.getPbAadhar();
            if (aadharDetails != null) {
                aadharDetails.setRcProfileMasterPmId(getUserPmId());
                tableAadharMaster.addAadharDetail(aadharDetails);
            }
        }
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
                mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
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

        // <editor-fold desc="Education Master">

        TableEducationMaster tableEducationMaster = new TableEducationMaster(databaseHandler);

        // Remove Existing Number
        tableEducationMaster.deleteEducation(getUserPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEducation())) {
            ArrayList<ProfileDataOperationEducation> arrayListEducation = profileDetail
                    .getPbEducation();
            ArrayList<Education> arrayListEdu = new ArrayList<>();
            for (int i = 0; i < arrayListEducation.size(); i++) {
                Education education = new Education();
                education.setEdmRecordIndexId(arrayListEducation.get(i).getEduId());
                education.setEdmSchoolCollegeName(arrayListEducation.get(i).getEduName());
                education.setEdmCourse(arrayListEducation.get(i).getEduCourse());
                education.setEdmEducationFromDate(arrayListEducation.get(i).getEduFromDate());
                education.setEdmEducationToDate(String.valueOf(arrayListEducation.get(i)
                        .getEduToDate()));
                education.setEdmEducationIsCurrent(arrayListEducation.get(i).getIsCurrent());
                education.setEdmEducationPrivacy(String.valueOf(arrayListEducation.get(i)
                        .getEduPublic()));
                education.setRcProfileMasterPmId(getUserPmId());
                arrayListEdu.add(education);
            }


            tableEducationMaster.addArrayEducation(arrayListEdu);
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
                address.setAmStateId(arrayListAddress.get(j).getStateId());
                address.setAmCity(arrayListAddress.get(j).getCity());
                address.setAmCityId(arrayListAddress.get(j).getCityId());
                address.setAmAddressPrivacy(String.valueOf(arrayListAddress.get(j).getAddPublic()));
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
                imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                        .getIMAccountPublic()));
                imAccount.setImImFirstName(arrayListImAccount.get(j).getIMAccountFirstName());
                imAccount.setImImLastName(arrayListImAccount.get(j).getIMAccountLastName());
                imAccount.setImImProfileImage(arrayListImAccount.get(j).getIMAccountProfileImage());
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

    private void getUserData() {

        String rawId;

        arrayListPBPhoneNumber = new ArrayList<>();
        arrayListPBEmailAddress = new ArrayList<>();

        if (checkNumberFavourite == null) {
            rawId = phoneBookId;
        } else {
            rawId = checkNumberFavourite;

            if (StringUtils.contains(rawId, ",")) {
                String rawIds[] = rawId.split(",");
                rawId = rawIds[0];
            }
        }

        // From PhoneBook
        if (!StringUtils.isEmpty(rawId)) {
            Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(rawId);

            if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                while (contactNumberCursor.moveToNext()) {

                    arrayListPBPhoneNumber.add(Utils.getFormattedNumber(this,
                            contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER))));

                }
                contactNumberCursor.close();
            }
        }

        //</editor-fold>

        // <editor-fold desc="Email Id">

        // From PhoneBook
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(rawId);

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            while (contactEmailCursor.moveToNext()) {
                arrayListPBEmailAddress.add(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
            }
            contactEmailCursor.close();
        }

        //</editor-fold>
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
        userProfile.setPmBadge(profileDetail.getPmBadge());
        userProfile.setPmLastSeen(profileDetail.getPmLastSeen());
        userProfile.setProfileRatingPrivacy(String.valueOf(profileDetail.getProfileRatingPrivacy()));

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
                mobileNumber.setMnmRecordIndexId(arrayListPhoneNumber.get(i).getPhoneId());
                mobileNumber.setMnmNumberType(arrayListPhoneNumber.get(i).getPhoneType());

                if (String.valueOf(arrayListPhoneNumber.get(i).getPhonePublic()).equalsIgnoreCase
                        ("3")) {
                    mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i)
                            .getPhoneNumber());
                } else {
                    if (arrayListPBPhoneNumber.size() > 0)
                        if (arrayListPBPhoneNumber.contains("+" + arrayListPhoneNumber.get(i)
                                .getOriginalNumber())) {
                            mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i)
                                    .getOriginalNumber());
                        } else {
                            mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i)
                                    .getPhoneNumber());
                        }
                    else
                        mobileNumber.setMnmMobileNumber("+" + arrayListPhoneNumber.get(i)
                                .getPhoneNumber());
                }

                mobileNumber.setMnmNumberPrivacy(String.valueOf(arrayListPhoneNumber.get(i)
                        .getPhonePublic()));
                mobileNumber.setMnmIsPrimary(String.valueOf(arrayListPhoneNumber.get(i)
                        .getPbRcpType()));
                mobileNumber.setMnmPhonePublic(arrayListPhoneNumber.get(i).getPhonePublic());
                mobileNumber.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                arrayListMobileNumber.add(mobileNumber);
            }
            tableMobileMaster.addUpdateArrayMobileNumber(arrayListMobileNumber, profileDetail
                    .getRcpPmId());
        } else {
            tableMobileMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        //<editor-fold desc="Email Master">
        TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileDetail.getPbEmailId();
            ArrayList<Email> arrayListEmail = new ArrayList<>();
            for (int i = 0; i < arrayListEmailId.size(); i++) {
                Email email = new Email();
                email.setEmRecordIndexId(arrayListEmailId.get(i).getEmId());

                if (arrayListPBEmailAddress.size() > 0)
                    if (arrayListPBEmailAddress.contains(arrayListEmailId.get(i).getOriginalEmail
                            ())) {
                        email.setEmEmailAddress(arrayListEmailId.get(i).getOriginalEmail());
                    } else {
                        email.setEmEmailAddress(arrayListEmailId.get(i).getEmEmailId());
                    }
                else
                    email.setEmEmailAddress(arrayListEmailId.get(i).getEmEmailId());

                email.setEmSocialType(arrayListEmailId.get(i).getEmSocialType());
                email.setEmEmailType(arrayListEmailId.get(i).getEmType());
                email.setEmEmailPrivacy(String.valueOf(arrayListEmailId.get(i).getEmPublic()));
                email.setEmIsVerified(String.valueOf(arrayListEmailId.get(i).getEmRcpType()));
                email.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                arrayListEmail.add(email);
            }
            tableEmailMaster.addUpdateArrayEmail(arrayListEmail, profileDetail.getRcpPmId());
        } else {
            tableEmailMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        //<editor-fold desc="Organization Master">
        TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                (databaseHandler);

        tableOrganizationMaster.deleteOrganization(profileDetail.getRcpPmId());

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
                organization.setOmIsPrivate(arrayListOrganization.get(i).getIsPrivate());
                organization.setOmIsCurrent(String.valueOf(arrayListOrganization.get(i)
                        .getIsCurrent()));

                if (arrayListOrganization.get(i).getIsVerify() != null)
                    if (arrayListOrganization.get(i).getIsVerify() == IntegerConstants
                            .RCP_TYPE_PRIMARY) {
                        organization.setOmOrganizationType(arrayListOrganization.get(i)
                                .getOrgIndustryType());
                        organization.setOmOrganizationLogo(arrayListOrganization.get(i)
                                .getEomLogoPath() + "/" + arrayListOrganization.get(i)
                                .getEomLogoName());
                    } else {
                        organization.setOmOrganizationType("");
                        organization.setOmOrganizationLogo("");
                    }
                else {
                    organization.setOmOrganizationType("");
                    organization.setOmOrganizationLogo("");
                }

                organization.setOmEnterpriseOrgId(arrayListOrganization.get(i)
                        .getOrgEntId());
                organization.setOmIsVerified(String.valueOf(arrayListOrganization.get(i)
                        .getIsVerify()));
                organization.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                organizationList.add(organization);
            }

            tableOrganizationMaster.addUpdateArrayOrganization(organizationList, profileDetail
                    .getRcpPmId());
        } else {
            tableOrganizationMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        // <editor-fold desc="Website Master">
        TableWebsiteMaster tableWebsiteMaster = new TableWebsiteMaster(databaseHandler);

        tableWebsiteMaster.deleteWebsite(profileDetail.getRcpPmId());

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
                website.setWmIsPrivate(arrayListWebsite.get(j).getWebIsPrivate());
                website.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                websiteList.add(website);
            }

            tableWebsiteMaster.addUpdateArrayWebsite(websiteList, profileDetail.getRcpPmId());
        } else {
            tableWebsiteMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        //<editor-fold desc="Address Master">
        TableAddressMaster tableAddressMaster = new TableAddressMaster(databaseHandler);

        tableAddressMaster.deleteAddress(profileDetail.getRcpPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            ArrayList<ProfileDataOperationAddress> arrayListAddress = profileDetail.getPbAddress();
            ArrayList<Address> addressList = new ArrayList<>();
            for (int j = 0; j < arrayListAddress.size(); j++) {
                Address address = new Address();

                address.setAmRecordIndexId(arrayListAddress.get(j).getAddId());
                address.setAmFormattedAddress(arrayListAddress.get(j).getFormattedAddress());
                address.setAmCity(arrayListAddress.get(j).getCity());
                address.setAmState(arrayListAddress.get(j).getState());
                address.setAmCountry(arrayListAddress.get(j).getCountry());
                address.setAmStreet(arrayListAddress.get(j).getStreet());
                address.setAmPoBox(arrayListAddress.get(j).getPoBox());
                address.setAmNeighborhood(arrayListAddress.get(j).getNeighborhood());
                address.setAmPostCode(arrayListAddress.get(j).getPostCode());
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

            tableAddressMaster.addUpdateArrayAddress(addressList, profileDetail.getRcpPmId());
        } else {
            tableAddressMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        // <editor-fold desc="Im Account Master">
        TableImMaster tableImMaster = new TableImMaster(databaseHandler);

        tableImMaster.deleteImAccount(profileDetail.getRcpPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts())) {
            ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileDetail
                    .getPbIMAccounts();
            ArrayList<ImAccount> imAccountsList = new ArrayList<>();
            for (int j = 0; j < arrayListImAccount.size(); j++) {
                ImAccount imAccount = new ImAccount();

                imAccount.setImRecordIndexId(arrayListImAccount.get(j).getIMId());
                imAccount.setImImDetail(arrayListImAccount.get(j).getIMAccountDetails());
                imAccount.setImImProtocol(arrayListImAccount.get(j).getIMAccountProtocol());
                imAccount.setImImPrivacy(String.valueOf(arrayListImAccount.get(j)
                        .getIMAccountPublic()));
                imAccount.setImImFirstName(arrayListImAccount.get(j).getIMAccountFirstName());
                imAccount.setImImLastName(arrayListImAccount.get(j).getIMAccountLastName());
                imAccount.setImImProfileImage(arrayListImAccount.get(j).getIMAccountProfileImage());
//                imAccount.setImIsPrivate(arrayListImAccount.get(j).getIMAccountIsPrivate());
                imAccount.setRcProfileMasterPmId(profileDetail.getRcpPmId());

                imAccountsList.add(imAccount);
            }

            tableImMaster.addUpdateArrayImAccount(imAccountsList, profileDetail.getRcpPmId());
        } else {
            tableImMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        // <editor-fold desc="Event Master">
        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        tableEventMaster.deleteEvent(profileDetail.getRcpPmId());

        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            ArrayList<ProfileDataOperationEvent> arrayListEvent = profileDetail.getPbEvent();
            ArrayList<Event> eventList = new ArrayList<>();
            for (int j = 0; j < arrayListEvent.size(); j++) {
                Event event = new Event();
                event.setEvmRecordIndexId(arrayListEvent.get(j).getEventId());
                event.setEvmStartDate(arrayListEvent.get(j).getEventDateTime());
                event.setEvmEventType(arrayListEvent.get(j).getEventType());
                event.setEvmIsYearHidden(arrayListEvent.get(j).getIsYearHidden());
                event.setRcProfileMasterPmId(profileDetail.getRcpPmId());
                eventList.add(event);
            }

            tableEventMaster.addUpdateArrayEvent(eventList, profileDetail.getRcpPmId());
        } else {
            tableEventMaster.deleteData(profileDetail.getRcpPmId());
        }
        //</editor-fold>

        // <editor-fold desc="Aadhar Details">
        TableAadharMaster tableAadharMaster = new TableAadharMaster(databaseHandler);
        // Remove Existing Number
        tableAadharMaster.deleteAadharDetails(profileDetail.getRcpPmId());

        if (profileDetail.getPbAadhar() != null) {
            ProfileDataOperationAadharNumber profileDataOperationAadharNumber = profileDetail
                    .getPbAadhar();
            profileDataOperationAadharNumber.setRcProfileMasterPmId(profileDetail.getRcpPmId());
            tableAadharMaster.addAadharDetail(profileDataOperationAadharNumber);
        }
        //</editor-fold>

        // <editor-fold desc="Education Master">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEducation())) {
            TableEducationMaster tableEducationMaster = new TableEducationMaster(databaseHandler);
            ArrayList<ProfileDataOperationEducation> arrayListEducation = profileDetail.getPbEducation();
            ArrayList<Education> arrayListEdu = new ArrayList<>();
            for (int j = 0; j < arrayListEducation.size(); j++) {

                Education education = new Education();

                education.setEdmRecordIndexId(arrayListEducation.get(j).getEduId());

                education.setEdmSchoolCollegeName(arrayListEducation.get(j).getEduName());
                education.setEdmCourse(arrayListEducation.get(j).getEduCourse());
                education.setEdmEducationFromDate(arrayListEducation.get(j)
                        .getEduFromDate());
                education.setEdmEducationToDate(arrayListEducation.get(j).getEduToDate());
                education.setEdmEducationIsCurrent(arrayListEducation.get(j).getIsCurrent
                        ());
//                        education.setEdmEducationIsPrivate(arrayListEducation.get(j).geti());
                education.setEdmEducationPrivacy(String.valueOf(arrayListEducation.get(j)
                        .getEduPublic()));

                education.setRcProfileMasterPmId(profileDetail.getRcpPmId());

                arrayListEdu.add(education);
            }

            tableEducationMaster.addArrayEducation(arrayListEdu);
        }
        //</editor-fold>
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void setFavouriteStatus(ArrayList<ProfileData> favourites) {

        WsRequestObject favouriteStatusObject = new WsRequestObject();
//        favouriteStatusObject.setPmId(Integer.parseInt(getUserPmId()));
        favouriteStatusObject.setFavourites(favourites);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    favouriteStatusObject, null, WsResponseObject.class, WsConstants
                    .REQ_MARK_AS_FAVOURITE, null, true).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT + WsConstants
                    .REQ_MARK_AS_FAVOURITE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void submitRating(String ratingStar, String comment) {

        WsRequestObject ratingObject = new WsRequestObject();
//        ratingObject.setPmId(Integer.parseInt(getUserPmId()));
        ratingObject.setPrComment(comment);
        ratingObject.setPrRatingStars(ratingStar);
        ratingObject.setPrStatus(String.valueOf(IntegerConstants.RATING_DONE));
        ratingObject.setPrToPmId(Integer.parseInt(pmId));

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    ratingObject, null, WsResponseObject.class, WsConstants.REQ_PROFILE_RATING,
                    null, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig
                    .WS_ROOT + WsConstants.REQ_PROFILE_RATING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void shareContact() {

        WsRequestObject uploadContactObject = new WsRequestObject();
//        uploadContactObject.setPmId(Integer.parseInt(getUserPmId()));
        uploadContactObject.setSendProfileType(IntegerConstants.SEND_PROFILE_NON_RCP_SOCIAL);
        uploadContactObject.setContactData(profileDataOperationVcard);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING, getResources().getString(R.string.msg_please_wait),
                    true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                    WsConstants.REQ_RCP_PROFILE_SHARING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void getProfileDetails() {
        if (Utils.isNetworkAvailable(this)) {
            asyncGetProfileDetails = new AsyncWebServiceCall(this, WSRequestType
                    .REQUEST_TYPE_JSON.getValue(), null, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DETAILS, getResources().getString(R.string.msg_please_wait),
                    true);
            asyncGetProfileDetails.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig
                    .WS_ROOT + WsConstants.REQ_GET_PROFILE_DETAILS + "/" + pmId);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void profileVisit(ArrayList<ProfileVisit> arrayListProfileVisit) {

        WsRequestObject profileVisitObject = new WsRequestObject();
        profileVisitObject.setArrayListProfileVisit(arrayListProfileVisit);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileVisitObject, null, WsResponseObject.class, WsConstants
                    .REQ_ADD_PROFILE_VISIT, null, true).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT + WsConstants
                    .REQ_ADD_PROFILE_VISIT);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void inviteContact(ArrayList<String> arrayListContactNumber, ArrayList<String>
            arrayListEmail) {

        WsRequestObject inviteContactObject = new WsRequestObject();
        inviteContactObject.setArrayListContactNumber(arrayListContactNumber);
        inviteContactObject.setArrayListEmailAddress(arrayListEmail);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    inviteContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_SEND_INVITATION, null, true).executeOnExecutor(AsyncTask
                            .THREAD_POOL_EXECUTOR,
                    BuildConfig.WS_ROOT + WsConstants.REQ_SEND_INVITATION);
        }
        /*else {
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }

    private void fetchOldRecordsServiceCall(ArrayList<CallLogHistoryType> callLogTypeArrayList) {
        // Log.i("HistoryServiceCalled", "Service Started");
        WsRequestObject deviceDetailObject = new WsRequestObject();
        deviceDetailObject.setHistoryTypeArrayList(callLogTypeArrayList);
        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_CALL_LOG_HISTORY_REQUEST, null, true).executeOnExecutor(AsyncTask
                            .THREAD_POOL_EXECUTOR,
                    BuildConfig.WS_ROOT + WsConstants.REQ_GET_CALL_LOG_HISTORY_REQUEST);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }

    }

    //</editor-fold>

    private BroadcastReceiver localBroadcastReceiverDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("networkIssue")) {
                String intentNetworkIssue = intent.getStringExtra("networkIssue");
                if (StringUtils.equalsIgnoreCase(intentNetworkIssue, "true")) {
                    Utils.showErrorSnackBar(ProfileDetailActivity.this,
                            relativeRootProfileDetail, getResources()
                                    .getString(R.string.msg_no_network));
                }
            } else if (intent.hasExtra("responseError")) {
                String responseError = intent.getStringExtra("responseError");
                if (StringUtils.equalsIgnoreCase(responseError, "true")) {
                    if (intent.hasExtra("responseMessage")) {
                        String responseMessage = intent.getStringExtra("responseMessage");
                        if (!StringUtils.isEmpty(responseMessage)) {
                            Utils.showErrorSnackBar(ProfileDetailActivity.this,
                                    relativeRootProfileDetail, responseMessage);
                        }
                    }
                }
            } else if (intent.hasExtra("serverError")) {
                String serverError = intent.getStringExtra("serverError");
                if (StringUtils.equalsIgnoreCase(serverError, "true")) {
                    Utils.showErrorSnackBar(ProfileDetailActivity.this,
                            relativeRootProfileDetail, getResources()
                                    .getString(R.string.msg_try_later));
                }
            } else if (intent.hasExtra("noApps")) {
                String noAppFound = intent.getStringExtra("noApps");
                if (StringUtils.equalsIgnoreCase(noAppFound, "true")) {
                    Utils.showErrorSnackBar(ProfileDetailActivity.this,
                            relativeRootProfileDetail, getResources()
                                    .getString(R.string.error_no_social_app_found));
                }
            }

        }
    };
}
