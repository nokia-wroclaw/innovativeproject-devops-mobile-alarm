package pwr.android_app.network.rest;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pwr.android_app.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    /* ========================================== DATA ========================================== */

    private static final String BASE_URL =
            "https://devops-nokia.herokuapp.com";

    private static HttpLoggingInterceptor loggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder okHttpClientBuilder =
            new OkHttpClient.Builder().addInterceptor(loggingInterceptor);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClientBuilder.build());   // ToDo: delete this line before release the app :)

    private static Retrofit retrofit =
            builder.build();

    /* ========================================= METHODS ======================================== */

    public static <S> S createService(Class<S> serviceClass) {

        return retrofit.create(serviceClass);
    }

    /* ========================================================================================== */
}
