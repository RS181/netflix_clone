package com.example.cms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Options extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Button MFilmes = (Button) findViewById(R.id.MFilmes);
        Button MClients = (Button) findViewById(R.id.Clients);
        Button MAdmins = (Button) findViewById(R.id.Admins);
        MAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Localintent = new Intent(getBaseContext(), ManageAdmins.class);
                startActivity(Localintent);
            }
        });

        MClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Localintent = new Intent(getBaseContext(), ManageClients.class);
                startActivity(Localintent);
            }
        });

        MFilmes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Localintent = new Intent(getBaseContext(), holder.class);
                startActivity(Localintent);
            }
        });
    }
}