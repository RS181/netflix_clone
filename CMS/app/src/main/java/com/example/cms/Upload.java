package com.example.cms;

import static android.widget.Toast.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.http.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class Upload extends Fragment {
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private ImageView image;

    private String path;
    private String Ano;

    private String MovieName;
    private String Duracao;

    private Uri videoUri;

    private View rootView;

    public EditText MName;

    private ProgressBar progressBar;

    private static final int PICK_VIDEO_REQUEST = 1;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent,PICK_VIDEO_REQUEST);
    }
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        progressBar = rootView.findViewById(R.id.Carregando);
        image = rootView.findViewById(R.id.insert_file);
        Button open = rootView.findViewById(R.id.Abrir_Ficheiros);
        Button upload = rootView.findViewById(R.id.Carregar);
        MName = rootView.findViewById(R.id.MovieName);
        EditText Year = rootView.findViewById(R.id.Year);
        EditText Duration = rootView.findViewById(R.id.Duration);
        TextView temp2 = rootView.findViewById(R.id.temp2);
        TextView temp1 = rootView.findViewById(R.id.temp1);

            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
                        openGallery();
                    }
                    Year.setVisibility(View.VISIBLE);
                    Duration.setVisibility(View.VISIBLE);
                    MName.setVisibility(View.VISIBLE);
                }
            });
            upload.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    Ano = Year.getText().toString();
                    Duracao = Duration.getText().toString();
                    MovieName = MName.getText().toString().replace(" ","");
                    temp1.setText(videoUri.toString());
                    path = RealPath.getRealPath(getContext(), videoUri);
                    temp2.setText(path);
                    if(videoUri!= null && Ano != null && Duracao != null && MovieName.matches("[a-zA-Z]+")){
                        File videofile = new File(path);
                        UploadMovie(videofile);
                        makeText(getContext(),"VIDEO, YEAR OR DURATION ARE Correct!!!" + videofile.getName(), LENGTH_SHORT).show();
                    } else {
                        makeText(getContext(),"VIDEO, YEAR OR DURATION ARE INAVLID!!!", LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            videoUri = data.getData();
            makeText(getContext(), "URi not null", LENGTH_SHORT).show();

            Bitmap videoThumbnail = null;
            try {
                videoThumbnail = extractFrameFromVideo(videoUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            image.setImageBitmap(videoThumbnail);
        }
    }
    private Bitmap extractFrameFromVideo(Uri videoUri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(getContext(), videoUri);
            return retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return null;
    }

    public void UploadMovie(File videofile){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://35.195.14.2/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<List<Post>> callMovieList = jsonPlaceHolderApi.getMoviesList();

        callMovieList.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()) {
                    List<Post> posts = response.body();
                    assert posts != null;
                    for (Post post : posts) {
                        String compare = post.getMovieName();
                        TextView temp2 = rootView.findViewById(R.id.temp2);
                        TextView temp1 = rootView.findViewById(R.id.temp1);
                        temp1.setText(compare);
                        temp2.setText(videofile.getName());
                        Toast.makeText(getContext(),"Entra || " + compare.equals(MovieName) + " || aqui", LENGTH_LONG).show();

                        if (compare.equals(MovieName)){
                            Toast.makeText(getContext(),"SAME MOVIE NAME, CHANGE NAME !!!", LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            return;
                        }
                    }
                        RequestBody file = RequestBody.create(MediaType.parse("video/mp4"), videofile);
                        MultipartBody.Part bodyfile = MultipartBody.Part.createFormData("file", MovieName, file);

                        RequestBody moviename = RequestBody.create(MediaType.parse("text/plain"), MovieName);
                        RequestBody duration = RequestBody.create(MediaType.parse("text/plain"),Duracao);
                        RequestBody year = RequestBody.create(MediaType.parse("text/plain"),Ano);

                        Call<Post> newcall = jsonPlaceHolderApi.MoviePost(bodyfile, moviename, duration, year);
                        newcall.enqueue(new Callback<Post>() {
                            Response<Post> response;
                            @Override
                            public void onResponse(Call<Post> call, Response<Post> response) {
                                this.response = response;
                                if (response.isSuccessful()){
                                    Post postResponse = response.body();
                                    makeText(getContext(), "Uploaded" + postResponse.getStatus(), LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onFailure(Call<Post> call, Throwable t) {
                               // TextView aux = rootView.findViewById(R.id.temp2);
//                                aux.setText(t.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                }else{
                    Toast.makeText(getContext(), "MAYBE : SERVER OFFLINE " + response.code() , Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getContext(), "FAILURE  ", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}