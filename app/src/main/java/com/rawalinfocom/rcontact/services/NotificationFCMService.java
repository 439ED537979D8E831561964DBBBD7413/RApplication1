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
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        // Check if message contains a data payload.

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> m = remoteMessage.getData();

            String api = m.get("API");
            if (api == null) {
                return;
            }
            TableCommentMaster  tableCommentMaster = new TableCommentMaster(new DatabaseHandler(this));
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
                    tableCommentMaster.addComment(comment);
                    break;
                case "profileRatingReply":
                    tableCommentMaster.addReply(m.get("pr_id"), m.get("pr_reply"),
                            Utils.getLocalTimeFromUTCTime(m.get("reply_at")), Utils.getLocalTimeFromUTCTime(m.get("updated_at")));
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
                    break;
                case "eventReply":
                    tableCommentMaster.addReply(m.get("id"), m.get("reply"),
                            Utils.getLocalTimeFromUTCTime(m.get("reply_date")), Utils.getLocalTimeFromUTCTime(m.get("updated_date")));
                    break;
            }

            final String x = "Message data payload: " + remoteMessage.getData();

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
