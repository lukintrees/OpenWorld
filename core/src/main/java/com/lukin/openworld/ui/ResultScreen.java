package com.lukin.openworld.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.Mode;
import com.lukin.openworld.utils.PvPMode;

public class ResultScreen implements Screen {
    private final BitmapFont font;
    private final Stage stage;
    private Label resultLabel;
    private Table resultTable;
    private GameScreen.GameMode enumMode;
    private Mode mode;
    private String win;
    private String whyGameStopped;

    public ResultScreen(String winString, String whyGameStopped, Mode mode) {
        this.whyGameStopped = whyGameStopped;
        this.stage = LKGame.getStage();
        this.font = LKGame.getDefaultFont();
        this.win = winString;
        enumMode = mode != null ? mode.getMode() : GameScreen.GameMode.DUNGEON;
        this.mode = mode;
    }

    @Override
    public void show() {
            resultTable = new Table();
            resultTable.bottom();

            FreeTypeFontGenerator fontGenerator = LKGame.getFontGenerator();
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 40;
            parameter.characters = LKGame.DEFAULT_CHARS_FOR_FONT;
            parameter.color = Color.WHITE;
            resultLabel = new Label(win, new Label.LabelStyle(fontGenerator.generateFont(parameter), Color.WHITE));
            resultTable.add(resultLabel).row();

            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
            Label whyGameStoppedLabel = new Label(whyGameStopped, labelStyle);
            resultTable.add(whyGameStoppedLabel).padBottom(30).row();

            if (enumMode == GameScreen.GameMode.PVP){
                PvPMode pvpMode = (PvPMode) mode;

                PvPMode.Team team1 = pvpMode.getTeams().get(0);
                Label team1Result = new Label((team1.name == null ? "Игрок 1" : team1.name) + ": " + team1.score, labelStyle);
                resultTable.add(team1Result).row();

                PvPMode.Team team2 = pvpMode.getTeams().get(1);
                Label team2Result = new Label((team2.name == null ? "Игрок 2" : team2.name) + ": " + team2.score, labelStyle);
                resultTable.add(team2Result).row();
            }else if (enumMode == GameScreen.GameMode.DUNGEON){

            }

            Label toGameScreenButton = new Label("На главный экран", labelStyle);
            resultTable.add(toGameScreenButton);

            resultTable.setPosition(stage.getWidth() / 2f - resultTable.getWidth() / 2f, 100);
            toGameScreenButton.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    LKGame.setScreen(LKGame.Screen.MAIN);
                }
            });
        stage.addActor(resultTable);
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
    }

    @Override
    public void dispose() {
    }
}
