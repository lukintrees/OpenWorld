package com.lukin.openworld.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.MultiplayerManagerThread;

import java.util.Set;

public class DeviceList extends Actor {
    private final Stage stage;
    private Label[] deviceLabels;

    public DeviceList() {
        stage = LKGame.getStage();
    }

    public void updateDevices(Set<MultiplayerManagerThread.Device> pairedDevices) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);
        // Создаем метки для каждого устройства и располагаем их вертикально
        float y = getHeight(); // начинаем с верхней грани актера
        deviceLabels = new Label[pairedDevices.size()];
        int i = 0; // начинаем с первого устройства
        for (MultiplayerManagerThread.Device device : pairedDevices) {
            Label label = new Label(device.name + " (" + device.address + ")", labelStyle);
            label.setPosition(getX(), y - label.getHeight() - 10); // устанавливаем позицию метки с учетом ее высоты
            label.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LKGame.getMultiplayer().connectToServerDevice(device);
                }
            });
            deviceLabels[i++] = label;
            stage.addActor(label);
            y -= label.getHeight(); // уменьшаем значение y на высоту метки
        }
        // Устанавливаем размеры актера на основе размеров меток
        setSize(getWidth(), getHeight() - y);
    }
}