package com.rawalinfocom.rcontact.model;

public class ProfileDataOperationEvent {
    private String event_type;
    private String event_date;
    private String event_public;

    public String getEventType() {return this.event_type;}

    public void setEventType(String event_type) {this.event_type = event_type;}

    public String getEventDate() {return this.event_date;}

    public void setEventDate(String event_date) {this.event_date = event_date;}

    public String getEventPublic() {return this.event_public;}

    public void setEventPublic(String event_public) {this.event_public = event_public;}
}
