package com.example.takeastep.activities.user.network;

import com.example.takeastep.models.Covid19ReportItem;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ApiClient {

    private static final String BASE_URL = "https://disease.sh/v3/covid-19/";
    private Covid19Interface covid19Interface;
    private static ApiClient INSTANCE;


    public ApiClient() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        covid19Interface = retrofit.create(Covid19Interface.class);

    }

    public static ApiClient getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ApiClient();
        }
        return INSTANCE;
    }


    public Call<Covid19ReportItem> getGeneralReport(){
        return covid19Interface.getGeneralReport();
    }


    public Call<Covid19ReportItem> getOneCountryReport(String countryName){
        return covid19Interface.getOneCountryReport(countryName);
    }

}
