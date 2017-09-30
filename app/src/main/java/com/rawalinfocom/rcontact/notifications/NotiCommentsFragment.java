package com.rawalinfocom.rcontact.notifications;

import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseNotificationFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RestorationActivity;
import com.rawalinfocom.rcontact.adapters.NotiCommentsAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.EventComment;
import com.rawalinfocom.rcontact.model.EventCommentData;
import com.rawalinfocom.rcontact.model.NotiCommentsItem;
import com.rawalinfocom.rcontact.model.RatingRequestResponseDataItem;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiCommentsFragment extends BaseNotificationFragment implements WsResponseListener {


    @BindView(R.id.search_view_noti_comments)
    SearchView searchViewNotiComments;

    /*@BindView(R.id.header1)
    TextView textTodayTitle;
    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;
    @BindView(R.id.relative_header1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.recycler_view1)
    RecyclerView recyclerTodayComments;

    @BindView(R.id.text_header2)
    TextView textYesterDayTitle;
    @BindView(R.id.header2_icon)
    ImageView headerYesterDayIcon;
    @BindView(R.id.relative_header2)
    RelativeLayout headerYesterdayLayout;
    @BindView(R.id.recycler_view2)
    RecyclerView recyclerYesterDayComments;

    @BindView(R.id.text_header3)
    TextView textPastDaysTitle;
    @BindView(R.id.header3_icon)
    ImageView headerPastDayIcon;
    @BindView(R.id.relative_header3)
    RelativeLayout headerPastdayLayout;
    @BindView(R.id.recycler_view3)
    RecyclerView recyclerPastDayComments;*/

    @BindView(R.id.text_view_more)
    TextView textViewMore;
    SoftKeyboard softKeyboard;

    List<NotiCommentsItem> listAllComments;
//    List<NotiCommentsItem> listTodayComments;
//    List<NotiCommentsItem> listYesterdayComments;
//    List<NotiCommentsItem> listPastComments;

    TableCommentMaster tableCommentMaster;

    NotiCommentsAdapter notiCommentsAdapter;
//    NotiCommentsAdapter todayCommentsAdapter;
//    NotiCommentsAdapter yesterdayCommentsAdapter;
//    NotiCommentsAdapter pastCommentsAdapter;

    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;

    String today;
    String yesterDay;
    String dayBeforeYesterday;
    String pastday5thDay;
    @BindView(R.id.recycler_view_comment_list)
    RecyclerView recyclerViewCommentList;
    @BindView(R.id.divider_timeline_search)
    View dividerTimelineSearch;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiCommentsFragment newInstance() {
        return new NotiCommentsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_comments_temp, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();

        tableCommentMaster = new TableCommentMaster(getDatabaseHandler());
        initData();
        //getAllEventComment(this);

    }

    private void initData() {

        today = getDate(0);
        yesterDay = getDate(-1);
        dayBeforeYesterday = getDate(-2);
        pastday5thDay = getDate(-6);

        ArrayList<Comment> replyReceivedAll = tableCommentMaster.getAllReplyReceived(pastday5thDay, today);
//        ArrayList<Comment> replyReceivedToday = tableCommentMaster.getAllReplyReceived(today, today);
//        ArrayList<Comment> replyReceivedYesterDay = tableCommentMaster.getAllReplyReceived(yesterDay, yesterDay);
//        ArrayList<Comment> replyReceivedPastDays = tableCommentMaster.getAllReplyReceived(pastday5thDay, dayBeforeYesterday);

        listAllComments = createReplyList(replyReceivedAll);
//        listTodayComments = createReplyList(replyReceivedToday);
//        listYesterdayComments = createReplyList(replyReceivedYesterDay);
//        listPastComments = createReplyList(replyReceivedPastDays);

        notiCommentsAdapter = new NotiCommentsAdapter(getActivity(), listAllComments);
//        todayCommentsAdapter = new NotiCommentsAdapter(getActivity(), listTodayComments, 0);
//        yesterdayCommentsAdapter = new NotiCommentsAdapter(getActivity(), listYesterdayComments, 1);
//        pastCommentsAdapter = new NotiCommentsAdapter(getActivity(), listPastComments, 2);

        recyclerViewCommentList.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewCommentList.setAdapter(notiCommentsAdapter);
//        recyclerTodayComments.setAdapter(todayCommentsAdapter);
//        recyclerYesterDayComments.setAdapter(yesterdayCommentsAdapter);
//        recyclerPastDayComments.setAdapter(pastCommentsAdapter);

//        updateHeight();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null)
                    ((NotificationsDetailActivity) getActivity()).updateNotificationCount(AppConstants.NOTIFICATION_TYPE_COMMENTS);
            }
        }, 300);

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(true);

                String fromDate = Utils.getStringPreference(getActivity(), AppConstants
                        .KEY_API_CALL_TIME_STAMP_COMMENT, "");
                pullMechanismServiceCall(fromDate, "", WsConstants.REQ_GET_COMMENT_DETAILS);
            }
        });
    }

    private void updateHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 40;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 30;
                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 30;
                break;
            case DisplayMetrics.DENSITY_HIGH: /*320*/
                heightPercent = 35;
                break;
            case DisplayMetrics.DENSITY_XHIGH: /*320*/
                heightPercent = 37;
                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 40;
                break;
        }
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * heightPercent) / 100;

