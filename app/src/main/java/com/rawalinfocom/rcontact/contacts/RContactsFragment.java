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
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.adapters.RContactListAdapter;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.ColorBubble
        .ColorGroupSectionTitleIndicator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.vertical
        .VerticalRecyclerViewFastScroller;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RContactsFragment extends BaseFragment {

    @BindView(R.id.progress_r_contact)
    ProgressWheel progressRContact;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    @BindView(R.id.scroller_all_contact)
    VerticalRecyclerViewFastScroller scrollerAllContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;

    // Fetch from local Profile Master
    ArrayList<UserProfile> arrayListUserProfile;

    // for Adapter
    ArrayList<ProfileData> arrayListProfileData;
    ArrayList<String> arrayListContactHeaders;

    RContactListAdapter rContactListAdapter;

    public RContactsFragment() {
        // Required empty public constructor
    }


    public static RContactsFragment newInstance() {
        return new RContactsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayListContactHeaders = new ArrayList<>();
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_r_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        scrollerAllContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerAllContact.setSectionIndicator(titleIndicator);

        setRecyclerViewLayoutManager(recyclerViewContactList);
//        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

        initSwipe();

        progressRContact.setVisibility(View.GONE);
        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());

        ArrayList<UserProfile> arrayListDisplayProfile = tableProfileMobileMapping
                .getRContactList();
        ArrayList<Object> arrayListRContact = new ArrayList<>();
        if (arrayListDisplayProfile.size() > 0) {
            for (int i = 0; i < arrayListDisplayProfile.size(); i++) {
                String headerLetter = StringUtils.upperCase(StringUtils.substring
                        (arrayListDisplayProfile.get(i).getPmFirstName(), 0, 1));
                if (!arrayListRContact.contains(headerLetter)) {
                    arrayListRContact.add(headerLetter);
                    arrayListContactHeaders.add(headerLetter);
                }
                arrayListRContact.add(arrayListDisplayProfile.get(i));
            }
            rContactListAdapter = new RContactListAdapter(getActivity(), arrayListRContact,
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
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                            actionNumber));
                    startActivity(intent);
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
                if (viewHolder instanceof AllContactListAdapter.ContactHeaderViewHolder) {
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

    private void fetchData() {
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        arrayListUserProfile = new ArrayList<>();
        arrayListUserProfile.addAll(tableProfileMaster.getAllUserProfiles());
        arrayListProfileData = new ArrayList<>();
        for (int i = 0; i < arrayListUserProfile.size(); i++) {
            ProfileData profileData = new ProfileData();
            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();
            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
            profileDataOperation.setPbNameFirst(arrayListUserProfile.get(i).getPmFirstName());
            profileDataOperation.setPbNameLast(arrayListUserProfile.get(i).getPmLastName());

           /* TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                    (getDatabaseHandler());
            tableProfileMobileMapping.getProfileMobileMappingPmId(Integer.parseInt
                    (arrayListUserProfile.get(i).getPmRcpId()));*/

            TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
            ArrayList<MobileNumber> arrayListMobileNumber = tableMobileMaster
                    .getMobileNumbersFromPmId(Integer.parseInt(arrayListUserProfile.get(i)
                            .getPmRcpId()));
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
            for (int j = 0; j < arrayListMobileNumber.size(); j++) {
                ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
                phoneNumber.setPhoneNumber(arrayListMobileNumber.get(j).getMnmMobileNumber());
                arrayListPhoneNumber.add(phoneNumber);
            }
            profileDataOperation.setPbPhoneNumber(arrayListPhoneNumber);
//            profileDataOperation.setPbPhoneNumber(new
// ArrayList<ProfileDataOperationPhoneNumber>());

            profileDataOperation.setPbEmailId(new ArrayList<ProfileDataOperationEmail>());

            arrayListOperation.add(profileDataOperation);
            profileData.setOperation(arrayListOperation);
            arrayListProfileData.add(profileData);
        }
    }
}
