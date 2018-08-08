package com.ridho.shareblood;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventDetail extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Slat = dataSnapshot.child("lat").getValue().toString();
                Slong = dataSnapshot.child("long").getValue().toString();
                lat = Double.parseDouble(Slat);
                longt = Double.parseDouble(Slong);

                String Sname = dataSnapshot.child("nama").getValue().toString();
                String alamat = dataSnapshot.child("alamat").getValue().toString();
                LatLng position = new LatLng(lat,longt);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markers))
                        .title(Sname)
                        .snippet(alamat));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (mLocationPermissionsGranted) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   Slat = dataSnapshot.child("lat").getValue().toString();
                    Slong = dataSnapshot.child("long").getValue().toString();
                    String alamat = dataSnapshot.child("alamat").getValue().toString();
                    lat = Double.parseDouble(Slat);
                    longt = Double.parseDouble(Slong);

                   String Sname = dataSnapshot.child("nama").getValue().toString();

                    LatLng position = new LatLng(lat,longt);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(Sname)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markers))
                            .snippet(alamat));

                    moveCamera(new LatLng(lat,longt),DEFAULT_ZOOM,Sname);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
    }


    private static final String TAG = "MapActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 17f;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private FloatingActionButton fBack,fCall,fMessage,fDirect;
    private CircleImageView profileImg;
    private TextView event,pj,phone,deskripsi,tanggal,lokasi,waktu;
    private String Slat,Slong,event1,lokasi1,phone1,deskripsi1,tanggal1,mulai1,selesai1;
    private double lat,longt;
    private GeoDataClient mGeoDataClient;
    private Marker marker;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);


        fBack = (FloatingActionButton)findViewById(R.id.backBtn);
        fCall = (FloatingActionButton)findViewById(R.id.callBtn);
        fMessage = (FloatingActionButton)findViewById(R.id.MessagBtn);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        event = (TextView)findViewById(R.id.nameEvent);
        phone = (TextView)findViewById(R.id.phone_number);
        pj = (TextView)findViewById(R.id.pj);
        tanggal = (TextView)findViewById(R.id.tanggalT);
        waktu = (TextView)findViewById(R.id.time);
        deskripsi = (TextView)findViewById(R.id.messageTx);
        lokasi = (TextView)findViewById(R.id.lokasi);



        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetail.this,MainActivity.class);
                startActivity(intent);
            }
        });
        getLocationPermission();

        Intent data = getIntent();
        final String key = data.getStringExtra("key");

        mDatabase = FirebaseDatabase.getInstance().getReference("eventDonor").child(key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event1 = dataSnapshot.child("nama").getValue().toString();
               lokasi1 = dataSnapshot.child("lokasi").getValue().toString();
                deskripsi1 = dataSnapshot.child("deskripsi").getValue().toString();
                 tanggal1 = dataSnapshot.child("tanggal").getValue().toString();
                 mulai1 = dataSnapshot.child("mulai").getValue().toString();
              selesai1 = dataSnapshot.child("selesai").getValue().toString();
              String id = dataSnapshot.child("id").getValue().toString();
                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference("Users").child(id);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String pj2 = dataSnapshot.child("name").getValue().toString();
                        String phone1 = dataSnapshot.child("phone_number").getValue().toString();
                        event.setText(event1);
                        pj.setText(pj2);
                        phone.setText(phone1);
                        lokasi.setText(lokasi1);
                        deskripsi.setText(deskripsi1);
                        tanggal.setText(tanggal1);
                        waktu.setText(mulai1 + "s/d" + selesai1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if (mLocationPermissionsGranted){
                final Task location =mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Location currentLocation =(Location)task.getResult();
                            Geocoder geocoder = new Geocoder(EventDetail.this);
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
                            Toast.makeText(EventDetail.this,"Cant Find Location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }catch (SecurityException e){

        }
    }

    private void moveCamera (LatLng latLng,float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.markers));
    }
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(EventDetail.this);
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
