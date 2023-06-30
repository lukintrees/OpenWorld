package com.lukin.openworld.entities;

import com.badlogic.gdx.math.Vector2;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.EnemyComponent;
import com.lukin.openworld.components.EnemyHearingComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.SteeringComponent;
import com.lukin.openworld.components.WeaponPlayerComponent;
import com.lukin.openworld.utils.EntityLoader;

public class Enemy extends LKEntity {
    public Enemy(int entityId, int weaponId){
        super();
        this.entityID = entityId;
        this.weaponID = weaponId;
        EntityLoader entityLoader = LKGame.getEntityLoader();
        add(new HitboxComponent());
        HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
        add(new SteeringComponent(hitboxComponent));
        add(new EnemyHearingComponent());
        add(new EnemyComponent());
        EntityComponent entityComponent = addAndReturn(new EntityComponent(EntityComponent.EntityType.ENEMY, EntityComponent.EntityState.NEUTRAL));
        float health = 60 * LKGame.getDifficultyManager().getDifficultyRate();
        entityComponent.setHealth(health);
        entityComponent.setMaxHealth(health);
        AnimationComponent animationComponent = addAndReturn(new AnimationComponent());
        animationComponent.animation = LKGame.getAssetManager().get(entityLoader.getEntity(entityId).animation);
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        EntityLoader.WeaponJson weapon = entityLoader.getWeapon(weaponId);
        weaponComponent.texture = LKGame.getAssetManager().get(weapon.texture);
        weaponComponent.bulletTexture = LKGame.getAssetManager().get(weapon.bulletTexture);
        weaponComponent.delayFromAttackBasic = weapon.delayFromAttack;
    }

    public Enemy(int entityUID, int entityId, int weaponId, boolean direction, Vector2 position, float animationTime) {
        super();
        this.entityUID = entityUID;
        this.entityID = entityId;
        this.weaponID = weaponId;
        EntityLoader entityLoader = LKGame.getEntityLoader();
        HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
        hitboxComponent.setPosition(position);
        EntityComponent entityComponent = addAndReturn(new EntityComponent(EntityComponent.EntityType.ENEMY, EntityComponent.EntityState.NEUTRAL));
        entityComponent.health = 50f;
        entityComponent.maxHealth = 50f;
        entityComponent.direction = direction;
        AnimationComponent animationComponent = addAndReturn(new AnimationComponent());
        animationComponent.animation = LKGame.getAssetManager().get(entityLoader.getEntity(entityId).animation);
        animationComponent.animationTime = animationTime;
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        EntityLoader.WeaponJson weapon = entityLoader.getWeapon(weaponId);
        weaponComponent.texture = LKGame.getAssetManager().get(weapon.texture);
        weaponComponent.bulletTexture = LKGame.getAssetManager().get(weapon.bulletTexture);
    }
}
