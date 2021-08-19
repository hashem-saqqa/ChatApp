package com.example.chatapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectRecent extends BottomSheetDialogFragment {

    View view;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    List<ChatHomeModel> dataSet;
    RecyclerView recyclerView;
    ChatHomeAdapter chatHomeAdapter;
    LinearLayoutManager linearLayoutManager;


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        view = View.inflate(getContext(), R.layout.select_recent, null);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        fillTheRecents();

    }

    private void fillTheRecents() {
        dataSet = new ArrayList<>();


        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                        databaseReference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    if (dataSnapshot1.child("receiver").getValue(String.class).equals(dataSnapshot.getKey()) &
                                            dataSnapshot1.child("sender").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid()) |
                                            dataSnapshot1.child("sender").getValue(String.class).equals(dataSnapshot.getKey()) &
                                                    dataSnapshot1.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())
                                    ) {

                                        dataSet.add(new ChatHomeModel(
                                                dataSnapshot.getKey(),
                                                dataSnapshot.child("photo").getValue(String.class),
                                                dataSnapshot.child("name").getValue(String.class)
                                        ));
                                        break;
                                    }
                                }
                                recyclerView = view.findViewById(R.id.imageRecentRV);
                                linearLayoutManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(linearLayoutManager);
                                chatHomeAdapter = new ChatHomeAdapter(getContext(), dataSet);
                                recyclerView.setAdapter(chatHomeAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public SelectRecent() {
    }

}
