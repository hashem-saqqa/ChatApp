package com.example.chatapp;

import android.content.Context;
import android.util.Log;
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

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MessageModel> dataSet;
    Context context;
    FirebaseAuth firebaseAuth;

    public ChatAdapter(List<MessageModel> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View receiverView = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_message_item, parent, false);
        View senderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_item, parent, false);

        if (viewType == 0) {

            return new SenderViewHolder(senderView);

        } else if (viewType == 1) {

            return new ReceiverViewHolder(receiverView);

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();

        if (dataSet.get(position).getSender().equals(firebaseAuth.getCurrentUser().getUid())) {
            return 0;
        } else if (dataSet.get(position).getReceiver().equals(firebaseAuth.getCurrentUser().getUid())) {
            return 1;
        }
        return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = dataSet.get(position);

        if (holder.getItemViewType() == 0) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;

            String hour = messageModel.getTime().substring(8, 10);
            String minute = messageModel.getTime().substring(10, 12);

            senderViewHolder.senderMessage.setText(messageModel.getMessageText());
            senderViewHolder.senderMessageTime.setText(hour + ":" + minute);

        } else if (holder.getItemViewType() == 1) {

            Log.d("TAGG", "onBindViewHolder: " + holder.getItemViewType());

            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;

            String hour = messageModel.getTime().substring(8, 10);
            String minute = messageModel.getTime().substring(10, 12);

            Picasso.get().load(messageModel.getReceiverImage()).into(receiverViewHolder.profileImage);
            receiverViewHolder.receiverMessage.setText(messageModel.getMessageText());
            receiverViewHolder.receiverMessageTime.setText(hour + ":" + minute);
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final TextView receiverMessage;
        private final TextView receiverMessageTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            receiverMessage = itemView.findViewById(R.id.messageReceiverTV);
            receiverMessageTime = itemView.findViewById(R.id.timeReceiverTV);

        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        private final TextView senderMessage;
        private final TextView senderMessageTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.messageSenderTV);
            senderMessageTime = itemView.findViewById(R.id.timeSenderTV);

        }
    }
}