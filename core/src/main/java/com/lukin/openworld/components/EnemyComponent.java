package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public class EnemyComponent implements Component {
    public Entity target;
    public Vector2 lastPosition = new Vector2();

    public Entity getTarget() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }

    public Vector2 getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Vector2 lastPosition) {
        this.lastPosition = lastPosition;
    }
}
