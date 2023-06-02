package com.lukin.openworld.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;

/**
 Класс MapConnection используется для храниения соединений на графе {@link MapGraph}.
 */
public class MapConnection implements Connection<Vector2> {
    private final Vector2 startNode;
    private final Vector2 endNode;
    private final float dist;

    public MapConnection(Vector2 startNode, Vector2 endNode){
        this.startNode = startNode;
        this.endNode = endNode;
        float dx = startNode.x - endNode.x;
        float dy = startNode.y - endNode.y;
        dist = (float) Math.sqrt(dx * dx + dy * dy);
    }
    @Override
    public float getCost() {
        return dist;
    }

    @Override
    public Vector2 getFromNode() {
        return startNode;
    }

    @Override
    public Vector2 getToNode() {
        return endNode;
    }
}
