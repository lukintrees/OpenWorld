package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;

public class EntityComponent implements Component {
    public enum EntityType {
        PLAYER,
        LOCAL_PLAYER,
        ENEMY
    }
    public enum EntityState {
        NEUTRAL,
        FOLLOW_PLAYER
    }
    public EntityComponent(EntityType entityType, EntityState entityState) {
        type = entityType;
        state = entityState;
    }
    public EntityType type;
    public EntityState state;
    public boolean direction;
    public float health = 200;
    public float maxHealth = 200;

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public EntityState getState() {
        return state;
    }

    public void setState(EntityState state) {
        this.state = state;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }
}
