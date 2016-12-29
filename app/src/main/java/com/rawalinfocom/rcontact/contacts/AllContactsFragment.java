package com.rawalinfocom.rcontact.contacts;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.ColorBubble
        .ColorGroupSectionTitleIndicator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.vertical
        .VerticalRecyclerViewFastScroller;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationRelationship;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;
import com.rawalinfocom.rcontact.services.ContactIdFetchService;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllContactsFragment extends BaseFragment implements WsResponseListener {

    private final int CONTACT_CHUNK = 10;

    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_all_contacts)
    RelativeLayout relativeRootAllContacts;
    @BindView(R.id.progress_all_contact)
    ProgressWheel progressAllContact;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;
    @BindView(R.id.scroller_all_contact)
    VerticalRecyclerViewFastScroller scrollerAllContact;
    @BindView(R.id.title_indicator)
    ColorGroupSectionTitleIndicator titleIndicator;

    ArrayList<Object> arrayListPhoneBookContacts;
    ArrayList<String> arrayListContactHeaders;
    ArrayList<ProfileData> arrayListUserContact;
    ArrayList<String> arrayListContactId;
    ArrayList<String> arrayListContactNumbers;
    ArrayList<String> arrayListContactEmails;

    MaterialDialog callConfirmationDialog;

    PhoneBookContacts phoneBookContacts;

