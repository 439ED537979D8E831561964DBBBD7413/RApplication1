package com.rawalinfocom.rcontact.relation;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.IndividualRelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relation_recommendation);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_search:
                break;
        }
    }

    private void init() {
        initToolBar();
        makeTempDataAndSetAdapter();
    }

    private void initToolBar() {
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionSearch.setOnRippleCompleteListener(this);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(R.string.toolbar_title);
    }

    private void makeTempDataAndSetAdapter() {

        ArrayList<RelationRecommendationType> recommendationRelationList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            RelationRecommendationType relationRecommendationType = new RelationRecommendationType();

            ArrayList<IndividualRelationRecommendationType> arrayList = new ArrayList<>();

            relationRecommendationType.setFirstName("Aniruddh");
            relationRecommendationType.setLastName("Pal");
            relationRecommendationType.setNumber("+91 886638723");

            IndividualRelationRecommendationType individualRelationRecommendationType;

            if (i == 0) {

                // All
                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("Co-worker");
                individualRelationRecommendationType.setOrganizationName("Hungama");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);

                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("Co-worker");
                individualRelationRecommendationType.setOrganizationName("RawalInfocom");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);

                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("Brother");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);

                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(true);

                arrayList.add(individualRelationRecommendationType);
            }

            if (i == 1) {

                // Business and Family
                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("Co-worker");
                individualRelationRecommendationType.setOrganizationName("RawalInfocom");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);

                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("Brother");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);
            }

            if (i == 2) {

                // Family and Friend
                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("Uncle");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);

                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(true);

                arrayList.add(individualRelationRecommendationType);
            }

            if (i == 3) {

                // Friend
                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("");
                individualRelationRecommendationType.setIsFriendRelation(true);

                arrayList.add(individualRelationRecommendationType);
            }

            if (i == 4) {

                // Family
                individualRelationRecommendationType = new IndividualRelationRecommendationType();
                individualRelationRecommendationType.setRelationName("");
                individualRelationRecommendationType.setOrganizationName("");
                individualRelationRecommendationType.setFamilyName("Father");
                individualRelationRecommendationType.setIsFriendRelation(false);

                arrayList.add(individualRelationRecommendationType);
            }

            relationRecommendationType.setIndividualRelationRecommendationTypeArrayList(arrayList);
            recommendationRelationList.add(relationRecommendationType);
        }

        if (recommendationRelationList.size() > 0) {
            RelationRecommendationListAdapter listAdapter = new RelationRecommendationListAdapter(this, recommendationRelationList);
            recycleViewRelation.setLayoutManager(new LinearLayoutManager(this));
            recycleViewRelation.setAdapter(listAdapter);
        }
    }
}
