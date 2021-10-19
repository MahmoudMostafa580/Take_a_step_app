package com.example.takeastep.activities.user.network;

import com.example.takeastep.models.Covid19ReportItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Covid19Interface {

    @GET("all")
    public Call<Covid19ReportItem> getGeneralReport();

    @GET("countries/{countryName}?yesterday=true&twoDaysAgo=true&strict=true&allowNull=true")
    public Call<Covid19ReportItem> getOneCountryReport(@Path("countryName") String countryName);
}
