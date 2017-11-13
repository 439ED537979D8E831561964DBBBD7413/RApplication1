package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.rawalinfocom.rcontact.asynctasks.AsyncGetWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableRelationMappingMaster;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRequest;
import com.rawalinfocom.rcontact.model.RelationRequestResponse;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

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
    @BindView(R.id.title_establish_relation)
    TextView titleEstablishRelation;
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

    private String contactName = "", thumbnailUrl, contactNumber = "";
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private String pmId;
    private TableRelationMappingMaster tableRelationMappingMaster;
    private ArrayList<RelationRecommendationType> existingRelationList;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayRCPUserData();
        getUserExistingRelation();
    }


    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_GET_RELATION">
            if (serviceType.contains(WsConstants.REQ_GET_RELATION)) {
                WsResponseObject sendRelationRequestObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (sendRelationRequestObject != null && StringUtils.equalsIgnoreCase
                        (sendRelationRequestObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<RelationRequest> allExistingRelationList = sendRelationRequestObject.
                            getAllExistingRelationList();

//                    Utils.showSuccessSnackBar(activity, relativeRootExistingRelation,
//                            "New Relation Added Successfully!!!");
                    storeProfileDataToDb(allExistingRelationList);
                    getExistingRelationData();

                } else {
                    if (sendRelationRequestObject != null) {
                        Log.e("error response", sendRelationRequestObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootExistingRelation,
                                sendRelationRequestObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "sendRelationRequestResponse null");
                        Utils.showErrorSnackBar(this, relativeRootExistingRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            Utils.showErrorSnackBar(this, relativeRootExistingRelation, "" + error
                    .getLocalizedMessage());
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.image_action_back:
                finish();
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

        textNoRelation.setVisibility(View.GONE);
        recycleViewRelation.setVisibility(View.VISIBLE);

        tableRelationMappingMaster = new TableRelationMappingMaster(databaseHandler);

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
        textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        Utils.setRatingColor(activity, ratingUser);

        textNoRelation.setTypeface(Utils.typefaceRegular(this));
        titleEstablishRelation.setTypeface(Utils.typefaceRegular(this));
        recycleViewRelation.setVisibility(View.VISIBLE);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, AddNewRelationActivity.class);
                intent.putExtra(AppConstants.EXTRA_PM_ID, pmId);
                intent.putExtra(AppConstants.EXTRA_IS_FROM, "existing");

                if (existingRelationList.size() > 0) {
                    intent.putExtra(AppConstants.EXTRA_EXISTING_RELATION_DETAILS, existingRelationList.get(0));
                } else {
                    intent.putExtra(AppConstants.EXTRA_CONTACT_NAME, contactName);
                    intent.putExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
                    intent.putExtra(AppConstants.EXTRA_CONTACT_NUMBER, contactNumber);
                }

                startActivity(intent);
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

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NUMBER)) {
                contactNumber = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NUMBER);
            } else {
                contactNumber = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL)) {
                thumbnailUrl = intent.getStringExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL);
            } else {
                thumbnailUrl = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
            } else {
                pmId = "-1";
            }
        }
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
            linearOrganizationDetail.setVisibility(View.INVISIBLE);
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
                }, "");

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void storeProfileDataToDb(ArrayList<RelationRequest> relationRequestResponse) {

        //<editor-fold desc="Relation Mapping Master">
        TableRelationMappingMaster tableRelationMappingMaster = new
                TableRelationMappingMaster(databaseHandler);

        if (!Utils.isArraylistNullOrEmpty(relationRequestResponse)) {

            ArrayList<RelationRequestResponse> relationResponseList = new ArrayList<>();

            for (int i = 0; i < relationRequestResponse.size(); i++) {

                RelationRequest relationRequest = relationRequestResponse.get(i);

                //<editor-fold desc="Family Relation">
                ArrayList<RelationRequest> familyRelation = relationRequest.getFamilyRelationList();
                if (!Utils.isArraylistNullOrEmpty(familyRelation)) {

                    for (int j = 0; j < familyRelation.size(); j++) {

                        RelationRequestResponse relationResponse = new RelationRequestResponse();

                        relationResponse.setId(familyRelation.get(j).getId());
                        relationResponse.setRcRelationMasterId(familyRelation.get(j).getRcRelationMasterId());
                        relationResponse.setRrmToPmId(familyRelation.get(j).getRrmToPmId());
                        relationResponse.setRrmType(familyRelation.get(j).getRrmType());
                        relationResponse.setRrmFromPmId(familyRelation.get(j).getRrmFromPmId());
                        relationResponse.setRcStatus(familyRelation.get(j).getRcStatus());
                        relationResponse.setRcOrgId(familyRelation.get(j).getRcOrgId());
                        relationResponse.setCreatedAt(familyRelation.get(j).getCreatedAt());

                        relationResponseList.add(relationResponse);
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Friend Relation">
                ArrayList<RelationRequest> friendRelation = relationRequest.getFriendRelationList();
                if (!Utils.isArraylistNullOrEmpty(friendRelation)) {

                    for (int j = 0; j < friendRelation.size(); j++) {

                        RelationRequestResponse relationResponse = new RelationRequestResponse();

                        relationResponse.setId(friendRelation.get(j).getId());
                        relationResponse.setRcRelationMasterId(friendRelation.get(j).getRcRelationMasterId());
                        relationResponse.setRrmToPmId(friendRelation.get(j).getRrmToPmId());
                        relationResponse.setRrmType(friendRelation.get(j).getRrmType());
                        relationResponse.setRrmFromPmId(friendRelation.get(j).getRrmFromPmId());
                        relationResponse.setRcStatus(friendRelation.get(j).getRcStatus());
                        relationResponse.setRcOrgId(friendRelation.get(j).getRcOrgId());
                        relationResponse.setCreatedAt(friendRelation.get(j).getCreatedAt());

                        relationResponseList.add(relationResponse);
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Business Relation">
                ArrayList<RelationRequest> businessRelation = relationRequest.getBusinessRelationList();
                if (!Utils.isArraylistNullOrEmpty(businessRelation)) {

                    for (int j = 0; j < businessRelation.size(); j++) {

                        RelationRequestResponse relationResponse = new RelationRequestResponse();

                        relationResponse.setId(businessRelation.get(j).getId());
                        relationResponse.setRcRelationMasterId(businessRelation.get(j).getRcRelationMasterId());
                        relationResponse.setRrmToPmId(businessRelation.get(j).getRrmToPmId());
                        relationResponse.setRrmType(businessRelation.get(j).getRrmType());
                        relationResponse.setRrmFromPmId(businessRelation.get(j).getRrmFromPmId());
                        relationResponse.setRcStatus(businessRelation.get(j).getRcStatus());
                        relationResponse.setRcOrgId(businessRelation.get(j).getRcOrgId());
                        relationResponse.setCreatedAt(businessRelation.get(j).getCreatedAt());

                        relationResponseList.add(relationResponse);
                    }
                }
            }

            tableRelationMappingMaster.deleteRelationMapping(String.valueOf(pmId));
            tableRelationMappingMaster.addRelationMapping(relationResponseList);
        }
    }

    private void getExistingRelationData() {

        existingRelationList = tableRelationMappingMaster.getExistingRelation(pmId);

        if (existingRelationList.size() > 0) {

            textNoRelation.setVisibility(View.GONE);
            recycleViewRelation.setVisibility(View.VISIBLE);

            ArrayList<IndividualRelationType> individualRelationTypes = existingRelationList.get(0)
                    .getIndividualRelationTypeList();

            listAdapter = new IndividualRelationRecommendationListAdapter(activity, individualRelationTypes, "rcp");
            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
            recycleViewRelation.setAdapter(listAdapter);

        } else {

            textNoRelation.setVisibility(View.VISIBLE);
            recycleViewRelation.setVisibility(View.GONE);
        }
    }

    private void getUserExistingRelation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RELATION, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                            WsConstants.REQ_GET_RELATION + "?startAt=0&user_id=" + pmId);
        } else {
            Utils.showErrorSnackBar(this, relativeRootExistingRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }
}
