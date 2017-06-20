package com.rawalinfocom.rcontact.notifications;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.rawalinfocom.rcontact.adapters.TimelineAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.EventComment;
import com.rawalinfocom.rcontact.model.EventCommentData;
import com.rawalinfocom.rcontact.model.Rating;
import com.rawalinfocom.rcontact.model.TimelineItem;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.leolin.shortcutbadger.ShortcutBadger;

public class TimelineActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;

    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;

    @BindView(R.id.recyclerview1)
    RecyclerView recyclerViewToday;
    @BindView(R.id.search_view_timeline)
    SearchView searchViewTimeline;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerViewYesterday;
    @BindView(R.id.recyclerview3)
    RecyclerView recyclerViewPast5day;
    @BindView(R.id.view_more)
    TextView viewMore;
    @BindView(R.id.h1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.h2)
    RelativeLayout headerYesterdayLayout;
    @BindView(R.id.h3)
    RelativeLayout headerPast5daysLayout;
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

    private TimelineAdapter todayTimelineAdapter;
    private TimelineAdapter yesterdayTimelineAdapter;
    private TimelineAdapter past5daysTimelineAdapter;

    TableCommentMaster tableCommentMaster;
    TableNotificationStateMaster tableNotificationStateMaster;
    public static int selectedRecycler = -1;
    public static int selectedRecyclerItem = -1;

    List<TimelineItem> listTimelineToday;
    List<TimelineItem> listTimelineYesterday;
    List<TimelineItem> listTimelinePastDay;

    String today;
    String yesterDay;
    String dayBeforeYesterday;
    String pastday5thDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        init();
        tableCommentMaster = new TableCommentMaster(databaseHandler);
        tableNotificationStateMaster = new TableNotificationStateMaster(databaseHandler);
        initData();
        // getAllEventComment(TimelineActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {

        headerTodayTitle.setTypeface(Utils.typefaceRegular(this));
        headerYesterdayTitle.setTypeface(Utils.typefaceRegular(this));
        headerPast5DaysTitle.setTypeface(Utils.typefaceRegular(this));

        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(getResources().getString(R.string.nav_text_timeline));
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
        searchViewTimeline.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    todayTimelineAdapter.updateList(listTimelineToday);
                    yesterdayTimelineAdapter.updateList(listTimelineYesterday);
                    past5daysTimelineAdapter.updateList(listTimelinePastDay);
                    updateHeight();
                }
                return false;
            }
        });

    }

    private void filter(String query) {
        {

            List<TimelineItem> temp = new ArrayList<>();
            for (TimelineItem item : listTimelineToday) {
                if (item.getWisherName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            todayTimelineAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (TimelineItem item : listTimelineYesterday) {
                if (item.getWisherName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            yesterdayTimelineAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (TimelineItem item : listTimelinePastDay) {
                if (item.getWisherName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            past5daysTimelineAdapter.updateList(temp);
            updateHeight();
        }
    }

    private void initData() {

        today = getDate(0);
        yesterDay = getDate(-1);
        dayBeforeYesterday = getDate(-2);
        pastday5thDay = getDate(-6);

        ArrayList<Comment> commentsToday = tableCommentMaster.getAllCommentReceivedBetween(today, today);
        ArrayList<Comment> commentsYesterday = tableCommentMaster.getAllCommentReceivedBetween(yesterDay, yesterDay);
        ArrayList<Comment> commentsPastday = tableCommentMaster.getAllCommentReceivedBetween(pastday5thDay, dayBeforeYesterday);

        listTimelineToday = creatTimelineList(commentsToday);
        listTimelineYesterday = creatTimelineList(commentsYesterday);
        listTimelinePastDay = creatTimelineList(commentsPastday);

        todayTimelineAdapter = new TimelineAdapter(this, listTimelineToday, 0);
        yesterdayTimelineAdapter = new TimelineAdapter(this, listTimelineYesterday, 1);
        past5daysTimelineAdapter = new TimelineAdapter(this, listTimelinePastDay, 2);

        recyclerViewToday.setAdapter(todayTimelineAdapter);
        recyclerViewYesterday.setAdapter(yesterdayTimelineAdapter);
        recyclerViewPast5day.setAdapter(past5daysTimelineAdapter);

        updateHeight();
        tableNotificationStateMaster.makeAllNotificationsAsReadByType(AppConstants.NOTIFICATION_TYPE_TIMELINE);
        int badgeCount = tableNotificationStateMaster.getTotalUnreadCount();
        ShortcutBadger.applyCount(this.getApplicationContext(), badgeCount);
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
                heightPercent = 42;
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
        setRecyclerViewHeight(recyclerViewYesterday, height);
        setRecyclerViewHeight(recyclerViewPast5day, height);
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

    private List<TimelineItem> creatTimelineList(ArrayList<Comment> comments) {
        List<TimelineItem> list = new ArrayList<>();
        for (Comment comment : comments) {
            TimelineItem item = new TimelineItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
            TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);
            if (comment.getEvmRecordIndexId() != null) {
                Event event = tableEventMaster.getEventByEvmRecordIndexId(comment.getEvmRecordIndexId());
                item.setEventDetail(getResources().getString(R.string.text_wishes_on_your) + event.getEvmEventType());
            }
            int pmId = comment.getRcProfileMasterPmId();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
            item.setWisherName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            item.setWisherComment(comment.getCrmComment());
            item.setWisherCommentTime(comment.getCrmCreatedAt());
            item.setCrmCloudPrId(comment.getCrmCloudPrId());
            item.setCrmType(comment.getCrmType());
            item.setCrmRating(comment.getCrmRating());
            item.setEvmRecordIndexId(comment.getEvmRecordIndexId());
            item.setUserComment(comment.getCrmReply());
            item.setUserCommentTime(comment.getCrmRepliedAt());
            list.add(item);

        }
        return list;
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

    private void getAllEventComment(Context context) {

        WsRequestObject addCommentObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_EVENT_COMMENT, getResources().getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_EVENT_COMMENT);
        } else {
            Toast.makeText(TimelineActivity.this, getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();

        }
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_RATING)) {

                {
                    WsResponseObject wsResponseObject = (WsResponseObject) data;
                    Rating rating = wsResponseObject.getProfileRating();

                    int updated = tableCommentMaster.addReply(rating.getPrId() + "", rating.getPrReply(), Utils.getLocalTimeFromUTCTime(rating.getReplyAt()), Utils.getLocalTimeFromUTCTime(rating.getReplyAt()));
                    if (updated != 0) {
                        if (selectedRecycler != -1 && selectedRecyclerItem != -1) {
                            switch (selectedRecycler) {
                                case 0:
                                    addReplyAndUpdateList(listTimelineToday, todayTimelineAdapter, rating.getPrReply(), rating.getReplyAt());
                                    break;
                                case 1:
                                    addReplyAndUpdateList(listTimelineYesterday, yesterdayTimelineAdapter, rating.getPrReply(), rating.getReplyAt());
                                    break;
                                case 2:
                                    addReplyAndUpdateList(listTimelinePastDay, past5daysTimelineAdapter, rating.getPrReply(), rating.getReplyAt());
                                    break;
                            }
                            selectedRecycler = -1;
                            selectedRecyclerItem = -1;
                        }
                        Utils.hideProgressDialog();
                    }


                }
            } else if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_EVENT_COMMENT)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                ArrayList<EventCommentData> eventReceiveCommentData = wsResponseObject.getEventReceiveCommentData();
                saveCommentDataToDb(eventReceiveCommentData);
                Utils.hideProgressDialog();
            } else if (serviceType.equalsIgnoreCase(WsConstants.REQ_ADD_EVENT_COMMENT)) {
                WsResponseObject wsResponseObject = (WsResponseObject) data;
                EventComment eventComment = wsResponseObject.getEventComment();

                int updated = tableCommentMaster.addReply(eventComment.getId(), eventComment.getReply(),
                        Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()), Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                if (updated != 0) {
                    if (selectedRecycler != -1 && selectedRecyclerItem != -1) {
                        switch (selectedRecycler) {
                            case 0:
                                addReplyAndUpdateList(listTimelineToday, todayTimelineAdapter, eventComment.getReply(), eventComment.getReplyAt());
                                break;
                            case 1:
                                addReplyAndUpdateList(listTimelineYesterday, yesterdayTimelineAdapter, eventComment.getReply(), eventComment.getReplyAt());
                                break;
                            case 2:
                                addReplyAndUpdateList(listTimelinePastDay, past5daysTimelineAdapter, eventComment.getReply(), eventComment.getReplyAt());
                                break;
                        }
                        selectedRecycler = -1;
                        selectedRecyclerItem = -1;
                    }
                    Utils.hideProgressDialog();
                }


            }
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(TimelineActivity.this, getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void addReplyAndUpdateList(List<TimelineItem> list, TimelineAdapter adapter, String prReply, String replyAt) {
        list.get(selectedRecyclerItem).setUserComment(prReply);
        list.get(selectedRecyclerItem).setUserCommentTime(Utils.getLocalTimeFromUTCTime(replyAt));
        adapter.notifyDataSetChanged();
    }

    private void saveCommentDataToDb(ArrayList<EventCommentData> eventReceiveCommentData) {
        if (eventReceiveCommentData == null) {
            return;
        }
        for (EventCommentData eventCommentData : eventReceiveCommentData) {
            ArrayList<EventComment> allBirthdayComments = eventCommentData.getBirthday();
            ArrayList<EventComment> allAnniversaryComments = eventCommentData.getAnniversary();
            ArrayList<EventComment> allCustomEventsComments = eventCommentData.getCustom();
            ArrayList<EventComment> allRatingComments = eventCommentData.getRating();
            if (allBirthdayComments != null) {
                for (EventComment eventComment : allBirthdayComments) {
                    Comment comment = createComment(eventComment, getResources().getString(R.string.text_birthday));
                    tableCommentMaster.addComment(comment);
                }
            }
            if (allAnniversaryComments != null) {
                for (EventComment eventComment : allAnniversaryComments) {
                    Comment comment = createComment(eventComment, getResources().getString(R.string.text_anniversary));
                    tableCommentMaster.addComment(comment);
                    refreshAllList();
                }
            }
            if (allCustomEventsComments != null) {
                for (EventComment eventComment : allCustomEventsComments) {
                    Comment comment = createComment(eventComment, getResources().getString(R.string.text_custom));
                    tableCommentMaster.addComment(comment);
                }
            }
            if (allRatingComments != null) {
                for (EventComment eventComment : allRatingComments) {
                    Comment comment = createComment(eventComment, getResources().getString(R.string.text_rating));
                    tableCommentMaster.addComment(comment);
                }
            }
        }
        refreshAllList();
    }

    private Comment createComment(EventComment eventComment, String commentType) {
        Comment comment = new Comment();
        comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
        comment.setCrmRating("");
        comment.setCrmType(commentType);
        if (commentType.equalsIgnoreCase(getResources().getString(R.string.text_rating))) {
            comment.setCrmCloudPrId(eventComment.getPrId());
            comment.setCrmRating(eventComment.getRatingStars());
        } else {
            comment.setCrmCloudPrId(eventComment.getId());
        }
        comment.setRcProfileMasterPmId(eventComment.getFromPmId());
        comment.setCrmComment(eventComment.getComment());
        comment.setCrmReply(eventComment.getReply());
        comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getCreatedDate()));
        comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()));
        comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
        comment.setEvmRecordIndexId(eventComment.getEventRecordIndexId() + "");
        return comment;
    }

    private void refreshAllList() {
        ArrayList<Comment> commentsToday = tableCommentMaster.getAllCommentReceivedBetween(today, today);
        ArrayList<Comment> commentsYesterday = tableCommentMaster.getAllCommentReceivedBetween(yesterDay, yesterDay);
        ArrayList<Comment> commentsPastday = tableCommentMaster.getAllCommentReceivedBetween(pastday5thDay, dayBeforeYesterday);

        listTimelineToday = creatTimelineList(commentsToday);
        listTimelineYesterday = creatTimelineList(commentsYesterday);
        listTimelinePastDay = creatTimelineList(commentsPastday);

        todayTimelineAdapter.updateList(listTimelineToday);
        yesterdayTimelineAdapter.updateList(listTimelineYesterday);
        past5daysTimelineAdapter.updateList(listTimelinePastDay);
        updateHeight();
    }
}
