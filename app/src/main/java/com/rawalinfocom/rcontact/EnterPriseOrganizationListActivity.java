package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.content.Intent;
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

import com.rawalinfocom.rcontact.adapters.EnterPriseOrganizationsAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.VerifiedOrganizationData;
import com.rawalinfocom.rcontact.model.VerifiedOrganizationDetails;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 25/09/17.
 */

public class EnterPriseOrganizationListActivity extends BaseActivity implements RippleView
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
    @BindView(R.id.txt_no_org_list)
    TextView txtNoOrgList;

    private Activity activity;
    private ArrayList<VerifiedOrganizationData> verifyArrayListOrganization;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organization_list);
        ButterKnife.bind(this);

        activity = EnterPriseOrganizationListActivity.this;

        init();
    }

    private void init() {

        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(R.string.str_org);

        txtNoOrgList.setVisibility(View.VISIBLE);
        txtNoOrgList.setText(getString(R.string.enter_or_select_your_organisation));
        txtNoOrgList.setTypeface(Utils.typefaceRegular(activity));
        organizationList.setVisibility(View.GONE);
        imgDone.setVisibility(View.GONE);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() > 0) {
                    imgDone.setVisibility(View.VISIBLE);
                    getOrganizationList(charSequence.toString());
                } else {

                    clearData();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchBox.getText().toString().trim().length() > 0) {

                    Intent intent = new Intent();
                    intent.putExtra("orgId", "");
                    intent.putExtra("organizationName", searchBox.getText().toString().trim());
                    intent.putExtra("organizationType", "");
                    intent.putExtra("logo", "");
                    intent.putExtra("isBack", "0");
                    setResult(Activity.RESULT_OK, intent);
                    finish();//finishing activity
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

            verifyArrayListOrganization = new ArrayList<>();
//            Utils.hideProgressDialog();

            // <editor-fold desc="REQ_GET_PROFILE_DETAILS">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_ORGANIZATIONS)) {
                WsResponseObject getProfileResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (getProfileResponse != null && StringUtils.equalsIgnoreCase(getProfileResponse
                        .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<VerifiedOrganizationData> organizationData =
                            getProfileResponse.getVerifiedOrganizationData();

                    txtNoOrgList.setVisibility(View.GONE);
                    organizationList.setVisibility(View.VISIBLE);

                    if (!Utils.isArraylistNullOrEmpty(organizationData)) {
                        for (int i = 0; i < organizationData.size(); i++) {
                            VerifiedOrganizationData verifiedOrganizationData = new VerifiedOrganizationData();

                            verifiedOrganizationData.setOmOrgId(organizationData.get(i).getOmOrgId());
                            verifiedOrganizationData.setOmOrgName(organizationData.get(i).getOmOrgName());
                            verifiedOrganizationData.setOmOrgIsVerify(organizationData.get(i)
                                    .getOmOrgIsVerify());

                            VerifiedOrganizationDetails organizationDetails = organizationData.get(i).getOmOrgDetails();

                            verifiedOrganizationData.setEomLogoPath(organizationDetails.getEomLogoPath()

                                    + "/" + organizationDetails.getEomLogoName());

                            if (organizationDetails.getVerifiedIndustryType() != null) {
                                verifiedOrganizationData.setEitType(organizationDetails.getVerifiedIndustryType().getEitType());
//                                verifiedOrganizationData.setEitId(organizationDetails.getVerifiedIndustryType().getEitId());
                            }
                            verifyArrayListOrganization.add(verifiedOrganizationData);
                        }

                        if (searchBox.getText().toString().trim().length() > 0)
                            setOrganizationListData();
                        else {
                            clearData();
                        }

                    } else {

                        verifyArrayListOrganization.clear();
                        txtNoOrgList.setVisibility(View.VISIBLE);
                        txtNoOrgList.setText(getString(R.string.search_no_organisation));
                        organizationList.setVisibility(View.GONE);
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

    private void clearData() {
        if (verifyArrayListOrganization != null)
            verifyArrayListOrganization.clear();

        txtNoOrgList.setVisibility(View.VISIBLE);
        organizationList.setVisibility(View.GONE);
        imgDone.setVisibility(View.GONE);
    }

    private void getOrganizationList(String name) {

        WsRequestObject organizationObject = new WsRequestObject();
        organizationObject.setOmName(name);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    organizationObject, null, WsResponseObject.class, WsConstants.REQ_GET_ORGANIZATIONS,
                    /*activity.getResources().getString(R.string.msg_please_wait)*/null, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants
                    .WS_ROOT + WsConstants.REQ_GET_ORGANIZATIONS);
        } else {
            Utils.showErrorSnackBar(this, relativeRootOrganization, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void setOrganizationListData() {

        EnterPriseOrganizationsAdapter adapter = new EnterPriseOrganizationsAdapter(activity, verifyArrayListOrganization,

                new EnterPriseOrganizationsAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String organizationName, String organizationType, String logo) {

                        Intent intent = new Intent();
                        intent.putExtra("orgId", orgId);
                        intent.putExtra("organizationName", organizationName);
                        intent.putExtra("organizationType", organizationType);
                        intent.putExtra("logo", logo);
                        intent.putExtra("isBack", "0");
                        setResult(Activity.RESULT_OK, intent);
                        finish();//finishing activity
                    }
                });

        organizationList.setAdapter(adapter);
    }
}
