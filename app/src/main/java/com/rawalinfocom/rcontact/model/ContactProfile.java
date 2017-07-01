package com.rawalinfocom.rcontact.model;

public class ContactProfile {

    private Integer pm_id;
    private ProfileData[] data;

    public Integer getPmId() {return this.pm_id;}

    public void setPmId(Integer pm_id) {this.pm_id = pm_id;}

    public ProfileData[] getData() {return this.data;}

    public void setData(ProfileData[] data) {this.data = data;}

}
