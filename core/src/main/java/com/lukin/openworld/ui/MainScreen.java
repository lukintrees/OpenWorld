package com.lukin.openworld.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lukin.openworld.LKGame;

public class MainScreen implements Screen {
    private final BitmapFont font;
    private final Stage stage;
    private Label gameLabel;
    private Table startButtons;
    private Label copyright;
    private boolean screenUsedBefore;

    public MainScreen() {
        this.stage = LKGame.getStage();
        this.font = LKGame.getDefaultFont();
    }

    @Override
    public void show() {
        if(!screenUsedBefore) {
            FreeTypeFontGenerator fontGenerator = LKGame.getFontGenerator();
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 70;
            parameter.characters = LKGame.DEFAULT_CHARS_FOR_FONT;
            parameter.color = Color.WHITE;
            gameLabel = new Label("LKMO", new Label.LabelStyle(fontGenerator.generateFont(parameter), Color.WHITE));
            gameLabel.setPosition(stage.getWidth() / 2f - gameLabel.getWidth() / 2f, 200);

            startButtons = new Table();

            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
            Label startButton = new Label("Начать приключение", labelStyle);
            startButtons.add(startButton).padRight(30);

            Label multiplayerButton = new Label("Сетевая игра", labelStyle);
            startButtons.add(multiplayerButton).padRight(30);

            Label difficultyChangeButton = new Label("Изменение сложности", labelStyle);
            startButtons.add(difficultyChangeButton);

            startButtons.setPosition(stage.getWidth() / 2f - startButtons.getWidth() / 2f, 100);

            copyright = new Label("©lukintrees origins. Thanks 0x72, VladPen", labelStyle);
            copyright.setPosition(0, 0);
            copyright.setFontScale(font.getXHeight() / 10);
            startButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LKGame.setScreen(LKGame.Screen.GAME);
                }
            });
            multiplayerButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LKGame.setScreen(LKGame.Screen.MULTIPLAYER);
                }
            });

            difficultyChangeButton.addListener(new ClickListener(){
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    LKGame.setScreen(LKGame.Screen.DIFFICULTY_CHANGE);
                }
            });
        }
        //LKGame.setMap(new TmxMapLoader().load("map/map-1.tmx"));
        LKGame.setMap(LKGame.getMapManager().getMap(MathUtils.random(1, 2)));
        stage.addActor(gameLabel);
        stage.addActor(startButtons);
        stage.addActor(copyright);
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