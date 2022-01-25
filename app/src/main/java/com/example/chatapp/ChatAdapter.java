package com.example.chatapp;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MessageModel> dataSet;
    Context context;
    FirebaseAuth firebaseAuth;
    int clickedPos = -1;

    public ChatAdapter(List<MessageModel> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View receiverView = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_message_item, parent, false);
        View senderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_item, parent, false);
        View receiverImageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_receiver_item, parent, false);
        View senderImageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_sender_item, parent, false);

        if (viewType == 0) {

            return new SenderViewHolder(senderView);

        } else if (viewType == 1) {

            return new ReceiverViewHolder(receiverView);

        } else if (viewType == 2) {

            return new SenderImageViewHolder(senderImageView);
        } else if (viewType == 3) {
            return new ReceiverImageViewHolder(receiverImageView);
        } else {
            return null;
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (dataSet.get(position).getSender().equals(firebaseAuth.getCurrentUser().getUid()) &
                !dataSet.get(position).getMessageText().equals("null")) {
            return 0;
        } else if (dataSet.get(position).getReceiver().equals(firebaseAuth.getCurrentUser().getUid()) &
                !dataSet.get(position).getMessageText().equals("null")) {
            return 1;
        } else if (dataSet.get(position).getSender().equals(firebaseAuth.getCurrentUser().getUid()) &
                !dataSet.get(position).getImageMessage().equals("null")) {
            return 2;
        } else if (dataSet.get(position).getReceiver().equals(firebaseAuth.getCurrentUser().getUid()) &
                !dataSet.get(position).getImageMessage().equals("null")) {
            return 3;
        }
        return 4;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel messageModel = dataSet.get(position);

        if (holder.getItemViewType() == 0) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;

            String globalTime = messageModel.getTime();

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

            senderViewHolder.senderMessage.setText(messageModel.getMessageText());
            senderViewHolder.senderMessageTime.setText(hour + ":" + minute);

            if (messageModel.isLastMsg() & messageModel.getStatus().equals("1")) {
                senderViewHolder.seenStatus.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickedPos = holder.getAdapterPosition();
                    return false;
                }
            });

        } else if (holder.getItemViewType() == 1) {


            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;

            String globalTime = messageModel.getTime();

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

            Picasso.get().load(messageModel.getReceiverImage()).networkPolicy(NetworkPolicy.OFFLINE).into(receiverViewHolder.profileImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(messageModel.getReceiverImage()).into(receiverViewHolder.profileImage);
                }
            });
            receiverViewHolder.receiverMessage.setText(messageModel.getMessageText());
            receiverViewHolder.receiverMessageTime.setText(hour + ":" + minute);

        } else if (holder.getItemViewType() == 2) {

            SenderImageViewHolder senderImageViewHolder = (SenderImageViewHolder) holder;

            String globalTime = messageModel.getTime();

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

            Picasso.get().load(messageModel.getImageMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(senderImageViewHolder.senderMessageImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(messageModel.getImageMessage()).into(senderImageViewHolder.senderMessageImage);

                }
            });
            senderImageViewHolder.senderMessageTime.setText(hour + ":" + minute);

            if (messageModel.isLastMsg() & messageModel.getStatus().equals("1")) {
                senderImageViewHolder.seenStatus.setVisibility(View.VISIBLE);
            }

        } else if (holder.getItemViewType() == 3) {

            ReceiverImageViewHolder receiverImageViewHolder = (ReceiverImageViewHolder) holder;

            String globalTime = messageModel.getTime();

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

            Picasso.get().load(messageModel.getReceiverImage()).networkPolicy(NetworkPolicy.OFFLINE).into(receiverImageViewHolder.profileImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(messageModel.getReceiverImage()).into(receiverImageViewHolder.profileImage);

                }
            });
            Picasso.get().load(messageModel.getImageMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(receiverImageViewHolder.receiverMessageImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(messageModel.getImageMessage()).into(receiverImageViewHolder.receiverMessageImage);
                }
            });
            Picasso.get().load(messageModel.getImageMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(receiverImageViewHolder.receiverMessageImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(messageModel.getImageMessage()).into(receiverImageViewHolder.receiverMessageImage);
                }
            });
            receiverImageViewHolder.receiverMessageTime.setText(hour + ":" + minute);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class ReceiverViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final CircleImageView profileImage;
        private final TextView receiverMessage;
        private final TextView receiverMessageTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            receiverMessage = itemView.findViewById(R.id.messageReceiverTV);
            receiverMessageTime = itemView.findViewById(R.id.timeReceiverTV);
            itemView.setOnCreateContextMenuListener(this);


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Copy = menu.add(0, v.getId(), 0, "Copy");
            Copy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "this is copy", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final TextView senderMessage;
        private final TextView senderMessageTime;
        private final TextView seenStatus;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessage = itemView.findViewById(R.id.messageSenderTV);
            senderMessageTime = itemView.findViewById(R.id.timeSenderTV);
            seenStatus = itemView.findViewById(R.id.seenStatus);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem Copy = menu.add(0, v.getId(), 0, "Copy");
            MenuItem Unsend = menu.add(0, v.getId(), 0, "Unsend");
            TextView test_data = v.findViewById(R.id.timeSenderTV);
            Copy.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "this is copy", Toast.LENGTH_SHORT).show();

                    Log.d("test1", "the time : " + dataSet.get(clickedPos).getTime());
                    Log.d("test1", "the text: " + dataSet.get(clickedPos).getMessageText());

                    return false;
                }
            });
            Unsend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "this is Unsend", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


        }
    }

    public class ReceiverImageViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final ImageView receiverMessageImage;
        private final TextView receiverMessageTime;


        public ReceiverImageViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            receiverMessageImage = itemView.findViewById(R.id.messageReceiverIV);
            receiverMessageTime = itemView.findViewById(R.id.timeReceiverTV);
        }
    }

    public class SenderImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final ImageView senderMessageImage;
        private final TextView senderMessageTime;
        private final TextView seenStatus;

        public SenderImageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageImage = itemView.findViewById(R.id.messageSenderIV);
            senderMessageTime = itemView.findViewById(R.id.timeSenderTV);
            seenStatus = itemView.findViewById(R.id.seenStatus);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Unsend = menu.add(0, v.getId(), 0, "Unsend");

            Unsend.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "this is Unsend", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }
}
