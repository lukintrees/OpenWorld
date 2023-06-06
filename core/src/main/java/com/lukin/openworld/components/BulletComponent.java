package com.lukin.openworld.components;

import com.badlogic.gdx.math.Vector2;

public class BulletComponent extends DamageComponent {
    public Vector2 velocity = new Vector2(100, 100);
    public float textureRotation;

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
}