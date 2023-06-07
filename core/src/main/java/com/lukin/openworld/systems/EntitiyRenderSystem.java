package com.lukin.openworld.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.WeaponPlayerComponent;

public class EntitiyRenderSystem extends EntitySystem implements EntityListener {
    private final Batch batch;
    private final ComponentMapper<HitboxComponent> hm = ComponentMapper.getFor(HitboxComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<EntityComponent> em = ComponentMapper.getFor(EntityComponent.class);
    private final ComponentMapper<WeaponPlayerComponent> wm = ComponentMapper.getFor(WeaponPlayerComponent.class);
    private final Array<Entity> entities;

    public EntitiyRenderSystem() {
        this.batch = LKGame.getBatch();
        entities = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            HitboxComponent hitbox = hm.get(entity);
            AnimationComponent animation = am.get(entity);
            EntityComponent entityComponent = em.get(entity);
            WeaponPlayerComponent weapon = wm.get(entity);
            Texture texture = animation.animation.getKeyFrame(animation.animationTime, true);
            batch.draw(texture, hitbox.x, hitbox.y, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), entityComponent.direction, false);
            if (weapon != null && weapon.texture != null) {
                if (weapon.texture.getWidth() == 16){
                    batch.draw(weapon.texture, hitbox.x + (entityComponent.direction ? -hitbox.width / 2 - 1 : hitbox.width / 2 + 1), hitbox.y + 3, 0, 0, 16, 16, 1f, 1f, weapon.weaponRotation, 0, 0, 16, 16, entityComponent.direction, false);
                }else {
                    batch.draw(weapon.texture, hitbox.x + (entityComponent.direction ? -20 : hitbox.getWidth() - 10), hitbox.y - 2,
                            entityComponent.direction ? 32 - 8 : 0, 0, 32, 16, 0.8f, 0.8f, weapon.weaponRotation, 0, 0, 32, 16, entityComponent.direction, false);
                }
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        if (em.has(entity)) {
            entities.add(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (em.has(entity)) {
            entities.removeValue(entity, true);
        }
    }
}
