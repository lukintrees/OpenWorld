package com.lukin.openworld.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lukin.openworld.components.EntityComponent;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class HealthBar extends Actor {
    private final EntityComponent player;
    private ShapeDrawer shapeDrawer;
    private boolean isLoaded;

    public HealthBar(EntityComponent entityComponent) {
        player = entityComponent;
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!isLoaded){
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.drawPixel(0, 0);
            Texture texture = new Texture(pixmap); //remember to dispose of later
            pixmap.dispose();
            TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
            shapeDrawer = new ShapeDrawer(getStage().getBatch(), region);
            isLoaded = true;
        }
        shapeDrawer.setColor(Color.GRAY);
        shapeDrawer.filledRectangle(getX(), getY(), getWidth(), getHeight());
        shapeDrawer.setColor(Color.RED);
        shapeDrawer.filledRectangle(getX(), getY(), getWidth() / (player.maxHealth / player.health), getHeight());
    }
}
