package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.events.MyLayoutManager;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiCommentsFragment extends BaseFragment {


    @BindView(R.id.search_view_events)
    SearchView searchViewEvents;

    @BindView(R.id.header1)
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
    RecyclerView recyclerPastDayComments;

    @BindView(R.id.text_view_more)
    TextView textViewMore;
    List<NotiCommentsItem> listTodayComments;
    List<NotiCommentsItem> listYesterdayComments;
    List<NotiCommentsItem> listPastComments;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiCommentsFragment newInstance() {
        return new NotiCommentsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_comments, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        initData();
    }

    private void initData() {
        TableCommentMaster tableCommentMaster = new TableCommentMaster(getDatabaseHandler());
        String today = getDate(0); // 22
        String yesterDay = getDate(-1); // 21
        String dayBeforeYesterday = getDate(-2); //20
        String pastday5thDay = getDate(-6); //16
        ArrayList<Comment> replyReceivedToday = tableCommentMaster.getAllReplyReceived(today, today);
        ArrayList<Comment> replyReceivedYesterDay = tableCommentMaster.getAllReplyReceived(yesterDay, yesterDay);
        ArrayList<Comment> replyReceivedPastDays = tableCommentMaster.getAllReplyReceived(pastday5thDay, dayBeforeYesterday);

//        NotiCommentsItem item1 = new NotiCommentsItem("Aakar Jain", "Aakar Jain reply you on your message.", "11:15 PM");
//        NotiCommentsItem item2 = new NotiCommentsItem("Angarika Shah", "Angarika Shah reply you on your message.", "11:15 PM");
//        NotiCommentsItem item3 = new NotiCommentsItem("Angarika Shah 1", "Angarika Shah 1 reply you on your message.", "11:15 PM");
//        NotiCommentsItem item4 = new NotiCommentsItem("Keval Pandit", "Keval Pandit reply you on your message.", "11:15 PM");
//        NotiCommentsItem item5 = new NotiCommentsItem("Keyur Kambli", "Keyur Kambli reply you on your message.", "11:15 PM");
//        NotiCommentsItem item6 = new NotiCommentsItem("Virat Gujarati", "Virat Gujarati reply you on message.", "11:15 PM");


        listTodayComments = createListToday(replyReceivedToday);
        listYesterdayComments = createListToday(replyReceivedYesterDay);
        listPastComments = createListToday(replyReceivedPastDays);

        NotiCommentsAdapter todayRatingAdapter = new NotiCommentsAdapter(getActivity(), listTodayComments, 0);
        NotiCommentsAdapter yesterdayRatingAdapter = new NotiCommentsAdapter(getActivity(), listYesterdayComments, 1);
        NotiCommentsAdapter pastRatingAdapter = new NotiCommentsAdapter(getActivity(), listPastComments, 2);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * 49) / 100;

        recyclerTodayComments.setAdapter(todayRatingAdapter);
        recyclerTodayComments.setLayoutManager(new MyLayoutManager(getActivity(), recyclerTodayComments, height));
        RecyclerView.Adapter mAdapter = recyclerTodayComments.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerTodayComments.getLayoutParams().height = height;
        }

        recyclerYesterDayComments.setAdapter(yesterdayRatingAdapter);
        recyclerYesterDayComments.setLayoutManager(new MyLayoutManager(getActivity(), recyclerYesterDayComments, height));
        mAdapter = recyclerYesterDayComments.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerYesterDayComments.getLayoutParams().height = height;
        }
        recyclerPastDayComments.setAdapter(pastRatingAdapter);
        recyclerPastDayComments.setLayoutManager(new MyLayoutManager(getActivity(), recyclerPastDayComments, height));
        mAdapter = recyclerPastDayComments.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > 3) {
            recyclerPastDayComments.getLayoutParams().height = height;
        }
        recyclerYesterDayComments.setVisibility(View.GONE);
        recyclerPastDayComments.setVisibility(View.GONE);
    }

    private List<NotiCommentsItem> createListToday(ArrayList<Comment> replyList) {
        List<NotiCommentsItem> list = new ArrayList<>();
        for (Comment comment : replyList) {
            NotiCommentsItem item = new NotiCommentsItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
            TableEventMaster tableEventMaster = new TableEventMaster(getDatabaseHandler());
            Event event = tableEventMaster.getEventByEvmRecordIndexId(Integer.parseInt(comment.getEvmRecordIndexId()));
            int pmId = comment.getRcProfileMasterPmId();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);
            item.setCommenterName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
            item.setCommenterInfo(userProfile.getPmFirstName() + " reply you on your message");
            item.setNotiCommentTime(Utils.getLocalTimeFromUTCTime(comment.getCrmRepliedAt()));
            item.setComment(comment.getCrmComment());
            item.setReply(comment.getCrmReply());
            item.setCommentTime(Utils.getLocalTimeFromUTCTime(comment.getCrmCreatedAt()));
            item.setReplyTime(Utils.getLocalTimeFromUTCTime(comment.getCrmRepliedAt()));
            item.setEventName(event.getEvmEventType());
            list.add(item);

        }
        return list;
    }

    private void init() {
        textTodayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textYesterDayTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textPastDaysTitle.setTypeface(Utils.typefaceRegular(getActivity()));
        textViewMore.setTypeface(Utils.typefaceRegular(getActivity()));

        headerTodayIcon.setImageResource(R.drawable.ic_collapse);
        headerTodayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerTodayComments.getVisibility() == View.VISIBLE) {
                    recyclerTodayComments.setVisibility(View.GONE);
                    headerTodayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerTodayComments.setVisibility(View.VISIBLE);
                    headerTodayIcon.setImageResource(R.drawable.ic_collapse);
                }
                recyclerYesterDayComments.setVisibility(View.GONE);
                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);

                recyclerPastDayComments.setVisibility(View.GONE);
                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerTodayComments.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerYesterDayComments.getVisibility() == View.VISIBLE) {
                    recyclerYesterDayComments.setVisibility(View.GONE);
                    headerYesterDayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerYesterDayComments.setVisibility(View.VISIBLE);
                    headerYesterDayIcon.setImageResource(R.drawable.ic_collapse);
                }

                recyclerPastDayComments.setVisibility(View.GONE);
                headerPastDayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerPastdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerTodayComments.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);

                recyclerYesterDayComments.setVisibility(View.GONE);
                headerYesterDayIcon.setImageResource(R.drawable.ic_expand);

                if (recyclerPastDayComments.getVisibility() == View.VISIBLE) {
                    recyclerPastDayComments.setVisibility(View.GONE);
                    headerPastDayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerPastDayComments.setVisibility(View.VISIBLE);
                    headerPastDayIcon.setImageResource(R.drawable.ic_collapse);
                }
            }
        });
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }
}
