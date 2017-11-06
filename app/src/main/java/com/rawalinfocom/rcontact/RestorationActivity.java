package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.database.TableRCContactRequest;
import com.rawalinfocom.rcontact.database.TableWebsiteMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Address;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.ContactRequestResponseDataItem;
import com.rawalinfocom.rcontact.model.Email;
import com.rawalinfocom.rcontact.model.Event;
import com.rawalinfocom.rcontact.model.ImAccount;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.Organization;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.RatingRequestResponseDataItem;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.Website;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestorationActivity extends BaseActivity implements WsResponseListener {

    @BindView(R.id.text_restore_header)
    TextView textRestoreHeader;
    @BindView(R.id.linear_indicator)
    LinearLayout linearIndicator;
    @BindView(R.id.button_restore)
    Button buttonRestore;
    @BindView(R.id.text_progress)
    TextView textProgress;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restoration);
        ButterKnife.bind(this);

        init();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            // <editor-fold desc="REQ_GET_RCP_CONTACT">
//            if (serviceType.contains(WsConstants.REQ_GET_RCP_CONTACT)) {
//                WsResponseObject getRCPContactUpdateResponse = (WsResponseObject) data;
//                if (getRCPContactUpdateResponse != null && StringUtils.equalsIgnoreCase
//                        (getRCPContactUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
//
//                    if (!Utils.isArraylistNullOrEmpty(getRCPContactUpdateResponse
//                            .getArrayListUserRcProfile())) {
//
//                        try {
//
//                            /* Store Unique Contacts to ProfileMobileMapping */
//                            storeToMobileMapping(getRCPContactUpdateResponse
//                                    .getArrayListUserRcProfile());
//
//                                /* Store Unique Emails to ProfileEmailMapping */
//                            storeToEmailMapping(getRCPContactUpdateResponse
//                                    .getArrayListUserRcProfile());
//
//                                /* Store Profile Details to respective Table */
//                            storeProfileDataToDb(getRCPContactUpdateResponse
//                                    .getArrayListUserRcProfile(), getRCPContactUpdateResponse
//                                    .getArrayListMapping());
//
//                        } catch (Exception e) {
//                            System.out.println("RContact error");
//                        }
//                    }
//                    if (!Utils.isArraylistNullOrEmpty(getRCPContactUpdateResponse
//                            .getArrayListMapping())) {
//                        removeRemovedDataFromDb(getRCPContactUpdateResponse
//                                .getArrayListMapping());
//                    }
//
//                    pullMechanismServiceCall("", "", WsConstants.REQ_GET_CONTACT_REQUEST);
//
//
//                } else {
//                    if (getRCPContactUpdateResponse != null) {
//                        System.out.println("RContact error --> " + getRCPContactUpdateResponse.getMessage());
//                    } else {
//                        System.out.println("RContact error --> getContactUpdateResponse null");
//                    }
//
//                    pullMechanismServiceCall("", "", WsConstants.REQ_GET_CONTACT_REQUEST);
//                }
//            }

            // <editor-fold desc="REQ_GET_CONTACT_REQUEST">
            if (serviceType.contains(WsConstants.REQ_GET_CONTACT_REQUEST)) {
                WsResponseObject getContactUpdateResponse = (WsResponseObject) data;
                if (getContactUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getContactUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeContactRequestResponseToDB(getContactUpdateResponse, getContactUpdateResponse.getRequestData(), getContactUpdateResponse.getResponseData());

                    pullMechanismServiceCall("", "", WsConstants.REQ_GET_RATING_DETAILS);

                } else {
                    if (getContactUpdateResponse != null) {
                        System.out.println("RContact error --> " + getContactUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getContactUpdateResponse null");
                    }

                    pullMechanismServiceCall("", "", WsConstants.REQ_GET_RATING_DETAILS);
                }
            }

            // <editor-fold desc="REQ_GET_RATING_DETAILS">
            if (serviceType.contains(WsConstants.REQ_GET_RATING_DETAILS)) {
                WsResponseObject getRatingUpdateResponse = (WsResponseObject) data;
                if (getRatingUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getRatingUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeRatingRequestResponseToDB(getRatingUpdateResponse, getRatingUpdateResponse.getRatingReceive(), getRatingUpdateResponse.getRatingDone()
                            , getRatingUpdateResponse.getRatingDetails());

                    pullMechanismServiceCall("", "", WsConstants.REQ_GET_COMMENT_DETAILS);

                } else {
                    if (getRatingUpdateResponse != null) {
                        System.out.println("RContact error --> " + getRatingUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getRatingUpdateResponse null");
                    }

                    pullMechanismServiceCall("", "", WsConstants.REQ_GET_COMMENT_DETAILS);
                }
            }

            // <editor-fold desc="REQ_GET_COMMENT_DETAILS">
            if (serviceType.contains(WsConstants.REQ_GET_COMMENT_DETAILS)) {
                WsResponseObject getCommentUpdateResponse = (WsResponseObject) data;
                if (getCommentUpdateResponse != null && StringUtils.equalsIgnoreCase
                        (getCommentUpdateResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    storeCommentRequestResponseToDB(getCommentUpdateResponse, getCommentUpdateResponse.getCommentReceive(), getCommentUpdateResponse.getCommentDone());

                } else {
                    if (getCommentUpdateResponse != null) {
                        System.out.println("RContact error --> " + getCommentUpdateResponse.getMessage());
                    } else {
                        System.out.println("RContact error --> getCommentUpdateResponse null");
                    }
                }

                buttonRestore.setBackgroundResource(R.drawable.bg_circle_green);
                progressBar.setVisibility(View.GONE);
                textProgress.setVisibility(View.GONE);
                // textProgress.setText(getString(R.string.str_done));
                buttonRestore.setEnabled(true);
            }
            //</editor-fold>

        } else {
            Log.e("error", error.toString());
        }
    }

    private void redirectToMainActivity() {

        Utils.setBooleanPreference(this, AppConstants.KEY_IS_RESTORE_DONE, true);
        Utils.setBooleanPreference(this, AppConstants.PREF_IS_LOGIN, true);

        // Redirect to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    // PROFILE COMMENT HISTORY RESTORE
    private void storeCommentRequestResponseToDB(WsResponseObject getCommentUpdateResponse, ArrayList<RatingRequestResponseDataItem> commentReceive,
                                                 ArrayList<RatingRequestResponseDataItem> commentDone) {

        try {
            TableCommentMaster tableCommentMaster = new TableCommentMaster(databaseHandler);

            // eventCommentDone
            for (int i = 0; i < commentDone.size(); i++) {

                RatingRequestResponseDataItem dataItem = commentDone.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_SENT);
                comment.setCrmType("Comment");
                comment.setCrmCloudPrId(dataItem.getCommentId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getToPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmReply(dataItem.getReply());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setEvmRecordIndexId(dataItem.getEventRecordIndexId());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                if (!StringUtils.isEmpty(dataItem.getReplyAt()))
                    comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
                else comment.setCrmRepliedAt("");

//                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
//                tableProfileMaster.updateUserProfileRating(toPmId, avgRating, totalUniqueRater);
//                Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, totalUniqueRater);
//                Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, avgRating);

                tableCommentMaster.addComment(comment);
            }

            // eventCommentReceive
            for (int i = 0; i < commentReceive.size(); i++) {

                RatingRequestResponseDataItem dataItem = commentReceive.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                comment.setCrmType("Comment");
                comment.setCrmCloudPrId(dataItem.getCommentId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getFromPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmReply(dataItem.getReply());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setEvmRecordIndexId(dataItem.getEventRecordIndexId());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                if (!StringUtils.isEmpty(dataItem.getReplyAt()))
                    comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
                else comment.setCrmRepliedAt("");

//                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
//                tableProfileMaster.updateUserProfileRating(toPmId, avgRating, totalUniqueRater);
//                Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, totalUniqueRater);
//                Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, avgRating);

                tableCommentMaster.addComment(comment);
            }

        } catch (Exception e) {
            System.out.println("RContact storeCommentRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getCommentUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getCommentUpdateResponse.getTimestamp());
            Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
        }
    }

    // PROFILE RATING HISTORY RESTORE
    private void storeRatingRequestResponseToDB(WsResponseObject getRatingUpdateResponse, ArrayList<RatingRequestResponseDataItem> ratingReceive,
                                                ArrayList<RatingRequestResponseDataItem> ratingDone, RatingRequestResponseDataItem ratingDetails) {

        try {
            TableCommentMaster tableCommentMaster = new TableCommentMaster(databaseHandler);

            // profileRatingComment
            for (int i = 0; i < ratingDone.size(); i++) {

                RatingRequestResponseDataItem dataItem = ratingDone.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_SENT);
                comment.setCrmType("Rating");
                comment.setCrmCloudPrId(dataItem.getPrId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getToPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmReply(dataItem.getReply());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                if (!StringUtils.isEmpty(dataItem.getReplyAt()))
                    comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
                else comment.setCrmRepliedAt("");

                tableCommentMaster.addComment(comment);
            }

            // profileRatingReply
            for (int i = 0; i < ratingReceive.size(); i++) {

                RatingRequestResponseDataItem dataItem = ratingReceive.get(i);

                Comment comment = new Comment();
                comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                comment.setCrmType("Rating");
                comment.setCrmCloudPrId(dataItem.getPrId());
                comment.setCrmRating(dataItem.getPrRatingStars());
                comment.setRcProfileMasterPmId(dataItem.getFromPmId());
                comment.setCrmComment(dataItem.getComment());
                comment.setCrmReply(dataItem.getReply());
                comment.setCrmProfileDetails(dataItem.getName());
                comment.setCrmImage(dataItem.getPmProfilePhoto());
                comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()));
                comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()));
                if (!StringUtils.isEmpty(dataItem.getReplyAt()))
                    comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(dataItem.getReplyAt()));
                else comment.setCrmRepliedAt("");

                tableCommentMaster.addComment(comment);

            }

            if (ratingDetails != null) {

                String avgRating = ratingDetails.getProfileRating();
                String totalUniqueRater = ratingDetails.getTotalProfileRateUser();
                String toPmId = getUserPmId();

                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                tableProfileMaster.updateUserProfileRating(toPmId, avgRating, totalUniqueRater);
                Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING, totalUniqueRater);
                Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, avgRating);
            }

        } catch (Exception e) {
            System.out.println("RContact storeRatingRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getRatingUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getRatingUpdateResponse.getTimestamp());
            Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
        }
    }

    // PROFILE PRIVATE DATA SHOWN REQUEST HISTORY RESTORE
    private void storeContactRequestResponseToDB(WsResponseObject getContactUpdateResponse, ArrayList<ContactRequestResponseDataItem> requestData,
                                                 ArrayList<ContactRequestResponseDataItem> responseData) {

        try {

            TableRCContactRequest tableRCContactRequest = new TableRCContactRequest
                    (databaseHandler);

            for (int i = 0; i < requestData.size(); i++) {

                ContactRequestResponseDataItem dataItem = requestData.get(i);
                if (String.valueOf(dataItem.getCarPmIdTo()).equals(Utils.getStringPreference(this, AppConstants
                        .PREF_USER_PM_ID, "0")) && dataItem.getCarAccessPermissionStatus() == 0) {
                    tableRCContactRequest.addRequest(AppConstants
                                    .COMMENT_STATUS_RECEIVED,
                            String.valueOf(dataItem.getCarId()),
                            dataItem.getCarMongodbRecordIndex(),
                            dataItem.getCarPmIdFrom(),
                            dataItem.getCarPpmParticular(),
                            Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()),
                            Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()),
                            dataItem.getName(), dataItem.getPmProfilePhoto());
                }
            }

            for (int i = 0; i < responseData.size(); i++) {

                ContactRequestResponseDataItem dataItem = responseData.get(i);
                if (String.valueOf(dataItem.getCarPmIdFrom()).equals(Utils.getStringPreference(this, AppConstants
                        .PREF_USER_PM_ID, "0"))
                        && dataItem.getCarAccessPermissionStatus() == 1) {
                    tableRCContactRequest.addRequest(AppConstants
                                    .COMMENT_STATUS_SENT,
                            String.valueOf(dataItem.getCarId()),
                            dataItem.getCarMongodbRecordIndex(),
                            dataItem.getCarPmIdTo(),
                            dataItem.getCarPpmParticular(),
                            Utils.getLocalTimeFromUTCTime(dataItem.getCreatedAt()),
                            Utils.getLocalTimeFromUTCTime(dataItem.getUpdatedAt()),
                            dataItem.getName(), dataItem.getPmProfilePhoto());
                }
            }

        } catch (Exception e) {
            System.out.println("RContact storeContactRequestResponseToDB error ");
        }

        if (!StringUtils.isEmpty(getContactUpdateResponse.getTimestamp())) {
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME, String.valueOf(System.currentTimeMillis()));
            Utils.setStringPreference(this, AppConstants.KEY_API_CALL_TIME_STAMP, getContactUpdateResponse.getTimestamp());
            Utils.setBooleanPreference(this, AppConstants.KEY_IS_FIRST_TIME, false);
        }
    }

    //<editor-fold desc="Private Methods">
    private void init() {

        textRestoreHeader.setTypeface(Utils.typefaceRegular(RestorationActivity.this));
        buttonRestore.setTypeface(Utils.typefaceRegular(RestorationActivity.this));

        buttonRestore.setEnabled(false);
        buttonRestore.setBackgroundResource(R.drawable.bg_circle_light_green);
        pullMechanismServiceCall("", "", WsConstants.REQ_GET_CONTACT_REQUEST);
//        RCPContactServiceCall("", WsConstants.REQ_GET_RCP_CONTACT);

        buttonRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectToMainActivity();
            }
        });
    }

    private void pullMechanismServiceCall(String fromDate, String toDate, String url) {

        WsRequestObject deviceDetailObject = new WsRequestObject();

        deviceDetailObject.setFromDate(fromDate);
        deviceDetailObject.setToDate(toDate);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    deviceDetailObject, null, WsResponseObject.class, url, null, true).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + url);
        }
    }
}
