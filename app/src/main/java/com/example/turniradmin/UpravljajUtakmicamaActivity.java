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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class UpravljajUtakmicamaActivity extends AppCompatActivity {
    Spinner spinnerTeam1, spinnerTeam2, spinnerDay, spinnerMonth, spinnerYear, spinnerHour, spinnerMinutes, spinnerGame, spinnerTeam1Players, spinnerTeam2Players;
    Spinner spinnerEvent1, spinnerEvent2, spinnerEvents, spinnerPlayers, spinnerChangeEvent;
    Button btnAddGame, btnAddEvent, btnSaveGame, btnDeleteEvent;
    TextView textVTeam1, textVTeam2;
    EditText etTeam1Goals, etTeam2Goals;
    RelativeLayout relLayout;
    long maxID = 0;
    int gameIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upravljaj_utakmicama);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upravljaj Utakmicama");
        assignViews();

        DatabaseReference referenceGames = FirebaseDatabase.getInstance().getReference().child("games");
        DatabaseReference referenceTeams = FirebaseDatabase.getInstance().getReference().child("teams");
        DatabaseReference referencePlayers = FirebaseDatabase.getInstance().getReference().child("players");

        ArrayList<String> teams = new ArrayList<>();
        ArrayList<Game> games = new ArrayList<>();
        ArrayList<String> gamesTeams = new ArrayList<>();

        referenceGames.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxID = snapshot.getChildrenCount();
                games.clear();
                gamesTeams.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    games.add(dataSnapshot.getValue(Game.class));
                    gamesTeams.add(dataSnapshot.child("team1").getValue(String.class) + " - " + dataSnapshot.child("team2").getValue(String.class));
                }
                ArrayAdapter<String> adapterGames = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, gamesTeams);
                spinnerGame.setAdapter(adapterGames);
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

                //gameEvents za zakazat utakmicu
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


        ArrayList<GameEvent> gameEvents = new ArrayList<>();
        ArrayList<String> gameEventsNames = new ArrayList<>();


        spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String teams[] = spinnerGame.getSelectedItem().toString().split("-", 2);
                gameIndex = spinnerGame.getSelectedItemPosition();
                String team1 = teams[0].trim();
                String team2 = teams[1].trim();

                Game game = games.get(gameIndex);
                etTeam1Goals.setText(game.getTeam1Goals());
                etTeam2Goals.setText(game.getTeam2Goals());


                ArrayList<String> team1_players = new ArrayList<>();
                ArrayList<String> team2_players = new ArrayList<>();
                ArrayList<String> playersFromBothTeams = new ArrayList<>();


                textVTeam1.setText(team1);
                textVTeam2.setText(team2);


                Query query1 = referencePlayers.orderByChild("team_name").equalTo(team1);
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            team1_players.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Player player = dataSnapshot.getValue(Player.class);
                                team1_players.add(player.getName() + " " + player.getLast_name() + " (" + player.getNumber() + ")");
                                playersFromBothTeams.add(player.getName() + " " + player.getLast_name() + " (" + player.getNumber() + ")");
                            }
                            ArrayAdapter<String> team1Adapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, team1_players);
                            spinnerTeam1Players.setAdapter(team1Adapter);


                        }catch (Exception e){
                            Toast.makeText(UpravljajUtakmicamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpravljajUtakmicamaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



                Query query2 = referencePlayers.orderByChild("team_name").equalTo(team2);
                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            team2_players.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Player player = dataSnapshot.getValue(Player.class);
                                team2_players.add(player.getName() + " " + player.getLast_name() + " (" + player.getNumber() + ")");
                                playersFromBothTeams.add(player.getName() + " " + player.getLast_name() + " (" + player.getNumber() + ")");
                            }
                            ArrayAdapter<String> team2Adapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, team2_players);
                            spinnerTeam2Players.setAdapter(team2Adapter);
                            ArrayAdapter<String> allPlayersAdapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, playersFromBothTeams);
                            spinnerPlayers.setAdapter(allPlayersAdapter);

                        }catch (Exception e){
                            Toast.makeText(UpravljajUtakmicamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpravljajUtakmicamaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });




                referenceGames.child(gameIndex + "").child("GameEvents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            gameEventsNames.clear();
                            gameEvents.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                GameEvent gameEvent = dataSnapshot.getValue(GameEvent.class);
                                gameEvents.add(gameEvent);
                                gameEventsNames.add(gameEvent.getEvent() + " - " + gameEvent.getScorerID());
                            }
                            ArrayAdapter<String> gameEventNamesAdapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, gameEventsNames);
                            spinnerEvents.setAdapter(gameEventNamesAdapter);

                        }catch (Exception e){
                            Toast.makeText(UpravljajUtakmicamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpravljajUtakmicamaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event1 = spinnerEvent1.getSelectedItem().toString();
                String event2 = spinnerEvent2.getSelectedItem().toString();

                if(!event1.equals("-")){
                    String player = spinnerTeam1Players.getSelectedItem().toString();
                    String team = spinnerTeam1.getSelectedItem().toString();
                    GameEvent gameEvent = new GameEvent(player, event1, team);
                    gameEvents.add(gameEvent);

                    spinnerEvent1.setSelection(0);
                }

                if(!event2.equals("-")){
                    String player = spinnerTeam2Players.getSelectedItem().toString();
                    String team = spinnerTeam2.getSelectedItem().toString();
                    GameEvent gameEvent = new GameEvent(player, event2, team);
                    gameEvents.add(gameEvent);

                    spinnerEvent2.setSelection(0);
                }


                
            }
        });
        
        btnSaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String team1Goals = etTeam1Goals.getText().toString();
                String team2Goals = etTeam2Goals.getText().toString();
                String changeEventTo = spinnerChangeEvent.getSelectedItem().toString();
                
                
                if(team1Goals.equals("") || Integer.parseInt(team1Goals) < 0 || team2Goals.equals("") || Integer.parseInt(team2Goals) < 0){
                    Toast.makeText(UpravljajUtakmicamaActivity.this, "Krivo ste unijeli rezultat", Toast.LENGTH_SHORT).show();
                }else{
                    referenceGames.child(gameIndex + "").child("team1Goals").setValue(team1Goals);
                    referenceGames.child(gameIndex + "").child("team2Goals").setValue(team2Goals);


                    if(gameEventsNames.size() > 0){
                        int index = gameEventsNames.indexOf(spinnerEvents.getSelectedItem().toString());
                        GameEvent gameEvent = gameEvents.get(index);

                        if(!changeEventTo.equals("-")){
                            gameEvent.setEvent(changeEventTo);
                        }

                        String scorerID = spinnerPlayers.getSelectedItem().toString();
                        gameEvent.setScorerID(scorerID);
                        referenceGames.child(gameIndex + "").child("GameEvents").setValue(gameEvents);

                    }else{

                        referenceGames.child(gameIndex + "").child("GameEvents").setValue(gameEvents);
                    }

                }
            }
        });

        btnDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameEventsNames.size() > 0){
                    Snackbar.make(relLayout, "Zelite li izbrisati događaj", Snackbar.LENGTH_LONG)
                            .setAction("DA", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int index = gameEventsNames.indexOf(spinnerEvents.getSelectedItem().toString());
                                    GameEvent gameEvent = gameEvents.get(index);

                                    gameEvents.remove(gameEvent);
                                    referenceGames.child(gameIndex + "").child("GameEvents").setValue(gameEvents);
                                }
                            }).setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright)).show();

                }else{
                    Toast.makeText(UpravljajUtakmicamaActivity.this, "Odaberi događaj", Toast.LENGTH_SHORT).show();
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
        spinnerGame = findViewById(R.id.spinnerGame);
        spinnerTeam1Players = findViewById(R.id.spinnerTeam1Players);
        spinnerTeam2Players = findViewById(R.id.spinnerTeam2Players);
        spinnerEvent1 = findViewById(R.id.spinnerEvent1);
        spinnerEvent2 = findViewById(R.id.spinnerEvent2);
        spinnerEvents = findViewById(R.id.spinnerEvents);
        spinnerPlayers = findViewById(R.id.spinnerPlayers);
        spinnerChangeEvent = findViewById(R.id.spinnerChangeEvent);

        btnAddGame = findViewById(R.id.btnAddGame);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnSaveGame = findViewById(R.id.btnSaveGame);
        btnDeleteEvent = findViewById(R.id.btnDeleteEvent);

        textVTeam1 = findViewById(R.id.textVTeam1);
        textVTeam2 = findViewById(R.id.textVTeam2);

        etTeam1Goals = findViewById(R.id.etTeam1Goals);
        etTeam2Goals = findViewById(R.id.etTeam2Goals);

        relLayout = findViewById(R.id.relLayout);

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

        spinnerTeam1Players.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTeam2Players.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerChangeEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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