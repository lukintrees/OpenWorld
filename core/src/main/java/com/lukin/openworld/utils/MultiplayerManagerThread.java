package com.lukin.openworld.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.AnimationComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.entities.Enemy;
import com.lukin.openworld.entities.LKEntity;
import com.lukin.openworld.entities.LocalPlayer;
import com.lukin.openworld.entities.RemotePlayer;
import com.lukin.openworld.ui.GameScreen;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MultiplayerManagerThread extends Thread implements EntityListener {
    private final HashMap<Integer, String> syncEntities = new HashMap<>();
    private final Multiplayer multiplayer;
    private boolean isServer;
    private boolean isClientConfigured;
    private LocalPlayer localPlayer;
    private Engine engine;
    private final Map<Device, DeviceProperties> devices = new HashMap<>();

    public MultiplayerManagerThread(Multiplayer multiplayer) {
        this.multiplayer = multiplayer;
    }

    @Override
    public void run() {
        engine = LKGame.getEngine();
        multiplayer.enableMultiplayer();
        /*if (isServer) {
            multiplayer.startListeningForClientConnections();
        } else {
            boolean infinity = true;
            while (infinity) {
                for (Device device : multiplayer.getPairedConnections()) {
                    try {
                        if (multiplayer.connectToServerDevice(device)) {
                            infinity = false;
                            break;
                        }
                    } catch (RuntimeException ignored) {
                    }
                }
            }
        }*/
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
        String[] tmp = new String(data, 0, dataLen).split("\\|", 2);
        String type = tmp[0];
        String message = tmp[1];
        if (type.equals("get")){
            write(device, "logic".getBytes());
        }
        if (!isServer) {
            if (type.equals("sync")) {
                tmp = message.split("\\|");
                String[] entities = tmp[0].split("&");
                String mapId = tmp[1];
                //??? нужна ли эта строка ???
                //LKGame.invalidateScreen(LKGame.Screen.GAME);
                for (String entity : entities) {
                    deserializeEntity(entity);
                }
                LKGame.setMap(new TmxMapLoader().load("map/map-" + mapId + ".tmx"));
                if (LKGame.getScreens().get(LKGame.Screen.GAME) == null) {
                    LKGame.getScreens().put(LKGame.Screen.GAME, new GameScreen());
                }
                ((GameScreen) LKGame.getScreens().get(LKGame.Screen.GAME)).setServer(false);
                LKGame.setScreen(LKGame.Screen.GAME);
            } else if (type.equals("remove")) {
                LKEntity[] entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                int uidForRemove = Integer.parseInt(message);
                for (LKEntity entity : entities) {
                    if (entity.entityUID == uidForRemove) {
                        engine.removeEntity(entity);
                    }
                }
            } else if (type.equals("update")) {
                LKEntity[] entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                int uidForUpdate = Integer.parseInt(message.split("-")[1]);
                for (LKEntity entity : entities) {
                    if (entity.entityUID == uidForUpdate) {
                        deserializeEntity(entity, message);
                        syncEntities.put(uidForUpdate, message);
                    }
                }
            }
        } else {
            if (type.equals("connected")) {
                StringBuilder msg = new StringBuilder("sync|");
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
                write(device, msg.toString().getBytes());
            }else if (type.equals("update")) {
                LKEntity[] entities = engine.getEntitiesFor(Family.all(EntityComponent.class).get()).toArray(LKEntity.class);
                int uidForUpdate = Integer.parseInt(message.split("-")[1]);
                for (LKEntity entity : entities) {
                    if (entity.entityUID == uidForUpdate) {
                        deserializeEntity(entity, message);
                        syncEntities.put(uidForUpdate, message);
                    }
                }
            }else if (type.equals("add")){
                Entity entity = deserializeEntity(message);
                if (entity instanceof RemotePlayer){
                    devices.get(device).player = (RemotePlayer) entity;
                }
                syncEntities.put(Integer.parseInt(message.split("-",3)[1]), message);
            }
        }

    }

    private StringBuilder serializeEntity(LKEntity entity) {
        /*
        Надо синхонизировать entities между сервером и клиентом
        Для клиента нужны entityUID, entityID, weaponID, entityComponent.direction, Vector2 position, animationComponent.animationTime
        остальное это серверная логика
        */
        StringBuilder entityString = new StringBuilder();
        EntityComponent entityComponent = entity.getComponent(EntityComponent.class);
        if (entityComponent != null) {
            entityString.append(entityComponent.type.toString());
            entityString.append("-");
            entityString.append(entity.entityUID);
            entityString.append("-");
            entityString.append(entity.entityID);
            entityString.append("-");
            entityString.append(entity.weaponID);
            entityString.append("-");
            entityString.append(entityComponent.direction ? 1 : 0);
            entityString.append("-");
            HitboxComponent hitboxComponent = entity.getComponent(HitboxComponent.class);
            entityString.append("(").append(hitboxComponent.x).append(",").append(hitboxComponent.y).append(")");
            entityString.append("-");
            AnimationComponent animationComponent = entity.getComponent(AnimationComponent.class);
            entityString.append(animationComponent.animationTime);
        }
        return entityString;
    }

    private LKEntity deserializeEntity(String entity) {
        String[] entityData = entity.split("-");
        LKEntity entity1 = null;
        switch (EntityComponent.EntityType.valueOf(entityData[0])) {
            case LOCAL_PLAYER:
            case PLAYER:
                entity1 = new RemotePlayer(Integer.parseInt(entityData[1]), Integer.parseInt(entityData[2]), Integer.parseInt(entityData[3])
                        , entityData[4].equals("1"), new Vector2().fromString(entityData[5]), Float.parseFloat(entityData[6]));
                break;
            case ENEMY:
                entity1 = new Enemy(Integer.parseInt(entityData[1]), Integer.parseInt(entityData[2]), Integer.parseInt(entityData[3])
                        , entityData[4].equals("1"), new Vector2().fromString(entityData[5]), Float.parseFloat(entityData[6]));
        }
        engine.addEntity(entity1);
        LKEntity.setEntitySequence(Integer.parseInt(entityData[1]));
        return entity1;
    }

    private void deserializeEntity(LKEntity entity,String entityStr){
        String[] entityData = entityStr.split("-");
        entity.weaponID = Integer.parseInt(entityData[3]);
        entity.getComponent(EntityComponent.class).direction = entityData[4].equals("1");
        entity.getComponent(HitboxComponent.class).setPosition(new Vector2().fromString(entityData[5]));
        entity.getComponent(AnimationComponent.class).animationTime = Float.parseFloat(entityData[6]);
    }

    public void write(Device device, byte[] data) {
        OutputStream outputStream = devices.get(device).outputStream;
        if (outputStream != null) {
            try {
                outputStream.write(0x01);
                outputStream.write(data);
                outputStream.write(0x04);
            } catch (IOException e) {
                //ignore
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
                    byte[] msg = "update|".concat(entityString1.toString()).getBytes();
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
            if (syncEntities.get(localPlayer.entityUID) == null || !syncEntities.get(localPlayer.entityUID).equals(serializedLocalPlayer)){
                syncEntities.put(localPlayer.entityUID, serializedLocalPlayer);
                byte[] msg = "update|".concat(serializedLocalPlayer).getBytes();
                for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                    write(device.getKey(), msg);
                }
            }
        }
    }

    @Override
    public void entityAdded(Entity entity) {
        if (isServer && entity.getComponent(EntityComponent.class) != null) {
            LKEntity lkEntity = (LKEntity) entity;
            StringBuilder entityString = serializeEntity(lkEntity);
            byte[] msg = ("add|" + entityString).getBytes();
            for (Map.Entry<Device, DeviceProperties> device : devices.entrySet()) {
                if (device.getValue().player != entity){
                    write(device.getKey(), msg);
                    syncEntities.put(lkEntity.entityUID, entityString.toString());
                }
            }
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        if (isServer && entity.getComponent(EntityComponent.class) != null) {
            LKEntity lkEntity = (LKEntity) entity;
            byte[] msg = ("remove|" + lkEntity.entityUID).getBytes();
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
