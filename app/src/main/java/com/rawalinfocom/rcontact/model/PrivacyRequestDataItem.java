package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 7/4/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class PrivacyRequestDataItem {
    @JsonProperty("car_pm_id_from")
    private Integer carPmIdFrom;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("car_mongodb_record_index")
    private String carMongodbRecordIndex;

    @JsonProperty("unm_status")
    private String unmStatus;

    @JsonProperty("created_by")
    private Integer createdBy;

    @JsonProperty("unm_id")
    private Integer unmId;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("car_access_permission_status")
    private Integer carAccessPermissionStatus;

    @JsonProperty("car_pm_id_to")
    private Integer carPmIdTo;

    @JsonProperty("car_id")
    private Integer carId;

    @JsonProperty("car_ppm_id")
    private Integer carPpmId;

    @JsonProperty("car_sent_on_cloud")
    private Integer carSentOnCloud;


    public String getPpmParticular() {
        return ppmParticular;
    }

    public void setPpmParticular(String ppmParticular) {
        this.ppmParticular = ppmParticular;
    }

    @JsonProperty("ppm_particular")
    private String ppmParticular;


    public String getPpmTag() {
        return ppmTag;
    }

    public void setPpmTag(String ppmTag) {
        this.ppmTag = ppmTag;
    }


    @JsonProperty("ppt_tag")
    private String ppmTag;


    public Integer getCarPmIdFrom() {
        return carPmIdFrom;
    }

    public void setCarPmIdFrom(Integer carPmIdFrom) {
        this.carPmIdFrom = carPmIdFrom;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCarMongodbRecordIndex() {
        return carMongodbRecordIndex;
    }

    public void setCarMongodbRecordIndex(String carMongodbRecordIndex) {
        this.carMongodbRecordIndex = carMongodbRecordIndex;
    }

    public String getUnmStatus() {
        return unmStatus;
    }

    public void setUnmStatus(String unmStatus) {
        this.unmStatus = unmStatus;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUnmId() {
        return unmId;
    }

    public void setUnmId(Integer unmId) {
        this.unmId = unmId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCarAccessPermissionStatus() {
        return carAccessPermissionStatus;
    }

    public void setCarAccessPermissionStatus(Integer carAccessPermissionStatus) {
        this.carAccessPermissionStatus = carAccessPermissionStatus;
    }

    public Integer getCarPmIdTo() {
        return carPmIdTo;
    }

    public void setCarPmIdTo(Integer carPmIdTo) {
        this.carPmIdTo = carPmIdTo;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getCarPpmId() {
        return carPpmId;
    }

    public void setCarPpmId(Integer carPpmId) {
        this.carPpmId = carPpmId;
    }

    public Integer getCarSentOnCloud() {
        return carSentOnCloud;
    }

    public void setCarSentOnCloud(Integer carSentOnCloud) {
        this.carSentOnCloud = carSentOnCloud;
    }
}