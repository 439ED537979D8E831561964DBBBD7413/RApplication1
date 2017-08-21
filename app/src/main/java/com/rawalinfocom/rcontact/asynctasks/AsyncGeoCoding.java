package com.rawalinfocom.rcontact.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.rawalinfocom.rcontact.R;
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
    private Exception error = null;
    private Context context;
    private WsResponseListener wsResponseListener;
    private ReverseGeocodingAddress objAddress = null;
    private String serviceType;
    private boolean showProgress;

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
            Utils.showProgressDialog(context, context.getString(R.string.str_fetching_address),
                    true);
        }
    }

    @Override
    protected Object doInBackground(String... params) {
        try {

            Geocoder geocoder = new Geocoder(context);

            List<Address> addresses = null;
            String addressText = "";

            String paramAddress = params[0];
            String paramCity = params[1];
            String paramLatitude = params[2];
            String paramLongitude = params[3];

            try {
                if (paramAddress != null) {
                    addresses = geocoder.getFromLocationName(paramAddress, 1);
                    if (addresses.size() <= 0) {
                        addresses = geocoder.getFromLocationName(paramCity, 1);
                    }
                } else {
                    addresses = geocoder.getFromLocation(Double.parseDouble(paramLatitude), Double
                            .parseDouble(paramLongitude), 1);
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

                if (paramLatitude != null) {
                    objAddress.setLatitude(paramLatitude);
                } else {
                    objAddress.setLatitude(StringUtils.defaultString(Double.toString(address
                            .getLatitude()), "0"));
                }
                if (paramLongitude != null) {
                    objAddress.setLongitude(paramLongitude);
                } else {
                    objAddress.setLongitude(StringUtils.defaultString(Double.toString(address
                            .getLongitude()), "0"));
                }

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
