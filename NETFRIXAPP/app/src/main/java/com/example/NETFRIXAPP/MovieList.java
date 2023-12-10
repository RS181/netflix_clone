package com.example.NETFRIXAPP;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieList extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Map<String, String>> itemlist = new ArrayList<>();
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private View view;
    private Customlistviewadapter customlistview;
    private ListView listView;
    private ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        swipeRefreshLayout = findViewById(R.id.SwipeAdmins);
        progressBar = findViewById(R.id.progressBarMovies);
        progressBar.setVisibility(View.VISIBLE);
        getMovieList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar = findViewById(R.id.progressBarMovies);
                progressBar.setVisibility(View.VISIBLE);
                getMovieList();
            }
        });
    }

    public void getMovieList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://35.195.14.2/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List <Post>> call = jsonPlaceHolderApi.getMoviesList();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> posts = response.body();
                    List<Map<String,String>> itemlist = new ArrayList<>();
                    assert posts != null;
                    for (Post post : posts) {
                        Map<String, String> map = new HashMap<>();
                        map.put("MovieID",post.getMovieID());
                        map.put("MovieName",post.getMovieName());
                        map.put("MovieDuration",post.getMovieDuration());
                        map.put("MovieYear",post.getMovieYear());
                        map.put("MovieLow",post.getMovieLow());
                        map.put("MovieHigh",post.getMoviehigh());
                        itemlist.add(map);
                    }
                    listView = findViewById(R.id.dynamicList);
                    customlistview = new Customlistviewadapter(getBaseContext(),itemlist);
                    listView.setAdapter(customlistview);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getBaseContext(), "MAYBE : SERVER OFFLINE " + response.code() , Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getBaseContext(), "FAILURE  ", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void showDeleteConfirmationDialog(final int fileId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()); // 'context' is the context passed to the adapter

        builder.setTitle("Delete File")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked Yes button. Remove the file.
                        //removeFileById(fileId);

                        // Notify the adapter that the data set has changed
                        customlistview.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No button. Do nothing.
                    }
                })
                .show();
    }
}