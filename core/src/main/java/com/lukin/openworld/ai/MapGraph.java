package com.lukin.openworld.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 Класс MapGraph преднозначен для хранения вершин и соединений для поиска пути.
 Поиск пути осуществляется через класс {@link LKPathFinder}.
 */
public class MapGraph implements IndexedGraph<Vector2> {
    private final Array<Vector2> nodes;
    private final Array<Connection<Vector2>>[] connections;
    private final Connection<Vector2>[] otherStartConnection;
    private final Connection<Vector2>[] otherEndConnection;
    private Array<Connection<Vector2>> startConnection;
    private Array<Connection<Vector2>> endConnection;
    private final Vector2 startNode = new Vector2();
    private final Vector2 endNode = new Vector2();
    private final Array<Rectangle> rectangles;

    public MapGraph(Array<Vector2> nodes, Array<Rectangle> collisionRectangles) {
        this.nodes = nodes;
        this.rectangles = collisionRectangles;

        connections = new Array[nodes.size];
        otherStartConnection = new MapConnection[nodes.size];
        otherEndConnection = new MapConnection[nodes.size];
        for (int i = 0; i < nodes.size; i++) {
            Array<Connection<Vector2>> conns = new Array<>();
            for (int k = 0; k < nodes.size; k++) {
                //Исключаем проверки одной и той же вершины
                if (k != i){
                    Vector2 fromNode = nodes.get(i);
                    Vector2 toNode = nodes.get(k);
                    boolean isIntersected = false;
                    for (int p = 0; p < rectangles.size; p++) {
                        if (Intersector.intersectSegmentRectangle(fromNode, toNode, rectangles.get(p))){
                            isIntersected = true;
                            break;
                        }
                    }
                    if (!isIntersected){
                        conns.add(new MapConnection(fromNode, toNode));
                    }
                }
            }
            connections[i] = conns;
        }
    }

    /**
     Возвращает начальную вершину пути.
     @return Начальная вершина пути.
     */
    public Vector2 getStartNode() {
        return startNode;
    }
    /**
     Устанавливает координаты начальной вершины пути.
     @param x Координата X начальной вершины пути.
     @param y Координата Y начальной вершины пути.
     */
    public void setStartNode(float x, float y) {
        this.startNode.set(x, y);
    }
    /**
     Устанавливает начальную вершину пути.
     @param startNode Начальная вершина пути.
     */
    public void setStartNode(Vector2 startNode) {
        this.startNode.set(startNode);
    }
    /**
     Возвращает конечную вершину пути.
     @return Конечная вершина пути.
     */
    public Vector2 getEndNode() {
        return endNode;
    }
    /**
     Устанавливает координаты конечной вершины пути.
     @param x Координата X конечной вершины пути.
     @param y Координата Y конечной вершины пути.
     */
    public void setEndNode(float x, float y) {
        this.endNode.set(x, y);
    }

    /**
     Возращает вершины графа. Используется для откладочных целей.
     @return Вершины графа.
     */
    public Array<Vector2> getNodes() {
        return nodes;
    }
    /**
     Устанавливает конечную вершину пути.
     @param endNode Конечная вершина пути.
     */
    public void setEndNode(Vector2 endNode) {
        this.endNode.set(endNode);
    }

