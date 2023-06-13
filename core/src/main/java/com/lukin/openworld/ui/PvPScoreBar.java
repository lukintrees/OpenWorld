package com.lukin.openworld.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.utils.PvPMode;

import java.util.Locale;

public class PvPScoreBar extends Actor {
    private PvPMode mode;
    private Table table;
    private Label timeLabel;
    private Label firstLabel;
    private Label secondLabel;

    public PvPScoreBar() {
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null && table == null){
            table = new Table();
            table.setBounds(getX(), getY(), getWidth(), getHeight());
            Label.LabelStyle labelStyle = new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE);
            firstLabel = new Label("Игрок 1  0", labelStyle);
            table.add(firstLabel).padRight(25);
            timeLabel = new Label("Время: 11:09", labelStyle);
            table.add(timeLabel).padRight(25);
            secondLabel = new Label("Игрок 2  0", labelStyle);
            table.add(secondLabel);
            stage.addActor(table);
        }
    }

    @Override
    public boolean remove() {
        boolean returned = super.remove();
        table.remove();
        return returned;
    }

    public void setMode(PvPMode mode) {
        this.mode = mode;
        Array<PvPMode.Team> teams = mode.getTeams();
        if (teams.size == 2){
            if (teams.get(0).name == null){
                teams.get(0).name = "Игрок 1";
            }
            if (teams.get(1).name == null){
                teams.get(1).name = "Игрок 2";
            }
            firstLabel.setText(teams.get(0).name + "  " + teams.get(0).score);
            secondLabel.setText(teams.get(1).name + "  " + teams.get(1).score);
        }
    }



    public void setTime(float time) {
        if(timeLabel == null) return;
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        String timeString = String.format(Locale.getDefault(),"%d:%02d", minutes, seconds);
        timeLabel.setText("Время: " + timeString);
    }
}
