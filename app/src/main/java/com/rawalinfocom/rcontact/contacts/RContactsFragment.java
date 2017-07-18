package com.rawalinfocom.rcontact.contacts;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.RContactListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.RecyclerItemDecoration;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class RContactsFragment extends BaseFragment {

    @BindView(R.id.progress_r_contact)
    ProgressWheel progressRContact;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    /*@BindView(R.id.relative_scroller)
    RelativeLayout relativeScroller;*/
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;
    /*@BindView(R.id.scroller_all_contact)
    VerticalRecyclerViewFastScroller scrollerAllContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;*/

    ArrayList<String> arrayListContactHeaders;
    private ArrayList<UserProfile> arrayListDisplayProfile;
    public ArrayList<Object> arrayListRContact;

    RContactListAdapter rContactListAdapter;

    MaterialDialog callConfirmationDialog;

    private View rootView;
    private boolean isReload = false;

    //<editor-fold desc="Constructors">

    public RContactsFragment() {
        // Required empty public constructor
    }

    public static RContactsFragment newInstance() {
        return new RContactsFragment();
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (arrayListRContact == null) {
//            isReload = false;
//        } else {
//            isReload = true;
//        }
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_r_contacts, container, false);
            ButterKnife.bind(this, rootView);
        }
        registerLocalBroadCast();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (!isReload) {
        arrayListContactHeaders = new ArrayList<>();
        init();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterLocalBroadCast();
    }

    private void registerLocalBroadCast() {
        // rating update broadcast receiver register
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver
                (localBroadcastReceiverRatingUpdate,
                new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_RATING_UPDATE));
    }

    private void unregisterLocalBroadCast() {
        //  rating update broadcast receiver unregister
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver
                (localBroadcastReceiverRatingUpdate);
    }

    // rating update broadcast receiver
    boolean ratingUpdate;
    private BroadcastReceiver localBroadcastReceiverRatingUpdate = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {

            ratingUpdate = intent.getBooleanExtra(AppConstants.EXTRA_RATING_UPDATE, false);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ratingUpdate) {
                        getRContactFromDB();
                        int pos = intent.getIntExtra(AppConstants.EXTRA_RCONTACT_POSITION, 0);
                        rContactListAdapter.updateList(pos, arrayListRContact);
                    }
                }
            }, 100);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == AppConstants.REQUEST_CODE_PROFILE_DETAIL && resultCode ==
                    RESULT_OK) {
                if (OptionMenuDialog.IS_CONTACT_DELETED) {
                    OptionMenuDialog.IS_CONTACT_DELETED = false;
                    init();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private void init() {

        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));
        textTotalContacts.setVisibility(View.GONE);

        setRecyclerViewLayoutManager(recyclerViewContactList);

        initSwipe();

        progressRContact.setVisibility(View.GONE);

        getRContactFromDB();

        if (arrayListDisplayProfile.size() > 0) {
            rContactListAdapter = new RContactListAdapter(this, arrayListRContact,
                    arrayListContactHeaders);
            recyclerViewContactList.setAdapter(rContactListAdapter);
        }
    }

    private void getRContactFromDB() {

        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());

        arrayListDisplayProfile = tableProfileMobileMapping.getRContactList(((BaseActivity)
                getActivity()).getUserPmId());

        arrayListRContact = new ArrayList<>();
        if (arrayListDisplayProfile.size() > 0) {
            arrayListRContact.addAll(arrayListDisplayProfile);
        }
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String actionNumber = StringUtils.defaultString(((RContactListAdapter
                        .RContactViewHolder) viewHolder).textContactNumber.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rContactListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof RContactListAdapter.ContactHeaderViewHolder || viewHolder
                        instanceof RContactListAdapter.ContactFooterViewHolder) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(getActivity(), R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(getActivity(), R.color.brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_sms);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width, (float) itemView.getRight() -
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewContactList);
    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerItemDecoration decoration = new RecyclerItemDecoration(getActivity(), ContextCompat
                .getColor(getActivity(), R.color.colorVeryLightGray), 0.7f);
        recyclerView.addItemDecoration(decoration);
        recyclerView.scrollToPosition(scrollPosition);
    }

    private void showCallConfirmationDialog(final String number) {

        final String finalNumber;

        if (!number.startsWith("+91")) {
            finalNumber = "+91" + number;
        } else {
            finalNumber = number;
        }

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                       /* Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);*/
                        Utils.callIntent(getActivity(), finalNumber);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " "
                + finalNumber + "?");

        callConfirmationDialog.showDialog();
    }
    //</editor-fold>
}
