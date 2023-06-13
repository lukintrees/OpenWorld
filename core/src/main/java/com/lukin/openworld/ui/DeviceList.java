package com.lukin.openworld.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.MultiplayerManagerThread;

import java.util.Set;

public class DeviceList extends Table {

    public DeviceList() {
    }

    public void updateDevices(Set<MultiplayerManagerThread.Device> devices) {
        clear();
        left().top();
        Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);
        for (MultiplayerManagerThread.Device device : devices) {
            Label label = new Label(device.name + " (" + device.address + ")", labelStyle);
            label.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LKGame.getMultiplayer().connectToServerDevice(device);
                }
            });
            add(label).row();
        }
    }
}