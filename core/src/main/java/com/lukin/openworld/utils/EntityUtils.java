package com.lukin.openworld.utils;

import com.badlogic.gdx.math.Vector2;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.entities.Bullet;
import com.lukin.openworld.entities.Enemy;
import com.lukin.openworld.entities.LKEntity;
import com.lukin.openworld.entities.RemotePlayer;

public class EntityUtils{
    private EntityUtils() {

    }

    public static StringBuilder serializeEntity(LKEntity entity) {
        /*
        Надо синхонизировать entities между сервером и клиентом
        Для клиента нужны entityUID, entityID, weaponID, entityComponent.direction, Vector2 position, animationComponent.animationTime
        Новое: health, weaponPlayerComponent.weaponRotation
        остальное это серверная логика
        */
        StringBuilder entityString = new StringBuilder();
        EntityComponent entityComponent = entity.getComponent(EntityComponent.class);
        if (entityComponent != null) {
            entityString.append(entityComponent.type == EntityComponent.EntityType.LOCAL_PLAYER ? EntityComponent.EntityType.PLAYER.toString() : entityComponent.type.toString());
            entityString.append("~");
            entityString.append(entity.entityUID);
            entityString.append("~");
            entityString.append(entity.entityID);
            entityString.append("~");
            entityString.append(entity.weaponID);
            entityString.append("~");
            entityString.append(entityComponent.direction);
            entityString.append("~");
            HitboxComponent hitboxComponent = entity.getComponent(HitboxComponent.class);
            entityString.append("(").append(hitboxComponent.x).append(",").append(hitboxComponent.y).append(")");
            entityString.append("~");
            AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
            entityString.append(animationComponent.animationTime);
            entityString.append("~");
            entityString.append(entityComponent.health);
        }
        return entityString;
    }

    public static StringBuilder serializeBullet(Bullet bullet){
        StringBuilder bulletString = new StringBuilder();
        BulletComponent bulletComponent = bullet.getComponent(BulletComponent.class);
        HitboxComponent hitboxComponent = bullet.getComponent(HitboxComponent.class);
        bulletString.append(bullet.entityUID);
        bulletString.append("~");
        bulletString.append(bullet.weaponID);
        bulletString.append("~");
        if(bulletComponent.owner != null){
            bulletString.append(bulletComponent.owner.entityUID);
        }else{
            bulletString.append("0");
        }
        bulletString.append("~");
        bulletString.append(bulletComponent.velocity.toString());
        bulletString.append("~");
        bulletString.append("(").append(hitboxComponent.x).append(",").append(hitboxComponent.y).append(")");
        bulletString.append("~");
        bulletString.append(bulletComponent.textureRotation);
        return bulletString;
    }

    public static Bullet deserializeBullet(String bullet){
        String[] bulletData = bullet.split("~");
        return new Bullet(Integer.parseInt(bulletData[0]), Integer.parseInt(bulletData[1]), Integer.parseInt(bulletData[2]), new Vector2().fromString(bulletData[3]), new Vector2().fromString(bulletData[4]), Float.parseFloat(bulletData[5]));
    }

    public static LKEntity deserializeEntity(String entity) {
        String[] entityData = entity.split("~");
        LKEntity entity1 = null;
        switch (EntityComponent.EntityType.valueOf(entityData[0])) {
            case LOCAL_PLAYER:
            case PLAYER:
                entity1 = new RemotePlayer(Integer.parseInt(entityData[1]), Integer.parseInt(entityData[2]), Integer.parseInt(entityData[3])
                        , entityData[4].equals("1"), new Vector2().fromString(entityData[5]), Float.parseFloat(entityData[6]));
                break;
            case ENEMY:
                entity1 = new Enemy(Integer.parseInt(entityData[1]), Integer.parseInt(entityData[2]), Integer.parseInt(entityData[3])
                        , entityData[4].equals("1"), new Vector2().fromString(entityData[5]), Float.parseFloat(entityData[6]));
        }
        LKEntity.setEntitySequence(Integer.parseInt(entityData[1]));
        return entity1;
    }

    public static void deserializeEntity(LKEntity entity,String entityStr){
        String[] entityData = entityStr.split("~");
        entity.weaponID = Integer.parseInt(entityData[3]);
        EntityComponent entityComponent = entity.getComponent(EntityComponent.class);
        entityComponent.direction = Boolean.parseBoolean(entityData[4]);
        entity.getComponent(HitboxComponent.class).setPosition(new Vector2().fromString(entityData[5]));
        entity.getComponent(AnimationComponent.class).animationTime = Float.parseFloat(entityData[6]);
        entityComponent.health = Float.parseFloat(entityData[7]);
    }
}
