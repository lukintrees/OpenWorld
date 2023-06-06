package com.lukin.openworld.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.SwordComponent;
import com.lukin.openworld.components.TextureComponent;

public class AttackRenderSystem extends EntitySystem implements EntityListener {
    private final Array<Entity> entities;
    private final SpriteBatch batch;

    public AttackRenderSystem() {
        entities = new Array<>();
        batch = LKGame.getBatch();
    }

    @Override
    public void update(float deltaTime) {
        for(Entity entity : entities){
            HitboxComponent hitbox = entity.getComponent(HitboxComponent.class);
            Texture texture = null;
            if(entity.getComponent(TextureComponent.class) != null){
                texture = entity.getComponent(TextureComponent.class).texture;
                if (texture == null) continue;
            } else if (entity.getComponent(AnimationComponent.class) != null) {
                AnimationComponent animation = entity.getComponent(AnimationComponent.class);
                texture = animation.animation.getKeyFrame(animation.animationTime, true);
            }

            if (entity.getComponent(BulletComponent.class) != null) {
                float rotation = entity.getComponent(BulletComponent.class).textureRotation;
                batch.draw(texture, hitbox.x, hitbox.y, 0, 0, hitbox.width, hitbox.height, 6, 6, rotation, 0, 0, 16, 16, false, false);
            }else{
                batch.draw(texture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
            }

        }
    }

    @Override
    public void entityAdded(Entity entity) {
        if(entity.getComponent(BulletComponent.class) != null || entity.getComponent(SwordComponent.class) != null){
            entities.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if(entity.getComponent(BulletComponent.class) != null || entity.getComponent(SwordComponent.class) != null){
            entities.removeValue(entity, true);
        }
    }
}
