package com.lukin.openworld.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lukin.openworld.LKGame;

import java.util.Arrays;
import java.util.function.Function;

public class ServerCreationScreen implements Screen {
    private final Stage stage;
    private final Array<String> modeStrings;
    private final Array<String> timesInMinutes;
    private Label infoLabel;
    private HorizontalGroup chooseMode;
    private HorizontalGroup chooseTime;
    private boolean screenUsedBefore;
    private Label createButton;

    public ServerCreationScreen() {
        this.stage = LKGame.getStage();
        modeStrings = new Array<>(Arrays.stream(GameScreen.GameMode.values()).map(new Function<GameScreen.GameMode, String>() {
            @Override
            public String apply(GameScreen.GameMode gameMode) {
                return gameMode.toString();
            }
        }).toArray(String[]::new));
        timesInMinutes = new Array<>(new String[]{"1 минута", "2 минуты", "3 минуты", "5 минут", "10 минут", "15 минут"});
    }

    @Override
    public void show() {
        if(!screenUsedBefore) {
            Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);

            infoLabel = new Label("Создание нового сервера", labelStyle);
            infoLabel.setPosition(30, stage.getHeight() - 40);
            infoLabel.setFontScale(20 / LKGame.getDefaultFont().getXHeight());

            chooseMode = new HorizontalGroup();
            chooseMode.setPosition(30, stage.getHeight() - 100);

            Texture buttonTexture = LKGame.getAssetManager().get("popular_icons/play.png", Texture.class);
            TextureRegion region = new TextureRegion(buttonTexture);
            region.flip(true, false);
            Image leftButton = new Image(region);
            chooseMode.addActor(leftButton);

            Label modeLabel = new Label(modeStrings.get(0), labelStyle);
            chooseMode.addActor(modeLabel);


            Image rightButton = new Image(new TextureRegion(buttonTexture));
            chooseMode.addActor(rightButton);

            chooseMode.space(10);

            leftButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    int indexOfCurrentText = modeStrings.indexOf(modeLabel.getText().toString(), false);
                    modeLabel.setText(indexOfCurrentText > 0 ? modeStrings.get(indexOfCurrentText - 1) : modeLabel.getText());
                }
            });
            rightButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    int indexOfNextText = modeStrings.indexOf(modeLabel.getText().toString(), false) + 1;
                    modeLabel.setText(indexOfNextText < modeStrings.size ? modeStrings.get(indexOfNextText) : modeLabel.getText());
                }
            });

            chooseTime = new HorizontalGroup();
            chooseTime.setPosition(30, stage.getHeight() - 130);
            chooseTime.invalidate();

            leftButton = new Image(region);
            chooseTime.addActor(leftButton);

            Label timeLabel = new Label(timesInMinutes.get(2), labelStyle);
            chooseTime.addActor(timeLabel);

            rightButton = new Image(new TextureRegion(buttonTexture));
            chooseTime.addActor(rightButton);

            chooseTime.space(10);

            leftButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    int indexOfCurrentText = timesInMinutes.indexOf(timeLabel.getText().toString(), false);
                    timeLabel.setText(indexOfCurrentText > 0 ? timesInMinutes.get(indexOfCurrentText - 1) : timeLabel.getText());
                }
            });
            rightButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    int indexOfNextText = timesInMinutes.indexOf(timeLabel.getText().toString(), false) + 1;
                    timeLabel.setText(indexOfNextText < timesInMinutes.size ? timesInMinutes.get(indexOfNextText) : timeLabel.getText());
                }
            });

            createButton = new Label("Создать сервер", labelStyle);
            createButton.setPosition(30, stage.getHeight() - 180);

            createButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    LKGame.setMap(LKGame.getMapManager().getRandomMap(GameScreen.GameMode.valueOf(modeLabel.getText().toString())));
                    LKGame.getMultiplayer().startListeningForClientConnections();
                    LKGame.getMultiplayerManagerThread().onServerStart(Integer.parseInt(timeLabel.getText().toString().split(" ")[0]) * 60);
                }
            });
        }
        stage.addActor(infoLabel);
        stage.addActor(chooseMode);
        stage.addActor(chooseTime);
        stage.addActor(createButton);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,0);
        stage.act(delta);
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
        stage.getViewport().update(Gdx.graphics.getHeight(), Gdx.graphics.getHeight(), true);
        screenUsedBefore = true;
    }

    @Override
    public void dispose() {
    }
}