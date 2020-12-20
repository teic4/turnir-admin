package com.example.turniradmin;

import java.util.ArrayList;

public class Game {
    private String team1;
    private String team2;
    private String date;
    private String time;
    private String team1Goals;
    private String team2Goals;
    private ArrayList<GameEvent> GameEvents;
    private boolean isPlayed = false;


    public Game() {
    }


    public Game(String team1, String team2, String date, String time, String team1Goals, String team2Goals, ArrayList<GameEvent> gameEvents) {
        this.team1 = team1;
        this.team2 = team2;
        this.date = date;
        this.time = time;
        this.team1Goals = team1Goals;
        this.team2Goals = team2Goals;
        GameEvents = gameEvents;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeam1Goals() {
        return team1Goals;
    }

    public void setTeam1Goals(String team1Goals) {
        this.team1Goals = team1Goals;
    }

    public String getTeam2Goals() {
        return team2Goals;
    }

    public void setTeam2Goals(String team2Goals) {
        this.team2Goals = team2Goals;
    }

    public ArrayList<GameEvent> getGameEvents() {
        return GameEvents;
    }

    public void setGameEvents(ArrayList<GameEvent> gameEvents) {
        GameEvents = gameEvents;
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public void setPlayed(boolean played) {
        isPlayed = played;
    }
}
