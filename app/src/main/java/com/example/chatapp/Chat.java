package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Chat extends AppCompatActivity {
    String userId;
    DatabaseReference databaseReference;
    TextView receiverName, receiverStatus;
    EditText messageET;
    ImageView voiceSendIcon;
    ImageView cameraIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getExtras().getString("userId");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        receiverName = findViewById(R.id.receiverName);
        receiverStatus = findViewById(R.id.receiverStatus);
        messageET = findViewById(R.id.messageET);
        voiceSendIcon = findViewById(R.id.voiceBtn);
        cameraIcon = findViewById(R.id.cameraBtn);

        fillTheUserData();

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    voiceSendIcon.setImageResource(R.drawable.send_icon);
                    cameraIcon.setVisibility(View.GONE);
                } else if (count == 0) {
                    voiceSendIcon.setImageResource(R.drawable.btn_send2);
                    cameraIcon.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }

    private void fillTheUserData() {
        databaseReference.child("users").orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    receiverName.setText(dataSnapshot.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendVoiceOrMsg(View view) {
        if (!messageET.getText().toString().equals("")) {
            createMessage();
        } else {

        }
    }


    public void sendImage(View view) {
    }

    private void createMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        Log.d("TAGgg", "createMessage: " + currentTime);
        databaseReference.child("messages").child(currentTime);


    }
}