package com.rawalinfocom.rcontact.model;

import java.util.ArrayList;

/**
 * Created by Monal on 18/11/16.
 */

public class UserContact {

    UserProfile userProfile;
    ArrayList<MobileNumber> arrayListMobileNumber;
    ArrayList<Email> arrayListEmail;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public ArrayList<MobileNumber> getArrayListMobileNumber() {
        return arrayListMobileNumber;
    }

    public void setArrayListMobileNumber(ArrayList<MobileNumber> arrayListMobileNumber) {
        this.arrayListMobileNumber = arrayListMobileNumber;
    }

    public ArrayList<Email> getArrayListEmail() {
        return arrayListEmail;
    }

    public void setArrayListEmail(ArrayList<Email> arrayListEmail) {
        this.arrayListEmail = arrayListEmail;
    }
}
