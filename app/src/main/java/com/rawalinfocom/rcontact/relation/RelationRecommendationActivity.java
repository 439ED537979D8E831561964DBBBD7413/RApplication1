package com.rawalinfocom.rcontact.relation;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
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

public class RelationRecommendationActivity extends BaseActivity implements WsResponseListener, RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_search)
    ImageView imageSearch;
    @BindView(R.id.ripple_action_search)
    RippleView rippleActionSearch;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_no_relation)
    TextView textNoRelation;
    @BindView(R.id.recycle_view_relation)
    RecyclerView recycleViewRelation;

    @BindView(R.id.image_back)
    ImageView imageBack;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.imgClose)
    ImageView imgClose;
    @BindView(R.id.relative_back)
    LinearLayout relativeBack;
    @BindView(R.id.relative_root_recommendation_relation)
    RelativeLayout relativeRootRecommendationRelation;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private RelationRecommendationListAdapter listAdapter;
    private ArrayList<RelationRecommendationType> recommendationRelationList;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_recommendation);
        ButterKnife.bind(this);
        removeNotification();
        initToolBar();
        init();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_GET_RECOMMENDATION">
            if (serviceType.contains(WsConstants.REQ_GET_RECOMMENDATION)) {

                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);

                WsResponseObject sendRelationRequestObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (sendRelationRequestObject != null && StringUtils.equalsIgnoreCase
                        (sendRelationRequestObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<ExistingRelationRequest> allExistingRelationList =
                            sendRelationRequestObject.getRecommendationsRelationList();

                    if (allExistingRelationList.size() > 0) {
                        getRelationRecommendationData(allExistingRelationList);
                    } else {
                        setVisibility(getString(R.string.str_no_relation_recommendation_found), View.VISIBLE, View.GONE);
                    }

                } else {
                    if (sendRelationRequestObject != null) {
                        Log.e("error response", sendRelationRequestObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootRecommendationRelation,
                                sendRelationRequestObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "sendRelationRequestResponse null");
                        Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_RELATION_ACTION">
            if (serviceType.contains(WsConstants.REQ_RELATION_ACTION)) {
                WsResponseObject deleteRelationObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (deleteRelationObject != null && StringUtils.equalsIgnoreCase
                        (deleteRelationObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(RelationRecommendationActivity.this,
                            relativeRootRecommendationRelation, deleteRelationObject.getMessage());

                    getRelationRecommendation();

//                    deleteRelationPosition = -1;

                } else {
                    if (deleteRelationObject != null) {
                        Log.e("error response", deleteRelationObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootRecommendationRelation,
                                deleteRelationObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "DeleteAcceptRelationResponse null");
                        Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_search:

                Utils.showKeyBoard(RelationRecommendationActivity.this);

                inputSearch.requestFocus();

                relativeBack.setVisibility(View.VISIBLE);
                relativeActionBack.setVisibility(View.GONE);

                break;
        }
    }

    private void initToolBar() {

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionSearch.setOnRippleCompleteListener(this);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(R.string.toolbar_title);

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(RelationRecommendationActivity.this, inputSearch);
                inputSearch.getText().clear();
                relativeBack.setVisibility(View.GONE);
                relativeActionBack.setVisibility(View.VISIBLE);
                if (listAdapter != null)
                    listAdapter.getFilter().filter("");
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (listAdapter != null)
                    listAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideSoftKeyboard(RelationRecommendationActivity.this, inputSearch);
                inputSearch.getText().clear();
                relativeBack.setVisibility(View.GONE);
                relativeActionBack.setVisibility(View.VISIBLE);
                if (listAdapter != null)
                    listAdapter.getFilter().filter("");
            }
        });
    }

    private void init() {

        recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));

        textNoRelation.setVisibility(View.GONE);
        recycleViewRelation.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        recommendationRelationList = new ArrayList<>();

        if (Utils.isNetworkAvailable(this)) {
            getRelationRecommendation();
        } else {
            setVisibility(getString(R.string.msg_no_network), View.VISIBLE, View.GONE);
        }

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeRefreshLayout.setRefreshing(true);
                if (Utils.isNetworkAvailable(RelationRecommendationActivity.this)) {
                    getRelationRecommendation();
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

    private void getRelationRecommendationData(ArrayList<ExistingRelationRequest> allExistingRelationList) {

        recommendationRelationList = new ArrayList<>();

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

            // businessRecommendation
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

            // familyRecommendation
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

            // friendRecommendation
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
            recommendationRelationList.add(recommendationType);
        }

        if (recommendationRelationList.size() > 0) {
            listAdapter = new RelationRecommendationListAdapter(this, recommendationRelationList,
                    new RelationRecommendationListAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position, String name, String pmId) {
                            type = "accept";
                            showAllRelations(position, name);
                        }

                        @Override
                        public void onDeleteClick(int position, String name, String pmId) {
                            type = "reject";
                            showAllRelations(position, name);
//                            dialogActionRelation(position, name);
                        }
                    });
            recycleViewRelation.setAdapter(listAdapter);
        } else {
            setVisibility(getString(R.string.str_no_relation_recommendation_found), View.VISIBLE, View.GONE);
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
        if (type.equalsIgnoreCase("reject"))
            buttonRight.setText(R.string.action_delete);
        else
            buttonRight.setText(R.string.action_accept);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_back);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                ArrayList<String> relationIds = new ArrayList<>();

                ArrayList<IndividualRelationType> individualRelationTypes =
                        recommendationRelationList.get(position).getIndividualRelationTypeList();

                for (int i = 0; i < individualRelationTypes.size(); i++) {
                    if (individualRelationTypes.get(i).getIsSelected())
                        relationIds.add(individualRelationTypes.get(i).getId());
                }

                if (relationIds.size() > 0) {
                    dialog.dismiss();

                    if (type.equalsIgnoreCase("reject"))
                        dialogActionRelation(name, relationIds);
                    else
                        acceptRelationRequest(relationIds);

                } else {
                    if (type.equalsIgnoreCase("reject")) {
                        Utils.showErrorSnackBar(RelationRecommendationActivity.this, relativeRootRecommendationRelation,
                                "Please select at least one relation to reject!");
                    } else {
                        Utils.showErrorSnackBar(RelationRecommendationActivity.this, relativeRootRecommendationRelation,
                                "Please select at least one relation to accept!");
                    }
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
                recommendationRelationList.get(position).getIndividualRelationTypeList(),
                new OrganizationRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String orgName, boolean isOrgVerified) {

                    }
                }, "existing");

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void dialogActionRelation(String RcpUserName, final ArrayList<String> relationIds) {

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
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        TextView textDeleteHint = dialog.findViewById(R.id.txtDeleteHint);
        textDeleteHint.setVisibility(View.VISIBLE);
        textDeleteHint.setTypeface(Utils.typefaceRegular(this));

//        if (type.equalsIgnoreCase("reject")) {
        textDialogTitle.setText(getString(R.string.delete_relation));
        textDeleteHint.setText(String.format("Are you sure you want to delete relationship with %s ?",
                RcpUserName));
//        } else {
//            textDialogTitle.setText(getString(R.string.accept_relation));
//            textDeleteHint.setText(String.format("Are you sure you want to accept relationship with %s ?",
//                    RcpUserName));
//        }

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
                dialog.dismiss();
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

//                if (type.equalsIgnoreCase("reject"))
                rejectRelationRequest(relationIds);
//                else
//                    acceptRelationRequest(relationIds);
            }
        });

        dialog.show();
    }

    private void getRelationRecommendation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RECOMMENDATION, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_GET_RECOMMENDATION + "?startAt=0");
        } else {
            Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void acceptRelationRequest(ArrayList<String> relationIds) {

        if (relationIds.size() > 0) {
            WsRequestObject deleteRelationObject = new WsRequestObject();
            deleteRelationObject.setRelationIds(relationIds);

            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), deleteRelationObject,
                        null, WsResponseObject.class, WsConstants.REQ_RELATION_ACTION, getString(R.string
                        .msg_please_wait), true)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                                WsConstants.REQ_RELATION_ACTION + "/accept");
            } else {
                Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, getResources()
                        .getString(R.string.msg_no_network));
            }
        } else {
            Utils.showErrorSnackBar(this, relativeRootRecommendationRelation,
                    "Please select at least one relation to accept!!!");
        }
    }

    private void rejectRelationRequest(ArrayList<String> relationIds) {

        if (relationIds.size() > 0) {
            WsRequestObject deleteRelationObject = new WsRequestObject();
            deleteRelationObject.setRelationIds(relationIds);

            if (Utils.isNetworkAvailable(this)) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), deleteRelationObject,
                        null, WsResponseObject.class, WsConstants.REQ_RELATION_ACTION, getString(R.string
                        .msg_please_wait), true)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                                WsConstants.REQ_RELATION_ACTION + "/reject");
            } else {
                Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, getResources()
                        .getString(R.string.msg_no_network));
            }
        } else {
            Utils.showErrorSnackBar(this, relativeRootRecommendationRelation,
                    "Please select at least one relation to delete!!!");
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) RelationRecommendationActivity.this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}
