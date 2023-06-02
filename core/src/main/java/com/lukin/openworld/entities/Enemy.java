package com.lukin.openworld.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    @Deprecated
    public Enemy(Animation<Texture> animation, TextureRegion weaponTexture) {
        super();
        EntityComponent entityComponent = addAndReturn(new EntityComponent(EntityComponent.EntityType.ENEMY, EntityComponent.EntityState.NEUTRAL));
        entityComponent.setHealth(50f);
        entityComponent.setMaxHealth(50f);
        HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
        add(new SteeringComponent(hitboxComponent));
        add(new EnemyHearingComponent());
        add(new EnemyComponent());
        AnimationComponent animationComponent = addAndReturn(new AnimationComponent());
        animationComponent.setAnimation(animation);
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fillRectangle(0, 0, 2, 2);
        weaponComponent.bulletTexture = new Texture(pixmap);
        weaponComponent.texture = null;
    }

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
        entityComponent.setHealth(50f);
        entityComponent.setMaxHealth(50f);
        AnimationComponent animationComponent = addAndReturn(new AnimationComponent());
        animationComponent.animation = EntityLoader.loadEntityAnimation(entityLoader.getEntity(entityId).animation);
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        weaponComponent.texture = EntityLoader.loadWeaponTexture(entityLoader.getWeapon(weaponId).texture);
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fillRectangle(0, 0, 2, 2);
        weaponComponent.bulletTexture = new Texture(pixmap);
        pixmap.dispose();
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
        animationComponent.animation = EntityLoader.loadEntityAnimation(entityLoader.getEntity(entityId).animation);
        animationComponent.animationTime = animationTime;
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        weaponComponent.texture = EntityLoader.loadWeaponTexture(entityLoader.getWeapon(weaponId).texture);
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fillRectangle(0, 0, 2, 2);
        weaponComponent.bulletTexture = new Texture(pixmap);
        pixmap.dispose();
    }
}
