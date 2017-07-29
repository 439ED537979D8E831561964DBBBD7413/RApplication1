package com.rawalinfocom.rcontact.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.sms.SmsFragment;

/**
 * Created by Aniruddh on 11/05/17.
 */

public class SmsReceiver extends BroadcastReceiver {

    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        /*if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    AppConstants.isSMSFromReceiver =  true;
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        long dateandTime = msgs[i].getTimestampMillis();
                        SmsFragment.smsDataTypeReceiver.setAddress(msg_from);
                        SmsFragment.smsDataTypeReceiver.setBody(msgBody);
                        SmsFragment.smsDataTypeReceiver.setDataAndTime(dateandTime);
                    }

                    Intent localBroadcastIntent = new Intent(AppConstants
                            .ACTION_LOCAL_BROADCAST_SMS_RECEIVER);
                    LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager
                            .getInstance(context);
                    myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                    if(Utils.getBooleanPreference(context,
                            AppConstants.PREF_RECENT_SMS_BROADCAST_RECEIVER_MAIN_INSTANCE,false)){
                        Intent localBroadcastIntent1 = new Intent(AppConstants
                                .ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_SMS);
                        LocalBroadcastManager myLocalBroadcastManager1 = LocalBroadcastManager
                                .getInstance(context);
                        myLocalBroadcastManager1.sendBroadcast(localBroadcastIntent1);
                    }


                }catch(Exception e){
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }*/
    }
}
