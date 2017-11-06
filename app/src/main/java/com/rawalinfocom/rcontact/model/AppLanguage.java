package com.rawalinfocom.rcontact.model;

/**
 * Created by admin on 21/07/17.
 */

public class AppLanguage {

    private String languageName;
    private String languageType;
    private boolean isSelected;

    public AppLanguage(String languageName, String languageType, boolean isSelected) {
        this.languageName = languageName;
        this.languageType = languageType;
        this.isSelected = isSelected;
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public String getLanguageType() {
        return this.languageType;
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }
}
