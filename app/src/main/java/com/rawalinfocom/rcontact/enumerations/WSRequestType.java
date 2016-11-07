package com.rawalinfocom.rcontact.enumerations;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Enumeration to define whether Request parameters for Web Service Post type would be Json or
 * Content-value pair
 */

public enum WSRequestType {

    REQUEST_TYPE_JSON(0),
    REQUEST_TYPE_CONTENT_VALUE(1);

    private int value;

    WSRequestType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
