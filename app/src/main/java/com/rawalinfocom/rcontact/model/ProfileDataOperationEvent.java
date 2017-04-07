package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIgnoreType
public class ProfileDataOperationEvent implements Serializable {

    private String eventId;
    private String eventType;
    private String eventDate;
    private String eventDateTime;
    private int eventPublic;

    //    private int eventRcType;
    private String eventRcType;
    private int isYearHidden;

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

   /* @JsonProperty("event_date")
    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }*/

    @JsonProperty("event_datetime")
    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    @JsonProperty("event_public")
    public int getEventPublic() {
        return this.eventPublic;
    }

    public void setEventPublic(int eventPublic) {
        this.eventPublic = eventPublic;
    }

    /*public int getEventRcType() {
        return eventRcType;
    }

    public void setEventRcType(int eventRcType) {
        this.eventRcType = eventRcType;
    }*/

    public String getEventRcType() {
        return eventRcType;
    }

    public void setEventRcType(String eventRcType) {
        this.eventRcType = eventRcType;
    }

    @JsonProperty("is_year_hidden")
    public int getIsYearHidden() {
        return isYearHidden;
    }

    public void setIsYearHidden(int isYearHidden) {
        this.isYearHidden = isYearHidden;
    }
}
