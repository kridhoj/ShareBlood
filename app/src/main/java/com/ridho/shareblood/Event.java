package com.ridho.shareblood;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class Event extends Fragment {


    private RecyclerView recyclerView;
    public View mMainView;
    private DatabaseReference mDatabase;
    private TextView tx1;
    public String a;
    public Event() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v = inflater.inflate(R.layout.fragment_event, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.event_post_list);
        LinearLayoutManager Mlayout = new LinearLayoutManager(this.getActivity());
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);

        recyclerView.setLayoutManager(Mlayout);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();

        tx1 = (TextView)v.findViewById(R.id.tx1);

        ubahData();
        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),EventInputName.class);
                startActivity(intent);
            }
        });

       return v;


    }


    public void ubahData(){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("eventDonor");
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<event_blog> options =
                new FirebaseRecyclerOptions.Builder<event_blog>()
                        .setQuery(mDatabase, event_blog.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<event_blog, Event.BlogViewHolder>(
                options
        ) {
            @Override
            public Event.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_blood_list, parent, false);

                return new Event.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final Event.BlogViewHolder holder, int position, final event_blog model) {

                final String key = getRef(position).getKey();

                final String kol = model.getId();

                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("Users").child(kol);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.setNama(model.getNama());
                        holder.setLokasi(model.getLokasi());
                        holder.setMulai(model.getMulai());
                        holder.setSelesai(model.getSelesai());

                        holder.setTanggal(model.getTanggal());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(),key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),EventDetail.class);
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
        public void setNama(String nama){
            TextView username = (TextView)itemView.findViewById(R.id.nama);
            username.setText(nama);
        }


        public void setLokasi(String lokasi) {
            TextView lokasi1 = (TextView)itemView.findViewById(R.id.lokasiTx);
            lokasi1.setText(lokasi);
        }

        public void setTanggal(String tanggal) {
            TextView tanggal1 = (TextView)itemView.findViewById(R.id.tanggalTx);
            tanggal1.setText(tanggal);
        }

        public void setMulai(String mulai) {
      TextView mulai1= (TextView)itemView.findViewById(R.id.mulaiTx);
      mulai1.setText(mulai);
        }

        public void setSelesai(String selesai){
            TextView selesai1 = (TextView)itemView.findViewById(R.id.selesaiTx);
            selesai1.setText(selesai);
        }


    }

}
