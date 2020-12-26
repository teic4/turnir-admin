package com.example.turniradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
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
    TextView textVTeam1, textVTeam2, tvEditEvent, tvChoosePlayer, tvChangeEvent;
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
                    Game game = dataSnapshot.getValue(Game.class);
                    games.add(game);
                    gamesTeams.add(game.getTeam1() + " - " + game.getTeam2());
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

        //UREDI UTAKMICU


        ArrayList<Player> playersFromBothTeams = new ArrayList<>();     //svi igraci iz oba tima
        ArrayList<String> playersFromBothTeamsNames = new ArrayList<>();

        ArrayList<Player> team1Players = new ArrayList<>();
        ArrayList<String> team1PlayersNames = new ArrayList<>();

        ArrayList<Player> team2Players = new ArrayList<>();
        ArrayList<String> team2PlayersNames = new ArrayList<>();


        ArrayList<GameEvent> gameEvents = new ArrayList<>();      //eventovi za odabrani game
        ArrayList<String> gameEventsNames = new ArrayList<>();



        spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                gameIndex = spinnerGame.getSelectedItemPosition();
                Game game = games.get(gameIndex);

                String team1 = game.getTeam1();
                String team2 = game.getTeam2();

                etTeam1Goals.setText(game.getTeam1Goals());
                etTeam2Goals.setText(game.getTeam2Goals());

                textVTeam1.setText(team1);
                textVTeam2.setText(team2);


                //napuni spinnerTeam1Players i spinnerPlayers
                Query query1 = referencePlayers.orderByChild("team_name").equalTo(team1);
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            playersFromBothTeams.clear();
                            playersFromBothTeamsNames.clear();

                            team1Players.clear();
                            team1PlayersNames.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Player player = dataSnapshot.getValue(Player.class);

                                team1Players.add(player);
                                team1PlayersNames.add(player.getName() + " " + player.getLast_name() + " (" + player.getNumber() + ")");

                            }
                            ArrayAdapter<String> team1Adapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, team1PlayersNames);
                            spinnerTeam1Players.setAdapter(team1Adapter);

                            playersFromBothTeams.addAll(team1Players);
                            playersFromBothTeamsNames.addAll(team1PlayersNames);


                        }catch (Exception e){
                            Toast.makeText(UpravljajUtakmicamaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpravljajUtakmicamaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


                //napuni spinnerTeam2Players i spinnerPlayers
                Query query2 = referencePlayers.orderByChild("team_name").equalTo(team2);
                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            team2Players.clear();
                            team2PlayersNames.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Player player = dataSnapshot.getValue(Player.class);

                                team2Players.add(player);
                                team2PlayersNames.add(player.getName() + " " + player.getLast_name() + " (" + player.getNumber() + ")");

                            }
                            ArrayAdapter<String> team2Adapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, team2PlayersNames);
                            spinnerTeam2Players.setAdapter(team2Adapter);

                            ArrayAdapter<String> allPlayersAdapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, playersFromBothTeamsNames);
                            spinnerPlayers.setAdapter(allPlayersAdapter);

                            playersFromBothTeams.addAll(team2Players);
                            playersFromBothTeamsNames.addAll(team2PlayersNames);


                            //SVI EVENTOVI I IGRACI
                            //moglo se i priko addValueEvent listenera ali radi manje potrosnje interneta izvlacin eventove iz games arraya

                            gameEvents.clear();
                            gameEventsNames.clear();
                            Game selectedGame = games.get(gameIndex);
                            ArrayList<GameEvent> selectedGameEvents = selectedGame.getGameEvents();

                            if(selectedGameEvents != null){

                                tvEditEvent.setVisibility(View.VISIBLE);
                                spinnerEvents.setVisibility(View.VISIBLE);
                                tvChoosePlayer.setVisibility(View.VISIBLE);
                                spinnerPlayers.setVisibility(View.VISIBLE);
                                tvChangeEvent.setVisibility(View.VISIBLE);
                                spinnerChangeEvent.setVisibility(View.VISIBLE);
                                btnDeleteEvent.setVisibility(View.VISIBLE);

                                //samo puni isti array koji je izvan itemselectedlistenera zbog koda u btn save i btn addEvent click listeneru
                                for (GameEvent gameEvent : selectedGameEvents){
                                    gameEvents.add(gameEvent);
                                }

                                for (GameEvent gameEvent : selectedGameEvents){
                                    long playerID = gameEvent.getScorerID();

                                    for(Player p : playersFromBothTeams){
                                        if(p.getId() == playerID){
                                            Player player = p;
                                            gameEventsNames.add(gameEvent.getEvent() + " - " + player.getName() + " " + player.getLast_name());
                                        }
                                    }

                                }
                                ArrayAdapter<String> gameEventNamesAdapter = new ArrayAdapter<>(UpravljajUtakmicamaActivity.this, android.R.layout.simple_spinner_dropdown_item, gameEventsNames);
                                spinnerEvents.setAdapter(gameEventNamesAdapter);
                            }else{
                                tvEditEvent.setVisibility(View.GONE);
                                spinnerEvents.setVisibility(View.GONE);
                                tvChoosePlayer.setVisibility(View.GONE);
                                spinnerPlayers.setVisibility(View.GONE);
                                tvChangeEvent.setVisibility(View.GONE);
                                spinnerChangeEvent.setVisibility(View.GONE);
                                btnDeleteEvent.setVisibility(View.GONE);
                            }



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

                if(spinnerEvent1.getSelectedItemPosition() != 0){
                    int index = spinnerTeam1Players.getSelectedItemPosition();
                    String team = textVTeam1.getText().toString();
                    long player_id = team1Players.get(index).getId();

                    GameEvent gameEvent = new GameEvent(player_id, event1, team);
                    gameEvents.add(gameEvent);

                    spinnerEvent1.setSelection(0);
                }

                if(spinnerEvent2.getSelectedItemPosition() != 0){
                    String team = textVTeam2.getText().toString();
                    int index = spinnerTeam2Players.getSelectedItemPosition();
                    long player_id = team2Players.get(index).getId();

                    GameEvent gameEvent = new GameEvent(player_id, event2, team);
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

                        referenceGames.child(gameIndex + "").child("played").setValue(true);
                        int indexPlayer = spinnerPlayers.getSelectedItemPosition();

                        long scorerID = playersFromBothTeams.get(indexPlayer).getId();

                        gameEvent.setScorerID(scorerID);
                        referenceGames.child(gameIndex + "").child("GameEvents").setValue(gameEvents);

                    }else{
                        referenceGames.child(gameIndex + "").child("GameEvents").setValue(gameEvents);
                        referenceGames.child(gameIndex + "").child("played").setValue(true);
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
        tvEditEvent = findViewById(R.id.tvEditEvent);
        tvChoosePlayer = findViewById(R.id.tvChoosePlayer);
        tvChangeEvent = findViewById(R.id.tvChangeEvent);

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