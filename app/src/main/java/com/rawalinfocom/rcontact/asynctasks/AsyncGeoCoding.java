package com.rawalinfocom.rcontact.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by user on 21/03/17.
 */

public class AsyncGeoCoding extends AsyncTask<String, Void, Object> {

    private final String LOG_TAG = "AsyncGeoCoding";
    Exception error = null;

    Context context;

    WsResponseListener wsResponseListener;

    ReverseGeocodingAddress objAddress = null;

    String serviceType;

    boolean showProgress;

    public AsyncGeoCoding(Context context, boolean showProgress, String serviceType) {
        this.context = context;
        this.serviceType = serviceType;
        objAddress = new ReverseGeocodingAddress();
        this.showProgress = showProgress;
        wsResponseListener = (WsResponseListener) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showProgress) {
            Utils.showProgressDialog(context, "Fetching address...", true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        try {

            Geocoder geocoder = new Geocoder(context);

            List<Address> addresses = null;
            String addressText = "";

            try {
                if (params[0] != null) {
                    addresses = geocoder.getFromLocationName(params[0], 1);
                } else {
                    addresses = geocoder.getFromLocation(Double.parseDouble(params[1]), Double
                            .parseDouble(params[2]), 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                if (address.getAddressLine(0) != null && address.getAddressLine(0).length() > 0) {
                    addressText = address.getAddressLine(0);
                }

                if (address.getLocality() != null && address.getLocality().length() > 0) {
                    if (addressText.length() > 0) {
                        addressText += ", " + address.getLocality();
                    } else {
                        addressText = address.getLocality();
                    }

                }

                if (address.getAdminArea() != null && address.getAdminArea().length() > 0) {
                    if (addressText.length() > 0) {
                        addressText += ", " + address.getAdminArea();
                    } else {
                        addressText = address.getAdminArea();
                    }
                }

                if (address.getCountryName() != null && address.getCountryName().length() > 0) {
                    if (addressText.length() > 0) {
                        addressText += ", " + address.getCountryName();
                    } else {
                        addressText = address.getCountryName();
                    }
                }

                if (address.getPostalCode() != null && address.getPostalCode().length() > 0) {
                    if (addressText.length() > 0) {
                        addressText += " - " + address.getPostalCode();
                    } else {
                        addressText = address.getPostalCode();
                    }
                }

                objAddress.setLatitude(StringUtils.defaultString(Double.toString(address
                        .getLatitude()), "0"));
                objAddress.setLongitude(StringUtils.defaultString(Double.toString(address
                        .getLongitude()), "0"));
                objAddress.setAddress(StringUtils.defaultString(addressText));
                objAddress.setPostalCode(StringUtils.defaultString(address.getPostalCode()));
                objAddress.setAddressLine(StringUtils.defaultString(address.getAddressLine(0)));
                objAddress.setCity(address.getLocality() != null ? address.getLocality() : "");
                objAddress.setState(address.getAdminArea() != null ? address.getAdminArea() : "");
                objAddress.setCountry(address.getCountryName() != null ? address.getCountryName()
                        : "");

            }
            return objAddress;

        } catch (Exception e) {
            this.error = e;
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        Utils.hideProgressDialog();
        wsResponseListener.onDeliveryResponse(serviceType, result, error);
    }

}
