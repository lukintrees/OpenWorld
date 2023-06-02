package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class EnemyHearingComponent implements Component {
    public Rectangle hearingRectangle = new Rectangle(0, 0, 160, 160);
    public HashMap<Entity, AudibleProperties> audibleEntities = new HashMap<>();
    public float timeToHear = 2f;
    public static class AudibleProperties {
        public Entity entity;
        public float hearingTime;
    }

    public Rectangle getHearingRectangle() {
        return hearingRectangle;
    }

    public void setHearingRectangle(Rectangle hearingRectangle) {
        this.hearingRectangle = hearingRectangle;
    }

    public HashMap<Entity, AudibleProperties> getAudibleEntities() {
        return audibleEntities;
    }

    public void setAudibleEntities(HashMap<Entity, AudibleProperties> audibleEntities) {
        this.audibleEntities = audibleEntities;
    }

    public float getTimeToHear() {
        return timeToHear;
    }

    public void setTimeToHear(float timeToHear) {
        this.timeToHear = timeToHear;
    }
}
