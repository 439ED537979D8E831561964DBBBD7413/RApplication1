package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.CountryListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableCountryMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Country;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryListActivity extends AppCompatActivity implements WsResponseListener,
        RippleView.OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.toolbar_country_search)
    Toolbar toolbarCountrySearch;
    @BindView(R.id.recycler_view_country_list)
    RecyclerView recyclerViewCountryList;
    @BindView(R.id.relative_root_country_list)
    RelativeLayout relativeRootCountryList;
    @BindView(R.id.text_empty_country)
    TextView textEmptyCountry;

    CountryListAdapter adapterCountryList;
    ArrayList<Country> arrayListCountry;

    DatabaseHandler databaseHandler;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_list);
        ButterKnife.bind(this);

        init();
        getCountryList();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_COUNTRY_CODE_DETAIL">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_COUNTRY_CODE_DETAIL)) {
                WsResponseObject countryListResponse = (WsResponseObject) data;
                if (countryListResponse.getStatus().equalsIgnoreCase(WsConstants
                        .RESPONSE_STATUS_TRUE)) {
//                    AppUtils.hideProgressDialog();

                    arrayListCountry = new ArrayList<>();
                    arrayListCountry.addAll(countryListResponse.getArrayListCountry());

                    if (!Utils.isArraylistNullOrEmpty(arrayListCountry)) {

                        textEmptyCountry.setVisibility(View.GONE);
                        recyclerViewCountryList.setVisibility(View.VISIBLE);

                        recyclerViewCountryList.setLayoutManager(new LinearLayoutManager(this));
                        adapterCountryList = new CountryListAdapter(this, arrayListCountry);
                        recyclerViewCountryList.setAdapter(adapterCountryList);

                        TableCountryMaster tableCountryMaster = new TableCountryMaster
                                (CountryListActivity.this, databaseHandler);
                        for (int i = 0; i < arrayListCountry.size(); i++) {
                            tableCountryMaster.addCountry(arrayListCountry.get(i));
                        }

                    } else {

                        textEmptyCountry.setVisibility(View.VISIBLE);
                        recyclerViewCountryList.setVisibility(View.GONE);

                    }

                } else {
                    Log.e("error response", countryListResponse.getMessage());
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootCountryList, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        databaseHandler = new DatabaseHandler(CountryListActivity.this);

        rippleActionBack.setOnRippleCompleteListener(this);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterCountryList.filter(s.toString());
                    recyclerViewCountryList.setAdapter(adapterCountryList);
                    if (adapterCountryList.getItemCount() < 1) {
                        textEmptyCountry.setVisibility(View.VISIBLE);
                        recyclerViewCountryList.setVisibility(View.GONE);
                    } else {
                        textEmptyCountry.setVisibility(View.GONE);
                        recyclerViewCountryList.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.showErrorSnackBar(CountryListActivity.this,
                            relativeRootCountryList, "No Country Found");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void getCountryList() {

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), null, null,
                    WsResponseObject.class, WsConstants.REQ_COUNTRY_CODE_DETAIL, getString(R
                    .string.msg_please_wait)).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_COUNTRY_CODE_DETAIL);
        } else {
            Utils.showErrorSnackBar(this, relativeRootCountryList, getResources()
                    .getString(R.string.msg_no_network));
        }

    }

    //</editor-fold>
}
