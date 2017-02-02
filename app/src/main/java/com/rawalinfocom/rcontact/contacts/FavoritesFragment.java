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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.ColorBubble
        .ColorGroupSectionTitleIndicator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.vertical
        .VerticalRecyclerViewFastScroller;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends BaseFragment {

    @BindView(R.id.progress_favorite_contact)
    ProgressWheel progressFavoriteContact;
    @BindView(R.id.relative_scroller)
    RelativeLayout relativeScroller;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.scroller_favorite_contact)
    VerticalRecyclerViewFastScroller scrollerFavoriteContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;

    PhoneBookContacts phoneBookContacts;

    AllContactListAdapter allContactListAdapter;

    ArrayList<ProfileData> arrayListUserContact;
    ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<String> arrayListContactHeaders;

    private View rootView;
    private boolean isReload = false;
    RContactApplication rContactApplication;

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
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
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

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        textTotalContacts.setTypeface(Utils.typefaceSemiBold(getActivity()));

        // Connect the recycler to the scroller (to let the scroller scroll the list)
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
        });

        initSwipe();

        progressFavoriteContact.setVisibility(View.GONE);

//        getFavouriteContacts();
        if (rContactApplication.getArrayListFavPhoneBookContacts().size() <= 0) {
            getFavouriteContacts();
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
                String actionNumber = StringUtils.defaultString(((AllContactListAdapter
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
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                            actionNumber));
                    startActivity(intent);
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
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    public void getFavouriteContacts() {
        phoneBookContacts = new PhoneBookContacts(getActivity());
        Cursor favouriteContactCursor = phoneBookContacts.getStarredContacts();

        ArrayList<String> arrayListFavouriteRawId = new ArrayList<>();

        if (favouriteContactCursor != null && favouriteContactCursor.getCount() > 0) {

            while (favouriteContactCursor.moveToNext()) {

                arrayListFavouriteRawId.add(favouriteContactCursor.getString
                        (favouriteContactCursor.getColumnIndex(ContactsContract.Contacts._ID)));

            }

            favouriteContactCursor.close();
        }

        arrayListUserContact = new ArrayList<>();
        arrayListPhoneBookContacts = new ArrayList<>();
        arrayListContactHeaders = new ArrayList<>();

        for (int i = 0; i < arrayListFavouriteRawId.size(); i++) {

            ProfileData profileData = new ProfileData();

            String rawId = arrayListFavouriteRawId.get(i);

            profileData.setLocalPhoneBookId(rawId);

            ProfileDataOperation operation = new ProfileDataOperation();

            //<editor-fold desc="Structured Name">
            Cursor contactStructuredNameCursor = phoneBookContacts.getStructuredName(rawId);
            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount() > 0) {

                while (contactStructuredNameCursor.moveToNext()) {

                    operation.setPbNamePrefix(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.PREFIX)));
                    operation.setPbNameFirst(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.GIVEN_NAME)));
                    operation.setPbNameMiddle(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.MIDDLE_NAME)));
                    operation.setPbNameLast(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.FAMILY_NAME)));
                    operation.setPbNameSuffix(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.SUFFIX)));

                }
                contactStructuredNameCursor.close();
            }
//                arrayListOperation.add(operation);
            //</editor-fold>

            //<editor-fold desc="Contact Number">
            Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(rawId);
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

            if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                while (contactNumberCursor.moveToNext()) {

                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();

                    phoneNumber.setPhoneNumber(contactNumberCursor.getString(contactNumberCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                    arrayListPhoneNumber.add(phoneNumber);


                }
                contactNumberCursor.close();
            }
            operation.setPbPhoneNumber(arrayListPhoneNumber);
            //</editor-fold>

            //<editor-fold desc="Email Id">
            Cursor contactEmailCursor = phoneBookContacts.getContactEmail(rawId);
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

            if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                while (contactEmailCursor.moveToNext()) {

                    ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                    emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));

                    arrayListEmailId.add(emailId);

                }
                contactEmailCursor.close();
            }
            operation.setPbEmailId(arrayListEmailId);
            //</editor-fold>

            //<editor-fold desc="Nick Name">
            Cursor contactNickNameCursor = phoneBookContacts.getContactNickName(rawId);

            if (contactNickNameCursor != null && contactNickNameCursor.getCount() > 0) {
                while (contactNickNameCursor.moveToNext()) {

                    operation.setPbNickname(contactNickNameCursor.getString
                            (contactNickNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Nickname.DATA1)));

                }
                contactNickNameCursor.close();
            }
            //</editor-fold>

            //<editor-fold desc="Note">
            Cursor contactNoteCursor = phoneBookContacts.getContactNote(rawId);

            if (contactNoteCursor != null && contactNoteCursor.getCount() > 0) {
                while (contactNoteCursor.moveToNext()) {

                    operation.setPbNote(contactNoteCursor.getString(contactNoteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Note.DATA1)));

                }
                contactNoteCursor.close();
            }
            //</editor-fold>

            //<editor-fold desc="Website">
            Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(rawId);
