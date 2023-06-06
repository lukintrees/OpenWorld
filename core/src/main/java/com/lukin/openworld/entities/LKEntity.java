package com.lukin.openworld.entities;

import com.badlogic.ashley.core.Entity;
import com.lukin.openworld.components.HitboxComponent;


/**
 Класс LKEntity расширяет класс Entity для большей совместимости с {@link com.badlogic.gdx.scenes.scene2d.Actor} из Stage2d
 */
public class LKEntity extends Entity {
    private static int entitySequence = 1;
    public int entityUID;
    public int entityID;
    public int weaponID;

    public LKEntity(){
        entityUID = entitySequence;
        entitySequence++;
    }

    public void setBounds(float x, float y, float width, float height){
        HitboxComponent hitboxComponent = getComponent(HitboxComponent.class);
        hitboxComponent.setBounds(x, y, width, height);
    }

    public static void setEntitySequence(int sequence) {
        entitySequence = ++sequence;
    }

    public static int getEntitySequence(){
        return entitySequence;
    }
}
