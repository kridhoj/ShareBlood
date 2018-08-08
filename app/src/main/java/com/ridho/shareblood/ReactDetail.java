package com.ridho.shareblood;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReactDetail extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button cancel,call,text;
    private TextView name,phone,user_add,goldar,alamat,hospital,deskripsi;
    private CircleImageView profileImg;
    String id_post1,goldar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react_detail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_page);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profileImg = (CircleImageView)findViewById(R.id.imgProfil);
        call = (Button)findViewById(R.id.hubungi);
        text = (Button)findViewById(R.id.pesan);
        cancel = (Button)findViewById(R.id.cancel);

        name = (TextView)findViewById(R.id.name);
        phone = (TextView)findViewById(R.id.phone);
        user_add = (TextView)findViewById(R.id.user_add);
        goldar = (TextView)findViewById(R.id.goldar);
        alamat = (TextView)findViewById(R.id.alamat);
        hospital = (TextView)findViewById(R.id.rumahsakit);
        deskripsi = (TextView)findViewById(R.id.deskripsi);

        Intent intent = getIntent();
        id_post1 = intent.getStringExtra("id_post");
        goldar1 = intent.getStringExtra("golongan");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("darahDonor").child(goldar1).child(id_post1);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String golongan = dataSnapshot.child("golongan").getValue().toString();
                final String alamat1 = dataSnapshot.child("alamat").getValue().toString();
                final String id = dataSnapshot.child("id").getValue().toString();
                final String deskripsi1 = dataSnapshot.child("pesan").getValue().toString();
                final String hospital1 = dataSnapshot.child("hospital").getValue().toString();

                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference("Users").child(id);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nama = dataSnapshot.child("name").getValue().toString();
                        final String phone1 = dataSnapshot.child("phone_number").getValue().toString();
                        String image = dataSnapshot.child("profile_image").getValue().toString();
                        String addUser = dataSnapshot.child("address").getValue().toString();
                        name.setText(nama);
                        goldar.setText(golongan);
                        alamat.setText(alamat1);
                        deskripsi.setText(deskripsi1);
                        hospital.setText(hospital1);
                        user_add.setText(addUser);
                        phone.setText(phone1);
                        Picasso.with(getApplicationContext()).load(image).into(profileImg);

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String dial = "tel:" + phone1;
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                            }
                        });

                        text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone1));
                                startActivity(smsIntent);
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
                                String Uid = firebaseAuth.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("pendonor").child(id_post1).child(Uid);
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("reactPost").child(Uid).child(id_post1);
                                db.removeValue();
                                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ReactDetail.this,"Anda Batal Untuk Mendonorkan Darah",Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(ReactDetail.this,MainActivity.class);
                                            startActivity(intent);
                                        }
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
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
