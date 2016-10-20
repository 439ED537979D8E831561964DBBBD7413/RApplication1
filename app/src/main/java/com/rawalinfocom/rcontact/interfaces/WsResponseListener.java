package com.rawalinfocom.rcontact.interfaces;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Single Listener for All web services call.
 * This is Interface, used for All the web services Response.
 * We can implement this to get response of web services with any activity or fragment.
 */

public interface WsResponseListener {

    /**
     * @param serviceType serviceType set by web service
     * @param data        data returned by service
     * @param error       error, if any
     */
    void onDeliveryResponse(String serviceType, Object data, Exception error);

}
