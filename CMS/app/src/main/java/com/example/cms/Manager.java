package com.example.cms;

import static java.lang.Thread.sleep;

import org.javatuples.Sextet;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.widget.BaseAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Manager extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Map<String, String>> itemlist = new ArrayList<>();
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private View view;
    private Customlistviewadapter customlistview;
    private ListView listView;
    private ProgressBar progressBar;

    private Handler handler;
    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_manager, container, false);
        swipeRefreshLayout = view.findViewById(R.id.Swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar = view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                getMovieList();
            }
        });
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        getMovieList();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getMovieList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://35.195.14.2/api/")
                //.baseUrl("https://my-json-server.typicode.com/typicode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List <Post>> call = jsonPlaceHolderApi.getMoviesList();


        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    listView = (ListView) view.findViewById(R.id.dynamicList);
                    customlistview = new Customlistviewadapter(getContext(),itemlist);
                    List<Post> posts = response.body();
                    List<Map<String,String>> itemlist = new ArrayList<>();
                    //assert posts != null;
                    for (Post post : posts) {
                        Map<String, String> map = new HashMap<>();
                        map.put("MovieID",post.getMovieID());
                        map.put("MovieName",post.getMovieName());
                        map.put("MovieDuration",post.getMovieDuration());
                        map.put("MovieYear",post.getMovieYear());
                        map.put("MovieLow",post.getMovieLow());
                        map.put("MovieHigh",post.getMoviehigh());
                        //map.put("Moviefake", post.getfake());

                        itemlist.add(map);
                    }
                    customlistview = new Customlistviewadapter(getContext(),itemlist);
                    listView.setAdapter(customlistview);
                    merge(itemlist);
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    Toast.makeText(getContext(), "MAYBE : SERVER OFFLINE " + response.code() , Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getContext(), "FAILURE  ", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void merge(List<Map<String, String>> list) {
        itemlist.addAll(list);
        makelist();
    }

    public void makelist(){
        String size = "newmakelist " + itemlist.size() + " newmakelist ";
        progressBar.setVisibility(View.GONE);
    }
    public void showDeleteConfirmationDialog(final int fileId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // 'context' is the context passed to the adapter

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