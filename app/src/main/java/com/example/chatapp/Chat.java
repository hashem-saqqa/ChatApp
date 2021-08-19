package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Chat extends AppCompatActivity {
    String userId, receiverImage, photo;
    DatabaseReference databaseReference;
    TextView receiverName;
    EditText messageET;
    ImageView voiceSendIcon;
    FirebaseAuth firebaseAuth;
    List<MessageModel> dataSet;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getExtras().getString("userId");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        receiverName = findViewById(R.id.receiverName);
        messageET = findViewById(R.id.messageET);
        voiceSendIcon = findViewById(R.id.voiceBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        fillTheUserData();

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    voiceSendIcon.setImageResource(R.drawable.send_icon);
                } else if (count == 0) {
                    voiceSendIcon.setImageResource(R.drawable.btn_send2);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        getTheMessages();
    }


    private void fillTheUserData() {
        databaseReference.child("users").orderByKey().equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    receiverName.setText(dataSnapshot.child("name").getValue(String.class));
                    receiverImage = dataSnapshot.child("photo").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendImageOrMsg(View view) {
        if (!messageET.getText().toString().equals("")) {
            createMessage();
            messageET.getText().clear();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 & resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            } else {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String currentTime = sdf.format(new Date());

                storageReference = FirebaseStorage.getInstance().getReference(currentTime);
                storageReference.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                photo = uri.toString();
                                createImageMessage();
                                getTheMessages();
                            }
                        });
                    }
                });

            }
        }

    }

    private void createMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());


        MessageModel message = new MessageModel(firebaseAuth.getCurrentUser().getUid()
                , userId, currentTime, messageET.getText().toString());

        databaseReference.child("messages").child(currentTime).setValue(message);


    }

    private void createImageMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        HashMap<String, Object> imageMessageData = new HashMap<>();
        imageMessageData.put("sender", firebaseAuth.getCurrentUser().getUid());
        imageMessageData.put("receiver", userId);
        imageMessageData.put("time", currentTime);
        imageMessageData.put("messageImage", photo);

        databaseReference.child("messages").child(currentTime).setValue(imageMessageData);


    }

    private void getTheMessages() {

        dataSet = new ArrayList<>();

        databaseReference.child("messages").orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataSet.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    if (dataSnapshot.child("receiver").getValue(String.class).equals(userId) &
                            dataSnapshot.child("sender").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid()) |
                            dataSnapshot.child("sender").getValue(String.class).equals(userId) &
                                    dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())
                    ) {
                        if (dataSnapshot.child("messageText").exists()) {
                            Log.d("theReceiverText", "onDataChange: " + dataSnapshot.child("receiver").getValue(String.class));

                            dataSet.add(new MessageModel(
                                    dataSnapshot.child("sender").getValue(String.class),
                                    dataSnapshot.child("receiver").getValue(String.class),
                                    dataSnapshot.child("time").getValue(String.class),
                                    dataSnapshot.child("messageText").getValue(String.class),
                                    receiverImage,
                                    "null"));
                        } else if (dataSnapshot.child("messageImage").exists()) {

                            Log.d("theReceiverImage", "onDataChange: " + dataSnapshot.child("receiver").getValue(String.class));

                            dataSet.add(new MessageModel(
                                    dataSnapshot.child("sender").getValue(String.class),
                                    dataSnapshot.child("receiver").getValue(String.class),
                                    dataSnapshot.child("time").getValue(String.class),
                                    "null",
                                    receiverImage,
                                    dataSnapshot.child("messageImage").getValue(String.class)
                            ));
                        }

                    }
                }

                recyclerView = findViewById(R.id.messagesRV);
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new ChatAdapter(dataSet, Chat.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void BackButton(View view) {
        finish();
    }
}