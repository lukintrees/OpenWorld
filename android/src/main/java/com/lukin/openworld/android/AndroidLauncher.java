package com.lukin.openworld.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.lukin.openworld.LKGame;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication {
    private LKGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useAccelerometer = false;
        configuration.useCompass = false;
        game = new LKGame();
        //BluetoothAndroid bluetoothAndroid = new BluetoothAndroid(this, this, game);
        //game.setBluetooth(bluetoothAndroid);
        initialize(game, configuration);
    }
}