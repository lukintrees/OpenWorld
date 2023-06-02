package com.lukin.openworld.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.Vector2;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.SteeringComponent;
import com.lukin.openworld.entities.LKEntity;

public class LKFollowPath extends Arrive<Vector2> {
    private Vector2 targetPoint;
    private int currentWaypoint = 1;
    private final SteeringComponent owner;
    private final Entity entity;
    private final LKRaycastCollisionDetector collisionDetector;
    private float distanceFromTarget;

    public LKFollowPath(Entity entity, SteeringComponent owner, LKRaycastCollisionDetector collisionDetector) {
        super(owner);
        this.entity = entity;
        this.owner = owner;
        this.collisionDetector = collisionDetector;
        setDistanceFromTarget(50f);
    }

    @Override
    protected SteeringAcceleration<Vector2> calculateRealSteering(SteeringAcceleration<Vector2> steering) {
        if(owner.currentPath == null || owner.currentPath.size == 0) return steering;
        if(owner.isPathModified){
            currentWaypoint = 1;
            targetPoint = owner.currentPath.get(currentWaypoint);
            owner.isPathModified = false;
        }
        AnimationComponent animation = entity.getComponent(AnimationComponent.class);
        if(isArrived()) {
            animation.animationTime = 0f;
            if(++currentWaypoint < owner.currentPath.size){
                targetPoint = owner.currentPath.get(currentWaypoint);
            }else return steering;
        }
        if (owner.currentPath.peek().dst2(owner.getPosition()) < distanceFromTarget && !collisionDetector.collides(new Ray<>(owner.currentPath.peek(), owner.getPosition()))) {
            animation.animationTime = 0f;
            return steering;
        }
        animation.animationTime += Gdx.graphics.getDeltaTime();
        return arrive(steering, targetPoint);
    }

    public boolean isArrived() {
        if (owner.getPosition() == null || targetPoint == null) return true;
        Vector2 toTarget = new Vector2(targetPoint).sub(owner.getPosition());
        float distance = toTarget.len();
        return distance <= 1;
    }

    public float getDistanceFromTarget() {
        return (float) Math.sqrt(distanceFromTarget);
    }

    public void setDistanceFromTarget(float distanceFromTarget) {
        this.distanceFromTarget = distanceFromTarget * distanceFromTarget;
    }
}
