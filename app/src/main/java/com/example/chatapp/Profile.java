package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    List<ChatHomeModel> dataSet;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    CircleImageView profileImage;
    TextView userNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();
        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setColorFilter(Color.parseColor("#007EF4"));
        profileIcon.setEnabled(false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        profileImage = findViewById(R.id.profileImage);
        userNameTV = findViewById(R.id.NameTV);

        fillTheProfile();

        fillTheUser();
    }

    private void fillTheProfile() {
        databaseReference.child("users").orderByKey().equalTo(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Picasso.get().load(dataSnapshot.child("photo").getValue(String.class)).networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(dataSnapshot.child("photo").getValue(String.class)).into(profileImage);

                                }
                            });
                            userNameTV.setText(dataSnapshot.child("name").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fillTheUser() {
        dataSet = new ArrayList<>();

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataSet.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                        dataSet.add(new ChatHomeModel(
                                dataSnapshot.getKey(),
                                dataSnapshot.child("photo").getValue(String.class),
                                dataSnapshot.child("name").getValue(String.class),
                                dataSnapshot.child("phone").getValue(String.class)

                        ));
                    }

                }
                recyclerView = findViewById(R.id.profileRV);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(new ProfileAdapter(dataSet, Profile.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void GoToHome(View view) {
        Intent intent = new Intent(getApplicationContext(), ChatHome.class);
        startActivity(intent);
    }

    public void LogOut(View view) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        firebaseAuth.signOut();
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
    }

    public void GoToCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 & resultCode == RESULT_OK & data != null) {
            SelectRecent selectRecent = new SelectRecent(data);
            selectRecent.show(getSupportFragmentManager(), "SelectRecent");
        }
    }
}