package com.rawalinfocom.rcontact.contacts;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.PlaceSuggestionListAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncGeoCoding;
import com.rawalinfocom.rcontact.asynctasks.AsyncGetGoogleLocation;
import com.rawalinfocom.rcontact.asynctasks.AsyncReverseGeoCoding;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.GPSTracker;
import com.rawalinfocom.rcontact.helper.RecyclerItemClickListener;
import com.rawalinfocom.rcontact.helper.RecyclerItemDecoration;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.GetGoogleLocationResponse;
import com.rawalinfocom.rcontact.model.GetGoogleLocationResultObject;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rawalinfocom.rcontact.ProfileRegistrationActivity.isFromSettings;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback, RippleView
        .OnRippleCompleteListener, WsResponseListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.relative_root_map)
    RelativeLayout relativeRootMap;
    @BindView(R.id.input_search_location)
    EditText inputSearchLocation;
    @BindView(R.id.image_right_right)
    ImageView imageRightRight;
    @BindView(R.id.ripple_action_right_right)
    RippleView rippleActionRightRight;
    @BindView(R.id.linear_action_right)
    LinearLayout linearActionRight;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.button_fetch_address)
    Button buttonFetchAddress;
    @BindView(R.id.relative_fetch_address)
    RelativeLayout relativeFetchAddress;
    @BindView(R.id.ripple_fetch_address)
    RippleView rippleFetchAddress;
    @BindView(R.id.recycler_view_suggestions)
    RecyclerView recyclerViewSuggestions;

    SupportMapFragment mapFragment;

    private GoogleMap googleMap;

    private double latitude = 0, longitude = 0;
    int locationCall = 0;
    //    GPSTracker gpsTracker;
    ReverseGeocodingAddress objAddress;

    private String defaultFormattedAddress = "Surat, Gujarat, India";

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

//        gpsTracker = new GPSTracker(this, null);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(AppConstants.EXTRA_FORMATTED_ADDRESS)) {
                defaultFormattedAddress = intent.getStringExtra(AppConstants
                        .EXTRA_FORMATTED_ADDRESS);
            }
            if (intent.hasExtra(AppConstants.EXTRA_LATITUDE)) {
                try {
                    latitude = intent.getDoubleExtra(AppConstants.EXTRA_LATITUDE, 0);
                } catch (Exception ignore) {
                }
            }
            if (intent.hasExtra(AppConstants.EXTRA_LONGITUDE)) {
                try {
                    longitude = intent.getDoubleExtra(AppConstants.EXTRA_LONGITUDE, 0);
                } catch (Exception ignore) {
                }

            }
        }


        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFromSettings) {
            isFromSettings = false;
            if (Utils.isLocationEnabled(this)) {
               /* gpsTracker = new GPSTracker(this, null);
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

                addMapMarker();*/
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
/*        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);*/
  /*      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION}, AppConstants
                    .MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {*/
        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.setMyLocationEnabled(true);
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
//        }

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
            }
        });
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
                }
                /*else {
                    try {
                        locationString = objAddress.getCity() + ", " + objAddress.getState() +
                       "," +
                                " " + objAddress.getCountry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
            //</editor-fold>

            //<editor-fold desc="REQ_GEO_CODING_ADDRESS">
            else if (serviceType.contains(WsConstants.REQ_GEO_CODING_ADDRESS)) {

                Utils.hideSoftKeyboard(this, inputSearchLocation);
//                relativeLastAddress.setVisibility(View.VISIBLE);

                objAddress = (ReverseGeocodingAddress) data;

                if (objAddress == null) {
                    Utils.showErrorSnackBar(this, relativeRootMap, "No Location Found");
                } else {
                    try {
                        latitude = Double.parseDouble(StringUtils.defaultString(objAddress
                                .getLatitude(), "0"));
                        longitude = Double.parseDouble(StringUtils.defaultString(objAddress
                                .getLongitude(), "0"));
                        if (latitude != 0 && longitude != 0) {
                            if (serviceType.contains("_TRUE")) {
                                addMapMarker();
                            } else {
//                                showAddressDialog(objAddress.getAddress());
                              /*  Toast.makeText(this, objAddress.getAddress(), Toast.LENGTH_SHORT)
                                        .show();*/
                                Intent intent = new Intent();
                                intent.putExtra(AppConstants.EXTRA_OBJECT_LOCATION, objAddress
                                        .getAddressLine());
                                intent.putExtra(AppConstants.EXTRA_OBJECT_ADDRESS, objAddress);
                                setResult(AppConstants.RESULT_CODE_MAP_LOCATION_SELECTION, intent);
                                finish();
                                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                            }
//                            textAddress.setText(objAddress.getAddress());
//                            showAddressDialog(objAddress.getAddress());
                        } else {
                            Utils.showErrorSnackBar(this, relativeRootMap, "Unable to find " +
                                    "Location");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.showErrorSnackBar(this, relativeRootMap, "Unable to find Location! " +
                                "Please try again.");

                    }
