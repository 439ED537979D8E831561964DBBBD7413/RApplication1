package com.rawalinfocom.rcontact.model;

/**
 * Created by user on 29/11/16.
 */

public class Education {

    private String edmId;
    private String edmRecordIndexId;
    private String edmSchoolCollegeName;
    private String edmCourse;
    private String edmEducationFromDate;
    private String edmEducationToDate;
    private Integer edmEducationIsCurrent;
    private Integer edmEducationIsPrivate;
    private String edmEducationPrivacy;
    private String rcProfileMasterPmId;

    public String getEdmId() {
        return edmId;
    }

    public void setEdmId(String edmId) {
        this.edmId = edmId;
    }

    public String getEdmRecordIndexId() {
        return edmRecordIndexId;
    }

    public void setEdmRecordIndexId(String edmRecordIndexId) {
        this.edmRecordIndexId = edmRecordIndexId;
    }

    public String getEdmSchoolCollegeName() {
        return edmSchoolCollegeName;
    }

    public void setEdmSchoolCollegeName(String edmSchoolCollegeName) {
        this.edmSchoolCollegeName = edmSchoolCollegeName;
    }

    public String getEdmCourse() {
        return edmCourse;
    }

    public void setEdmCourse(String edmCourse) {
        this.edmCourse = edmCourse;
    }

    public String getEdmEducationFromDate() {
        return edmEducationFromDate;
    }

    public void setEdmEducationFromDate(String edmEducationFromDate) {
        this.edmEducationFromDate = edmEducationFromDate;
    }

    public String getEdmEducationToDate() {
        return edmEducationToDate;
    }

    public void setEdmEducationToDate(String edmEducationToDate) {
        this.edmEducationToDate = edmEducationToDate;
    }

    public Integer getEdmEducationIsCurrent() {
        return edmEducationIsCurrent;
    }

    public void setEdmEducationIsCurrent(Integer edmEducationIsCurrent) {
        this.edmEducationIsCurrent = edmEducationIsCurrent;
    }

    public Integer getEdmEducationIsPrivate() {
        return edmEducationIsPrivate;
    }

    public void setEdmEducationIsPrivate(Integer edmEducationIsPrivate) {
        this.edmEducationIsPrivate = edmEducationIsPrivate;
    }

    public String getEdmEducationPrivacy() {
        return edmEducationPrivacy;
    }

    public void setEdmEducationPrivacy(String edmEducationPrivacy) {
        this.edmEducationPrivacy = edmEducationPrivacy;
    }

    public String getRcProfileMasterPmId() {
        return rcProfileMasterPmId;
    }

    public void setRcProfileMasterPmId(String rcProfileMasterPmId) {
        this.rcProfileMasterPmId = rcProfileMasterPmId;
    }
}
