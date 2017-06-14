package com.rawalinfocom.rcontact.contacts;


import android.content.Intent;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.RContactListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
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
    public static ArrayList<Object> arrayListRContact;

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
        if (arrayListRContact == null) {
            arrayListContactHeaders = new ArrayList<>();
            isReload = false;
        } else {
            isReload = true;
        }
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

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isReload) {
            init();
        }
    }

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
                /*Toast.makeText(getActivity(), "Called: " + allContactListAdapter
                        .getListClickedPosition(), Toast.LENGTH_SHORT).show();*/
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

        /*// Connect the recycler to the scroller (to let the scroller scroll the list)
        scrollerAllContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerAllContact.setSectionIndicator(titleIndicator);

        setRecyclerViewLayoutManager(recyclerViewContactList);
//        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewContactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utils.isLastItemDisplaying(recyclerViewContactList)) {
                    relativeScroller.setVisibility(View.GONE);
                } else {
                    relativeScroller.setVisibility(View.VISIBLE);
                }
            }
        });*/

        setRecyclerViewLayoutManager(recyclerViewContactList);

        initSwipe();

        progressRContact.setVisibility(View.GONE);
        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());

        ArrayList<UserProfile> arrayListDisplayProfile = tableProfileMobileMapping
                .getRContactList();
        arrayListRContact = new ArrayList<>();
        if (arrayListDisplayProfile.size() > 0) {
            /*for (int i = 0; i < arrayListDisplayProfile.size(); i++) {
                String headerLetter = StringUtils.upperCase(StringUtils.substring
                        (arrayListDisplayProfile.get(i).getPmFirstName(), 0, 1));
                if (!arrayListRContact.contains(headerLetter)) {
                    arrayListRContact.add(headerLetter);
                    arrayListContactHeaders.add(headerLetter);
                }
                arrayListRContact.add(arrayListDisplayProfile.get(i));
            }*/
            arrayListRContact.addAll(arrayListDisplayProfile);
            rContactListAdapter = new RContactListAdapter(this, arrayListRContact,
                    arrayListContactHeaders);
            recyclerViewContactList.setAdapter(rContactListAdapter);

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
        recyclerView.scrollToPosition(scrollPosition);
    }

    private void showCallConfirmationDialog(final String number) {

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
                        Utils.callIntent(getActivity(), number);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");

        callConfirmationDialog.showDialog();

    }

    //</editor-fold>
}
