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
import android.widget.ScrollView;
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

public class UpravljajIgracimaActivity extends AppCompatActivity {

    EditText etRedCards, etYellowCards, etName, etLastName, etNumber, etGamesNum, etGoals;
    Spinner spinnerTeam, spinnerPlayer, spinnerDay, spinnerMonth, spinnerYear, spinnerChangeTeam;
    Button btnAddPlayer, btnDeletePlayer;
    ScrollView parentLayout;
    String key = "";
    long playerID = -1;
    long maxID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upravljaj_igracima);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upravljaj igračima");
        assignViews();

        DatabaseReference referenceTeams = FirebaseDatabase.getInstance().getReference().child("teams");
        DatabaseReference referencePlayers = FirebaseDatabase.getInstance().getReference().child("players");



        ArrayList<String> teams = new ArrayList<>();

        referenceTeams.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    teams.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        String team = dataSnapshot.getValue(String.class);
                        teams.add(team);

                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UpravljajIgracimaActivity.this, android.R.layout.simple_spinner_dropdown_item, teams);
                    spinnerTeam.setAdapter(adapter);
                    spinnerChangeTeam.setAdapter(adapter);

                }catch (Exception e){
                    Toast.makeText(UpravljajIgracimaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpravljajIgracimaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        ArrayList<String> playersName = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();

        referencePlayers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                maxID = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpravljajIgracimaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        spinnerTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String team = spinnerTeam.getSelectedItem().toString();
                Query query = referencePlayers.orderByChild("team_name").equalTo(team);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            playersName.clear();
                            players.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                Player player = dataSnapshot.getValue(Player.class);
                                players.add(player);
                                playersName.add(player.getName() + " " + player.getLast_name());
                            }
                            ArrayAdapter<String> adapterPlayers = new ArrayAdapter<>(UpravljajIgracimaActivity.this, android.R.layout.simple_spinner_dropdown_item, playersName);
                            spinnerPlayer.setAdapter(adapterPlayers);

                            if(players.size() == 0) {
                                etName.setText("");
                                etLastName.setText("");
                                etNumber.setText("");
                                etRedCards.setText("");
                                etYellowCards.setText("");
                                etGoals.setText("");
                                etGamesNum.setText("");
                                spinnerChangeTeam.setSelection(0);
                                spinnerDay.setSelection(0);
                                spinnerMonth.setSelection(0);
                                spinnerYear.setSelection(0);
                            }

                        }catch (Exception e){
                            Toast.makeText(UpravljajIgracimaActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UpravljajIgracimaActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



                spinnerPlayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);

                        if(players.size() != 0){

                            int index = playersName.indexOf(spinnerPlayer.getSelectedItem().toString());
                            Player player2 = players.get(index);

                            etName.setText(player2.getName());
                            etLastName.setText(player2.getLast_name());
                            etNumber.setText(player2.getNumber());
                            etRedCards.setText(player2.getRed_cards() + "");
                            etYellowCards.setText(player2.getYellow_cards() + "");
                            etGoals.setText(player2.getGoals() + "");
                            etGamesNum.setText(player2.getNumber_of_games() + "");

                            String date = player2.getDate();

                            int dayInt = Integer.parseInt(date.substring(8,10));
                            int monthInt = Integer.parseInt(date.substring(5,7));
                            int yearInt = Integer.parseInt(date.substring(0,4));

                            spinnerDay.setSelection(dayInt - 1);
                            spinnerMonth.setSelection(monthInt - 1);
                            spinnerYear.setSelection(yearInt - 1960);

                            playerID = player2.getId();

                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //CHANGE COLOR OF SPINNER
        spinnerChangeTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //CHANGE COLOR OF SPINNER
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //CHANGE COLOR OF SPINNER
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //CHANGE COLOR OF SPINNER
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnDeletePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = spinnerPlayer.getSelectedItemPosition();

                if(index > -1){
                    Snackbar.make(parentLayout, "Zelite li izbrisati igraca?", Snackbar.LENGTH_LONG)
                            .setAction("DA", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Player player_to_del = players.get(index);
                                    Toast.makeText(UpravljajIgracimaActivity.this, players.get(index).getName(), Toast.LENGTH_SHORT).show();
                                    players.remove(index);
                                    for(Player p : players){
                                        p.setId(players.indexOf(p));
                                    }
                                    referencePlayers.setValue(players);
                                    spinnerTeam.setSelection(0);
                                }
                            }).setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright)).show();

                }else{
                    Toast.makeText(UpravljajIgracimaActivity.this, "Odaberi igrača", Toast.LENGTH_SHORT).show();
                }


            }
        });


        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(maxID);
            }
        });

    }

    private void assignViews(){
        parentLayout = findViewById(R.id.scrollView);

        etRedCards = findViewById(R.id.etRedCards);
        etYellowCards = findViewById(R.id.etYellowCards);
        etName = findViewById(R.id.etName);
        etLastName = findViewById(R.id.etLastName);
        etNumber = findViewById(R.id.etNumber);
        etGamesNum = findViewById(R.id.etGamesNum);
        etGoals = findViewById(R.id.etGoals);

        spinnerTeam = findViewById(R.id.spinnerTeam);
        spinnerPlayer = findViewById(R.id.spinnerPlayer);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerChangeTeam = findViewById(R.id.spinnerChangeTeam);

        btnAddPlayer = findViewById(R.id.btnAddPlayer);
        btnDeletePlayer = findViewById(R.id.btnDeletePlayer);


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

        ArrayList<String> years = new ArrayList<>();
        for(int i = 1960; i < 2010; i++){
            years.add(i + "");
        }


        ArrayAdapter<String> adapterYears = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        spinnerYear.setAdapter(adapterYears);

    }


    private void save(long playerID){

        if(playerID != this.playerID){
            playerID = maxID;
        }


        String team = spinnerChangeTeam.getSelectedItem().toString();
        String name = etName.getText().toString();
        String last_name = etLastName.getText().toString();
        String number = etNumber.getText().toString();
        String red_cardsString = etRedCards.getText().toString();
        String yellow_cardsString = etYellowCards.getText().toString();
        String goalsString = etGoals.getText().toString();
        String games_numString = etGamesNum.getText().toString();


        //YYYY-MM-DD
        String day = spinnerDay.getSelectedItem().toString();
        String month = spinnerMonth.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();
        String date = year + "-" + month + "-" + day;



        DatabaseReference referencePlayers = FirebaseDatabase.getInstance().getReference().child("players");
        if(playerID == -1){
            Toast.makeText(this, "Problem u bazi podataka", Toast.LENGTH_SHORT).show();
        }else{
            if(team.equals("") || name.equals("") || last_name.equals("") || number.equals("") || red_cardsString.equals("") || yellow_cardsString.equals("") || goalsString.equals("") || games_numString.equals("") ){
                Toast.makeText(this, "Unijeli ste krive vrijednosti", Toast.LENGTH_SHORT).show();
            }else{
                int yellow_cards = Integer.parseInt(yellow_cardsString);
                int red_cards = Integer.parseInt(red_cardsString);
                int goals = Integer.parseInt(goalsString);
                int games_num = Integer.parseInt(games_numString);

                Player playerObject = new Player(team, name, last_name, date, number, goals, yellow_cards, red_cards, games_num, playerID);
                referencePlayers.child(playerID + "").setValue(playerObject);
                int index = spinnerChangeTeam.getSelectedItemPosition();
                spinnerTeam.setSelection(index);
                Toast.makeText(this, "Spremljeno", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else if(item.getItemId() == R.id.btnCalendar){
            save(playerID);
        }

        return super.onOptionsItemSelected(item);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);

        return super.onCreateOptionsMenu(menu);

    }

}