//        setRecyclerViewHeight(recyclerTodayComments, height);
//        setRecyclerViewHeight(recyclerYesterDayComments, height);
//        setRecyclerViewHeight(recyclerPastDayComments, height);
//
    }

    private void setRecyclerViewHeight(RecyclerView recyclerView, int height) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int heightRecycler = recyclerView.getMeasuredHeight();
        if (heightRecycler > height) {
            recyclerView.getLayoutParams().height = height;
        } else {
            recyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private List<NotiCommentsItem> createReplyList(ArrayList<Comment> replyList) {

        List<NotiCommentsItem> list = new ArrayList<>();

        try {
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
            UserProfile currentUserProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt(getUserPmId()));
            for (Comment comment : replyList) {
                NotiCommentsItem item = new NotiCommentsItem();
                TableEventMaster tableEventMaster = new TableEventMaster(getDatabaseHandler());

                if (comment.getCrmType().equalsIgnoreCase("Rating")) {

                } else {
                    Event event = tableEventMaster.getEventByEvmRecordIndexId(comment.getEvmRecordIndexId());
                    item.setEventName(event.getEvmEventType());
                }

                int pmId = comment.getRcProfileMasterPmId();
                UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
                item.setCommenterName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
                item.setCommenterImage(userProfile.getPmProfileImage());
                item.setCommenterInfo(getResources().getString(R.string.text_reply_you_on_your, userProfile.getPmFirstName()));
                item.setNotiCommentTime(comment.getCrmRepliedAt());
                item.setComment(comment.getCrmComment());
                item.setReply(comment.getCrmReply());
                item.setCommentTime(comment.getCrmCreatedAt());
                item.setReplyTime(comment.getCrmRepliedAt());
                item.setReplyTime(comment.getCrmRepliedAt());
                item.setReceiverPersonImage(currentUserProfile.getPmProfileImage());

                list.add(item);

            }
            return list;

        } catch (Exception e) {
            System.out.println("RContact noticomment error");
        }

        return list;
    }

    private void init() {
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(layoutRoot, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                // Code here
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textViewMore.setVisibility(View.VISIBLE);
                    }
                });

            }

            @Override
            public void onSoftKeyboardShow() {
                // Code here
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textViewMore.setVisibility(View.GONE);
                    }
                });
            }
        });

//        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
//        textYesterDayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
//        textPastDaysTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));

