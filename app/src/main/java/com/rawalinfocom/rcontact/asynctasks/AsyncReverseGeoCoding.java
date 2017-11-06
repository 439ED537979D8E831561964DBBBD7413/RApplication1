package com.rawalinfocom.rcontact.asynctasks;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by user on 21/02/17.
 */

public class AsyncReverseGeoCoding extends AsyncTask<LatLng, Void, Object> {

    private final String LOG_TAG = "AsyncReverseGeoCoding";
    private Exception error = null;

    private Context mContext;

    private WsResponseListener wsResponseListener;
    private boolean showProgress;

    private ReverseGeocodingAddress objAddress;

    private String serviceType;

    public AsyncReverseGeoCoding(Context mContext, String serviceType, boolean
            showProgress) {

        this.mContext = mContext;
        this.serviceType = serviceType;
        this.showProgress = showProgress;
        objAddress = new ReverseGeocodingAddress();
        wsResponseListener = (WsResponseListener) mContext;
    }

    public AsyncReverseGeoCoding(Fragment fragment, String serviceType, boolean
            showProgress) {

        this.mContext = fragment.getActivity();
        this.serviceType = serviceType;
        this.showProgress = showProgress;
        objAddress = new ReverseGeocodingAddress();
        wsResponseListener = (WsResponseListener) fragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (showProgress) {
            Utils.showProgressDialog(mContext, mContext.getString(R.string.str_fetching_address), true);
        }
    }

    @Override
    protected Object doInBackground(LatLng... params) {
        try {

            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            // double latitude = 39.279792;
            // double longitude = -76.617560;

            List<Address> addresses = null;
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                if (StringUtils.length(address.getAddressLine(0)) > 0) {
                    addressText = address.getAddressLine(0);
                }

                if (StringUtils.length(address.getLocality()) > 0) {
                    if (addressText.length() > 0) {
                        addressText += ", " + address.getLocality();
                    } else {
                        addressText = address.getLocality();
                    }

                }

                if (StringUtils.length(address.getAdminArea()) > 0) {
                    if (addressText.length() > 0) {
                        addressText += ", " + address.getAdminArea();
                    } else {
                        addressText = address.getAdminArea();
                    }
                }

                objAddress.setLatitude(Double.toString(latitude));
                objAddress.setLongitude(Double.toString(longitude));
                objAddress.setAddress(addressText);
                objAddress.setCity(address.getLocality());
                objAddress.setState(address.getAdminArea());
                objAddress.setCountry(address.getCountryName());

            }
            return objAddress;
        } catch (Exception e) {
            this.error = e;
//            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (showProgress) {
            Utils.hideProgressDialog();
        }
        wsResponseListener.onDeliveryResponse(serviceType, result, error);
    }
}
