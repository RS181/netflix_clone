package com.example.NETFRIXAPP;

import java.util.List;
import java.util.Observable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface JsonPlaceHolderApi {

    @POST("connect/signup/admin")
    Call<Post> NewAdminUser(@Body Post post);

    @POST("connect/signup/usr")
    Call<Post> NewNormalUser(@Body Post post);
    @GET("connect/all/admin")
    Call<List<Post>> getAdminsList();

    @GET("connect/all/usr")
    Call<List<Post>> getClientsList();

    @POST("connect/remove/admin")
    Call<Post> removeAdmin(@Body RequestBody post);

    @POST("connect/remove/usr")
    Call<Post> removeClients(@Body RequestBody post);

    @POST("connect/login/usr")
    Call<Post> createPost(@Body Post post);

    @GET("connect/all/usr")
    Call<List<Post>> getPosts();

    @POST("movie/remove")
    Call<Post> removeMovie(@Body RequestBody post);

    @GET("movie/all")
    Call<List<Post>> getMoviesList();

    @Multipart
    @POST("upload/movie")
    Call<Post> MoviePost(@Part MultipartBody.Part file,
                         @Part("moviename") RequestBody moviename,
                         @Part("duration") RequestBody duration,
                         @Part("year") RequestBody year); // mp4

   /* @POST("upload/movie")
    Call<Post> MoviePost(@Body RequestBody file); // mp4*/

    @GET("demo/posts")
    Call<List<Post>> getfake();

}
