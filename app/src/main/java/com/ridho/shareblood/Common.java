package com.ridho.shareblood;

import com.ridho.shareblood.Remote.APIService;
import com.ridho.shareblood.Remote.RetrofitClient;

public class Common {
    public static String currentToken = "";

    private static String baseURL = "https://fcm.googleapis.com/";

    public static APIService getFCMClient(){
        return RetrofitClient.getClient(baseURL).create(APIService.class);
    }
}
