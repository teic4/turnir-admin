package com.example.turniradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpravljajEkipamaActivity extends AppCompatActivity {

    ConstraintLayout parentLayout;
    Button btnAddTeam, btnDeleteTeam, btnChangeTeamName;
    EditText etAddTeam, etChangeTeamName;
    Spinner spinnerTeams;
    long maxID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upravljaj_ekipama);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upravljaj ekipama");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("teams");

        btnAddTeam = findViewById(R.id.btnAddTeam);
        btnDeleteTeam = findViewById(R.id.btnDeleteTeam);
        btnChangeTeamName = findViewById(R.id.btnChangeTeamName);
        parentLayout = findViewById(R.id.constaintLayout);

        etAddTeam = findViewById(R.id.etAddTeam);
        etChangeTeamName = findViewById(R.id.etChangeTeamName);

        spinnerTeams = findViewById(R.id.spinnerTeams);
        ArrayList<String> teams = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();

        spinnerTeams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    teams.clear();
                    maxID = snapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String team = dataSnapshot.getValue(String.class);
                        teams.add(team);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UpravljajEkipamaActivity.this, android.R.layout.simple_spinner_dropdown_item, teams);
                    spinnerTeams.setAdapter(adapter);



                }catch (Exception e){
                    Toast.makeText(UpravljajEkipamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpravljajEkipamaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        btnDeleteTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Snackbar.make(parentLayout, "Želite li izbrisati ekipu?", Snackbar.LENGTH_LONG)
                        .setAction("DA", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String team_to_del = spinnerTeams.getSelectedItem().toString();
                                int index = teams.indexOf(team_to_del);
                                teams.remove(index);
                                reference.setValue(teams);
                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright)).show();
            }
        });


        btnChangeTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_team_name = etChangeTeamName.getText().toString();
                if(!new_team_name.equals("")){
                    String team_to_change = spinnerTeams.getSelectedItem().toString();
                    int index = teams.indexOf(team_to_change);
                    String key_to_change = keys.get(index);
                    reference.child(key_to_change).setValue(new_team_name);
                }

            }
        });

        btnAddTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String team_to_add = etAddTeam.getText().toString();
                if(!team_to_add.equals("")){
                    boolean postoji = false;
                    for (String s : teams){
                        if(team_to_add.equals(s)){
                            postoji = true;
                        }
                    }
                    if(postoji){
                        Toast.makeText(UpravljajEkipamaActivity.this, "Ekipa sa istim imenom je vec registrirana", Toast.LENGTH_SHORT).show();
                    }else{
                        reference.child(maxID + "").setValue(team_to_add);
                        Toast.makeText(UpravljajEkipamaActivity.this, "Ekipa registrirana", Toast.LENGTH_SHORT).show();
                    }
                }
               
            }

        });



    }

    //Back navigation bar button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


}

