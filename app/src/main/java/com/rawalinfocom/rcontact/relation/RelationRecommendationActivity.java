package com.rawalinfocom.rcontact.relation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRequest;
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
    @BindView(R.id.no_record_to_display)
    TextView noRecordToDisplay;
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

    private RelationRecommendationListAdapter listAdapter;
    private ArrayList<RelationRecommendationType> recommendationRelationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_recommendation);
        ButterKnife.bind(this);
        initToolBar();
        init();
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

//                    Utils.showSuccessSnackBar(activity, relativeRootRecommendationRelation,
//                            "New Relation Added Successfully!!!");
//                    storeProfileDataToDb(allExistingRelationList);
//                    getExistingRelationData();

                    Utils.setBooleanPreference(RelationRecommendationActivity.this,
                            AppConstants.PREF_GET_RELATION, false);

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

            //<editor-fold desc="REQ_DELETE_RELATION">
            if (serviceType.contains(WsConstants.REQ_DELETE_RELATION)) {
                WsResponseObject deleteRelationObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (deleteRelationObject != null && StringUtils.equalsIgnoreCase
                        (deleteRelationObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(RelationRecommendationActivity.this,
                            relativeRootRecommendationRelation, deleteRelationObject.getMessage());

//                    tableRelationMappingMaster.deleteRelationMapping(deletePmId);
//                    deletePmId = "";

//                    existingRelationList.remove(deleteRelationPosition);
//                    listAdapter.notifyDataSetChanged();

//                    deleteRelationPosition = -1;

                } else {
                    if (deleteRelationObject != null) {
                        Log.e("error response", deleteRelationObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootRecommendationRelation,
                                deleteRelationObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "deleteRelationResponse null");
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
                relativeBack.setVisibility(View.GONE);
                relativeActionBack.setVisibility(View.VISIBLE);
                listAdapter.getFilter().filter("");
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter.getFilter().filter("");
            }
        });
    }

    private void init() {

        recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));

        recommendationRelationList = new ArrayList<>();

        if (Utils.isNetworkAvailable(this)) {
            getRelationRecommendation();
        } else {
            getRelationRecommendationData();
        }
    }

    private void getRelationRecommendationData() {

    }

    private void makeTempDataAndSetAdapter() {

        for (int i = 0; i < 5; i++) {
            RelationRecommendationType relationRecommendationType = new RelationRecommendationType();

            ArrayList<IndividualRelationType> arrayList = new ArrayList<>();

            IndividualRelationType individualRelationType;

            if (i == 0) {

                relationRecommendationType.setFirstName("Aniruddh");
                relationRecommendationType.setLastName("Pal");
                relationRecommendationType.setNumber("+91 886638723");
                relationRecommendationType.setDateAndTime("02 Oct, 17");

                // All
                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("Co-worker");
                individualRelationType.setOrganizationName("Hungama");
                individualRelationType.setFamilyName("");
                individualRelationType.setIsFriendRelation(false);

                arrayList.add(individualRelationType);

                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("Co-worker");
                individualRelationType.setOrganizationName("RawalInfocom");
                individualRelationType.setFamilyName("");
                individualRelationType.setIsFriendRelation(false);

                arrayList.add(individualRelationType);

                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("");
                individualRelationType.setOrganizationName("");
                individualRelationType.setFamilyName("Brother");
                individualRelationType.setIsFriendRelation(true);

                arrayList.add(individualRelationType);

//                individualRelationType = new IndividualRelationType();
//                individualRelationType.setRelationName("");
//                individualRelationType.setOrganizationName("");
//                individualRelationType.setFamilyName("");
//                individualRelationType.setIsFriendRelation(true);
//
//                arrayList.add(individualRelationType);
            }

            if (i == 1) {

                relationRecommendationType.setFirstName("Darshan");
                relationRecommendationType.setLastName("Gajera");
                relationRecommendationType.setNumber("+91 9712978901");
                relationRecommendationType.setDateAndTime("05 Oct, 17");

                // Business and Family
                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("Co-worker");
                individualRelationType.setOrganizationName("RawalInfocom");
                individualRelationType.setFamilyName("");
                individualRelationType.setIsFriendRelation(false);

                arrayList.add(individualRelationType);

                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("");
                individualRelationType.setOrganizationName("");
                individualRelationType.setFamilyName("Brother");
                individualRelationType.setIsFriendRelation(false);

                arrayList.add(individualRelationType);
            }

            if (i == 2) {

                relationRecommendationType.setFirstName("Manish");
                relationRecommendationType.setLastName("Bhikadiya");
                relationRecommendationType.setNumber("+91 9123457859");
                relationRecommendationType.setDateAndTime("07 Oct, 17");

                // Family and Friend
                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("");
                individualRelationType.setOrganizationName("");
                individualRelationType.setFamilyName("Uncle");
                individualRelationType.setIsFriendRelation(true);

                arrayList.add(individualRelationType);
//                individualRelationType = new IndividualRelationType();
//                individualRelationType.setRelationName("");
//                individualRelationType.setOrganizationName("");
//                individualRelationType.setFamilyName("");
//                individualRelationType.setIsFriendRelation(true);
//
//                arrayList.add(individualRelationType);
            }

            if (i == 3) {

                relationRecommendationType.setFirstName("Viraj");
                relationRecommendationType.setLastName("Kakadiya");
                relationRecommendationType.setNumber("+91 9879879870");
                relationRecommendationType.setDateAndTime("07 Oct, 17");

                // Friend
                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("");
                individualRelationType.setOrganizationName("");
                individualRelationType.setFamilyName("");
                individualRelationType.setIsFriendRelation(true);

                arrayList.add(individualRelationType);
            }

            if (i == 4) {

                relationRecommendationType.setFirstName("Ashish");
                relationRecommendationType.setLastName("Dungrani");
                relationRecommendationType.setNumber("+91 9876549871");
                relationRecommendationType.setDateAndTime("07 Oct, 17");

                // Family
                individualRelationType = new IndividualRelationType();
                individualRelationType.setRelationName("");
                individualRelationType.setOrganizationName("");
                individualRelationType.setFamilyName("Father");
                individualRelationType.setIsFriendRelation(false);

                arrayList.add(individualRelationType);
            }

            relationRecommendationType.setIndividualRelationTypeList(arrayList);
            recommendationRelationList.add(relationRecommendationType);
        }

        if (recommendationRelationList.size() > 0) {
            listAdapter = new RelationRecommendationListAdapter(this, recommendationRelationList);
            recycleViewRelation.setAdapter(listAdapter);
        }
    }

    private void getRelationRecommendation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RELATION, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, BuildConfig.WS_ROOT +
                            WsConstants.REQ_GET_RELATION + "?startAt=0&status=1");
        } else {
            Utils.showErrorSnackBar(this, relativeRootRecommendationRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }
}
