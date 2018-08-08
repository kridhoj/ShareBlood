package com.ridho.shareblood;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ridho.shareblood.models.PlaceInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsEventBlood extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener{
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Maps Is Ready", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (mMarker != null){
                        mMarker.remove();
                    }
                    Geocoder geocoder =
                            new Geocoder(MapsEventBlood.this);
                    List<Address> list;
                    try {
                        list = geocoder.getFromLocation(latLng.latitude,
                                latLng.longitude, 1);
                    } catch (IOException e) {
                        return;
                    }
                    Address address = list.get(0);

                    LatLng mLat = mMarker.getPosition();
                    MarkerOptions options = new MarkerOptions()
                            .title(address.getAddressLine(0))
                            .position(new LatLng(latLng.latitude,
                                    latLng.longitude))
                            .snippet("Longtitude :"+ mLat.longitude + "Latitude : "+ mLat.latitude);

                    mMarker = mMap.addMarker(options);
                }
            });

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);

            init();
        }
    }
    private static final String TAG = "MapActivity";
    private FirebaseAuth mAuth;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Button mPilih;
    private PlaceInfo mPlaces;
    private Marker mMarker;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mSearchText;
    private ImageView mGps,mSearch;
    private String golongan;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_input_blood);
        mSearchText = (AutoCompleteTextView)findViewById(R.id.input_search);
        mGps = (ImageView)findViewById(R.id.ic_gps);
        mSearch = (ImageView)findViewById(R.id.ic_magnify);
        mPilih = (Button)findViewById(R.id.confirm_btn);
        mAuth = FirebaseAuth.getInstance();
        getLocationPermission();

    }
    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if (mLocationPermissionsGranted){
                final Task location =mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && task.getResult() != null ){
                            Location currentLocation =(Location)task.getResult();
                            Geocoder geocoder = new Geocoder(MapsEventBlood.this);
                            List<Address> list = new ArrayList<>();

                            try{
                                list = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                            }catch (IOException e){

                            }

                            if (list.size() > 0){
                                Address address = list.get(0);

                                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

                            }
                        }else{
                            Toast.makeText(MapsEventBlood.this,"Cant Find Location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){

        }
    }

    private void init(){



        mGeoDataClient = Places.getGeoDataClient(this, null);

        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, BOUNDS_GREATER_SYDNEY, null);
        mSearchText.setAdapter(mAdapter);
        mSearchText.setOnItemClickListener(mAutocomplate);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction()==KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    geoLocate();
                }
                return false;
            }
        });

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate();
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMarker != null){
                    mMarker.remove();
                }
                getDeviceLocation();

            }
        });
        mPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng latP = mMarker.getPosition();
                double latitudeP = latP.latitude;
                double longtitudeP = latP.longitude;
                Geocoder geocoder =
                        new Geocoder(MapsEventBlood.this);
                List<Address> list;
                try {
                    list = geocoder.getFromLocation(latP.latitude,
                            latP.longitude, 1);
                } catch (IOException e) {
                    return;
                }
                Address address = list.get(0);
                String addressP = address.getAddressLine(0) ;
                Toast.makeText(MapsEventBlood.this,addressP,Toast.LENGTH_LONG).show();

                Intent theIntent = new Intent();
                theIntent.putExtra("address", addressP);
                theIntent.putExtra("lat" , latitudeP);
                theIntent.putExtra("long",longtitudeP);
                setResult(RESULT_OK,theIntent);
                finish();
            }
        });
        hideSoftKeyboard();
    }

    private void geoLocate(){
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsEventBlood.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){

        }

        if (list.size() > 0){
            Address address = list.get(0);

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));

        }
    }
    private void moveCamera (LatLng latLng,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).draggable(true);
        mMarker = mMap.addMarker(markerOptions);

        hideSoftKeyboard();
    }
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsEventBlood.this);
    }

    private void getLocationPermission(){
        String[] permission = {FINE_LOCATION,COURSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permission,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permission,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i=0; i < grantResults.length;i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            sendToStart();
        }
    }
    private void sendToStart() {
        Intent startintent = new Intent(MapsEventBlood.this,SplashScreen.class);
        startActivity(startintent);

        finish();
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
        ......... Auto Complate Search
    */

    private AdapterView.OnItemClickListener mAutocomplate = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);
            hideSoftKeyboard();
        }
    };
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            PlaceBufferResponse places = task.getResult();

            if (places.isClosed()){
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{

                mPlaces = new PlaceInfo();

                String pAddress = mPlaces.setAddress(place.getAddress().toString());
                mPlaces.setId(place.getId());
                mPlaces.setLatLng(place.getLatLng());
                LatLng platLng = mPlaces.setLatLng(place.getLatLng());

            }catch (NullPointerException e){

            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude),DEFAULT_ZOOM,mPlaces.getAddress());
            places.release();

        }
    };

}
