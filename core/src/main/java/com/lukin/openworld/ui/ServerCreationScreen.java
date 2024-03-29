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
    private Label infoModeLabel;
    private Label infoTimeLabel;
    private BackButton backButton;

    public ServerCreationScreen() {
        this.stage = LKGame.getStage();
        modeStrings = new Array<>(Arrays.stream(GameScreen.GameMode.values()).map(new Function<GameScreen.GameMode, String>() {
            @Override
            public String apply(GameScreen.GameMode gameMode) {
                return gameMode.getTranslation();
            }
        }).toArray(String[]::new));
        timesInMinutes = new Array<>(new String[]{"1 минута", "2 минуты", "3 минуты", "5 минут", "10 минут", "15 минут"});
    }

    @Override
    public void show() {
        if(!screenUsedBefore) {
            backButton = new BackButton(LKGame.Screen.MULTIPLAYER);
            backButton.setPosition(10, stage.getHeight() - backButton.getHeight() - 15);

            Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);

            infoLabel = new Label("Создание нового сервера", labelStyle);
            infoLabel.setPosition(30, stage.getHeight() - 60);
            infoLabel.setFontScale(20 / LKGame.getDefaultFont().getXHeight());

            infoModeLabel = new Label("Выбор режима", labelStyle);
            infoModeLabel.setPosition(30, stage.getHeight() - 100);

            chooseMode = new HorizontalGroup();
            chooseMode.setPosition(30, stage.getHeight() - 120);

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
                    if (indexOfCurrentText > 0) {
                        boolean visible = modeStrings.get(indexOfCurrentText - 1).equals("Игрок против игрока");
                        chooseTime.setVisible(visible);
                        infoTimeLabel.setVisible(visible);
                        modeLabel.setText(modeStrings.get(indexOfCurrentText - 1));
                    }else{
                        modeLabel.setText(modeLabel.getText());
                    }
                }
            });
            rightButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    int indexOfCurrentText = modeStrings.indexOf(modeLabel.getText().toString(), false);
                    if (indexOfCurrentText + 1 < modeStrings.size) {
                        boolean visible = modeStrings.get(indexOfCurrentText + 1).equals("Игрок против игрока");
                        chooseTime.setVisible(visible);
                        infoTimeLabel.setVisible(visible);
                        modeLabel.setText(modeStrings.get(indexOfCurrentText + 1));
                    }else {
                        modeLabel.setText(modeLabel.getText());
                    }
                }
            });

            infoTimeLabel = new Label("Длительность", labelStyle);
            infoTimeLabel.setVisible(false);
            infoTimeLabel.setPosition(30, stage.getHeight() - 160);

            chooseTime = new HorizontalGroup();
            chooseTime.setVisible(false);
            chooseTime.setPosition(30, stage.getHeight() - 175);
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
            createButton.setPosition(30, stage.getHeight() - 210);

            createButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    GameScreen.GameMode gameMode = GameScreen.GameMode.DUNGEON;

                    LKGame.setMap(LKGame.getMapManager().getRandomMap(GameScreen.GameMode.getModeByTranslation(modeLabel.getText().toString())));
                    LKGame.getMultiplayer().startListeningForClientConnections();
                    LKGame.getMultiplayerManagerThread().onServerStart(Integer.parseInt(timeLabel.getText().toString().split(" ")[0]) * 60);
                }
            });
        }
        stage.addActor(backButton);
        stage.addActor(infoLabel);
        stage.addActor(infoModeLabel);
        stage.addActor(chooseMode);
        stage.addActor(infoTimeLabel);
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