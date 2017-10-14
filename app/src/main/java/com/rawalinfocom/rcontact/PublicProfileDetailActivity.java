package com.rawalinfocom.rcontact;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.PublicProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    String pmId = "", contactName = "";

    PublicProfileDetailAdapter phoneDetailAdapter;

    RContactApplication rContactApplication;

    LinearLayoutManager mLinearLayoutManager;

    ArrayList<Object> tempPhoneNumber;
    ArrayList<Object> tempEmail;

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
//                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, editProfileResponse.getMessage());
                    Toast.makeText(this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (editProfileResponse != null)
                        Toast.makeText(this, editProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();

//                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, editProfileResponse.getMessage());
                    else
                        Toast.makeText(this, getString(R.string.str_request_sending_fail), Toast.LENGTH_SHORT).show();

//                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R.string.str_request_sending_fail));
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
        linearCallSms.setVisibility(View.GONE);

    }

    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        rippleActionRightLeft.setVisibility(View.GONE);
        rippleActionRightCenter.setVisibility(View.GONE);
        rippleActionRightRight.setVisibility(View.GONE);
        textName.setVisibility(View.GONE);
        rippleInvite.setVisibility(View.GONE);

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewContactNumber.setNestedScrollingEnabled(false);
        recyclerViewEmail.setNestedScrollingEnabled(false);
        recyclerViewWebsite.setNestedScrollingEnabled(false);
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

        relativeProfilePercentage.setVisibility(View.GONE);

        textFullScreenText.setSelected(true);
        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);

        mLinearLayoutManager = new LinearLayoutManager(this);
        /*recyclerCallHistory.setLayoutManager(mLinearLayoutManager);
        recyclerCallHistory.setNestedScrollingEnabled(false);*/

        Utils.setRatingColor(PublicProfileDetailActivity.this, ratingUser);

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        //call service
        cardContactDetails.setVisibility(View.GONE);
        cardOtherDetails.setVisibility(View.GONE);
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
                        Utils.zoomImageFromThumb(PublicProfileDetailActivity.this, imageProfile, profileDetail.getPbProfilePhoto(),
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
                    textViewAllOrganization.setVisibility(View.GONE);
                    textTime.setVisibility(View.VISIBLE);
                } else {
                    textViewAllOrganization.setVisibility(View.VISIBLE);
                    textTime.setVisibility(View.GONE);
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
                    textTime.setVisibility(View.GONE);
                }
                textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());
                textOrganization.setText(tempOrganization.get(0).getOrgName());

                if (StringUtils.equalsIgnoreCase(tempOrganization.get(0).getOrgToDate(), "")) {
                    if (!StringUtils.isEmpty(tempOrganization.get(0).getOrgFromDate())) {
                        String formattedFromDate = Utils.convertDateFormat(tempOrganization.get(0).getOrgFromDate(),
                                "yyyy-MM-dd hh:mm:ss", Utils.getEventDateFormat(tempOrganization.get(0).getOrgFromDate()));

                        textTime.setText(String.format("%s to Present ", formattedFromDate));
                    } else {
                        textTime.setVisibility(View.GONE);
                    }
                } else {
                    if (!StringUtils.isEmpty(tempOrganization.get(0).getOrgFromDate()) && !StringUtils.isEmpty(tempOrganization.get(0).getOrgToDate())) {
                        String formattedFromDate = Utils.convertDateFormat(tempOrganization.get(0).getOrgFromDate(),
                                "yyyy-MM-dd hh:mm:ss", Utils.getEventDateFormat(tempOrganization.get(0).getOrgFromDate()));
                        String formattedToDate = Utils.convertDateFormat(tempOrganization.get(0).getOrgToDate(),
                                "yyyy-MM-dd hh:mm:ss", Utils.getEventDateFormat(tempOrganization.get(0).getOrgToDate()));

                        textTime.setText(String.format("%s to %s ", formattedFromDate, formattedToDate));
                    }
                }

                textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAllOrganizations(tempOrganization);
                    }
                });

            } else {
                linearOrganizationDetail.setVisibility(View.INVISIBLE);
            }
            //</editor-fold>

            //<editor-fold desc="User Rating">
            if (profileDetail != null) {
                textUserRating.setText(profileDetail.getTotalProfileRateUser());
                ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
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
                linearPhone.setVisibility(View.GONE);
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
                PublicProfileDetailAdapter emailDetailAdapter = new PublicProfileDetailAdapter(this, tempEmail,
                        AppConstants.EMAIL, pmId);
                recyclerViewEmail.setAdapter(emailDetailAdapter);
            } else {
                linearEmail.setVisibility(View.GONE);
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
                PublicProfileDetailAdapter websiteDetailAdapter = new PublicProfileDetailAdapter(this,
                        tempWebsite, AppConstants.WEBSITE, pmId);
                recyclerViewWebsite.setAdapter(websiteDetailAdapter);
            } else {
                linearWebsite.setVisibility(View.GONE);
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
                PublicProfileDetailAdapter addressDetailAdapter = new PublicProfileDetailAdapter(this,
                        tempAddress, AppConstants.ADDRESS, pmId);
                recyclerViewAddress.setAdapter(addressDetailAdapter);
            } else {
                linearAddress.setVisibility(View.GONE);
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
                PublicProfileDetailAdapter imAccountDetailAdapter = new PublicProfileDetailAdapter(this,
                        tempImAccount, AppConstants.IM_ACCOUNT, pmId);
                recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
            } else {
                linearSocialContact.setVisibility(View.GONE);
            }
            //</editor-fold>

            if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite)) || (!Utils
                    .isArraylistNullOrEmpty(arrayListAddress)) || (!Utils.isArraylistNullOrEmpty
                    (arrayListImAccount))) {
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

            if (!Utils.isArraylistNullOrEmpty(arrayListEvent)) {
                ArrayList<Object> tempEvent = new ArrayList<>();
                tempEvent.addAll(arrayListEvent);
                linearEvent.setVisibility(View.VISIBLE);
                PublicProfileDetailAdapter eventDetailAdapter = new PublicProfileDetailAdapter(this, tempEvent,
                        AppConstants.EVENT, pmId);
                recyclerViewEvent.setAdapter(eventDetailAdapter);
            } else {
                linearEvent.setVisibility(View.GONE);
                cardOtherDetails.setVisibility(View.GONE);
            }
            //</editor-fold>

            linearGender.setVisibility(View.GONE);

            if (Utils.isArraylistNullOrEmpty(arrayListEvent) && StringUtils.length(StringUtils
                    .defaultString(profileDetail != null ? profileDetail.getPbGender() : null))
                    <= 0) {
                cardOtherDetails.setVisibility(View.GONE);
            } else {
                cardOtherDetails.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
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

//    private void zoomImageFromThumb(final View thumbView, String imageUrl) {
//        // If there's an animation in progress, cancel it
//        // immediately and proceed with this one.
//        if (mCurrentAnimator != null) {
//            mCurrentAnimator.cancel();
//        }
//
//        // Load the high-resolution "zoomed-in" image.
////        imageEnlarge.setImageResource(imageResId);
//        Glide.with(this)
//                .load(imageUrl)
//                .placeholder(R.drawable.home_screen_profile)
//                .bitmapTransform(new CropCircleTransformation(this))
//                .error(R.drawable.home_screen_profile)
//                .into(imageEnlarge);
//
//        // Calculate the starting and ending bounds for the zoomed-in image.
//        // This step involves lots of math. Yay, math.
//        final Rect startBounds = new Rect();
//        final Rect finalBounds = new Rect();
//        final Point globalOffset = new Point();
//
//        // The start bounds are the global visible rectangle of the thumbnail,
//        // and the final bounds are the global visible rectangle of the container
//        // view. Also set the container view's offset as the origin for the
//        // bounds, since that's the origin for the positioning animation
//        // properties (X, Y).
//        thumbView.getGlobalVisibleRect(startBounds);
//        frameContainer.getGlobalVisibleRect(finalBounds, globalOffset);
//        startBounds.offset(-globalOffset.x, -globalOffset.y);
//        finalBounds.offset(-globalOffset.x, -globalOffset.y);
//
//        // Adjust the start bounds to be the same aspect ratio as the final
//        // bounds using the "center crop" technique. This prevents undesirable
//        // stretching during the animation. Also calculate the start scaling
//        // factor (the end scaling factor is always 1.0).
//        float startScale;
//        if ((float) finalBounds.width() / finalBounds.height()
//                > (float) startBounds.width() / startBounds.height()) {
//            // Extend start bounds horizontally
//            startScale = (float) startBounds.height() / finalBounds.height();
//            float startWidth = startScale * finalBounds.width();
//            float deltaWidth = (startWidth - startBounds.width()) / 2;
//            startBounds.left -= deltaWidth;
//            startBounds.right += deltaWidth;
//        } else {
//            // Extend start bounds vertically
//            startScale = (float) startBounds.width() / finalBounds.width();
//            float startHeight = startScale * finalBounds.height();
//            float deltaHeight = (startHeight - startBounds.height()) / 2;
//            startBounds.top -= deltaHeight;
//            startBounds.bottom += deltaHeight;
//        }
//
//        // Hide the thumbnail and show the zoomed-in view. When the animation
//        // begins, it will position the zoomed-in view in the place of the
//        // thumbnail.
//        thumbView.setAlpha(0f);
////        imageEnlarge.setVisibility(View.VISIBLE);
//        frameImageEnlarge.setVisibility(View.VISIBLE);
//        frameImageEnlarge.animate()
//                .alpha(1.0f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        frameImageEnlarge.setVisibility(View.VISIBLE);
//                    }
//                });
//
//
//        // Set the pivot point for SCALE_X and SCALE_Y transformations
//        // to the top-left corner of the zoomed-in view (the default
//        // is the center of the view).
//        imageEnlarge.setPivotX(0f);
//        imageEnlarge.setPivotY(0f);
//
//        // Construct and run the parallel animation of the four translation and
//        // scale properties (X, Y, SCALE_X, and SCALE_Y).
//        AnimatorSet set = new AnimatorSet();
//        set.play(ObjectAnimator.ofFloat(imageEnlarge, View.X,
//                startBounds.left, finalBounds.left))
//                .with(ObjectAnimator.ofFloat(imageEnlarge, View.Y,
//                        startBounds.top, finalBounds.top))
//                .with(ObjectAnimator.ofFloat(imageEnlarge, View.SCALE_X,
//                        startScale, 1f)).with(ObjectAnimator.ofFloat(imageEnlarge,
//                View.SCALE_Y, startScale, 1f));
//        set.setDuration(mShortAnimationDuration);
//        set.setInterpolator(new DecelerateInterpolator());
//        set.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mCurrentAnimator = null;
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//                mCurrentAnimator = null;
//            }
//        });
//        set.start();
//        mCurrentAnimator = set;
//
//        // Upon clicking the zoomed-in image, it should zoom back down
//        // to the original bounds and show the thumbnail instead of
//        // the expanded image.
//        final float startScaleFinal = startScale;
//        imageEnlarge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mCurrentAnimator != null) {
//                    mCurrentAnimator.cancel();
//                }
//
//                // Animate the four positioning/sizing properties in parallel,
//                // back to their original values.
//                AnimatorSet set = new AnimatorSet();
//                set.play(ObjectAnimator
//                        .ofFloat(imageEnlarge, View.X, startBounds.left))
//                        .with(ObjectAnimator
//                                .ofFloat(imageEnlarge,
//                                        View.Y, startBounds.top))
//                        .with(ObjectAnimator
//                                .ofFloat(imageEnlarge,
//                                        View.SCALE_X, startScaleFinal))
//                        .with(ObjectAnimator
//                                .ofFloat(imageEnlarge,
//                                        View.SCALE_Y, startScaleFinal));
//                set.setDuration(mShortAnimationDuration);
//                set.setInterpolator(new DecelerateInterpolator());
//                set.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        thumbView.setAlpha(1f);
////                        imageEnlarge.setVisibility(View.GONE);
//                        frameImageEnlarge.setVisibility(View.GONE);
//                       /* frameImageEnlarge.animate()
//                                .alpha(0.0f)
//                                .setDuration(70)
//                                .setListener(new AnimatorListenerAdapter() {
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        super.onAnimationEnd(animation);
//                                        frameImageEnlarge.setVisibility(View.GONE);
//                                    }
//                                });*/
//                        mCurrentAnimator = null;
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        thumbView.setAlpha(1f);
////                        imageEnlarge.setVisibility(View.GONE);
//                        frameImageEnlarge.setVisibility(View.GONE);
//                      /*  frameImageEnlarge.animate()
//                                .alpha(0.0f)
//                                .setDuration(70)
//                                .setListener(new AnimatorListenerAdapter() {
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        super.onAnimationEnd(animation);
//                                        frameImageEnlarge.setVisibility(View.GONE);
//                                    }
//                                });*/
//
//                        mCurrentAnimator = null;
//                    }
//                });
//                set.start();
//                mCurrentAnimator = set;
//            }
//        });
//    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void getProfileDetails() {
        if (Utils.isNetworkAvailable(this)) {
            asyncGetProfileDetails = new AsyncWebServiceCall(this, WSRequestType
                    .REQUEST_TYPE_JSON.getValue(), null, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DETAILS, getResources().getString(R.string.msg_please_wait),
                    true);
            asyncGetProfileDetails.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants
                    .WS_ROOT + WsConstants.REQ_GET_PROFILE_DETAILS + "/" + pmId);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>

}
