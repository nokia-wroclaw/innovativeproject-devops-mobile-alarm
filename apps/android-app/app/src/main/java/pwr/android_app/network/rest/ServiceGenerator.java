package pwr.android_app.network.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    /* ========================================== DATA ========================================== */

    private static final String BASE_URL =
            "https://devops-nokia.herokuapp.com";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit =
            builder.build();

    /* ========================================= METHODS ======================================== */

    public static <S> S createService(Class<S> serviceClass) {

        return retrofit.create(serviceClass);
    }

    /* ========================================================================================== */
}
