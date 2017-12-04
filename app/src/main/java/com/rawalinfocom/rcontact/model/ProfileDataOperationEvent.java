package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationEvent implements Serializable {

    private String eventId;
    private String eventType;
    private String eventDateTime;
    private String eventDate;
    private Integer eventPublic;
    private String eventRcType;
    private Integer isYearHidden;
//    private Integer isPrivate;

    @JsonProperty("event_id")
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @JsonProperty("event_type")
    public String getEventType() {
        return StringUtils.defaultString(this.eventType);
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty("event_datetime")
    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    @JsonProperty("event_public")
    public Integer getEventPublic() {
        return this.eventPublic;
    }

    public void setEventPublic(Integer eventPublic) {
        this.eventPublic = eventPublic;
    }

    @JsonProperty(value = "event_date", access = JsonProperty.Access.READ_ONLY)
    public String getEventDate() {
        return eventDate;
    }

    @JsonProperty(value = "event_date", access = JsonProperty.Access.READ_ONLY)
    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    /*public Integer getEventRcType() {
        return eventRcType;
    }

    public void setEventRcType(Integer eventRcType) {
        this.eventRcType = eventRcType;
    }*/

    public String getEventRcType() {
        return eventRcType;
    }

    public void setEventRcType(String eventRcType) {
        this.eventRcType = eventRcType;
    }

    @JsonProperty("is_year_hidden")
    public Integer getIsYearHidden() {
        return isYearHidden;
    }

    public void setIsYearHidden(Integer isYearHidden) {
        this.isYearHidden = isYearHidden;
    }

//    @JsonProperty("is_private")
//    public Integer getIsPrivate() {
//        return isPrivate;
//    }

//    public void setIsPrivate(Integer isPrivate) {
//        this.isPrivate = isPrivate;
//    }
}
