package com.lukin.openworld.utils;

import com.badlogic.gdx.Screen;
import com.lukin.openworld.LKGame;

public interface ScreenChangeListener {
    void screenChanged(LKGame.Screen enumScreen, Screen screenObject);
}
