package com.example.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatSenderAdapter extends RecyclerView.Adapter<ChatSenderAdapter.ViewHolder> {
    private List<MessageModel> dataSet;
    Context context;
    FirebaseAuth firebaseAuth;

    public ChatSenderAdapter(List<MessageModel> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.receiver_item, parent, false);
        ChatSenderAdapter.ViewHolder viewHolder = new ChatSenderAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MessageModel messageModel = dataSet.get(position);
        firebaseAuth = FirebaseAuth.getInstance();

        if (messageModel.getSender().equals(firebaseAuth.getCurrentUser().getUid())) {

            holder.senderMessage.setText(messageModel.getMessageText());

            String hour = messageModel.getTime().substring(8, 10);
            String minute = messageModel.getTime().substring(10, 12);

            holder.senderMessageTime.setText(hour + ":" + minute);

            holder.receiverCL.setVisibility(View.GONE);
            holder.profileImage.setVisibility(View.GONE);

        } else if (messageModel.getReceiver().equals(firebaseAuth.getCurrentUser().getUid())) {

            Picasso.get().load(messageModel.getReceiverImage()).into(holder.profileImage);
            holder.receiverMessage.setText(messageModel.getMessageText());
            holder.receiverMessageTime.setText(messageModel.getTime());
            holder.senderCL.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final TextView senderMessage;
        private final TextView receiverMessage;
        private final TextView senderMessageTime;
        private final TextView receiverMessageTime;
        private final ConstraintLayout senderCL;
        private final ConstraintLayout receiverCL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            senderMessage = itemView.findViewById(R.id.messageSenderTV);
            receiverMessage = itemView.findViewById(R.id.messageReceiverTV);
            senderMessageTime = itemView.findViewById(R.id.timeSenderTV);
            receiverMessageTime = itemView.findViewById(R.id.timeReceiverTV);
            senderCL = itemView.findViewById(R.id.messageSenderCL);
            receiverCL = itemView.findViewById(R.id.messageReceiverCL);

        }
    }
}
