package com.ridho.shareblood;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EdgeEffect;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EventEdit extends AppCompatActivity {
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private DatePickerDialog datePickerDialog;
     SimpleDateFormat dateFormater;
    private android.support.v7.widget.Toolbar mToolbar;
    private Button alamatBtn,saveBtn,editBtn,deleteBtn,btnDatePicker;
    private TextView idPost;
    private MaterialEditText event,alamat,lokasi,deskripsi,latlng,longlng,tanggal,mulai,selesai,pj;
    public  String id,key;
    private DatabaseReference mDatabase;
    static final int PICK_CONTACT_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Event Details");
        dateFormater = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        alamatBtn = (Button)findViewById(R.id.alamat_btn);
        saveBtn = (Button)findViewById(R.id.save_btn);
        editBtn = (Button)findViewById(R.id.edit_btn);
        deleteBtn = (Button)findViewById(R.id.delete_btn);
        btnDatePicker = (Button)findViewById(R.id.date_input);

        idPost = (TextView)findViewById(R.id.id_post);
        pj = (MaterialEditText)findViewById(R.id.pj);
        latlng =(MaterialEditText) findViewById(R.id.latng);
        longlng =(MaterialEditText) findViewById(R.id.longtng);

        event = (MaterialEditText)findViewById(R.id.event);
        tanggal = (MaterialEditText)findViewById(R.id.tanggal);
        mulai = (MaterialEditText)findViewById(R.id.mulai);
        selesai = (MaterialEditText)findViewById(R.id.selesai);
        alamat = (MaterialEditText)findViewById(R.id.alamat);
        lokasi = (MaterialEditText)findViewById(R.id.lokasi);
        deskripsi = (MaterialEditText)findViewById(R.id.deskripsi);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        mDatabase = FirebaseDatabase.getInstance().getReference("eventDonor").child(key);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String alamat1 = dataSnapshot.child("alamat").getValue().toString();
                final String deskripsi1 = dataSnapshot.child("deskripsi").getValue().toString();
                id = dataSnapshot.child("id").getValue().toString();
                final String id_post = dataSnapshot.child("id_post").getValue().toString();
                final String lat1 = dataSnapshot.child("lat").getValue().toString();
                final String lokasi1 = dataSnapshot.child("lokasi").getValue().toString();
                final String long1 = dataSnapshot.child("long").getValue().toString();
                final String mulai1 = dataSnapshot.child("mulai").getValue().toString();
                final String nama = dataSnapshot.child("nama").getValue().toString();
                final String phone1 = dataSnapshot.child("phone").getValue().toString();
                final String pj1 = dataSnapshot.child("pj").getValue().toString();
                final String selesai1 = dataSnapshot.child("selesai").getValue().toString();
                final String tanggal1 = dataSnapshot.child("tanggal").getValue().toString();

                idPost.setText(id_post);
                event.setText(nama);
                pj.setText(pj1);
                tanggal.setText(tanggal1);
                lokasi.setText(lokasi1);
                alamat.setText(alamat1);
                deskripsi.setText(deskripsi1);
                mulai.setText(mulai1);
                selesai.setText(selesai1);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference().child("eventDonor").child(key);
                        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("userPost").child(id).child("eventPost").child(key);
                        mBase.removeValue();
                        mData.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent(EventEdit.this,MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(EventEdit.this,"Data Berhasil Di Hapus",Toast.LENGTH_LONG).show();
                                }   else {
                                    Toast.makeText(EventEdit.this,"Data Gagal Di Hapus",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        event.setEnabled(true);
                        pj.setEnabled(true);
                        lokasi.setEnabled(true);
                        alamat.setEnabled(true);
                        deskripsi.setEnabled(true);
                        mulai.setEnabled(true);
                        selesai.setEnabled(true);

                        btnDatePicker.setVisibility(View.VISIBLE);
                        alamatBtn.setVisibility(View.VISIBLE);
                        editBtn.setVisibility(View.INVISIBLE);
                        saveBtn.setVisibility(View.VISIBLE);

                        btnDatePicker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDateDialog();
                            }
                        });



                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mDatabase.removeValue();
                                String nama = event.getText().toString();
                                String pj2 = pj.getText().toString();
                                String add = alamat.getText().toString();
                                String deskripsi1 = deskripsi.getText().toString();
                                String mulai2 = mulai.getText().toString();
                                String lokasi2 = lokasi.getText().toString();
                                String selesai2 = selesai.getText().toString();
                                String tanggal2 = tanggal.getText().toString();

                                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("eventDonor").child(key);
                                HashMap<String,String> userMap = new HashMap<>();
                                userMap.put("alamat", add);
                                userMap.put("lokasi", lokasi2);
                                userMap.put("lat", lat1);
                                userMap.put("phone",phone1);
                                userMap.put("long", long1);
                                userMap.put("deskripsi", deskripsi1);
                                userMap.put("id_post",id_post);
                                userMap.put("pj",pj2);
                                userMap.put("nama",nama);
                                userMap.put("id",id);
                                userMap.put("tanggal",tanggal2);
                                userMap.put("mulai",mulai2);
                                userMap.put("selesai",selesai2);

                                mBase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            event.setEnabled(false);
                                            pj.setEnabled(false);
                                            lokasi.setEnabled(false);
                                            alamat.setEnabled(false);
                                            deskripsi.setEnabled(false);
                                            mulai.setEnabled(false);
                                            selesai.setEnabled(false);

                                            btnDatePicker.setVisibility(View.INVISIBLE);
                                            alamatBtn.setVisibility(View.INVISIBLE);
                                            editBtn.setVisibility(View.VISIBLE);
                                            saveBtn.setVisibility(View.INVISIBLE);
                                            Toast.makeText(EventEdit.this,"Data Berhasil Di Sunting",Toast.LENGTH_LONG).show();
                                        }else {
                                            Toast.makeText(EventEdit.this,"Data Gagal Di Sunting",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                        alamatBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                initGoogleAPIClient();//Init Google API Client
                                checkPermissions();//Check Permission
                                Intent intent = new Intent(EventEdit.this, MapsInput.class);
                                startActivityForResult(intent,PICK_CONTACT_REQUEST);
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
    private void showDateDialog(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                tanggal.setText(dateFormater.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(EventEdit.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(EventEdit.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EventEdit.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(EventEdit.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(EventEdit.this,
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
                            status.startResolutionForResult(EventEdit.this, REQUEST_CHECK_SETTINGS);
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
                    Toast.makeText(EventEdit.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
