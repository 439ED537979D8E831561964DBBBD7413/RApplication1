package com.rawalinfocom.rcontact.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rawalinfocom.rcontact.MainActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableAddressMaster;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.database.TableEmailMaster;
import com.rawalinfocom.rcontact.database.TableEventMaster;
import com.rawalinfocom.rcontact.database.TableImMaster;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableNotificationStateMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.database.TableRCContactRequest;
import com.rawalinfocom.rcontact.database.TableRCNotificationUpdates;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.ContactRequestData;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.NotificationData;
import com.rawalinfocom.rcontact.model.NotificationStateData;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.notifications.NotificationsDetailActivity;
import com.rawalinfocom.rcontact.notifications.TimelineActivity;
import com.rawalinfocom.rcontact.relation.ExistingRelationActivity;
import com.rawalinfocom.rcontact.relation.RelationRecommendationActivity;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by maulik on 10/05/17.
 */

public class NotificationFCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Utils.getBooleanPreference(this, AppConstants.PREF_IS_LOGIN, false)) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (remoteMessage.getData().size() > 0) {

                Map<String, String> m = remoteMessage.getData();
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                String notiData = m.get("default");
                if (notiData != null) {
                    if (m.get("API").equalsIgnoreCase("rcontactUpdate")) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            NotificationData obj = mapper.readValue(notiData, NotificationData
                                    .class);
                            TableRCNotificationUpdates tableRCNotificationUpdates = new
                                    TableRCNotificationUpdates(databaseHandler);
                            int id = tableRCNotificationUpdates.addUpdate(obj);
                            if (id != -1) {
                                TableNotificationStateMaster notificationStateMaster = new
                                        TableNotificationStateMaster(databaseHandler);
                                NotificationStateData notificationStateData = new
                                        NotificationStateData();
                                notificationStateData.setNotificationState(1);
                                notificationStateData.setCloudNotificationId(obj.getId());
                                notificationStateData.setCreatedAt(obj.getCreatedAt());
                                notificationStateData.setUpdatedAt(obj.getUpdatedAt());
                                notificationStateData.setNotificationType(AppConstants
                                        .NOTIFICATION_TYPE_RUPDATE);
                                notificationStateData.setNotificationMasterId(obj.getId());
                                notificationStateMaster.addNotificationState(notificationStateData);
                                if (!Utils.getBooleanPreference(this, AppConstants
                                        .PREF_DISABLE_PUSH, false))
                                    sendNotification(obj.getDetails(), AppConstants
                                            .NOTIFICATION_TYPE_RUPDATE);
                                else
                                    sendBroadcastForCountupdate();

                                updateNotificationCount(databaseHandler);
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
                String api = m.get("API");
                if (api == null) {
                    return;
                }
                String msg = m.get("msg");
                if (api.equalsIgnoreCase("relationshipRequest")) {
                    sendNotification(msg, AppConstants.NOTIFICATION_TYPE_RELATION_REQUEST);
                    return;
                }
                if (api.equalsIgnoreCase("relationshipAccept")) {
                    sendNotification(msg, AppConstants.NOTIFICATION_TYPE_RELATION_ACCEPT);
                    return;
                }
                if (api.equalsIgnoreCase("newUserRegistration")) {
                    String first_name = m.get("first_name");
                    String last_name = m.get("last_name");
                    String mobile_num = "+" + m.get("mobile_number");
                    String rcp_pm_id = m.get("rcp_pm_id");
                    String pm_badge = m.get("pm_badge");
                    String mnm_id = m.get("mnm_id");
                    String pb_profile_photo = m.get("pb_profile_photo");
                    String total_profile_rate_user = m.get("total_profile_rate_user");

                    ArrayList<String> rcpIds = getRawIdFromNumber(mobile_num);

                    if (!(rcpIds.size() > 0)) {
                        return;
                    }

                    // Hashmap with key as rcpId and value as rawId/s
                    HashMap<String, String> mapLocalRcpId = new HashMap<>();
                    for (int i = 0; i < rcpIds.size(); i++) {
                        String phonebookRawId;
                        if (mapLocalRcpId.containsKey(rcp_pm_id)) {
                            phonebookRawId = mapLocalRcpId.get(rcp_pm_id) +
                                    "," + rcpIds.get(i);
                        } else {
                            phonebookRawId = rcpIds.get(i);
                        }
                        mapLocalRcpId.put(rcp_pm_id, phonebookRawId);
                    }

                    // Basic Profile Data
                    TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                    ArrayList<UserProfile> arrayListUserProfile = new ArrayList<>();
                    UserProfile userProfile = new UserProfile();
                    userProfile.setPmFirstName(first_name);
                    userProfile.setPmLastName(last_name);
                    userProfile.setPmRcpId(rcp_pm_id);
                    userProfile.setPmBadge(pm_badge);
                    userProfile.setPmProfileImage(pb_profile_photo);
                    userProfile.setTotalProfileRateUser(total_profile_rate_user);
                    if (mapLocalRcpId.containsKey(rcp_pm_id)) {
                        userProfile.setPmRawId(mapLocalRcpId.get(rcp_pm_id));
                    }

                    String existingRawId = tableProfileMaster.getRawIdFromRcpId(Integer.parseInt
                            (userProfile.getPmRcpId()));
                    if (StringUtils.length(existingRawId) <= 0) {
                        arrayListUserProfile.add(userProfile);
                        tableProfileMaster.addArrayProfile(arrayListUserProfile);

                        TableProfileMobileMapping tableProfileMobileMapping = new
                                TableProfileMobileMapping(databaseHandler);
                        ArrayList<ProfileMobileMapping> arrayListProfileMobileMapping = new
                                ArrayList<>();
                        ProfileMobileMapping profileMobileMapping = new ProfileMobileMapping();
                        profileMobileMapping.setMpmMobileNumber(mobile_num);
                        profileMobileMapping.setMpmCloudMnmId(mnm_id);
                        profileMobileMapping.setMpmCloudPmId(rcp_pm_id);
                        profileMobileMapping.setMpmIsRcp("1");
                        arrayListProfileMobileMapping.add(profileMobileMapping);
                        tableProfileMobileMapping.addArrayProfileMobileMapping
                                (arrayListProfileMobileMapping);

                        TableMobileMaster tableMobileMaster = new TableMobileMaster
                                (databaseHandler);
                        ArrayList<MobileNumber> arrayListMobileNumber = new ArrayList<>();
                        MobileNumber mobileNumber = new MobileNumber();
                        mobileNumber.setMnmRecordIndexId(mnm_id);
                        mobileNumber.setMnmMobileNumber(mobile_num);
                        mobileNumber.setMnmNumberType(getString(R.string.type_mobile));
                        mobileNumber.setMnmNumberPrivacy(String.valueOf(1));
                        mobileNumber.setMnmIsPrivate(0);
                        mobileNumber.setRcProfileMasterPmId(rcp_pm_id);
                        mobileNumber.setMnmIsPrimary(String.valueOf(IntegerConstants
                                .RCP_TYPE_PRIMARY));
                        arrayListMobileNumber.add(mobileNumber);
                        tableMobileMaster.addArrayMobileNumber(arrayListMobileNumber);
                        Log.i(TAG, "Name:" + first_name + " " + last_name + " Number: " +
                                mobile_num + "become RCP");
                    }
                    return;
                }

                TableNotificationStateMaster notificationStateMaster = new
                        TableNotificationStateMaster(databaseHandler);
                NotificationStateData notificationStateData = new NotificationStateData();
                notificationStateData.setNotificationState(Integer.parseInt(m.get("unm_status")));
                notificationStateData.setCloudNotificationId(m.get("unm_id"));
                TableCommentMaster tableCommentMaster = new TableCommentMaster(databaseHandler);
                Comment comment = new Comment();
                switch (api) {
                    case "profileRatingComment":
                        comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                        comment.setCrmType("Rating");
                        comment.setCrmCloudPrId(m.get("pr_id"));
                        comment.setCrmRating(m.get("pr_rating_stars"));
                        comment.setRcProfileMasterPmId(Integer.parseInt(m.get("pr_from_pm_id")));
                        comment.setCrmComment(m.get("pr_comment"));
                        comment.setCrmProfileDetails(m.get("name"));
                        comment.setCrmImage(m.get("pm_profile_photo"));
                        comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(m.get("created_at")));
                        comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
                        String avgRating = m.get("profile_rating");
                        String totalUniqueRater = m.get("total_profile_rate_user");
                        String toPmId = m.get("pr_to_pm_id");

                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        tableProfileMaster.updateUserProfileRating(toPmId, avgRating,
                                totalUniqueRater);
                        Utils.setStringPreference(this, AppConstants.PREF_USER_TOTAL_RATING,
                                totalUniqueRater);
                        Utils.setStringPreference(this, AppConstants.PREF_USER_RATING, avgRating);

                        notificationStateData.setCreatedAt(comment.getCrmCreatedAt());
                        notificationStateData.setUpdatedAt(comment.getCrmUpdatedAt());
                        notificationStateData.setNotificationType(AppConstants
                                .NOTIFICATION_TYPE_TIMELINE);
                        int id = tableCommentMaster.addComment(comment);
                        if (id != -1) {
                            notificationStateData.setNotificationMasterId(m.get("pr_id"));
                            notificationStateMaster.addNotificationState(notificationStateData);
                            if (!Utils.getBooleanPreference(this, AppConstants.PREF_DISABLE_PUSH,
                                    false))
                                sendNotification(msg, AppConstants.NOTIFICATION_TYPE_TIMELINE);
                            else
                                sendBroadcastForCountupdate();
                        }
                        break;
                    case "profileRatingReply":
                        int isUpdated = tableCommentMaster.addReply(m.get("pr_id"), m.get
                                        ("pr_reply"),
                                Utils.getLocalTimeFromUTCTime(m.get("reply_at")), Utils
                                        .getLocalTimeFromUTCTime(m.get("reply_at")));

                        notificationStateData.setCreatedAt(m.get("reply_at"));
                        notificationStateData.setUpdatedAt(m.get("reply_at"));
                        notificationStateData.setNotificationType(AppConstants
                                .NOTIFICATION_TYPE_RATE);
                        if (isUpdated > 0) {
                            notificationStateData.setNotificationMasterId(m.get("pr_id"));
                            notificationStateMaster.addNotificationState(notificationStateData);
                            if (!Utils.getBooleanPreference(this, AppConstants.PREF_DISABLE_PUSH,
                                    false))
                                sendNotification(msg, AppConstants.NOTIFICATION_TYPE_RATE);
                            else
                                sendBroadcastForCountupdate();
                        }
                        break;
                    case "eventComment":
                        comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
                        comment.setCrmType(m.get("type"));
                        comment.setCrmCloudPrId(m.get("id"));
                        comment.setCrmProfileDetails(m.get("name"));
                        comment.setCrmImage(m.get("pm_profile_photo"));
                        comment.setRcProfileMasterPmId(Integer.parseInt(m.get("from_pm_id")));
                        comment.setCrmComment(m.get("comment"));
                        comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(m.get
                                ("created_date")));
                        comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
                        comment.setEvmRecordIndexId(m.get("event_record_index_id"));
                        int eventId = tableCommentMaster.addComment(comment);
                        if (eventId != -1) {
                            notificationStateData.setCreatedAt(m.get("created_date"));
                            notificationStateData.setUpdatedAt(m.get("updated_at"));
                            notificationStateData.setNotificationType(AppConstants
                                    .NOTIFICATION_TYPE_TIMELINE);
                            notificationStateData.setNotificationMasterId(m.get("id"));
                            notificationStateMaster.addNotificationState(notificationStateData);
                            if (!Utils.getBooleanPreference(this, AppConstants.PREF_DISABLE_PUSH,
                                    false)) {
                                if (!Utils.getBooleanPreference(this, AppConstants
                                        .PREF_DISABLE_EVENT_PUSH, false))
                                    sendNotification(msg, AppConstants.NOTIFICATION_TYPE_TIMELINE);
                                else
                                    sendBroadcastForCountupdate();
                            } else
                                sendBroadcastForCountupdate();

                        }
                        break;
                    case "eventReply":
                        int updatedEvents = tableCommentMaster.addReply(m.get("id"), m.get("reply"),
                                Utils.getLocalTimeFromUTCTime(m.get("reply_date")), Utils
                                        .getLocalTimeFromUTCTime(m.get("reply_date")));
                        if (updatedEvents > 0) {
                            notificationStateData.setCreatedAt(m.get("reply_date"));
                            notificationStateData.setUpdatedAt(m.get("reply_date"));
                            notificationStateData.setNotificationType(AppConstants
                                    .NOTIFICATION_TYPE_COMMENTS);
                            notificationStateData.setNotificationMasterId(m.get("id"));
                            notificationStateMaster.addNotificationState(notificationStateData);
                            if (!Utils.getBooleanPreference(this, AppConstants.PREF_DISABLE_PUSH,
                                    false)) {
                                if (!Utils.getBooleanPreference(this, AppConstants
                                        .PREF_DISABLE_EVENT_PUSH, false))
                                    sendNotification(msg, AppConstants.NOTIFICATION_TYPE_COMMENTS);
                                else
                                    sendBroadcastForCountupdate();
                            } else
                                sendBroadcastForCountupdate();
                        }
                        break;
                    case "sendContactRequest":
                        TableRCContactRequest tableRCContactRequest = new TableRCContactRequest
                                (databaseHandler);
                        if (m.get("car_pm_id_to").equals(Utils.getStringPreference(this,
                                AppConstants
                                .PREF_USER_PM_ID, "0"))
                                && m.get("car_access_permission_status").equals("0")) {
                            int requestId = tableRCContactRequest.addRequest(AppConstants
                                            .COMMENT_STATUS_RECEIVED,
                                    m.get("car_id"),
                                    m.get("car_mongodb_record_index"),
                                    Integer.parseInt(m.get("car_pm_id_from")),
                                    m.get("car_ppm_particular_text"),
                                    Utils.getLocalTimeFromUTCTime(m.get("created_at")),
                                    Utils.getLocalTimeFromUTCTime(m.get("updated_at")),
                                    m.get("name"), m.get("pm_profile_photo"));
                            if (requestId != -1) {
                                notificationStateData.setCreatedAt(m.get("created_at"));
                                notificationStateData.setUpdatedAt(m.get("updated_at"));
                                notificationStateData.setNotificationType(AppConstants
                                        .NOTIFICATION_TYPE_PROFILE_REQUEST);
                                notificationStateData.setNotificationMasterId(m.get("car_id"));
                                notificationStateMaster.addNotificationState(notificationStateData);
                                if (!Utils.getBooleanPreference(this, AppConstants
                                        .PREF_DISABLE_PUSH, false))
                                    sendNotification(msg, AppConstants
                                            .NOTIFICATION_TYPE_PROFILE_REQUEST);
                                else
                                    sendBroadcastForCountupdate();
                            }
                        }
                        break;
                    case "acceptContactRequest":
                        TableRCContactRequest tableRCContactRequest1 = new TableRCContactRequest
                                (databaseHandler);
                        if (m.get("car_pm_id_from").equals(Utils.getStringPreference(this,
                                AppConstants.PREF_USER_PM_ID, "0"))
                                && m.get("car_access_permission_status").equals("1")) {
                            int requestId = tableRCContactRequest1.addRequest(AppConstants
                                            .COMMENT_STATUS_SENT,
                                    m.get("car_id"),
                                    m.get("car_mongodb_record_index"),
                                    Integer.parseInt(m.get("car_pm_id_to")),
                                    m.get("car_ppm_particular_text"),
                                    Utils.getLocalTimeFromUTCTime(m.get("created_at")),
                                    Utils.getLocalTimeFromUTCTime(m.get("updated_at")),
                                    m.get("name"), m.get("pm_profile_photo"));
                            if (requestId != -1) {
                                notificationStateData.setCreatedAt(m.get("created_at"));
                                notificationStateData.setUpdatedAt(m.get("updated_at"));
                                notificationStateData.setNotificationType(AppConstants
                                        .NOTIFICATION_TYPE_PROFILE_RESPONSE);
                                notificationStateData.setNotificationMasterId(m.get("car_id"));
                                notificationStateMaster.addNotificationState(notificationStateData);
                                ObjectMapper mapper = new ObjectMapper();
                                String data = m.get("car_contact");
                                try {
                                    if (data != null) {
                                        ContactRequestData obj = mapper.readValue(data,
                                                ContactRequestData.class);
                                        updatePrivacySetting(m.get("car_ppm_particular"), m.get
                                                ("car_mongodb_record_index"), obj, databaseHandler);
                                    }
                                    if (!Utils.getBooleanPreference(this, AppConstants.PREF_DISABLE_PUSH, false))
                                        sendNotification(msg, AppConstants
                                                .NOTIFICATION_TYPE_PROFILE_RESPONSE);
                                    else
                                        sendBroadcastForCountupdate();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                }
                updateNotificationCount(databaseHandler);
            }
        }
    }

