package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class InputComponent implements Component {
    public Touchpad touchpad;
    public Touchpad shootTouchpad;

    public Touchpad getTouchpad() {
        return touchpad;
    }

    public void setTouchpad(Touchpad touchpad) {
        this.touchpad = touchpad;
    }

    public Touchpad getShootTouchpad() {
        return shootTouchpad;
    }

    public void setShootTouchpad(Touchpad shootTouchpad) {
        this.shootTouchpad = shootTouchpad;
    }
}
