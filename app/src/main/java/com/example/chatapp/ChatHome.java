package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
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
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        getSupportActionBar().hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        getTheRecents();

    }


    private void getTheRecents() {

        dataSet = new ArrayList<>();

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                        dataSet.add(new ChatHomeModel(
                                dataSnapshot.getKey(),
                                dataSnapshot.child("photo").getValue(String.class),
                                dataSnapshot.child("name").getValue(String.class)
                        ));
                    }

                }
                recyclerView = findViewById(R.id.chatHomeRV);
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                chatHomeAdapter = new ChatHomeAdapter(ChatHome.this, dataSet);
                recyclerView.setAdapter(chatHomeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void newMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        firebaseAuth.signOut();
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
    }
}