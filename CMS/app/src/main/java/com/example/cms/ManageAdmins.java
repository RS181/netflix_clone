package com.example.cms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageAdmins extends AppCompatActivity {


    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Map<String, String>> itemlist = new ArrayList<>();
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private View view;
    private CustomListViewAdmins customlistview;
    private ListView listView;
    private ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admins);
        swipeRefreshLayout = findViewById(R.id.SwipeAdmins);
        progressBar = findViewById(R.id.progressBarAdmins);
        progressBar.setVisibility(View.VISIBLE);
        getAdminsList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar = findViewById(R.id.progressBarAdmins);
                progressBar.setVisibility(View.VISIBLE);
                getAdminsList();
            }
        });
    }

    public void getAdminsList(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://35.195.14.2/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List <Post>> call = jsonPlaceHolderApi.getAdminsList();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> posts = response.body();
                    //Toast.makeText(getBaseContext(), response.body().get(0).getLogin(),Toast.LENGTH_SHORT).show();

                    List<Map<String,String>> itemlist = new ArrayList<>();
                    for (Post post : posts) {
                        Map<String, String> map = new HashMap<>();
                        map.put("Admin",post.getLogin());
                        map.put("Password",post.getPassword());
                        itemlist.add(map);
                        //Toast.makeText(getBaseContext(), post.getLogin() +" // " +post.getPassword(),Toast.LENGTH_SHORT).show();
                    }
                    listView = (ListView) findViewById(R.id.dynamicList);
                    customlistview = new CustomListViewAdmins(getBaseContext(),itemlist);
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
}