package it.benche.upofood.message;

import it.benche.upofood.notifications.MyResponse;
import it.benche.upofood.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAteK9Q5E:APA91bGBmkPUH-FWcmR04B_okKCx0n070trGxT-q2FU7eeT51a7Bvz-fV46XQMvz59hh6qMB_OXDg37XMTpWLuZGLkIQMWovImF_nsRuJM9QsU4CT4KH4qdgS_N1l1V0v0ArPZtmAhOx"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}