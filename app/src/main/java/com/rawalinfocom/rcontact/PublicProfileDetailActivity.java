package com.rawalinfocom.rcontact;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.adapters.PublicProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.PrivacySettingPopupDialog;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.PrivacyDataItem;
import com.rawalinfocom.rcontact.model.PrivacyEntityItem;
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
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class PublicProfileDetailActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;

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
    @BindView(R.id.text_full_screen_text)
    TextView textFullScreenText;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_view_all_organization)
    TextView textViewAllOrganization;
    @BindView(R.id.text_time)
    TextView textTime;

    @BindView(R.id.recycler_view_contact_number)
    RecyclerView recyclerViewContactNumber;
    @BindView(R.id.linear_phone)
    LinearLayout linearPhone;

    @BindView(R.id.recycler_view_email)
    RecyclerView recyclerViewEmail;
    @BindView(R.id.linear_email)
    LinearLayout linearEmail;

    @BindView(R.id.recycler_view_website)
    RecyclerView recyclerViewWebsite;
    @BindView(R.id.linear_website)
    LinearLayout linearWebsite;

    @BindView(R.id.recycler_view_address)
    RecyclerView recyclerViewAddress;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;

    @BindView(R.id.recycler_view_education)
    RecyclerView recyclerViewEducation;
    @BindView(R.id.linear_education)
    LinearLayout linearEducation;

    @BindView(R.id.recycler_view_social_contact)
    RecyclerView recyclerViewSocialContact;
    @BindView(R.id.linear_social_contact)
    LinearLayout linearSocialContact;
    @BindView(R.id.card_contact_details)
    CardView cardContactDetails;
    @BindView(R.id.recycler_view_event)
    RecyclerView recyclerViewEvent;
    @BindView(R.id.linear_event)
    LinearLayout linearEvent;
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
    @BindView(R.id.relative_profile_percentage)
    RelativeLayout relativeProfilePercentage;
    @BindView(R.id.linear_call_sms)
    LinearLayout linearCallSms;
   /*@BindView(R.id.relative_call_history)
    RelativeLayout relativeCallHistory;
    @BindView(R.id.recycler_call_history)
    RecyclerView recyclerCallHistory;*/

    @BindView(R.id.ripple_invite)
    RippleView rippleInvite;


    @BindView(R.id.image_enlarge)
    ImageView imageEnlarge;
    @BindView(R.id.frame_image_enlarge)
    FrameLayout frameImageEnlarge;

    @BindView(R.id.frame_container)
    FrameLayout frameContainer;

    ProfileDataOperation profileDataOperationVcard;
    @BindView(R.id.image_aadhar_card)
    ImageView imageAadharCard;
    @BindView(R.id.text_label_aadhar_card)
    TextView textLabelAadharCard;
    @BindView(R.id.button_request)
    AppCompatButton buttonRequest;
    @BindView(R.id.button_privacy)
    ImageView buttonPrivacy;
    @BindView(R.id.text_aadhar_number)
    TextView textAadharNumber;
    @BindView(R.id.relative_aadhar_number)
    RelativeLayout relativeAadharNumber;
    @BindView(R.id.text_label_UIDAI_number)
    TextView textLabelUIDAINumber;
    @BindView(R.id.linear_aadhar_card)
    LinearLayout linearAadharCard;
    @Nullable
    @BindView(R.id.image_last_seen)
    ImageView imageLastSeen;
    @Nullable
    @BindView(R.id.text_label_last_seen)
    TextView textLabelLastSeen;
    @Nullable
    @BindView(R.id.button_request_all)
    AppCompatButton buttonRequestAll;
    @Nullable
    @BindView(R.id.linear_last_seen)
    LinearLayout linearLastSeen;
    @Nullable
    @BindView(R.id.card_last_seen_details)
    CardView cardLastSeenDetails;

