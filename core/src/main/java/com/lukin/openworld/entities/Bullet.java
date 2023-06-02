package com.lukin.openworld.entities;

import com.badlogic.gdx.graphics.Texture;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.components.TextureComponent;

public class Bullet extends LKEntity {
        public Bullet(Texture bulletTexture) {
            super();
            add(new BulletComponent());
            HitboxComponent hitboxComponent = addAndReturn(new HitboxComponent());
            hitboxComponent.setBounds(0, 0, bulletTexture.getWidth(), bulletTexture.getHeight());
            add(new TextureComponent(bulletTexture));
        }

}
