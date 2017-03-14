package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.events.EventAdapter;
import com.rawalinfocom.rcontact.events.EventItem;
import com.rawalinfocom.rcontact.events.MyLayoutManager;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.search_view_events)
    SearchView searchViewEvents;
    @BindView(R.id.text_header1)
    TextView textTodayTitle;
    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;
    @BindView(R.id.h1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.header1)
    RelativeLayout header1;
    @BindView(R.id.recyclerview1)
    RecyclerView recyclerViewToday;
    @BindView(R.id.text_header2)
    TextView textRecentTitle;
    @BindView(R.id.header2_icon)
    ImageView headerRecentIcon;
    @BindView(R.id.h2)
    RelativeLayout headerRecentLayout;
    @BindView(R.id.header2)
    RelativeLayout header2;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerViewRecent;
    @BindView(R.id.text_header3)
    TextView textUpcomingTitle;
    @BindView(R.id.header3_icon)
    ImageView headerUpcomingIcon;
    @BindView(R.id.h3)
    RelativeLayout headerUpcomingLayout;
    @BindView(R.id.header3)
    RelativeLayout header3;
    @BindView(R.id.recyclerview3)
    RecyclerView recyclerViewUpcoming;
    @BindView(R.id.viewmore)
    TextView viewmore;

    private EventAdapter todayEventAdapter;
    private EventAdapter recentEventAdapter;
    private EventAdapter upcomingEventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        init();
        initData();
    }

    private void init() {
        textToolbarTitle.setText("Events");
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textTodayTitle.setTypeface(Utils.typefaceRegular(this));
        textRecentTitle.setTypeface(Utils.typefaceRegular(this));
        textUpcomingTitle.setTypeface(Utils.typefaceRegular(this));

        rippleActionBack.setOnRippleCompleteListener(this);
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
                recyclerViewRecent.setVisibility(View.GONE);
                headerRecentIcon.setImageResource(R.drawable.ic_expand);
                recyclerViewUpcoming.setVisibility(View.GONE);
                headerUpcomingIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerRecentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewToday.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);
                recyclerViewUpcoming.setVisibility(View.GONE);
                headerUpcomingIcon.setImageResource(R.drawable.ic_expand);
                if (recyclerViewRecent.getVisibility() == View.VISIBLE) {
                    recyclerViewRecent.setVisibility(View.GONE);
                    headerRecentIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewRecent.setVisibility(View.VISIBLE);
                    headerRecentIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
        headerUpcomingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewToday.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);
                recyclerViewRecent.setVisibility(View.GONE);
                headerRecentIcon.setImageResource(R.drawable.ic_expand);
                if (recyclerViewUpcoming.getVisibility() == View.VISIBLE) {
                    recyclerViewUpcoming.setVisibility(View.GONE);
                    headerUpcomingIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewUpcoming.setVisibility(View.VISIBLE);
                    headerUpcomingIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
    }

    private void initData() {
        //EventItem(String wisherName, String eventName, String notiTime, String eventDetail, String userComment, int notiType)
        EventItem item1 = new EventItem("A Dhameliya", "Birthday", "12:00 PM", "20 Years Old", "Happy Birthday!", 0);
        EventItem item2 = new EventItem("B Dhameliya", "Anniversary", "11:16 PM", "1st Anniversary", "Congrats!", 0);
        EventItem item3 = new EventItem("C Dhameliya", "Birthday", "12:00 PM", "20 Years Old", "Happy Birthday!", 0);
        EventItem item4 = new EventItem("D Dhameliya", "Anniversary", "11:16 PM", "5th Anniversary ", "Congo bro", 0);
        EventItem item5 = new EventItem("E Dhameliya", "Birthday", "11:16 PM", "37 Years Old", "Happy Birthday dada", 0);
        EventItem item6 = new EventItem("F Dhameliya", "Anniversary", "11:16 PM", "5th Anniversary ", "Congo bro", 0);
        EventItem item7 = new EventItem("G Dhameliya", "Birthday", "11:16 PM", "67 Years Old", "", 0);
        List<EventItem> listTodayEvent = Arrays.asList(item7, item2, item7, item3, item7, item6, item5, item4);
        List<EventItem> listRecentEvent = Arrays.asList(item7, item5, item7, item7);
        List<EventItem> listUpcomingEvent = Arrays.asList(item7, item2, item7, item3, item7, item6, item5, item4);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * 57) / 100;

        todayEventAdapter = new EventAdapter(this, listTodayEvent);
        recyclerViewToday.setAdapter(todayEventAdapter);
        recyclerViewToday.setLayoutManager(new MyLayoutManager(getApplicationContext(), recyclerViewToday, height));
        RecyclerView.Adapter mAdapter = recyclerViewToday.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerViewToday.getLayoutParams().height = height;
        }

        recentEventAdapter = new EventAdapter(this, listRecentEvent);
        recyclerViewRecent.setAdapter(recentEventAdapter);
        recyclerViewRecent.setLayoutManager(new MyLayoutManager(getApplicationContext(), recyclerViewRecent, height));
        mAdapter = recyclerViewRecent.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerViewRecent.getLayoutParams().height = height;
        }

        upcomingEventAdapter = new EventAdapter(this, listUpcomingEvent);
        recyclerViewUpcoming.setAdapter(upcomingEventAdapter);
        recyclerViewUpcoming.setLayoutManager(new MyLayoutManager(getApplicationContext(), recyclerViewUpcoming, height));
        mAdapter = recyclerViewUpcoming.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerViewUpcoming.getLayoutParams().height = height;
        }
        recyclerViewRecent.setVisibility(View.GONE);
        recyclerViewUpcoming.setVisibility(View.GONE);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }
}
