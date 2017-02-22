package com.rawalinfocom.rcontact.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.rawalinfocom.rcontact.ProfileRegistrationActivity;
import com.rawalinfocom.rcontact.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 21/02/17.
 */

public class GPSTracker extends Service implements LocationListener {

    final String TAG = "GPSTracker";

    private final Context mContext;
    Fragment fragment;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean isGPSTrackingEnabled = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    //    static public DialogVerticalActionArea dialogServices;
    static public MaterialDialog dialogGps;

    public GPSTracker(Context context, Fragment fragment) {
        this.mContext = context;
        this.fragment = fragment;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                this.isGPSTrackingEnabled = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.i("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager
                                .NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager
                                    .GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
//            else {
//                // no network provider is enabled
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */

    @SuppressLint("NewApi")
    @SuppressWarnings("unused")
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED && checkSelfPermission(Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TO DO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Update GPSTracker latitude and longitude
     */
    @SuppressWarnings("unused")
    public void updateGPSCoordinates() {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean getIsGPSTrackingEnabled() {
        return this.isGPSTrackingEnabled;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {

        RippleView.OnRippleCompleteListener rippleClickListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        dialogGps.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        dialogGps.dismissDialog();
                        ProfileRegistrationActivity.isFromSettings = true;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        break;
                }
            }
        };

        dialogGps = new MaterialDialog(mContext, rippleClickListener);

        dialogGps.setDialogTitle("GPS is disabled!");
        dialogGps.setDialogBody("GPS is not enabled. Do you want to go to settings menu?");

        dialogGps.setLeftButtonText("CANCEL");
        dialogGps.setRightButtonText("SETTINGS");
/*
        View.OnClickListener cancelClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Handler ripplehandler = new Handler();
                ripplehandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogGps.dismissDialog();
                        showLocationServicesDialog();
                    }
                }, AppConstant.RIPPLE_DURATION);
            }
        };*/

       /* View.OnClickListener settingsClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Handler ripplehandler = new Handler();
                ripplehandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoopleFragment.isFromSettings = true;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                }, AppConstant.RIPPLE_DURATION);
            }
        };*/

        dialogGps.showDialog();

    }

//    public void showSettingsAlert() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is disabled!");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//
//            }
//        });
//
//        // on pressing ic_cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // Showing Alert Message
//        alertDialog.show();
//    }

    /**
     * Get list of address by latitude and longitude
     *
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses that
                 * are known to describe the area immediately surrounding the
                 * given latitude and longitude.
                 */
                return geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);
            } catch (IOException e) {
                // e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     *
     * @return null or addressLine
     */
    @SuppressWarnings("unused")
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getAddressLine(0);
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     *
     * @return null or locality
     */
    @SuppressWarnings("unused")
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getLocality();
        } else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     *
     * @return null or postalCode
     */
    @SuppressWarnings("unused")
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getPostalCode();
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     *
     * @return null or postalCode
     */
    @SuppressWarnings("unused")
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getCountryName();
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.e("-------------- lat:", location.getLatitude() + " :");
//        Log.e("-------------- lang:", location.getLongitude() + " :");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//        Log.e("-------------- status", status + ":");
    }

    @Override
    public void onProviderEnabled(String provider) {
//        try {
//            LoopleFragment.enableFromSettings = true;
//            Location location = getLocation();
//            if (location != null) {
//                Log.e("status", "enabled");
//            } else {
//                Log.e("status", "disabled");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
