package com.example.chatapp;

import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAgJdN8MY:APA91bHWVrucwYo09B90gxLWrdQtf6Prr2ZfdivhftWgqz_LmwOSkXYEuyJzavWSpmKaYFPNABZrnXYuX-fYELZxVVqdXedCH9l5u2QPG08cN3Ir3fmjtNpIr4O9foTN9l3j9IIO_sVs"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
