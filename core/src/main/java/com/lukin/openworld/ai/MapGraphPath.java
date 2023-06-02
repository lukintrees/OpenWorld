package com.lukin.openworld.ai;

import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

/**
 Класс MapGraphPath исплоьзуется для хранения пути в виде точек {@link Vector2}.
 Он реализует интерфейс SmootableGraphPath для того чтобы путь существ осуществлялся более плавно.
 */
public class MapGraphPath implements SmoothableGraphPath<Vector2, Vector2>{
    private final Array<Vector2> nodes = new Array<>();

    public Array<Vector2> getNodes() {
        return nodes;
    }

    @Override
    public int getCount() {
        return nodes.size;
    }

    @Override
    public Vector2 get(int index) {
        return nodes.get(index);
    }


    @Override
    public void add(Vector2 node) {
        nodes.add(node);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public void reverse() {
        nodes.reverse();
    }

    @Override
    public Vector2 getNodePosition(int index) {
        return nodes.get(index);
    }

    @Override
    public void swapNodes(int index1, int index2) {
        nodes.swap(index1, index2);
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.truncate(newLength);
    }

    @Override
    public Iterator<Vector2> iterator() {
        return nodes.iterator();
    }

    public LinePath<Vector2> convertToLinePath(){
        return new LinePath<>(nodes);
    }
}
