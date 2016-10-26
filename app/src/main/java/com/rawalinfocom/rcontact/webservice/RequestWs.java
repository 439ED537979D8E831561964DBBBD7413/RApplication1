package com.rawalinfocom.rcontact.webservice;

import android.content.ContentValues;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * A Class to invoke Get or Post Request
 */

public class RequestWs {

    /**
     * Get Request
     **/
    public <CLS> CLS getGetRequest(String url, Class<CLS> cls) throws Exception {
//        return new WebServiceRequestGet(url).execute(cls);
        return new WebServiceGet(url).execute(cls);
    }


    /**
     * Post HttpUrlConnection Request
     **/
    public <CLS> CLS getPostRequest(String url, int requestType, Object reqCls, Class<CLS> cls,
                                    ContentValues contentValues) throws Exception {
        return new WebServicePost(url, requestType).execute(cls, reqCls, contentValues);
    }

}
