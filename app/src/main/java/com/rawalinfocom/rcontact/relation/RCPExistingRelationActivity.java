package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hardik on 03/10/17.
 */

public class RCPExistingRelationActivity extends BaseActivity implements WsResponseListener, View.OnClickListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_relation)
    ImageView imageRelation;
    @BindView(R.id.ripple_action_relation)
    RippleView rippleActionRelation;
    @BindView(R.id.image_right_left)
    ImageView imageRightLeft;
    @BindView(R.id.ripple_action_right_left)
    RippleView rippleActionRightLeft;
    @BindView(R.id.image_right_center)
    ImageView imageRightCenter;
    @BindView(R.id.ripple_action_right_center)
    RippleView rippleActionRightCenter;
    @BindView(R.id.image_right_right)
    ImageView imageRightRight;
    @BindView(R.id.ripple_action_right_right)
    RippleView rippleActionRightRight;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;

    @BindView(R.id.text_full_screen_text)
    TextView textFullScreenText;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_view_all_organization)
    TextView textViewAllOrganization;
    @BindView(R.id.linear_organization_detail)
    LinearLayout linearOrganizationDetail;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.img_user_rating)
    ImageView imgUserRating;
    @BindView(R.id.rating_user)
    RatingBar ratingUser;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.linear_basic_detail)
    LinearLayout linearBasicDetail;
    @BindView(R.id.relative_basic_detail)
    RelativeLayout relativeBasicDetail;
    @BindView(R.id.text_no_relation)
    TextView textNoRelation;
    @BindView(R.id.recycle_view_relation)
    RecyclerView recycleViewRelation;
    @BindView(R.id.relative_root_existing_relation)
    RelativeLayout relativeRootExistingRelation;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @BindView(R.id.image_enlarge)
    ImageView imageEnlarge;
    @BindView(R.id.frame_image_enlarge)
    FrameLayout frameImageEnlarge;
    @BindView(R.id.frame_container)
    FrameLayout frameContainer;


    private Activity activity;
    private IndividualRelationRecommendationListAdapter listAdapter;

    private String contactName = "", thumbnailUrl;
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private String pmId;

    // For relation
    // Business - 0
    // Family - 1
    // Friend - 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcp_existing_relation);

        activity = RCPExistingRelationActivity.this;

        ButterKnife.bind(this);
        initToolbar();
        init();
        getIntentDetails(getIntent());
        displayRCPUserData();
        makeTempDataAndSetAdapter();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.image_action_back:
                finish();
                break;
            case R.id.image_add_new:
                startActivity(new Intent(activity, AddNewRelationActivity.class));
                break;
        }
    }

    private void initToolbar() {
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(getResources().getString(R.string.relation_toolbar_title));
        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        imageActionBack.setOnClickListener(this);
    }

    private void init() {

        rippleActionRelation.setVisibility(View.GONE);
        rippleActionRightLeft.setVisibility(View.GONE);
        rippleActionRightCenter.setVisibility(View.GONE);
        rippleActionRightRight.setVisibility(View.GONE);

        textFullScreenText.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));

        textFullScreenText.setSelected(true);

        textNoRelation.setTypeface(Utils.typefaceRegular(this));
        recycleViewRelation.setVisibility(View.VISIBLE);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, AddNewRelationActivity.class));
            }
        });

        rippleActionBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                onBackPressed();
            }
        });
    }

    private void displayRCPUserData() {

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
                    Utils.zoomImageFromThumb(activity, imageProfile, userProfile.getPmProfileImage(),
                            frameImageEnlarge, imageEnlarge, frameContainer);
                }

            }
        });

        getOrganizationsList();
        getRCPUserRating();
    }

    private void getIntentDetails(Intent intent) {

        if (intent != null) {

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
        }
//            if (intent.hasExtra(AppConstants.EXTRA_FROM_NOTI_PROFILE)) {
//                isFromNotification = intent.getBooleanExtra(AppConstants.EXTRA_FROM_NOTI_PROFILE, false);
//            }
//            if (intent.hasExtra(AppConstants.EXTRA_DIALOG_CALL_LOG_INSTANCE)) {
//                isDialogCallLogInstance = intent.getBooleanExtra(AppConstants
//                        .EXTRA_DIALOG_CALL_LOG_INSTANCE, false);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_RCP_VERIFIED_ID)) {
//                callLogRcpVerfiedId = intent.getStringExtra(AppConstants.EXTRA_RCP_VERIFIED_ID);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER)) {
//                historyNumber = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NAME)) {
//                historyName = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NAME);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE)) {
//                profileActivityCallInstance = intent.getBooleanExtra(AppConstants
//                        .EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, false);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_DATE)) {
//                historyDate = intent.getLongExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, 0);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CALL_UNIQUE_ID)) {
//                hashMapKey = intent.getStringExtra(AppConstants.EXTRA_CALL_UNIQUE_ID);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID)) {
//                uniqueContactId = intent.getStringExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE)) {
//                profileThumbnail = intent.getStringExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME)) {
//                callLogCloudName = intent.getStringExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_IS_RCP_USER)) {
//                isCallLogRcpUser = intent.getBooleanExtra(AppConstants.EXTRA_IS_RCP_USER, false);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_PHONE_BOOK_ID)) {
//                phoneBookId = intent.getStringExtra(AppConstants.EXTRA_PHONE_BOOK_ID);
//            } else {
//                phoneBookId = "-1";
//            }
//            Log.i("phonebookId", phoneBookId);
//
//            if (intent.hasExtra(AppConstants.EXTRA_IS_FROM_FAVOURITE)) {
//                isFromFavourite = intent.getBooleanExtra(AppConstants.EXTRA_IS_FROM_FAVOURITE,
//                        false);
//            }
//
//
//            if (intent.hasExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME)) {
//                cloudContactName = intent.getStringExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME);
//                cloudContactName = StringUtils.substring(cloudContactName, 2, cloudContactName
//                        .length() - 1);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE)) {
//                isHideFavourite = true;
//                checkNumberFavourite = intent.getStringExtra(AppConstants
//                        .EXTRA_CHECK_NUMBER_FAVOURITE);
//            }
//
//            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_POSITION)) {
//                listClickedPosition = intent.getIntExtra(AppConstants.EXTRA_CONTACT_POSITION, -1);
//            }
//        }
//
//        if (phoneBookId.equals("-1"))
//            phoneBookId = getStarredStatusFromNumber(historyNumber);
    }

    private void getOrganizationsList() {

        linearOrganizationDetail.setVisibility(View.VISIBLE);

        TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                (databaseHandler);

        arrayListOrganization = tableOrganizationMaster.getAllOrganizationsFromPmId(Integer.parseInt(pmId));

        if (arrayListOrganization.size() == 1) {
            textViewAllOrganization.setVisibility(View.GONE);
        } else {
            textViewAllOrganization.setVisibility(View.VISIBLE);
        }

        if (arrayListOrganization.size() > 0) {
            textDesignation.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            textOrganization.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));

            textDesignation.setText(arrayListOrganization.get(0).getOrgJobTitle());
            textOrganization.setText(arrayListOrganization.get(0).getOrgName());

        } else {
            linearOrganizationDetail.setVisibility(View.GONE);
        }

        textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllOrganizations();
            }
        });
    }

    private void getRCPUserRating() {

        TableProfileMaster profileDetail = new TableProfileMaster(databaseHandler);

        UserProfile userProfile = profileDetail.getProfileFromPmId(Integer.parseInt(pmId));

        //<editor-fold desc="User Rating">
        if (userProfile != null) {
            textUserRating.setText(userProfile.getTotalProfileRateUser());
            ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));
        } else {
            textUserRating.setText("0");
            ratingUser.setRating(0);
            ratingUser.setEnabled(false);
        }
        //</editor-fold>
    }

    private void showAllOrganizations() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_done);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_back);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationRelationListAdapter adapter = new OrganizationRelationListAdapter(this, arrayListOrganization,
                new OrganizationRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String orgName) {
                    }
                });

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void makeTempDataAndSetAdapter() {

        ArrayList<IndividualRelationRecommendationType> arrayList = new ArrayList<>();

        IndividualRelationRecommendationType individualRelationRecommendationType;

        // All
        individualRelationRecommendationType = new IndividualRelationRecommendationType();
        individualRelationRecommendationType.setRelationName("Co-worker");
        individualRelationRecommendationType.setOrganizationName("Hungama");
        individualRelationRecommendationType.setFamilyName("");
        individualRelationRecommendationType.setIsFriendRelation(false);

        arrayList.add(individualRelationRecommendationType);

        individualRelationRecommendationType = new IndividualRelationRecommendationType();
        individualRelationRecommendationType.setRelationName("Co-worker");
        individualRelationRecommendationType.setOrganizationName("RawalInfocom");
        individualRelationRecommendationType.setFamilyName("");
        individualRelationRecommendationType.setIsFriendRelation(false);

        arrayList.add(individualRelationRecommendationType);

        individualRelationRecommendationType = new IndividualRelationRecommendationType();
        individualRelationRecommendationType.setRelationName("Co-worker");
        individualRelationRecommendationType.setOrganizationName("Peacock Technologies");
        individualRelationRecommendationType.setFamilyName("");
        individualRelationRecommendationType.setIsFriendRelation(false);

        arrayList.add(individualRelationRecommendationType);

        individualRelationRecommendationType = new IndividualRelationRecommendationType();
        individualRelationRecommendationType.setRelationName("");
        individualRelationRecommendationType.setOrganizationName("");
        individualRelationRecommendationType.setFamilyName("Brother");
        individualRelationRecommendationType.setIsFriendRelation(false);

        arrayList.add(individualRelationRecommendationType);

        individualRelationRecommendationType = new IndividualRelationRecommendationType();
        individualRelationRecommendationType.setRelationName("");
        individualRelationRecommendationType.setOrganizationName("");
        individualRelationRecommendationType.setFamilyName("Uncle");
        individualRelationRecommendationType.setIsFriendRelation(false);

        arrayList.add(individualRelationRecommendationType);

        individualRelationRecommendationType = new IndividualRelationRecommendationType();
        individualRelationRecommendationType.setRelationName("");
        individualRelationRecommendationType.setOrganizationName("");
        individualRelationRecommendationType.setFamilyName("");
        individualRelationRecommendationType.setIsFriendRelation(true);

        arrayList.add(individualRelationRecommendationType);

        listAdapter = new IndividualRelationRecommendationListAdapter(activity, arrayList, "rcp");
        recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
        recycleViewRelation.setAdapter(listAdapter);
    }
}
