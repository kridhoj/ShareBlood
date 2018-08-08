package com.ridho.shareblood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.*;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ProfileInput extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private Toolbar mToolbar;
    private MaterialEditText iName,iEmail,iAddress,iGolongan,iAgama;
    private Button iMaps,iType,mNext;
    private static final String TAG = "ProfileInputActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private MaterialEditText tvDateResult;
    private Button btnDatePicker;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private String golongan_darah;
    private ProgressDialog mRegProgress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RadioGroup radioGroup;
    private FirebaseUser current_user;
    static final int PICK_CONTACT_REQUEST = 1;
    private String alamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_input);
        radioGroup = (RadioGroup)findViewById(R.id.rGroup);
        iAgama = (MaterialEditText)findViewById(R.id.agama);
        iName = (MaterialEditText)findViewById(R.id.nama_input);
        iEmail = (MaterialEditText)findViewById(R.id.email_input);
        iAddress = (MaterialEditText)findViewById(R.id.address_input);
        iMaps = (Button)findViewById(R.id.maps_input);
        iType = (Button)findViewById(R.id.type_button);
        mNext = (Button)findViewById(R.id.nextButton);
        tvDateResult = (MaterialEditText) findViewById(R.id.date_text);
        btnDatePicker = (Button)findViewById(R.id.date_input);
        iGolongan = (MaterialEditText)findViewById(R.id.gol_Text);
        iGolongan.setEnabled(false);
        tvDateResult.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();
        mRegProgress = new ProgressDialog(this);
        if (isServicesOK()){
            init();
        }
        initGoogleAPIClient();
        checkPermissions();

        Common.currentToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("newPost");
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();

        iType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(ProfileInput.this);
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
                        iGolongan.setText(golongan_darah);
                    }
                });
                bType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        golongan_darah = "B";
                        dialog.dismiss();
                        iGolongan.setText(golongan_darah);
                    }
                });
                abType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        golongan_darah = "AB";
                        dialog.dismiss();
                        iGolongan.setText(golongan_darah);
                    }
                });
                oType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        golongan_darah = "O";
                        dialog.dismiss();
                        iGolongan.setText(golongan_darah);
                    }
                });
                dialog.show();
            }
        });


        mToolbar = (Toolbar)findViewById(R.id.toolbar_page);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Input Data Diri");

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Intent mainIntent = new Intent(ProfileInput.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else {
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                            switch (checkedId) {
                                case R.id.lakiLaki:
                                    simpan("Laki-laki");
                                    break;
                                case R.id.perempuan:
                                    simpan("Perempuan");
                                    break;
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void simpan(final String s){
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = iName.getText().toString();
                String email = iEmail.getText().toString();
                String address = iAddress.getText().toString();
                String tanggal = tvDateResult.getText().toString();
                String golongan = iGolongan.getText().toString();
                String phone_user = current_user.getPhoneNumber();
                String agama1 = iAgama.getText().toString();

                if(!TextUtils.isEmpty(agama1) || !TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(address) || !TextUtils.isEmpty(tanggal) || !TextUtils.isEmpty(golongan)){

                    mRegProgress.setTitle("Saving Data");
                    mRegProgress.setMessage("Please wait while we save your data !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    lanjutkan(display_name, email, address, tanggal, golongan, phone_user,s,agama1);

                }else{

                    Toast.makeText(ProfileInput.this,"Harap Isi Semua Field",Toast.LENGTH_LONG).show();

                }

            }
        });
    }
    //open datepicker
    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                tvDateResult.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    //open maps
    private void init(){
        iMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGoogleAPIClient();
                checkPermissions();
                Intent intent = new Intent(ProfileInput.this, MapsInput.class);
                startActivityForResult(intent,PICK_CONTACT_REQUEST);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ProfileInput.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ProfileInput.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void lanjutkan(final String display_name, String email, String address, String tanggal, String golongan, String phoneNumber,String s, String agama1){
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        String image = "https://firebasestorage.googleapis.com/v0/b/shareblood-bb427.appspot.com/o/profile_images%2Fsplash_logo.png?alt=media&token=5d1a555e-f378-45bb-8ad8-5aa1841dbc96";
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", display_name);
        userMap.put("email", email);
        userMap.put("address", address);
        userMap.put("tanggal", tanggal);
        userMap.put("golongan", golongan);
        userMap.put("profile_image",image);
        userMap.put("phone_number", phoneNumber);
        userMap.put("jenis_kelamin",s);
        userMap.put("pendonor","no");
        userMap.put("agama",agama1);
        userMap.put("device_token", Common.currentToken);
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("Token");
        mBase.child(uid).setValue(Common.currentToken);

        mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    mRegProgress.dismiss();

                    Intent mainIntent = new Intent(ProfileInput.this, ImageInput.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();

                }else {

                    mRegProgress.hide();
                    Toast.makeText(ProfileInput.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
    });
    }
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(ProfileInput.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ProfileInput.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileInput.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ProfileInput.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(ProfileInput.this,
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
                            status.startResolutionForResult(ProfileInput.this, REQUEST_CHECK_SETTINGS);
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
            alamat = data.getStringExtra("address");
            iAddress.setText(alamat);
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
                    Toast.makeText(ProfileInput.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