    private ArrayList<String> getRawIdFromNumber(String phoneNumber) {
        String numberId = "";
        Set<String> set = new HashSet<>();
        try {

            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = getContentResolver().query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            String rawId = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                            set.add(rawId);
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>(set);
    }

    private void updateNotificationCount(DatabaseHandler databaseHandler) {
        TableNotificationStateMaster notificationStateMaster = new TableNotificationStateMaster
                (databaseHandler);
        int badgeCount = notificationStateMaster.getTotalUnreadCount();
        ShortcutBadger.applyCount(this.getApplicationContext(), badgeCount);

    }

    private void updatePrivacySetting(String ppmTag, String cloudMongoId, ContactRequestData obj,
                                      DatabaseHandler databaseHandler) {
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

    private void sendNotification(String messageBody, int type) {
        Class aClass = MainActivity.class;
        int tabIndex = 0;
        int subTabIndex = 0;
        switch (type) {
            case AppConstants.NOTIFICATION_TYPE_TIMELINE:
                aClass = TimelineActivity.class;
                break;
            case AppConstants.NOTIFICATION_TYPE_PROFILE_REQUEST:
                aClass = NotificationsDetailActivity.class;
                tabIndex = NotificationsDetailActivity.TAB_REQUEST;
                subTabIndex = 0;
                break;
            case AppConstants.NOTIFICATION_TYPE_PROFILE_RESPONSE:
                aClass = NotificationsDetailActivity.class;
                tabIndex = NotificationsDetailActivity.TAB_REQUEST;
                subTabIndex = 1;
                break;
            case AppConstants.NOTIFICATION_TYPE_RATE:
                aClass = NotificationsDetailActivity.class;
                tabIndex = NotificationsDetailActivity.TAB_RATING;
                break;
            case AppConstants.NOTIFICATION_TYPE_COMMENTS:
                aClass = NotificationsDetailActivity.class;
                tabIndex = NotificationsDetailActivity.TAB_COMMENTS;
                break;
            case AppConstants.NOTIFICATION_TYPE_RUPDATE:
                aClass = NotificationsDetailActivity.class;
                tabIndex = NotificationsDetailActivity.TAB_RCONTACTS;
                break;
            case AppConstants.NOTIFICATION_TYPE_RELATION_REQUEST:
                aClass = RelationRecommendationActivity.class;
                break;
            case AppConstants.NOTIFICATION_TYPE_RELATION_ACCEPT:
                aClass = ExistingRelationActivity.class;
                break;

        }
        Intent intent = new Intent(this, aClass);
        intent.putExtra("TAB_INDEX", tabIndex);
        intent.putExtra("SUB_TAB_INDEX", subTabIndex);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("RContacts")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.ic_notification_flat);
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Utils.getID(), notificationBuilder.build());
        sendBroadcastForCountupdate();
    }

    private void sendBroadcastForCountupdate() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(AppConstants.ACTION_LOCAL_BROADCAST_UPDATE_NOTIFICATION_COUNT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
