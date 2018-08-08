package com.ridho.shareblood;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PendonorList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private String id_post;
    private TextView tx1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendonor_list);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("List Pendonor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tx1 = (TextView)findViewById(R.id.tX1);
        recyclerView = (RecyclerView)findViewById(R.id.pendonor_list);
        LinearLayoutManager Mlayout = new LinearLayoutManager(this);
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(Mlayout);

        Intent intent = getIntent();
        id_post = intent.getStringExtra("id_post");
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("pendonor");
        mBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tx1.setVisibility(View.INVISIBLE);
                    ubahData();
                } else {
                    tx1.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ubahData();
    }

    public void ubahData(){

        mDatabase = FirebaseDatabase.getInstance().getReference("pendonor").child(id_post);
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<listPendonor> options =
                new FirebaseRecyclerOptions.Builder<listPendonor>()
                        .setQuery(mDatabase, listPendonor.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<listPendonor, PendonorList.BlogViewHolder>(
                options
        ) {
            @Override
            public PendonorList.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.info_list, parent, false);

                return new PendonorList.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final PendonorList.BlogViewHolder holder, int position, final listPendonor model) {

                final String key = getRef(position).getKey();
                final String kol = model.getId();

                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("Users").child(kol);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("profile_image").getValue().toString();

                        holder.setName(name);
                        holder.setProfile_image(getApplicationContext(),image);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(PendonorList.this,key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(PendonorList.this,Pendonor.class);
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
        public void setProfile_image(Context c, String profile_image){
            CircleImageView setProfile_image = (CircleImageView)itemView.findViewById(R.id.imgProfil);
            Picasso.with(c).load(profile_image).into(setProfile_image);
        }

    }

}
