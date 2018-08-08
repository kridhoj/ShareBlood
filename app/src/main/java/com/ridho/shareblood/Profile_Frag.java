package com.ridho.shareblood;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Frag extends Fragment {
    private Button signoutButton,editProfile,saveBtn,pendonorBtn,cancelBtn;
    private FirebaseAuth mAuth;
    private TextView uidText,phoneTx,nameTx;
    private MaterialEditText p_Agama,p_name,p_phone,p_address,p_blood,p_borndate,p_email,p_jKelamin;
    private String uid,nama,noHp,address,blood,borndate,email,jkelamin,download_url,agama1,photo,token;
    private FirebaseUser mCurrent_user;
    private DatabaseReference mUsersDatabase;
    private CircleImageView profileImg;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;
    private Uri resultUri;

    public View mMainView;

    public Profile_Frag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mMainView = inflater.inflate(R.layout.fragment_profile_, container, false);
        mImageStorage = FirebaseStorage.getInstance().getReference();
       cancelBtn = (Button)mMainView.findViewById(R.id.btnPendonorCancel);
        pendonorBtn = (Button)mMainView.findViewById(R.id.btnPendonor);
        signoutButton =(Button)mMainView.findViewById(R.id.singout_button1);
        editProfile = (Button)mMainView.findViewById(R.id.editProfile);
        saveBtn = (Button)mMainView.findViewById(R.id.saveBtn);
        phoneTx = (TextView)mMainView.findViewById(R.id.phoneTx);
        nameTx = (TextView)mMainView.findViewById(R.id.nameTx);
        p_Agama = (MaterialEditText)mMainView.findViewById(R.id.p_agama);
        p_name = (MaterialEditText)mMainView.findViewById(R.id.p_name);
        p_phone = (MaterialEditText)mMainView.findViewById(R.id.p_phone);
        p_jKelamin = (MaterialEditText)mMainView.findViewById(R.id.p_gender);
        p_address = (MaterialEditText)mMainView.findViewById(R.id.p_alamat);
        p_blood = (MaterialEditText)mMainView.findViewById(R.id.p_blood);
        p_borndate = (MaterialEditText)mMainView.findViewById(R.id.p_date);
        p_email = (MaterialEditText)mMainView.findViewById(R.id.p_email);
        profileImg = (CircleImageView)mMainView.findViewById(R.id.imgProfil);

        p_name.setEnabled(false);
        p_phone.setEnabled(false);
        p_borndate.setEnabled(false);
        p_blood.setEnabled(false);
        p_address.setEnabled(false);
        p_email.setEnabled(false);
        p_jKelamin.setEnabled(false);
        p_Agama.setEnabled(false);

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
      uid = mCurrent_user.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mAuth = FirebaseAuth.getInstance();
        final Context c = getActivity().getApplicationContext();
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String date = dataSnapshot.child("tanggal").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                blood = dataSnapshot.child("golongan").getValue().toString();
                String phone = dataSnapshot.child("phone_number").getValue().toString();
                photo = dataSnapshot.child("profile_image").getValue().toString();
                String gender = dataSnapshot.child("jenis_kelamin").getValue().toString();
                String pendonor = dataSnapshot.child("pendonor").getValue().toString();
                String agama = dataSnapshot.child("agama").getValue().toString();
                token = dataSnapshot.child("device_token").getValue().toString();

                if (pendonor.equals("yes")){
                    pendonorBtn.setVisibility(View.INVISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                }else {
                    cancelBtn.setVisibility(View.INVISIBLE);
                    pendonorBtn.setVisibility(View.VISIBLE);
                }

                nameTx.setText(name);
                phoneTx.setText(phone);
                p_jKelamin.setText(gender);
                p_address.setText(address);
                p_blood.setText(blood);
                p_name.setText(name);
                p_email.setText(email);
                p_borndate.setText(date);
                p_Agama.setText(agama);
                p_phone.setText(phone);
                Picasso.with(c).load(photo).resize(100,100).into(profileImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendToStart();
            }
        });


        pendonorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mDialog = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.terms,null);
                mDialog.setView(mView);
                mDialog.setTitle("Syarat dan Ketentuan");
                mDialog.setNegativeButton("Bata",null);
                mDialog.setPositiveButton("Saya Setuju, Lanjutkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String uid = mCurrent_user.getUid();
                        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("pendonorSukarela").child(blood).child(uid);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("pendonor");
                        HashMap<String,String> usermap = new HashMap<>();
                        usermap.put("Uid",uid);
                        databaseReference.setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        mBase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                            cancelBtn.setVisibility(View.VISIBLE);
                                            pendonorBtn.setVisibility(View.INVISIBLE);
                                            Toast.makeText(getContext(),"Anda Telah Menjadi Pendonor Sukarela",Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    }
                });
                mDialog.show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = mCurrent_user.getUid();
                DatabaseReference Data = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("pendonor");
                Data.setValue("no").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            DatabaseReference mData = FirebaseDatabase.getInstance().getReference("pendonorSukarela").child(blood).child(uid);
                            mData.removeValue();
                            cancelBtn.setVisibility(View.INVISIBLE);
                            pendonorBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                profileImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType("image/*");
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                    }
                });

                p_name.setEnabled(true);
                p_borndate.setEnabled(true);
                p_blood.setEnabled(true);
                p_address.setEnabled(true);
                p_email.setEnabled(true);
                p_jKelamin.setEnabled(true);
                p_Agama.setEnabled(true);

                saveBtn.setVisibility(View.VISIBLE);
        saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        p_name.setEnabled(false);
                        p_phone.setEnabled(false);
                        p_borndate.setEnabled(false);
                        p_blood.setEnabled(false);
                        p_address.setEnabled(false);
                        p_email.setEnabled(false);
                        p_jKelamin.setEnabled(false);
                        jkelamin = p_jKelamin.getText().toString();
                        nama = p_name.getText().toString();
                        noHp = p_phone.getText().toString();
                        borndate = p_borndate.getText().toString();
                        blood = p_blood.getText().toString();
                        address = p_address.getText().toString();
                        email = p_email.getText().toString();
                        agama1 = p_Agama.getText().toString();

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("address",address);
                        userMap.put("agama",agama1);
                        userMap.put("email",email);
                        userMap.put("golongan",blood);
                        userMap.put("jenis_kelamin",jkelamin);
                        userMap.put("name",nama);
                        userMap.put("pendonor","yes");
                        userMap.put("phone_number",noHp);
                        userMap.put("profile_image",photo);
                        userMap.put("tanggal",borndate);
                        userMap.put("device_token",token);

                        DatabaseReference dB = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        dB.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    editProfile.setVisibility(View.VISIBLE);
                                    saveBtn.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                });
                editProfile.setVisibility(View.INVISIBLE);

            }
        });

        return mMainView;
    }
    private void sendToStart() {
        Intent startintent = new Intent(getActivity(),SplashScreen.class);
        startActivity(startintent);

        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_SHORT).show();
            File thumb_filePath = new File(imageUri.getPath());
            Picasso.with(getContext()).load(thumb_filePath).resize(80,80).into(profileImg);
//            CropImage.activity(imageUri)
//                    .setAspectRatio(1, 1)
//                    .setMinCropWindowSize(200, 200)
//                    .start(getContext(), Profile_Frag.this);
//
//            //Toast.makeText(SettingsActivity.this, imageUri, Toast.LENGTH_LONG).show();

        }

//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK) {
//
//
//                resultUri = result.getUri();
//
//                File thumb_filePath = new File(resultUri.getPath());
//                Picasso.with(getContext()).load(thumb_filePath).resize(80,80).into(profileImg);
//              /*  StorageReference filepath = mImageStorage.child("profile_images").child(uid + ".jpg");
//
//                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()){
//                            download_url = task.getResult().getDownloadUrl().toString();
//
//                            mProgressDialog.dismiss();
//                        }else{
//                            Toast.makeText(getContext(),"Photo Gagal di Upload",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//*/
//            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                Exception error = result.getError();
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
