package com.rawalinfocom.rcontact.webservicedialog;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

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
    public <CLS> CLS getPostRequest(Context context, String url, int requestType, Object reqCls,
                                    Class<CLS> cls, ContentValues contentValues, boolean
                                            setHeader) throws Exception {
        return new WebServicePost(context, url, requestType, setHeader).execute(cls, reqCls,
                contentValues);
    }

}
