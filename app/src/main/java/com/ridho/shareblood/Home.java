package com.ridho.shareblood;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private RecyclerView recyclerView;
    public View mMainView;
    private DatabaseReference mDatabase;
    private RadioGroup radioGroup;
    private TextView tx1;
    public String a;
    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView)mMainView.findViewById(R.id.blood_post_list);
        LinearLayoutManager Mlayout = new LinearLayoutManager(this.getActivity());
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(Mlayout);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        tx1 = (TextView)mMainView.findViewById(R.id.tx1);

        String c = "A";


        ubahData(c);

        radioGroup = (RadioGroup)mMainView.findViewById(R.id.gRadio);

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

        FloatingActionButton fab = mMainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),BloodDonorInput.class);
                startActivity(intent);
            }
        });


        return mMainView;
    }

    public void ubahData(String s){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("darahDonor").child(s);
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<blood_blog> options =
                new FirebaseRecyclerOptions.Builder<blood_blog>()
                        .setQuery(mDatabase, blood_blog.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blood_blog, BlogViewHolder>(
                options
        ) {
            @Override
            public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blood_list_item, parent, false);

                return new BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final BlogViewHolder holder, int position, final blood_blog model) {

                final String key = getRef(position).getKey();
                final String gol = model.getGolongan();
                final String kol = model.getId();

                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("Users").child(kol);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();
                        String date = dataSnapshot.child("tanggal").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        String blood = dataSnapshot.child("golongan").getValue().toString();
                        String phone = dataSnapshot.child("phone_number").getValue().toString();
                        String image = dataSnapshot.child("profile_image").getValue().toString();


                        holder.setName(name);
                        holder.setGolongan(model.getGolongan());
                        holder.setPesan(model.getPesan());
                        holder.setProfile_image(getActivity(),image);
                        holder.setTanggal(model.getTanggal());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(),key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),BloodRequestDetail.class);
                                intent.putExtra("key",key);
                                intent.putExtra("gol1",gol);
                                intent.putExtra("id",kol);
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
        public void setPesan(String pesan){
            TextView messageTx = (TextView)itemView.findViewById(R.id.messageTx);
            messageTx.setText(pesan);
        }

        public void setGolongan(String golongan){
            TextView golongan2 = (TextView)itemView.findViewById(R.id.golonganTx);
            golongan2.setText(golongan);

        }
        public void setProfile_image(Context c,String profile_image){
            CircleImageView setProfile_image = (CircleImageView)itemView.findViewById(R.id.imgProfil);
            Picasso.with(c).load(profile_image).resize(40,40).into(setProfile_image);
        }
        public void setTanggal(String tanggal){
            TextView tanggal1 = (TextView)itemView.findViewById(R.id.tanggal);
            tanggal1.setText(tanggal);
        }
    }
}
