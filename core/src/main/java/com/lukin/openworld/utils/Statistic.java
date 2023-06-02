package com.lukin.openworld.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

import java.util.HashMap;

public class Statistic {
    private int deaths;
    private int kills;
    private final HashMap<String, Integer> killsByEnemy = new HashMap<>();
    private final HashMap<String, Integer> deathsByEnemy = new HashMap<>();

    public Statistic(){

    }

    public void addKillByEnemy(String name){
        Integer value = killsByEnemy.get(name);
        kills++;
        if (value != null){
            value++;
        }else{
            killsByEnemy.put(name, 1);
        }
    }

    public Integer getKillsByEnemy(String name){
        return killsByEnemy.get(name);
    }

    public void addDeathByEnemy(String name){
        Integer value = deathsByEnemy.get(name);
        deaths++;
        if (value != null){
            value++;
        }else{
            deathsByEnemy.put(name, 1);
        }
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKills() {
        return kills;
    }

    public void saveToFile(String fileName){
        Json json = new Json();
        json.toJson(this, Gdx.files.local(fileName));
    }

    public static Statistic loadFromFile(String filename) {
        Json json = new Json();
        return json.fromJson(Statistic.class, Gdx.files.local(filename));
    }
}
