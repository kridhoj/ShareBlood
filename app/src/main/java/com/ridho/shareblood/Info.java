package com.ridho.shareblood;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Info extends Fragment {
    private RecyclerView recyclerView;
    public View mMainView;
    private DatabaseReference mDatabase;
    private RadioGroup radioGroup;
    private String a;
    public Info() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_info, container, false);
        recyclerView = (RecyclerView)mMainView.findViewById(R.id.pendonor_list);
        LinearLayoutManager Mlayout = new LinearLayoutManager(this.getActivity());
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(Mlayout);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        radioGroup = (RadioGroup)mMainView.findViewById(R.id.gRadio);
        mBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                a = dataSnapshot.child("golongan").getValue().toString();
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch(i) {
                            case R.id.aType:
                                a = "A";
                                ubahData(a);
                                break;
                            case R.id.bType:
                                a = "B";
                                ubahData(a);
                                break;
                            case R.id.abType:
                                a = "AB";
                                ubahData(a);
                                break;
                            case R.id.oType:
                                a = "O";
                                ubahData(a);
                                break;
                        }
                    }
                });


                ubahData(a);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return mMainView;
    }
    public void ubahData(String s){

        mDatabase = FirebaseDatabase.getInstance().getReference("pendonorSukarela").child(s);
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<info_blog> options =
                new FirebaseRecyclerOptions.Builder<info_blog>()
                        .setQuery(mDatabase, info_blog.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<info_blog, Info.BlogViewHolder>(
                options
        ) {
            @Override
            public Info.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.info_list, parent, false);

                return new Info.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final Info.BlogViewHolder holder, int position, final info_blog model) {

                final String key = getRef(position).getKey();
                final String kol = model.getUid();

                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("Users").child(kol);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("profile_image").getValue().toString();

                        holder.setName(name);
                        holder.setProfile_image(getActivity(),image);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(),key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),Pendonor.class);
                                intent.putExtra("key",key);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        firebaseRecyclerAdapter.startListening();

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);

        }
        public void setName(String name){
            TextView username = (TextView)itemView.findViewById(R.id.username);
            username.setText(name);
        }
        public void setProfile_image(Context c,String profile_image){
            CircleImageView setProfile_image = (CircleImageView)itemView.findViewById(R.id.imgProfil);
            Picasso.with(c).load(profile_image).into(setProfile_image);
        }

    }

}
