package com.ridho.shareblood;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class SplashScreen extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    Button splash_signin_button;
    ImageView splash_image;
    Animation go_top_anim, go_bot_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        splash_signin_button = (Button) findViewById(R.id.splash_button);
        splash_image = (ImageView) findViewById(R.id.splash_logo);

        go_bot_anim = AnimationUtils.loadAnimation(this, R.anim.go_bot_anim);
        go_top_anim = AnimationUtils.loadAnimation(this, R.anim.go_top_anim);

        splash_image.setAnimation(go_top_anim);
        splash_signin_button.setAnimation(go_bot_anim);

        splash_signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startintent = new Intent(SplashScreen.this, PhoneNumberVerification.class);
                startActivity(startintent);

            }
        });
        if (mCurrentUser != null) {
            String uid = mCurrentUser.getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        }
}