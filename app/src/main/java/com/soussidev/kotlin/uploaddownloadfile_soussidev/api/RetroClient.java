package com.soussidev.kotlin.uploaddownloadfile_soussidev.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Soussi on 10/10/2017.
 */

public class RetroClient {

    private static final String ROOT_URL_UPLOAD = "http://192.168.0.50:8080/php_upload/";
    private static final String ROOT_URL_Download = "http://10.0.2.2:8080/php_upload/";

    public RetroClient() {

    }

    /**
     *@Info Get Retro Client
     *@author soussi
     * @return JSON Object
     */
    private static Retrofit getRetroClient() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL_UPLOAD)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiService getApiService() {
        return getRetroClient().create(ApiService.class);
    }

    /**
     *@Info for Download
     *@author soussi
     * @return JSON Object
     */
    private static Retrofit getRetroClientDownload() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL_Download)
                //.addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiService getApiServiceDownload() {
        return getRetroClientDownload().create(ApiService.class);
    }
}
