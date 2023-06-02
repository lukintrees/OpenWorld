package com.lukin.openworld.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.MultiplayerManagerThread;

import java.util.Set;

public class DevicesList{
    private final Table table;

    public DevicesList() {
        Stage stage = LKGame.getStage();
        table = new Table();
        stage.addActor(table);
    }

    public void setDevices(Set<MultiplayerManagerThread.Device> devices){
        table.clear();
        Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);
        for(MultiplayerManagerThread.Device device : devices){
            table.add(new Label(device.name, labelStyle));
            table.add(new Label(device.address, labelStyle));
            table.row();
        }
    }

    public void setBounds(float x, float y, float width, float height){
        table.setBounds(x, y, width, height);
        //scrollPane.setBounds(x, y, width, height);
    }
}
