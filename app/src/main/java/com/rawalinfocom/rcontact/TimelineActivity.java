package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.events.MyLayoutManager;
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
    RecyclerView recyclerViewToday;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.search_view_timeline)
    SearchView searchViewTimeline;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerViewYesterday;
    @BindView(R.id.recyclerview3)
    RecyclerView recyclerViewPast5day;
    @BindView(R.id.viewmore)
    TextView viewmore;
    @BindView(R.id.h1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.h2)
    RelativeLayout headerYesterdayLayout;
    @BindView(R.id.h3)
    RelativeLayout headerPast5daysLayout;
    @BindView(R.id.header1)
    RelativeLayout header1;
    @BindView(R.id.header2)
    RelativeLayout header2;
    @BindView(R.id.header3)
    RelativeLayout header3;
    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;
    @BindView(R.id.header2_icon)
    ImageView headerYesterdayIcon;
    @BindView(R.id.header3_icon)
    ImageView headerPast5DaysIcon;
    @BindView(R.id.text_header1)
    TextView headerTodayTitle;
    @BindView(R.id.text_header2)
    TextView headerYesterdayTitle;
    @BindView(R.id.text_header3)
    TextView headerPast5DaysTitle;

    private TimelineSectionAdapter todayTimelineAdapter;
    private TimelineSectionAdapter yesterdayTimelineAdapter;
    private TimelineSectionAdapter past5daysTimelineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        init();
        initData();
    }

    private void init() {
        headerTodayTitle.setTypeface(Utils.typefaceRegular(this));
        headerYesterdayTitle.setTypeface(Utils.typefaceRegular(this));
        headerPast5DaysTitle.setTypeface(Utils.typefaceRegular(this));

        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText("Timeline");
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        headerTodayIcon.setImageResource(R.drawable.ic_collapse);

        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recyclerViewToday.getVisibility() == View.VISIBLE) {
                    recyclerViewToday.setVisibility(View.GONE);
                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewToday.setVisibility(View.VISIBLE);
                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
                }

                recyclerViewYesterday.setVisibility(View.GONE);
                headerYesterdayIcon.setImageResource(R.drawable.ic_expand);

                recyclerViewPast5day.setVisibility(View.GONE);
                headerPast5DaysIcon.setImageResource(R.drawable.ic_expand);
            }
        });

        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewToday.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerViewYesterday.getVisibility() == View.VISIBLE) {
                    recyclerViewYesterday.setVisibility(View.GONE);
                    headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewYesterday.setVisibility(View.VISIBLE);
                    headerYesterdayIcon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerViewPast5day.setVisibility(View.GONE);
                headerPast5DaysIcon.setImageResource(R.drawable.ic_expand);

            }
        });

        headerPast5daysLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewToday.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                recyclerViewYesterday.setVisibility(View.GONE);
                headerYesterdayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerViewPast5day.getVisibility() == View.VISIBLE) {
                    recyclerViewPast5day.setVisibility(View.GONE);
                    headerPast5DaysIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewPast5day.setVisibility(View.VISIBLE);
                    headerPast5DaysIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
        recyclerViewYesterday.setVisibility(View.GONE);
        recyclerViewPast5day.setVisibility(View.GONE);
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


        final List<TimelineItem> listTimelineToday = Arrays.asList(item1, item2, item3, item4, item5, item6, item7, item1, item2);
        final List<TimelineItem> listTimelineYesterday = Arrays.asList(item1, item2, item3);
        final List<TimelineItem> listTimelinePast5days = Arrays.asList(item1, item2, item3, item4, item5, item6, item7, item1, item2);

        todayTimelineAdapter = new TimelineSectionAdapter(getApplicationContext(), listTimelineToday);
        yesterdayTimelineAdapter = new TimelineSectionAdapter(getApplicationContext(), listTimelineYesterday);
        past5daysTimelineAdapter = new TimelineSectionAdapter(getApplicationContext(), listTimelinePast5days);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * 57) / 100;

        recyclerViewToday.setAdapter(todayTimelineAdapter);
        recyclerViewToday.setLayoutManager(new MyLayoutManager(this, recyclerViewToday, height));
        RecyclerView.Adapter adapter = recyclerViewToday.getAdapter();
        int itemCount = adapter.getItemCount();
        if (itemCount > 2) {
            recyclerViewToday.getLayoutParams().height = height;
        }

        recyclerViewYesterday.setAdapter(yesterdayTimelineAdapter);
        recyclerViewYesterday.setLayoutManager(new MyLayoutManager(this, recyclerViewYesterday, height));
        adapter = recyclerViewYesterday.getAdapter();
        itemCount = adapter.getItemCount();
        if (itemCount > 2) {
            recyclerViewYesterday.getLayoutParams().height = height;
        }

        recyclerViewPast5day.setAdapter(past5daysTimelineAdapter);
        recyclerViewPast5day.setLayoutManager(new MyLayoutManager(this, recyclerViewPast5day, height));
        adapter = recyclerViewPast5day.getAdapter();
        itemCount = adapter.getItemCount();
        if (itemCount > 2) {
            recyclerViewPast5day.getLayoutParams().height = height;
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
