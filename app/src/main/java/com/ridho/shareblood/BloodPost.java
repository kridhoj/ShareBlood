package com.ridho.shareblood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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


public class BloodPost extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private RecyclerView recyclerView;
    public View mMainView;
    private DatabaseReference mDatabase;
    private TextView tx1;
    public String a;
    private RelativeLayout emptyState;
    public BloodPost() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_blood_post, container, false);
       recyclerView = (RecyclerView)v.findViewById(R.id.blood_post_list);
        LinearLayoutManager Mlayout = new LinearLayoutManager(this.getActivity());
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);
        emptyState = (RelativeLayout)v.findViewById(R.id.emptyState);
        recyclerView.setLayoutManager(Mlayout);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid1 = firebaseUser.getUid();
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("userPost").child(uid1).child("bloodPost");
        mBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    emptyState.setVisibility(View.INVISIBLE);
                    ubahData();
                } else {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       return v;
    }

    public void ubahData(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("userPost").child(uid).child("bloodPost");
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<blood_blog> options =
                new FirebaseRecyclerOptions.Builder<blood_blog>()
                        .setQuery(mDatabase, blood_blog.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<blood_blog, BloodPost.BlogViewHolder>(
                options
        ) {
            @Override
            public BloodPost.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blood_post, parent, false);

                return new BloodPost.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final BloodPost.BlogViewHolder holder, int position, final blood_blog model) {

                final String key = getRef(position).getKey();
                final String golongan = model.getGolongan();
                holder.setGolongan(model.getGolongan());
                holder.setTanggal(model.getTanggal());
                holder.setNotif(model.getNotif());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), key, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), BloodDetail.class);
                        intent.putExtra("golongan",golongan);
                        intent.putExtra("key", key);
                        startActivity(intent);
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
        public void setTanggal(String tanggal){
            TextView tanggal1 = (TextView)itemView.findViewById(R.id.tanggal);
            tanggal1.setText(tanggal);
        }

        public void setGolongan(String golongan){
            TextView golongan2 = (TextView)itemView.findViewById(R.id.golonganTx);
            golongan2.setText(golongan);
        }

        public void setNotif(String notif){
            if (notif.equals("yes")){
                ImageView img = (ImageView)itemView.findViewById(R.id.notif);
                img.setVisibility(View.VISIBLE);
            }else{
                ImageView img = (ImageView)itemView.findViewById(R.id.notif);
                img.setVisibility(View.INVISIBLE);
            }
        }
    }
}
