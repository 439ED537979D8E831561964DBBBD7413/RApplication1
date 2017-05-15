
package com.rawalinfocom.rcontact.notifications;

import android.os.Bundle;
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
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.RcontactUpdatesData;
import com.rawalinfocom.rcontact.adapters.NotiRContactsAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableRCNotificationUpdates;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.NotiRContactsItem;
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
        init();
        initData();
//        getAllRContactUpdates(this);
    }

    private void initData() {

        ArrayList<NotiRContactsItem> updates = tableRCNotificationUpdates.getAllUpdatesFromDB();
        updtaesAdapter = new NotiRContactsAdapter(getActivity(), updates);
        recyclerRContactsNoti.setAdapter(updtaesAdapter);
        recyclerRContactsNoti.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void init() {

    }

    private void getAllRContactUpdates(Fragment fragment) {

        WsRequestObject allUpdatesObject = new WsRequestObject();

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    allUpdatesObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_RCONTACT_UPDATES, "Getting updates..", true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_RCONTACT_UPDATES);
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_RCONTACT_UPDATES)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                if (wsResponseObject != null) {
                    ArrayList<RcontactUpdatesData> updatesData = wsResponseObject.getRcontactUpdate();
//                    saveUpdatesToDb(updatesData);
                }
                Utils.hideProgressDialog();

            }
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }
    }

//    private void saveUpdatesToDb(ArrayList<RcontactUpdatesData> updatesData) {
//        {
//            if (updatesData == null) {
//                return;
//            }
//            for (RcontactUpdatesData rconUpdate : updatesData) {
//                tableRCNotificationUpdates.addUpdate(rconUpdate);
//                refreshAllList();
//            }
//        }
//
//    }

    private void refreshAllList() {
        ArrayList<NotiRContactsItem> updates = tableRCNotificationUpdates.getAllUpdatesFromDB();
        updtaesAdapter.updateList(updates);
    }
}
