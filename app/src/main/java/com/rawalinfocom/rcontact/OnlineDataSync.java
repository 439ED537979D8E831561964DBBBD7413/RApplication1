package com.rawalinfocom.rcontact;

import android.content.Context;

import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileVisit;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Monal on 01/03/17.
 */

public class OnlineDataSync {

    private Context context;

    public OnlineDataSync(Context context) {
        this.context = context;
        syncOfflineProfileViews();
    }

    private void syncOfflineProfileViews() {
        HashMap<String, String> mapProfileViews = new HashMap<>();
        if (Utils.getHashMapPreference(context, AppConstants
                .PREF_PROFILE_VIEWS) != null) {
            mapProfileViews.putAll(Utils.getHashMapPreference(context, AppConstants
                    .PREF_PROFILE_VIEWS));

            ArrayList<ProfileVisit> arrayListProfileVisit = new ArrayList<>();
            Iterator iterator = mapProfileViews.entrySet().iterator();
            while (iterator.hasNext()) {
                ProfileVisit profileVisit = new ProfileVisit();
                Map.Entry pair = (Map.Entry) iterator.next();
                profileVisit.setVisitorPmId(Integer.parseInt(pair.getKey().toString()));
                profileVisit.setVisitCount(Integer.parseInt(pair.getValue().toString()));
                iterator.remove(); // avoids a ConcurrentModificationException
                arrayListProfileVisit.add(profileVisit);
            }

            WsRequestObject profileVisitObject = new WsRequestObject();
            profileVisitObject.setArrayListProfileVisit(arrayListProfileVisit);

            sendToCloud(profileVisitObject, WsConstants.REQ_ADD_PROFILE_VISIT);

        }
    }

    private void sendToCloud(WsRequestObject requestObject, String requestApi) {
        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObject, null, WsResponseObject.class, requestApi, null, true).execute
                    (WsConstants.WS_ROOT + requestApi);
        }
    }
}
