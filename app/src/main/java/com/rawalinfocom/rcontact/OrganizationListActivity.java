package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.adapters.EnterpriseOrganizationsAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.OrganizationData;
import com.rawalinfocom.rcontact.model.OrganizationListData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 25/09/17.
 */

public class OrganizationListActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    @BindView(R.id.search_box)
    EditText searchBox;
    @BindView(R.id.organization_list)
    RecyclerView organizationList;
    @BindView(R.id.relative_root_organization)
    RelativeLayout relativeRootOrganization;
    @BindView(R.id.imgDone)
    ImageView imgDone;

    private Activity activity;
    private ArrayList<OrganizationData> arrayListOrganization;
    private EnterpriseOrganizationsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organization_list);
        ButterKnife.bind(this);

        activity = OrganizationListActivity.this;

        init();
    }

    private void init() {

        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(R.string.org_list);

        arrayListOrganization = new ArrayList<>();

        dummyOrganizationList();
//        getOrganizationList();

        setOrganizationListData();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchBox.getText().toString().trim().length() > 0) {
                    Toast.makeText(activity, searchBox.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            //<editor-fold desc="Back">
            case R.id.ripple_action_back:
                onBackPressed();
                break;
            //</editor-fold>
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

                    ArrayList<OrganizationListData> arrayListOrganizationData =
                            profileDetail.getPbOrganizationList();
                    if (!Utils.isArraylistNullOrEmpty(arrayListOrganizationData)) {
                        for (int i = 0; i < arrayListOrganizationData.size(); i++) {
                            OrganizationData organizationData = new OrganizationData();
                            organizationData.setOmId(arrayListOrganizationData.get(i).getOrgId());
                            organizationData.setOmRecordIndexId(arrayListOrganizationData.get(i).getOrgId());
                            organizationData.setOmOrganizationCompany(arrayListOrganizationData.get(i)
                                    .getOrgName());
                            organizationData.setOmOrganizationDesignation(arrayListOrganizationData.get(i)
                                    .getOrgJobTitle());
                            organizationData.setOmOrganizationProfileImage(arrayListOrganizationData.get(i)
                                    .getOrgProfileImage());
                            arrayListOrganization.add(organizationData);
                        }

                        setOrganizationListData();
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootOrganization, "" + error.getLocalizedMessage());
        }
    }

    private void getOrganizationList() {

        WsRequestObject organizationObject = new WsRequestObject();
        organizationObject.setPmId(Integer.parseInt(getUserPmId()));

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    organizationObject, null, WsResponseObject.class, WsConstants.REQ_PROFILE_RATING,
                    null, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants
                    .WS_ROOT + WsConstants.REQ_PROFILE_RATING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootOrganization, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void dummyOrganizationList() {

        for (int i = 0; i < 30; i++) {
            OrganizationData organizationData = new OrganizationData();
            organizationData.setOmId(String.valueOf(i * 12));
            organizationData.setOmRecordIndexId(String.valueOf(i * 12));
            organizationData.setOmOrganizationCompany("Rawal Infocom " + (i * 12));
            organizationData.setOmOrganizationDesignation("Android Developer " + (i));
            organizationData.setOmOrganizationProfileImage("https://media.licdn.com/mpr/mpr/shrink_200_200/" +
                    "AAEAAQAAAAAAAAgxAAAAJDZmZjk4OGEyLTIwMGItNDAwNS05MTEwLTJmMDM3YTBmNjVjMw.png");
            arrayListOrganization.add(organizationData);
        }
    }

    private void setOrganizationListData() {

        adapter = new EnterpriseOrganizationsAdapter(activity, arrayListOrganization,

                new EnterpriseOrganizationsAdapter.OnClickListener() {
                    @Override
                    public void onClick(String organizationName) {
                        Toast.makeText(activity, organizationName, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        organizationList.setAdapter(adapter);
    }
}