//    DatabaseHandler databaseHandler;

    AllContactListAdapter allContactListAdapter;

    UserProfile meProfile;

    //<editor-fold desc="Constructors">
    public AllContactsFragment() {
        // Required empty public constructor

    }

    public static AllContactsFragment newInstance() {
        return new AllContactsFragment();
    }
    //</editor-fold>

    //<editor-fold desc="Override Methods">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        databaseHandler = ((BaseActivity) getActivity()).databaseHandler;
        arrayListPhoneBookContacts = new ArrayList<>();
        arrayListContactHeaders = new ArrayList<>();

        arrayListContactHeaders.add(" ");

        meProfile = ((BaseActivity) getActivity()).getUserProfile();

        arrayListPhoneBookContacts.add("My Profile");
        ProfileData myProfileData = new ProfileData();

        ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();
        ProfileDataOperation myOperation = new ProfileDataOperation();
        myOperation.setPbNameFirst(meProfile.getPmFirstName());
        myOperation.setPbNameLast(meProfile.getPmLastName());

        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
        ProfileDataOperationPhoneNumber myNumber = new ProfileDataOperationPhoneNumber();
        myNumber.setPhoneNumber(meProfile.getMobileNumber());
        arrayListPhoneNumber.add(myNumber);
        myOperation.setPbPhoneNumber(arrayListPhoneNumber);

        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
        myOperation.setPbEmailId(arrayListEmail);

        arrayListOperation.add(myOperation);
        myProfileData.setOperation(arrayListOperation);
        arrayListPhoneBookContacts.add(myProfileData);

        phoneBookContacts = new PhoneBookContacts(getActivity());

    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      /*  if (view != null) {
            //this will prevent the fragment from re-inflating(when you come back from B)
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        } else {
            //inflate the view and do what you done in onCreateView()
            init();
        }*/
        init();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null && getActivity() != null) {

            //<editor-fold desc="REQ_UPLOAD_CONTACTS">

            if (serviceType.contains(WsConstants.REQ_UPLOAD_CONTACTS)) {
                WsResponseObject uploadContactResponse = (WsResponseObject) data;
                progressAllContact.setVisibility(View.GONE);
                if (uploadContactResponse != null && StringUtils.equalsIgnoreCase
                        (uploadContactResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    /* Save synced data details in Preference */
                    String previouslySyncedData = (StringUtils.split(serviceType, "_"))[1];
                    int nextNumber = Integer.parseInt(StringUtils.defaultString
                            (previouslySyncedData, "0")) + CONTACT_CHUNK;
                    Utils.setIntegerPreference(getActivity(), AppConstants.PREF_SYNCED_CONTACTS,
                            nextNumber);

                    if (!Utils.isArraylistNullOrEmpty(uploadContactResponse
                            .getArrayListUserRcProfile())) {

                        /* Store Unique Contacts to ProfileMobileMapping */
                        storeToMobileMapping(uploadContactResponse.getArrayListUserRcProfile());

                        /* Store Unique Emails to ProfileEmailMapping */
                        storeToEmailMapping(uploadContactResponse.getArrayListUserRcProfile());

                        /* Store Profile Details to respective Table */
                        storeProfileDataToDb(uploadContactResponse.getArrayListUserRcProfile());

                    } else {

                        /* Store Unique Contacts to ProfileMobileMapping */
                        storeToMobileMapping(null);

                        /* Store Unique Contacts to ProfileMobileMapping */
                        storeToEmailMapping(null);

                    }

                 /* Call uploadContact api if there is more data to sync */
                    if (nextNumber < arrayListContactId.size()) {
                        phoneBookOperations();
                    } else {
                        Utils.showSuccessSnackbar(getActivity(), relativeRootAllContacts, "All " +
                                "Contact Synced");
                    }

                    /* Populate recycler view */
                    populateRecyclerView();

                } else {
                    if (uploadContactResponse != null) {
                        Log.e("error response", uploadContactResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "uploadContactResponse null");
                        Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            progressAllContact.setVisibility(View.GONE);
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(cursorListReceiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        allContactListAdapter = null;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        // Connect the recycler to the scroller (to let the scroller scroll the list)
        scrollerAllContact.setRecyclerView(recyclerViewContactList);

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        recyclerViewContactList.setOnScrollListener(scrollerAllContact.getOnScrollListener());

        // Connect the section indicator to the scroller
        scrollerAllContact.setSectionIndicator(titleIndicator);
//        titleIndicator.setTitleText("A");

        setRecyclerViewLayoutManager(recyclerViewContactList);
//        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(getActivity-()));

        initSwipe();

        ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity(),
                AppConstants.PREF_CONTACT_ID_SET);
        if (arrayListContactIds != null) {
            arrayListContactId = new ArrayList<>(arrayListContactIds);
            phoneBookOperations();
        } else {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(cursorListReceiver,
                    new IntentFilter(AppConstants.ACTION_CONTACT_FETCH));
            Intent contactIdFetchService = new Intent(getActivity(), ContactIdFetchService.class);
            getActivity().startService(contactIdFetchService);
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

    private void storeToMobileMapping(ArrayList<ProfileDataOperation> profileData) {
        if (!Utils.isArraylistNullOrEmpty(arrayListContactNumbers)) {
            TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                    (getDatabaseHandler());
            ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new ArrayList<>();
            for (int i = 0; i < arrayListContactNumbers.size(); i++) {
                if (!tableProfileMobileMapping.getIsMobileNumberExists(arrayListContactNumbers
                        .get(i))) {

                    ProfileMobileMapping profileMobileMapping = new
                            ProfileMobileMapping();
                    profileMobileMapping.setMpmMobileNumber(arrayListContactNumbers
                            .get(i));
                    profileMobileMapping.setMpmIsRcp("0");
                    if (!Utils.isArraylistNullOrEmpty(profileData)) {
                        for (int j = 0; j < profileData.size(); j++) {
                            if (StringUtils.equalsIgnoreCase(arrayListContactNumbers.get(i),
                                    profileData.get(j).getVerifiedMobileNumber())) {
                                profileMobileMapping.setMpmCloudMnmId(profileData.get(j)
                                        .getMnmCloudId());
                                profileMobileMapping.setMpmCloudPmId(profileData.get(j)
                                        .getRcpPmId());
                                profileMobileMapping.setMpmIsRcp("1");
                            }
                        }
                    }
                    arrayListProfileMobileMapping.add(profileMobileMapping);
                }
            }
            tableProfileMobileMapping.addArrayProfileMobileMapping
                    (arrayListProfileMobileMapping);
        }


    }

    private void storeToEmailMapping(ArrayList<ProfileDataOperation> profileData) {
        if (!Utils.isArraylistNullOrEmpty(arrayListContactEmails)) {
            TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping
                    (getDatabaseHandler());
            ArrayList<ProfileEmailMapping> arrayListProfileEmailMapping = new ArrayList<>();
            for (int i = 0; i < arrayListContactEmails.size(); i++) {
                if (!tableProfileEmailMapping.getIsEmailIdExists(arrayListContactEmails.get(i))) {

                    ProfileEmailMapping profileEmailMapping = new ProfileEmailMapping();
                    profileEmailMapping.setEpmEmailId(arrayListContactEmails.get(i));
                    profileEmailMapping.setEpmIsRcp("0");
                    if (!Utils.isArraylistNullOrEmpty(profileData)) {
                        for (int j = 0; j < profileData.size(); j++) {
                            if (StringUtils.equalsIgnoreCase(arrayListContactEmails.get(i),
                                    profileData.get(j).getVerifiedEmailAddress())) {
                                profileEmailMapping.setEpmCloudEmId(profileData.get(j)
                                        .getEmCloudId());
                                profileEmailMapping.setEpmCloudPmId(profileData.get(j)
                                        .getRcpPmId());
                                profileEmailMapping.setEpmIsRcp("1");
                            }
                        }
                    }

                    arrayListProfileEmailMapping.add(profileEmailMapping);
                }
            }
            tableProfileEmailMapping.addArrayProfileEmailMapping(arrayListProfileEmailMapping);
        }


    }

    private void storeProfileDataToDb(ArrayList<ProfileDataOperation> profileData) {

        // Basic Profile Data
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());

        ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
        for (int i = 0; i < profileData.size(); i++) {
            UserProfile userProfile = new UserProfile();
            userProfile.setPmSuffix(profileData.get(i).getPbNameSuffix());
            userProfile.setPmPrefix(profileData.get(i).getPbNamePrefix());
            userProfile.setPmFirstName(profileData.get(i).getPbNameFirst());
            userProfile.setPmMiddleName(profileData.get(i).getPbNameMiddle());
            userProfile.setPmLastName(profileData.get(i).getPbNameLast());
            userProfile.setPmPhoneticFirstName(profileData.get(i).getPbPhoneticNameFirst());
            userProfile.setPmPhoneticMiddleName(profileData.get(i).getPbPhoneticNameMiddle());
            userProfile.setPmPhoneticLastName(profileData.get(i).getPbPhoneticNameLast());
            userProfile.setPmIsFavourite(profileData.get(i).getIsFavourite());
            userProfile.setPmNotes(profileData.get(i).getPbNote());
            userProfile.setPmNickName(profileData.get(i).getPbNickname());
            userProfile.setPmRcpId(profileData.get(i).getRcpPmId());
            userProfile.setPmNosqlMasterId(profileData.get(i).getNoSqlMasterId());

            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = profileData.get(i)
                    .getPbPhoneNumber();
            ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
            for (int j = 0; j < arrayListPhoneNumber.size(); j++) {
                MobileNumber mobileNumber = new MobileNumber();
                mobileNumber.setMnmMobileNumber(arrayListPhoneNumber.get(j).getPhoneNumber());
                mobileNumber.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
//                arrayListPhoneNumber.get(j).
                arrayListMobileNumber.add(mobileNumber);
            }

            TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
            tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);

            ArrayList<ProfileDataOperationEmail> arrayListEmailId = profileData.get(i)
                    .getPbEmailId();
            ArrayList<Email> arrayListEmail = new ArrayList<>();
            for (int j = 0; j < arrayListEmailId.size(); j++) {
                Email email = new Email();
                email.setEmEmailAddress(arrayListEmailId.get(j).getEmEmailId());
                email.setRcProfileMasterPmId(profileData.get(i).getRcpPmId());
                arrayListEmail.add(email);
            }

            TableEmailMaster tableEmailMaster = new TableEmailMaster(getDatabaseHandler());
            tableEmailMaster.addArrayEmail(arrayListEmail);

            arrayListUserProfile.add(userProfile);
        }

        tableProfileMaster.addArrayProfile(arrayListUserProfile);

    }

    private void populateRecyclerView() {

        if (allContactListAdapter == null) {
            allContactListAdapter = new AllContactListAdapter(this,
                    arrayListPhoneBookContacts, arrayListContactHeaders);
            recyclerViewContactList.setAdapter(allContactListAdapter);

            setRecyclerViewLayoutManager(recyclerViewContactList);

        } else {
            allContactListAdapter.notifyDataSetChanged();
        }

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

    //</editor-fold>

    //<editor-fold desc="Local Broadcast Receiver">
    private BroadcastReceiver cursorListReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(AppConstants.EXTRA_LOCAL_BROADCAST_MESSAGE);
            if (StringUtils.equals(message, WsConstants.RESPONSE_STATUS_TRUE)) {
                /*HashSet<String> retrievedContactIdSet = (HashSet<String>) Utils
                        .getStringSetPreference(getActivity(), AppConstants
                                .PREF_CONTACT_ID_SET);*/
                ArrayList<String> arrayListContactIds = Utils.getArrayListPreference(getActivity(),
                        AppConstants.PREF_CONTACT_ID_SET);
                if (arrayListContactIds != null) {
                    arrayListContactId = new ArrayList<>(arrayListContactIds);
                    phoneBookOperations();
                } else {
                    Log.e("Local Broadcast Receiver onReceive: ", "Error while Retriving Ids!");
                }
            } else {
                Log.e("Local Broadcast Receiver onReceive: ", "Error while Retriving Ids!");
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="Phone book Data Cursor">

    private void phoneBookOperations() {
        arrayListUserContact = new ArrayList<>();
        arrayListContactNumbers = new ArrayList<>();
        arrayListContactEmails = new ArrayList<>();
        int previouslySyncedData = Utils.getIntegerPreference(getActivity(), AppConstants
                .PREF_SYNCED_CONTACTS, 0);
        int previousTo = previouslySyncedData + CONTACT_CHUNK;
        if (previousTo > arrayListContactId.size()) {
            previousTo = arrayListContactId.size();
        }

        int forFrom, forTo;

        if (previouslySyncedData < previousTo) {
            forFrom = previouslySyncedData;
            forTo = previousTo;
        } else {
            forFrom = 0;
            forTo = arrayListContactId.size();
        }

        for (int i = forFrom; i < forTo; i++) {

            ProfileData profileData = new ProfileData();

            String rawId = arrayListContactId.get(i);

            profileData.setLocalPhoneBookId(rawId);

            /* profileData.setGivenName(contactNameCursor.getString(contactNameCursor
            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));*/

            ProfileDataOperation operation = new ProfileDataOperation();

            //<editor-fold desc="Structured Name">
            Cursor contactStructuredNameCursor = phoneBookContacts.getStructuredName(rawId);
            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();

            if (contactStructuredNameCursor != null && contactStructuredNameCursor.getCount() > 0) {

                /*   operation.setIsFavourite(contactNameCursor.getString(contactNameCursor
                .getColumnIndex(ContactsContract.Contacts.STARRED)));*/
                while (contactStructuredNameCursor.moveToNext()) {

                    operation.setFlag("1");
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
                    operation.setPbPhoneticNameFirst(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME)));
                    operation.setPbPhoneticNameMiddle(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.PHONETIC_MIDDLE_NAME)));
                    operation.setPbPhoneticNameLast(contactStructuredNameCursor.getString
                            (contactStructuredNameCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME)));

                }
                contactStructuredNameCursor.close();
            }
//                arrayListOperation.add(operation);
            //</editor-fold>

            // <editor-fold desc="Starred Contact">
            Cursor starredContactCursor = phoneBookContacts.getStarredStatus(rawId);

            if (starredContactCursor != null && starredContactCursor.getCount() > 0) {


                while (starredContactCursor.moveToNext()) {

                    operation.setIsFavourite(starredContactCursor.getString(starredContactCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED)));

                }
                starredContactCursor.close();
            }
            //</editor-fold>

            //<editor-fold desc="Contact Number">
            Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(rawId);
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();

            if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                int numberCount = 0;
                while (contactNumberCursor.moveToNext()) {

                    ProfileDataOperationPhoneNumber phoneNumber = new
                            ProfileDataOperationPhoneNumber();

                    phoneNumber.setPhoneId(++numberCount);
                    phoneNumber.setPhoneNumber(Utils.getFormattedNumber(getActivity(),
                            contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                    phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                            (contactNumberCursor.getInt
                                    (contactNumberCursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Phone.TYPE))));
                    phoneNumber.setPhonePublic(1);

                    arrayListPhoneNumber.add(phoneNumber);

                    if (!arrayListContactNumbers.contains(Utils.getFormattedNumber(getActivity(),
                            phoneNumber.getPhoneNumber()))) {
                        arrayListContactNumbers.add(Utils.getFormattedNumber(getActivity(),
                                phoneNumber.getPhoneNumber()));
                    }

                }
                contactNumberCursor.close();
            }
            operation.setPbPhoneNumber(arrayListPhoneNumber);
            //</editor-fold>

            //<editor-fold desc="Email Id">
            Cursor contactEmailCursor = phoneBookContacts.getContactEmail(rawId);
            ArrayList<ProfileDataOperationEmail> arrayListEmailId = new ArrayList<>();

            if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                int emailCount = 0;
                while (contactEmailCursor.moveToNext()) {

                    ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();

                    emailId.setEmId(++emailCount);
                    emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                    emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                            contactEmailCursor.getInt
                                    (contactEmailCursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Email.TYPE))));
                    emailId.setEmPublic(1);

                    arrayListEmailId.add(emailId);
                    arrayListContactEmails.add(emailId.getEmEmailId());
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
            ArrayList<String> arrayListWebsite = new ArrayList<>();

            if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
                while (contactWebsiteCursor.moveToNext()) {

                    String website = contactWebsiteCursor.getString(contactWebsiteCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));

                    arrayListWebsite.add(website);

                }
                contactWebsiteCursor.close();
            }

            operation.setPbWebAddress(arrayListWebsite);
            //</editor-fold>

            //<editor-fold desc="Organization">
            Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(rawId);
            ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new
                    ArrayList<>();

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
                    organization.setOrgDepartment(contactOrganizationCursor.getString
                            (contactOrganizationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Organization.DEPARTMENT)));
                    organization.setOrgType(phoneBookContacts.getOrganizationType
                            (contactOrganizationCursor,
                                    contactOrganizationCursor.getInt((contactOrganizationCursor
                                            .getColumnIndex(ContactsContract.CommonDataKinds
                                                    .Organization.TYPE)))));
                    organization.setOrgJobDescription(contactOrganizationCursor.getString
                            (contactOrganizationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                    organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                            (contactOrganizationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Organization.OFFICE_LOCATION)));

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
                    address.setCity(contactAddressCursor.getString(contactAddressCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                    .CITY)));
                    address.setCountry(contactAddressCursor.getString(contactAddressCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                    .COUNTRY)));
                    address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                    .NEIGHBORHOOD)));
                    address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                    .POSTCODE)));
                    address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                    .POBOX)));
                    address.setStreet(contactAddressCursor.getString(contactAddressCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                    .STREET)));
                    address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                            contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));

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

                    imAccount.setIMAccountType(phoneBookContacts.getImAccountType(contactImCursor,
                            contactImCursor.getInt(contactImCursor.getColumnIndex
                                    (ContactsContract.CommonDataKinds.Im.TYPE))));

                    imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                            (contactImCursor.getInt(
                                    (contactImCursor.getColumnIndex(ContactsContract.CommonDataKinds
                                            .Im.PROTOCOL)))));

                    imAccount.setIMAccountPublic("1");


                    arrayListImAccount.add(imAccount);

                }
                contactImCursor.close();
            }

            operation.setPbIMAccounts(arrayListImAccount);
            //</editor-fold>

            //<editor-fold desc="Event">
            Cursor contactEventCursor = phoneBookContacts.getContactEvent(rawId);
            ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();

            if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
                while (contactEventCursor.moveToNext()) {

                    ProfileDataOperationEvent event = new ProfileDataOperationEvent();

                    event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                            contactEventCursor
                                    .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                            .CommonDataKinds.Event.TYPE))));

                    event.setEventDate(contactEventCursor.getString(contactEventCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                    .START_DATE)));

                    event.setEventPublic("1");

                    arrayListEvent.add(event);

                }
                contactEventCursor.close();
            }

            operation.setPbEvent(arrayListEvent);
            //</editor-fold>

            //<editor-fold desc="Relation">
            Cursor contactRelationCursor = phoneBookContacts.getContactRelationShip(rawId);
            ArrayList<ProfileDataOperationRelationship> arrayListRelationship = new
                    ArrayList<>();

            if (contactRelationCursor != null && contactRelationCursor.getCount() > 0) {
                while (contactRelationCursor.moveToNext()) {

                    ProfileDataOperationRelationship relationship = new
                            ProfileDataOperationRelationship();

                    relationship.setRelationshipDetails(contactRelationCursor.getString
                            (contactRelationCursor.getColumnIndex(ContactsContract
                                    .CommonDataKinds.Relation.NAME)));
                    relationship.setRelationshipType(phoneBookContacts.getRelationType
                            (contactRelationCursor,
                                    contactRelationCursor.getInt((contactRelationCursor
                                            .getColumnIndex(ContactsContract.CommonDataKinds
                                                    .Relation
                                                    .TYPE)))));
                    relationship.setRelationshipPublic("1");

                    arrayListRelationship.add(relationship);

                }
                contactRelationCursor.close();
            }

            operation.setPbRelationship(arrayListRelationship);
            //</editor-fold>

            arrayListOperation.add(operation);
            profileData.setOperation(arrayListOperation);

            arrayListUserContact.add(profileData);

        }

        if (arrayListUserContact.size() > 0) {
//            arrayListPhoneBookContacts.addAll(arrayListUserContact);

            for (int i = 0; i < arrayListUserContact.size(); i++) {
                String headerLetter = StringUtils.upperCase(StringUtils.substring
                        (arrayListUserContact.get(i).getOperation().get(0).getPbNameFirst(), 0, 1));
                if (!arrayListPhoneBookContacts.contains(headerLetter)) {
                    arrayListContactHeaders.add(headerLetter);
                    arrayListPhoneBookContacts.add(headerLetter);
                }
                arrayListPhoneBookContacts.add(arrayListUserContact.get(i));
            }

            if (previouslySyncedData < previousTo) {
                uploadContacts(previouslySyncedData);
            } else {
                progressAllContact.setVisibility(View.GONE);
                populateRecyclerView();
            }

        } else {
            progressAllContact.setVisibility(View.GONE);
        }

    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void uploadContacts(int previouslySyncedData) {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(((BaseActivity) getActivity()).getUserPmId());
        uploadContactObject.setProfileData(arrayListUserContact);

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_UPLOAD_CONTACTS + "_" + previouslySyncedData, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_UPLOAD_CONTACTS);
        } else {
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>

}
