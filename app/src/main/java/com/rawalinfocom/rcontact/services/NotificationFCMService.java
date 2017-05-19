package com.rawalinfocom.rcontact.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.database.TableRCContactRequest;
import com.rawalinfocom.rcontact.database.TableRCNotificationUpdates;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.ContactRequestData;
import com.rawalinfocom.rcontact.model.NotificationData;
import com.rawalinfocom.rcontact.model.NotificationStateData;
import com.rawalinfocom.rcontact.notifications.NotificationsActivity;

import java.io.IOException;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by maulik on 10/05/17.
 */

public class NotificationFCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> m = remoteMessage.getData();
            DatabaseHandler databaseHandler = new DatabaseHandler(this);
            String notiData = m.get("default");
            /*if (notiData != null) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    NotificationData obj = mapper.readValue(notiData, NotificationData.class);
                    TableRCNotificationUpdates tableRCNotificationUpdates = new TableRCNotificationUpdates(databaseHandler);
                    tableRCNotificationUpdates.addUpdate(obj);
                    sendNotification(obj.getDetails());

                    TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster(databaseHandler);
                    NotificationStateData notificationStateData = new NotificationStateData();
                    notificationStateData.setNotificationState(Integer.parseInt(m.get("unm_status")));
                    notificationStateData.setCloudNotificationId(m.get("unm_id"));
                    notificationStateData.setCreatedAt(obj.getCreatedAt());
                    notificationStateData.setUpdatedAt(obj.getCreatedAt());
                    notificationStateData.setNotificationType(5);
                    notificationStateMaster.addNotificationState(notificationStateData);
                    int badgeCount = 1;
                    ShortcutBadger.applyCount(this.getApplicationContext(), badgeCount);
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
            String api = m.get("API");
            if (api == null) {
                return;
            }

            TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster(databaseHandler);
            NotificationStateData notificationStateData = new NotificationStateData();
            notificationStateData.setNotificationState(Integer.parseInt(m.get("unm_status")));
            notificationStateData.setCloudNotificationId(m.get("unm_id"));
//            notificationStateMaster.addNotificationState();
            TableCommentMaster tableCommentMaster = new TableCommentMaster(databaseHandler);
            Comment comment = new Comment();
            switch (api) {
                case "profileRatingComment":
                    comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                    comment.setCrmType(getResources().getString(R.string.text_rating));
                    comment.setCrmCloudPrId(m.get("pr_id"));
                    comment.setCrmRating(m.get("pr_rating_stars"));
                    comment.setRcProfileMasterPmId(Integer.parseInt(m.get("pr_from_pm_id")));
                    comment.setCrmComment(m.get("pr_comment"));
                    comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(m.get("created_at")));
                    comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
                    notificationStateData.setCreatedAt(comment.getCrmCreatedAt());
                    notificationStateData.setUpdatedAt(comment.getCrmUpdatedAt());
                    notificationStateData.setNotificationType(1);
                    tableCommentMaster.addComment(comment);
                    notificationStateMaster.addNotificationState(notificationStateData);
                    break;
                case "profileRatingReply":
                    tableCommentMaster.addReply(m.get("pr_id"), m.get("pr_reply"),
                            Utils.getLocalTimeFromUTCTime(m.get("reply_at")), Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
                    notificationStateData.setCreatedAt(m.get("reply_at"));
                    notificationStateData.setUpdatedAt(m.get("updated_at"));
                    notificationStateData.setNotificationType(2);
                    notificationStateMaster.addNotificationState(notificationStateData);
                    break;
                case "eventComment":
                    comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                    comment.setCrmType(m.get("type"));
                    comment.setCrmCloudPrId(m.get("id"));
                    comment.setRcProfileMasterPmId(Integer.parseInt(m.get("from_pm_id")));
                    comment.setCrmComment(m.get("comment"));
                    comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(m.get("created_date")));
                    comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(m.get("updated_date")));
                    comment.setEvmRecordIndexId(m.get("event_record_index_id"));
                    tableCommentMaster.addComment(comment);
                    notificationStateData.setCreatedAt(m.get("created_date"));
                    notificationStateData.setUpdatedAt(m.get("updated_date"));
                    notificationStateData.setNotificationType(1);
                    notificationStateMaster.addNotificationState(notificationStateData);
                    break;
                case "eventReply":
                    tableCommentMaster.addReply(m.get("id"), m.get("reply"),
                            Utils.getLocalTimeFromUTCTime(m.get("reply_date")), Utils.getLocalTimeFromUTCTime(m.get("updated_date")));
                    notificationStateData.setCreatedAt(m.get("reply_date"));
                    notificationStateData.setUpdatedAt(m.get("updated_date"));
                    notificationStateData.setNotificationType(3);
                    notificationStateMaster.addNotificationState(notificationStateData);
                    break;
                case "sendContactRequest":
                    TableRCContactRequest tableRCContactRequest = new TableRCContactRequest(databaseHandler);
                    if (m.get("car_pm_id_to").equals(Utils.getStringPreference(this, AppConstants.PREF_USER_PM_ID, "0"))
                            && m.get("car_access_permission_status").equals("0")) {
                        tableRCContactRequest.addRequest(AppConstants.COMMENT_STATUS_RECEIVED,
                                m.get("car_id"),
                                m.get("car_mongodb_record_index"),
                                Integer.parseInt(m.get("car_pm_id_from")),
                                m.get("car_ppm_particular_text"),
                                Utils.getLocalTimeFromUTCTime(m.get("created_at")),
                                Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
                        notificationStateData.setCreatedAt(m.get("created_at"));
                        notificationStateData.setUpdatedAt(m.get("updated_at"));
                        notificationStateData.setNotificationType(4);
                        notificationStateMaster.addNotificationState(notificationStateData);
                    }
                    break;
                case "acceptContactRequest":
                    TableRCContactRequest tableRCContactRequest1 = new TableRCContactRequest(databaseHandler);
                    if (m.get("car_pm_id_from").equals(Utils.getStringPreference(this, AppConstants.PREF_USER_PM_ID, "0"))
                            && m.get("car_access_permission_status").equals("1")) {
                        tableRCContactRequest1.addRequest(AppConstants.COMMENT_STATUS_SENT,
                                m.get("car_id"),
                                m.get("car_mongodb_record_index"),
                                Integer.parseInt(m.get("car_pm_id_to")),
                                m.get("car_ppm_particular_text"),
                                Utils.getLocalTimeFromUTCTime(m.get("created_at")),
                                Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
                        notificationStateData.setCreatedAt(m.get("created_at"));
                        notificationStateData.setUpdatedAt(m.get("updated_at"));
                        notificationStateData.setNotificationType(4);
                        notificationStateMaster.addNotificationState(notificationStateData);

                        ObjectMapper mapper = new ObjectMapper();
                        String data = m.get("car_contact");
                        try {
                            ContactRequestData obj = mapper.readValue(data, ContactRequestData.class);
                            updatePrivacySetting(m.get("car_ppm_particular"), m.get("car_mongodb_record_index"), obj, databaseHandler);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            String msg = m.get("msg");
            sendNotification(msg);
            int badgeCount = 1;
            ShortcutBadger.applyCount(this.getApplicationContext(), badgeCount);
        }
    }

    private void updatePrivacySetting(String ppmTag, String cloudMongoId, ContactRequestData obj, DatabaseHandler databaseHandler) {
        switch (ppmTag) {
            case "pb_phone_number":
                TableMobileMaster tableMobileMaster = new TableMobileMaster(databaseHandler);
                tableMobileMaster.updatePrivacySetting(obj, cloudMongoId);
                break;
            case "pb_email_id":
                TableEmailMaster tableEmailMaster = new TableEmailMaster(databaseHandler);
                tableEmailMaster.updatePrivacySetting(obj, cloudMongoId);
                break;
            case "pb_address":
                TableAddressMaster tableAddressMaster = new TableAddressMaster(databaseHandler);
                tableAddressMaster.updatePrivacySetting(obj, cloudMongoId);
                break;
            case "pb_im_accounts":
                TableImMaster tableImMaster = new TableImMaster(databaseHandler);
                tableImMaster.updatePrivacySetting(obj, cloudMongoId);
                break;
            case "pb_event":
                TableEventMaster tableEventMaster = new TableEventMaster(databaseHandler);
                tableEventMaster.updatePrivacySetting(obj, cloudMongoId);
                break;
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("RContacts")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Utils.getID(), notificationBuilder.build());
    }
}