    /**
     Обновляет связи начальной вершины пути.
     Вызывает метод updatePathConnections для обновления связей начальной вершины пути.
     */
    public void updateStartPathConnections(){
        startConnection = updatePathConnections(startNode, endNode, otherStartConnection);
    }
    /**
     Обновляет связи конечной вершины пути.
     Вызывает метод updatePathConnections для обновления связей конечной вершины пути.
     */
    public void updateEndPathConnections(){
        endConnection = updatePathConnections(endNode, startNode, otherEndConnection);
    }
    /**
     Обновляет связи между двумя вершинами пути.
     Вычисляет все возможные соединения от fromVertex до других вершин и проверяет, пересекаются ли они с препятствиями.
     Для непересекающихся вершин создает соединения и добавляет их в массив связей.
     Также проверяет, можно ли добавить соединение между fromNode и toNode, и если возможно, добавляет его в массив связей.
     @param fromNode Начальная вершина пути.
     @param toNode Конечная вершина пути.
     @param otherConnection Массив связей между начальной/конечной вершиной и другими вершинами.
     @return Массив связей между начальной и конечной вершинами пути.
     */
    public Array<Connection<Vector2>> updatePathConnections(Vector2 fromNode, Vector2 toNode, Connection<Vector2>[] otherConnection) {
        //Вычисляем все возможные соединения от fromNode до других вершин
        Array<Connection<Vector2>> conns = new Array<>();
        for (int k = 0; k < nodes.size; k++) {
            Vector2 node = nodes.get(k);
            boolean isIntersected = false;
            for (int p = 0; p < rectangles.size; p++) {
                if (Intersector.intersectSegmentRectangle(fromNode, node, rectangles.get(p))){
                    isIntersected = true;
                    break;
                }
            }
            if (!isIntersected){
                conns.add(new MapConnection(fromNode, node));
                otherConnection[k] = new MapConnection(node, fromNode);
            }else{
                otherConnection[k] = null;
            }
        }
        //Вычисляем можно ли добавить соединение между fromNode до toNode
        boolean isIntersected = false;
        for (int p = 0; p < rectangles.size; p++) {
            if (Intersector.intersectSegmentRectangle(fromNode, toNode, rectangles.get(p))){
                isIntersected = true;
                break;
            }
        }
        if (!isIntersected){
            conns.add(new MapConnection(fromNode, toNode));
        }
        return conns;
    }

    /**
     Возвращает индекс узла в данном графе.
     Если узел является начальной вершиной, возвращает индекс, равный количеству вершин в графе.
     Если узел является конечной вершиной, возвращает индекс, равный количеству вершин в графе плюс 1.
     В противном случае возвращает индекс указанной вершины в массиве вершин данного графа.
     @param node Вершина, для которого необходимо получить индекс.
     @return Индекс указанного узла в массиве узлов данного графа.
     */
    @Override
    public int getIndex(Vector2 node) {
        if(node == startNode){
            return nodes.size;
        }else{
            if (node == endNode){
                return nodes.size + 1;
            }else{
                return nodes.indexOf(node, true);
            }
        }
    }

    /**
     Метод возращает все {@link Rectangle} по которым происходит проверка колизии.
     @return Массив {@link Rectangle}.
     */
    public Array<Rectangle> getRectangles() {
        return rectangles;
    }

    /**
     Метод возращает количество вершин в этом графе.
     @return количество вершин в графе.
     */
    @Override
    public int getNodeCount() {
        return nodes.size + 2;
    }

    /**
     Метод возвращает массив связей, соединенных с указанным узлом.
     Если указанный узел является начальным узлом, возвращает начальную связь.
     Если указанный узел является конечным узлом, возвращает конечную связь.
     В противном случае возвращает массив связей, соединенных с указанным узлом.
     @param fromNode Вершина, для которого необходимо получить связи.
     @return Массив связей, соединенных с указанной вершины.
     */
    @Override
    public Array<Connection<Vector2>> getConnections(Vector2 fromNode) {
        if(fromNode == startNode){
            return startConnection;
        } else if (fromNode == endNode) {
            return endConnection;
        }else{
            int index = nodes.indexOf(fromNode, true);
            Connection<Vector2> connToStart = otherStartConnection[index];
            Connection<Vector2> connToEnd = otherEndConnection[index];
            Array<Connection<Vector2>> newConns = new Array<>(connections[index]);
            if (connToStart != null){
                newConns.add(connToStart);
            }
            if (connToEnd != null){
                newConns.add(connToEnd);
            }
            return newConns;
        }
    }
}
