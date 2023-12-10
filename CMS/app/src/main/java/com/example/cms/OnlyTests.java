package com.example.cms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnlyTests extends AppCompatActivity {


    private List<Map<String,String>> itemlist;

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_tests);

        getMovieList();
        Toast.makeText(OnlyTests.this, "SUCESSFULL!!!",Toast.LENGTH_SHORT).show();

    }

    public void getMovieList() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://my-json-server.typicode.com/typicode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<Post>> call = jsonPlaceHolderApi.getfake();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                List<Post> posts = response.body();
                //TextView textView = findViewById(R.id.textfortest);
                int i = 0;
                for (Post post : posts) {
                    /*Toast.makeText(OnlyTests.this, "Chega aqui", Toast.LENGTH_SHORT).show();
                    Map<String, String> map = new HashMap<>();

                    map.put("MovieID", post.getMovieID());
                    map.put("MovieName", post.getMovieName());
                    map.put("MovieDuration", post.getMovieDuration());
                    map.put("MovieYear", post.getMovieYear());
                    map.put("MovieLow", post.getMovieLow());
                    map.put("MovieHigh", post.getMoviehigh());
                    itemlist.add(map);

                    textView.setText(map.get("MovieName"));
                    i++;*/
                    //Toast.makeText(OnlyTests.this, post.getfake(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });
    }
}