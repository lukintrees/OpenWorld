package com.lukin.openworld.utils;

import static com.lukin.openworld.utils.EntityUtils.deserializeBullet;
import static com.lukin.openworld.utils.EntityUtils.deserializeEntity;
import static com.lukin.openworld.utils.EntityUtils.serializeBullet;
import static com.lukin.openworld.utils.EntityUtils.serializeEntity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.BulletComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.entities.Bullet;
import com.lukin.openworld.entities.LKEntity;
import com.lukin.openworld.entities.LocalPlayer;
import com.lukin.openworld.entities.RemotePlayer;
import com.lukin.openworld.ui.GameScreen;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MultiplayerManagerThread extends Thread implements EntityListener {
    private final HashMap<Integer, String> syncEntities = new HashMap<>();
    private String searializedGameModeString;
    private final Multiplayer multiplayer;
    private boolean isServer;
    private LocalPlayer localPlayer;
    private Engine engine;
    private GameScreen.GameMode mode;
    private Mode gameMode;
    private final Map<Device, DeviceProperties> devices = new HashMap<>();

    public MultiplayerManagerThread(Multiplayer multiplayer) {
        this.multiplayer = multiplayer;
    }

    @Override
    public void run() {
        engine = LKGame.getEngine();
        multiplayer.enableMultiplayer();
    }

    public void onServerStart(int time){
        mode = LKGame.getMapManager().getMapProperty(LKGame.getMap()).getMode();
        if (mode == GameScreen.GameMode.PVP){
            gameMode = new PvPMode(1, 5);
        }
        isServer = true;
        LKGame.getScreens().put(LKGame.Screen.GAME, new GameScreen(
                gameMode,
                1,
                time
        ));
        LKGame.setScreen(LKGame.Screen.GAME);
    }

    public boolean isMultiplayer(){
        return !devices.isEmpty() || multiplayer.isStarted();
    }

    public void addDevice(Device device, OutputStream outputStream) {
        devices.put(device, new DeviceProperties(outputStream));
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    public void read(Device device, byte[] data, int dataLen) {
        String[] tmp = new String(data, 0, dataLen, StandardCharsets.UTF_8).split("\\|", 2);
        String type = tmp[0];
        String message = tmp[1];
        if (isServer) {
            handleServerPacket(device, type, message);
        } else {
            handleClientPacket(type, message);
        }

    }

    private void handleClientPacket(String type, String message) {
        String[] tmp;
        switch (type) {
            case "sync":
                tmp = message.split("\\|");
                LKEntity.setEntitySequence(Integer.parseInt(tmp[0]));
                String[] entitiesString = tmp[1].split("&");
                String mapId = tmp[2];
                mode = GameScreen.GameMode.valueOf(tmp[3]);
                LKGame.setMap(LKGame.getMapManager().getMap(Integer.parseInt(mapId)));
                if (mode == GameScreen.GameMode.PVP){
                    PvPMode pvpMode = new PvPMode();
                    pvpMode.deserialize(tmp[4]);
                    gameMode = pvpMode;
                    LKGame.getScreens().put(LKGame.Screen.GAME, new GameScreen(pvpMode, pvpMode.getPlayerCount(), Float.parseFloat(tmp[5])));
                }else{
                    LKGame.getScreens().put(LKGame.Screen.GAME, new GameScreen());
                }
                ((GameScreen) LKGame.getScreens().get(LKGame.Screen.GAME)).setServer(false);
                LKGame.setScreen(LKGame.Screen.GAME);
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for (String entity : entitiesString) {
                            LKEntity entity1 = deserializeEntity(entity);
                            engine.addEntity(entity1);
                        }
                    }
                });
                break;
            case "remove":
                LKEntity[] entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                int uidForRemove = Integer.parseInt(message);
                for (LKEntity entity : entities) {
                    if (entity.entityUID == uidForRemove) {
                        engine.removeEntity(entity);
                    }
                }
                break;
            case "update":
                entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                int uidForUpdate = Integer.parseInt(message.split("~")[1]);
                for (LKEntity entity : entities) {
                    if (entity.entityUID == uidForUpdate) {
                        deserializeEntity(entity, message);
                        syncEntities.put(uidForUpdate, message);
                    }
                }
                break;
            case "add":
                LKEntity entity = deserializeEntity(message);
                engine.addEntity(entity);
                break;
            case "add-b":
                Bullet bullet = deserializeBullet(message);
                engine.addEntity(bullet);
                break;
            case "set":
                tmp = message.split("\\|");
                if(gameMode == null){
                    gameMode = new PvPMode();
                }
                gameMode.deserialize(tmp[1]);
        }
    }

    private void handleServerPacket(Device device, String type, String message) {
        switch (type) {
            case "connected":
                StringBuilder msg = new StringBuilder("sync|");
                msg.append(LKEntity.getEntitySequence());
                msg.append("|");
                LKEntity[] entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                StringBuilder entityString = serializeEntity(entities[0]);
                msg.append(entityString);
                syncEntities.put(entities[0].entityUID, entityString.toString());
                for (int i = 1; i < entities.length; i++) {
                    msg.append("&");
                    entityString = serializeEntity(entities[i]);
                    msg.append(entityString);
                    syncEntities.put(entities[i].entityUID, entityString.toString());
                }
                msg.append("|");
                msg.append(LKGame.getMap().getProperties().get("mapId", 0, Integer.class));
                msg.append("|");
                msg.append(mode.toString());
                if (mode == GameScreen.GameMode.PVP){
                    msg.append("|");
                    msg.append(gameMode.serialize());
                }
                msg.append("|");
                msg.append(((GameScreen) LKGame.getScreens().get(LKGame.Screen.GAME)).getTime());
                write(device, msg.toString());
                break;
            case "update":
                entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                int uidForUpdate = Integer.parseInt(message.split("~")[1]);
                for (LKEntity entity : entities) {
                    if (entity.entityUID == uidForUpdate) {
                        deserializeEntity(entity, message);
                        syncEntities.put(uidForUpdate, message);
                    }
                }
                break;
            case "add":
                LKEntity entity = deserializeEntity(message);
                if (entity instanceof RemotePlayer) {
                    if (mode == GameScreen.GameMode.PVP && devices.get(device).player != null && devices.get(device).player.entityUID != entity.entityUID){
                        PvPMode pvpMode = (PvPMode) gameMode;
                        for (PvPMode.Team team : pvpMode.getTeams()){
                            if (team.entities.get(0) == devices.get(device).player.entityUID){
                                team.entities.set(0, entity.entityUID);
                            }
                        }
                    }
                    devices.get(device).player = (RemotePlayer) entity;
                }
                syncEntities.put(entity.entityUID, message.replace("P", "LOCAL_P"));
                engine.addEntity(entity);
                break;
            case "add-b":
                Bullet bullet = deserializeBullet(message);
                engine.addEntity(bullet);
                break;
            case "get":
                write(device, ("logic|" + mode.toString()));
                devices.remove(device);
                break;
        }
    }


    public synchronized void write(Device device, String data) {
        OutputStream outputStream = devices.get(device).outputStream;
        if (outputStream != null) {
            try {
                outputStream.write(0x01);
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.write(0x04);
            } catch (IOException e) {
                Gdx.app.error("TCP", new String(data), e);
                //devices.remove(device);
            }
        }
    }

    public void act() {
        if (isServer){
            LKEntity[] entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
            for (LKEntity entity : entities) {
                String entityString = syncEntities.get(entity.entityUID);
                if (entityString == null) return;
                StringBuilder entityString1 = serializeEntity(entity);
                if (!entityString.equals(entityString1.toString())) {
                    syncEntities.put(entity.entityUID, entityString1.toString());
                    String msg = "update|".concat(entityString1.toString());
                    for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                        if (device.getValue().player != entity){
                            write(device.getKey(), msg);
                        }
                    }
                }
            }
            if (mode == GameScreen.GameMode.PVP){
                String serializedGameMode = gameMode.serialize();
                if (!serializedGameMode.equals(searializedGameModeString)){
                    searializedGameModeString = serializedGameMode;
                    String msg = "set|teams|".concat(serializedGameMode);
                    for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                        write(device.getKey(), msg);
                    }
                }
            }
        }else{
            if (localPlayer == null){
                ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get());
                for (Entity entity : entities){
                    if (entity.getComponent(EntityComponent.class).type == EntityComponent.EntityType.LOCAL_PLAYER){
                        localPlayer = (LocalPlayer) entity;
                        break;
                    }
                }
                if (localPlayer == null) return;
            }
            String serializedLocalPlayer = serializeEntity(localPlayer).toString();
            if (syncEntities.get(localPlayer.entityUID) == null || !syncEntities.get(localPlayer.entityUID).equals(serializedLocalPlayer)) {
                syncEntities.put(localPlayer.entityUID, serializedLocalPlayer);
                String msg = "update|".concat(serializedLocalPlayer);
                for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                    write(device.getKey(), msg);
                }
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        if (entity.getComponent(EntityComponent.class) != null) {
            if (isServer) {
                LKEntity lkEntity = (LKEntity) entity;
                StringBuilder entityString = serializeEntity(lkEntity);
                String msg = ("add|" + entityString);
                for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                    if (device.getValue().player != entity) {
                        write(device.getKey(), msg);
                        syncEntities.put(lkEntity.entityUID, entityString.toString());
                    }
                }
                if (mode == GameScreen.GameMode.PVP){
                    PvPMode pvpMode = (PvPMode) gameMode;
                    pvpMode.getTeams().get(0).entities.set(0, lkEntity.entityUID);
                }
            }else if(entity instanceof LocalPlayer){
                localPlayer = (LocalPlayer) entity;
                LKEntity lkEntity = (LKEntity) entity;
                StringBuilder entityString = serializeEntity(lkEntity);
                String msg = ("add|" + entityString);
                for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                    write(device.getKey(), msg);
                    syncEntities.put(lkEntity.entityUID, entityString.toString());
                }
            }
        }
        if (entity.getComponent(BulletComponent.class) != null) {
            if (isServer) {
                Bullet bullet = (Bullet) entity;
                BulletComponent bulletComponent = bullet.getComponent(BulletComponent.class);
                StringBuilder entityString = serializeBullet(bullet);
                String msg = ("add-b|" + entityString);
                for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                    if (bulletComponent.owner == device.getValue().player) continue;
                    write(device.getKey(), msg);
                }
            }else {
                if (localPlayer == null){
                    ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get());
                    for (Entity entity1 : entities){
                        if (entity1.getComponent(EntityComponent.class).type == EntityComponent.EntityType.LOCAL_PLAYER){
                            localPlayer = (LocalPlayer) entity1;
                            break;
                        }
                    }
                    if (localPlayer == null) return;
                }
                Bullet bullet = (Bullet) entity;
                BulletComponent bulletComponent = bullet.getComponent(BulletComponent.class);
                StringBuilder entityString = serializeBullet(bullet);
                String msg = ("add-b|" + entityString);
                for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                    if (bulletComponent.owner == localPlayer){
                        write(device.getKey(), msg);
                    }
                }
            }
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (isServer && entity.getComponent(EntityComponent.class) != null) {
            LKEntity lkEntity = (LKEntity) entity;
            String msg = ("remove|" + lkEntity.entityUID);
            for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                write(device.getKey(), msg);
                syncEntities.remove(lkEntity.entityUID);
            }
        }
    }

    public static class Device {
        public String name;
        public String address;

        public Device(String address, String name) {
            this.address = address;
            this.name = name;
        }
    }

    public static class DeviceProperties {
        public OutputStream outputStream;
        public RemotePlayer player;

        public DeviceProperties(OutputStream outputStream) {
            this.outputStream = outputStream;
        }
    }
}
