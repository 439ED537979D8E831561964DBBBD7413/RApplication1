package com.rawalinfocom.rcontact.notifications;

import android.app.Activity;
import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiRatingHistoryAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.EventComment;
import com.rawalinfocom.rcontact.model.EventCommentData;
import com.rawalinfocom.rcontact.model.NotiRatingItem;
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

public class RatingHistory extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.search_view)
    SearchView searchView;

    /*@BindView(R.id.text_header1)
    TextView textTodayTitle;
    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;
    @BindView(R.id.relative_header1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.recycler_view1)
    RecyclerView recyclerViewToday;

    @BindView(R.id.text_header2)
    TextView textYesterdayTitle;
    @BindView(R.id.header2_icon)
    ImageView headerYesterdayIcon;

    @BindView(R.id.relative_header2)
    RelativeLayout headerYesterdayLayout;
    @BindView(R.id.recycler_view2)
    RecyclerView recyclerViewYesterday;

    @BindView(R.id.text_header3)
    TextView textPast5daysTitle;
    @BindView(R.id.header3_icon)
    ImageView headerPast5dayIcon;
    @BindView(R.id.relative_header3)
    RelativeLayout headerPast5dayLayout;
    @BindView(R.id.recycler_view3)
    RecyclerView recyclerViewPast5day;*/
    @BindView(R.id.text_view_more)
    TextView textViewMore;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;


    SoftKeyboard softKeyboard;
    TableCommentMaster tableCommentMaster;

    List<NotiRatingItem> listAllRatingDone;
//    List<NotiRatingItem> listTodayRatingDone;
//    List<NotiRatingItem> listYesterdayRatingDone;
//    List<NotiRatingItem> listPastRatingDone;

    List<NotiRatingItem> listAllRatingReceive;
//    List<NotiRatingItem> listYesterdayRatingReceive;
//    List<NotiRatingItem> listYesterdayRatingReceive;
//    List<NotiRatingItem> listPastRatingReceive;

    NotiRatingHistoryAdapter notiHisRatingAdapter;
//    NotiRatingHistoryAdapter todayRatingAdapter;
//    NotiRatingHistoryAdapter yesterdayRatingAdapter;
//    NotiRatingHistoryAdapter pastRatingAdapter;

    private static int tabIndex = 0;
    int height;
    String today;
    String yesterDay;
    String dayBeforeYesterday;
    String pastday5thDay;
    @BindView(R.id.recycler_view_rating_history)
    RecyclerView recyclerViewRatingHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_history_temp);
        ButterKnife.bind(this);
        init();
        tableCommentMaster = new TableCommentMaster(databaseHandler);
        initData();
        // getAllEventRatingReceived(RatingHistory.this);
    }

    private void getAllEventRatingReceived(Activity activity) {
        WsRequestObject addCommentObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_EVENT_COMMENT, getResources().getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants.REQ_GET_EVENT_COMMENT);
        } else {
            Toast.makeText(RatingHistory.this, getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();

        }
    }

    private void init() {
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabIndex = tab.getPosition();
                if (tabIndex == 0) {
                    if (notiHisRatingAdapter != null)
                        notiHisRatingAdapter.updateList(listAllRatingDone);
//                    if (todayRatingAdapter != null)
//                        todayRatingAdapter.updateList(listTodayRatingDone);
//                    if (yesterdayRatingAdapter != null)
//                        yesterdayRatingAdapter.updateList(listYesterdayRatingDone);
//                    if (pastRatingAdapter != null) {
//                        pastRatingAdapter.updateList(listPastRatingDone);
//                        updateHeight();
//                    }
                } else {
                    if (notiHisRatingAdapter != null)
                        notiHisRatingAdapter.updateList(listAllRatingReceive);
//                    if (todayRatingAdapter != null)
//                        todayRatingAdapter.updateList(listTodayRatingReceive);
//                    if (yesterdayRatingAdapter != null)
//                        yesterdayRatingAdapter.updateList(listYesterdayRatingReceive);
//                    if (pastRatingAdapter != null) {
//                        pastRatingAdapter.updateList(listPastRatingReceive);
//                        updateHeight();
//                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.text_rating_done)), true);
        tabs.addTab(tabs.newTab().setText(getResources().getString(R.string.text_rating_receive)));
        textToolbarTitle.setText(getResources().getString(R.string.nav_text_rating_history));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openWebSite(getApplicationContext());
            }
        });

