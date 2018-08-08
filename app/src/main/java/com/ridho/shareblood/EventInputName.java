package com.ridho.shareblood;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class EventInputName extends AppCompatActivity {

    private Button dateBtn,nextBtn;
    private MaterialEditText name,pj,date,mulai,selesai;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private String Sname,Sdate,Spj;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private ProgressDialog progressDialog;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_input_name);

        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Input Data Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("EventDonor");
        mUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(EventInputName.this);
        dateBtn = (Button)findViewById(R.id.date_input);
        nextBtn = (Button)findViewById(R.id.nextButton);

        name = (MaterialEditText)findViewById(R.id.eventName);
        pj = (MaterialEditText)findViewById(R.id.penanggunJawab);
        date = (MaterialEditText)findViewById(R.id.date_text);
        mulai = (MaterialEditText)findViewById(R.id.mulai);
        selesai = (MaterialEditText)findViewById(R.id.selesai);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sname = name.getText().toString();
                mDatabase = FirebaseDatabase.getInstance().getReference("EventDonor").child(Sname);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Sname = name.getText().toString();
                        Sdate = date.getText().toString();
                        Spj = pj.getText().toString();
                        String uid = mUser.getUid();
                        String mulai1 = mulai.getText().toString();
                        String selesai1 = selesai.getText().toString();
                        if (!TextUtils.isEmpty(Sname) || !TextUtils.isEmpty(Sdate) || !TextUtils.isEmpty(Spj) || !TextUtils.isEmpty(mulai1) || !TextUtils.isEmpty(selesai1) ){

                            progressDialog.setTitle("Saving Data");
                            progressDialog.setMessage("Please wait while we save your data !");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            Intent intent = new Intent(EventInputName.this,MapsEventInput.class);
                            intent.putExtra("name",Sname);
                            intent.putExtra("date",Sdate);
                            intent.putExtra("pj",Spj);
                            intent.putExtra("id",uid);
                            intent.putExtra("mulai",mulai1);
                            intent.putExtra("selesai",selesai1);
                            startActivity(intent);
                            finish();

                        }else {

                            Toast.makeText(EventInputName.this,"Tolong Masukkan Semua Data",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
    }

    private void showDateDialog() {
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
                date.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }
}
