package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.timeline.TimelineItem;
import com.rawalinfocom.rcontact.timeline.TimelineSectionAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.recyclerview1)
    RecyclerView recyclerview1;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.search_view_timeline)
    SearchView searchViewTimeline;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerview2;
    @BindView(R.id.recyclerview3)
    RecyclerView recyclerview3;
    @BindView(R.id.viewmore)
    TextView viewmore;
    @BindView(R.id.activity_timeline)
    RelativeLayout activityTimeline;
    @BindView(R.id.h1)
    RelativeLayout h1;
    @BindView(R.id.h2)
    RelativeLayout h2;
    @BindView(R.id.h3)
    RelativeLayout h3;
    @BindView(R.id.header1)
    RelativeLayout header1;
    @BindView(R.id.header2)
    RelativeLayout header2;
    @BindView(R.id.header3)
    RelativeLayout header3;
    @BindView(R.id.header1_icon)
    ImageView header1Icon;
    @BindView(R.id.header2_icon)
    ImageView header2Icon;
    @BindView(R.id.header3_icon)
    ImageView header3Icon;
    @BindView(R.id.text_header1)
    TextView textHeader1;
    @BindView(R.id.text_header2)
    TextView textHeader2;
    @BindView(R.id.text_header3)
    TextView textHeader3;

    private TimelineSectionAdapter sectionAdapter1;
    private TimelineSectionAdapter sectionAdapter2;
    private TimelineSectionAdapter sectionAdapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        init();
        initData();
    }

    private void init() {
        //searchViewTimeline.setIconified(false);
        textHeader1.setTypeface(Utils.typefaceRegular(this));
        textHeader2.setTypeface(Utils.typefaceRegular(this));
        textHeader3.setTypeface(Utils.typefaceRegular(this));
        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText("Timeline");
        header1Icon.setImageResource(R.drawable.ic_collapse);
        h3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerview1.setVisibility(View.GONE);
                recyclerview2.setVisibility(View.GONE);
                if (recyclerview3.getVisibility() == View.VISIBLE) {
                    recyclerview3.setVisibility(View.GONE);
                    header3Icon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerview3.setVisibility(View.VISIBLE);
                    header3Icon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
        h2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerview1.setVisibility(View.GONE);
                if (recyclerview2.getVisibility() == View.VISIBLE) {
                    recyclerview2.setVisibility(View.GONE);
                    header2Icon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerview2.setVisibility(View.VISIBLE);
                    header2Icon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerview3.setVisibility(View.GONE);

            }
        });
        h1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerview1.getVisibility() == View.VISIBLE) {
                    recyclerview1.setVisibility(View.GONE);
                    header1Icon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerview1.setVisibility(View.VISIBLE);
                    header1Icon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerview2.setVisibility(View.GONE);
                recyclerview3.setVisibility(View.GONE);
            }
        });
    }

    private void initData() {
        //TimelineItem(String wisherName, String eventName, String notiTime, String eventDetail,
        // String wisherComment, String wisherCommentTime, String userComment, String userCommentTime, int notiType)
        TimelineItem item1 = new TimelineItem("A Dhameliya", "Birthday", "12:00 PM", "20 Years Old", "Happy Birthday!", "11:00 PM", "", "", 0);
        TimelineItem item2 = new TimelineItem("B Dhameliya", "Anniversary", "11:16 PM", "1st Anniversary", "Congrats!", "11:00 PM", "Thanks", "11:18PM", 0);
        TimelineItem item3 = new TimelineItem("C Dhameliya", "Rating", "11:16 PM", "3", "Nice Profile dude", "11:00 PM", "Thanks", "11:18PM", 1);
        TimelineItem item4 = new TimelineItem("D Dhameliya", "Anniversary", "11:16 PM", "5th Anniversary ", "Congo bro", "11:00 PM", "", "", 0);
        TimelineItem item5 = new TimelineItem("E Dhameliya", "Birthday", "11:16 PM", "37 Years Old", "Happy Birthday dada", "11:16 PM", "Thank you very much", "11:16 PM", 0);
        TimelineItem item6 = new TimelineItem("F Dhameliya", "Rating", "11:16 PM", "5", "", "", "", "", 1);
        TimelineItem item7 = new TimelineItem("G Dhameliya", "Birthday", "11:16 PM", "67 Years Old", "Happy Birthday GD", "11:16 PM", "", "", 0);


        final List<TimelineItem> sections1 = Arrays.asList(item1, item2, item3, item4, item5, item6, item7, item1, item2);
        final List<TimelineItem> sections2 = Arrays.asList(item1, item2, item3);
        final List<TimelineItem> sections3 = Arrays.asList(item1, item2);
        sectionAdapter1 = new TimelineSectionAdapter(getApplicationContext(), sections2);
        sectionAdapter2 = new TimelineSectionAdapter(getApplicationContext(), sections3);
        sectionAdapter3 = new TimelineSectionAdapter(getApplicationContext(), sections1);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int a = (displaymetrics.heightPixels * 57) / 100;

        recyclerview1.setAdapter(sectionAdapter1);
        recyclerview1.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter mAdapter = recyclerview1.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 2) {
            recyclerview1.getLayoutParams().height = a;
        }

        recyclerview2.setAdapter(sectionAdapter2);
        recyclerview2.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter mAdapter2 = recyclerview2.getAdapter();
        int totalItemCount2 = mAdapter2.getItemCount();
        if (totalItemCount2 > 2) {
            recyclerview2.getLayoutParams().height = a;
        }

        recyclerview3.setAdapter(sectionAdapter3);
        recyclerview3.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.Adapter mAdapter3 = recyclerview3.getAdapter();
        int totalItemCount3 = mAdapter3.getItemCount();
        if (totalItemCount3 > 2) {
            recyclerview3.getLayoutParams().height = a;
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
