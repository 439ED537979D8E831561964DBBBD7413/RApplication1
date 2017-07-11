package com.rawalinfocom.rcontact.notifications;

import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiRatingAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
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

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingFragment extends BaseFragment implements WsResponseListener {


    @BindView(R.id.search_view_noti_rating)
    SearchView searchViewNotiRating;

    @BindView(R.id.header1)
    TextView textTodayTitle;
    @BindView(R.id.header1_icon)
    ImageView headerTodayIcon;
    @BindView(R.id.relative_header1)
    RelativeLayout headerTodayLayout;
    @BindView(R.id.recycler_view1)
    RecyclerView recyclerTodayRating;

    @BindView(R.id.text_header2)
    TextView textYesterDayTitle;
    @BindView(R.id.header2_icon)
    ImageView headerYesterDayIcon;
    @BindView(R.id.relative_header2)
    RelativeLayout headerYesterdayLayout;
    @BindView(R.id.recycler_view2)
    RecyclerView recyclerYesterDayRating;

    @BindView(R.id.text_header3)
    TextView textPastDaysTitle;
    @BindView(R.id.header3_icon)
    ImageView headerPastDayIcon;
    @BindView(R.id.relative_header3)
    RelativeLayout headerPastdayLayout;
    @BindView(R.id.recycler_view3)
    RecyclerView recyclerPastDayRating;

    @BindView(R.id.text_view_more)
    TextView textViewMore;
    TableCommentMaster tableCommentMaster;

    List<NotiRatingItem> listTodayRating;
    List<NotiRatingItem> listYesterdayRating;
    List<NotiRatingItem> listPastRating;

    NotiRatingAdapter todayRatingAdapter;
    NotiRatingAdapter yesterdayRatingAdapter;
    NotiRatingAdapter pastRatingAdapter;

    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;
    SoftKeyboard softKeyboard;
    String today;
    String yesterDay;
    String dayBeforeYesterday;
    String pastday5thDay;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiRatingFragment newInstance() {
        return new NotiRatingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_rating, container, false);
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
        // getAllRatingComment(this);

    }

    private void initData() {

        today = getDate(0);
        yesterDay = getDate(-1);
        dayBeforeYesterday = getDate(-2);
        pastday5thDay = getDate(-6);

        ArrayList<Comment> replyReceivedToday = tableCommentMaster.getAllRatingReplyReceived(today, today);
        ArrayList<Comment> replyReceivedYesterDay = tableCommentMaster.getAllRatingReplyReceived(yesterDay, yesterDay);
        ArrayList<Comment> replyReceivedPastDays = tableCommentMaster.getAllRatingReplyReceived(pastday5thDay, dayBeforeYesterday);

        listTodayRating = createRatingReplyList(replyReceivedToday);
        listYesterdayRating = createRatingReplyList(replyReceivedYesterDay);
        listPastRating = createRatingReplyList(replyReceivedPastDays);

        todayRatingAdapter = new NotiRatingAdapter(getActivity(), listTodayRating, 0);
        yesterdayRatingAdapter = new NotiRatingAdapter(getActivity(), listYesterdayRating, 1);
        pastRatingAdapter = new NotiRatingAdapter(getActivity(), listPastRating, 2);

        recyclerTodayRating.setAdapter(todayRatingAdapter);
        recyclerYesterDayRating.setAdapter(yesterdayRatingAdapter);
        recyclerPastDayRating.setAdapter(pastRatingAdapter);
        updateHeight();
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
            case DisplayMetrics.DENSITY_HIGH: /*240*/
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

        setRecyclerViewHeight(recyclerTodayRating, height);
        setRecyclerViewHeight(recyclerYesterDayRating, height);
        setRecyclerViewHeight(recyclerPastDayRating, height);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null)
                    ((NotificationsDetailActivity) getActivity()).updateNotificationCount(AppConstants.NOTIFICATION_TYPE_RATE);
            }
        }, 800);
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

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    private List<NotiRatingItem> createRatingReplyList(ArrayList<Comment> replyList) {
        List<NotiRatingItem> list = new ArrayList<>();
        for (Comment comment : replyList) {
            NotiRatingItem item = new NotiRatingItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

            int pmId = comment.getRcProfileMasterPmId();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);

            item.setRaterName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            item.setRaterPersonImage(userProfile.getPmProfileImage());
            item.setRating(comment.getCrmRating());
            item.setNotiTime(comment.getCrmRepliedAt());
            item.setComment(comment.getCrmComment());
            item.setReply(comment.getCrmReply());
            item.setCommentTime(comment.getCrmCreatedAt());
            item.setReplyTime(comment.getCrmRepliedAt());
            list.add(item);

        }
        return list;
    }

    private void getAllRatingComment(Fragment fragment) {

        WsRequestObject addCommentObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    addCommentObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_EVENT_COMMENT, getResources().getString(R.string.msg_please_wait), true)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,WsConstants.WS_ROOT + WsConstants.REQ_GET_EVENT_COMMENT);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_no_network), Toast.LENGTH_SHORT).show();
        }
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
        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textYesterDayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textPastDaysTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));

        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerTodayRating.getVisibility() == View.VISIBLE) {
                    recyclerTodayRating.setVisibility(View.GONE);
                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerTodayRating.setVisibility(View.VISIBLE);
                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerYesterDayRating.setVisibility(View.GONE);
                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);

                recyclerPastDayRating.setVisibility(View.GONE);
                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTodayRating.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerYesterDayRating.getVisibility() == View.VISIBLE) {
                    recyclerYesterDayRating.setVisibility(View.GONE);
                    headerYesterDayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerYesterDayRating.setVisibility(View.VISIBLE);
                    headerYesterDayIcon.setImageResource(R.drawable.ic_collapse);
                }

                recyclerPastDayRating.setVisibility(View.GONE);
                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerPastdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerTodayRating.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                recyclerYesterDayRating.setVisibility(View.GONE);
                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerPastDayRating.getVisibility() == View.VISIBLE) {
                    recyclerPastDayRating.setVisibility(View.GONE);
                    headerPastDayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerPastDayRating.setVisibility(View.VISIBLE);
                    headerPastDayIcon.setImageResource(R.drawable.ic_collapse);
                }
            }
        });
        searchViewNotiRating.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    todayRatingAdapter.updateList(listTodayRating);
                    yesterdayRatingAdapter.updateList(listYesterdayRating);
                    pastRatingAdapter.updateList(listPastRating);
                    updateHeight();
                }
                return false;
            }
        });

        recyclerYesterDayRating.setVisibility(View.GONE);
        recyclerPastDayRating.setVisibility(View.GONE);
    }

    private void filter(String query) {
        {

            List<NotiRatingItem> temp = new ArrayList<>();
            for (NotiRatingItem item : listTodayRating) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            todayRatingAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiRatingItem item : listYesterdayRating) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            yesterdayRatingAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiRatingItem item : listPastRating) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            pastRatingAdapter.updateList(temp);
            updateHeight();
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
            ArrayList<EventComment> allRatingComments = eventCommentData.getRating();
            if (allRatingComments != null) {
                for (EventComment eventComment : allRatingComments) {
                    tableCommentMaster.addReply(eventComment.getPrId(), eventComment.getReply(),
                            Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()), Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
                    refreshAllList();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();

    }

    private void refreshAllList() {

        ArrayList<Comment> replyReceivedToday = tableCommentMaster.getAllRatingReplyReceived(today, today);
        ArrayList<Comment> replyReceivedYesterDay = tableCommentMaster.getAllRatingReplyReceived(yesterDay, yesterDay);
        ArrayList<Comment> replyReceivedPastDays = tableCommentMaster.getAllRatingReplyReceived(pastday5thDay, dayBeforeYesterday);

        listTodayRating = createRatingReplyList(replyReceivedToday);
        listYesterdayRating = createRatingReplyList(replyReceivedYesterDay);
        listPastRating = createRatingReplyList(replyReceivedPastDays);

        todayRatingAdapter.updateList(listTodayRating);
        yesterdayRatingAdapter.updateList(listYesterdayRating);
        pastRatingAdapter.updateList(listPastRating);

        updateHeight();
    }
}
