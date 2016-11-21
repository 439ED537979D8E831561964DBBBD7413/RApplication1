package com.rawalinfocom.rcontact.model;

public class ProfileDataOperationAddress {
    private String country;
    private String address_type;
    private String city;
    private String post_code;
    private String Street;
    private String google_log;
    private String google_address;
    private String neighborhood;
    private String state;
    private String po_box;
    private String google_lat;
    private String formatted_address;

    public String getCountry() {return this.country;}

    public void setCountry(String country) {this.country = country;}

    public String getAddressType() {return this.address_type;}

    public void setAddressType(String address_type) {this.address_type = address_type;}

    public String getCity() {return this.city;}

    public void setCity(String city) {this.city = city;}

    public String getPostCode() {return this.post_code;}

    public void setPostCode(String post_code) {this.post_code = post_code;}

    public String getStreet() {return this.Street;}

    public void setStreet(String Street) {this.Street = Street;}

    public String getGoogleLog() {return this.google_log;}

    public void setGoogleLog(String google_log) {this.google_log = google_log;}

    public String getGoogleAddress() {return this.google_address;}

    public void setGoogleAddress(String googledddress) {this.google_address = googledddress;}

    public String getNeighborhood() {return this.neighborhood;}

    public void setNeighborhood(String neighborhood) {this.neighborhood = neighborhood;}

    public String getState() {return this.state;}

    public void setState(String state) {this.state = state;}

    public String getPoBox() {return this.po_box;}

    public void setPoBox(String po_box) {this.po_box = po_box;}

    public String getGoogleLat() {return this.google_lat;}

    public void setGoogleLat(String google_lat) {this.google_lat = google_lat;}

    public String getFormattedAddress() {
        return formatted_address;
    }

    public void setFormattedAddress(String formatted_address) {
        this.formatted_address = formatted_address;
    }
}
