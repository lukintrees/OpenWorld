package com.lukin.openworld.ai;

import com.badlogic.gdx.ai.utils.Collision;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LKRaycastCollisionDetector implements RaycastCollisionDetector<Vector2> {
    private final Array<Rectangle> rectangles;


    public LKRaycastCollisionDetector(Array<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    @Override
    public boolean collides(Ray<Vector2> ray) {
        for (Rectangle rectangle : rectangles) {
            if(Intersector.intersectSegmentRectangle(ray.start, ray.end, rectangle)){
                return true;
            }
        }
        return false;
    }

    public boolean findCollision(Collision<Vector2> outputCollision, Ray<Vector2> inputRay) {
        throw new UnsupportedOperationException("Метод не требует имплемитации");
    }
}
