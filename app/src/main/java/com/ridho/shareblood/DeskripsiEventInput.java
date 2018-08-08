package com.ridho.shareblood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.HashMap;
import java.util.Random;

public class DeskripsiEventInput extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deskripsi_event_input);
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Input Data Darah Donor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegProgress = new ProgressDialog(this);
        PostBtn = (Button)findViewById(R.id.postBtn);
        message = (EditText)findViewById(R.id.messageTxt);
        pesan = message.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("eventDonor");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
                                hospital = data.getStringExtra("lokasi");
                                String nama = data.getStringExtra("nama");
                                String id = data.getStringExtra("id");
                                String tanggal = data.getStringExtra("tanggal");
                                String mulai = data.getStringExtra("mulai");
                                String pj = data.getStringExtra("pj");
                                String selesai = data.getStringExtra("selesai");
                                lat1 = data.getDoubleExtra("lat",1);
                                longt1 = data.getDoubleExtra("longt",1);
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
                                lanjutkan(alamat, hospital, lat, longt, pesan,phone,profileUrl,id_post,username,nama,id,tanggal,mulai,selesai,pj);
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

    private void lanjutkan(String alamat, String hospital, String lat, String longt, String pesan, String phone, String profileUrl, final String post_id, String username, String nama, String id, String tanggal, String mulai, String selesai, String pj) {
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid1 = current_user.getUid();
        DatabaseReference mdatabase = mDatabase.child(id_post);
        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("userPost").child(uid1).child("eventPost").child(id_post);

        String device_token = FirebaseInstanceId.getInstance().getToken();

        final HashMap<String, String> userMap = new HashMap<>();
        userMap.put("alamat", alamat);
        userMap.put("lokasi", hospital);
        userMap.put("lat", lat);
        userMap.put("phone",phone);
        userMap.put("long", longt);
        userMap.put("deskripsi", pesan);
        userMap.put("id_post",post_id);
        userMap.put("pj",pj);
        userMap.put("nama",nama);
        userMap.put("id",id);
        userMap.put("tanggal",tanggal);
        userMap.put("mulai",mulai);
        userMap.put("selesai",selesai);

        mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
            mDb.child("id_post").setValue(post_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        mRegProgress.dismiss();

                        Intent mainIntent = new Intent(DeskripsiEventInput.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();

                    }else {
                        Toast.makeText(DeskripsiEventInput.this, "Cannot Add. Please check the form and try again.", Toast.LENGTH_LONG).show();

                    }
                }
            });
                }else {

                    mRegProgress.hide();
                    Toast.makeText(DeskripsiEventInput.this, "Cannot Add. Please check the form and try again.", Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}
