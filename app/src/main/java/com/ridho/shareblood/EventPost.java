package com.ridho.shareblood;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class EventPost extends Fragment {

    private RecyclerView recyclerView;
    private TextView tx;
    private DatabaseReference mDatabase;
    private RelativeLayout emptyState;
    public EventPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View itemView =  inflater.inflate(R.layout.fragment_event_post, container, false);
        recyclerView = (RecyclerView)itemView.findViewById(R.id.event_post_list);
        tx = (TextView)itemView.findViewById(R.id.txF);
        LinearLayoutManager Mlayout = new LinearLayoutManager(this.getActivity());
        emptyState = (RelativeLayout)itemView.findViewById(R.id.emptyState);
        Mlayout.setReverseLayout(true);
        Mlayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(Mlayout);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid1 = firebaseUser.getUid();
        DatabaseReference mBase = FirebaseDatabase.getInstance().getReference().child("userPost").child(uid1).child("eventPost");
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
    return itemView;
    }
    public void ubahData(){
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
            protected void onBindViewHolder(final EventPost.BlogViewHolder holder, int position, final event_blog model) {

                final String key = getRef(position).getKey();

                        holder.setNama(model.getNama());
                        holder.setLokasi(model.getLokasi());
                        holder.setTanggal(model.getTanggal());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(),key,Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity(),EventEdit.class);
                                intent.putExtra("key",key);
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
