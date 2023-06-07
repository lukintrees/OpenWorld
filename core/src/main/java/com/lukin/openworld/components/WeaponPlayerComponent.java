package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;

public class WeaponPlayerComponent implements Component {
    public Texture texture;
    public float weaponRotation;
    public float delayFromAttack = 0.3f;
    public float delayFromAttackBasic = 0.3f;
    public Texture bulletTexture;
}
