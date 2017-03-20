package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.events.EventAdapter;
import com.rawalinfocom.rcontact.events.EventItem;
import com.rawalinfocom.rcontact.events.MyLayoutManager;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.EventComment;
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
    @BindView(R.id.viewmore)
    TextView viewmore;


    private EventAdapter todayEventAdapter;
    private EventAdapter recentEventAdapter;
    private EventAdapter upcomingEventAdapter;
    public static String evmRecordId = "";
    public static int selectedRecycler = -1;
    public static int selectedRecyclerItem = -1;
    TableCommentMaster tableCommentMaster;
    String today;
    List<EventItem> listTodayEvent;
    List<EventItem> listRecentEvent;
    List<EventItem> listUpcomingEvent;

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
        tableCommentMaster = new TableCommentMaster(databaseHandler);
        String s = Utils.getLocalTimeFromUTCTime("2017-03-18 10:15:50");
        Log.i("Maulik", "Localtime" + s);
        TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);

        String today = getEventDate(0);
        String yesterDay = getEventDate(-1);
        String tomorrow = getEventDate(1);
        String day7th = getEventDate(7);

        ArrayList<Event> eventsToday = tableEventMaster.getAllEventsBetWeen(today, today);
        ArrayList<Event> eventsRecent = tableEventMaster.getAllEventsBetWeen(yesterDay, yesterDay);
        ArrayList<Event> eventsUpcoming7 = tableEventMaster.getAllEventsBetWeen(tomorrow, day7th);

        listTodayEvent = createEventList(eventsToday);
        listRecentEvent = createEventList(eventsRecent);
        listUpcomingEvent = createEventList(eventsUpcoming7);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * 57) / 100;

        todayEventAdapter = new EventAdapter(this, listTodayEvent, 0);
        recyclerViewToday.setAdapter(todayEventAdapter);
        recyclerViewToday.setLayoutManager(new MyLayoutManager(getApplicationContext(), recyclerViewToday, height));
        RecyclerView.Adapter mAdapter = recyclerViewToday.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerViewToday.getLayoutParams().height = height;
        }

        recentEventAdapter = new EventAdapter(this, listRecentEvent, 1);
        recyclerViewRecent.setAdapter(recentEventAdapter);
        recyclerViewRecent.setLayoutManager(new MyLayoutManager(getApplicationContext(), recyclerViewRecent, height));
        mAdapter = recyclerViewRecent.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerViewRecent.getLayoutParams().height = height;
        }

        upcomingEventAdapter = new EventAdapter(this, listUpcomingEvent, 2);
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

    private List<EventItem> createEventList(ArrayList<Event> eventsToday) {
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
                Log.i("MAULIK", "year can not be parsed");
            }

            item.setPersonName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            item.setEventName(eventName);
            item.setEventType(getEventType(eventName));
            item.setEventDetail(setEventDetailText(currentYear - eventYear, eventType));
            item.setEventDate(e.getEvmStartDate());
            item.setEventRecordIndexId(e.getEvmRecordIndexId());
            item.setPersonRcpPmId(pmId);
            Comment comment = tableCommentMaster.getComment(e.getEvmRecordIndexId());
            if (comment != null) {
                item.setUserComment(comment.getCrmComment());
                item.setCommentTime(comment.getCrmUpdatedAt());
                item.setEventCommentPending(false);
            } else {
                item.setEventCommentPending(true);
            }

            list.add(item);
        }
        return list;
    }

    private int getEventType(String eventName) {
        int eventType = 0;
        if ("Birthday".equalsIgnoreCase(eventName)) eventType = AppConstants.COMMENT_TYPE_BIRTHDAY;
        if ("Aniversary".equalsIgnoreCase(eventName))
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
                s = eventYears + " Year Old";
            } else {
                s = eventYears + " Years Old";
            }
        }
        if (eventType == AppConstants.COMMENT_TYPE_ANNIVERSARY) {
            s = Utils.addDateSufixes(eventYears) + " Aniversary";
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
                Log.i("MAULIK no err", wsResponseObject.getMessage());
                Log.i("MAULIK no err", wsResponseObject.getStatus());
//                "id": "14899823733238",
//                        "from_pm_id": 28,
//                        "comment": "hello",
//                        "reply": "",
//                        "date": "2017-03-15",
//                        "status": 1,
//                        "created_at": "2017-03-20 03:59:33",
//                        "updated_at": "2017-03-20 03:59:33",
//                        "reply_at": "",
//                        "to_pm_id": 1,
//                        "type": "birthday"
//    static final String CREATE_TABLE_RC_COMMENT_MASTER = "CREATE TABLE " + TABLE_RC_COMMENT_MASTER +
//            " (" +
//            " " + COLUMN_CRM_ID + " integer NOT NULL CONSTRAINT rc_comment_master_pk PRIMARY KEY AUTOINCREMENT," +
//            " " + COLUMN_CRM_STATUS + " integer NOT NULL," +
//            " " + COLUMN_CRM_RATING + " text," +
//            " " + COLUMN_CRM_TYPE + " int NOT NULL," +
//            " " + COLUMN_CRM_CLOUD_PR_ID + " text NOT NULL," +
//            " " + COLUMN_CRM_RC_PROFILE_MASTER_PM_ID + " integer NOT NULL," +
//            " " + COLUMN_CRM_COMMENT + " text NOT NULL," +
//            " " + COLUMN_CRM_REPLY + " text," +
//            " " + COLUMN_CRM_CREATED_AT + " datetime NOT NULL," +
//            " " + COLUMN_CRM_REPLIED_AT + " datetime," +
//            " " + COLUMN_CRM_UPDATED_AT + " datetime NOT NULL" +
//            ");";
                Log.i("MAULIK no err", "eventComment.getId()" + eventComment.getId());
                Log.i("MAULIK no err", "eventComment.getFromPmId()" + eventComment.getFromPmId());
                Log.i("MAULIK no err", "eventComment.getComment()" + eventComment.getComment());
                Log.i("MAULIK no err", "eventComment.getReply() " + eventComment.getReply());
                Log.i("MAULIK no err", "eventComment.getDate() " + eventComment.getDate());
                Log.i("MAULIK no err", "eventComment.getStatus() " + eventComment.getStatus());
                Log.i("MAULIK no err", "eventComment.getCreatedAt() " + eventComment.getCreatedAt());
                Log.i("MAULIK no err", "eventComment.getUpdatedAt() " + eventComment.getUpdatedAt());
                Log.i("MAULIK no err", "eventComment.getReplyAt() " + eventComment.getReplyAt());
                Log.i("MAULIK no err", "eventComment.getToPmId() " + eventComment.getToPmId());
                Log.i("MAULIK no err", "eventComment.getType() " + eventComment.getType());
                Comment comment = new Comment();
                comment.setCrmStatus(Integer.parseInt(eventComment.getStatus()));
                comment.setCrmRating("");
                comment.setCrmType(getEventType(eventComment.getType()));
                comment.setCrmCloudPrId(eventComment.getId());
                comment.setRcProfileMasterPmId(eventComment.getToPmId());
                comment.setCrmComment(eventComment.getComment());
                comment.setCrmReply(eventComment.getReply());
                comment.setCrmCreatedAt(eventComment.getCreatedAt());
                comment.setCrmRepliedAt(eventComment.getReplyAt());
                comment.setCrmUpdatedAt(eventComment.getUpdatedAt());
                comment.setEvmRecordIndexId(evmRecordId);
                if (evmRecordId != null) {
                    tableCommentMaster.addComment(comment);
                    evmRecordId = "";
                    switch (selectedRecycler) {
                        case 0:
                            listTodayEvent.get(selectedRecyclerItem).setUserComment(eventComment.getComment());
                            listTodayEvent.get(selectedRecyclerItem).setCommentTime(eventComment.getUpdatedAt());
                            listTodayEvent.get(selectedRecyclerItem).setEventCommentPending(false);
                            todayEventAdapter.notifyDataSetChanged();
                            break;
                        case 1:
                            listRecentEvent.get(selectedRecyclerItem).setUserComment(eventComment.getComment());
                            listRecentEvent.get(selectedRecyclerItem).setCommentTime(eventComment.getUpdatedAt());
                            listRecentEvent.get(selectedRecyclerItem).setEventCommentPending(false);
                            recentEventAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            listUpcomingEvent.get(selectedRecyclerItem).setUserComment(eventComment.getComment());
                            listUpcomingEvent.get(selectedRecyclerItem).setCommentTime(eventComment.getUpdatedAt());
                            listUpcomingEvent.get(selectedRecyclerItem).setEventCommentPending(false);
                            upcomingEventAdapter.notifyDataSetChanged();
                            break;
                    }
                    Utils.hideProgressDialog();
                }
            }
        } else {
            // toast error
            Log.i("MAULIK err is there", error.toString());
        }

    }
}
