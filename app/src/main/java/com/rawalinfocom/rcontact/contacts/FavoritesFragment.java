package com.rawalinfocom.rcontact.contacts;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LongSparseArray;
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
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
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
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class FavoritesFragment extends BaseFragment implements LoaderManager
        .LoaderCallbacks<Cursor>, WsResponseListener {

    @BindView(R.id.progress_favorite_contact)
    ProgressWheel progressFavoriteContact;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_favourite)
    RelativeLayout relativeRootFavourite;
    @BindView(R.id.layout_empty_view)
    LinearLayout layoutEmptyView;
    LongSparseArray<ProfileData> array = new LongSparseArray<>();

    AllContactAdapter allContactListAdapter;
    ArrayList<ProfileData> arrayListUserContact = new ArrayList<>();
    ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<String> arrayListContactHeaders;
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    Unbinder unbinder;

    private View rootView;
    private boolean isReload = false;
    RContactApplication rContactApplication;
    MaterialDialog callConfirmationDialog, permissionConfirmationDialog;
    public String callNumber = "";

    boolean isFromSettings = false;
    int settingRequestPermission = 0;

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
            if (rContactApplication.getFavouriteStatus() == RContactApplication.FAVOURITE_REMOVED
                    || rContactApplication.getFavouriteStatus() == RContactApplication
                    .FAVOURITE_ADDED) {
                arrayListContactHeaders = new ArrayList<>();
                arrayListPhoneBookContacts = new ArrayList<>();
                arrayListUserContact = new ArrayList<>();
                array = new LongSparseArray<>();
                isReload = false;
            } else {
                isReload = true;
            }
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
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (allContactListAdapter != null && rContactApplication.getFavouriteStatus() ==
                RContactApplication.FAVOURITE_REMOVED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (allContactListAdapter.getListClickedPosition() != -1) {
                        arrayListPhoneBookContacts.remove(allContactListAdapter
                                .getListClickedPosition());
                        allContactListAdapter.notifyItemRemoved(allContactListAdapter
                                .getListClickedPosition());
                        rContactApplication.setFavouriteStatus(RContactApplication
                                .FAVOURITE_UNMODIFIED);
                    }
                }
            }, 500);
        }
        if (isFromSettings) {
            isFromSettings = false;
            if (settingRequestPermission == AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission
                        .READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    if (!isReload) {
                        init();
                    }
                }
            }
            /*else if (settingRequestPermission == AppConstants
                    .MY_PERMISSIONS_REQUEST_READ_CONTACTS) {

            }*/
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
        //Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Uri uri = ContactsContract.Data.CONTENT_URI;
//        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
//                ContactsContract.Data.LOOKUP_KEY, ContactsContract.PhoneLookup
// .PHOTO_THUMBNAIL_URI,
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract
//                .CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
//                ContactsContract.Contacts._ID};
        Set<String> set = new HashSet<>();
        set.add(ContactsContract.Data.MIMETYPE);
        set.add(ContactsContract.Data.CONTACT_ID);
        set.add(ContactsContract.CommonDataKinds.Phone.NUMBER);
//        set.add(ContactsContract.CommonDataKinds.Phone.TYPE);
//        set.add(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
        set.add(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
//        set.add(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
//        set.add(ContactsContract.CommonDataKinds.StructuredName.PREFIX);
//        set.add(ContactsContract.CommonDataKinds.StructuredName.SUFFIX);
//        set.add(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME);
        set.add(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);
        set.add(ContactsContract.Contacts.PHOTO_ID);
//        set.add(ContactsContract.Contacts.LOOKUP_KEY);
        set.add(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID);
        String[] projection = set.toArray(new String[0]);

//        String selection = "starred = ?";
//        String[] selectionArgs = new String[]{"1"};
//        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
//        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
//        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?) and starred = ?";
        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)" +
                " and starred = ?" +
                " and " + ContactsContract.Contacts.HAS_PHONE_NUMBER + " > 0" +
                " and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " in (" + AppConstants
                .CONTACT_STORAGES + ")";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                "1"
        };
//        String sortOrder = ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";
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
        rContactApplication.setFavouriteStatus(RContactApplication.FAVOURITE_UNMODIFIED);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
