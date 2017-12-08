package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationEducation implements Serializable {

    private String eduId;
    private String eduName;
    private String eduCourse;
    private Integer isCurrent;
    private String eduFromDate;
    private String eduToDate;
    private Integer eduPublic;
    private Integer isPrivate;

    @JsonProperty("edu_id")
    public String getEduId() {
        return eduId;
    }

    public void setEduId(String eduId) {
        this.eduId = eduId;
    }

    @JsonProperty("edu_name")
    public String getEduName() {
        return eduName;
    }

    public void setEduName(String eduName) {
        this.eduName = eduName;
    }

    @JsonProperty("edu_course")
    public String getEduCourse() {
        return eduCourse;
    }

    public void setEduCourse(String eduCourse) {
        this.eduCourse = eduCourse;
    }

    @JsonProperty("is_current")
    public Integer getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Integer isCurrent) {
        this.isCurrent = isCurrent;
    }

    @JsonProperty("edu_from_date")
    public String getEduFromDate() {
        return eduFromDate;
    }

    public void setEduFromDate(String eduFromDate) {
        this.eduFromDate = eduFromDate;
    }

    @JsonProperty("edu_to_date")
    public String getEduToDate() {
        return eduToDate;
    }

    public void setEduToDate(String eduToDate) {
        this.eduToDate = eduToDate;
    }

    @JsonProperty("edu_public")
    public Integer getEduPublic() {
        return eduPublic;
    }

    public void setEduPublic(Integer eduPublic) {
        this.eduPublic = eduPublic;
    }

    @JsonProperty("is_private")
    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }
}
