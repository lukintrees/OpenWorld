package com.lukin.openworld.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.DifficultyManager;

public class DifficultyChangeScreen implements Screen {
    private final Stage stage;
    private Table difficulties;
    private BackButton backButton;
    private Label infoLabel;

    public DifficultyChangeScreen(){
        this.stage = LKGame.getStage();
    }

    @Override
    public void show() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);

        infoLabel = new Label("Выберите сложности", labelStyle);
        infoLabel.setPosition(30, stage.getHeight() - 40);
        infoLabel.setFontScale(20 / LKGame.getDefaultFont().getXHeight());

        difficulties = new Table();
        difficulties.setPosition(250, stage.getHeight() / 2 - 10);
        difficulties.center();

        Label easyLabel = new Label("Лёгкая", labelStyle);
        difficulties.add(easyLabel).padRight(50);
        Label middleLabel = new Label("Средняя", labelStyle);
        difficulties.add(middleLabel).padRight(50);
        Label hardLabel = new Label("Сложная", labelStyle);
        difficulties.add(hardLabel).padRight(50);
        difficulties.row();

        Label easyInfoLabel = difficulties.add(new Label("Враги реже стреляют,\n чаще промахиваются", labelStyle)).padRight(20).getActor();
        easyInfoLabel.setFontScale(6 / LKGame.getDefaultFont().getXHeight());
        Label middleInfoLabel = difficulties.add(new Label("Враги чаще стреляют,\n меньше промахиваются", labelStyle)).padRight(20).getActor();
        middleInfoLabel.setFontScale(6 / LKGame.getDefaultFont().getXHeight());
        Label hardInfoLabel = difficulties.add(new Label("Враги часто стреляют,\n почти не промахиватся", labelStyle)).getActor();
        hardInfoLabel.setFontScale(6 / LKGame.getDefaultFont().getXHeight());

        ClickListener easyClickListener = new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                LKGame.getDifficultyManager().changeMode(DifficultyManager.DifficultyEnum.EASY);
                LKGame.setScreen(LKGame.Screen.MAIN);
            }
        };
        easyInfoLabel.addListener(easyClickListener);
        easyLabel.addListener(easyClickListener);

        ClickListener middleClickListener = new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                LKGame.getDifficultyManager().changeMode(DifficultyManager.DifficultyEnum.MIDDLE);
                LKGame.setScreen(LKGame.Screen.MAIN);
            }
        };
        middleInfoLabel.addListener(middleClickListener);
        middleLabel.addListener(middleClickListener);

        ClickListener hardClickListener = new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                LKGame.getDifficultyManager().changeMode(DifficultyManager.DifficultyEnum.HARD);
                LKGame.setScreen(LKGame.Screen.MAIN);
            }
        };
        hardInfoLabel.addListener(hardClickListener);
        hardLabel.addListener(hardClickListener);


        stage.addActor(infoLabel);
        stage.addActor(difficulties);
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
