package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiCommentsItem {

    private String commenterName;
    private String commenterImage;
    private String commenterInfo;
    private String notiCommentTime;
    private String eventName;
    private String comment;
    private String commentTime;
    private String reply;
    private String replyTime;
    private String receiverPersonImage;

    public String getCommenterName() {
        return commenterName;
    }

    public String getCommenterInfo() {
        return commenterInfo;
    }

    public String getNotiCommentTime() {
        return notiCommentTime;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public void setCommenterInfo(String commenterInfo) {
        this.commenterInfo = commenterInfo;
    }

    public void setNotiCommentTime(String notiCommentTime) {
        this.notiCommentTime = notiCommentTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getCommenterImage() {
        return commenterImage;
    }

    public void setCommenterImage(String commenterImage) {
        this.commenterImage = commenterImage;
    }

    public String getReceiverPersonImage() {
        return receiverPersonImage;
    }

    public void setReceiverPersonImage(String receiverPersonImage) {
        this.receiverPersonImage = receiverPersonImage;
    }
}
