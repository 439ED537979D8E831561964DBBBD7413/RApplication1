package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

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

    private Activity activity;

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

        activity = ExistingRelationActivity.this;
        textNoRelation.setTypeface(Utils.typefaceRegular(this));
        inputSearch.setTypeface(Utils.typefaceRegular(this));

        textNoRelation.setVisibility(View.GONE);
        recycleViewRelation.setVisibility(View.VISIBLE);

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
                startActivity(new Intent(ExistingRelationActivity.this, AddNewRelationActivity.class));
                break;
        }
    }

    private void makeTempDataAndSetAdapter() {

        ArrayList<RelationRecommendationType> existingRelationList = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            RelationRecommendationType relationRecommendationType = new RelationRecommendationType();

            ArrayList<IndividualRelationRecommendationType> arrayList = new ArrayList<>();

            IndividualRelationRecommendationType individualRelationRecommendationType =
                    new IndividualRelationRecommendationType();

            if (i == 0 || i == 4 || i == 8) {

                relationRecommendationType.setFirstName("Aniruddh");
                relationRecommendationType.setLastName("Pal");
                relationRecommendationType.setNumber("+91 886638723");

                individualRelationRecommendationType.setRelationName("Co-worker");
                individualRelationRecommendationType.setOrganizationName("Hungama");
                individualRelationRecommendationType.setFamilyName("Grandfather");
                individualRelationRecommendationType.setIsFriendRelation(true);
            } else if (i == 1 || i == 5 || i == 9) {

                relationRecommendationType.setFirstName("Mayur");
                relationRecommendationType.setLastName("Patel");
                relationRecommendationType.setNumber("+91 9876453210");

                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(true);

            } else if (i == 2 || i == 6 | i == 10) {

                relationRecommendationType.setFirstName("Darshan");
                relationRecommendationType.setLastName("Gajera");
                relationRecommendationType.setNumber("+91 9712378901");

                individualRelationRecommendationType.setRelationName("Co-worker");
                individualRelationRecommendationType.setOrganizationName("RawalInfocom");
                individualRelationRecommendationType.setFamilyName("Brother");
                individualRelationRecommendationType.setIsFriendRelation(false);

            } else {

                relationRecommendationType.setFirstName("Aniruddh");
                relationRecommendationType.setLastName("Pal");
                relationRecommendationType.setNumber("+91 886638723");

                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("Uncle");
                individualRelationRecommendationType.setIsFriendRelation(true);
            }

            arrayList.add(individualRelationRecommendationType);

            relationRecommendationType.setIndividualRelationRecommendationTypeArrayList(arrayList);
            existingRelationList.add(relationRecommendationType);
        }

        if (existingRelationList.size() > 0) {
            ExistingRelationListAdapter listAdapter = new ExistingRelationListAdapter(this, existingRelationList);
            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
            recycleViewRelation.setAdapter(listAdapter);
        }
    }
}
