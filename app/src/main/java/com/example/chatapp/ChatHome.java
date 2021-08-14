package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatHome extends AppCompatActivity {
    List<ChatHomeModel> dataSet;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ChatHomeAdapter chatHomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        getSupportActionBar().hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        getTheRecents();

    }

    private void getTheRecents() {
        dataSet = new ArrayList<>();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    dataSet.add(new ChatHomeModel(
                            dataSnapshot.child("photo").getValue(String.class),
                            dataSnapshot.child("name").getValue(String.class),
                            "9:00 pm",
                            "whatever"
                    ));
                }
                recyclerView = findViewById(R.id.chatHomeRV);
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                chatHomeAdapter = new ChatHomeAdapter(dataSet);
                recyclerView.setAdapter(chatHomeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}