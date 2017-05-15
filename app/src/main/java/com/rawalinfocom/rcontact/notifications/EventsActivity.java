package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.EventAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.EventComment;
import com.rawalinfocom.rcontact.model.EventItem;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

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
    @BindView(R.id.view_more)
    TextView viewmore;


    private EventAdapter todayEventAdapter;
    private EventAdapter recentEventAdapter;
    private EventAdapter upcomingEventAdapter;
    public static String evmRecordId = "";
    public static int selectedRecycler = -1;
    public static int selectedRecyclerItem = -1;
    TableCommentMaster tableCommentMaster;
    List<EventItem> listTodayEvent;
    List<EventItem> listRecentEvent;
    List<EventItem> listUpcomingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        init();
        initData();
    }

    private void init() {
        textToolbarTitle.setText(getResources().getString(R.string.nav_text_events));
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
        searchViewEvents.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    todayEventAdapter.updateList(listTodayEvent);
                    recentEventAdapter.updateList(listRecentEvent);
                    upcomingEventAdapter.updateList(listUpcomingEvent);
                    updateHeight();
                }
                return false;
            }
        });
        recyclerViewRecent.setVisibility(View.GONE);
        recyclerViewUpcoming.setVisibility(View.GONE);

    }

    void filter(String text) {

        List<EventItem> temp = new ArrayList<>();
        for (EventItem item : listTodayEvent) {
            if (item.getPersonName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(item);
            }
        }
        todayEventAdapter.updateList(temp);

        temp = new ArrayList<>();
        for (EventItem item : listRecentEvent) {
            if (item.getPersonName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(item);
            }
        }
        recentEventAdapter.updateList(temp);

        temp = new ArrayList<>();
        for (EventItem item : listUpcomingEvent) {
            if (item.getPersonName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(item);
            }
        }
        upcomingEventAdapter.updateList(temp);
        updateHeight();
    }

    private void initData() {

        tableCommentMaster = new TableCommentMaster(databaseHandler);
        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        String today = getEventDate(0);
        String yesterDay = getEventDate(-1);
        String tomorrow = getEventDate(1);
        String day7th = getEventDate(7);
        String currentUserPmId = Utils.getStringPreference(this, AppConstants.PREF_USER_PM_ID, "0");
        int currentPmID = Integer.parseInt(currentUserPmId);

        ArrayList<Event> eventsToday = tableEventMaster.getAllEventsBetWeenExceptCurrentUser(today, today, currentPmID);
        ArrayList<Event> eventsRecent = tableEventMaster.getAllEventsBetWeenExceptCurrentUser(yesterDay, yesterDay, currentPmID);
        ArrayList<Event> eventsUpcoming7 = tableEventMaster.getAllEventsBetWeenExceptCurrentUser(tomorrow, day7th, currentPmID);

        listTodayEvent = createEventList(eventsToday, 0);
        listRecentEvent = createEventList(eventsRecent, 1);
        listUpcomingEvent = createEventList(eventsUpcoming7, 2);

        todayEventAdapter = new EventAdapter(this, listTodayEvent, 0);
        recentEventAdapter = new EventAdapter(this, listRecentEvent, 1);
        upcomingEventAdapter = new EventAdapter(this, listUpcomingEvent, 2);

        recyclerViewToday.setAdapter(todayEventAdapter);
        recyclerViewRecent.setAdapter(recentEventAdapter);
        recyclerViewUpcoming.setAdapter(upcomingEventAdapter);

        updateHeight();

    }

    private void updateHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 50;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 30;
                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 35;
                break;
            case DisplayMetrics.DENSITY_HIGH: /*240*/
                heightPercent = 40;
                break;
            case DisplayMetrics.DENSITY_XHIGH: /*320*/
                heightPercent = 45;
                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 50;
                break;
        }
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * heightPercent) / 100;

        setRecyclerViewHeight(recyclerViewToday, height);
        setRecyclerViewHeight(recyclerViewRecent, height);
        setRecyclerViewHeight(recyclerViewUpcoming, height);
    }

    private void setRecyclerViewHeight(RecyclerView recyclerView, int height) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int heightRecycler = recyclerView.getMeasuredHeight();
        if (heightRecycler > height) {
            recyclerView.getLayoutParams().height = height;
        } else {
            recyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private List<EventItem> createEventList(ArrayList<Event> events, int listPosition) {
        EventItem item;
        int currentYear;
        int eventYear;
        Date date = new Date();
        currentYear = date.getYear();
        List<EventItem> list = new ArrayList<>();
        for (Event e : events) {
            item = new EventItem();
            String eventName = e.getEvmEventType();
            int eventType = -1;
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
            if (e.getEvmIsPrivate() != IntegerConstants.IS_PRIVATE && e.getRcProfileMasterPmId() != null && e.getRcProfileMasterPmId().length() > 0) {
                int pmId = Integer
                        .parseInt(e.getRcProfileMasterPmId());

                UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);

                eventType = getEventType(eventName);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = sdf.parse(e.getEvmStartDate());
                    eventYear = date.getYear();
                } catch (ParseException e1) {
                    eventYear = 0;
                    e1.printStackTrace();
                }

                item.setPersonName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
                item.setPersonFirstName(userProfile.getPmFirstName());
                item.setPersonLastName(userProfile.getPmLastName());
                item.setEventName(eventName);
                item.setEventType(getEventType(eventName));
                if (e.getEvmIsYearHidden() == 1) {
                    item.setEventDetail("");
                } else {
                    item.setEventDetail(setEventDetailText(currentYear - eventYear, eventType));
                }
                item.setEventDate(e.getEvmStartDate());
                item.setEventRecordIndexId(e.getEvmRecordIndexId());
                item.setPersonRcpPmId(pmId);
                Comment comment = tableCommentMaster.getComment(e.getEvmRecordIndexId());
                if (comment != null) {
                    item.setUserComment(comment.getCrmComment());
                    item.setCommentTime(comment.getCrmUpdatedAt());
                    item.setEventCommentPending(false);
                    if (listPosition != 1) {
                        list.add(item);
                    }
                } else {
                    item.setEventCommentPending(true);
                    list.add(item);
                }
            }


        }
        return list;
    }

    private int getEventType(String eventName) {
        int eventType = 0;
        if (getResources().getString(R.string.text_birthday).equalsIgnoreCase(eventName))
            eventType = AppConstants.COMMENT_TYPE_BIRTHDAY;
        if (getResources().getString(R.string.text_anniversary).equalsIgnoreCase(eventName))
            eventType = AppConstants.COMMENT_TYPE_ANNIVERSARY;

        return eventType;
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
        if (eventType == AppConstants.COMMENT_TYPE_BIRTHDAY) {
            if (eventYears == 1) {
                s = eventYears + " " + getResources().getString(R.string.text_year_old);
            } else {
                s = eventYears + " " + getResources().getString(R.string.text_years_old);
            }
        }
        if (eventType == AppConstants.COMMENT_TYPE_ANNIVERSARY) {
            s = Utils.addDateSufixes(eventYears) + " " + getResources().getString(R.string.text_anniversary);
        }
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

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_ADD_EVENT_COMMENT)) {
                WsResponseObject wsResponseObject = (WsResponseObject) data;
                EventComment eventComment = wsResponseObject.getEventComment();

                Comment comment = new Comment();
                comment.setCrmStatus(Integer.parseInt(eventComment.getStatus()));
                comment.setCrmRating("");
                comment.setCrmType(eventComment.getType());
                comment.setCrmCloudPrId(eventComment.getId());
                comment.setRcProfileMasterPmId(eventComment.getToPmId());
                comment.setCrmComment(eventComment.getComment());
                comment.setCrmReply(eventComment.getReply());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getCreatedDate()));
                comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                comment.setEvmRecordIndexId(evmRecordId);

                if (evmRecordId != null) {
                    tableCommentMaster.addComment(comment);
                    evmRecordId = "";
                    switch (selectedRecycler) {
                        case 0:
                            listTodayEvent.get(selectedRecyclerItem).setUserComment(eventComment.getComment());
                            listTodayEvent.get(selectedRecyclerItem).setCommentTime(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                            listTodayEvent.get(selectedRecyclerItem).setEventCommentPending(false);
                            todayEventAdapter.notifyDataSetChanged();
                            break;
                        case 1:
                            listRecentEvent.get(selectedRecyclerItem).setUserComment(eventComment.getComment());
                            listRecentEvent.get(selectedRecyclerItem).setCommentTime(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                            listRecentEvent.get(selectedRecyclerItem).setEventCommentPending(false);
                            recentEventAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            listUpcomingEvent.get(selectedRecyclerItem).setUserComment(eventComment.getComment());
                            listUpcomingEvent.get(selectedRecyclerItem).setCommentTime(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                            listUpcomingEvent.get(selectedRecyclerItem).setEventCommentPending(false);
                            upcomingEventAdapter.notifyDataSetChanged();
                            break;
                    }
                    Utils.hideProgressDialog();
                }
            }
        } else {
            Toast.makeText(EventsActivity.this, getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }

    }


}