//        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
//        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (recyclerTodayComments.getVisibility() == View.VISIBLE) {
//                    recyclerTodayComments.setVisibility(View.GONE);
//                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerTodayComments.setVisibility(View.VISIBLE);
//                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//                recyclerYesterDayComments.setVisibility(View.GONE);
//                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);
//
//                recyclerPastDayComments.setVisibility(View.GONE);
//                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
//            }
//        });
//        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerTodayComments.setVisibility(View.GONE);
//                headerTodayIcon.setImageResource(R.drawable.ic_expand);
//
//                if (recyclerYesterDayComments.getVisibility() == View.VISIBLE) {
//                    recyclerYesterDayComments.setVisibility(View.GONE);
//                    headerYesterDayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerYesterDayComments.setVisibility(View.VISIBLE);
//                    headerYesterDayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//
//                recyclerPastDayComments.setVisibility(View.GONE);
//                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
//            }
//        });
//        headerPastdayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                recyclerTodayComments.setVisibility(View.GONE);
//                headerTodayIcon.setImageResource(R.drawable.ic_expand);
//
//                recyclerYesterDayComments.setVisibility(View.GONE);
//                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);
//
//                if (recyclerPastDayComments.getVisibility() == View.VISIBLE) {
//                    recyclerPastDayComments.setVisibility(View.GONE);
//                    headerPastDayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerPastDayComments.setVisibility(View.VISIBLE);
//                    headerPastDayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//            }
//        });

        searchViewNotiComments.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    notiCommentsAdapter.updateList(listAllComments);
//                    todayCommentsAdapter.updateList(listTodayComments);
//                    yesterdayCommentsAdapter.updateList(listYesterdayComments);
//                    pastCommentsAdapter.updateList(listPastComments);
                    updateHeight();
                }
                return false;
            }
        });

