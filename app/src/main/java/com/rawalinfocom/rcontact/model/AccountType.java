package com.rawalinfocom.rcontact.model;

import java.io.Serializable;

/**
 * Created by Aniruddh on 24/07/17.
 */

public class AccountType implements Serializable {

    String itemName;
    String itemIcon;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }
}
