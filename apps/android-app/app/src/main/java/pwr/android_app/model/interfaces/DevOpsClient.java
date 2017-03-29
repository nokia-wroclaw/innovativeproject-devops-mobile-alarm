package pwr.android_app.model.interfaces;

import pwr.android_app.model.dataStructures.UserData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DevOpsClient {

    @GET("/testjson")
    Call<UserData> doTestJson();

    @FormUrlEncoded
    @POST("/loginandroid")
    Call<UserData> loginToApp(@Field("email") String email, @Field("password") String password);

}
