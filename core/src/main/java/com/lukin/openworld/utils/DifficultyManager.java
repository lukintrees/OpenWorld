package com.lukin.openworld.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class DifficultyManager {
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String PREFERENCES_NAME = "game_preferences";
    private static final Preferences preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
    private DifficultyEnum currentDifficulty;

    public DifficultyManager() {
        currentDifficulty = DifficultyEnum.MIDDLE;
        if (preferences.contains(DIFFICULTY_KEY)) {
            currentDifficulty = DifficultyEnum.valueOf(preferences.getString(DIFFICULTY_KEY));
        }
    }

    public void changeMode(DifficultyEnum difficulty) {
        currentDifficulty = difficulty;
        preferences.putString(DIFFICULTY_KEY, difficulty.name());
        preferences.flush();
    }

    public DifficultyEnum getDifficulty() {
        return currentDifficulty;
    }

    public float getDifficultyRate(){
        if (currentDifficulty == DifficultyEnum.EASY){
            return 0.8f;
        }else if(currentDifficulty == DifficultyEnum.MIDDLE){
            return 1;
        }else{
            return 1.2f;
        }
    }

    public float getDifficultyRateReverse(){
        if (currentDifficulty == DifficultyEnum.EASY){
            return 1.3f;
        }else if(currentDifficulty == DifficultyEnum.MIDDLE){
            return 0.9f;
        }else{
            return 0.5f;
        }
    }

    public enum DifficultyEnum {
        EASY,
        MIDDLE,
        HARD
    }
}

