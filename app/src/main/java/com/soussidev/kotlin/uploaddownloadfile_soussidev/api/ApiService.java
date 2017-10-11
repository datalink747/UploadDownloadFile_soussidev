package com.soussidev.kotlin.uploaddownloadfile_soussidev.api;

import com.soussidev.kotlin.uploaddownloadfile_soussidev.api.response.Result;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by Soussi on 10/10/2017.
 */

public interface ApiService {

    @Multipart
    @POST("upload.php")
    Call<Result> uploadImage(@Part MultipartBody.Part file);

    @GET("{name}")
    @Streaming
    Call<ResponseBody> downloadFile(@Path("name") String name);
}
