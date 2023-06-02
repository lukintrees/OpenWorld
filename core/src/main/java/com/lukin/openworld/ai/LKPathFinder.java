package com.lukin.openworld.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 Класс LKPathFinder предназначен для поиска пути на двумерном графе в пространстве.
 Поиск пути осуществляется с использованием алгоритма A* (A-star).
 Конструктор класса принимает граф ({@link MapGraph}), по которому будет производиться поиск пути.
 Статический метод {@link #createPathFinderByMap(TiledMap)} принимает объект TiledMap и создает граф на основе слоев "background" и "collision".
 Метод findPath принимает координаты начальной и конечной точек (x1, y1, x2, y2) или объекты Vector2 и возвращает массив узлов
 ({@link Vector2}), представляющих найденный путь. Если путь не найден, метод возвращает null.
 */
public class LKPathFinder {
    private final MapGraph graph;
    private static final Heuristic<Vector2> heuristic = (node, endNode) -> (new Vector2(node)).dst(endNode);
    private final IndexedAStarPathFinder<Vector2> pf;
    private final PathSmoother<Vector2, Vector2> pathSmoother;

    /**
     Создает объект LKPathFinder на основе графа MapGraph.
     @param graph Граф, на основе которого будет осуществляться поиск пути.
     */
    private LKPathFinder(MapGraph graph) {
        this.graph = graph;
        pf = new IndexedAStarPathFinder<>(graph);
        pathSmoother = new PathSmoother<>(new LKRaycastCollisionDetector(graph.getRectangles()));
    }
    /**
     Создает объект LKPathFinder на основе объекта TiledMap.
     Граф создается на основе слоев "background" и "collision" карты.
     @param map Объект TiledMap.
     @return Объект LKPathFinder.
     */
    public static LKPathFinder createPathFinderByMap(TiledMap map){
        Array<Rectangle> rectangles = MapAiUtils.getCollisionRectangles((TiledMapTileLayer) map.getLayers().get("collision"));
        Array<Vector2> nodes = MapAiUtils.getNodes((TiledMapTileLayer) map.getLayers().get("background"), (TiledMapTileLayer) map.getLayers().get("collision"));
        MapGraph graph = new MapGraph(nodes, rectangles);
        return new LKPathFinder(graph);
    }
    /**
     Находит путь на графе от точки (x1, y1) до точки (x2, y2).
     @param x1 Координата x начальной точки.
     @param y1 Координата y начальной точки.
     @param x2 Координата x конечной точки.
     @param y2 Координата y конечной точки.
     @return Массив узлов ({@link Vector2}), представляющих найденный путь, или null, если путь не найден.
     */
    public Array<Vector2> findPath(float x1, float y1, float x2, float y2){
        graph.setStartNode(x1, y1);
        graph.setEndNode(x2, y2);
        graph.updateStartPathConnections();
        graph.updateEndPathConnections();
        MapGraphPath path = new MapGraphPath();
        if (pf.searchNodePath(graph.getStartNode(), graph.getEndNode(), heuristic, path)){
            pathSmoother.smoothPath(path);
            return path.getNodes();
        }else{
            return null;
        }
    }
    /**
     Находит путь на графе от точки from до точки to.
     @param from Начальная точка ({@link Vector2}).
     @param to Конечная точка ({@link Vector2}).
     @return Массив узлов ({@link Vector2}), представляющих найденный путь, или null, если путь не найден.
     */
    public Array<Vector2> findPath(Vector2 from, Vector2 to){
        return findPath(from.x, from.y, to.x, to.y);
    }

    public MapGraph getGraph() {
        return graph;
    }
}
