package com.lukin.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.EntityComponent.EntityType;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.InputComponent;
import com.lukin.openworld.components.WeaponPlayerComponent;
import com.lukin.openworld.entities.Bullet;

public class LocalPlayerSystem extends EntitySystem implements EntityListener {
    private Entity localPlayer;
    private final TiledMap map;
    private final OrthographicCamera camera;
    private static final int SPEED_RATIO = 100;
    private boolean fistPlayerLoad;
    private final Vector2 levelExitPosition;

    public LocalPlayerSystem(OrthographicCamera camera) {
        this.camera = camera;
        map = LKGame.getMap();
        levelExitPosition = new Vector2(map.getProperties().get("exitX", Integer.class) * 16,
                (map.getProperties().get("height", Integer.class) - map.getProperties().get("exitY", Integer.class)) * 16);
    }

    @Override
    public void update(float deltaTime) {
        if(localPlayer != null) {
            if(fistPlayerLoad) loadLocalPlayer();
            updateLocalPlayerPosition(deltaTime);
            checkShootTouchpad();
            checkExit();
        }
    }

    public void checkExit(){
        if (levelExitPosition.dst2(localPlayer.getComponent(HitboxComponent.class).getPosition()) < 625){
            LKGame.setScreen(LKGame.Screen.MAIN);
        }
    }

    private Rectangle checkPosition(float addX, float addY, HitboxComponent hitbox) {
        Rectangle hitboxNew = new Rectangle(hitbox.x + addX, hitbox.y + addY, hitbox.width, hitbox.height);
        Vector2 pos = new Vector2(Math.round(hitboxNew.x/16), Math.round(hitboxNew.y/16));
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collision");
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                TiledMapTileLayer.Cell cell = layer.getCell((int) pos.x + i, (int) (pos.y + j));
                if (cell != null) {
                    Rectangle rect = new Rectangle((int) (pos.x + i) * 16, (int) (pos.y + j) * 16, 16, 16);
                    if (hitboxNew.overlaps(rect)) {
                        float overlapX = Math.min(hitboxNew.x + hitboxNew.width, rect.x + rect.width) - Math.max(hitboxNew.x, rect.x);
                        float overlapY = Math.min(hitboxNew.y + hitboxNew.height, rect.y + rect.height) - Math.max(hitboxNew.y, rect.y);
                        if (Math.abs(overlapX) < Math.abs(overlapY)) {
                            // horizontal collision
                            if (i < 0) {
                                // collision from the left
                                hitboxNew.x += overlapX;
                            } else {
                                // collision from the right
                                hitboxNew.x -= overlapX;
                            }
                        } else {
                            // vertical collision
                            if (j < 0) {
                                // collision from the top
                                hitboxNew.y += overlapY;
                            } else {
                                // collision from the bottom
                                hitboxNew.y -= overlapY;
                            }
                        }
                    }
                }
            }
        }

        return hitboxNew;
    }

    private void loadLocalPlayer(){
        HitboxComponent hitbox = localPlayer.getComponent(HitboxComponent.class);
        camera.position.x = hitbox.x;
        camera.position.y = hitbox.y;
        camera.update();
    }

    private void updateLocalPlayerPosition(float deltaTime) {
        InputComponent input = localPlayer.getComponent(InputComponent.class);
        if(input.touchpad.isTouched()){
            float addX = input.touchpad.getKnobPercentX() * deltaTime * SPEED_RATIO;
            float addY = input.touchpad.getKnobPercentY() * deltaTime * SPEED_RATIO;
            HitboxComponent hitbox = localPlayer.getComponent(HitboxComponent.class);
            Rectangle hitboxNew = checkPosition(addX, addY, hitbox);
            hitbox.x += hitboxNew.x - hitbox.x;
            hitbox.y += hitboxNew.y - hitbox.y;
            camera.position.set(hitbox.x, hitbox.y, 0);
            camera.update();
            localPlayer.getComponent(EntityComponent.class).direction = addX < 0;
            AnimationComponent animation = localPlayer.getComponent(AnimationComponent.class);
            animation.animationTime += deltaTime;
        }else {
            AnimationComponent animation = localPlayer.getComponent(AnimationComponent.class);
            animation.animationTime = 0f;
        }
    }

    private void checkShootTouchpad(){
        InputComponent input = localPlayer.getComponent(InputComponent.class);
        if(input.shootTouchpad.isTouched()){
            WeaponPlayerComponent weaponPlayerComponent = localPlayer.getComponent(WeaponPlayerComponent.class);
            weaponPlayerComponent.delayFromAttack -= Gdx.graphics.getDeltaTime();
            if(weaponPlayerComponent.delayFromAttack >= 0) return;
            EntityComponent entity = localPlayer.getComponent(EntityComponent.class);
            HitboxComponent hitbox = localPlayer.getComponent(HitboxComponent.class);
            Bullet bullet = new Bullet(weaponPlayerComponent.bulletTexture);
            HitboxComponent bulletHitbox = bullet.getComponent(HitboxComponent.class);
            bulletHitbox.setPosition(hitbox.x + (entity.direction ? 0 : hitbox.width), hitbox.y + hitbox.height / 2);
            BulletComponent bulletComponent = bullet.getComponent(BulletComponent.class);
            float angle = MathUtils.atan2(input.shootTouchpad.getKnobPercentY(), input.shootTouchpad.getKnobPercentX());
            bulletComponent.velocity.setAngleRad(angle);
            bulletComponent.textureRotation = angle * MathUtils.radiansToDegrees;
            bulletComponent.owner = localPlayer;
            getEngine().addEntity(bullet);
            weaponPlayerComponent.delayFromAttack = weaponPlayerComponent.delayFromAttackBasic;
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        EntityComponent entityComponent = entity.getComponent(EntityComponent.class);
        if (entityComponent != null && entityComponent.type == EntityType.LOCAL_PLAYER) {
            fistPlayerLoad = true;
            localPlayer = entity;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        EntityComponent entityComponent = entity.getComponent(EntityComponent.class);
        if (entityComponent != null && entityComponent.type == EntityType.LOCAL_PLAYER) {
            localPlayer = null;
        }
    }
}