//        textTodayTitle.setTypeface(Utils.typefaceRegular(this));
//        textYesterdayTitle.setTypeface(Utils.typefaceRegular(this));
//        textPast5daysTitle.setTypeface(Utils.typefaceRegular(this));

        rippleActionBack.setOnRippleCompleteListener(this);
//        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
//        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (recyclerViewToday.getVisibility() == View.VISIBLE) {
//                    recyclerViewToday.setVisibility(View.GONE);
//                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerViewToday.setVisibility(View.VISIBLE);
//                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//                recyclerViewYesterday.setVisibility(View.GONE);
//                headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
//                recyclerViewPast5day.setVisibility(View.GONE);
//                headerPast5dayIcon.setImageResource(R.drawable.ic_expand);
//            }
//        });
//        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerViewToday.setVisibility(View.GONE);
//                headerTodayIcon.setImageResource(R.drawable.ic_expand);
//                recyclerViewPast5day.setVisibility(View.GONE);
//                headerPast5dayIcon.setImageResource(R.drawable.ic_expand);
//                if (recyclerViewYesterday.getVisibility() == View.VISIBLE) {
//                    recyclerViewYesterday.setVisibility(View.GONE);
//                    headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerViewYesterday.setVisibility(View.VISIBLE);
//                    headerYesterdayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//
//            }
//        });
//        headerPast5dayLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerViewToday.setVisibility(View.GONE);
//                headerTodayIcon.setImageResource(R.drawable.ic_expand);
//                recyclerViewYesterday.setVisibility(View.GONE);
//                headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
//                if (recyclerViewPast5day.getVisibility() == View.VISIBLE) {
//                    recyclerViewPast5day.setVisibility(View.GONE);
//                    headerPast5dayIcon.setImageResource(R.drawable.ic_expand);
//                } else {
//                    recyclerViewPast5day.setVisibility(View.VISIBLE);
//                    headerPast5dayIcon.setImageResource(R.drawable.ic_collapse);
//                }
//
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query, tabIndex);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tabIndex == 0) {
                    if (TextUtils.isEmpty(newText)) {
                        notiHisRatingAdapter.updateList(listAllRatingDone);
//                        todayRatingAdapter.updateList(listTodayRatingDone);
//                        yesterdayRatingAdapter.updateList(listYesterdayRatingDone);
//                        pastRatingAdapter.updateList(listPastRatingDone);
//                        updateHeight();
                    }
                } else {
                    if (TextUtils.isEmpty(newText)) {
                        notiHisRatingAdapter.updateList(listAllRatingReceive);
//                        todayRatingAdapter.updateList(listTodayRatingReceive);
//                        yesterdayRatingAdapter.updateList(listYesterdayRatingReceive);
//                        pastRatingAdapter.updateList(listPastRatingReceive);
//                        updateHeight();
                    }

                }
                return false;
            }

        });
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    private void initData() {

        today = getDate(0);
        yesterDay = getDate(-1);
        dayBeforeYesterday = getDate(-2);
        pastday5thDay = getDate(-6);

        ArrayList<Comment> ratingDoneAll = tableCommentMaster.getAllRatingDone(pastday5thDay, today);
//        ArrayList<Comment> ratingDoneToday = tableCommentMaster.getAllRatingDone(today, today);
//        ArrayList<Comment> ratingDoneYesterday = tableCommentMaster.getAllRatingDone(yesterDay, yesterDay);
//        ArrayList<Comment> ratingDonePast5day = tableCommentMaster.getAllRatingDone(pastday5thDay, dayBeforeYesterday);

        ArrayList<Comment> ratingReceiveAll = tableCommentMaster.getAllRatingReceived(pastday5thDay, today);
//        ArrayList<Comment> ratingReceiveToday = tableCommentMaster.getAllRatingReceived(today, today);
//        ArrayList<Comment> ratingReceiveYesterday = tableCommentMaster.getAllRatingReceived(yesterDay, yesterDay);
//        ArrayList<Comment> ratingReceivePast5day = tableCommentMaster.getAllRatingReceived(pastday5thDay, dayBeforeYesterday);

        listAllRatingDone = createRatingList(ratingDoneAll, 0);
//        listTodayRatingDone = createRatingList(ratingDoneToday, 0);
//        listYesterdayRatingDone = createRatingList(ratingDoneYesterday, 0);
//        listPastRatingDone = createRatingList(ratingDonePast5day, 0);

        listAllRatingReceive = createRatingList(ratingReceiveAll, 1);
//        listTodayRatingReceive = createRatingList(ratingReceiveToday, 1);
//        listYesterdayRatingReceive = createRatingList(ratingReceiveYesterday, 1);
//        listPastRatingReceive = createRatingList(ratingReceivePast5day, 1);

        notiHisRatingAdapter = new NotiRatingHistoryAdapter(this, listAllRatingDone);
//        todayRatingAdapter = new NotiRatingHistoryAdapter(this, listTodayRatingDone, 0);
//        yesterdayRatingAdapter = new NotiRatingHistoryAdapter(this, listYesterdayRatingDone, 1);
//        pastRatingAdapter = new NotiRatingHistoryAdapter(this, listPastRatingDone, 2);

        recyclerViewRatingHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRatingHistory.setAdapter(notiHisRatingAdapter);
//        recyclerViewToday.setAdapter(todayRatingAdapter);
//        recyclerViewYesterday.setAdapter(yesterdayRatingAdapter);
//        recyclerViewPast5day.setAdapter(pastRatingAdapter);

//        updateHeight();

//        recyclerViewYesterday.setVisibility(View.GONE);
//        recyclerViewPast5day.setVisibility(View.GONE);
    }

    private void updateHeight() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 40;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 28;
                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 28;
                break;
            case DisplayMetrics.DENSITY_HIGH: /*240*/
                heightPercent = 30;
                break;
            case DisplayMetrics.DENSITY_XHIGH: /*320*/
                heightPercent = 37;
                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
                heightPercent = 40;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 40;
                break;
        }
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = (displaymetrics.heightPixels * heightPercent) / 100;
//        setRecyclerViewHeight(recyclerViewToday, height);
//        setRecyclerViewHeight(recyclerViewYesterday, height);
//        setRecyclerViewHeight(recyclerViewPast5day, height);
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

    private List<NotiRatingItem> createRatingList(ArrayList<Comment> listComment, int historyType) {
        List<NotiRatingItem> list = new ArrayList<>();
        for (Comment comment : listComment) {
            NotiRatingItem item = new NotiRatingItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

            int pmId = comment.getRcProfileMasterPmId();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
            UserProfile currentUserProfile = tableProfileMaster.getProfileFromCloudPmId(Integer.parseInt(getUserPmId()));
            if (historyType == 0) {
                // 0 done
                item.setRaterName(currentUserProfile.getPmFirstName() + " " + currentUserProfile.getPmLastName());
                item.setRaterPersonImage(currentUserProfile.getPmProfileImage());
                item.setReceiverPersonName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
                item.setReceiverPersonImage(userProfile.getPmProfileImage());

            } else {
                // 1 receive
                String name = userProfile.getPmFirstName() + " " + userProfile.getPmLastName();
                if (name.trim().length() > 0) {
                    item.setRaterName(name);
                    item.setRaterPersonImage(userProfile.getPmProfileImage());
                } else {
                    item.setRaterName(comment.getCrmProfileDetails());
                    item.setRaterPersonImage(comment.getCrmImage());
                }
                item.setReceiverPersonName(currentUserProfile.getPmFirstName() + " " + currentUserProfile.getPmLastName());
                item.setReceiverPersonImage(currentUserProfile.getPmProfileImage());
            }
            item.setRating(comment.getCrmRating());
            item.setNotiTime(comment.getCrmCreatedAt());
            item.setComment(comment.getCrmComment());
            item.setReply(comment.getCrmReply());
            item.setHistoryType(historyType);
            item.setCommentTime(comment.getCrmCreatedAt());
            item.setReplyTime(comment.getCrmRepliedAt());
            list.add(item);

        }
        return list;
    }

    private void filter(String query, int tabIndex) {

        if (tabIndex == 0) {
            List<NotiRatingItem> temp = new ArrayList<>();
            for (NotiRatingItem item : listAllRatingDone) {
                if (item.getReceiverPersonName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            notiHisRatingAdapter.updateList(temp);

//            List<NotiRatingItem> temp = new ArrayList<>();
//            for (NotiRatingItem item : listTodayRatingDone) {
//                if (item.getReceiverPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            todayRatingAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiRatingItem item : listYesterdayRatingDone) {
//                if (item.getReceiverPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            yesterdayRatingAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiRatingItem item : listPastRatingDone) {
//                if (item.getReceiverPersonName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            pastRatingAdapter.updateList(temp);
//            updateHeight();
        } else {

            List<NotiRatingItem> temp = new ArrayList<>();
            for (NotiRatingItem item : listAllRatingReceive) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            notiHisRatingAdapter.updateList(temp);

//            List<NotiRatingItem> temp = new ArrayList<>();
//            for (NotiRatingItem item : listTodayRatingReceive) {
//                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            todayRatingAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiRatingItem item : listYesterdayRatingReceive) {
//                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            yesterdayRatingAdapter.updateList(temp);
//
//            temp = new ArrayList<>();
//            for (NotiRatingItem item : listPastRatingReceive) {
//                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
//                    temp.add(item);
//                }
//            }
//            pastRatingAdapter.updateList(temp);
//            updateHeight();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_EVENT_COMMENT)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                ArrayList<EventCommentData> eventReceiveCommentData = wsResponseObject.getEventReceiveCommentData();
                saveCommentDataToDb(eventReceiveCommentData);
                Utils.hideProgressDialog();
            }
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(RatingHistory.this, getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCommentDataToDb(ArrayList<EventCommentData> eventReceiveCommentData) {
        if (eventReceiveCommentData == null) {
            return;
        }
        for (EventCommentData eventCommentData : eventReceiveCommentData) {
            ArrayList<EventComment> allRatingComments = eventCommentData.getRating();
            if (allRatingComments != null) {
                for (EventComment eventComment : allRatingComments) {
                    Comment comment = createComment(eventComment, getResources().getString(R.string.str_tab_rating));
                    tableCommentMaster.addComment(comment);
                    tableCommentMaster.addReply(eventComment.getPrId(), eventComment.getReply(),
                            Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()), Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
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
        if (commentType.equalsIgnoreCase(getResources().getString(R.string.str_tab_rating))) {
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
        ArrayList<Comment> ratingDoneAll = tableCommentMaster.getAllRatingDone(pastday5thDay, today);
//        ArrayList<Comment> ratingDoneToday = tableCommentMaster.getAllRatingDone(today, today);
//        ArrayList<Comment> ratingDoneYesterday = tableCommentMaster.getAllRatingDone(yesterDay, yesterDay);
//        ArrayList<Comment> ratingDonePast5day = tableCommentMaster.getAllRatingDone(pastday5thDay, dayBeforeYesterday);

        ArrayList<Comment> ratingReceiveAll = tableCommentMaster.getAllRatingReceived(pastday5thDay, today);
//        ArrayList<Comment> ratingReceiveToday = tableCommentMaster.getAllRatingReceived(today, today);
//        ArrayList<Comment> ratingReceiveYesterday = tableCommentMaster.getAllRatingReceived(yesterDay, yesterDay);
//        ArrayList<Comment> ratingReceivePast5day = tableCommentMaster.getAllRatingReceived(pastday5thDay, dayBeforeYesterday);


        listAllRatingDone = createRatingList(ratingDoneAll, 0);
//        listTodayRatingDone = createRatingList(ratingDoneToday, 0);
//        listYesterdayRatingDone = createRatingList(ratingDoneYesterday, 0);
//        listPastRatingDone = createRatingList(ratingDonePast5day, 0);

        listAllRatingReceive = createRatingList(ratingReceiveAll, 1);
//        listTodayRatingReceive = createRatingList(ratingReceiveToday, 1);
//        listYesterdayRatingReceive = createRatingList(ratingReceiveYesterday, 1);
//        listPastRatingReceive = createRatingList(ratingReceivePast5day, 1);

        if (tabIndex == 0) {
            notiHisRatingAdapter.updateList(listAllRatingDone);
//            todayRatingAdapter.updateList(listTodayRatingDone);
//            yesterdayRatingAdapter.updateList(listYesterdayRatingDone);
//            pastRatingAdapter.updateList(listPastRatingDone);
//            updateHeight();

        } else {

            notiHisRatingAdapter.updateList(listAllRatingReceive);
//            todayRatingAdapter.updateList(listTodayRatingReceive);
//            yesterdayRatingAdapter.updateList(listYesterdayRatingReceive);
//            pastRatingAdapter.updateList(listPastRatingReceive);
//            updateHeight();

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
    protected void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }
}
