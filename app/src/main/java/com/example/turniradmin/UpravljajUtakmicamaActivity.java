package com.example.turniradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UpravljajUtakmicamaActivity extends AppCompatActivity {
    Spinner spinnerTeam1, spinnerTeam2, spinnerDay, spinnerMonth, spinnerYear, spinnerHour, spinnerMinutes;
    Button btnAddGame;
    long maxID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upravljaj_utakmicama);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upravljaj Utakmicama");
        assignViews();

        DatabaseReference referenceGames = FirebaseDatabase.getInstance().getReference().child("games");
        DatabaseReference referenceTeams = FirebaseDatabase.getInstance().getReference().child("teams");
        ArrayList<String> teams = new ArrayList<>();

        referenceGames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxID = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        referenceTeams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    teams.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String team = dataSnapshot.getValue(String.class);
                        teams.add(team);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, teams);
                    spinnerTeam1.setAdapter(adapter);
                    spinnerTeam2.setAdapter(adapter);



                }catch (Exception e){
                    Toast.makeText(UpravljajUtakmicamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpravljajUtakmicamaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        btnAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String team1 = spinnerTeam1.getSelectedItem().toString();
                String team2 = spinnerTeam2.getSelectedItem().toString();

                //YYY-MM-DD
                String year = spinnerYear.getSelectedItem().toString();
                String month = spinnerMonth.getSelectedItem().toString();
                String day = spinnerDay.getSelectedItem().toString();
                String date = year + "-" + month + "-" + day;

                String hour = spinnerHour.getSelectedItem().toString();
                String minutes = spinnerMinutes.getSelectedItem().toString();
                String time = hour + ":" + minutes;
                String team1Goals = "0";
                String team2Goals = "0";
                ArrayList<GameEvent> gameEvents = new ArrayList<>();

                if(team1.equals(team2)){
                    Toast.makeText(UpravljajUtakmicamaActivity.this, "Unesite 2 ekipe", Toast.LENGTH_SHORT).show();
                }else{
                    Game game = new Game(team1, team2, date, time, team1Goals, team2Goals, gameEvents);
                    referenceGames.child(maxID + "").setValue(game);
                    Toast.makeText(UpravljajUtakmicamaActivity.this, "Utakmica zakazana", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void assignViews(){
        spinnerTeam1 = findViewById(R.id.spinnerTeam1);
        spinnerTeam2 = findViewById(R.id.spinnerTeam2);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerHour = findViewById(R.id.spinnerHour);
        spinnerMinutes = findViewById(R.id.spinnerMinutes);

        btnAddGame = findViewById(R.id.btnAddGame);


        //SPINERS
        ArrayList<String> days = new ArrayList<>();
        for(int i = 1; i < 32; i++){
            if(i < 10){
                days.add("0" + i + "");
            }else{
                days.add(i + "");
            }

        }
        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        spinnerDay.setAdapter(adapterDays);

        ArrayList<String> months = new ArrayList<>();
        for(int i = 1; i < 13; i++){
            if(i < 10){
                months.add("0" + i + "");
            }else{
                months.add(i + "");
            }
        }
        ArrayAdapter<String> adapterMonths = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        spinnerMonth.setAdapter(adapterMonths);

        spinnerTeam1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTeam2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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