package pwr.android_app.network.rest;

import java.util.List;

import pwr.android_app.dataStructures.ServiceData;
import pwr.android_app.dataStructures.SynchronizedData;
import pwr.android_app.dataStructures.UserData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @GET("/testjson")
    Call<UserData> doTestJson();

    @FormUrlEncoded
    @POST("/loginandroid")
    Call<UserData> login(@Field("email") String email, @Field("password") String password);

    @GET("/logoutandroid")
    Call<Void> logout(@Header("Cookie") String cookie);

    @GET("/servicesandroid")
    Call<List<ServiceData>> get_services(@Header("Cookie") String cookie);
}