//            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == AppConstants.REQUEST_CODE_PROFILE_DETAIL && resultCode ==
                    RESULT_OK) {
                if (OptionMenuDialog.IS_CONTACT_DELETED) {
                    OptionMenuDialog.IS_CONTACT_DELETED = false;
                    /*arrayListPhoneBookContacts.remove(allContactListAdapter
                            .getListClickedPosition());
                    allContactListAdapter.notifyItemRemoved(allContactListAdapter
                            .getListClickedPosition());
                    rContactApplication.setArrayListAllPhoneBookContacts
                            (arrayListPhoneBookContacts);
                    RContactsFragment.arrayListRContact = null;*/
                }
               /* Toast.makeText(getActivity(), "Called: " + allContactListAdapter
                        .getListClickedPosition(), Toast.LENGTH_SHORT).show();*/
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null && getActivity() != null) {

            // <editor-fold desc="REQ_SEND_INVITATION">
            if (serviceType.contains(WsConstants.REQ_SEND_INVITATION)) {
                WsResponseObject inviteContactResponse = (WsResponseObject) data;
                if (inviteContactResponse != null && StringUtils.equalsIgnoreCase
                        (inviteContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    Utils.showSuccessSnackBar(getActivity(), relativeRootFavourite,
                            getActivity().getString(R.string.invitation_sent));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    // Permission Granted
                  /*  Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                  callNumber));
                    startActivity(intent);*/
                    Utils.callIntent(getActivity(), callNumber);
                } else {
                    // Permission Denied
                    showPermissionConfirmationDialog(AppConstants
                            .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                }
            }
            break;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

//        textEmptyView = ButterKnife.findById(layoutEmptyView, R.id.text_empty_view);

        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));
        textTotalContacts.setVisibility(View.GONE);
        layoutEmptyView.setVisibility(View.GONE);

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
        if (rContactApplication.getArrayListFavPhoneBookContacts() != null) {
            relativeRootFavourite.setVisibility(View.VISIBLE);
            if (rContactApplication.getArrayListFavPhoneBookContacts().size() <= 0) {
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
        } else {
            relativeRootFavourite.setVisibility(View.GONE);
            layoutEmptyView.setVisibility(View.VISIBLE);
            textEmptyView.setVisibility(View.VISIBLE);
            textEmptyView.setText(getString(R.string.str_no_favorite));
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

                    if (!actionNumber.startsWith("+91")) {
                        callNumber = "+91" + actionNumber;
                    } else {
                        callNumber = actionNumber;
                    }

                    showCallConfirmationDialog();
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
                 /* Disable swiping in multiple RC case */
                if (viewHolder instanceof AllContactAdapter.AllContactViewHolder) {
                    if (((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .recyclerViewMultipleRc.getVisibility() == View.VISIBLE) {
                        return 0;
                    }
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
                        (i)).getRawContactId())) {
                    ((ProfileData) arrayListPhoneBookContacts.get(i)).setTempIsRcp(true);
                    ArrayList<UserProfile> userProfiles = new ArrayList<>();
                    userProfiles.addAll(tableProfileMaster.getProfileDetailsFromRawId((
                            (ProfileData) arrayListPhoneBookContacts.get(i)).getRawContactId()));
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
        final int mimeTypeIdx = data.getColumnIndex(ContactsContract.Data.MIMETYPE);
        final int idIdx = data.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        final int phoneIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        final int givenNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.DISPLAY_NAME);
        final int photoURIIdx = data.getColumnIndex(ContactsContract.PhoneLookup
                .PHOTO_THUMBNAIL_URI);

        final int rawIdIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone
                .RAW_CONTACT_ID);


        while (data.moveToNext()) {

            long id = data.getLong(idIdx);
            ProfileData profileData = array.get(id);

            if (profileData == null) {
                profileData = new ProfileData();
                array.put(id, profileData);
                arrayListUserContact.add(profileData);
            }

            profileData.setLocalPhoneBookId(data.getString(rawIdIdx));
            profileData.setRawContactId(data.getString(rawIdIdx));

            switch (data.getString(mimeTypeIdx)) {
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    profileData.setTempNumber(data.getString(phoneIdx));
                    profileData.setProfileUrl(data.getString(photoURIIdx));
                    break;
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    profileData.setTempFirstName(data.getString(givenNameIdx));
                    profileData.setName(StringUtils.defaultString(data.getString
                            (givenNameIdx)));
//                    profileData.setTempLastName(data.getString(familyNameIdx));
//                    profileData.setTempPrefix(data.getString(prefixNameIdx));
//                    profileData.setTempSufix(data.getString(suffixNameIdx));
//                    profileData.setTempMiddleName(data.getString(middleNameIdx));
//                    if (StringUtils.length(data.getString(familyNameIdx)) > 0) {
//                        profileData.setName(data.getString(givenNameIdx));
//                    } else {
//                        profileData.setName(StringUtils.defaultString(data.getString
//                                (givenNameIdx)));
//                    }
                    break;
            }
        }

        if (arrayListUserContact.size() > 0) {
            for (int i = 0; i < arrayListUserContact.size(); i++) {
                arrayListPhoneBookContacts.add(arrayListUserContact.get(i));
            }
//        final int phoneIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//        final int givenNameIdx = data.getColumnIndex(ContactsContract.CommonDataKinds
//                .Phone.DISPLAY_NAME);
//        final int photoURIIdx = data.getColumnIndex(ContactsContract.PhoneLookup
//                .PHOTO_THUMBNAIL_URI);
//        final int lookUpKeyIdx = data.getColumnIndex(ContactsContract.Data.LOOKUP_KEY);
//        final int rawIdIdx = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone
//                .RAW_CONTACT_ID);
//
//        ArrayList contactsWithNoName = new ArrayList<>();
//        String lastDisplayName = "XXX", lastRawId = "XXX";
//
//        while (data.moveToNext()) {
//            ProfileData profileData;
//            profileData = new ProfileData();
//            profileData.setTempFirstName(data.getString(givenNameIdx));
//            profileData.setTempNumber(data.getString(phoneIdx));
//            profileData.setProfileUrl(data.getString(photoURIIdx));
//            profileData.setLocalPhoneBookId(data.getString(lookUpKeyIdx));
//            profileData.setRawContactId(data.getString(rawIdIdx));
//            profileData.setTempRawId(data.getString(rawIdIdx));
//
//            if (profileData.getTempFirstName().equals(profileData.getTempNumber())) {
//                contactsWithNoName.add(profileData);
//            } else {
//                if (lastDisplayName.equals(profileData.getTempFirstName()) && lastRawId.equals
//                        (profileData.getTempRawId())) {
//                } else {
//                    arrayListPhoneBookContacts.add(profileData);
//                    lastDisplayName = profileData.getTempFirstName();
//                    lastRawId = profileData.getTempRawId();
//                }
//            }
//        }
//        arrayListPhoneBookContacts.addAll(contactsWithNoName);
        }
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
            textEmptyView.setText(getString(R.string.str_no_favorite));
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

    private void showCallConfirmationDialog() {

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
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest
                                .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission
                                    .CALL_PHONE}, AppConstants
                                    .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        } else {
                           /* Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                    callNumber));
                            startActivity(intent);*/
                            Utils.callIntent(getActivity(), callNumber);
                        }
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getActivity().getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getActivity().getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getActivity().getString(R.string.action_call) + " "
                + callNumber + "?");

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

    private void showPermissionConfirmationDialog(final int permissionType) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        switch (permissionType) {
                            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                                getActivity().finish();
                                break;
                            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                                break;
                        }
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        isFromSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getActivity().getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        switch (permissionType) {
                            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                                settingRequestPermission = AppConstants
                                        .MY_PERMISSIONS_REQUEST_READ_CONTACTS;
                                break;
                            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                                settingRequestPermission = AppConstants
                                        .MY_PERMISSIONS_REQUEST_PHONE_CALL;
                                break;
                        }
                        getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                        break;
                }
            }
        };

        String message = "";
        switch (permissionType) {
            case AppConstants.MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                message = getString(R.string.contact_read_permission);
                break;
            case AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL:
                message = getString(R.string.calling_permission);
                break;
        }

        permissionConfirmationDialog = new MaterialDialog(getActivity(), cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        permissionConfirmationDialog.setRightButtonText(getString(R.string.action_ok));
        permissionConfirmationDialog.setDialogBody(message);

        permissionConfirmationDialog.showDialog();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //</editor-fold>
}
