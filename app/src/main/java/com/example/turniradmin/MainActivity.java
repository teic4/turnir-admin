package com.example.turniradmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Button upravljajIgracima, upravljajEkipama, upravljajUtakmicama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Turnir Admin");

        upravljajEkipama = findViewById(R.id.upravljajEkipama);
        upravljajIgracima = findViewById(R.id.upravljajIgracima);
        upravljajUtakmicama = findViewById(R.id.upravljajUtakmicama);

        upravljajIgracima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpravljajIgracimaActivity.class);
                startActivity(intent);
            }
        });

        upravljajUtakmicama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpravljajUtakmicamaActivity.class);
                startActivity(intent);
            }
        });

        upravljajEkipama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpravljajEkipamaActivity.class);
                startActivity(intent);
            }
        });

    }


}