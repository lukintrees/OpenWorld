package com.lukin.openworld.entities;

import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.TextureComponent;

import java.util.Arrays;

public class Bullet extends LKEntity {
        public Bullet(Texture bulletTexture, int weaponId) {
            super();
            this.weaponID = weaponId;
            add(new BulletComponent());
            HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
            if (bulletTexture != null){
                hitboxComponent.setBounds(0, 0, 4, 3);
            }else{
                hitboxComponent.setBounds(0, 0, 2, 2);
            }
            add(new TextureComponent(bulletTexture));
        }

        public Bullet(int entityUID, int weaponID, int ownerEntityUID, Vector2 velocity, Vector2 position, float textureRotation){
            this.entityUID = entityUID;
            this.weaponID = weaponID;
            Texture bulletTexture = LKGame.getAssetManager().get(LKGame.getEntityLoader().getWeapon(weaponID).bulletTexture);
            BulletComponent bulletComponent = addAndReturn(new BulletComponent());
            bulletComponent.textureRotation = textureRotation;
            bulletComponent.velocity = velocity;
            LKEntity[] entities = LKGame.getEngine().getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
            for (LKEntity entity : entities) {
                if (entity.entityUID == ownerEntityUID) {
                    bulletComponent.owner = entity;
                    break;
                }
            }
            HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
            hitboxComponent.setBounds(position.x, position.y, 4, 3);
            add(new TextureComponent(bulletTexture));

        }
}
