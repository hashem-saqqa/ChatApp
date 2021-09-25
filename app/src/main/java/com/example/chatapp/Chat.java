package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    ApiService apiService;
    boolean notify = false;
    String currentPhotoPath;
    ValueEventListener valueEventListener;


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
                    voiceSendIcon.setImageResource(R.drawable.btn_send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        getTheMessages();

        updateToken();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);

    }

    private void updateToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                String token = task.getResult();

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokens");
                Token token1 = new Token(token);
                databaseReference.child(firebaseUser.getUid()).setValue(token1);
            }
        });
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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.chatapp.fileprovider",
                        photoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 & resultCode == RESULT_OK) {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
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
                        }
                    });
                }
            });

        }

    }

    private void createMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        notify = true;

        MessageModel message = new MessageModel(firebaseAuth.getCurrentUser().getUid()
                , userId, currentTime, messageET.getText().toString(), "0");

        databaseReference.child("messages").child(currentTime).setValue(message);

        String msg = message.getMessageText();
        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
                if (notify) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendNotification(userId, snapshot.child("name").getValue(String.class), msg);
                        }
                    }, 3000);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(String userId, String name, String msg) {
        databaseReference.child("messages").orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("receiver").getValue(String.class).equals(userId) &
                            dataSnapshot.child("sender").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid()) |
                            dataSnapshot.child("sender").getValue(String.class).equals(userId) &
                                    dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())
                    ) {
                        if (dataSnapshot.child("status").getValue(String.class).equals("0")) {

                            DatabaseReference tokens = databaseReference.child("tokens");
                            tokens.orderByKey().equalTo(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Token token = dataSnapshot.getValue(Token.class);
                                        Data data = new Data(firebaseAuth.getCurrentUser().getUid(), name + " : " + msg
                                                , "New Message", userId, R.mipmap.chat_app_logo);
                                        Sender sender = new Sender(data, token.getToken());

                                        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                            @Override
                                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                if (response.code() == 200) {
                                                    if (response.body().success != 1) {
                                                        Toast.makeText(Chat.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<MyResponse> call, Throwable t) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void createImageMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        notify = true;

        HashMap<String, Object> imageMessageData = new HashMap<>();
        imageMessageData.put("sender", firebaseAuth.getCurrentUser().getUid());
        imageMessageData.put("receiver", userId);
        imageMessageData.put("time", currentTime);
        imageMessageData.put("messageImage", photo);
        imageMessageData.put("status", "0");

        databaseReference.child("messages").child(currentTime).setValue(imageMessageData);

        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (notify) {
                    Log.e("TAGG", "onDataChange: working in the images");
                    sendNotification(userId, snapshot.child("name").getValue(String.class), "photo");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getTheMessages() {

        dataSet = new ArrayList<>();


        valueEventListener = new ValueEventListener() {
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

                            dataSet.add(new MessageModel(
                                    dataSnapshot.child("sender").getValue(String.class),
                                    dataSnapshot.child("receiver").getValue(String.class),
                                    dataSnapshot.child("time").getValue(String.class),
                                    dataSnapshot.child("messageText").getValue(String.class),
                                    receiverImage,
                                    "null",
                                    dataSnapshot.child("status").getValue(String.class)
                            ));
                            if (dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())) {
                                databaseReference.child("messages").child(dataSnapshot.getKey()).child("status").setValue("1");
                            }
                        } else if (dataSnapshot.child("messageImage").exists()) {


                            dataSet.add(new MessageModel(
                                    dataSnapshot.child("sender").getValue(String.class),
                                    dataSnapshot.child("receiver").getValue(String.class),
                                    dataSnapshot.child("time").getValue(String.class),
                                    "null",
                                    receiverImage,
                                    dataSnapshot.child("messageImage").getValue(String.class),
                                    dataSnapshot.child("status").getValue(String.class)
                            ));

                            if (dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())) {
                                databaseReference.child("messages").child(dataSnapshot.getKey()).child("status").setValue("1");
                            }
                        }
                    }
                }
                if (!dataSet.isEmpty()) {
                    dataSet.get(dataSet.size() - 1).setLastMsg(true);
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
        };
        databaseReference.child("messages").orderByChild("time").addValueEventListener(valueEventListener);

    }

    // edit to back to the chatHome and destroy
    public void BackButton(View view) {
        Intent intent = new Intent(this, ChatHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        databaseReference.child("messages").removeEventListener(valueEventListener);
        startActivity(intent);
//        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseReference.child("messages").removeEventListener(valueEventListener);
        this.finish();

    }
}