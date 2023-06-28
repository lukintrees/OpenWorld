package com.lukin.openworld.utils;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.entities.LKEntity;
import com.lukin.openworld.entities.LocalPlayer;
import com.lukin.openworld.entities.RemotePlayer;
import com.lukin.openworld.ui.GameScreen;
import com.lukin.openworld.ui.PvPScoreBar;


public class PvPMode implements Mode {
    private int playerCount;
    private int playersInTeams;
    private int scoreToWin;
    private Array<Team> teams = new Array<>();

    public PvPMode(){

    }

    public PvPMode(int playerCount, int scoreToWin) {
        this.playerCount = playerCount;
        this.scoreToWin = scoreToWin;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    @Override
    public void entityAdded(Entity entity) {
        if(entity instanceof LocalPlayer || entity instanceof RemotePlayer) {
            if (playersInTeams == 0){
                Team team = new Team();
                team.entities.add(((LKEntity) entity).entityUID);
                teams.add(team);
                playersInTeams++;
            }else if (playersInTeams == 1){
                Team team = new Team();
                team.entities.add(((LKEntity) entity).entityUID);
                teams.add(team);
                playersInTeams++;
            }
        }
    }

    @Override
    public void entityRemoved(Entity entity) {

    }

    public static class Team{
        public String name;
        public Array<Integer> entities = new Array<>();
        public int score;
    }

    public Array<Team> getTeams() {
        return teams;
    }

    public void setTeams(Array<Team> teams) {
        this.teams = teams;
    }

    public void setScoreToWin(int scoreToWin) {
        this.scoreToWin = scoreToWin;
    }

    public void onKill(LKEntity killer, LKEntity victim){
        for(Team team : teams){
            for(Integer entity : team.entities){
                if(entity == killer.entityUID){
                    team.score++;
                    updateUI();
                    break;
                }
            }
        }
    }

    @Override
    public GameScreen.GameMode getMode() {
        return GameScreen.GameMode.PVP;
    }

    private void updateUI(){
        for (Actor actor : LKGame.getStage().getActors()){
            if (actor instanceof PvPScoreBar){
                PvPScoreBar scoreBar = (PvPScoreBar) actor;
                scoreBar.setMode(this);
            }
        }
    }

    public String serialize() {
        Json json = new Json(JsonWriter.OutputType.json);
        return json.toJson(this, PvPMode.class);
    }

    public void deserialize(String s) {
        System.out.println(s);
        Json json = new Json();
        PvPMode mode = json.fromJson(PvPMode.class, s);
        this.playerCount = ++mode.playerCount;
        this.playersInTeams = mode.playersInTeams;
        this.teams = mode.teams;
        updateUI();
    }

    @Override
    public boolean is(GameScreen.GameMode modeEnum) {
        return modeEnum == GameScreen.GameMode.PVP;
    }
}
