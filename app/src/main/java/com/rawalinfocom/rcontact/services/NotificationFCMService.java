package com.rawalinfocom.rcontact.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.database.DatabaseHandler;
import com.rawalinfocom.rcontact.database.TableCommentMaster;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.Comment;
import com.rawalinfocom.rcontact.model.EventComment;

import java.util.Map;

/**
 * Created by user on 06/03/17.
 */

public class NotificationFCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Handler handler = new Handler(Looper.getMainLooper());


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> m = remoteMessage.getData();
            String api = m.get("API");

            String pr_status = m.get("pr_status");
            String pr_from_pm_id = m.get("pr_from_pm_id");
            String pr_to_pm_id = m.get("pr_to_pm_id");
            String pr_comment = m.get("pr_comment");
            String pr_reply = m.get("pr_reply");
            String profile_rating = m.get("profile_rating");
            String updated_at = m.get("updated_at");
            String pr_id = m.get("pr_id");
            String created_at = m.get("created_at");
            String reply_at = m.get("reply_at");
            String pr_rating_stars = m.get("pr_rating_stars");
            String total_profile_rate_user = m.get("total_profile_rate_user");

//            Log.d(TAG, "api: " + api);
//            Log.d(TAG, "pr_status: " + pr_status);
//            Log.d(TAG, "pr_from_pm_id: " + pr_from_pm_id);
//            Log.d(TAG, "pr_to_pm_id: " + pr_to_pm_id);
//            Log.d(TAG, "pr_comment: " + pr_comment);
//            Log.d(TAG, "pr_reply: " + pr_reply);
//            Log.d(TAG, "profile_rating: " + profile_rating);
//            Log.d(TAG, "updated_at: " + updated_at);
//            Log.d(TAG, "pr_id: " + pr_id);
//            Log.d(TAG, "created_at: " + created_at);
//            Log.d(TAG, "reply_at: " + reply_at);
//            Log.d(TAG, "pr_rating_stars: " + pr_rating_stars);
//            Log.d(TAG, "total_profile_rate_user: " + total_profile_rate_user);
//
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            final String x = "Message data payload: " + remoteMessage.getData();


            Comment comment = new Comment();
            comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
            comment.setCrmType(getResources().getString(R.string.text_rating));
            comment.setCrmCloudPrId(pr_id);
            comment.setCrmRating(pr_rating_stars);
            comment.setRcProfileMasterPmId(Integer.parseInt(pr_from_pm_id));
            comment.setCrmComment(pr_comment);
            comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(created_at));
            comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(updated_at));
            TableCommentMaster  tableCommentMaster = new TableCommentMaster(new DatabaseHandler(this));
            if (api.equalsIgnoreCase("profileRatingComment")) {
                tableCommentMaster.addComment(comment);
            }
            if (api.equalsIgnoreCase("profileRatingReply")) {
                tableCommentMaster.addReply(pr_id, pr_reply,
                        Utils.getLocalTimeFromUTCTime(reply_at), Utils.getLocalTimeFromUTCTime(updated_at));
            }
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Notification Received\n" + x, Toast.LENGTH_SHORT).show();
                }
            });

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            final String y = "Message Notification Body: " + remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handler.postDelayed(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Notification Received\n" + y, Toast.LENGTH_SHORT).show();
                }
            }, 2000);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    }

    private Comment createComment(EventComment eventComment, String commentType) {
        Comment comment = new Comment();
        comment.setCrmStatus(AppConstants.COMMENT_STATUS_RECEIVED);
        comment.setCrmRating("");
        comment.setCrmType(commentType);
        if (commentType.equalsIgnoreCase(getResources().getString(R.string.text_rating))) {
            comment.setCrmCloudPrId(eventComment.getPrId());
            comment.setCrmRating(eventComment.getRatingStars());
        } else {
            comment.setCrmCloudPrId(eventComment.getId());
        }
        comment.setRcProfileMasterPmId(eventComment.getFromPmId());
        comment.setCrmComment(eventComment.getComment());
        comment.setCrmReply(eventComment.getReply());
        comment.setCrmCreatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getCreatedDate()));
        comment.setCrmRepliedAt(Utils.getLocalTimeFromUTCTime(eventComment.getReplyAt()));
        comment.setCrmUpdatedAt(Utils.getLocalTimeFromUTCTime(eventComment.getUpdatedDate()));
        comment.setEvmRecordIndexId(eventComment.getEventRecordIndexId() + "");
        return comment;
    }
}
