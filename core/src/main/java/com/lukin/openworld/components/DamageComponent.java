package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class DamageComponent implements Component {
    public float lifeTime = 5;
    public float damage = 10;
    public Entity owner;

    public float getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }
}
