package com.ridho.shareblood;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private DatabaseReference mDatabase;
    private RadioGroup radioGroup;
public View mMainView;
    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_profile, container, false);
        radioGroup = (RadioGroup)mMainView.findViewById(R.id.gRadio);
        RadioButton profil = (RadioButton)mMainView.findViewById(R.id.profil);
        profil.setChecked(true);
        Fragment fragment = new Profile_Frag();
        getFragmentManager().beginTransaction().add(R.id.conteks_menu,fragment).commit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Fragment fragment = null;
                switch(i) {
                    case R.id.profil:
                        fragment = new Profile_Frag();
                        break;
                    case R.id.aktifitas:
                        fragment = new Aktifitas();
                        break;

                }
                if(fragment != null)
                {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.conteks_menu, fragment);
                    ft.commit();
                }
            }
        });
        return mMainView;
    }
}