//            ArrayList<String> arrayListWebsite = new ArrayList<>();
            ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();

            if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
                while (contactWebsiteCursor.moveToNext()) {

                   /* String website = contactWebsiteCursor.getString(contactWebsiteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

                    arrayListWebsite.add(website);*/

                    ProfileDataOperationWebAddress webAddress = new
                            ProfileDataOperationWebAddress();

                    webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));

                   /* String website = contactWebsiteCursor.getString(contactWebsiteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));*/

//                    arrayListWebsite.add(website);
                    arrayListWebsite.add(webAddress);

                }
                contactWebsiteCursor.close();
            }

            operation.setPbWebAddress(arrayListWebsite);
            //</editor-fold>

            //<editor-fold desc="Organization">
            Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(rawId);
            ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

            if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
                while (contactOrganizationCursor.moveToNext()) {

                    ProfileDataOperationOrganization organization = new
                            ProfileDataOperationOrganization();

                    organization.setOrgName(contactOrganizationCursor.getString
                            (contactOrganizationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Organization.COMPANY)));
                    organization.setOrgJobTitle(contactOrganizationCursor.getString
                            (contactOrganizationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Organization.TITLE)));

                    arrayListOrganization.add(organization);

                }
                contactOrganizationCursor.close();
            }

            operation.setPbOrganization(arrayListOrganization);
            //</editor-fold>

            //<editor-fold desc="Address">
            Cursor contactAddressCursor = phoneBookContacts.getContactAddress(rawId);
            ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();

            if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
                while (contactAddressCursor.moveToNext()) {

                    ProfileDataOperationAddress address = new ProfileDataOperationAddress();

                    address.setFormattedAddress(contactAddressCursor.getString
                            (contactAddressCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));

                    arrayListAddress.add(address);

                }
                contactAddressCursor.close();
            }

            operation.setPbAddress(arrayListAddress);
            //</editor-fold>

            //<editor-fold desc="IM Account">
            Cursor contactImCursor = phoneBookContacts.getContactIm(rawId);
            ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();

            if (contactImCursor != null && contactImCursor.getCount() > 0) {
                while (contactImCursor.moveToNext()) {

                    ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                    imAccount.setIMAccountDetails(contactImCursor.getString(contactImCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                    arrayListImAccount.add(imAccount);

                }
                contactImCursor.close();
            }

            operation.setPbIMAccounts(arrayListImAccount);
            //</editor-fold>

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListUserContact.add(profileData);

        }

        for (int i = 0; i < arrayListUserContact.size(); i++) {
            String headerLetter = StringUtils.upperCase(StringUtils.substring
                    (arrayListUserContact.get(i).getOperation().get(0).getPbNameFirst(), 0, 1));
            headerLetter = StringUtils.length(headerLetter) > 0 ? headerLetter : "#";
            if (!arrayListPhoneBookContacts.contains(headerLetter)) {
                arrayListContactHeaders.add(headerLetter);
                arrayListPhoneBookContacts.add(headerLetter);
            }
            arrayListPhoneBookContacts.add(arrayListUserContact.get(i));
        }

        populateRecyclerView();

        textTotalContacts.setText(arrayListUserContact.size() + " Contacts");

    }

    private void populateRecyclerView() {

        allContactListAdapter = new AllContactListAdapter(this,
                arrayListPhoneBookContacts, arrayListContactHeaders);
        recyclerViewContactList.setAdapter(allContactListAdapter);

        setRecyclerViewLayoutManager(recyclerViewContactList);

    }

    public AllContactListAdapter getAllContactListAdapter() {
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
