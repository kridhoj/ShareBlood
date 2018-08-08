package com.ridho.shareblood;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.ridho.shareblood.Model.MyResponse;
import com.ridho.shareblood.Model.Notification;
import com.ridho.shareblood.Model.Sender;
import com.ridho.shareblood.Remote.APIService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageInput extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button PostBtn;
    private EditText message;
    public String hospital,alamat,pesan,golongan;
    public double lat1,longt1;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase,imgDb;
    private ProgressDialog mRegProgress;
   private String profileImg,id_post;
   private int post_id;
   APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_input);
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Input Data Darah Donor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegProgress = new ProgressDialog(this);
        PostBtn = (Button)findViewById(R.id.postBtn);
        message = (EditText)findViewById(R.id.messageTxt);
        pesan = message.getText().toString();

        Intent data = getIntent();
        golongan = data.getStringExtra("bloodtype");
        mService = Common.getFCMClient();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("darahDonor").child(golongan);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Notification notification = new Notification("Ada Yang Membutuhkan Darah","POST TERBARU");
                        Sender sender = new Sender("/topics/newPost",notification,"");
                        mService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        MyResponse myResponse = response.body();
                                    }
                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("ERROR",t.getMessage());
                                    }
                                });
                        final String uid = mCurrentUser.getUid();
                        final String phone = mCurrentUser.getPhoneNumber();
                        imgDb = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                        imgDb.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                profileImg = dataSnapshot.child("profile_image").getValue().toString();
                                String username = dataSnapshot.child("name").getValue().toString();
                                Intent data = getIntent();
                                golongan = data.getStringExtra("bloodtype");
                                alamat = data.getStringExtra("alamat");
                                hospital = data.getStringExtra("hospital");
                                lat1 = data.getDoubleExtra("lat",1);
                                longt1 = data.getDoubleExtra("long",1);
                                String pesan = message.getText().toString();
                                String lat = Double.toString(lat1);
                                String longt = Double.toString(longt1);
                                String profileUrl = profileImg;
                                mRegProgress.setTitle("Saving Data");
                                mRegProgress.setMessage("Please wait while we save your data !");
                                mRegProgress.setCanceledOnTouchOutside(false);
                                mRegProgress.show();
                                Random r = new Random();
                                post_id = r.nextInt(99999999 - 1) + 1;
                                id_post = String.valueOf(post_id);
                                lanjutkan(alamat, hospital, lat, longt, pesan, uid,phone,profileUrl,id_post,username,golongan);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

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

    private void lanjutkan(String alamat, String hospital, String lat, String longt, final String pesan, String uid, String phone, String profileUrl, String post_id, String name, final String golongan) {
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid1 = current_user.getUid();
        DatabaseReference mdatabase = mDatabase.child(id_post);
        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("userPost").child(uid1).child("bloodPost").child(id_post);

        String device_token = FirebaseInstanceId.getInstance().getToken();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

       final HashMap<String, String> userMap = new HashMap<>();
        userMap.put("alamat", alamat);
        userMap.put("hospital", hospital);
        userMap.put("lat", lat);
        userMap.put("long", longt);
        userMap.put("tanggal",date);
        userMap.put("pesan", pesan);
        userMap.put("id", uid);
        userMap.put("id_post",post_id);
        userMap.put("name",name);
        userMap.put("golongan",golongan);
        userMap.put("notif", "no");

        mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                mDb.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mRegProgress.dismiss();

                            Intent mainIntent = new Intent(MessageInput.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(MessageInput.this,"Data Berhasil Di Post",Toast.LENGTH_LONG).show();



                        }else{
                            mRegProgress.hide();
                            Toast.makeText(MessageInput.this, "Cannot Add. Please check the form and try again.", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                }else {

                    mRegProgress.hide();
                    Toast.makeText(MessageInput.this, "Cannot Add. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });
    }

}
