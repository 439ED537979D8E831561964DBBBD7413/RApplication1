package com.rawalinfocom.rcontact.receivers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableSpamDetailMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.SpamDataType;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by Aniruddh on 22/02/17.
 */

public class PhoneCallReceiver extends BroadcastReceiver implements WsResponseListener {

    //The receiver will be recreated whenever android feels like it.  We need a static variable
    // to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    private Context context;
    private DatabaseHandler databaseHandler;
    private SpamDataType spamDataType;


    public PhoneCallReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);
        spamDataType = new SpamDataType();

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
                Toast.makeText(context, "Incoming - call", Toast.LENGTH_SHORT).show();
                /*String name = getNameFromNumber(Utils.getFormattedNumber(context, savedNumber));
                if (StringUtils.isEmpty(name)) {
                }*/
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                /*String name = getNameFromNumber(Utils.getFormattedNumber(context, savedNumber));
                if (StringUtils.isEmpty(name)) {
                    callSpamServiceApi();
                }*/
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
                Toast.makeText(context, "Call - ended", Toast.LENGTH_SHORT).show();
                AppConstants.isFromReceiver = true;
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                initializeEndCallDialog();

                Intent localBroadcastIntent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_RECEIVE_RECENT_CALLS);
                LocalBroadcastManager myLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                myLocalBroadcastManager.sendBroadcast(localBroadcastIntent);

                break;
        }
        lastState = state;
    }

