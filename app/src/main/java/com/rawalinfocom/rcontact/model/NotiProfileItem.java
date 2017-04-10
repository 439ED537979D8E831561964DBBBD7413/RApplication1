package com.rawalinfocom.rcontact.model;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiProfileItem {

    private String personName;
    private String notiInfo;
    private String notiRequestTime;
    private int profileNotiType;

    public String getRcpUserPmId() {
        return rcpUserPmId;
    }

    public void setRcpUserPmId(String rcpUserPmId) {
        this.rcpUserPmId = rcpUserPmId;
    }

    private String rcpUserPmId;


    public int getCardCloudId() {
        return cardCloudId;
    }

    public void setCardCloudId(int cardCloudId) {
        this.cardCloudId = cardCloudId;
    }

    private int cardCloudId;

    public int getProfileNotiType() {
        return profileNotiType;
    }

    public void setProfileNotiType(int profileNotiType) {
        this.profileNotiType = profileNotiType;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setNotiInfo(String notiInfo) {
        this.notiInfo = notiInfo;
    }

    public void setNotiRequestTime(String notiRequestTime) {
        this.notiRequestTime = notiRequestTime;
    }

    public String getPersonName() {
        return personName;
    }

    public String getNotiInfo() {
        return notiInfo;
    }

    public String getNotiRequestTime() {
        return notiRequestTime;
    }
}
