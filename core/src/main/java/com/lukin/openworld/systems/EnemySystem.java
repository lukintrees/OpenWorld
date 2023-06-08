package com.lukin.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.ai.LKFollowPath;
import com.lukin.openworld.ai.LKPathFinder;
import com.lukin.openworld.ai.LKRaycastCollisionDetector;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.EnemyComponent;
import com.lukin.openworld.components.EnemyHearingComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.EntityComponent.EntityType;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.InputComponent;
import com.lukin.openworld.components.SteeringComponent;
import com.lukin.openworld.components.WeaponPlayerComponent;
import com.lukin.openworld.entities.Bullet;
import com.lukin.openworld.entities.LKEntity;

public class EnemySystem extends EntitySystem implements EntityListener {
    private Array<Entity> entities;
    private final LKPathFinder pathFinder;
    private final Array<Entity> players;
    private final LKRaycastCollisionDetector collisionDetector;

    public EnemySystem(){
       pathFinder = LKPathFinder.createPathFinderByMap(LKGame.getMap());
       entities = new Array<>();
       players = new Array<>();
       collisionDetector = new LKRaycastCollisionDetector(pathFinder.getGraph().getRectangles());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            actHearingActivity(deltaTime, entity);
            WeaponPlayerComponent weaponPlayerComponent = entity.getComponent(WeaponPlayerComponent.class);
            weaponPlayerComponent.delayFromAttack -= deltaTime;
            if (entity.getComponent(EntityComponent.class).state == EntityComponent.EntityState.FOLLOW_PLAYER) {
                SteeringComponent sc = entity.getComponent(SteeringComponent.class);
                EnemyComponent enemyComponent = entity.getComponent(EnemyComponent.class);
                if (enemyComponent.target == null){
                    if (players.get(0) == null){
                        break;
                    }else{
                        enemyComponent.target = players.get(0);
                    }
                }
                HitboxComponent enemyHitbox = entity.getComponent(HitboxComponent.class);
                if (sc.currentMode != SteeringComponent.SteeringState.FOLLOW_PATH || sc.currentPath == null) {
                    sc.currentMode = SteeringComponent.SteeringState.FOLLOW_PATH;
                    sc.steeringBehavior = new LKFollowPath(entity, sc, collisionDetector);
                    ((LKFollowPath) sc.steeringBehavior).setDistanceFromTarget(32);
                }
                HitboxComponent targetHitbox = enemyComponent.target.getComponent(HitboxComponent.class);
                if (Vector2.dst2(enemyHitbox.x, enemyHitbox.y, targetHitbox.x, targetHitbox.y) > 10000) {
                    EntityComponent ec = entity.getComponent(EntityComponent.class);
                    sc.currentMode = SteeringComponent.SteeringState.NONE;
                    ec.state = EntityComponent.EntityState.NEUTRAL;
                    AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
                    animationComponent.animationTime = 0f;
                    EnemyHearingComponent ehc = entity.getComponent(EnemyHearingComponent.class);
                    ehc.hearingRectangle.setCenter(enemyHitbox.x, enemyHitbox.y);
                }
                if (targetHitbox.x != enemyComponent.lastPosition.x || targetHitbox.y != enemyComponent.lastPosition.y) {
                    Array<Vector2> path = pathFinder.findPath(enemyHitbox.x + 3, enemyHitbox.y + 3,
                            targetHitbox.x + 1, targetHitbox.y + 1);
                    sc.setCurrentPath(path);
                    enemyComponent.lastPosition.set(targetHitbox.x, targetHitbox.y);
                }
                if (!collisionDetector.collides(new Ray<>(new Vector2(enemyHitbox.x, enemyHitbox.y), new Vector2(targetHitbox.x + 1, targetHitbox.y + 1)))) {
                    setWeaponRotation(entity, enemyHitbox, targetHitbox, weaponPlayerComponent);
                    shootBullet(entity, enemyHitbox, targetHitbox, weaponPlayerComponent);
                }
                SteeringAcceleration<Vector2> sa = new SteeringAcceleration<>(new Vector2());
                sc.steeringBehavior.calculateSteering(sa);
                entity.getComponent(EntityComponent.class).direction = sa.linear.x <= 0;
                enemyHitbox.add(sa.linear);
                Rectangle hearingRectangle = entity.getComponent(EnemyHearingComponent.class).hearingRectangle;
                hearingRectangle.setPosition(hearingRectangle.x + sa.linear.y, hearingRectangle.y + sa.linear.y);
            }
        }
    }

    private void actHearingActivity(float deltaTime, Entity entity){
        EnemyHearingComponent ehc = entity.getComponent(EnemyHearingComponent.class);
        EntityComponent ec = entity.getComponent(EntityComponent.class);
        EnemyComponent enc = entity.getComponent(EnemyComponent.class);
        for(Entity player : players) {
            HitboxComponent playerHitbox = player.getComponent(HitboxComponent.class);
            if (ehc.hearingRectangle.contains(playerHitbox.x, playerHitbox.y)) {
                EnemyHearingComponent.AudibleProperties audibleProperties = ehc.audibleEntities.get(player);
                if (audibleProperties != null) {
                    if (audibleProperties.hearingTime > 0){
                        if (ec.state != EntityComponent.EntityState.FOLLOW_PLAYER){
                            ec.state = EntityComponent.EntityState.FOLLOW_PLAYER;
                            if(enc.target != audibleProperties.entity){
                                enc.target = audibleProperties.entity;
                                enc.lastPosition.set(0, 0);
                            }
                        }
                    }else{
                        audibleProperties.hearingTime += deltaTime;
                    }
                } else {
                    audibleProperties = new EnemyHearingComponent.AudibleProperties();
                    audibleProperties.entity = player;
                    audibleProperties.hearingTime += deltaTime;
                    ehc.audibleEntities.put(player, audibleProperties);
                }
            }else{
                EnemyHearingComponent.AudibleProperties audibleProperties = ehc.audibleEntities.get(player);
                if(audibleProperties != null && audibleProperties.hearingTime > 0){
                        audibleProperties.hearingTime -= deltaTime / 10;
                }
            }
        }
    }

    private void shootBullet(Entity owner, HitboxComponent ownerHitbox, HitboxComponent targetHitbox, WeaponPlayerComponent weaponPlayerComponent){
        if(weaponPlayerComponent.delayFromAttack >= 0) return;
        Bullet bullet = new Bullet(weaponPlayerComponent.bulletTexture, ((LKEntity) owner).weaponID);
        HitboxComponent bulletHitbox = bullet.getComponent(HitboxComponent.class);
        bulletHitbox.setPosition(ownerHitbox.x, ownerHitbox.y + ownerHitbox.height / 2);
        BulletComponent bulletComponent = bullet.getComponent(BulletComponent.class);
        EntityComponent entity = owner.getComponent(EntityComponent.class);
        float weaponOriginX = ownerHitbox.x + (entity.direction ? -20 : ownerHitbox.width - 10) + (entity.direction ? 32 - 8 : 0);
        float weaponOriginY = ownerHitbox.y - 2;
        float weaponLength = 14;
        float angle = MathUtils.atan2(targetHitbox.y - ownerHitbox.y, targetHitbox.x - ownerHitbox.x);
        float adjustedAngle = angle + (entity.direction ? 50 : -50);
        float bulletOffsetX = weaponLength * MathUtils.cos(adjustedAngle);
        float bulletOffsetY = weaponLength * MathUtils.sin(adjustedAngle);
        bulletHitbox.setPosition(weaponOriginX + bulletOffsetX, weaponOriginY + bulletOffsetY);
        bulletComponent.velocity.setAngleRad(angle);
        bulletComponent.textureRotation = angle * MathUtils.radiansToDegrees;
        bulletComponent.owner = (LKEntity) owner;
        getEngine().addEntity(bullet);
        weaponPlayerComponent.delayFromAttack = weaponPlayerComponent.delayFromAttackBasic + MathUtils.random(0.2f);
    }

    private void setWeaponRotation(Entity owner, HitboxComponent ownerHitbox, HitboxComponent targetHitbox, WeaponPlayerComponent weapon){
        EntityComponent entity = owner.getComponent(EntityComponent.class);
        weapon.weaponRotation = MathUtils.atan2(targetHitbox.y - ownerHitbox.y, targetHitbox.x - ownerHitbox.x) * MathUtils.radiansToDegrees;
        if (entity.direction){
            weapon.weaponRotation += 180;
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        EntityComponent ec = entity.getComponent(EntityComponent.class);
        if(ec == null) return;
        if(ec.type == EntityType.ENEMY){
            entities.add(entity);
        } else if (ec.type == EntityType.LOCAL_PLAYER || ec.type == EntityType.PLAYER) {
            players.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        EntityComponent ec = entity.getComponent(EntityComponent.class);
        if(ec == null) return;
        if(ec.type == EntityType.ENEMY){
            entities.removeValue(entity, true);
        } else if (ec.type == EntityType.LOCAL_PLAYER || ec.type == EntityType.PLAYER) {
            players.removeValue(entity, true);
        }
    }
}
