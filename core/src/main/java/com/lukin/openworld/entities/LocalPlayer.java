package com.lukin.openworld.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.InputComponent;
import com.lukin.openworld.components.WeaponPlayerComponent;
import com.lukin.openworld.utils.EntityLoader;

public class LocalPlayer extends LKEntity{

    public LocalPlayer(int entityId, int weaponId, InputComponent inputComponent){
        super();
        this.entityID = entityId;
        this.weaponID = weaponId;
        EntityLoader entityLoader = LKGame.getEntityLoader();
        add(new HitboxComponent());
        EntityComponent entityComponent = addAndReturn(new EntityComponent(EntityComponent.EntityType.LOCAL_PLAYER, EntityComponent.EntityState.NEUTRAL));
        entityComponent.setHealth(200f);
        entityComponent.setMaxHealth(200f);
        add(inputComponent);
        AnimationComponent animationComponent = addAndReturn(new AnimationComponent());
        animationComponent.animation = LKGame.getAssetManager().get(entityLoader.getEntity(entityId).animation);
        WeaponPlayerComponent weaponComponent = addAndReturn(new WeaponPlayerComponent());
        weaponComponent.texture = LKGame.getAssetManager().get(entityLoader.getWeapon(weaponId).texture);
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fillRectangle(0, 0, 2, 2);
        weaponComponent.bulletTexture = new Texture(pixmap);
        pixmap.dispose();
    }
}
