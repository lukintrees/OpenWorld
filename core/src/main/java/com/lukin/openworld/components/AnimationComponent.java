package com.lukin.openworld.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationComponent implements Component{
    public Animation<Texture> animation;
    public float animationTime = 0;


    public void setAnimation(Animation<Texture> animation) {
        this.animation = animation;
    }
    public void addAnimationTime(float time){
        animationTime += time;
    }
    public void setAnimationTime(float animationTime) {
        this.animationTime = animationTime;
    }
}
