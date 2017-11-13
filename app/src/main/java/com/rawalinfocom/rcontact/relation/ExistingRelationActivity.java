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

        getAllExistingRelation();
//        getExistingRelationData();

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

    private void makeTempDataAndSetAdapter() {

        ArrayList<RelationRecommendationType> existingRelationList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            RelationRecommendationType relationRecommendationType = new RelationRecommendationType();

            ArrayList<IndividualRelationType> arrayList = new ArrayList<>();

            IndividualRelationType individualRelationTypeList;

            if (i == 0) {

                relationRecommendationType.setFirstName("Aniruddh");
                relationRecommendationType.setLastName("Pal");
                relationRecommendationType.setNumber("+91 886638723");
                relationRecommendationType.setDateAndTime("02 Oct, 17");

                // All
                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("1");
                individualRelationTypeList.setRelationName("Co-worker");
                individualRelationTypeList.setOrganizationName("Hungama");
                individualRelationTypeList.setFamilyName("");
                individualRelationTypeList.setIsFriendRelation(false);

                arrayList.add(individualRelationTypeList);

                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("2");
                individualRelationTypeList.setRelationName("Co-worker");
                individualRelationTypeList.setOrganizationName("RawalInfocom");
                individualRelationTypeList.setFamilyName("");
                individualRelationTypeList.setIsFriendRelation(false);

                arrayList.add(individualRelationTypeList);

                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("3");
                individualRelationTypeList.setRelationName("");
                individualRelationTypeList.setOrganizationName("");
                individualRelationTypeList.setFamilyName("Brother");
                individualRelationTypeList.setIsFriendRelation(true);

                arrayList.add(individualRelationTypeList);
            }

            if (i == 1) {

                relationRecommendationType.setFirstName("Darshan");
                relationRecommendationType.setLastName("Gajera");
                relationRecommendationType.setNumber("+91 9712978901");
                relationRecommendationType.setDateAndTime("05 Oct, 17");

                // Business and Family
                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("4");
                individualRelationTypeList.setRelationName("Co-worker");
                individualRelationTypeList.setOrganizationName("RawalInfocom");
                individualRelationTypeList.setFamilyName("");
                individualRelationTypeList.setIsFriendRelation(false);

                arrayList.add(individualRelationTypeList);

                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("5");
                individualRelationTypeList.setRelationName("");
                individualRelationTypeList.setOrganizationName("");
                individualRelationTypeList.setFamilyName("Brother");
                individualRelationTypeList.setIsFriendRelation(false);

                arrayList.add(individualRelationTypeList);
            }

            if (i == 2) {

                relationRecommendationType.setFirstName("Manish");
                relationRecommendationType.setLastName("Bhikadiya");
                relationRecommendationType.setNumber("+91 9123457859");
                relationRecommendationType.setDateAndTime("07 Oct, 17");

                // Family and Friend
                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("6");
                individualRelationTypeList.setRelationName("");
                individualRelationTypeList.setOrganizationName("");
                individualRelationTypeList.setFamilyName("Uncle");
                individualRelationTypeList.setIsFriendRelation(true);

                arrayList.add(individualRelationTypeList);
            }

            if (i == 3) {

                relationRecommendationType.setFirstName("Viraj");
                relationRecommendationType.setLastName("Kakadiya");
                relationRecommendationType.setNumber("+91 9879879870");
                relationRecommendationType.setDateAndTime("07 Oct, 17");

                // Friend
                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("7");
                individualRelationTypeList.setRelationName("");
                individualRelationTypeList.setOrganizationName("");
                individualRelationTypeList.setFamilyName("");
                individualRelationTypeList.setIsFriendRelation(true);

                arrayList.add(individualRelationTypeList);
            }

            if (i == 4) {

                relationRecommendationType.setFirstName("Ashish");
                relationRecommendationType.setLastName("Dungrani");
                relationRecommendationType.setNumber("+91 9876549871");
                relationRecommendationType.setDateAndTime("07 Oct, 17");

                // Family
                individualRelationTypeList = new IndividualRelationType();
                individualRelationTypeList.setRelationId("8");
                individualRelationTypeList.setRelationName("");
                individualRelationTypeList.setOrganizationName("");
                individualRelationTypeList.setFamilyName("Brother");
                individualRelationTypeList.setIsFriendRelation(false);

                arrayList.add(individualRelationTypeList);
            }

            relationRecommendationType.setIndividualRelationTypeList(arrayList);
            existingRelationList.add(relationRecommendationType);
        }

        if (existingRelationList.size() > 0) {
//            listAdapter = new ExistingRelationListAdapter(activity, existingRelationList);
            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
            recycleViewRelation.setAdapter(listAdapter);
        }
    }

    private void getAllExistingRelation() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncGetWebServiceCall(this, WsResponseObject.class, WsConstants
                    .REQ_GET_RELATION, getResources().getString(R.string.msg_please_wait))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT +
                            WsConstants.REQ_GET_RELATION + "?startAt=0");
        } else {
            Utils.showErrorSnackBar(this, relativeRootExistingRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }
}
