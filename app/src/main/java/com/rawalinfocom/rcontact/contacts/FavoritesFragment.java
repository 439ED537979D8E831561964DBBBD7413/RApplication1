package com.rawalinfocom.rcontact.contacts;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.AllContactAdapter;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends BaseFragment implements LoaderManager
        .LoaderCallbacks<Cursor>, WsResponseListener {

    @BindView(R.id.progress_favorite_contact)
    ProgressWheel progressFavoriteContact;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    /*@BindView(R.id.relative_scroller)
    RelativeLayout relativeScroller;
    @BindView(R.id.scroller_favorite_contact)
    VerticalRecyclerViewFastScroller scrollerFavoriteContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;*/
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;
    @BindView(R.id.relative_root_favourite)
    RelativeLayout relativeRootFavourite;
    @BindView(R.id.layout_empty_view)
    LinearLayout layoutEmptyView;

    TextView textEmptyView;

    PhoneBookContacts phoneBookContacts;

    //    AllContactListAdapter allContactListAdapter;
    AllContactAdapter allContactListAdapter;
    ArrayList<ProfileData> arrayListUserContact;
    ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<String> arrayListContactHeaders;

    private View rootView;
    private boolean isReload = false;
    RContactApplication rContactApplication;
    private MaterialDialog callConfirmationDialog;

    //<editor-fold desc="Constructors">

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rContactApplication = (RContactApplication) getActivity().getApplicationContext();
        if (arrayListPhoneBookContacts == null) {
            arrayListContactHeaders = new ArrayList<>();
            arrayListPhoneBookContacts = new ArrayList<>();
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
       /* // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        ButterKnife.bind(this, view);
        return view;*/
        if (rootView == null || rContactApplication.getFavouriteStatus() != RContactApplication
                .FAVOURITE_UNMODIFIED) {
            rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rContactApplication.getFavouriteStatus() == RContactApplication.FAVOURITE_REMOVED) {
            if (allContactListAdapter.getListClickedPosition() != -1) {
                arrayListPhoneBookContacts.remove(allContactListAdapter.getListClickedPosition());
                allContactListAdapter.notifyItemRemoved(allContactListAdapter
                        .getListClickedPosition());
                rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_UNMODIFIED);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isReload || rContactApplication.getFavouriteStatus() != RContactApplication
                .FAVOURITE_UNMODIFIED) {
//        if (!isReload || rContactApplication.isFavouriteModified()) {
            init();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Data.LOOKUP_KEY, ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract
                .CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                ContactsContract.Contacts._ID};

        String selection = "starred = ?";
        String[] selectionArgs = new String[]{"1"};
//        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"; ;

        return new CursorLoader(
                getActivity(),
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        arrayListPhoneBookContacts = new ArrayList<>();
        getFavouritesFromPhonebook(data);
//        data.close();

//        rContactApplication.setFavouriteModified(false);
        rContactApplication.setFavouriteStatus(0);

        populateRecyclerView();
        initSwipe();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("onPause", "called");
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null && getActivity() != null) {

            // <editor-fold desc="REQ_SEND_INVITATION">
            if (serviceType.contains(WsConstants.REQ_SEND_INVITATION)) {
                WsResponseObject inviteContactResponse = (WsResponseObject) data;
                if (inviteContactResponse != null && StringUtils.equalsIgnoreCase
                        (inviteContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(getActivity(), relativeRootFavourite, "Invitation" +
                            " sent successfully");
                } else {
                    if (inviteContactResponse != null) {
                        Log.e("error response", inviteContactResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                        Utils.showErrorSnackBar(getActivity(), relativeRootFavourite, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            Utils.showErrorSnackBar(getActivity(), relativeRootFavourite, "" + (error != null ?
                    error.getLocalizedMessage() : null));
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        textEmptyView = ButterKnife.findById(layoutEmptyView, R.id.text_empty_view);

        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));
        textTotalContacts.setVisibility(View.GONE);

       /* // Connect the recycler to the scroller (to let the scroller scroll the list)
        scrollerFavoriteContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerFavoriteContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerFavoriteContact.setSectionIndicator(titleIndicator);

        setRecyclerViewLayoutManager(recyclerViewContactList);
//        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewContactList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (Utils.isFirstItemDisplaying(recyclerViewContactList) && Utils
                        .isLastItemDisplaying(recyclerViewContactList)) {
                    relativeScroller.setVisibility(View.GONE);
                } else {
                    relativeScroller.setVisibility(View.VISIBLE);
                }
            }
        });*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContactList.setLayoutManager(linearLayoutManager);

//        initSwipe();

        progressFavoriteContact.setVisibility(View.GONE);

        if (rContactApplication.getArrayListFavPhoneBookContacts().size() <= 0) {
//            getFavouriteContacts();
            if (getLoaderManager().getLoader(0) == null) {
                getLoaderManager().initLoader(0, null, this);
            } else {
                getLoaderManager().restartLoader(0, null, this);
            }
//            getLoaderManager().initLoader(0, null, this);
        } else {
            arrayListPhoneBookContacts = rContactApplication.getArrayListFavPhoneBookContacts();
            arrayListContactHeaders = rContactApplication.getArrayListFavContactHeaders();
            populateRecyclerView();
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
                String actionNumber = StringUtils.defaultString(((AllContactAdapter
                        .AllContactViewHolder) viewHolder).textContactNumber.getText()
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
                        allContactListAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof AllContactAdapter.ContactHeaderViewHolder ||
                        viewHolder instanceof AllContactAdapter.ContactFooterViewHolder) {
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
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    private void getRcpDetail() {
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        ArrayList<String> arrayListIds = tableProfileMaster.getAllRcpId();
        for (int i = 0; i < arrayListPhoneBookContacts.size(); i++) {
            if (arrayListPhoneBookContacts.get(i) instanceof ProfileData) {
                if (arrayListIds.contains(((ProfileData) arrayListPhoneBookContacts.get
                        (i)).getLocalPhoneBookId())) {
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(true);
                   /*  String name = tableProfileMaster.getNameFromRawId(((ProfileData)
                    arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId());
                    ((ProfileData) arrayListPhoneBookContacts.get(i))
                            .setTempRcpName(name);*/
                    ArrayList<UserProfile> userProfiles = new ArrayList<>();
                    userProfiles.addAll(tableProfileMaster.getProfileDetailsFromRawId((
                            (ProfileData) arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId
                            ()));
                    String name = "0";
                    String rcpID = "0";
                    if (userProfiles.size() > 1) {
                        for (int j = 0; j < userProfiles.size();
                             j++) {
                            if (name.equalsIgnoreCase("0")) {
                                name = userProfiles.get(j).getPmRcpId();
                            } else {
                                name = name + "," + userProfiles.get(j).getPmRcpId();
                            }
                        }
                    } else if (userProfiles.size() == 1) {
                        name = userProfiles.get(0).getPmFirstName() + " " + userProfiles.get(0)
                                .getPmLastName();
                        rcpID = userProfiles.get(0).getPmRcpId();
                    }
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpName(name);
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpId(rcpID);
                } else {
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(false);
                }
                final int finalI = i;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        allContactListAdapter.notifyItemChanged(finalI);
                    }
                });
            }
        }
    }

    private void getFavouritesFromPhonebook(Cursor data) {

        final int phoneIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        final int givenNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.DISPLAY_NAME);
        final int photoURIIdx = data.getColumnIndex(ContactsContract.PhoneLookup
                .PHOTO_THUMBNAIL_URI);
        final int lookUpKeyIdx = data.getColumnIndex(ContactsContract.Data.LOOKUP_KEY);
        final int rawIdIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone
                .RAW_CONTACT_ID);

        ArrayList contactsWithNoName = new ArrayList<>();
        String lastDisplayName = "XXX", lastRawId = "XXX";

        while (data.moveToNext()) {
            ProfileData profileData;
            profileData = new ProfileData();
            profileData.setTempFirstName(data.getString(givenNameIdx));
            profileData.setTempNumber(data.getString(phoneIdx));
            profileData.setProfileUrl(data.getString(photoURIIdx));
            profileData.setLocalPhoneBookId(data.getString(lookUpKeyIdx));
            profileData.setTempRawId(data.getString(rawIdIdx));

            if (profileData.getTempFirstName().equals(profileData.getTempNumber())) {
                contactsWithNoName.add(profileData);
            } else {
                if (lastDisplayName.equals(profileData.getTempFirstName()) && lastRawId.equals
                        (profileData.getTempRawId())) {
                } else {
                    arrayListPhoneBookContacts.add(profileData);
                    lastDisplayName = profileData.getTempFirstName();
                    lastRawId = profileData.getTempRawId();
                }
            }
        }
        arrayListPhoneBookContacts.addAll(contactsWithNoName);
    }

    private void populateRecyclerView() {
        /*relativeRootFavourite.setVisibility(View.VISIBLE);
        layoutEmptyView.setVisibility(View.GONE);*/
        if (arrayListPhoneBookContacts.size() > 0) {

            /*allContactListAdapter = new AllContactListAdapter(this,
                    arrayListPhoneBookContacts, arrayListContactHeaders);*/
            allContactListAdapter = new AllContactAdapter(this, arrayListPhoneBookContacts,
                    arrayListContactHeaders);
            recyclerViewContactList.setAdapter(allContactListAdapter);

//            setRecyclerViewLayoutManager(recyclerViewContactList);
        } else {
            relativeRootFavourite.setVisibility(View.GONE);
            layoutEmptyView.setVisibility(View.VISIBLE);
            textEmptyView.setText("No Favourites");
        }

        getRcpDetail();
        /*TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        ArrayList<String> arrayListIds = tableProfileMaster.getAllRcpId();
        for (int i = 0; i < arrayListPhoneBookContacts.size(); i++) {
            if (arrayListPhoneBookContacts.get(i) instanceof ProfileData) {
                if (arrayListIds.contains(((ProfileData) arrayListPhoneBookContacts.get
                        (i)).getLocalPhoneBookId())) {
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(true);
                  *//*  String name = tableProfileMaster.getNameFromRawId(((ProfileData)
                            arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId());
                    ((ProfileData) arrayListPhoneBookContacts.get(i))
                            .setTempRcpName(name);*//*
                    ArrayList<UserProfile> userProfiles = new ArrayList<>();
                    userProfiles.addAll(tableProfileMaster.getProfileDetailsFromRawId((
                            (ProfileData) arrayListPhoneBookContacts.get(i)).getLocalPhoneBookId
                            ()));
                    String name = "0";
                    String rcpID = "0";
                    if (userProfiles.size() > 1) {
                        for (int j = 0; j < userProfiles.size();
                             j++) {
                            if (name.equalsIgnoreCase("0")) {
                                name = userProfiles.get(j).getPmRcpId();
                            } else {
                                name = name + "," + userProfiles.get(j).getPmRcpId();
                            }
                        }
                    } else if (userProfiles.size() == 1) {
                        name = userProfiles.get(0).getPmFirstName() + " " + userProfiles.get(0)
                                .getPmLastName();
                        rcpID = userProfiles.get(0).getPmRcpId();
                    }
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpName(name);
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempRcpId(rcpID);
                } else {
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(false);
                }
                final int finalI = i;
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        allContactListAdapter.notifyItemChanged(finalI);
                    }
                });
            }
        }*/

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
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);
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

    /*public AllContactListAdapter getAllContactListAdapter() {
        return allContactListAdapter;
    }*/

    public AllContactAdapter getAllContactListAdapter() {
        return allContactListAdapter;
    }

    public ArrayList<Object> getArrayListPhoneBookContacts() {
        return arrayListPhoneBookContacts;
    }

    public RecyclerView getRecyclerViewContactList() {
        return recyclerViewContactList;
    }

    //</editor-fold>
}
