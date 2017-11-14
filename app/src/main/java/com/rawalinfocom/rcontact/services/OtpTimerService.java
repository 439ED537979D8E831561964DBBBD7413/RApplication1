package com.rawalinfocom.rcontact.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableOtpLogDetails;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.OtpLog;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by Monal on 27/10/16.
 * <p>
 * Service to calculate 20 Minutes for Otp validation
 */

public class OtpTimerService extends Service implements WsResponseListener {

    private final static String LOG_TAG = "BroadcastService";

    CountDownTimer cdt = null;

    DatabaseHandler databaseHandler;

    String mobileNumber;
    long serviceEndTime;
    boolean callMspServer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mobileNumber = intent.getStringExtra(AppConstants.EXTRA_MOBILE_NUMBER);
            serviceEndTime = intent.getLongExtra(AppConstants.EXTRA_OTP_SERVICE_END_TIME, (long)
                    (AppConstants.OTP_VALIDITY_DURATION * 60 * 1000));
            callMspServer = intent.getBooleanExtra(AppConstants.EXTRA_CALL_MSP_SERVER, true);

            cdt = new CountDownTimer(serviceEndTime, 2 * 60 * 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.i(LOG_TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    Log.i(LOG_TAG, "Timer finished");

                    databaseHandler = new DatabaseHandler(OtpTimerService.this);

                    TableOtpLogDetails tableOtpLogDetails = new TableOtpLogDetails(databaseHandler);
                    OtpLog otpLog = tableOtpLogDetails.getLastOtpDetails();

                    // Update OTP validity Flag to 0
                    otpLog.setOldValidityFlag("0");
                    tableOtpLogDetails.updateOtp(otpLog);

                    if (callMspServer) {
                        // Get SMS status from third party
                        getMspDeliveryStatus(otpLog);
                    }
                }
            };

            cdt.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        super.onDestroy();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_MSP_DELIVERY_TIME">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_MSP_DELIVERY_TIME)) {
                WsResponseObject mspDeliveryStatusResponse = (WsResponseObject) data;
                if (mspDeliveryStatusResponse != null && StringUtils.equalsIgnoreCase
                        (mspDeliveryStatusResponse
                                .getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    if (mspDeliveryStatusResponse.getOtpLog().getOldMspDeliveryTime() == null) {
                        Log.e(LOG_TAG, "MSP Server not responding.");
                    } else {

                        databaseHandler = new DatabaseHandler(OtpTimerService.this);

                        TableOtpLogDetails tableOtpLogDetails = new TableOtpLogDetails
                                (databaseHandler);
                        OtpLog otpLog = tableOtpLogDetails.getLastOtpDetails();

                        Date currentValidityTime = Utils.getStringDateToDate(otpLog
                                .getOldValidUpto());
                        Date mspValidityTime = Utils.getStringDateToDate(Utils.getOtpExpirationTime
                                (mspDeliveryStatusResponse.getOtpLog().getOldMspDeliveryTime()));

                        Log.i("currentValidityTime: ", currentValidityTime.toString() + " ");
                        Log.i("mspValidityTime: ", mspValidityTime + " ");
                        Log.i("difference: ", mspValidityTime.getTime() -
                                currentValidityTime.getTime() + " ");

                        try {
                            if (mspValidityTime.after(currentValidityTime)) {
                                otpLog.setOldValidityFlag("1");
                                otpLog.setOldValidUpto(Utils.getOtpExpirationTime
                                        (mspDeliveryStatusResponse.getOtpLog()
                                                .getOldMspDeliveryTime()));
                                otpLog.setOldMspDeliveryTime(mspDeliveryStatusResponse.getOtpLog()
                                        .getOldMspDeliveryTime());
                                tableOtpLogDetails.updateOtp(otpLog);

                                Intent otpServiceIntent = new Intent(this, OtpTimerService.class);
                                otpServiceIntent.putExtra(AppConstants.EXTRA_MOBILE_NUMBER,
                                        mobileNumber);
                                otpServiceIntent.putExtra(AppConstants
                                        .EXTRA_OTP_SERVICE_END_TIME, mspValidityTime.getTime() -
                                        currentValidityTime.getTime());
                                otpServiceIntent.putExtra(AppConstants.EXTRA_CALL_MSP_SERVER,
                                        false);
                                startService(otpServiceIntent);
                            } else {
                                otpLog.setOldValidityFlag("0");
                                tableOtpLogDetails.updateOtp(otpLog);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Update OTP validity Flag to 0
                            otpLog.setOldValidityFlag("0");
                            tableOtpLogDetails.updateOtp(otpLog);
                        }

                        /*Toast.makeText(this, mspDeliveryStatusResponse.getOtpLog()
                                .getOldMspDeliveryTime(), Toast.LENGTH_SHORT).show();*/
                    }
                } else {
                    if (mspDeliveryStatusResponse != null) {
                        Log.e("error response", mspDeliveryStatusResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                    }
                }
            }
            //</editor-fold>

        } else {
            Log.e("onDeliveryResponse: ", error.getLocalizedMessage());
        }
    }

    private void getMspDeliveryStatus(OtpLog otpLog) {

        WsRequestObject mspDeliveryStatusObject = new WsRequestObject();
        mspDeliveryStatusObject.setPmId(Integer.parseInt(otpLog.getRcProfileMasterPmId()));
        mspDeliveryStatusObject.setMobileNumber(mobileNumber);
        mspDeliveryStatusObject.setOtp(otpLog.getOldOtp());
        mspDeliveryStatusObject.setOtpGenerationTime(otpLog.getOldGeneratedAt());

//        if (Utils.isNetworkAvailable(this)) {
//            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
//                    mspDeliveryStatusObject, null, WsResponseObject.class, WsConstants
//                    .REQ_MSP_DELIVERY_TIME, null, false).execute(BuildConfig.WS_ROOT + WsConstants
//                    .REQ_MSP_DELIVERY_TIME);
//        }
    }
}
