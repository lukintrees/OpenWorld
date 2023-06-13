package com.lukin.openworld.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.ui.GameScreen;

import java.util.Locale;
import java.util.Objects;

public class MapManager {
    private MapEntry[] maps;

    public MapManager() {
        AssetManager assetManager = LKGame.getAssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());

        FileHandle[] mapFiles;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            mapFiles = Gdx.files.internal("map").list();
        } else {
            mapFiles = Gdx.files.internal("./assets/map").list();
        }
        maps = new MapEntry[mapFiles.length + 1];

        for (FileHandle mapFile : mapFiles) {
            if (!mapFile.name().endsWith(".tmx")) {
                continue;
            }

            String fileName = mapFile.name();
            String[] parts = fileName.split("-");
            int id = Integer.parseInt(parts[1]);
            String mode = parts[2].split("\\.")[0];

            assetManager.load(mapFile.path(), TiledMap.class);
            assetManager.finishLoading();

            TiledMap map = assetManager.get(mapFile.path(), TiledMap.class);
            String playerSpawnsString = map.getProperties().get("playerSpawns", String.class);
            if (mode.equals("pvp")){
                String[] playerSpawnsParts = playerSpawnsString.split("\\|");
                Vector2[] playerSpawns = new Vector2[playerSpawnsParts.length];
                for (int i = 0; i < playerSpawnsParts.length; i++) {
                    playerSpawns[i] = new Vector2().fromString(playerSpawnsParts[i]);
                    playerSpawns[i].y = ((TiledMapTileLayer) map.getLayers().get("background")).getHeight() - playerSpawns[i].y;
                    playerSpawns[i].scl(16);
                }
                maps[id] = new MapEntry(map, new MapProperty(GameScreen.GameMode.valueOf(mode.toUpperCase()), playerSpawns));
            }else {
                maps[id] = new MapEntry(map, new MapProperty(GameScreen.GameMode.valueOf(mode.toUpperCase()), null));
            }
        }
    }

    public TiledMap getMap(int id) {
        return maps[id].getMap();
    }

    public MapEntry getMapEntry(int id) {
        return maps[id];
    }

    public MapProperty getMapProperty(int id) {
        return maps[id].getProperty();
    }

    public MapEntry[] getMaps() {
        return maps;
    }

    public MapProperty getMapProperty(TiledMap map) {
        return getMapProperty(map.getProperties().get("mapId", Integer.class));
    }

    public TiledMap getRandomMap(GameScreen.GameMode mode){
        Array<MapEntry> mapsByMode = new Array<>(maps.length);
        for(MapEntry entry : maps){
            if(entry != null && entry.getProperty().getMode() == mode){
                mapsByMode.add(entry);
            }
        }
        if (mapsByMode.notEmpty()){
            return mapsByMode.random().getMap();
        }else if (maps.length != 0){
            while(true){
                MapEntry randomEntry = maps[MathUtils.random(0, maps.length - 1)];
                if (randomEntry != null){
                    return randomEntry.getMap();
                }
            }
        }
        return null;
    }

    public static class MapEntry {
        private TiledMap map;
        private MapProperty property;

        public MapEntry(TiledMap map, MapProperty property) {
            this.map = map;
            this.property = property;
        }

        public TiledMap getMap() {
            return map;
        }

        public MapProperty getProperty() {
            return property;
        }
    }

    public static class MapProperty {
        private GameScreen.GameMode mode;
        private Vector2[] playerSpawns;

        public MapProperty(GameScreen.GameMode mode, Vector2[] playerSpawns) {
            this.mode = mode;
            this.playerSpawns = playerSpawns;
        }

        public GameScreen.GameMode getMode() {
            return mode;
        }

        public Vector2[] getPlayerSpawns() {
            return playerSpawns;
        }
    }
}
