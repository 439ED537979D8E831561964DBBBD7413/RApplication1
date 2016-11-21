package com.rawalinfocom.rcontact.model;

import java.util.ArrayList;

public class ProfileData {

    private String local_phonebook_id;
    private String givenName;
    private ArrayList<ProfileDataOperation> operation;

    public String getLocalPhonebookId() {return this.local_phonebook_id;}

    public void setLocalPhonebookId(String local_phonebook_id) {
        this.local_phonebook_id = local_phonebook_id;
    }

    public ArrayList<ProfileDataOperation> getOperation() {
        return operation;
    }

    public void setOperation(ArrayList<ProfileDataOperation> operation) {
        this.operation = operation;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
}
