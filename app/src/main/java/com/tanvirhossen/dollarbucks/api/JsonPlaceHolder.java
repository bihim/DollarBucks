package com.tanvirhossen.dollarbucks.api;

import com.tanvirhossen.dollarbucks.model.CountryModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolder {
    @GET("api")
    Call<CountryModel> getCountry();
}
