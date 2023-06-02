package com.lukin.openworld.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.lukin.openworld.LKGame;

public class EntityLoader {
    private static final String ENTITY_FILE = "entities.json";
    private static final String WEAPON_FILE = "weapons.json";
    private final Array<EntityJson> entities;
    private final Array<WeaponJson> weapons;

    public EntityLoader() {
        String text = Gdx.files.internal(ENTITY_FILE).readString();
        JsonValue root = new JsonReader().parse(text);
        entities = new Array<>(root.size + 1);
        entities.add(null);
        for (JsonValue value : root.iterator()) {
            EntityJson entity = createEntityFromJsonValue(value);
            entities.add(entity);
        }
        text = Gdx.files.internal(WEAPON_FILE).readString();
        root = new JsonReader().parse(text);
        weapons = new Array<>(root.size + 1);
        weapons.add(null);
        for (JsonValue value : root.iterator()) {
            WeaponJson weapon = createWeaponFromJsonValue(value);
            weapons.add(weapon);
        }
    }

    public static int[][][] createAnimationArray(JsonValue animation) {
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0; i < animation.size; i++) {
            for (int j = 0; j < animation.get(i).size; j++) {
                if (maxWidth < animation.get(i).size) {
                    maxWidth = animation.get(i).size;
                }
                if (maxHeight < animation.get(i).get(j).size) {
                    maxHeight = animation.get(i).get(j).size;
                }
            }
        }
        int[][][] animationArray = new int[animation.size][maxWidth][maxHeight];
        for (int i = 0; i < animation.size; i++) {
            for (int j = 0; j < animation.get(i).size; j++) {
                for (int k = 0; k < animation.get(i).get(j).size; k++) {
                    animationArray[i][j][k] = animation.get(i).get(j).getInt(k) + 1;
                }
            }
        }
        return animationArray;
    }

    private WeaponJson createWeaponFromJsonValue(JsonValue value) {
        WeaponJson weapon = new WeaponJson();
        weapon.id = value.getInt("id");
        weapon.name = value.getString("name");
        String[] textureString = value.getString("texture").split(":");
        if (textureString[0].equals("tile")) {
            weapon.texture = new int[]{Integer.parseInt(textureString[1])};
        } else if (textureString[0].equals("tiles")) {
            String[] numberss = textureString[1].split("-");
            int[] numbers = new int[numberss.length];
            for (int i = 0; i < numberss.length; i++) {
                numbers[i] = Integer.parseInt(numberss[i]);
            }
            weapon.texture = numbers;
        }
        return weapon;
    }

    private EntityJson createEntityFromJsonValue(JsonValue value) {
        EntityJson entity = new EntityJson();
        entity.id = value.getInt("id");
        entity.name = value.getString("name");
        entity.animation = createAnimationArray(value.get("animation"));
        return entity;
    }

    public static Texture loadWeaponTexture(int[] texture){
        TiledMapTileSets tileSets = LKGame.getMap().getTileSets();
        Pixmap tileSetPixmap = ImageUtils.getPixmapFromTileSet(tileSets);
        if (texture.length == 1){
            return ImageUtils.getSingleTileTexture(texture[0], tileSets, tileSetPixmap);
        } else {
            return new Texture("kalash.png");
        }

    }

    public Array<EntityJson> getEntities() {
        return entities;
    }

    public EntityJson getEntity(int id) {
        return entities.get(id);
    }

    public Array<WeaponJson> getWeapons(){
        return weapons;
    }

    public WeaponJson getWeapon(int id) {
        return weapons.get(id);
    }

    public static class EntityJson {
        public int id;
        public String name;
        public int[][][] animation;
    }

    public static class WeaponJson {
        public int id;
        public String name;
        public int[] texture;
    }
}