//                    Log.i("onDeliveryResponse", objAddress.getAddress());
                }
            }
            //</editor-fold>

            //<editor-fold desc="REQ_GOOGLE_TEXT_BY_LOCATIONS">
            else if (serviceType.equalsIgnoreCase(WsConstants.REQ_GOOGLE_TEXT_BY_LOCATIONS)) {
                GetGoogleLocationResponse getGoogleLocationResponse = (GetGoogleLocationResponse)
                        data;
                if (getGoogleLocationResponse.getStatus().equalsIgnoreCase("OK")) {
                    ArrayList<GetGoogleLocationResultObject> arylstLocationResults = new
                            ArrayList<>();

                    arylstLocationResults.addAll(getGoogleLocationResponse.getPredictions());

                    PlaceSuggestionListAdapter adapter = new PlaceSuggestionListAdapter(this,
                            arylstLocationResults);
                    recyclerViewSuggestions.setAdapter(adapter);

                } else if (getGoogleLocationResponse.getStatus().equalsIgnoreCase
                        ("OVER_QUERY_LIMIT")) {
                    Utils.showErrorSnackBar(this, relativeRootMap, "You have exceeded your daily " +
                            "request quota for this API.");
                } else {
                    Log.e(serviceType + "response", "fail");
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

            case R.id.ripple_fetch_address:
                if (latitude != 0 && longitude != 0) {
                    AsyncGeoCoding asyncGeoCodingFetch = new AsyncGeoCoding(this, true, WsConstants
                            .REQ_GEO_CODING_ADDRESS + "_FALSE");
                    asyncGeoCodingFetch.execute(null, String.valueOf(latitude), String.valueOf
                            (longitude));
                } else {
                    Utils.showErrorSnackBar(this, relativeRootMap, "Please search any location!");
                }
                break;

            /*case R.id.ripple_action_right_left:
                if (StringUtils.length(StringUtils.trim(inputSearchLocation.getText().toString())
                ) > 0) {
                    AsyncGeoCoding asyncGeoCoding = new AsyncGeoCoding(this, true, WsConstants
                            .REQ_GEO_CODING_ADDRESS + "_TRUE");
                    asyncGeoCoding.execute(StringUtils.trim(inputSearchLocation.getText()
                            .toString()));
                } else {
                    Utils.showErrorSnackBar(this, relativeRootMap, "Please add Address to search");
                }
                break;*/

            case R.id.ripple_action_right_right:
               /* if (StringUtils.length(StringUtils.trim(inputSearchLocation.getText().toString())
                ) > 0) {
                    AsyncGeoCoding asyncGeoCoding = new AsyncGeoCoding(this, true, WsConstants
                            .REQ_GEO_CODING_ADDRESS + "_TRUE");
                    asyncGeoCoding.execute(StringUtils.trim(inputSearchLocation.getText()
                            .toString()));
                } else {
                    Utils.showErrorSnackBar(this, relativeRootMap, "Please add Address to search");
                }*/
                inputSearchLocation.setText("");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("onActivityResult", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("onActivityResult", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /* @Override
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
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.setMyLocationEnabled(true);
                        gpsTracker = new GPSTracker(this, null);
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();

                        addMapMarker();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }


                } else {

                    // Permission Denied
                    latitude = 21.1702;
                    longitude = 72.8311;
                    addMapMarker();


                }
            }
            break;
        }
    }*/

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        recyclerViewSuggestions.setVisibility(View.GONE);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this));

        RecyclerItemDecoration decoration = new RecyclerItemDecoration(this, ContextCompat.getColor
                (this, R.color.darkGray), 0.5f);
        recyclerViewSuggestions.addItemDecoration(decoration);

        recyclerViewSuggestions.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener
                        .OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView textview = (TextView) view.findViewById(R.id.text_suggestion);
                        inputSearchLocation.setText(textview.getText().toString());
                        recyclerViewSuggestions.setVisibility(View.GONE);
                        if (StringUtils.length(StringUtils.trim(inputSearchLocation.getText()
                                .toString())
                        ) > 0) {
                            AsyncGeoCoding asyncGeoCoding = new AsyncGeoCoding(MapsActivity.this,
                                    true, WsConstants.REQ_GEO_CODING_ADDRESS + "_TRUE");
                            asyncGeoCoding.execute(StringUtils.trim(inputSearchLocation.getText()
                                    .toString()));
                        } else {
                            Utils.showErrorSnackBar(MapsActivity.this, relativeRootMap, "Please " +
                                    "add Address to search");
                        }
                    }
                })
        );

        rippleActionRightRight.setOnRippleCompleteListener(this);
        rippleFetchAddress.setOnRippleCompleteListener(this);

        if (latitude == 0 || longitude == 0) {
        /*    if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION}, AppConstants
                        .MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            } else {*/
            if (StringUtils.length(defaultFormattedAddress) > 0 && !(StringUtils.equals
                    (defaultFormattedAddress, "Surat, Gujarat, India"))) {
                AsyncGeoCoding asyncGeoCoding = new AsyncGeoCoding(this, true, WsConstants
                        .REQ_GEO_CODING_ADDRESS + "_TRUE");
                asyncGeoCoding.execute(StringUtils.trim(defaultFormattedAddress));
            }
            /*else {
                if (Utils.isLocationEnabled(this)) {
                    getLocationDetail();
                } else {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission
                            .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        gpsTracker.showSettingsAlert();
                    }
                }
            }*/

//            }
        } else {
            AsyncReverseGeoCoding asyncReverseGeoCoding = new AsyncReverseGeoCoding(this,
                    WsConstants.REQ_REVERSE_GEO_CODING_ADDRESS, false);
            asyncReverseGeoCoding.execute(new LatLng(latitude, longitude));
        }

        /*buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLastAddress.setVisibility(View.GONE);
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(AppConstants.EXTRA_OBJECT_LOCATION, textAddress.getText()
                        .toString());
                intent.putExtra(AppConstants.EXTRA_OBJECT_ADDRESS, objAddress);
                setResult(AppConstants.REQUEST_CODE_MAP_LOCATION_SELECTION, intent);
                finish();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });*/

        inputSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete
                            .MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        /*inputSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    recyclerViewSuggestions.setVisibility(View.VISIBLE);
//                    searchLocation(false, charSequence.toString());
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete
                                .MODE_OVERLAY).build(MapsActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                } else {
                    recyclerViewSuggestions.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

    }

    public void addMapMarker() {
        googleMap.clear();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
            }
        }, 1000);
    }

    private void getLocationDetail() {
        GPSTracker gpsTracker = new GPSTracker(this, null);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        AsyncReverseGeoCoding asyncReverseGeoCoding = new AsyncReverseGeoCoding(this, WsConstants
                .REQ_REVERSE_GEO_CODING_ADDRESS, false);
        asyncReverseGeoCoding.execute(new LatLng(latitude, longitude));

    }

    private void showAddressDialog(String googleAddress) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map_address);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textLabelMyAddress = (TextView) dialog.findViewById(R.id.text_label_my_address);
        final TextView textMyAddress = (TextView) dialog.findViewById(R.id.text_my_address);
        TextView textLabelGoogleAddress = (TextView) dialog.findViewById(R.id
                .text_label_google_address);
        final TextView textGoogleAddress = (TextView) dialog.findViewById(R.id.text_google_address);
        Button buttonMyAddress = (Button) dialog.findViewById(R.id.button_my_address);
        Button buttonGoogleAddress = (Button) dialog.findViewById(R.id.button_google_address);

        textMyAddress.setText(defaultFormattedAddress);
        textGoogleAddress.setText(googleAddress);

        textLabelMyAddress.setTypeface(Utils.typefaceSemiBold(this));
        textLabelGoogleAddress.setTypeface(Utils.typefaceSemiBold(this));
        textMyAddress.setTypeface(Utils.typefaceRegular(this));
        textGoogleAddress.setTypeface(Utils.typefaceRegular(this));
        buttonMyAddress.setTypeface(Utils.typefaceRegular(this));
        buttonGoogleAddress.setTypeface(Utils.typefaceRegular(this));

        buttonGoogleAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(AppConstants.EXTRA_OBJECT_LOCATION, textGoogleAddress.getText()
                        .toString());
                intent.putExtra(AppConstants.EXTRA_OBJECT_ADDRESS, objAddress);
                setResult(AppConstants.RESULT_CODE_MAP_LOCATION_SELECTION, intent);
                finish();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });

        buttonMyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(AppConstants.EXTRA_OBJECT_LOCATION, textGoogleAddress.getText()
                        .toString());
                intent.putExtra(AppConstants.EXTRA_OBJECT_ADDRESS, objAddress);
                setResult(AppConstants.RESULT_CODE_MY_LOCATION_SELECTION, intent);
                finish();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            }
        });

        dialog.show();
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void searchLocation(Boolean displayProgress, String queryString) {
        if (Utils.isNetworkAvailable(this)) {
            AsyncGetGoogleLocation asyncGetGoogleLocation = new AsyncGetGoogleLocation
                    (this, displayProgress, WsConstants.REQ_GOOGLE_TEXT_BY_LOCATIONS);
            asyncGetGoogleLocation.execute("https://maps.googleapis" +
                    ".com/maps/api/place/autocomplete/json?key" +
                    "=AIzaSyDHLCyy3FXO9IshxYd2-XAR6uSmVPvnAZQ&input=" + queryString +
                    "&types=geocode&sensor=true");
        } else {
            Utils.showErrorSnackBar(this, relativeRootMap, getResources().getString(R.string
                    .msg_no_network));
        }
    }

    //</editor-fold>

}
