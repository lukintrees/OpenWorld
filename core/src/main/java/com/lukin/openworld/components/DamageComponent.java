package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.lukin.openworld.entities.LKEntity;

public class DamageComponent implements Component {
    public float lifeTime = 5;
    public float damage = 10;
    public LKEntity owner;
}
