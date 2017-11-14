package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.rawalinfocom.rcontact.database.TableRelationMappingMaster;
import com.rawalinfocom.rcontact.database.TableRelationMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRequest;
import com.rawalinfocom.rcontact.model.RelationRequestResponse;
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

    private Activity activity;
    private ExistingRelationListAdapter listAdapter;
    private TableRelationMappingMaster tableRelationMappingMaster;
    private Integer pmId;

    // For relation
    // Business - 0
    // Family - 1
    // Friend - 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_relation);

        ButterKnife.bind(this);
        initToolbar();

        tableRelationMappingMaster = new TableRelationMappingMaster(databaseHandler);

        activity = ExistingRelationActivity.this;
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

        if (Utils.getBooleanPreference(ExistingRelationActivity.this,
                AppConstants.PREF_GET_RELATION, true)) {
            getAllExistingRelation();
        } else {
            getExistingRelationData();
        }

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listAdapter.getFilter().filter(charSequence.toString());

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
                imgClear.setVisibility(View.GONE);
                listAdapter.getFilter().filter("");
                inputSearch.getText().clear();
            }
        });
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

                    Utils.setBooleanPreference(ExistingRelationActivity.this,
                            AppConstants.PREF_GET_RELATION, false);

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
            case R.id.image_add_new:
                Intent intent = new Intent(activity, AddNewRelationActivity.class);
                intent.putExtra(AppConstants.EXTRA_IS_FROM, "own");
                startActivity(intent);
                break;
        }
    }

    private void storeProfileDataToDb(ArrayList<RelationRequest> relationRequestResponse) {

        //<editor-fold desc="Relation Mapping Master">
        TableRelationMappingMaster tableRelationMappingMaster = new
                TableRelationMappingMaster(databaseHandler);

        if (!Utils.isArraylistNullOrEmpty(relationRequestResponse)) {

            ArrayList<RelationRequestResponse> relationResponseList = new ArrayList<>();

            for (int i = 0; i < relationRequestResponse.size(); i++) {

                RelationRequest relationRequest = relationRequestResponse.get(i);
                pmId = relationRequest.getRrmToPmId();

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

        final ArrayList<RelationRecommendationType> existingRelationList = tableRelationMappingMaster
                .getAllExistingRelation();

        if (existingRelationList.size() > 0) {
            listAdapter = new ExistingRelationListAdapter(activity, existingRelationList,
                    new ExistingRelationListAdapter.OnClickListener() {
                        @Override
                        public void onClick(int position) {

                            Intent intent = new Intent(activity, AddNewRelationActivity.class);
                            intent.putExtra(AppConstants.EXTRA_EXISTING_RELATION_DETAILS,
                                    existingRelationList.get(position));
                            intent.putExtra(AppConstants.EXTRA_PM_ID,
                                    existingRelationList.get(position).getPmId());
                            intent.putExtra(AppConstants.EXTRA_IS_FROM, "existing");
                            startActivity(intent);

                        }
                    });
            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
            recycleViewRelation.setAdapter(listAdapter);

        } else {

            textNoRelation.setVisibility(View.VISIBLE);
            recycleViewRelation.setVisibility(View.GONE);
        }
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
}
