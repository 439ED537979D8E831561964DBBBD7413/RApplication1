package com.rawalinfocom.rcontact.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileDataOperationEvent {
    private String eventType;
    private String eventDate;
    private String eventPublic;

    @JsonProperty("event_type")
    public String getEventType() {return this.eventType;}

    public void setEventType(String eventType) {this.eventType = eventType;}

    @JsonProperty("event_date")
    public String getEventDate() {return this.eventDate;}

    public void setEventDate(String eventDate) {this.eventDate = eventDate;}

    @JsonProperty("event_public")
    public String getEventPublic() {return this.eventPublic;}

    public void setEventPublic(String eventPublic) {this.eventPublic = eventPublic;}
}
