package com.rawalinfocom.rcontact.model;

/**
 * Created by Maulik on 7/4/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class ContactRequestResponseDataItem {

    @JsonProperty("car_id")
    private Integer carId;

    @JsonProperty("car_pm_id_from")
    private Integer carPmIdFrom;

    @JsonProperty("car_pm_id_to")
    private Integer carPmIdTo;

    @JsonProperty("car_ppm_id")
    private Integer carPpmId;

    @JsonProperty("car_access_permission_status")
    private Integer carAccessPermissionStatus;

    @JsonProperty("car_mongodb_record_index")
    private String carMongodbRecordIndex;

    @JsonProperty("car_ppm_particular_text")
    private String carPpmParticular;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("name")
    private String Name;

    @JsonProperty("pm_profile_photo")
    private String pmProfilePhoto;


    public String getCarPpmParticular() {
        return carPpmParticular;
    }

    public void setCarPpmParticular(String carPpmParticular) {
        this.carPpmParticular = carPpmParticular;
    }

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

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPmProfilePhoto() {
        return pmProfilePhoto;
    }

    public void setPmProfilePhoto(String pmProfilePhoto) {
        this.pmProfilePhoto = pmProfilePhoto;
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
}