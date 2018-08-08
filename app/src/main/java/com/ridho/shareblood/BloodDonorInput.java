package com.ridho.shareblood;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BloodDonorInput extends AppCompatActivity {
    private Toolbar mToolbar;
    private CheckBox a,b,ab,o;
    private Button NextBtn;
    private RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_donor_input);

        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Input Data Darah Donor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NextBtn = (Button)findViewById(R.id.nextButton);
        radioGroup = (RadioGroup)findViewById(R.id.rg);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String bloodType;
                switch (i){
                    case R.id.a_type:
                        bloodType = "A";
                        next(bloodType);
                        break;
                    case R.id.ab_type:
                        bloodType = "AB";
                        next(bloodType);
                        break;
                    case R.id.b_type:
                        bloodType = "B";
                        next(bloodType);
                        break;
                    case R.id.o_type:
                        bloodType = "O";
                        next(bloodType);
                        break;
                }
            }
        });

    }

    public void next(final String s){
        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BloodDonorInput.this,MapsDonorInput.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("golongan",s);
                startActivity(intent);
            }
        });
    }
}
