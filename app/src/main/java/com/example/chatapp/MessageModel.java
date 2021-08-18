package com.example.chatapp;

public class MessageModel {
    private String msgId;
    private String sender;
    private String receiver;
    private String time;
    private String messageText;
    private String receiverImage;
    private String imageMessage;

    public MessageModel() {
    }

    public MessageModel(String sender, String receiver, String time, String messageText, String receiverImage, String imageMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.messageText = messageText;
        this.receiverImage = receiverImage;
        this.imageMessage = imageMessage;
    }

    public MessageModel(String sender, String receiver, String time, String messageText, String receiverImage) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.messageText = messageText;
        this.receiverImage = receiverImage;
    }

    public MessageModel(String sender, String receiver, String time, String messageText) {
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.messageText = messageText;
    }


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getImageMessage() {
        return imageMessage;
    }

    public void setImageMessage(String imageMessage) {
        this.imageMessage = imageMessage;
    }
}
