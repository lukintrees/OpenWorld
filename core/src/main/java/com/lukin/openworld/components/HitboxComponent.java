package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class HitboxComponent implements Component {
    public float x = 0f;
    public float y = 0f;
    public float width = 0f;
    public float height = 0f;

    public HitboxComponent() {}

    public HitboxComponent(HitboxComponent other) {
        x = other.x;
        y = other.y;
        width = other.width;
        height = other.height;
    }

    public void setRectangle(Rectangle rectangle) {
        x = rectangle.x;
        y = rectangle.y;
        width = rectangle.width;
        height = rectangle.height;
    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void setPosition(Vector2 position){
        setPosition(position.x, position.y);
    }

    public void setPosition(HitboxComponent hitboxComponent){
        setPosition(hitboxComponent.x, hitboxComponent.y);
    }

    public Vector2 getPosition(){
        return new Vector2(x, y);
    }
    public boolean overlaps(Rectangle rectangle) {
        return x < rectangle.x + rectangle.width && x + width > rectangle.x && y < rectangle.y + rectangle.height && y + height > rectangle.y;
    }

    public boolean overlaps(HitboxComponent hitbox) {
        return x < hitbox.x + hitbox.width && x + width > hitbox.x && y < hitbox.y + hitbox.height && y + height > hitbox.y;
    }

    public void add(Vector2 v){
        x += v.x;
        y += v.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HitboxComponent) {
            HitboxComponent other = (HitboxComponent) obj;
            return x == other.x && y == other.y && width == other.width && height == other.height;
        }
        return false;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
