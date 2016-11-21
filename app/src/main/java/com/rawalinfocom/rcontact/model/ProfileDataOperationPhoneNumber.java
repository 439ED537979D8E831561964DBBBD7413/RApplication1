package com.rawalinfocom.rcontact.model;

public class ProfileDataOperationPhoneNumber {
    private String ph_type;
    private String ph_no;
    private int ph_public;
    private int ph_id;

    public String getPhoneType() {return this.ph_type;}

    public void setPhoneType(String ph_type) {this.ph_type = ph_type;}

    public String getPhoneNumber() {return this.ph_no;}

    public void setPhoneNumber(String ph_no) {this.ph_no = ph_no;}

    public int getPhonePublic() {return this.ph_public;}

    public void setPhonePublic(int ph_public) {this.ph_public = ph_public;}

    public int getPhoneId() {return this.ph_id;}

    public void setPhoneId(int ph_id) {this.ph_id = ph_id;}
}
