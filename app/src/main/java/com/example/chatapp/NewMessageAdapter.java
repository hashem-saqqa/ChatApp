package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewMessageAdapter extends RecyclerView.Adapter<NewMessageAdapter.ViewHolder> {
    private List<ChatHomeModel> DataSet;
    Context context;
    Intent data;
    StorageReference storageReference;
    String photo,userId;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;




    public NewMessageAdapter(Context context, List<ChatHomeModel> dataSet) {
        this.context = context;
        DataSet = dataSet;
    }

    public NewMessageAdapter(Context context, List<ChatHomeModel> dataSet, Intent data) {
        this.context = context;
        DataSet = dataSet;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View rootView = inflater.inflate(R.layout.new_message_item, parent, false);

        ViewHolder ViewHolder = new ViewHolder(rootView);

        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatHomeModel chatHomeModel = DataSet.get(position);

        Picasso.get().load(chatHomeModel.getProfileImage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.profileImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(chatHomeModel.getProfileImage()).into(holder.profileImage);

            }
        });
        holder.userName.setText(chatHomeModel.getUserName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data==null){
                    Intent intent = new Intent(context, Chat.class);
                    intent.putExtra("userId", chatHomeModel.getUserId());
                    context.startActivity(intent);
                }else {
                    userId = chatHomeModel.getUserId();
                    Log.d("TAGff1", "onClick: ");
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
                                    Intent intent = new Intent(context, Chat.class);
                                    intent.putExtra("userId", chatHomeModel.getUserId());
                                    context.startActivity(intent);

                                }
                            });
                        }
                    });
                }
            }
        });


    }
    private void createImageMessage() {
        Log.d("TAGff2", "onClick: ");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> imageMessageData = new HashMap<>();
        imageMessageData.put("sender", firebaseAuth.getCurrentUser().getUid());
        imageMessageData.put("receiver", userId);
        imageMessageData.put("time", currentTime);
        imageMessageData.put("messageImage", photo);

        databaseReference.child("messages").child(currentTime).setValue(imageMessageData);
        Log.d("TAGff3", "onClick: ");



    }


    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final TextView userName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);

        }

    }
}
