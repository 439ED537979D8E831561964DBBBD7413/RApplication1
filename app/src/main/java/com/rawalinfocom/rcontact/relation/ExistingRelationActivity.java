package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncGetWebServiceCall;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ExistingRelationRequest;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationResponse;
import com.rawalinfocom.rcontact.model.RelationUserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Hardik on 03/10/17.
 */

public class ExistingRelationActivity extends BaseActivity implements WsResponseListener, View.OnClickListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_add_new)
    ImageView imageAddNew;
    @BindView(R.id.recycle_view_relation)
    RecyclerView recycleViewRelation;
    @BindView(R.id.text_no_relation)
    TextView textNoRelation;
    @BindView(R.id.relative_root_existing_relation)
    LinearLayout relativeRootExistingRelation;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.img_filter)
    ImageView imgFilter;
    @BindView(R.id.img_clear)
    ImageView imgClear;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Activity activity;
    private ExistingRelationListAdapter listAdapter;
    //    private TableRelationMappingMaster tableRelationMappingMaster;
    //    private Integer pmId;
    private ArrayList<RelationRecommendationType> existingRelationList;
    private ArrayList<String> relationIds;
    private String deletePmId = "";
    private int deleteRelationPosition = -1;

    // For relation
    // Business - 0
    // Family - 1
    // Friend - 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_relation);

        activity = ExistingRelationActivity.this;

        ButterKnife.bind(this);
        removeNotification();
        initToolbar();

        textNoRelation.setTypeface(Utils.typefaceRegular(this));
        inputSearch.setTypeface(Utils.typefaceRegular(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    private void initToolbar() {
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(getResources().getString(R.string.relation_toolbar_title));
        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        imageActionBack.setOnClickListener(this);
        imageAddNew = ButterKnife.findById(includeToolbar, R.id.image_add_new);
        imageAddNew.setOnClickListener(this);
    }

    private void init() {

        textNoRelation.setVisibility(View.GONE);
        recycleViewRelation.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        if (Utils.isNetworkAvailable(this)) {
            getAllExistingRelation();
        } else {
            setVisibility(getString(R.string.msg_no_network), View.VISIBLE, View.GONE);
        }

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (listAdapter != null) {
                    listAdapter.getFilter().filter(charSequence.toString());
                }
                if (charSequence.toString().trim().length() == 0) {
                    imgClear.setVisibility(View.GONE);
                } else {
                    imgClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(activity, inputSearch);
                imgClear.setVisibility(View.GONE);
                inputSearch.getText().clear();

                if (listAdapter != null)
                    listAdapter.getFilter().filter("");
            }
        });

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                if (Utils.isNetworkAvailable(ExistingRelationActivity.this)) {
                    getAllExistingRelation();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    setVisibility(getString(R.string.msg_no_network), View.VISIBLE, View.GONE);
                }
            }
        });
    }

    private void setVisibility(String text, int textVisibility, int viewVisibility) {

        textNoRelation.setVisibility(textVisibility);
        textNoRelation.setText(text);
        recycleViewRelation.setVisibility(viewVisibility);
        swipeRefreshLayout.setVisibility(viewVisibility);
    }


    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_GET_RELATION">
            if (serviceType.contains(WsConstants.REQ_GET_RELATION)) {

                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                WsResponseObject sendRelationRequestObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (sendRelationRequestObject != null && StringUtils.equalsIgnoreCase
                        (sendRelationRequestObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<ExistingRelationRequest> allExistingRelationList = sendRelationRequestObject.
                            getAllExistingRelationList();

                    setExistingRelationData(allExistingRelationList);

                } else {
                    if (sendRelationRequestObject != null) {
                        Log.e("error response", sendRelationRequestObject.getMessage());
//                        Utils.showErrorSnackBar(this, relativeRootExistingRelation,
//                                sendRelationRequestObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "sendRelationRequestResponse null");
                        Utils.showErrorSnackBar(this, relativeRootExistingRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_DELETE_RELATION">
            if (serviceType.contains(WsConstants.REQ_DELETE_RELATION)) {
                WsResponseObject deleteRelationObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (deleteRelationObject != null && StringUtils.equalsIgnoreCase
                        (deleteRelationObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(activity, relativeRootExistingRelation,
                            deleteRelationObject.getMessage());

                    getAllExistingRelation();

                } else {
                    if (deleteRelationObject != null) {
                        Log.e("error response", deleteRelationObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootExistingRelation,
                                deleteRelationObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "deleteRelationResponse null");
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
            case R.id.image_add_new:

                if (Utils.isNetworkAvailable(activity)) {
                    Intent intent = new Intent(activity, AddNewRelationActivity.class);
                    intent.putExtra(AppConstants.EXTRA_IS_FROM, "own");
                    startActivity(intent);
                } else {
                    Utils.showErrorSnackBar(activity, relativeRootExistingRelation, getResources()
                            .getString(R.string.msg_no_network));
                }

                break;
        }
    }

//    private void storeProfileDataToDb(ArrayList<ExistingRelationRequest> relationRequestResponse) {
//
//        //<editor-fold desc="Relation Mapping Master">
//        TableRelationMappingMaster tableRelationMappingMaster = new
//                TableRelationMappingMaster(databaseHandler);
//
//        if (!Utils.isArraylistNullOrEmpty(relationRequestResponse)) {
//
//            for (int i = 0; i < relationRequestResponse.size(); i++) {
//
//                ArrayList<RelationRequestResponse> relationResponseList = new ArrayList<>();
//                ExistingRelationRequest relationRequest = relationRequestResponse.get(i);
//
//                //<editor-fold desc="Family Relation">
//                ArrayList<RelationResponse> familyRelation = relationRequest.getFamilyRelationList();
//                if (!Utils.isArraylistNullOrEmpty(familyRelation)) {
//
//                    for (int j = 0; j < familyRelation.size(); j++) {
//
//                        RelationRequestResponse relationResponse = new RelationRequestResponse();
//
//                        relationResponse.setId(familyRelation.get(j).getId());
//                        relationResponse.setRcRelationMasterId(familyRelation.get(j).getRcRelationMasterId());
//                        relationResponse.setRrmToPmId(familyRelation.get(j).getRrmToPmId());
//                        relationResponse.setRrmType(familyRelation.get(j).getRrmType());
//                        relationResponse.setRrmFromPmId(familyRelation.get(j).getRrmFromPmId());
//                        relationResponse.setRcStatus(familyRelation.get(j).getRcStatus());
//                        relationResponse.setRcOrgId(familyRelation.get(j).getRcOrgId());
//                        relationResponse.setCreatedAt(familyRelation.get(j).getCreatedAt());
//
//                        relationResponseList.add(relationResponse);
//                    }
//                }
//                //</editor-fold>
//
//                //<editor-fold desc="Friend Relation">
//                ArrayList<RelationResponse> friendRelation = relationRequest.getFriendRelationList();
//                if (!Utils.isArraylistNullOrEmpty(friendRelation)) {
//
//                    for (int j = 0; j < friendRelation.size(); j++) {
//
//                        RelationRequestResponse relationResponse = new RelationRequestResponse();
//
//                        relationResponse.setId(friendRelation.get(j).getId());
//                        relationResponse.setRcRelationMasterId(friendRelation.get(j).getRcRelationMasterId());
//                        relationResponse.setRrmToPmId(friendRelation.get(j).getRrmToPmId());
//                        relationResponse.setRrmType(friendRelation.get(j).getRrmType());
//                        relationResponse.setRrmFromPmId(friendRelation.get(j).getRrmFromPmId());
//                        relationResponse.setRcStatus(friendRelation.get(j).getRcStatus());
//                        relationResponse.setRcOrgId(friendRelation.get(j).getRcOrgId());
//                        relationResponse.setCreatedAt(friendRelation.get(j).getCreatedAt());
//
//                        relationResponseList.add(relationResponse);
//                    }
//                }
//                //</editor-fold>
//
//                //<editor-fold desc="Business Relation">
//                ArrayList<RelationResponse> businessRelation = relationRequest.getBusinessRelationList();
//                if (!Utils.isArraylistNullOrEmpty(businessRelation)) {
//
//                    for (int j = 0; j < businessRelation.size(); j++) {
//
//                        RelationRequestResponse relationResponse = new RelationRequestResponse();
//
//                        relationResponse.setId(businessRelation.get(j).getId());
//                        relationResponse.setRcRelationMasterId(businessRelation.get(j).getRcRelationMasterId());
//                        relationResponse.setRrmToPmId(businessRelation.get(j).getRrmToPmId());
//                        relationResponse.setRrmType(businessRelation.get(j).getRrmType());
//                        relationResponse.setRrmFromPmId(businessRelation.get(j).getRrmFromPmId());
//                        relationResponse.setRcStatus(businessRelation.get(j).getRcStatus());
//                        relationResponse.setRcOrgId(businessRelation.get(j).getRcOrgId());
//                        relationResponse.setCreatedAt(businessRelation.get(j).getCreatedAt());
//
//                        relationResponseList.add(relationResponse);
//                    }
//                }
//
//                tableRelationMappingMaster.deleteRelationMapping(String.valueOf(relationRequest.getRrmToPmId()));
//                tableRelationMappingMaster.addRelationMapping(relationResponseList);
//            }
//        }
//    }

//    private void getExistingRelationData() {
//
//        existingRelationList = new ArrayList<>();
//
////        existingRelationList = tableRelationMappingMaster
////                .getAllExistingRelation();
//
//        if (existingRelationList.size() > 0) {
//            listAdapter = new ExistingRelationListAdapter(activity, existingRelationList,
//                    new ExistingRelationListAdapter.OnClickListener() {
//                        @Override
//                        public void onClick(int position) {
//
//                            Intent intent = new Intent(activity, AddNewRelationActivity.class);
//                            intent.putExtra(AppConstants.EXTRA_EXISTING_RELATION_DETAILS,
//                                    existingRelationList.get(position));
//                            intent.putExtra(AppConstants.EXTRA_PM_ID,
//                                    existingRelationList.get(position).getPmId());
//                            intent.putExtra(AppConstants.EXTRA_IS_FROM, "existing");
//                            startActivity(intent);
//
//                        }
//
//                        @Override
//                        public void onDeleteClick(int position, String name, String pmId) {
//                            deletePmId = pmId;
//                            deleteRelationPosition = position;
//                            showAllRelations(position, name);
//                        }
//                    });
//            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
//            recycleViewRelation.setAdapter(listAdapter);
//
//        } else {
//
//            textNoRelation.setVisibility(View.VISIBLE);
//            recycleViewRelation.setVisibility(View.GONE);
//        }
//    }

    private void setExistingRelationData(ArrayList<ExistingRelationRequest> allExistingRelationList) {

        existingRelationList = new ArrayList<>();

        for (int i = 0; i < allExistingRelationList.size(); i++) {

            ExistingRelationRequest existingRelationRequest = allExistingRelationList.get(i);

            RelationUserProfile relationUserProfile = existingRelationRequest.getRelationUserProfile();

            RelationRecommendationType recommendationType = new RelationRecommendationType();
            recommendationType.setFirstName(relationUserProfile.getPmFirstName());
            recommendationType.setLastName(relationUserProfile.getPmLastName());
            recommendationType.setNumber("+" + relationUserProfile.getMobileNumber());
            recommendationType.setPmId(String.valueOf(allExistingRelationList.get(i).getRrmToPmId()));
            recommendationType.setDateAndTime("");
            recommendationType.setProfileImage(relationUserProfile.getProfilePhoto());
            recommendationType.setGender(relationUserProfile.getPbGender());

            ArrayList<IndividualRelationType> relationRecommendations = new ArrayList<>();

            // businessRelation
            ArrayList<RelationResponse> businessRecommendation = existingRelationRequest
                    .getBusinessRelationList();

            if (!Utils.isArraylistNullOrEmpty(businessRecommendation)) {

                for (int j = 0; j < businessRecommendation.size(); j++) {

                    IndividualRelationType individualRelationType = new IndividualRelationType();

                    individualRelationType.setId(String.valueOf(businessRecommendation.get(j).
                            getId()));
                    individualRelationType.setRelationId(String.valueOf(businessRecommendation.get(j).
                            getRcRelationMasterId()));
                    individualRelationType.setRelationName(businessRecommendation.get(j).getRmParticular());
                    individualRelationType.setOrganizationName(businessRecommendation.get(j).getOrgName());
                    individualRelationType.setIsOrgVerified(businessRecommendation.get(j).getOmIsVerified());
                    individualRelationType.setFamilyName("");
                    individualRelationType.setOrganizationId(String.valueOf(businessRecommendation.get(j).getRcOrgId()));
                    individualRelationType.setIsFriendRelation(false);
//                    individualRelationType.setIsVerify("1");
                    individualRelationType.setRelationType(businessRecommendation.get(j).getRrmType());
                    individualRelationType.setRcStatus(businessRecommendation.get(j).getRcStatus());
                    individualRelationType.setIsSelected(false);

                    relationRecommendations.add(individualRelationType);
                }
            }

            // familyRelation
            ArrayList<RelationResponse> familyRecommendation = existingRelationRequest
                    .getFamilyRelationList();

            if (!Utils.isArraylistNullOrEmpty(familyRecommendation)) {

                for (int j = 0; j < familyRecommendation.size(); j++) {

                    IndividualRelationType individualRelationType = new IndividualRelationType();

                    individualRelationType.setId(String.valueOf(familyRecommendation.get(j).
                            getId()));
                    individualRelationType.setRelationId(String.valueOf(familyRecommendation.get(j).
                            getRcRelationMasterId()));
                    individualRelationType.setRelationName("");
                    individualRelationType.setOrganizationName("");
                    individualRelationType.setFamilyName(familyRecommendation.get(j).getRmParticular());
                    individualRelationType.setOrganizationId("");
                    individualRelationType.setIsFriendRelation(false);
//                    individualRelationType.setIsVerify("1");
                    individualRelationType.setRelationType(familyRecommendation.get(j).getRrmType());
                    individualRelationType.setRcStatus(familyRecommendation.get(j).getRcStatus());
                    individualRelationType.setIsSelected(false);

                    relationRecommendations.add(individualRelationType);
                }
            }

            // friendRelation
            ArrayList<RelationResponse> friendRecommendation = existingRelationRequest
                    .getFriendRelationList();

            if (!Utils.isArraylistNullOrEmpty(friendRecommendation)) {

                for (int j = 0; j < friendRecommendation.size(); j++) {

                    IndividualRelationType individualRelationType = new IndividualRelationType();

                    individualRelationType.setId(String.valueOf(friendRecommendation.get(j).
                            getId()));
                    individualRelationType.setRelationId(String.valueOf(friendRecommendation.get(j).
                            getRcRelationMasterId()));
                    individualRelationType.setRelationName("");
                    individualRelationType.setOrganizationName("");
                    individualRelationType.setFamilyName("");
                    individualRelationType.setOrganizationId("");
                    individualRelationType.setIsFriendRelation(true);
//                    individualRelationType.setIsVerify("1");
                    individualRelationType.setRelationType(friendRecommendation.get(j).getRrmType());
                    individualRelationType.setRcStatus(friendRecommendation.get(j).getRcStatus());
                    individualRelationType.setIsSelected(false);

                    relationRecommendations.add(individualRelationType);
                }
            }

            recommendationType.setIndividualRelationTypeList(relationRecommendations);
            existingRelationList.add(recommendationType);
        }

        if (existingRelationList.size() > 0) {

            listAdapter = new ExistingRelationListAdapter(activity, existingRelationList,
                    new ExistingRelationListAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position) {

                            if (Utils.isNetworkAvailable(activity)) {
                                Intent intent = new Intent(activity, AddNewRelationActivity.class);
                                intent.putExtra(AppConstants.EXTRA_EXISTING_RELATION_DETAILS,
                                        existingRelationList.get(position));
                                intent.putExtra(AppConstants.EXTRA_CONTACT_NAME,
                                        existingRelationList.get(position).getFirstName() + " " + existingRelationList.get(position).getLastName());
                                intent.putExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL, existingRelationList.get(position).getProfileImage());
                                intent.putExtra(AppConstants.EXTRA_CONTACT_NUMBER, existingRelationList.get(position).getNumber());
                                intent.putExtra(AppConstants.EXTRA_PM_ID, existingRelationList.get(position).getPmId());
                                intent.putExtra(AppConstants.EXTRA_GENDER, existingRelationList.get(position).getGender());
                                intent.putExtra(AppConstants.EXTRA_IS_FROM, "existing");
                                startActivity(intent);
                            } else {
                                Utils.showErrorSnackBar(activity, relativeRootExistingRelation, getResources()
                                        .getString(R.string.msg_no_network));
                            }
                        }

                        @Override
                        public void onDeleteClick(int position, String name, String pmId) {
                            deletePmId = pmId;
                            deleteRelationPosition = position;
                            showAllRelations(position, name);
                        }
                    });
            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
            recycleViewRelation.setAdapter(listAdapter);

        } else {
            setVisibility(getString(R.string.str_no_relation_found), View.VISIBLE, View.GONE);
        }
    }

    private void showAllRelations(final int position, final String name) {

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
        textDialogTitle.setText(String.format("Relation with %s", name));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = dialog.findViewById(R.id.button_right);
        Button buttonLeft = dialog.findViewById(R.id.button_left);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_delete);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_back);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                relationIds = new ArrayList<>();

                ArrayList<IndividualRelationType> individualRelationTypes =
                        existingRelationList.get(position).getIndividualRelationTypeList();

                for (int i = 0; i < individualRelationTypes.size(); i++) {
                    if (individualRelationTypes.get(i).getIsSelected())
                        relationIds.add(individualRelationTypes.get(i).getId());
                }

                if (relationIds.size() > 0) {
                    dialog.dismiss();
                    dialogDeleteRelation(name, relationIds);
                } else {
                    Utils.showErrorSnackBar(ExistingRelationActivity.this, relativeRootExistingRelation,
                            "Please select at least one relation to delete!!!");
                }
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationRelationListAdapter adapter = new OrganizationRelationListAdapter(this,
                existingRelationList.get(position).getIndividualRelationTypeList(),
                new OrganizationRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String orgName, boolean isOrgVerified) {

                    }
                }, "existing");

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void dialogDeleteRelation(String RcpUserName, final ArrayList<String> relationIds) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_privacy_policy);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        textDialogTitle.setText(getString(R.string.delete_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        TextView textDeleteHint = dialog.findViewById(R.id.txtDeleteHint);
        textDeleteHint.setVisibility(View.VISIBLE);
        textDeleteHint.setTypeface(Utils.typefaceRegular(this));
        textDeleteHint.setText(String.format("Are you sure you want to delete relationship with %s ?",
                RcpUserName));

        Button buttonRight = dialog.findViewById(R.id.ok_button);
        Button buttonLeft = dialog.findViewById(R.id.cancel_button);
        LinearLayout linear_call_dialog_list = dialog.findViewById(R.id.linear_call_dialog_list);
        linear_call_dialog_list.setVisibility(View.GONE);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_ok);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_cancel);

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePmId = "";
                deleteRelationPosition = -1;
                dialog.dismiss();
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteRelation(relationIds);
            }
        });

        dialog.show();
    }

    private void getAllExistingRelation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RELATION, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_GET_RELATION + "?startAt=0");
        } else {
            Utils.showErrorSnackBar(this, relativeRootExistingRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void deleteRelation(ArrayList<String> relationIds) {

        if (relationIds.size() > 0) {
            WsRequestObject deleteRelationObject = new WsRequestObject();
            deleteRelationObject.setRelationIds(relationIds);

            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), deleteRelationObject,
                        null, WsResponseObject.class, WsConstants.REQ_DELETE_RELATION, getString(R.string
                        .msg_please_wait), true)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                                WsConstants.REQ_DELETE_RELATION);
            } else {
                Utils.showErrorSnackBar(this, relativeRootExistingRelation, getResources()
                        .getString(R.string.msg_no_network));
            }
        } else {
            Utils.showErrorSnackBar(this, relativeRootExistingRelation, "Please select " +
                    "any relation to delete");
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}
