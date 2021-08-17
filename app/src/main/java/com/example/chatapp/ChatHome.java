package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatHome extends AppCompatActivity {
    List<ChatHomeModel> dataSet;
    List<ChatHomeModel> searchDataSet;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ChatHomeAdapter chatHomeAdapter;
    FirebaseAuth firebaseAuth;
    EditText searchET;
    ImageView searchIcon, newMessageIcon;
    TextView newMessageTV;
    ConstraintLayout searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        getSupportActionBar().hide();
        ImageView chatIcon = findViewById(R.id.chatIcon);
        chatIcon.setColorFilter(Color.parseColor("#007EF4"));


        searchBar = findViewById(R.id.searchBar);
        searchBar.setVisibility(View.INVISIBLE);
        searchET = findViewById(R.id.search_chat_homeET);
        searchIcon = findViewById(R.id.searchIcon);
        newMessageIcon = findViewById(R.id.newMessageIcon);
        newMessageTV = findViewById(R.id.newMsgText);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString().toLowerCase();
                searchDataSet = new ArrayList<>();

                databaseReference.child("users").orderByChild("name").startAt(name).endAt(name+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (!dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                searchDataSet.add(new ChatHomeModel(
                                        dataSnapshot.getKey(),
                                        dataSnapshot.child("photo").getValue(String.class),
                                        dataSnapshot.child("name").getValue(String.class)
                                ));
                            }

                        }
                        recyclerView = findViewById(R.id.chatHomeRV);
                        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        chatHomeAdapter = new ChatHomeAdapter(ChatHome.this, searchDataSet);
                        recyclerView.setAdapter(chatHomeAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getTheRecent();

    }


    private void getTheRecent() {

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
        Intent intent = new Intent(getApplicationContext(), NewMessage.class);
        startActivity(intent);
//        Intent intent = new Intent(getApplicationContext(), Login.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        firebaseAuth.signOut();
//        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
    }

    public void searchChatHome(View view) {

        searchBar.setVisibility(View.VISIBLE);

        searchIcon.setVisibility(View.INVISIBLE);
        newMessageIcon.setVisibility(View.INVISIBLE);
        newMessageTV.setVisibility(View.INVISIBLE);

    }

    public void HideSearchBar(View view) {

        searchBar.setVisibility(View.GONE);

        searchIcon.setVisibility(View.VISIBLE);
        newMessageIcon.setVisibility(View.VISIBLE);
        newMessageTV.setVisibility(View.VISIBLE);

        getTheRecent();

    }
}