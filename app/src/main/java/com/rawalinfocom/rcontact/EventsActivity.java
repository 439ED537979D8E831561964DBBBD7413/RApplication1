package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.events.EventAdapter;
import com.rawalinfocom.rcontact.events.EventItem;
import com.rawalinfocom.rcontact.events.MyLayoutManager;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    @BindView(R.id.header1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.recyclerview1)
    RecyclerView recyclerViewToday;
    @BindView(R.id.text_header2)
    TextView textRecentTitle;
    @BindView(R.id.header2_icon)
    ImageView headerRecentIcon;
    @BindView(R.id.header2)
    RelativeLayout headerRecentLayout;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerViewRecent;
    @BindView(R.id.text_header3)
    TextView textUpcomingTitle;
    @BindView(R.id.header3_icon)
    ImageView headerUpcomingIcon;
    @BindView(R.id.header3)
    RelativeLayout headerUpcomingLayout;
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
        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        String today = getEventDate(0);
        String yesterDay = getEventDate(-1);
        String tomorrow = getEventDate(1);
        String day7th = getEventDate(7);

        ArrayList<Event> eventsToday = tableEventMaster.getAllEventsBetWeen(today, today);
        ArrayList<Event> eventsRecent = tableEventMaster.getAllEventsBetWeen(yesterDay, yesterDay);
        ArrayList<Event> eventsUpcoming7 = tableEventMaster.getAllEventsBetWeen(tomorrow, day7th);

        List<EventItem> listTodayEvent = createTodayList(eventsToday);
        List<EventItem> listRecentEvent = createTodayList(eventsRecent);
        List<EventItem> listUpcomingEvent = createTodayList(eventsUpcoming7);

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

    private List<EventItem> createTodayList(ArrayList<Event> eventsToday) {
        EventItem item;
        int currentYear;
        int eventYear;
        Date date = new Date();
        currentYear = date.getYear();
        List<EventItem> list = new ArrayList<>();
        for (Event e : eventsToday) {
            item = new EventItem();
            String eventName = e.getEvmEventType();
            int eventType = -1;
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                    .parseInt(e.getRcProfileMasterPmId()));

            if ("Birthday".equals(eventName)) eventType = 1;
            if ("Aniversary".equals(eventName)) eventType = 2;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(e.getEvmStartDate());
                eventYear = date.getYear();
            } catch (ParseException e1) {
                eventYear = 0;
                e1.printStackTrace();
                Log.i("MAULIK", "year can not be parsed");
            }
            item.setEventName(eventName);
            item.setEventDetail(setEventDetailText(currentYear - eventYear, eventType));
            item.setWisherName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            list.add(item);
        }
        return list;
    }

    private String getEventDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    private String setEventDetailText(int eventYears, int eventType) {
        String s = "";
        if (eventYears <= 0) return s;
        if (eventType == 1) s = eventYears + " Years Old";
        if (eventType == 2) s = eventYears + " Aniversary Years";
        return s;
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
