package com.rawalinfocom.rcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.BlockedContactsListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.ProfileMenuOptionDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlockContactListActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_right_right)
    ImageView imageRightRight;
    @BindView(R.id.ripple_action_right_right)
    RippleView rippleActionRightRight;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_total_contacts)
    TextView textTotalContacts;
    @BindView(R.id.recycler_block_contacts)
    RecyclerView recyclerBlockContacts;
    @BindView(R.id.main_content)
    RelativeLayout mainContent;
    @BindView(R.id.relative_block_contact_list)
    RelativeLayout relativeBlockContactList;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    ArrayList<CallLogType> listOfBlockContact;
    @BindView(R.id.no_record_to_display)
    TextView textNoRecordToDisplay;
    BlockedContactsListAdapter blockedContactsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_contact_list);
        ButterKnife.bind(this);
        registerReceiver();
        init();
        setToolBarText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_right:
                ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(getString(R.string
                        .unblock_All)));
                ProfileMenuOptionDialog profileMenuOptionDialog = new ProfileMenuOptionDialog(this,
                        arrayList, " ", 0, false, listOfBlockContact, "", "", "", "", "");
                profileMenuOptionDialog.showDialog();

                break;
        }
    }

    private void registerReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_UNBLOCK

        );
        localBroadcastManager.registerReceiver(localBroadcastReceiverUnBlock, intentFilter);
    }

    private void unRegisterReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiverUnBlock);
    }

    private void setToolBarText() {
        textToolbarTitle.setText(getResources().getString(R.string.block_action_title));
    }

    private void init() {

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightRight.setOnRippleCompleteListener(this);

        listOfBlockContact = new ArrayList<>();
        makeListToPass();
        setAdapter();
    }

    private void setAdapter() {
        if (listOfBlockContact != null && listOfBlockContact.size() > 0) {
            textNoRecordToDisplay.setVisibility(View.GONE);
            recyclerBlockContacts.setVisibility(View.VISIBLE);
            textTotalContacts.setVisibility(View.VISIBLE);
            blockedContactsListAdapter = new
                    BlockedContactsListAdapter(this, listOfBlockContact);
            recyclerBlockContacts.setAdapter(blockedContactsListAdapter);
            setRecyclerViewLayoutManager(recyclerBlockContacts);
        } else {
            textNoRecordToDisplay.setVisibility(View.VISIBLE);
            recyclerBlockContacts.setVisibility(View.GONE);
            textTotalContacts.setVisibility(View.GONE);
        }

    }

    private void makeListToPass() {
        if (Utils.getHashMapPreferenceForBlock(this, AppConstants
                .PREF_BLOCK_CONTACT_LIST) != null) {
            HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                    Utils.getHashMapPreferenceForBlock(this, AppConstants.PREF_BLOCK_CONTACT_LIST);
            String hashKey = "";
            if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                for (String key : blockProfileHashMapList.keySet()) {
                    hashKey = key;
                    if (blockProfileHashMapList.containsKey(hashKey)) {
                        listOfBlockContact.addAll(blockProfileHashMapList.get(hashKey));
                    }
                }
            }

            if (listOfBlockContact != null && listOfBlockContact.size() > 0) {
                int count = listOfBlockContact.size();
                textTotalContacts.setVisibility(View.VISIBLE);
                textTotalContacts.setText(String.format(Locale.getDefault(), "%d " + getString(R
                        .string.contacts), count));
            }
        }
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    private BroadcastReceiver localBroadcastReceiverUnBlock = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.i("CallLogFragment", "onReceive() of LocalBroadcast");
            listOfBlockContact = new ArrayList<>();
            makeListToPass();
            setAdapter();

            /*if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                    .PREF_BLOCK_CONTACT_LIST) != null) {
                HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                        Utils.getHashMapPreferenceForBlock(context, AppConstants
                        .PREF_BLOCK_CONTACT_LIST);
                String hashKey = "";
                if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                    for (String key : blockProfileHashMapList.keySet()) {
                        hashKey = key;
                        for(int i=0; i< listOfBlockContact.size(); i++){
                            CallLogType callLogType =  listOfBlockContact.get(i);
                            String uniqueKey  = callLogType.getUniqueContactId();
                            if(uniqueKey.equalsIgnoreCase(hashKey)){

                            }else{
                                listOfBlockContact.remove(callLogType);
                            }
                        }
                    }
                }else{
                    textNoRecordToDisplay.setVisibility(View.VISIBLE);
                    recyclerBlockContacts.setVisibility(View.GONE);
                    textTotalContacts.setVisibility(View.GONE);
                }
                if(listOfBlockContact.size()>0){
                    int count =  listOfBlockContact.size();
                    textTotalContacts.setText(count + " Contacts");
                    blockedContactsListAdapter.notifyDataSetChanged();
                }


            }*/

        }
    };
}
