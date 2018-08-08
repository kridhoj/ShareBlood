package com.ridho.shareblood;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Pendonor extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button call,text;
    private MaterialEditText p_name,p_phone,p_address,p_blood,p_borndate,p_email,p_jKelamin,p_Agama;
    private TextView Uid;
    private CircleImageView profileImg;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendonor);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_page);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        call = (Button)findViewById(R.id.hubungi);
        text = (Button)findViewById(R.id.pesan);
        Uid = (TextView)findViewById(R.id.uid);
        p_Agama = (MaterialEditText)findViewById(R.id.p_agama);
        p_name = (MaterialEditText)findViewById(R.id.p_name);
        p_phone = (MaterialEditText)findViewById(R.id.p_phone);
        p_jKelamin = (MaterialEditText)findViewById(R.id.p_gender);
        p_address = (MaterialEditText)findViewById(R.id.p_alamat);
        p_blood = (MaterialEditText)findViewById(R.id.p_blood);
        p_borndate = (MaterialEditText)findViewById(R.id.p_date);
        p_email = (MaterialEditText)findViewById(R.id.p_email);
        profileImg = (CircleImageView)findViewById(R.id.imgProfil);

        Intent intent = getIntent();
        uid = intent.getStringExtra("key");


        p_name.setEnabled(false);
        p_phone.setEnabled(false);
        p_borndate.setEnabled(false);
        p_blood.setEnabled(false);
        p_address.setEnabled(false);
        p_email.setEnabled(false);
        p_jKelamin.setEnabled(false);
        p_Agama.setEnabled(false);
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String date = dataSnapshot.child("tanggal").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String blood = dataSnapshot.child("golongan").getValue().toString();
                final String phone = dataSnapshot.child("phone_number").getValue().toString();
                String image = dataSnapshot.child("profile_image").getValue().toString();
                String gender = dataSnapshot.child("jenis_kelamin").getValue().toString();
                String pendonor = dataSnapshot.child("pendonor").getValue().toString();
                String agama = dataSnapshot.child("agama").getValue().toString();

                Uid.setText(uid);
                p_jKelamin.setText(gender);
                p_address.setText(address);
                p_blood.setText(blood);
                p_name.setText(name);
                p_email.setText(email);
                p_borndate.setText(date);
                p_phone.setText(phone);
                p_Agama.setText(agama);
                Picasso.with(getApplicationContext()).load(image).into(profileImg);

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String dial = "tel:" + phone;
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                    }
                });

                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phone));
                        startActivity(smsIntent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