//    private void blockCall(Context context, String numberToBlock) {
//        if (Utils.getHashMapPreferenceForBlock(context, AppConstants
//                .PREF_BLOCK_CONTACT_LIST) != null) {
//            HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
//                    Utils.getHashMapPreferenceForBlock(context, AppConstants.PREF_BLOCK_CONTACT_LIST);
//            ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
//            String blockedNumber = "";
//            String hashKey = "";
//            if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
//                for (String key : blockProfileHashMapList.keySet()) {
//                    System.out.println(key);
//                    hashKey = key;
//                    if (blockProfileHashMapList.containsKey(hashKey)) {
//                        callLogTypeList.addAll(blockProfileHashMapList.get(hashKey));
//                        if (callLogTypeList != null) {
//                            for (int j = 0; j < callLogTypeList.size(); j++) {
//                                String tempNumber = callLogTypeList.get(j).getNumber();
//                                if (tempNumber.equalsIgnoreCase(numberToBlock)) {
//                                    blockedNumber = tempNumber;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (!TextUtils.isEmpty(blockedNumber)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Telephony telephonyService;
//                    try {
//                        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                        Method m1 = tm.getClass().getDeclaredMethod("getITelephony");
//                        m1.setAccessible(true);
//                        Object iTelephony = m1.invoke(tm);
//
//                        Method m2 = iTelephony.getClass().getDeclaredMethod("silenceRinger");
//                        Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");
//
//                        m2.invoke(iTelephony);
//                        m3.invoke(iTelephony);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    try {
//
//                        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//                        // Get the getITelephony() method
//                        Class classTelephony = Class.forName(telephonyManager.getClass().getName());
//                        Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");
//
//                        // Ignore that the method is supposed to be private
//                        methodGetITelephony.setAccessible(true);
//
//                        // Invoke getITelephony() to get the ITelephony interface
//                        Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);
//
//                        // Get the endCall method from ITelephony
//                        Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
//                        Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
//
//                        // Invoke endCall()
//                        methodEndCall.invoke(telephonyInterface);
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

    private void callSpamServiceApi() {

        if (Utils.isNetworkAvailable(context)) {
            WsRequestObject deviceDetailObject = new WsRequestObject();
            deviceDetailObject.setUnknownNumberList(new ArrayList<String>(Arrays.asList(savedNumber)));

            new AsyncWebServiceCall((Activity) context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DATA, null, true).execute(
                    WsConstants.WS_ROOT + WsConstants.REQ_GET_PROFILE_DATA);

        }
    }

    private void initializeIncomingCallDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_incoming_call);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        ImageView imageClose = (ImageView) dialog.findViewById(R.id.image_close);
        TextView textViewLastCallTime = (TextView) dialog.findViewById(R.id.text_last_call_time);
        ImageView imageProfile = (ImageView) dialog.findViewById(R.id.image_profile);
        TextView textNumber = (TextView) dialog.findViewById(R.id.text_number);
        TextView textInternetStrenght = (TextView) dialog.findViewById(R.id.text_internet_strenght);
        TextView textSpamReport = (TextView) dialog.findViewById(R.id.text_spam_report);
        LinearLayout llSpam = (LinearLayout) dialog.findViewById(R.id.ll_spam);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        textInternetStrenght.setVisibility(View.GONE);

        String compareCurr = "";
        String compareHistDate = "";
        String currentDate = String.valueOf(System.currentTimeMillis());
        Date currDate = new Date(currentDate);
        compareCurr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format
                (currDate);
        compareHistDate = callLastHistoryTime(savedNumber);

        if (StringUtils.length(compareHistDate) > 0) {
            textViewLastCallTime.setVisibility(View.VISIBLE);
            String subCurrent = compareCurr.substring(0, 10);
            String subHistDate = compareHistDate.substring(0, 10);
            if (subCurrent.equalsIgnoreCase(subHistDate)) {
                String currHour = compareCurr.substring(12, 13);
                String hisHour = compareHistDate.substring(12, 13);
                int diff = (Integer.parseInt(currHour)) - (Integer.parseInt(hisHour));
                if (diff > 0) {
                    textViewLastCallTime.setText("Last call " + diff + " hrs. ago");
                }
            } else {
                //To do :  Day diffs to display
            }
        } else {
            textViewLastCallTime.setVisibility(View.GONE);
        }


        if (StringUtils.equalsIgnoreCase(spamDataType.getRcpVerfiy(), "0")) {
            textNumber.setTypeface(Utils.typefaceBold(context));
            textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .textColorBlue));

            String contactNameToDisplay = "";
            String prefix = spamDataType.getPrefix();
            String suffix = spamDataType.getSuffix();
            String firstName = spamDataType.getFirstName();
            String middleName = spamDataType.getMiddleName();
            String lastName = spamDataType.getLastName();

            if (StringUtils.length(prefix) > 0)
                contactNameToDisplay = contactNameToDisplay + prefix + " ";
            if (StringUtils.length(suffix) > 0)
                contactNameToDisplay = contactNameToDisplay + suffix + " ";
            if (StringUtils.length(firstName) > 0)
                contactNameToDisplay = contactNameToDisplay + firstName + " ";
            if (StringUtils.length(middleName) > 0)
                contactNameToDisplay = contactNameToDisplay + middleName + " ";
            if (StringUtils.length(lastName) > 0)
                contactNameToDisplay = contactNameToDisplay + lastName + "";

            if (!StringUtils.isEmpty(contactNameToDisplay)) {
                textNumber.setText(savedNumber + " (" + contactNameToDisplay + ")");
            } else {
                textNumber.setText(savedNumber);
            }


            if (!StringUtils.isEmpty(spamDataType.getSpamCount())) {
                llSpam.setVisibility(View.VISIBLE);
                textSpamReport.setTypeface(Utils.typefaceBold(context));
                textSpamReport.setText(context.getString(R.string.report_spam) + " (" + spamDataType.getSpamCount() + ")");

            } else {
                llSpam.setVisibility(View.GONE);
            }
        } else if (StringUtils.equalsIgnoreCase(spamDataType.getRcpVerfiy(), "1")) {

            textNumber.setTypeface(Utils.typefaceBold(context));
            textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .colorAccent));

            String contactNameToDisplay = "";
            String prefix = spamDataType.getPrefix();
            String suffix = spamDataType.getSuffix();
            String firstName = spamDataType.getFirstName();
            String middleName = spamDataType.getMiddleName();
            String lastName = spamDataType.getLastName();

            if (StringUtils.length(prefix) > 0)
                contactNameToDisplay = contactNameToDisplay + prefix + " ";
            if (StringUtils.length(suffix) > 0)
                contactNameToDisplay = contactNameToDisplay + suffix + " ";
            if (StringUtils.length(firstName) > 0)
                contactNameToDisplay = contactNameToDisplay + firstName + " ";
            if (StringUtils.length(middleName) > 0)
                contactNameToDisplay = contactNameToDisplay + middleName + " ";
            if (StringUtils.length(lastName) > 0)
                contactNameToDisplay = contactNameToDisplay + lastName + "";

            if (!StringUtils.isEmpty(contactNameToDisplay)) {
                textNumber.setText(savedNumber + " (" + contactNameToDisplay + ")");
            } else {
                textNumber.setText(savedNumber);
            }


            if (!StringUtils.isEmpty(spamDataType.getSpamCount())) {
                llSpam.setVisibility(View.VISIBLE);
                textSpamReport.setTypeface(Utils.typefaceBold(context));
                textSpamReport.setText(context.getString(R.string.report_spam) + " (" + spamDataType.getSpamCount() + ")");

            } else {
                llSpam.setVisibility(View.GONE);
            }

        } else {
            textNumber.setTypeface(Utils.typefaceBold(context));
            textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));

            textNumber.setText(savedNumber);

        }

        dialog.show();


    }

    private void initializeEndCallDialog() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_end_call);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.width =  WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        ImageView imageClose = (ImageView) dialog.findViewById(R.id.image_close);
        ImageView imageIcon = (ImageView) dialog.findViewById(R.id.image_icon);
        ImageView imageProfile = (ImageView) dialog.findViewById(R.id.image_profile);
        TextView textStaticInfo  =(TextView) dialog.findViewById(R.id.text_static_info);
        TextView textNumber  =(TextView) dialog.findViewById(R.id.text_number);
        TextView textInternetStrenght  =(TextView) dialog.findViewById(R.id.text_internet_strenght);
        TextView textSpamReport  =(TextView) dialog.findViewById(R.id.text_spam_report);
        LinearLayout llSpam  =(LinearLayout) dialog.findViewById(R.id.ll_spam);
        LinearLayout llCall  =(LinearLayout) dialog.findViewById(R.id.ll_call);
        LinearLayout llMessage  =(LinearLayout) dialog.findViewById(R.id.ll_message);
        LinearLayout llSave  =(LinearLayout) dialog.findViewById(R.id.ll_save);
        Button buttonViewProfile = (Button) dialog.findViewById(R.id.button_view_profile);
        Button buttonViewCallHistory = (Button) dialog.findViewById(R.id.button_view_call_history);


        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        llSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String unicodeNumber = savedNumber.replace("*", Uri.encode("*")).replace("#", Uri.encode("#"));
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + unicodeNumber));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }catch (SecurityException e){
                    e.printStackTrace();
                }

            }
        });

        llMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:" + savedNumber));
                context.startActivity(smsIntent);

            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addToContact(context, savedNumber);
            }
        });


        buttonViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonViewCallHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        SpamDataType spamDataType =  setRCPDetailsAndSpamCountforUnsavedNumbers(savedNumber);

        if (StringUtils.equalsIgnoreCase(spamDataType.getRcpVerfiy(), "0")) {
            textNumber.setTypeface(Utils.typefaceBold(context));
            textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .textColorBlue));

            buttonViewProfile.setVisibility(View.GONE);

            String contactNameToDisplay = "";
            String prefix = spamDataType.getPrefix();
            String suffix = spamDataType.getSuffix();
            String firstName = spamDataType.getFirstName();
            String middleName = spamDataType.getMiddleName();
            String lastName = spamDataType.getLastName();

            if (StringUtils.length(prefix) > 0)
                contactNameToDisplay = contactNameToDisplay + prefix + " ";
            if (StringUtils.length(suffix) > 0)
                contactNameToDisplay = contactNameToDisplay + suffix + " ";
            if (StringUtils.length(firstName) > 0)
                contactNameToDisplay = contactNameToDisplay + firstName + " ";
            if (StringUtils.length(middleName) > 0)
                contactNameToDisplay = contactNameToDisplay + middleName + " ";
            if (StringUtils.length(lastName) > 0)
                contactNameToDisplay = contactNameToDisplay + lastName + "";

            if (!StringUtils.isEmpty(contactNameToDisplay)) {
                textNumber.setText(savedNumber + " (" + contactNameToDisplay + ")");
            } else {
                textNumber.setText(savedNumber);
            }


            if (!StringUtils.isEmpty(spamDataType.getSpamCount())) {
                llSpam.setVisibility(View.VISIBLE);
                textSpamReport.setTypeface(Utils.typefaceBold(context));
                textSpamReport.setText(context.getString(R.string.report_spam) + " (" + spamDataType.getSpamCount() + ")");

            } else {
                llSpam.setVisibility(View.GONE);
            }
        } else if (StringUtils.equalsIgnoreCase(spamDataType.getRcpVerfiy(), "1")) {

            textNumber.setTypeface(Utils.typefaceBold(context));
            textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .colorAccent));

            buttonViewProfile.setVisibility(View.VISIBLE);

            String contactNameToDisplay = "";
            String prefix = spamDataType.getPrefix();
            String suffix = spamDataType.getSuffix();
            String firstName = spamDataType.getFirstName();
            String middleName = spamDataType.getMiddleName();
            String lastName = spamDataType.getLastName();

            if (StringUtils.length(prefix) > 0)
                contactNameToDisplay = contactNameToDisplay + prefix + " ";
            if (StringUtils.length(suffix) > 0)
                contactNameToDisplay = contactNameToDisplay + suffix + " ";
            if (StringUtils.length(firstName) > 0)
                contactNameToDisplay = contactNameToDisplay + firstName + " ";
            if (StringUtils.length(middleName) > 0)
                contactNameToDisplay = contactNameToDisplay + middleName + " ";
            if (StringUtils.length(lastName) > 0)
                contactNameToDisplay = contactNameToDisplay + lastName + "";

            if (!StringUtils.isEmpty(contactNameToDisplay)) {
                textNumber.setText(savedNumber + " (" + contactNameToDisplay + ")");
            } else {
                textNumber.setText(savedNumber);
            }


            if (!StringUtils.isEmpty(spamDataType.getSpamCount())) {
                llSpam.setVisibility(View.VISIBLE);
                textSpamReport.setTypeface(Utils.typefaceBold(context));
                textSpamReport.setText(context.getString(R.string.report_spam) + " (" + spamDataType.getSpamCount() + ")");

            } else {
                llSpam.setVisibility(View.GONE);
            }

        } else {
            textNumber.setTypeface(Utils.typefaceBold(context));
            textNumber.setTextColor(ContextCompat.getColor(context, R.color
                    .colorBlack));

            textNumber.setText(savedNumber);

        }

        dialog.show();

    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        // <editor-fold desc="REQ_GET_PROFILE_DATA">
        if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DATA)) {
            WsResponseObject getProfileDataResponse = (WsResponseObject) data;
            if (getProfileDataResponse != null && StringUtils.equalsIgnoreCase
                    (getProfileDataResponse
                            .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                ArrayList<SpamDataType> spamDataTypeList = getProfileDataResponse.getSpamDataTypeArrayList();
                if (spamDataTypeList.size() > 0) {
                    try {
                        TableSpamDetailMaster tableSpamDetailMaster = new TableSpamDetailMaster(databaseHandler);
                        tableSpamDetailMaster.insertSpamDetails(spamDataTypeList);
                        spamDataType = setRCPDetailsAndSpamCountforUnsavedNumbers(savedNumber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                if (getProfileDataResponse != null) {
                    Log.e("error response", getProfileDataResponse.getMessage());
                } else {
                    Log.e("onDeliveryResponse: ", "getProfileDataResponse null");
                }
            }
            initializeIncomingCallDialog();
        }
        //</editor-fold>
    }


    private SpamDataType setRCPDetailsAndSpamCountforUnsavedNumbers(String number) {
        SpamDataType spamDataType = new SpamDataType();
        try {
            TableSpamDetailMaster tableSpamDetailMaster = new TableSpamDetailMaster(databaseHandler);
            if (!StringUtils.isEmpty(number)) {
                if (number.startsWith("+91"))
                    number = number.replace("+", "");
                else
                    number = "91" + number;

                spamDataType = tableSpamDetailMaster.getSpamDetailsFromNumber(number);
                if (spamDataType != null && !StringUtils.isEmpty(spamDataType.getSpamCount())) {
                    String lastName = spamDataType.getLastName();
                    String firstName = spamDataType.getFirstName();
                    String prefix = spamDataType.getPrefix();
                    String suffix = spamDataType.getSuffix();
                    String middleName = spamDataType.getMiddleName();
                    String isRcpVerified = spamDataType.getRcpVerfiy();
                    String rcpId = spamDataType.getRcpPmId();
                    String profileRating = spamDataType.getProfileRating();
                    String totalProfileRateUser = spamDataType.getTotalProfileRateUser();
                    String spamCount = spamDataType.getSpamCount();


                    if (!StringUtils.isEmpty(lastName))
                        spamDataType.setLastName(lastName);
                    if (!StringUtils.isEmpty(firstName))
                        spamDataType.setFirstName(firstName);
                    if (!StringUtils.isEmpty(prefix))
                        spamDataType.setPrefix(prefix);
                    if (!StringUtils.isEmpty(suffix))
                        spamDataType.setSuffix(suffix);
                    if (!StringUtils.isEmpty(middleName))
                        spamDataType.setMiddleName(middleName);
                    if (!StringUtils.isEmpty(isRcpVerified))
                        spamDataType.setRcpVerfiy(isRcpVerified);
                    if (!StringUtils.isEmpty(rcpId))
                        spamDataType.setRcpPmId(rcpId);
                    if (!StringUtils.isEmpty(profileRating))
                        spamDataType.setProfileRating(profileRating);
                    if (!StringUtils.isEmpty(totalProfileRateUser))
                        spamDataType.setTotalProfileRateUser(totalProfileRateUser);
                    if (!StringUtils.isEmpty(spamCount))
                        spamDataType.setSpamCount(spamCount);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return spamDataType;
    }

    private String getNameFromNumber(String phoneNumber) {
        String contactName = "";
        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                    CallLog.Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }

    private String callLastHistoryTime(String number) {
        Cursor cursor = null;
        String callDataAndTime = "";
        try {
            if (!TextUtils.isEmpty(number)) {
                cursor = getCallHistoryDataByNumber(number);
            }

            if (cursor != null && cursor.getCount() > 0) {
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                String callDate = cursor.getString(date);
                Date date1 = new Date(callDate);
                callDataAndTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format
                        (date1);
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDataAndTime;
    }
}
