package com.example.secureout;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("doc/id/{Doc_num}")
    Call<List<Model_Doc>> getDocById(@Path("Doc_num") String doc_num);

    @Multipart
    @POST("doc/upload/draw1")
    Call<ResponseBody> uploadSig1(
            @Part MultipartBody.Part file1,
            @Part("doc_num") RequestBody doc_num);

    @Multipart
    @POST("doc/upload/draw2")
    Call<ResponseBody> uploadSig2(
            @Part MultipartBody.Part file1,
            @Part("doc_num") RequestBody doc_num);

    @Multipart
    @POST("doc/upload/draw3")
    Call<ResponseBody> uploadSig3(
            @Part MultipartBody.Part file1,
            @Part("doc_num") RequestBody doc_num);

    @Multipart
    @POST("doc/uploads")
    Call<ResponseBody> uploadPicture(
            @Part MultipartBody.Part file1,
            @Part MultipartBody.Part file2,
            @Part MultipartBody.Part file3,
            @Part MultipartBody.Part file4,
            @Part MultipartBody.Part file5,
            @Part("doc_num") RequestBody doc_num);
}
