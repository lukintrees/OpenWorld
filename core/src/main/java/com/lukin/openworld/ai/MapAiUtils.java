package com.lukin.openworld.ai;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.HashSet;

/**
 Класс MapAiUtils используется для получения данных для постройки пути.
 */
public class MapAiUtils {


    /** Функция для получения всех тайлов ввиде {@link Rectangle} в определённом слое
     * @param collisionLayer Слой тайлов
     * @return Возращает массив {@link Rectangle} */
    public static Array<Rectangle> getCollisionRectangles(TiledMapTileLayer collisionLayer) {
        Array<Rectangle> rectangles = new Array<>();
        float tileWidth = collisionLayer.getTileWidth();
        float tileHeight = collisionLayer.getTileHeight();
        for (int x = 0;x < collisionLayer.getWidth();x++) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                if(collisionLayer.getCell(x, y) != null) {
                    rectangles.add(new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight));
                }
            }
        }
        return rectangles;
    }

    /**
     Возвращает массив вершин {@link Vector2} из {@link TiledMapTileLayer} для целей поиска пути.
     Вершины считаются допустимыми, если они смежны с тайлом столкновения и имеют по крайней мере один тайл земли
     смежный с ними по диагонали (чтобы позволить движение по диагонали).
     @param groundLayer слой тайлов, содержащий тайлы земли.
     @param collisionLayer слой тайлов, содержащий тайлы столкновения.
     @return Массив вершин {@link Vector2}.
     */
    public static Array<Vector2> getNodes(TiledMapTileLayer groundLayer, TiledMapTileLayer collisionLayer){
        HashSet<Vector2> nodesSet = new HashSet<>();
        int[] xs = new int[]{-1, 0, 0, 1};
        int[] ys = new int[]{0, 1, -1, 0};
        for (int x = 0; x < collisionLayer.getWidth(); x++) {
            for (int y = 0; y < collisionLayer.getHeight(); y++) {
                if(collisionLayer.getCell(x, y) != null){
                    for (int x1 = -1; x1 < 2; x1++) {
                        for (int y1 = -1; y1 < 2; y1++) {
                            if((x1 != 0 && y1 != 0) && (collisionLayer.getCell(x + x1, y + y1) == null && groundLayer.getCell(x + x1, y + y1) != null)){
                                int touches = 0;
                                for (int i = 0; i < xs.length; i++) {
                                    int x2 = xs[i];
                                    int y2 = ys[i];
                                    if(collisionLayer.getCell(x + x1 + x2, y + y1 + y2) != null){
                                        touches++;
                                    }
                                }
                                if(touches == 0 || touches == 2){
                                    nodesSet.add(new Vector2((x + x1) * 16, (y + y1) * 16));
                                }
                            }
                        }
                    }
                }
            }
        }
        Vector2[] nodes = new Vector2[nodesSet.size()];
        nodesSet.toArray(nodes);
        return new Array<>(nodes);
    }

    /**
     SonarLint так сказал
     */
    private MapAiUtils() {
        throw new IllegalStateException("Класс с утилитами");
    }
}
