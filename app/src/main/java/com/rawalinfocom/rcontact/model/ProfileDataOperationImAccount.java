package com.rawalinfocom.rcontact.model;

public class ProfileDataOperationImAccount {
    private String IM_account_type;
    private String IM_account_details;
    private String IM_account_protocol;
    private String IM_account_public;

    public String getIMAccountType() {return this.IM_account_type;}

    public void setIMAccountType(String IM_account_type) {this.IM_account_type = IM_account_type;}

    public String getIMAccountDetails() {return this.IM_account_details;}

    public void setIMAccountDetails(String IM_account_details) {this.IM_account_details =
            IM_account_details;}

    public String getIMAccountPublic() {return this.IM_account_public;}

    public void setIMAccountPublic(String IM_account_public) {this.IM_account_public = IM_account_public;}


    public String getIMAccountProtocol() {
        return IM_account_protocol;
    }

    public void setIMAccountProtocol(String IM_account_protocol) {
        this.IM_account_protocol = IM_account_protocol;
    }
}
