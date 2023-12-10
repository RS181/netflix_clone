package com.example.cms;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;

import com.google.android.exoplayer2.extractor.amr.AmrExtractor;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements ServerStatusListener {
    private TextView textViewResult;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    public Handler handler;
    public Runnable runnable;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView username = findViewById(R.id.username);
        TextView password = findViewById(R.id.password);

        Button loginbtn = (Button)  findViewById(R.id.buttonlogin);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button Reg = (Button) findViewById(R.id.buttonregistrar);


        // Create and execute the CheckServerTask
        CheckServerTask checkServerTask = new CheckServerTask(this);
        checkServerTask.execute();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkServerTask.execute();
                handler.postDelayed(this, 500);
            }
        };


        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Localintent = new Intent(getBaseContext(), AdminReg.class);
                startActivity(Localintent);

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://35.195.14.2/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        intent = new Intent(this, Options.class);
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        // Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start(intent);
                createPost(username.getText().toString() , password.getText().toString());
            }
        });
    }
    public void createPost(String user , String pass){
        Post post = new Post(user,pass);

        Call <Post> call = jsonPlaceHolderApi.createPost(post);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "LOGIN SUCESSFULL!!!",Toast.LENGTH_SHORT).show();
                    return;
                }

                Post postResponse = response.body();
                Toast.makeText(MainActivity.this, postResponse.getStatus(),Toast.LENGTH_SHORT).show();

                if("OK".equals(postResponse.getStatus())){
                    start(intent);
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }
    public void start(Intent intent){
        startActivity(intent);
    }

    @Override
    public void onServerStatusChanged(boolean isOnline) {
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (isOnline) {
            statusTextView.setText("Server is online");
        } else {
            statusTextView.setText("Server is offline");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}