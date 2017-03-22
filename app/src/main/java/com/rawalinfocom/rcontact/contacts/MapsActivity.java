package com.rawalinfocom.rcontact.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncGeoCoding;
import com.rawalinfocom.rcontact.asynctasks.AsyncReverseGeoCoding;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.GPSTracker;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rawalinfocom.rcontact.ProfileRegistrationActivity.isFromSettings;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.relative_root_map)
    RelativeLayout relativeRootMap;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.input_search_location)
    EditText inputSearchLocation;
    @BindView(R.id.image_right_right)
    ImageView imageRightRight;
    @BindView(R.id.ripple_action_right_right)
    RippleView rippleActionRightRight;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.relative_last_address)
    RelativeLayout relativeLastAddress;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.text_address)
    TextView textAddress;
    @BindView(R.id.button_cancel)
    Button buttonCancel;
    @BindView(R.id.button_done)
    Button buttonDone;


    SupportMapFragment mapFragment;

    private GoogleMap googleMap;

    private double latitude = 0, longitude = 0;
    int locationCall = 0;
    GPSTracker gpsTracker;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        gpsTracker = new GPSTracker(this, null);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromSettings) {
            isFromSettings = false;
            if (Utils.isLocationEnabled(this)) {
                gpsTracker = new GPSTracker(this, null);
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

                addMapMarker();
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (latitude == 0 || longitude == 0) {
                    latitude = 21.1702;
                    longitude = 72.8311;
                }
                addMapMarker();
            }
        }, 1000);

    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_REVERSE_GEO_CODING_ADDRESS">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_REVERSE_GEO_CODING_ADDRESS)) {
                ReverseGeocodingAddress objAddress = (ReverseGeocodingAddress) data;
                if (objAddress == null) {
                    if (locationCall < 2) {
                        getLocationDetail();
                        locationCall++;
                    }
                } else {
                    try {
                       /* locationString = objAddress.getCity() + ", " + objAddress.getState() +
                       "," +
                                " " + objAddress.getCountry();*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_GEO_CODING_ADDRESS">
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_GEO_CODING_ADDRESS)) {

                Utils.hideSoftKeyboard(this, inputSearchLocation);
                relativeLastAddress.setVisibility(View.VISIBLE);

                ReverseGeocodingAddress objAddress = (ReverseGeocodingAddress) data;

                if (objAddress == null) {
                    Utils.showErrorSnackBar(this, relativeRootMap, "No Location Found");
                } else {
                    latitude = Double.parseDouble(objAddress.getLatitude());
                    longitude = Double.parseDouble(objAddress.getLongitude());
                    addMapMarker();
                    textAddress.setText(objAddress.getAddress());
//                    Log.i("onDeliveryResponse", objAddress.getAddress());
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootMap, "" + error.getLocalizedMessage());
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_right:
                if (StringUtils.length(StringUtils.trim(inputSearchLocation.getText().toString())
                ) > 0) {
                    AsyncGeoCoding asyncGeoCoding = new AsyncGeoCoding(this, true, WsConstants
                            .REQ_GEO_CODING_ADDRESS);
                    asyncGeoCoding.execute(StringUtils.trim(inputSearchLocation.getText()
                            .toString()));
                } else {
                    Utils.showErrorSnackBar(this, relativeRootMap, "Please add Address to search");
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission Granted

                    if (Utils.isLocationEnabled(this)) {
                        gpsTracker = new GPSTracker(this, null);
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();

                        addMapMarker();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }


                } else {

                    // Permission Denied


                }
            }
            break;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightRight.setOnRippleCompleteListener(this);

        textLabelAddress.setTypeface(Utils.typefaceSemiBold(this));
        textAddress.setTypeface(Utils.typefaceRegular(this));
        buttonCancel.setTypeface(Utils.typefaceRegular(this));
        buttonDone.setTypeface(Utils.typefaceRegular(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION}, AppConstants
                    .MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        } else {
            if (Utils.isLocationEnabled(this)) {
               /* latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                getCityName();*/
                getLocationDetail();
            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    private void addMapMarker() {
        googleMap.clear();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 25.0f));
            }
        }, 1000);
    }

    private void getLocationDetail() {
        gpsTracker = new GPSTracker(this, null);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        AsyncReverseGeoCoding asyncReverseGeoCoding = new AsyncReverseGeoCoding(this, WsConstants
                .REQ_REVERSE_GEO_CODING_ADDRESS, false);
        asyncReverseGeoCoding.execute(new LatLng(latitude, longitude));

    }

    //</editor-fold>

}
