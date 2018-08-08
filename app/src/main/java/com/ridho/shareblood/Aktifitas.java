package com.ridho.shareblood;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;


public class Aktifitas extends Fragment {

    private RecyclerView recyclerView1,recyclerView2,recyclerView3;
    public View mMainView;
    private DatabaseReference mDatabase;
    private TextView tx1,tx2,tx3;

    public String a;
    public Aktifitas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_aktifitas, container, false);
        recyclerView1 = (RecyclerView)v.findViewById(R.id.blood_post_list);
        recyclerView2 = (RecyclerView)v.findViewById(R.id.event_post_list);
        recyclerView3 = (RecyclerView)v.findViewById(R.id.react_post_list);

        LinearLayoutManager Mlayout = new LinearLayoutManager(this.getActivity());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager((this.getActivity()));
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager((this.getActivity()));
        linearLayoutManager1.setReverseLayout(true);
        linearLayoutManager1.setStackFromEnd(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setReverseLayout(true);
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);
        tx1 = (TextView)v.findViewById(R.id.tX1);
        tx2 = (TextView)v.findViewById(R.id.tX2);
        tx3 = (TextView)v.findViewById(R.id.tX3);
        recyclerView1.setLayoutManager(Mlayout);
        recyclerView2.setLayoutManager(linearLayoutManager1);
        recyclerView3.setLayoutManager(linearLayoutManager2);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid1 = firebaseUser.getUid();
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("userPost").child(uid1).child("bloodPost");
        mBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tx1.setVisibility(View.INVISIBLE);
                    ubahData();
                } else {
                    tx1.setVisibility(View.VISIBLE);
                    recyclerView1.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference dBase = FirebaseDatabase.getInstance().getReference().child("userPost").child(uid1).child("eventPost");
        dBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tx2.setVisibility(View.INVISIBLE);
                    eventData();
                } else {
                    tx2.setVisibility(View.VISIBLE);
                    recyclerView2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference DBase = FirebaseDatabase.getInstance().getReference().child("reactPost").child(uid1);
        DBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tx3.setVisibility(View.INVISIBLE);
                    reactData();
                } else {
                    tx3.setVisibility(View.VISIBLE);
                    recyclerView3.setVisibility(View.INVISIBLE);
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

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        AlertDialog.Builder mDialog = new AlertDialog.Builder(getContext());
                        mDialog.setTitle("Verifikasi");
                        mDialog.setMessage("Apakah Anda Yakin Ingin Menghapus");
                        mDialog.setNegativeButton("Cancel",null);
                        mDialog.setPositiveButton("Ya, Hapus Data", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabase.child(key).removeValue();
                                FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = firebaseAuth.getUid();
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("userPost").child(uid).child("bloodPost").child(key);
                                mDatabase.removeValue();
                                DatabaseReference mReact = FirebaseDatabase.getInstance().getReference("darahDonor").child(golongan).child(key);
                                mReact.removeValue();
                                Toast.makeText(getContext(),"Data Berhasil Di Hapus",Toast.LENGTH_LONG).show();
                            }
                        });
                        AlertDialog dialog = mDialog.create();
                        dialog.show();
                        return true;
                    }
                });

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

        recyclerView1.setAdapter(firebaseRecyclerAdapter);

        class BlogViewHolder extends RecyclerView.ViewHolder{

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
    public void eventData(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("userPost").child(uid).child("eventPost");
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<event_blog> options =
                new FirebaseRecyclerOptions.Builder<event_blog>()
                        .setQuery(mDatabase, event_blog.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<event_blog, EventPost.BlogViewHolder>(
                options
        ) {
            @Override
            public EventPost.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.event_post, parent, false);

                return new EventPost.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final EventPost.BlogViewHolder holder1, int position, final event_blog model) {

                final String key = getRef(position).getKey();
                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("eventDonor").child(key);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("nama").getValue().toString();
                        final String lokasi1 = dataSnapshot.child("lokasi").getValue().toString();
                        final String tanggal1 = dataSnapshot.child("tanggal").getValue().toString();
                        holder1.setNama(name);
                        holder1.setLokasi(lokasi1);
                        holder1.setTanggal(tanggal1);

                        holder1.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(),key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),EventEdit.class);
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

        recyclerView2.setAdapter(firebaseRecyclerAdapter);

        class BlogViewHolder extends RecyclerView.ViewHolder{

            View mView;

            public BlogViewHolder(View itemView) {
                super(itemView);

            }
            public void setNama(String nama){
                TextView username = (TextView)itemView.findViewById(R.id.nama);
                username.setText(nama);
            }


            public void setLokasi(String lokasi) {
                TextView lokasi1 = (TextView)itemView.findViewById(R.id.lokasiXX);
                lokasi1.setText(lokasi);
            }

            public void setTanggal(String tanggal) {
                TextView tanggal1 = (TextView)itemView.findViewById(R.id.tanggal);
                tanggal1.setText(tanggal);
            }

        }
    }
    public void reactData(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("reactPost").child(uid);
        mDatabase.keepSynced(true);

        FirebaseRecyclerOptions<react> options =
                new FirebaseRecyclerOptions.Builder<react>()
                        .setQuery(mDatabase, react.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<react, ReactPost.BlogViewHolder>(
                options
        ) {
            @Override
            public ReactPost.BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.react_list, parent, false);

                return new ReactPost.BlogViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ReactPost.BlogViewHolder holder2, int position, final react model) {

                final String key = getRef(position).getKey();
                final String uID = model.getId();
                DatabaseReference mBase = FirebaseDatabase.getInstance().getReference("Users").child(uID);
                mBase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String nama = dataSnapshot.child("name").getValue().toString();
                        holder2.setGoldar(model.getGoldar());
                        holder2.nama1(nama);
                        final String id_post = model.getId_post();
                        holder2.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(),key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),ReactDetail.class);
                                intent.putExtra("id_post",id_post);
                                intent.putExtra("golongan",model.getGoldar());
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

        recyclerView3.setAdapter(firebaseRecyclerAdapter);

        class BlogViewHolder extends RecyclerView.ViewHolder{

            View mView;

            public BlogViewHolder(View itemView) {
                super(itemView);

            }

            public void setGoldar(String goldar){
                TextView golonganDarah = (TextView)itemView.findViewById(R.id.goldar);
                golonganDarah.setText(goldar);
            }

            public void nama1(String nama){
                TextView nama2 = (TextView)itemView.findViewById(R.id.namaTx);
                nama2.setText(nama);
            }
        }
    }
}
