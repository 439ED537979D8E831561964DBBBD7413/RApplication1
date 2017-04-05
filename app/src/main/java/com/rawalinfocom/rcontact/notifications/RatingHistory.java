package com.rawalinfocom.rcontact.notifications;

import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
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

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiRatingHistoryAdapter;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.NotiRatingItem;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @BindView(R.id.text_header1)
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
    RecyclerView recyclerViewPast5day;
    @BindView(R.id.text_view_more)
    TextView textViewMore;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;


    SoftKeyboard softKeyboard;
    TableCommentMaster tableCommentMaster;

    List<NotiRatingItem> listTodayRatingDone;
    List<NotiRatingItem> listYesterdayRatingDone;
    List<NotiRatingItem> listPastRatingDone;

    List<NotiRatingItem> listTodayRatingReceive;
    List<NotiRatingItem> listYesterdayRatingReceive;
    List<NotiRatingItem> listPastRatingReceive;

    NotiRatingHistoryAdapter todayRatingAdapter;
    NotiRatingHistoryAdapter yesterdayRatingAdapter;
    NotiRatingHistoryAdapter pastRatingAdapter;

    private static int tabIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_history);
        ButterKnife.bind(this);
        init();
        tableCommentMaster = new TableCommentMaster(databaseHandler);
        initData();
    }

    private void init() {
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabIndex = tab.getPosition();
                if (tabIndex == 0) {
                    if (todayRatingAdapter != null)
                        todayRatingAdapter.updateList(listTodayRatingDone);
                    if (yesterdayRatingAdapter != null)
                        yesterdayRatingAdapter.updateList(listYesterdayRatingDone);
                    if (pastRatingAdapter != null) {
                        pastRatingAdapter.updateList(listPastRatingDone);
                        updateHeight();
                    }
                } else {
                    if (todayRatingAdapter != null)
                        todayRatingAdapter.updateList(listTodayRatingReceive);
                    if (yesterdayRatingAdapter != null)
                        yesterdayRatingAdapter.updateList(listYesterdayRatingReceive);
                    if (pastRatingAdapter != null) {
                        pastRatingAdapter.updateList(listPastRatingReceive);
                        updateHeight();
                    }
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

        textTodayTitle.setTypeface(Utils.typefaceRegular(this));
        textYesterdayTitle.setTypeface(Utils.typefaceRegular(this));
        textPast5daysTitle.setTypeface(Utils.typefaceRegular(this));

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
                recyclerViewYesterday.setVisibility(View.GONE);
                headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
                recyclerViewPast5day.setVisibility(View.GONE);
                headerPast5dayIcon.setImageResource(R.drawable.ic_expand);
            }
        });
        headerYesterdayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewToday.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);
                recyclerViewPast5day.setVisibility(View.GONE);
                headerPast5dayIcon.setImageResource(R.drawable.ic_expand);
                if (recyclerViewYesterday.getVisibility() == View.VISIBLE) {
                    recyclerViewYesterday.setVisibility(View.GONE);
                    headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewYesterday.setVisibility(View.VISIBLE);
                    headerYesterdayIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
        headerPast5dayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewToday.setVisibility(View.GONE);
                headerTodayIcon.setImageResource(R.drawable.ic_expand);
                recyclerViewYesterday.setVisibility(View.GONE);
                headerYesterdayIcon.setImageResource(R.drawable.ic_expand);
                if (recyclerViewPast5day.getVisibility() == View.VISIBLE) {
                    recyclerViewPast5day.setVisibility(View.GONE);
                    headerPast5dayIcon.setImageResource(R.drawable.ic_expand);
                } else {
                    recyclerViewPast5day.setVisibility(View.VISIBLE);
                    headerPast5dayIcon.setImageResource(R.drawable.ic_collapse);
                }

            }
        });
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
                        todayRatingAdapter.updateList(listTodayRatingDone);
                        yesterdayRatingAdapter.updateList(listYesterdayRatingDone);
                        pastRatingAdapter.updateList(listPastRatingDone);
                    }
                } else {
                    if (TextUtils.isEmpty(newText)) {
                        todayRatingAdapter.updateList(listTodayRatingReceive);
                        yesterdayRatingAdapter.updateList(listYesterdayRatingReceive);
                        pastRatingAdapter.updateList(listPastRatingReceive);
                    }

                }
                return false;
            }

        });
    }

    private String getDate(int dayToAddorSub) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        Date date = new Date();
        date.setTime(date.getTime() + dayToAddorSub * 24 * 60 * 60 * 1000);
        return sdf.format(date);
    }

    private void initData() {

        String today = getDate(0); // 22
        String yesterDay = getDate(-1); // 21
        String dayBeforeYesterday = getDate(-2); //20
        String pastday5thDay = getDate(-6); //16

        ArrayList<Comment> ratingDoneToday = tableCommentMaster.getAllRatingDone(today, today);
        ArrayList<Comment> ratingDoneYesterday = tableCommentMaster.getAllRatingDone(yesterDay, yesterDay);
        ArrayList<Comment> ratingDonePast5day = tableCommentMaster.getAllRatingDone(pastday5thDay, dayBeforeYesterday);

        ArrayList<Comment> ratingReceiveToday = tableCommentMaster.getAllRatingReceived(today, today);
        ArrayList<Comment> ratingReceiveYesterday = tableCommentMaster.getAllRatingReceived(yesterDay, yesterDay);
        ArrayList<Comment> ratingReceivePast5day = tableCommentMaster.getAllRatingReceived(pastday5thDay, dayBeforeYesterday);

        listTodayRatingDone = createRatingList(ratingDoneToday, 0);
        listYesterdayRatingDone = createRatingList(ratingDoneYesterday, 0);
        listPastRatingDone = createRatingList(ratingDonePast5day, 0);

        listTodayRatingReceive = createRatingList(ratingReceiveToday, 1);
        listYesterdayRatingReceive = createRatingList(ratingReceiveYesterday, 1);
        listPastRatingReceive = createRatingList(ratingReceivePast5day, 1);

        todayRatingAdapter = new NotiRatingHistoryAdapter(this, listTodayRatingDone, 0);
        yesterdayRatingAdapter = new NotiRatingHistoryAdapter(this, listYesterdayRatingDone, 1);
        pastRatingAdapter = new NotiRatingHistoryAdapter(this, listPastRatingDone, 2);

        recyclerViewToday.setAdapter(todayRatingAdapter);
        recyclerViewYesterday.setAdapter(yesterdayRatingAdapter);
        recyclerViewPast5day.setAdapter(pastRatingAdapter);

        updateHeight();

        recyclerViewYesterday.setVisibility(View.GONE);
        recyclerViewPast5day.setVisibility(View.GONE);
    }

    private void updateHeight() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        int density = getResources().getDisplayMetrics().densityDpi;
        int heightPercent = 40;
        int maxItemCount = 1;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW: /*120*/
                heightPercent = 30;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_MEDIUM: /*160*/
                heightPercent = 30;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_HIGH: /*320*/
                heightPercent = 35;
                maxItemCount = 1;
                break;
            case DisplayMetrics.DENSITY_XXHIGH: /*480*/
            case DisplayMetrics.DENSITY_XXXHIGH: /*680*/
                heightPercent = 40;
                maxItemCount = 2;
                break;
        }
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = (displaymetrics.heightPixels * heightPercent) / 100;

        recyclerViewToday.setLayoutManager(new CustomLayoutManager(this, recyclerViewToday, height));
        RecyclerView.Adapter mAdapter = recyclerViewToday.getAdapter();
        int totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > maxItemCount) {
            recyclerViewToday.getLayoutParams().height = height;
        } else {
            recyclerViewToday.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }


        recyclerViewYesterday.setLayoutManager(new CustomLayoutManager(this, recyclerViewYesterday, height));
        mAdapter = recyclerViewYesterday.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > maxItemCount) {
            recyclerViewYesterday.getLayoutParams().height = height;
        } else {
            recyclerViewYesterday.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }


        recyclerViewPast5day.setLayoutManager(new CustomLayoutManager(this, recyclerViewPast5day, height));
        mAdapter = recyclerViewPast5day.getAdapter();
        totalItemCount = mAdapter.getItemCount();
        if (totalItemCount > maxItemCount) {
            recyclerViewPast5day.getLayoutParams().height = height;
        } else {
            recyclerViewPast5day.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

    }

    private List<NotiRatingItem> createRatingList(ArrayList<Comment> listComment, int historyType) {
        List<NotiRatingItem> list = new ArrayList<>();
        for (Comment comment : listComment) {
            NotiRatingItem item = new NotiRatingItem();
            TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);

            int pmId = comment.getRcProfileMasterPmId();
            UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(pmId);

            item.setRaterName(userProfile.getPmFirstName() + " " + userProfile.getPmLastName());
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
            for (NotiRatingItem item : listTodayRatingDone) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            todayRatingAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiRatingItem item : listYesterdayRatingDone) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            yesterdayRatingAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiRatingItem item : listPastRatingDone) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            pastRatingAdapter.updateList(temp);
        } else {

            List<NotiRatingItem> temp = new ArrayList<>();
            for (NotiRatingItem item : listTodayRatingReceive) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            todayRatingAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiRatingItem item : listTodayRatingReceive) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            yesterdayRatingAdapter.updateList(temp);

            temp = new ArrayList<>();
            for (NotiRatingItem item : listTodayRatingReceive) {
                if (item.getRaterName().toLowerCase().contains(query.toLowerCase())) {
                    temp.add(item);
                }
            }
            pastRatingAdapter.updateList(temp);
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

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
