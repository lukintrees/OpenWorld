package com.lukin.openworld.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.MultiplayerManagerThread;

public class MultiplayerScreen implements Screen {
    private final Stage stage;
    private DeviceList devicesList;
    private HorizontalGroup refreshButton;

    public MultiplayerScreen(){
        this.stage = LKGame.getStage();
    }

    @Override
    public void show() {
        refreshButton = new HorizontalGroup();
        refreshButton.setTouchable(Touchable.enabled);
        Image reloadImage = new Image(new Texture(Gdx.files.internal("popular_icons/reload.png")));
        refreshButton.addActor(reloadImage);
        refreshButton.space(10);
        Label reloadText = new Label("обновить", new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE));
        refreshButton.addActor(reloadText);
        refreshButton.setPosition(10, 150);
        ClickListener clickListener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new Thread() {
                    @Override
                    public void run() {
                        devicesList.updateDevices(LKGame.getMultiplayer().getPairedConnections());
                    }
                }.start();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

            }
        };
        reloadText.addListener(clickListener);
        reloadImage.addListener(clickListener);
        Label startServer = new Label("Создать сервер", new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE));
        startServer.setPosition(10, 5);
        startServer.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                LKGame.setScreen(LKGame.Screen.SERVER_CREATION);
            }
        });
        devicesList = new DeviceList();
        devicesList.setBounds(124, 6, stage.getWidth() - 124, stage.getHeight() - 6);
        stage.addActor(startServer);
        stage.addActor(refreshButton);
        stage.addActor(devicesList);
        new Thread(){
            @Override
            public void run() {
                devicesList.updateDevices(LKGame.getMultiplayer().getPairedConnections());
            }
        }.start();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {

    }
}
