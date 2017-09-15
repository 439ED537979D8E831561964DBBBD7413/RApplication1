
package com.rawalinfocom.rcontact.notifications;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiRContactsAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableRCNotificationUpdates;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.NotiRContactsItem;
import com.rawalinfocom.rcontact.model.NotificationData;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsFragment extends BaseFragment implements WsResponseListener {

    TableRCNotificationUpdates tableRCNotificationUpdates;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerRContactsNoti;
    NotiRContactsAdapter updtaesAdapter;

    private ArrayList<NotiRContactsItem> updates;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiRContactsFragment newInstance() {
        return new NotiRContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_rcontacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tableRCNotificationUpdates = new TableRCNotificationUpdates(getDatabaseHandler());
        initData();
    }

    private void initData() {

        updates = tableRCNotificationUpdates.getAllUpdatesFromDB();

        updtaesAdapter = new NotiRContactsAdapter(getActivity(), updates);
        recyclerRContactsNoti.setAdapter(updtaesAdapter);
        recyclerRContactsNoti.setLayoutManager(new LinearLayoutManager(getActivity()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null)
                    ((NotificationsDetailActivity) getActivity()).updateNotificationCount(AppConstants.NOTIFICATION_TYPE_RUPDATE);
            }
        }, 800);

        getAllRContactUpdates(this);
        getImageFromVideo();
    }

    private void getImageFromVideo() {

        try {

            for (int i = 0; i < updates.size(); i++) {
                if (updates.get(i).getNotiType().equalsIgnoreCase("video")) {
                    String img_url = "http://img.youtube.com/vi/" +
                            updates.get(i).getNotiUrl().substring(updates.get(i).getNotiUrl().lastIndexOf("/") + 1)
                        /*Utils.extractYoutubeId(updates.get(i).getNotiUrl()) */ + "/0.jpg";

                    NotiRContactsItem notiRContactsItem = new NotiRContactsItem();
                    notiRContactsItem.setNotiId(updates.get(i).getNotiId());
                    notiRContactsItem.setNotiTitle(updates.get(i).getNotiTitle());
                    notiRContactsItem.setNotiDetails(updates.get(i).getNotiDetails());
                    notiRContactsItem.setNotiImage(img_url);
                    notiRContactsItem.setNotiType(updates.get(i).getNotiType());
                    notiRContactsItem.setNotiTime(updates.get(i).getNotiTime());
                    notiRContactsItem.setNotiUrl(updates.get(i).getNotiUrl());
                    updates.set(i, notiRContactsItem);
                }
            }

            if (updates.size() > 0)
                updtaesAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            System.out.println("RContacts error get image from video");
        }

    }

    private void getAllRContactUpdates(Fragment fragment) {

        WsRequestObject allUpdatesObject = new WsRequestObject();
        allUpdatesObject.setTimeStamp(Utils.getStringPreference(getActivity(),
                AppConstants.KEY_RCONTACTS_API_CALL_TIME_STAMP, ""));

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    allUpdatesObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_RCONTACT_UPDATES, "Getting updates..", true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT_V2 + WsConstants.REQ_GET_RCONTACT_UPDATES);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.msg_no_internet),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_RCONTACT_UPDATES)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                if (wsResponseObject != null) {

                    ArrayList<NotificationData> updatesData = wsResponseObject.getRcontactUpdate();
                    saveUpdatesToDb(updatesData);
                    Utils.setStringPreference(getActivity(), AppConstants.KEY_RCONTACTS_API_CALL_TIME_STAMP,
                            wsResponseObject.getTimestamp());
                }

                Utils.hideProgressDialog();

            }
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUpdatesToDb(ArrayList<NotificationData> updatesData) {
        if (updatesData == null) {
            return;
        }
        for (NotificationData rconUpdate : updatesData) {
            tableRCNotificationUpdates.addUpdate(rconUpdate);
        }

        if (updatesData.size() > 0)
            refreshAllList();
    }

    private void refreshAllList() {
        updates = tableRCNotificationUpdates.getAllUpdatesFromDB();
        updtaesAdapter.updateList(updates);
        getImageFromVideo();
    }
}
