package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    TextView newMessageTV,noRecents;
    ConstraintLayout searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_home);

        getSupportActionBar().hide();
        ImageView chatIcon = findViewById(R.id.chatIcon);
        chatIcon.setColorFilter(Color.parseColor("#007EF4"));
        chatIcon.setEnabled(false);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setVisibility(View.INVISIBLE);
        searchET = findViewById(R.id.search_chat_homeET);
        searchIcon = findViewById(R.id.searchIcon);
        newMessageIcon = findViewById(R.id.newMessageIcon);
        newMessageTV = findViewById(R.id.newMsgText);
        noRecents = findViewById(R.id.noRecents);
        noRecents.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = s.toString();
                searchDataSet = new ArrayList<>();

                for (int i =0 ; i<dataSet.size(); i++) {
                    if (dataSet.get(i).getUserName().startsWith(name.toLowerCase()) | dataSet.get(i).getUserName().startsWith(name.toUpperCase())){
                        searchDataSet.add(new ChatHomeModel(
                                dataSet.get(i).getUserId(),
                                dataSet.get(i).getProfileImage(),
                                dataSet.get(i).getUserName()
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
            public void afterTextChanged(Editable s) {

            }
        });

        getTheRecent();
        if (dataSet.isEmpty()){
            noRecents.setVisibility(View.VISIBLE);
        }
    }


    private void getTheRecent() {

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void newMessage(View view) {
        Intent intent = new Intent(getApplicationContext(), NewMessage.class);
        startActivity(intent);
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

    public void GoToProfile(View view) {
        Intent intent = new Intent(getApplicationContext(), Profile.class);
        startActivity(intent);
    }

    public void GoToCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 & resultCode == RESULT_OK & data != null){
            SelectRecent selectRecent = new SelectRecent();
            selectRecent.show(getSupportFragmentManager(),"SelectRecent");
        }
    }
}