//    private Animator mCurrentAnimator;
//    private int mShortAnimationDuration;

    String pmId = "", contactName = "";

    PublicProfileDetailAdapter phoneDetailAdapter;

    RContactApplication rContactApplication;

    LinearLayoutManager mLinearLayoutManager;

    ArrayList<Object> tempPhoneNumber;
    ArrayList<Object> tempEmail;
    ArrayList<Object> tempEducation;

    public String callNumber = "";

    AsyncWebServiceCall asyncGetProfileDetails;
    private boolean hasNumber;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        rContactApplication = (RContactApplication) getApplicationContext();
        ButterKnife.bind(this);

        Intent intent = getIntent();

        getIntentDetails(intent);

        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            //<editor-fold desc="View More">
            case R.id.ripple_view_more:
                if (relativeSectionViewMore.getVisibility() == View.VISIBLE) {
                    relativeSectionViewMore.setVisibility(GONE);
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

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    // Permission Granted
                    Utils.callIntent(PublicProfileDetailActivity.this, callNumber);
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
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            Utils.hideProgressDialog();

            // <editor-fold desc="REQ_GET_PROFILE_DETAILS">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DETAILS)) {
                WsResponseObject getProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (getProfileResponse != null && StringUtils.equalsIgnoreCase(getProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ProfileDataOperation profileDetail = getProfileResponse.getProfileDetail();
                    setUpView(profileDetail);
                    /*storeProfileDataToDb(profileDetail);
                    getDataFromDB();*/

                }
                /*else {
                    Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                    Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                            .string.msg_try_later));
                }*/
            }
            //</editor-fold>

            // <editor-fold desc="REQ_PROFILE_PRIVACY_REQUEST">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_PRIVACY_REQUEST)) {
                WsResponseObject editProfileResponse = (WsResponseObject) data;

                Utils.hideProgressDialog();

                if (editProfileResponse != null && StringUtils.equalsIgnoreCase
                        (editProfileResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
//                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail,
// editProfileResponse.getMessage());
                    Toast.makeText(this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (editProfileResponse != null)
                        Toast.makeText(this, editProfileResponse.getMessage(), Toast
                                .LENGTH_SHORT).show();

//                        Utils.showErrorSnackBar(this, relativeRootProfileDetail,
// editProfileResponse.getMessage());
                    else
                        Toast.makeText(this, getString(R.string.str_request_sending_fail), Toast
                                .LENGTH_SHORT).show();

//                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
// .string.str_request_sending_fail));
                }
            }
            //</editor-fold>
        } else {
            Utils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    public RelativeLayout getRootRelativeLayout() {
        return relativeRootProfileDetail;
    }

    private void getIntentDetails(Intent intent) {
        if (intent != null) {

            if (intent.hasExtra(AppConstants.PREF_USER_NUMBER)) {
                hasNumber = intent.getBooleanExtra(AppConstants.PREF_USER_NUMBER, false);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
                if (!StringUtils.isEmpty(pmId)) {
                    if (!pmId.equalsIgnoreCase("-1") && !pmId.equalsIgnoreCase(getUserPmId())) {
                        if (!Utils.isNetworkAvailable(this)) {
//
////                        getProfileDetails();
////                        ArrayList<ProfileVisit> profileVisits = new ArrayList<>();
////                        ProfileVisit profileVisit = new ProfileVisit();
////                        profileVisit.setVisitorPmId(Integer.parseInt(pmId));
////                        profileVisit.setVisitCount(1);
////                        profileVisits.add(profileVisit);
////                        profileVisit(profileVisits);
//                    } else {
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
        }
    }

    private void layoutVisibility() {

        relativeContactDetails.setVisibility(View.VISIBLE);

        textToolbarTitle.setText(getString(R.string.str_profile_deails));
        linearCallSms.setVisibility(GONE);

    }

    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        rippleActionRightLeft.setVisibility(GONE);
        rippleActionRightCenter.setVisibility(GONE);
        rippleActionRightRight.setVisibility(GONE);
        textName.setVisibility(GONE);
        rippleInvite.setVisibility(GONE);

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEducation.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewContactNumber.setNestedScrollingEnabled(false);
        recyclerViewEmail.setNestedScrollingEnabled(false);
        recyclerViewWebsite.setNestedScrollingEnabled(false);
        recyclerViewEducation.setNestedScrollingEnabled(false);
        recyclerViewAddress.setNestedScrollingEnabled(false);
        recyclerViewEvent.setNestedScrollingEnabled(false);
        recyclerViewSocialContact.setNestedScrollingEnabled(false);

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFullScreenText.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));
        textTime.setTypeface(Utils.typefaceRegular(this));

        textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));

        relativeProfilePercentage.setVisibility(GONE);

        textFullScreenText.setSelected(true);
        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        /*recyclerCallHistory.setLayoutManager(mLinearLayoutManager);
        recyclerCallHistory.setNestedScrollingEnabled(false);*/

        Utils.setRatingColor(PublicProfileDetailActivity.this, ratingUser);

//        mShortAnimationDuration = getResources().getInteger(android.R.integer
// .config_shortAnimTime);

        //call service
        cardContactDetails.setVisibility(GONE);
        cardOtherDetails.setVisibility(GONE);
        getProfileDetails();

        layoutVisibility();
        initSwipe();

    }

    private void setUpView(final ProfileDataOperation profileDetail) {

        try {
            Utils.hideProgressDialog();
            cardContactDetails.setVisibility(View.VISIBLE);
            cardOtherDetails.setVisibility(View.VISIBLE);

            profileDataOperationVcard = new ProfileDataOperation();

            profileDataOperationVcard.setPbNameFirst(contactName);

            //<editor-fold desc="Profile Image">

            if (StringUtils.length(profileDetail.getPbProfilePhoto()) > 0) {
                Glide.with(this)
                        .load(profileDetail.getPbProfilePhoto())
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
                    if (StringUtils.length(profileDetail.getPbProfilePhoto()) > 0) {
                        Utils.zoomImageFromThumb(PublicProfileDetailActivity.this, imageProfile,
                                profileDetail.getPbProfilePhoto(),
                                frameImageEnlarge, imageEnlarge, frameContainer);
                    }
                }
            });

            //</editor-fold>

            //<editor-fold desc="User Name">
            if (profileDetail != null) {
                textFullScreenText.setText(profileDetail.getPbNameFirst() + " " + profileDetail
                        .getPbNameLast());
            }
            //</editor-fold>

            //<editor-fold desc="Organization Detail">

            // From Cloud
            ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                    .getPbOrganization())) {
                arrayListOrganization.addAll(profileDetail.getPbOrganization());
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListOrganization)) {

                final ArrayList<ProfileDataOperationOrganization> tempOrganization = new
                        ArrayList<>();
                tempOrganization.addAll(arrayListOrganization);

                linearOrganizationDetail.setVisibility(View.VISIBLE);

                if (tempOrganization.size() == 1) {
                    textViewAllOrganization.setVisibility(GONE);
                    textTime.setVisibility(View.VISIBLE);
                } else {
                    textViewAllOrganization.setVisibility(View.VISIBLE);
                    textTime.setVisibility(GONE);
                }

                if (arrayListOrganization.size() > 0) {
                    textDesignation.setTextColor(ContextCompat.getColor(PublicProfileDetailActivity
                            .this, R.color.colorAccent));
                    textOrganization.setTextColor(ContextCompat.getColor(PublicProfileDetailActivity
                            .this, R.color.colorAccent));
                    textTime.setTextColor(ContextCompat.getColor(PublicProfileDetailActivity
                            .this, R.color.colorAccent));
                } else {
                    textDesignation.setTextColor(ContextCompat.getColor(PublicProfileDetailActivity
                            .this, R.color.colorBlack));
                    textOrganization.setTextColor(ContextCompat.getColor(PublicProfileDetailActivity
                            .this, R.color.colorBlack));
                    textTime.setVisibility(GONE);
                }

                if (MoreObjects.firstNonNull(tempOrganization.get(0).getIsVerify(), 0) ==
                        IntegerConstants.RCP_TYPE_PRIMARY) {

                    textOrganization.setText(tempOrganization.get(0).getOrgName());
                    textOrganization.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ico_relation_single_tick_green_svg, 0);

                } else {
                    textOrganization.setText(tempOrganization.get(0).getOrgName());
                }
                textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());

                if (StringUtils.equalsIgnoreCase(tempOrganization.get(0).getOrgToDate(), "")) {
                    if (!StringUtils.isEmpty(tempOrganization.get(0).getOrgFromDate())) {
                        String formattedFromDate = Utils.convertDateFormat(tempOrganization.get
                                        (0).getOrgFromDate(),
                                "yyyy-MM-dd", Utils.getEventDateFormat(tempOrganization.get(0)
                                        .getOrgFromDate()));

                        textTime.setText(String.format("%s to Present ", formattedFromDate));
                    } else {
                        textTime.setVisibility(GONE);
                    }
                } else {
                    if (!StringUtils.isEmpty(tempOrganization.get(0).getOrgFromDate()) &&
                            !StringUtils.isEmpty(tempOrganization.get(0).getOrgToDate())) {
                        String formattedFromDate = Utils.convertDateFormat(tempOrganization.get
                                        (0).getOrgFromDate(),
                                "yyyy-MM-dd", Utils.getEventDateFormat(tempOrganization.get(0)
                                        .getOrgFromDate()));
                        String formattedToDate = Utils.convertDateFormat(tempOrganization.get(0)
                                        .getOrgToDate(),
                                "yyyy-MM-dd", Utils.getEventDateFormat(tempOrganization.get(0)
                                        .getOrgToDate()));

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
                            String orgPublicLink = BuildConfig.ORANISATION_PUBLIC_LINK +
                                    tempOrganization.get(0).getOrgEntId();
                            if (!StringUtils.isEmpty(orgPublicLink)) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(orgPublicLink));
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

                linearBasicDetailRating.setEnabled(false);
                textUserRating.setText(profileDetail.getTotalProfileRateUser());

                if ((MoreObjects.firstNonNull(profileDetail.getProfileRatingPrivacy(), 0)) ==
                        IntegerConstants
                                .PRIVACY_EVERYONE) {
                    ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
                } else {
                    ratingUser.setRating(0);
                    ratingUser.setEnabled(false);
                }
            } else {
                textUserRating.setText("0");
                ratingUser.setRating(0);
                ratingUser.setEnabled(false);
            }
            //</editor-fold>

            //<editor-fold desc="Phone Number">

            // From Cloud
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                    .getPbPhoneNumber())) {
                arrayListPhoneNumber.addAll(profileDetail.getPbPhoneNumber());
            }

            tempPhoneNumber = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber)) {
                tempPhoneNumber.addAll(arrayListPhoneNumber);

                linearPhone.setVisibility(View.VISIBLE);
                phoneDetailAdapter = new PublicProfileDetailAdapter(this,
                        tempPhoneNumber, AppConstants.PHONE_NUMBER, pmId);
                if (hasNumber) {
                    phoneDetailAdapter.setShowNumber(true);
                } else {
                    phoneDetailAdapter.setShowNumber(false);
                }
                recyclerViewContactNumber.setAdapter(phoneDetailAdapter);
            } else {
                linearPhone.setVisibility(GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Email Id">

            // From Cloud
            ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
            ArrayList<String> arrayListCloudEmail = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId
                    ())) {
                arrayListEmail.addAll(profileDetail.getPbEmailId());
//                for (int i = 0; i < arrayListEmail.size(); i++) {
//                    arrayListCloudEmail.add(arrayListEmail.get(i).getEmEmailId());
//                }
            }

            tempEmail = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(arrayListEmail)) {
                tempEmail.addAll(arrayListEmail);
//                tempEmail.addAll(arrayListCloudEmail);
                linearEmail.setVisibility(View.VISIBLE);
                PublicProfileDetailAdapter emailDetailAdapter = new PublicProfileDetailAdapter
                        (this, tempEmail,
                                AppConstants.EMAIL, pmId);
                recyclerViewEmail.setAdapter(emailDetailAdapter);
            } else {
                linearEmail.setVisibility(GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Education">

            // From Cloud
            ArrayList<ProfileDataOperationEducation> arrayListEducation = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbEducation())) {
                arrayListEducation.addAll(profileDetail.getPbEducation());

                /*for (int i = 0; i < arrayListEducation.size(); i++) {
                    if ((MoreObjects.firstNonNull(arrayListEducation.get(i).getEduPublic(), 0))
                            == IntegerConstants
                            .PRIVACY_PRIVATE) {
                        pbEducation.add(arrayListEducation.get(i).getEduId());
                    }
                }*/
            }

            tempEducation = new ArrayList<>();
            if (!Utils.isArraylistNullOrEmpty(arrayListEducation)) {
                tempEducation.addAll(arrayListEducation);
                linearEducation.setVisibility(View.VISIBLE);
                /*ProfileDetailAdapter educationDetailAdapter = new ProfileDetailAdapter(this,
                        tempEducation, AppConstants.EDUCATION, false, pmId);*/
                PublicProfileDetailAdapter educationDetailAdapter = new
                        PublicProfileDetailAdapter(this, tempEducation, AppConstants.EDUCATION,
                        pmId);
                recyclerViewEducation.setAdapter(educationDetailAdapter);
            } else {
                linearEducation.setVisibility(View.GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Website">

            // From Cloud
            ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                    ())) {
                arrayListWebsite.addAll(profileDetail.getPbWebAddress());
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListWebsite)) {
                ArrayList<Object> tempWebsite = new ArrayList<>();
                tempWebsite.addAll(arrayListWebsite);

                linearWebsite.setVisibility(View.VISIBLE);
                PublicProfileDetailAdapter websiteDetailAdapter = new PublicProfileDetailAdapter
                        (this,
                                tempWebsite, AppConstants.WEBSITE, pmId);
                recyclerViewWebsite.setAdapter(websiteDetailAdapter);
            } else {
                linearWebsite.setVisibility(GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Address">
            // From Cloud
            ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress
                    ())) {
                arrayListAddress.addAll(profileDetail.getPbAddress());
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListAddress)) {
                ArrayList<Object> tempAddress = new ArrayList<>();
                tempAddress.addAll(arrayListAddress);
                linearAddress.setVisibility(View.VISIBLE);
                PublicProfileDetailAdapter addressDetailAdapter = new PublicProfileDetailAdapter
                        (this,
                                tempAddress, AppConstants.ADDRESS, pmId);
                recyclerViewAddress.setAdapter(addressDetailAdapter);
            } else {
                linearAddress.setVisibility(GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Im Account">

            // From Cloud
            ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts
                    ())) {
                arrayListImAccount.addAll(profileDetail.getPbIMAccounts());
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListImAccount)) {
                ArrayList<Object> tempImAccount = new ArrayList<>();
                tempImAccount.addAll(arrayListImAccount);
                linearSocialContact.setVisibility(View.VISIBLE);
                PublicProfileDetailAdapter imAccountDetailAdapter = new
                        PublicProfileDetailAdapter(this,
                        tempImAccount, AppConstants.IM_ACCOUNT, pmId);
                recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
            } else {
                linearSocialContact.setVisibility(GONE);
            }
            //</editor-fold>

            // <editor-fold desc="Aadhar card details">
            if (profileDetail != null) {
                final ProfileDataOperationAadharNumber aadharDetails;
                if (profileDetail.getPbAadhar() != null) {
                    linearAadharCard.setVisibility(View.VISIBLE);
                    aadharDetails = profileDetail.getPbAadhar();

                    if ((MoreObjects.firstNonNull(aadharDetails.getAadharPublic(), 3)) ==
                            IntegerConstants
                                    .PRIVACY_PRIVATE && aadharDetails.getAadharNumber()
                            .equalsIgnoreCase("0")) {
                        if (hasNumber)
                            buttonRequest.setVisibility(GONE);
                        else
                            buttonRequest.setVisibility(GONE);

                        buttonPrivacy.setVisibility(GONE);
                    } else {
                        if ((MoreObjects.firstNonNull(aadharDetails.getAadharPublic(), 3)) !=
                                IntegerConstants
                                        .PRIVACY_PRIVATE && aadharDetails.getAadharNumber()
                                .equalsIgnoreCase("0")) {
                            if (hasNumber)
                                buttonRequest.setVisibility(GONE);
                            else
                                buttonRequest.setVisibility(GONE);

                            buttonPrivacy.setVisibility(GONE);
                        }
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
                                    PrivacySettingPopupDialog(null, PublicProfileDetailActivity
                                    .this,
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
                                            if (Utils.isNetworkAvailable(PublicProfileDetailActivity
                                                    .this)) {
                                                new AsyncWebServiceCall
                                                        (PublicProfileDetailActivity.this,
                                                                WSRequestType.REQUEST_TYPE_JSON
                                                                        .getValue(),
                                                                wsRequestObject, null,
                                                                WsResponseObject
                                                                        .class,
                                                                WsConstants
                                                                        .REQ_SET_PRIVACY_SETTING,
                                                                PublicProfileDetailActivity
                                                                        .this.getResources()
                                                                        .getString(R
                                                                                .string
                                                                                .msg_please_wait)
                                                                , true)
                                                        .executeOnExecutor
                                                                (AsyncTask.THREAD_POOL_EXECUTOR,
                                                                        BuildConfig.WS_ROOT +
                                                                                WsConstants
                                                                                        .REQ_SET_PRIVACY_SETTING);
                                            } else {
                                                //show no toast
                                                Toast.makeText(PublicProfileDetailActivity.this,
                                                        PublicProfileDetailActivity.this
                                                                .getResources()
                                                                .getString(R.string.msg_no_network),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, AppConstants
                                    .AADHAR_NUMBER,
                                    0, aadharDetails.getAadharPublic(), aadharDetails.getAadharId
                                    () + "");
                            privacySettingPopupDialog.setDialogTitle(PublicProfileDetailActivity
                                    .this
                                    .getResources().getString(R
                                            .string.privacy_dialog_title));
                            privacySettingPopupDialog.showDialog();
                        }
                    });

                } else {
                    linearAadharCard.setVisibility(GONE);
                }
            } else {
                linearAadharCard.setVisibility(GONE);
            }


            textAadharNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!textAadharNumber.getText().toString().contains("X")) {
                        Utils.copyToClipboard(PublicProfileDetailActivity.this,
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

            if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite)) || (!Utils
                    .isArraylistNullOrEmpty(arrayListAddress)) || (!Utils.isArraylistNullOrEmpty
                    (arrayListImAccount))
                    || (profileDetail.getPbAadhar() != null)
                    || (profileDetail.getPbEducation() != null)) {
                rippleViewMore.setVisibility(View.VISIBLE);
            } else {
                rippleViewMore.setVisibility(GONE);
            }

            // <editor-fold desc="Last seen details">

            cardLastSeenDetails.setVisibility(View.VISIBLE);
            buttonRequestAll.setVisibility(GONE);

            if (!StringUtils.isBlank(profileDetail.getPmLastSeen())) {

                long elapsedDays = 0;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
                        "HH:mm:ss", Locale.getDefault());

                final Date d = simpleDateFormat.parse(profileDetail.getPmLastSeen());
                final Calendar calendar = Calendar.getInstance();

                calendar.setTime(d);
                calendar.add(Calendar.HOUR, 5);
                calendar.add(Calendar.MINUTE, 30);

                String startDate = simpleDateFormat.format(calendar.getTime());
                String endDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));

                try {

                    long difference = simpleDateFormat.parse(endDate).getTime() -
                            simpleDateFormat.parse(startDate).getTime();

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

                    String date = Utils.convertDateFormat(startDate, "yyyy-MM-dd HH:mm:ss",
                            "hh:mm a");
                    textLabelLastSeen.setText(String.format("Last seen today at %s", date));
                }

            } else {
                textLabelLastSeen.setText("Last seen N/A");
                textLabelLastSeen.setVisibility(View.VISIBLE);
            }

            // </editor-fold>

            // <editor-fold desc="Event">

            // From Cloud
            ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();
            if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent()
            )) {
                arrayListEvent.addAll(profileDetail.getPbEvent());
            }

            if (!Utils.isArraylistNullOrEmpty(arrayListEvent)) {
                ArrayList<Object> tempEvent = new ArrayList<>();
                tempEvent.addAll(arrayListEvent);
                linearEvent.setVisibility(View.VISIBLE);
                PublicProfileDetailAdapter eventDetailAdapter = new PublicProfileDetailAdapter
                        (this, tempEvent,
                                AppConstants.EVENT, pmId);
                recyclerViewEvent.setAdapter(eventDetailAdapter);
            } else {
                linearEvent.setVisibility(GONE);
                cardOtherDetails.setVisibility(GONE);
            }
            //</editor-fold>

            linearGender.setVisibility(GONE);

            if (Utils.isArraylistNullOrEmpty(arrayListEvent) && StringUtils.length(StringUtils
                    .defaultString(profileDetail.getPbGender())) <= 0) {
                cardOtherDetails.setVisibility(GONE);
            } else {
                cardOtherDetails.setVisibility(View.VISIBLE);
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

        rippleLeft.setVisibility(GONE);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_close);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = dialog.findViewById(R.id.recycler_view_dialog_list);
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

                actionNumber = StringUtils.defaultString(((PublicProfileDetailAdapter
                        .ProfileDetailViewHolder) viewHolder).textMain.getText()
                        .toString());

                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");

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
                if (viewHolder instanceof PublicProfileDetailAdapter.ProfileDetailViewHolder &&
                        StringUtils.startsWithIgnoreCase(((PublicProfileDetailAdapter
                                .ProfileDetailViewHolder) viewHolder).textMain.getText()
                                .toString(), "+XX") || viewHolder instanceof
                        PublicProfileDetailAdapter.ProfileDetailViewHolder &&
                        StringUtils.startsWithIgnoreCase(((PublicProfileDetailAdapter
                                .ProfileDetailViewHolder) viewHolder).textMain.getText()
                                .toString(), "" +
                                "XX")) {
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
                        p.setColor(ContextCompat.getColor(PublicProfileDetailActivity.this, R.color
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
                        p.setColor(ContextCompat.getColor(PublicProfileDetailActivity.this, R.color
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

        String finalNumber = Utils.getFormattedNumber(PublicProfileDetailActivity.this, number);

        if (ContextCompat.checkSelfPermission(PublicProfileDetailActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager
                .PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission
                    .CALL_PHONE}, AppConstants
                    .MY_PERMISSIONS_REQUEST_PHONE_CALL);
        else {
            Utils.callIntent(PublicProfileDetailActivity.this, finalNumber);
        }
    }

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

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

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

    //</editor-fold>

}
