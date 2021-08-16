package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHomeAdapter extends RecyclerView.Adapter<ChatHomeAdapter.senderViewHolder> {
    private List<ChatHomeModel> DataSet;
    Context context;
//    private OnUserListener onUserListener;

    public ChatHomeAdapter(Context context, List<ChatHomeModel> dataSet) {
        this.context = context;
        DataSet = dataSet;
    }

    @NonNull
    @Override
    public senderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.chat_home_item, parent, false);

        senderViewHolder senderViewHolder = new senderViewHolder(rootView);

        return senderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull senderViewHolder holder, int position) {

        ChatHomeModel chatHomeModel = DataSet.get(position);

        Picasso.get().load(chatHomeModel.getProfileImage()).into(holder.profileImage);
        holder.userName.setText(chatHomeModel.getUserName());
        holder.time.setText(chatHomeModel.getTime());
        holder.lastMsg.setText(chatHomeModel.getLastMsg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Chat.class);
                intent.putExtra("userId",chatHomeModel.getUserId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return DataSet.size();
    }

    public class senderViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final TextView userName;
        private final TextView lastMsg;
        private final TextView time;

//        OnUserListener onUserListener;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);

//            this.onUserListener = onUserListener;
//            itemView.setOnClickListener(this);

            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            time = itemView.findViewById(R.id.time);

        }

    }
}
