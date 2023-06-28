package com.lukin.openworld.utils;

import com.badlogic.ashley.core.EntityListener;
import com.lukin.openworld.entities.LKEntity;
import com.lukin.openworld.ui.GameScreen;

public interface Mode extends EntityListener {
    void onKill(LKEntity killer, LKEntity victim);
    GameScreen.GameMode getMode();
    boolean is(GameScreen.GameMode modeEnum);
    String serialize();

    void deserialize(String s);
}
