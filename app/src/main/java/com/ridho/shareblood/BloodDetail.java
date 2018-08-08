package com.ridho.shareblood;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class BloodDetail extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private android.support.v7.widget.Toolbar mToolbar;
    private Button goldarBtn,alamatBtn,saveBtn,editBtn,deleteBtn,listBtn;
    private TextView idPost,tanggalPost;
    private MaterialEditText goldar,alamat,rumahsakit,deskripsi,latlng,longlng;
    public  String golongan_darah,id,key;
    static final int PICK_CONTACT_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_detail);
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post Details");

        goldarBtn = (Button)findViewById(R.id.goldar_btn);
        alamatBtn = (Button)findViewById(R.id.alamat_btn);
        saveBtn = (Button)findViewById(R.id.save_btn);
        editBtn = (Button)findViewById(R.id.edit_btn);
        deleteBtn = (Button)findViewById(R.id.delete_btn);
        listBtn = (Button)findViewById(R.id.pendonor_btn);

        idPost = (TextView)findViewById(R.id.id_post);
        tanggalPost = (TextView)findViewById(R.id.tanggalPost);

        latlng =(MaterialEditText) findViewById(R.id.latng);
        longlng =(MaterialEditText) findViewById(R.id.longtng);

        goldar = (MaterialEditText)findViewById(R.id.goldar);
        alamat = (MaterialEditText)findViewById(R.id.alamat);
        rumahsakit = (MaterialEditText)findViewById(R.id.rumahsakit);
        deskripsi = (MaterialEditText)findViewById(R.id.deskripsi);
        initGoogleAPIClient();
        checkPermissions();
        final Intent intent = getIntent();
        final String golongan = intent.getStringExtra("golongan");
        key = intent.getStringExtra("key");



        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(BloodDetail.this,PendonorList.class);
                intent1.putExtra("id_post",key);
                startActivityForResult(intent1,1);
            }
        });

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("darahDonor").child(golongan).child(key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gol = dataSnapshot.child("golongan").getValue().toString();
                final String add = dataSnapshot.child("alamat").getValue().toString();
                final String hospital = dataSnapshot.child("hospital").getValue().toString();
                final String deskrip = dataSnapshot.child("pesan").getValue().toString();
                final String tanggal = dataSnapshot.child("tanggal").getValue().toString();
               final String lat = dataSnapshot.child("lat").getValue().toString();
               final String longt = dataSnapshot.child("long").getValue().toString();
                final String name = dataSnapshot.child("name").getValue().toString();
                final String notif = dataSnapshot.child("notif").getValue().toString();
              id = dataSnapshot.child("id").getValue().toString();
                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("userPost").child(id).child("bloodPost").child(key);
                mBase.child("notif").setValue("no");
                idPost.setText(key);
                tanggalPost.setText(tanggal);

                latlng.setText(lat);
                longlng.setText(longt);
                goldar.setText(golongan);
                alamat.setText(add);
                rumahsakit.setText(hospital);
                deskripsi.setText(deskrip);



                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mDialog = new AlertDialog.Builder(BloodDetail.this);
                        mDialog.setTitle("Hapus");
                        mDialog.setMessage("Apakah Anda Ingin Menghapus");
                        mDialog.setNegativeButton("Cancel",null);
                        mDialog.setPositiveButton("Ya, Hapus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delReact(key,id,gol);
                            }
                        });
                        AlertDialog dialog = mDialog.create();
                        dialog.show();
                    }
                });

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goldar.setEnabled(true);
                        rumahsakit.setEnabled(true);
                        deskripsi.setEnabled(true);

                        editBtn.setVisibility(View.INVISIBLE);
                        saveBtn.setVisibility(View.VISIBLE);

                        alamatBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initGoogleAPIClient();//Init Google API Client
                                checkPermissions();//Check Permission
                                Intent intent = new Intent(BloodDetail.this, MapsInput.class);
                                startActivityForResult(intent,PICK_CONTACT_REQUEST);
                            }
                        });


                        alamatBtn.setVisibility(View.VISIBLE);
                        goldarBtn.setVisibility(View.VISIBLE);
                        goldarBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder mDialog = new AlertDialog.Builder(BloodDetail.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_type,null);
                                Button aType = (Button)mView.findViewById(R.id.Atype);
                                Button bType = (Button)mView.findViewById(R.id.Btype);
                                Button abType = (Button)mView.findViewById(R.id.ABtype);
                                Button oType = (Button)mView.findViewById(R.id.Otype);


                                mDialog.setView(mView);
                                final AlertDialog dialog = mDialog.create();

                                aType.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        golongan_darah = "A";
                                        dialog.dismiss();
                                        goldar.setText(golongan_darah);
                                    }
                                });
                                bType.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        golongan_darah = "B";
                                        dialog.dismiss();
                                        goldar.setText(golongan_darah);
                                    }
                                });
                                abType.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        golongan_darah = "AB";
                                        dialog.dismiss();
                                        goldar.setText(golongan_darah);
                                    }
                                });
                                oType.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        golongan_darah = "O";
                                        dialog.dismiss();
                                        goldar.setText(golongan_darah);
                                    }
                                });
                                dialog.show();
                            }
                        });
                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDatabase.removeValue();
                               DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("userPost").child(id).child("bloodPost").child(key);
                                mDb.removeValue();
                                String hospital1 = rumahsakit.getText().toString();
                                String add1 = alamat.getText().toString();
                                String goldar1 = goldar.getText().toString();
                                String pesan = deskripsi.getText().toString();
                                String long3 = longlng.getText().toString();
                                String lat3 = latlng.getText().toString();
                             DatabaseReference mBase = FirebaseDatabase.getInstance().getReference("darahDonor").child(goldar1).child(key);
                                final HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("alamat", add1);
                                userMap.put("hospital", hospital1);
                                userMap.put("lat", lat3);
                                userMap.put("long", long3);
                                userMap.put("tanggal",tanggal);
                                userMap.put("pesan", pesan);
                                userMap.put("id", id);
                                userMap.put("id_post",key);
                                userMap.put("name",name);
                                userMap.put("golongan",goldar1);
                                userMap.put("notif",notif);
                                mBase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                                   goldar.setEnabled(false);
                                                   rumahsakit.setEnabled(false);
                                                   deskripsi.setEnabled(false);

                                                   editBtn.setVisibility(View.VISIBLE);
                                                   saveBtn.setVisibility(View.INVISIBLE);
                                                   Intent intent1 = new Intent(BloodDetail.this,MainActivity.class);
                                                   startActivity(intent1);
                                            Toast.makeText(BloodDetail.this, "Berhasil Mengubah Data", Toast.LENGTH_SHORT).show();
                                              }else {
                                            Toast.makeText(BloodDetail.this,"Gagal Menyunting",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userPost").child(id).child("bloodPost").child(key);
                                ref.setValue(userMap);
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void delReact (String id_post,String id,String goldar){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("userPost").child(id).child("bloodPost").child(id_post);
        mDatabase.removeValue();
        DatabaseReference mReact = FirebaseDatabase.getInstance().getReference("darahDonor").child(goldar).child(id_post);
        mReact.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(BloodDetail.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(BloodDetail.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(BloodDetail.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(BloodDetail.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(BloodDetail.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(BloodDetail.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(BloodDetail.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");

                        break;
                }
                break;
        }
        if (requestCode==PICK_CONTACT_REQUEST&&resultCode==RESULT_OK&&data!=null){
            final String alamat2 = data.getStringExtra("address");
            double lat2 = data.getDoubleExtra("lat",1);
            double longt2 = data.getDoubleExtra("long",1);
            String lat4 = Double.toString(lat2);
            String longt4 = Double.toString(longt2);
            longlng.setText(longt4);
            latlng.setText(lat4);
            alamat.setText(alamat2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };
    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
                    Toast.makeText(BloodDetail.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
