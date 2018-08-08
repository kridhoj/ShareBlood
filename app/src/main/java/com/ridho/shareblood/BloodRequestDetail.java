package com.ridho.shareblood;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BloodRequestDetail extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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

                Sname = dataSnapshot.child("name").getValue().toString();
                LatLng position = new LatLng(lat,longt);
                marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.markers))
                        .title(Sname));
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
                    lat = Double.parseDouble(Slat);
                    longt = Double.parseDouble(Slong);

                    Sname = dataSnapshot.child("name").getValue().toString();

                    LatLng position = new LatLng(lat,longt);
                    marker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.markers))
                            .position(position)
                            .title(Sname));

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
    private TextView name,phone,pesan,golongan;
    public  String Sname,Sphone,Spesan,Sgolongan,key,Salamat,Slat,Slong,Simg,Sid,uid;
    private double lat,longt;
    private GeoDataClient mGeoDataClient;
    private Marker marker;
    private DatabaseReference mDatabase;
    private Button response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request_detail);

        fBack = (FloatingActionButton)findViewById(R.id.backBtn);
        fCall = (FloatingActionButton)findViewById(R.id.callBtn);
        fMessage = (FloatingActionButton)findViewById(R.id.MessagBtn);

        response = (Button)findViewById(R.id.response);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        name = (TextView)findViewById(R.id.username);
        phone = (TextView)findViewById(R.id.phone_number);
        pesan =(TextView)findViewById(R.id.messageTx);
        golongan = (TextView)findViewById(R.id.golonganTx);

        profileImg = (CircleImageView)findViewById(R.id.imgProfil);


        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BloodRequestDetail.this,MainActivity.class);
                startActivity(intent);
            }
        });
        getLocationPermission();

        Intent data = getIntent();
        key = data.getStringExtra("key");
        uid = data.getStringExtra("id");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid1 = firebaseUser.getUid();

        final String golongan1 = data.getStringExtra("gol1");

        if (uid.equals(uid1)){
            response.setVisibility(View.INVISIBLE);
        }else{
            response.setVisibility(View.VISIBLE);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("darahDonor").child(golongan1).child(key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sgolongan = dataSnapshot.child("golongan").getValue().toString();
                Spesan = dataSnapshot.child("pesan").getValue().toString();
                Sid = dataSnapshot.child("id").getValue().toString();
                Salamat = dataSnapshot.child("alamat").getValue().toString();
                final String id = dataSnapshot.child("id_post").getValue().toString();
                final DatabaseReference mB = FirebaseDatabase.getInstance().getReference("Users").child(Sid);
                mB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Simg = dataSnapshot.child("profile_image").getValue().toString();
                        Picasso.with(BloodRequestDetail.this).load(Simg).into(profileImg);
                        Sphone = dataSnapshot.child("phone_number").getValue().toString();
                        Sname = dataSnapshot.child("name").getValue().toString();

                        name.setText(Sname);
                        phone.setText(Sphone);
                        pesan.setText(Spesan);
                        golongan.setText(Sgolongan);

                        response.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(BloodRequestDetail.this, ResponseDetail.class);
                                intent.putExtra("id_post",key);
                                intent.putExtra("golongan",golongan1);
                                startActivityForResult(intent,1);

                                }
                        });

                        fCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String dial = "tel:" + Sphone;
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                            }
                        });
                        fMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Sphone));
                                startActivity(smsIntent);
                            }
                        });

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
                            Geocoder geocoder = new Geocoder(BloodRequestDetail.this);
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
                            Toast.makeText(BloodRequestDetail.this,"Cant Find Location",Toast.LENGTH_SHORT).show();
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

        mapFragment.getMapAsync(BloodRequestDetail.this);
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


}
