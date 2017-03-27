package com.rawalinfocom.rcontact;

import android.content.Context;
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
import android.widget.Toast;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.events.MyLayoutManager;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.EventComment;
import com.rawalinfocom.rcontact.model.EventCommentData;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.timeline.TimelineAdapter;
import com.rawalinfocom.rcontact.timeline.TimelineItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

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
    @BindView(R.id.view_more)
    TextView viewMore;
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

    private TimelineAdapter todayTimelineAdapter;
    private TimelineAdapter yesterdayTimelineAdapter;
    private TimelineAdapter past5daysTimelineAdapter;
    TableCommentMaster tableCommentMaster;
    public static int selectedRecycler = -1;
    public static int selectedRecyclerItem = -1;
    List<TimelineItem> listTimelineToday;
    List<TimelineItem> listTimelineYesterday;
    List<TimelineItem> listTimelinePastDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        init();
        tableCommentMaster = new TableCommentMaster(databaseHandler);

        // temporory code
        tableCommentMaster.deleteAllReceivedComments();
        getAllEventComment(TimelineActivity.this);
        // temporory code delete

        //initData();
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
        //today 22
        String today = getDate(0); // 22
        String yesterDay = getDate(-1); // 21
        String dayBeforeYesterday = getDate(-2); //20
        String pastday5thDay = getDate(-6); //16
        ArrayList<Comment> commentsToday = tableCommentMaster.getAllCommentReceivedBetween(today,
                today);
        ArrayList<Comment> commentsYesterday = tableCommentMaster.getAllCommentReceivedBetween
                (yesterDay, yesterDay);
        ArrayList<Comment> commentsPastday = tableCommentMaster.getAllCommentReceivedBetween
                (pastday5thDay, dayBeforeYesterday);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 50;
        int maxItemCount = 1;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 30;
                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 35;
                break;
            case DisplayMetrics.DENSITY_HIGH: /*320*/
                heightPercent = 42;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 50;
                maxItemCount = 2;
                break;
        }
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * heightPercent) / 100;

        listTimelineToday = creatTimelineList(commentsToday);
        listTimelineYesterday = creatTimelineList(commentsYesterday);
        listTimelinePastDay = creatTimelineList(commentsPastday);

        todayTimelineAdapter = new TimelineAdapter(this, listTimelineToday, 0);
        yesterdayTimelineAdapter = new TimelineAdapter(this, listTimelineYesterday, 1);
        past5daysTimelineAdapter = new TimelineAdapter(this, listTimelinePastDay, 2);


        recyclerViewToday.setAdapter(todayTimelineAdapter);
        recyclerViewToday.setLayoutManager(new MyLayoutManager(this, recyclerViewToday, height));
        RecyclerView.Adapter adapter = recyclerViewToday.getAdapter();
        int itemCount = adapter.getItemCount();
        if (itemCount > maxItemCount) {
            recyclerViewToday.getLayoutParams().height = height;
        }

        recyclerViewYesterday.setAdapter(yesterdayTimelineAdapter);
        recyclerViewYesterday.setLayoutManager(new MyLayoutManager(this, recyclerViewYesterday,
                height));
        adapter = recyclerViewYesterday.getAdapter();
        itemCount = adapter.getItemCount();
        if (itemCount > maxItemCount) {
            recyclerViewYesterday.getLayoutParams().height = height;
        }

        recyclerViewPast5day.setAdapter(past5daysTimelineAdapter);
        recyclerViewPast5day.setLayoutManager(new MyLayoutManager(this, recyclerViewPast5day,
                height));
        adapter = recyclerViewPast5day.getAdapter();
        itemCount = adapter.getItemCount();
        if (itemCount > maxItemCount) {
            recyclerViewPast5day.getLayoutParams().height = height;
        }

    }

    private List<TimelineItem> creatTimelineList(ArrayList<Comment> comments) {
        List<TimelineItem> list = new ArrayList<>();
        for (Comment comment : comments) {
            TimelineItem item = new TimelineItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
            TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);
            Event event = tableEventMaster.getEventByEvmRecordIndexId(Integer.parseInt(comment
                    .getEvmRecordIndexId()));
            int pmId = comment.getRcProfileMasterPmId();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
            item.setWisherName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            item.setEventDetail("wishes you on your " + event.getEvmEventType());
            item.setWisherComment(comment.getCrmComment());
            item.setWisherCommentTime(comment.getCrmCreatedAt());
            item.setCrmCloudPrId(comment.getCrmCloudPrId());
            item.setCrmType(comment.getCrmType());
            item.setEvmRecordIndexId(Integer.parseInt(comment.getEvmRecordIndexId()));
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
                    .REQ_GET_EVENT_COMMENT, "Getting comments..", true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_EVENT_COMMENT);
        } else {
            //show no toast
            Toast.makeText(TimelineActivity.this, "Please check your internet connection.", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_EVENT_COMMENT)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                ArrayList<EventCommentData> eventReceiveCommentData = wsResponseObject
                        .getEventReceiveCommentData();
                ArrayList<EventCommentData> eventSendCommentData = wsResponseObject
                        .getEventSendCommentData();
                saveCommentDataToDb(eventReceiveCommentData);
                saveReplyDataToDb(eventSendCommentData);
                Utils.hideProgressDialog();
                initData();
            } else if (serviceType.equalsIgnoreCase(WsConstants.REQ_ADD_EVENT_COMMENT)) {
                WsResponseObject wsResponseObject = (WsResponseObject) data;
                EventComment eventComment = wsResponseObject.getEventComment();

                int updated = tableCommentMaster.addReply(eventComment.getId(), eventComment
                        .getReply(), eventComment.getReplyAt(), eventComment.getUpdatedDate());
                if (updated != 0) {
                    if (selectedRecycler != -1 && selectedRecyclerItem != -1) {
                        switch (selectedRecycler) {
                            case 0:
                                listTimelineToday.get(selectedRecyclerItem).setUserComment
                                        (eventComment.getReply());
                                listTimelineToday.get(selectedRecyclerItem).setUserCommentTime
                                        (Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()));
                                todayTimelineAdapter.notifyDataSetChanged();
                                break;
                            case 1:
                                listTimelineYesterday.get(selectedRecyclerItem).setUserComment
                                        (eventComment.getReply());
                                listTimelineYesterday.get(selectedRecyclerItem)
                                        .setUserCommentTime(Utils.getLocalTimeFromUTCTime
                                                (eventComment.getReplyAt()));
                                yesterdayTimelineAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                listTimelinePastDay.get(selectedRecyclerItem).setUserComment
                                        (eventComment.getReply());
                                listTimelinePastDay.get(selectedRecyclerItem).setUserCommentTime
                                        (Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()));
                                past5daysTimelineAdapter.notifyDataSetChanged();
                                break;
                        }
                        selectedRecycler = -1;
                        selectedRecyclerItem = -1;
                    }
                    Utils.hideProgressDialog();
                }


            }
        } else {
            Toast.makeText(TimelineActivity.this, "There is some error, please try again.", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void saveReplyDataToDb(ArrayList<EventCommentData> eventSendCommentData) {
        if (eventSendCommentData == null) {
            return;
        }
        for (EventCommentData eventCommentData : eventSendCommentData) {
            ArrayList<EventComment> allBirthdayComments = eventCommentData.getBirthday();
            ArrayList<EventComment> allAnniversaryComments = eventCommentData.getAnniversary();
            ArrayList<EventComment> allCustomEventsComments = eventCommentData.getCustom();
            if (allBirthdayComments != null) {
                for (EventComment eventComment : allBirthdayComments) {
                    // Comment comment = createComment(eventComment, "Birthday");
                    int updated = tableCommentMaster.addReply(eventComment.getId(), eventComment
                            .getReply(), eventComment.getReplyAt(), eventComment.getUpdatedDate());
                }
            }
            if (allAnniversaryComments != null) {
                for (EventComment eventComment : allAnniversaryComments) {
                    //Comment comment = createComment(eventComment, "Anniversary");
                    int updated = tableCommentMaster.addReply(eventComment.getId(), eventComment
                            .getReply(), eventComment.getReplyAt(), eventComment.getUpdatedDate());
                }
            }
            if (allCustomEventsComments != null) {
                for (EventComment eventComment : allCustomEventsComments) {
                    //Comment comment = createComment(eventComment, "Custom");
                    int updated = tableCommentMaster.addReply(eventComment.getId(), eventComment
                            .getReply(), eventComment.getReplyAt(), eventComment.getUpdatedDate());
                }
            }
        }
    }

    private void saveCommentDataToDb(ArrayList<EventCommentData> eventReceiveCommentData) {
        if (eventReceiveCommentData == null) {
            return;
        }
        for (EventCommentData eventCommentData : eventReceiveCommentData) {
            ArrayList<EventComment> allBirthdayComments = eventCommentData.getBirthday();
            ArrayList<EventComment> allAnniversaryComments = eventCommentData.getAnniversary();
            ArrayList<EventComment> allCustomEventsComments = eventCommentData.getCustom();
            if (allBirthdayComments != null) {
                for (EventComment eventComment : allBirthdayComments) {
                    Comment comment = createComment(eventComment, "Birthday");
                    tableCommentMaster.addComment(comment);
                }
            }
            if (allAnniversaryComments != null) {
                for (EventComment eventComment : allAnniversaryComments) {
                    Comment comment = createComment(eventComment, "Anniversary");
                    tableCommentMaster.addComment(comment);
                }
            }
            if (allCustomEventsComments != null) {
                for (EventComment eventComment : allCustomEventsComments) {
                    Comment comment = createComment(eventComment, "Custom");
                    tableCommentMaster.addComment(comment);
                }
            }
        }
    }

    private Comment createComment(EventComment eventComment, String commentType) {
        Comment comment = new Comment();
        // get-comment-event gives u all recived events by other user id
        comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
        comment.setCrmRating("");
        comment.setCrmType(commentType);
        comment.setCrmCloudPrId(eventComment.getId());
        comment.setRcProfileMasterPmId(eventComment.getFromPmId());
        comment.setCrmComment(eventComment.getComment());
        comment.setCrmReply(eventComment.getReply());
        comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getCreatedDate()));
        comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()));
        comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
        comment.setEvmRecordIndexId(eventComment.getEventRecordIndexId() + "");
        return comment;
    }


}
