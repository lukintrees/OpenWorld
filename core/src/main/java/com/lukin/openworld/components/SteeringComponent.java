package com.lukin.openworld.components;

import static com.badlogic.gdx.math.MathUtils.atan2;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SteeringComponent implements Component,Steerable<Vector2>{
    public enum SteeringState {FOLLOW_PATH,NONE} 	// a list of possible behaviours
    public SteeringState currentMode = SteeringState.NONE; 	// stores which state the entity is currently in
    public Array<Vector2> currentPath;
    // Steering data
    public HitboxComponent position;
    float maxLinearSpeed = 5f;	// stores the max speed the entity can go
    float maxLinearAcceleration = 1f;	// stores the max acceleration
    float maxAngularSpeed = 10f;		// the max turning speed
    float maxAngularAcceleration = 1f;// the max turning acceleration
    float zeroThreshold = 0.1f;
    public SteeringBehavior<Vector2> steeringBehavior; // stors the action behaviour
    private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<>(new Vector2()); // this is the actual steering vactor for our unit
    private boolean tagged = true;		// This is a generic flag utilized in a variety of ways. (never used this myself)
    //Костыли
    private Vector2 linearVelocity = new Vector2();
    private float angularVelocity = 0f;
    private float orientation = 0f;
    public boolean isPathModified;

    public SteeringComponent(HitboxComponent hitboxComponent){
        position = hitboxComponent;
    }

    @Override
    public Vector2 getPosition() {
        return position.getPosition();
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }
    @Override
    public float vectorToAngle(Vector2 vector) {
        return atan2(-vector.x, vector.y);
    }
    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.set(1, 0);
        outVector.rotateRad(angle);
        return outVector;
    }
    @Override
    public Location<Vector2> newLocation() {
        return new SteeringComponent(position);
    }
    @Override
    public float getZeroLinearSpeedThreshold() {
        return zeroThreshold;
    }
    @Override
    public void setZeroLinearSpeedThreshold(float value) {
        zeroThreshold = value;
    }
    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }
    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }
    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }
    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }
    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }
    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }
    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }
    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }
    public void setLinearVelocity(Vector2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }
    @Override
    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    @Override
    public float getAngularVelocity() {
        return angularVelocity;
    }
    @Override
    public float getBoundingRadius() {
        // the minimum radius size for a circle required to cover whole object
        float boundingRadius = 1f;
        return boundingRadius;
    }
    @Override
    public boolean isTagged() {
        return this.tagged;
    }
    @Override
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public void setCurrentPath(Array<Vector2> currentPath) {
        isPathModified = true;
        this.currentPath = currentPath;
    }
}