//        recyclerYesterDayComments.setVisibility(View.GONE);
//        recyclerPastDayComments.setVisibility(View.GONE);
        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openWebSite(getActivity().getApplicationContext());
            }
        });
    }

    private void filter(String query) {
        {

            List<NotiCommentsItem> temp = new ArrayList<>();
            for (NotiCommentsItem item : listAllComments) {
                if (item.getCommenterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            notiCommentsAdapter.updateList(temp);

//            List<NotiCommentsItem> temp = new ArrayList<>();
//            for (NotiCommentsItem item : listTodayComments) {
//                if (item.getCommenterName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            todayCommentsAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiCommentsItem item : listYesterdayComments) {
//                if (item.getCommenterName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            yesterdayCommentsAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiCommentsItem item : listPastComments) {
//                if (item.getCommenterName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            pastCommentsAdapter.updateList(temp);
//            updateHeight();
        }
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    private void getAllEventComment(Fragment fragment) {

        WsRequestObject addCommentObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_EVENT_COMMENT, getResources().getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants.REQ_GET_EVENT_COMMENT);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_EVENT_COMMENT)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                ArrayList<EventCommentData> eventSendCommentData = wsResponseObject.getEventSendCommentData();
                saveReplyDataToDb(eventSendCommentData);
                Utils.hideProgressDialog();
            }

            // <editor-fold desc="REQ_GET_COMMENT_DETAILS">
            if (serviceType.contains(WsConstants.REQ_GET_COMMENT_DETAILS)) {
                WsResponseObject getCommentUpdateResponse = (WsResponseObject) data;
                if (getCommentUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getCommentUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    // cancel the Visual indication of a refresh
                    swipeRefreshLayout.setRefreshing(false);

                    storeCommentRequestResponseToDB(getCommentUpdateResponse,
                            getCommentUpdateResponse.getCommentReceive(), getCommentUpdateResponse.getCommentDone());
                    refreshAllList();
                    Utils.hideProgressDialog();

                } else {

                    Utils.hideProgressDialog();

                    if (getCommentUpdateResponse != null) {
                        System.out.println("RContact error --> " + getCommentUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getCommentUpdateResponse null");
                    }
                }
            }
            //</editor-fold>
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
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
                    tableCommentMaster.addReply(eventComment.getId(), eventComment.getReply(),
                            Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()), Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                }
            }
            if (allAnniversaryComments != null) {
                for (EventComment eventComment : allAnniversaryComments) {
                    tableCommentMaster.addReply(eventComment.getId(), eventComment.getReply(),
                            Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()), Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                }
            }
            if (allCustomEventsComments != null) {
                for (EventComment eventComment : allCustomEventsComments) {
                    tableCommentMaster.addReply(eventComment.getId(), eventComment.getReply(),
                            Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()), Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                }
            }
        }
        refreshAllList();
    }

    // PROFILE COMMENT HISTORY RESTORE
    private void storeCommentRequestResponseToDB(WsResponseObject getCommentUpdateResponse, ArrayList<RatingRequestResponseDataItem> commentReceive,
                                                 ArrayList<RatingRequestResponseDataItem> commentDone) {

        try {

            // eventCommentDone
            for (int i = 0; i < commentDone.size(); i++) {

                RatingRequestResponseDataItem dataItem = commentDone.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_SENT);
                comment.setCrmType("Comment");
                comment.setCrmCloudPrId(dataItem.getCommentId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getToPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmReply(dataItem.getReply());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setEvmRecordIndexId(dataItem.getEventRecordIndexId());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                if (!StringUtils.isEmpty(dataItem.getReplyAt()))
                    comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
                else comment.setCrmRepliedAt("");

                tableCommentMaster.addComment(comment);
            }

            // eventCommentReceive
            for (int i = 0; i < commentReceive.size(); i++) {

                RatingRequestResponseDataItem dataItem = commentReceive.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                comment.setCrmType("Comment");
                comment.setCrmCloudPrId(dataItem.getCommentId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getFromPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmReply(dataItem.getReply());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setEvmRecordIndexId(dataItem.getEventRecordIndexId());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                if (!StringUtils.isEmpty(dataItem.getReplyAt()))
                    comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
                else comment.setCrmRepliedAt("");

                tableCommentMaster.addComment(comment);
            }

        } catch (Exception e) {
            System.out.println("RContact storeCommentRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getCommentUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(getActivity(), AppConstants.KEY_API_CALL_TIME_STAMP_COMMENT,
                    getCommentUpdateResponse.getTimestamp());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();

    }

    private void refreshAllList() {

        ArrayList<Comment> replyReceivedAll = tableCommentMaster.getAllReplyReceived(pastday5thDay, today);
//        ArrayList<Comment> replyReceivedToday = tableCommentMaster.getAllReplyReceived(today, today);
//        ArrayList<Comment> replyReceivedYesterDay = tableCommentMaster.getAllReplyReceived(yesterDay, yesterDay);
//        ArrayList<Comment> replyReceivedPastDays = tableCommentMaster.getAllReplyReceived(pastday5thDay, dayBeforeYesterday);

        listAllComments = createReplyList(replyReceivedAll);
//        listTodayComments = createReplyList(replyReceivedToday);
//        listYesterdayComments = createReplyList(replyReceivedYesterDay);
//        listPastComments = createReplyList(replyReceivedPastDays);

        notiCommentsAdapter.updateList(listAllComments);
//        todayCommentsAdapter.updateList(listTodayComments);
//        yesterdayCommentsAdapter.updateList(listYesterdayComments);
//        pastCommentsAdapter.updateList(listPastComments);

//        updateHeight();
    }

    private void pullMechanismServiceCall(String fromDate, String toDate, String url) {

        if (Utils.isNetworkAvailable(getActivity())) {
            WsRequestObject deviceDetailObject = new WsRequestObject();

            deviceDetailObject.setFromDate(fromDate);
            deviceDetailObject.setToDate(toDate);

            if (Utils.isNetworkAvailable(getActivity())) {
                new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(), deviceDetailObject, null,
                        WsResponseObject.class, url, getResources().getString(R.string.msg_please_wait), true)
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + url);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        null.unbind();
    }
}
