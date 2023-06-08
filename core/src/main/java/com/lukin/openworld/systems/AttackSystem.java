package com.lukin.openworld.systems;


import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.SwordComponent;
import com.lukin.openworld.entities.Enemy;
import com.lukin.openworld.entities.LocalPlayer;

public class AttackSystem extends EntitySystem implements EntityListener {
    private final Array<Entity> damageEntities;
    private final Array<Entity> entities;
    private final TiledMapTileLayer collisionLayer;
    private final boolean isServer;

    public AttackSystem(boolean isServer) {
        damageEntities = new Array<>();
        entities = new Array<>();
        collisionLayer = (TiledMapTileLayer) LKGame.getMap().getLayers().get("collision");
        this.isServer = isServer;
    }

    @Override
    public void update(float deltaTime) {
        for (Entity damageEntity : damageEntities) {
            if (damageEntity.getComponent(BulletComponent.class) != null) {
                BulletComponent bulletComponent = damageEntity.getComponent(BulletComponent.class);
                bulletComponent.lifeTime -= deltaTime;
                if (bulletComponent.lifeTime < 0) {
                    getEngine().removeEntity(damageEntity);
                    continue;
                }
                HitboxComponent bulletHitbox = damageEntity.getComponent(HitboxComponent.class);
                for (Entity entity : entities) {
                    if (bulletComponent.owner == entity) continue;
                    HitboxComponent entityHitbox = entity.getComponent(HitboxComponent.class);
                    if (bulletHitbox.overlaps(entityHitbox)) {
                        if(entity instanceof Enemy) {
                            entity.getComponent(EntityComponent.class).state = EntityComponent.EntityState.FOLLOW_PLAYER;
                        }
                        if (entity instanceof LocalPlayer){
                            Gdx.input.vibrate(200);
                        }

                        EntityComponent entityComponent = entity.getComponent(EntityComponent.class);
                        entityComponent.health -= bulletComponent.damage;
                        if (entityComponent.health <= 0) {
                            getEngine().removeEntity(entity);
                            if (entity instanceof LocalPlayer){
                                LKGame.setScreen(LKGame.Screen.MAIN);
                                return;
                            }
                        }
                        getEngine().removeEntity(damageEntity);
                        break;
                    }

                }
                bulletHitbox.x += bulletComponent.velocity.x * deltaTime;
                bulletHitbox.y += bulletComponent.velocity.y * deltaTime;
                if (checkBulletCollision(bulletHitbox)) {
                    getEngine().removeEntity(damageEntity);
                }
            }else if (damageEntity.getComponent(SwordComponent.class) != null){
                //TODO
            }
        }
    }

    private boolean checkBulletCollision(HitboxComponent hitbox) {
        Vector2 pos = new Vector2(Math.round(hitbox.x/16), Math.round(hitbox.y/16));
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) pos.x + i, (int) (pos.y + j));
                if (cell != null) {
                    Rectangle rect = new Rectangle((int) (pos.x + i) * 16, (int) (pos.y + j) * 16, 16, 16);
                    if (hitbox.overlaps(rect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void entityAdded(Entity entity) {
        if (entity.getComponent(BulletComponent.class) != null || entity.getComponent(SwordComponent.class) != null) {
            damageEntities.add(entity);
            return;
        }
        if (entity.getComponent(EntityComponent.class)!= null) {
            entities.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (entity.getComponent(BulletComponent.class) != null || entity.getComponent(SwordComponent.class) != null) {
            damageEntities.removeValue(entity, true);
            return;
        }
        if (entity.getComponent(EntityComponent.class)!= null) {
            entities.removeValue(entity, true);
        }
    }
}
