package com.lukin.openworld.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lukin.openworld.LKGame;

public class BackButton extends HorizontalGroup {

    public BackButton(LKGame.Screen screenEnum){
        TextureRegion backImage = new TextureRegion(LKGame.getAssetManager().get("popular_icons/play.png", Texture.class));
        backImage.flip(true, false);
        addActor(new Image(backImage));
        Label backLabel = new Label("Назад", new Label.LabelStyle(LKGame.getDefaultFont(), Color.WHITE));
        addActor(backLabel);
        backLabel.addListener(new ClickListener(){
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                LKGame.setScreen(screenEnum);
            }
        });
        space(5);
    }
}
