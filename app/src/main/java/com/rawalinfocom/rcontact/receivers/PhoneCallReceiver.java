package com.rawalinfocom.rcontact.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telecom.Call;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Aniruddh on 22/02/17.
 */

public class PhoneCallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable
    // to remember data between instantiations

    private int lastState = TelephonyManager.CALL_STATE_IDLE;
    private Date callStartTime;
    private boolean isIncoming;
    private String savedNumber;  //because the passed incoming is only valid in ringing

    public PhoneCallReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We
        // use it to get the number.
        try {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager
                        .EXTRA_INCOMING_NUMBER);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to
    // IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
//                blockCall(context,savedNumber);
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on
                // them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;

            case TelephonyManager.CALL_STATE_IDLE:

                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                AppConstants.isFromReceiver = true;
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());

                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_CALLS);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                break;
        }
        lastState = state;
    }

    private void blockCall(Context context, String numberToBlock) {
        if (Utils.getHashMapPreferenceForBlock(context, AppConstants
                .PREF_BLOCK_CONTACT_LIST) != null) {
            HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                    Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);
            ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
            String blockedNumber = "";
            String hashKey = "";
            if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                for (String key : blockProfileHashMapList.keySet()) {
                    System.out.println(key);
                    hashKey = key;
                    if (blockProfileHashMapList.containsKey(hashKey)) {
                        callLogTypeList.addAll(blockProfileHashMapList.get(hashKey));
                        if (callLogTypeList != null) {
                            for (int j = 0; j < callLogTypeList.size(); j++) {
                                String tempNumber = callLogTypeList.get(j).getNumber();
                                if (tempNumber.equalsIgnoreCase(numberToBlock)) {
                                    blockedNumber = tempNumber;
                                }
                            }
                        }
                    }
                }
            }

            if (!TextUtils.isEmpty(blockedNumber)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Telephony telephonyService;
                    try {
                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        Method m1 = tm.getClass().getDeclaredMethod("getITelephony");
                        m1.setAccessible(true);
                        Object iTelephony = m1.invoke(tm);

                        Method m2 = iTelephony.getClass().getDeclaredMethod("silenceRinger");
                        Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");

                        m2.invoke(iTelephony);
                        m3.invoke(iTelephony);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {

                        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                        // Get the getITelephony() method
                        Class classTelephony = Class.forName(telephonyManager.getClass().getName());
                        Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

                        // Ignore that the method is supposed to be private
                        methodGetITelephony.setAccessible(true);

                        // Invoke getITelephony() to get the ITelephony interface
                        Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

                        // Get the endCall method from ITelephony
                        Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                        Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

                        // Invoke endCall()
                        methodEndCall.invoke(telephonyInterface);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
