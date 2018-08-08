package com.ridho.shareblood.Remote;

import com.ridho.shareblood.Model.MyResponse;
import com.ridho.shareblood.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvNlYIss:APA91bGHCKxpWr589V5y79_c3llJwl0bBzKJNRQZf8PeikC3Ue-o2X47jHfx8Bfxs_k9sEUJ0S4lSNJM6I5tGzFARJhloDv7pM5H0pPLuKsC1vD9sCfvFZLpFOkCM5UrVuMUo0_T7M_o"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
