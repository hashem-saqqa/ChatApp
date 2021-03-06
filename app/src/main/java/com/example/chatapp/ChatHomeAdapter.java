package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHomeAdapter extends RecyclerView.Adapter<ChatHomeAdapter.ViewHolder> {
    private List<ChatHomeModel> DataSet;
    Context context;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String lastMsg, lastMsgTime;


    public ChatHomeAdapter(Context context, List<ChatHomeModel> dataSet) {
        this.context = context;
        DataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.chat_home_item, parent, false);

        ViewHolder ViewHolder = new ViewHolder(rootView);

        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatHomeModel chatHomeModel = DataSet.get(position);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        holder.readStatus.setVisibility(View.GONE);

        if (!DataSet.isEmpty()) {
            databaseReference.child("messages").orderByChild("time").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        if (dataSnapshot.child("receiver").getValue(String.class).equals(chatHomeModel.getUserId()) &
                                dataSnapshot.child("sender").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid()) |
                                dataSnapshot.child("sender").getValue(String.class).equals(chatHomeModel.getUserId()) &
                                        dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())
                        ) {
                            lastMsg = dataSnapshot.child("messageText").getValue(String.class);
                            lastMsgTime = dataSnapshot.child("time").getValue(String.class);

                            if (dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())
                                    & dataSnapshot.child("status").getValue(String.class).equals("0")
                            ) {

                                holder.readStatus.setVisibility(View.VISIBLE);

                            } else if (dataSnapshot.child("receiver").getValue(String.class).equals(firebaseAuth.getCurrentUser().getUid())
                                    & dataSnapshot.child("status").getValue(String.class).equals("1")
                            ) {

                                holder.readStatus.setVisibility(View.INVISIBLE);

                            }
                        }
                    }

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
                    holder.lastMsg.setText(lastMsg);

                    String globalTime = lastMsgTime;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date date = null;
                    try {
                        date = sdf.parse(globalTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    sdf.setTimeZone(TimeZone.getDefault());
                    String currentTime = sdf.format(date);


                    String hour = currentTime.substring(8, 10);
                    String minute = currentTime.substring(10, 12);

                    holder.time.setText(hour + ":" + minute);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("userId", chatHomeModel.getUserId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final TextView userName;
        private final TextView lastMsg;
        private final TextView time;
        private final ImageView readStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            time = itemView.findViewById(R.id.time);
            readStatus = itemView.findViewById(R.id.readStatus);

        }

    }
}
