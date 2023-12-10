package com.example.cms;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminReg extends AppCompatActivity {
    private ProgressBar progressBar;

    private void showAlert() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message
        builder.setTitle("Alert")
                .setMessage("Passwords are not the same");

        // Set positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reg);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        EditText textUser = findViewById(R.id.newusername);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        EditText textPass = findViewById(R.id.newpassword);
        EditText textVerPass = findViewById(R.id.VeirficarNewPass);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Button Reg = (Button) findViewById(R.id.newregistar);

        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar = findViewById(R.id.ProgressToReg);
                progressBar.setVisibility(View.VISIBLE);
                Boolean check = textPass.getText().toString().equals(textVerPass.getText().toString());
//                    Toast.makeText(getBaseContext(),textPass.getText().toString() + "/" + textVerPass.getText().toString().toString(), LENGTH_SHORT).show();
                if (check && textUser.toString() != null  && textPass.toString() != null && !(textUser.toString().equals(" ")) && !(textPass.toString().equals(" ")) && !(textVerPass.toString().equals(" "))) {
                    progressBar.setVisibility(View.VISIBLE);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://35.195.14.2/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                    Post post = new Post(textUser.getText().toString(), textPass.getText().toString());


                    Call<Post> call = jsonPlaceHolderApi.NewAdminUser(post);

                    call.enqueue(new Callback<Post>() {
                        @Override
                        public void onResponse(Call<Post> call, Response<Post> response) {

                            if (response.isSuccessful()) {
                                Toast.makeText(getBaseContext(), "REGISTADO NEW ADMIN", LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                Intent main = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(main);
                            } else {
                                Toast.makeText(getBaseContext(), "Failed to register", LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<Post> call, Throwable t) {
                            Toast.makeText(getBaseContext(), "Failed to register", LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), "Passwords are not the same or something is Null", LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    showAlert();
                }
            }

        });
    }
}