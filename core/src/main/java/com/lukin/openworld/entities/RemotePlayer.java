package com.lukin.openworld.entities;

import com.badlogic.gdx.math.Vector2;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.WeaponPlayerComponent;
import com.lukin.openworld.utils.EntityLoader;

public class RemotePlayer extends LKEntity{


    public RemotePlayer(int entityUID, int entityId, int weaponId, boolean direction, Vector2 position, float animationTime){
        this.entityUID = entityUID;
        this.entityID = entityId;
        this.weaponID = weaponId;
        EntityLoader entityLoader = LKGame.getEntityLoader();
        HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
        hitboxComponent.setPosition(position);
        EntityComponent entityComponent = addAndReturn(new EntityComponent(EntityComponent.EntityType.PLAYER, EntityComponent.EntityState.NEUTRAL));
        entityComponent.health = 200f;
        entityComponent.maxHealth = 200f;
        entityComponent.direction = direction;
        AnimationComponent animationComponent = addAndReturn(new AnimationComponent());
        animationComponent.animation = LKGame.getAssetManager().get(entityLoader.getEntity(entityId).animation);
        animationComponent.animationTime = animationTime;
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        weaponComponent.texture = LKGame.getAssetManager().get(entityLoader.getWeapon(weaponId).texture);
    }

    public void setPosition(Vector2 position){
        HitboxComponent hitboxComponent = this.getComponent(HitboxComponent.class);
        hitboxComponent.setPosition(position);
    }

    public void setAnimationTime(float animationTime){
        AnimationComponent animationComponent = this.getComponent(AnimationComponent.class);
        animationComponent.animationTime = animationTime;
    }
}
