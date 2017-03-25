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
    private String eventDate;
    private int eventPublic;

    //    private int eventRcType;
    private String eventRcType;

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

//    @JsonProperty("event_date")
    @JsonProperty("event_datetime")
    public String getEventDate() {
        return StringUtils.defaultString(this.eventDate);
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
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
}
