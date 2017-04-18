package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRatingItem {

    private String raterName;
    private String rating;
    private String notiTime;
    private String comment;
    private String commentTime;
    private String reply;
    private String replyTime;
    private int historyType;
    private String receiverPersonName;

    public String getReceiverPersonName() {
        return receiverPersonName;
    }

    public void setReceiverPersonName(String receiverPersonName) {
        this.receiverPersonName = receiverPersonName;
    }

    public int getHistoryType() {
        return historyType;
    }

    public void setHistoryType(int historyType) {
        this.historyType = historyType;
    }


    public String getRaterName() {
        return raterName;
    }

    public void setRaterName(String raterName) {
        this.raterName = raterName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNotiTime() {
        return notiTime;
    }

    public void setNotiTime(String notiTime) {
        this.notiTime = notiTime;
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
}
