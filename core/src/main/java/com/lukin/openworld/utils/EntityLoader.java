package com.lukin.openworld.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.lukin.openworld.LKGame;

import java.util.Arrays;

public class EntityLoader{
    private static final String ENTITY_FILE = "entities.json";
    private static final String WEAPON_FILE = "weapons.json";
    private static AssetManager assetManager;
    private static Class<Animation<Texture>> animationClass;
    private final Array<EntityJson> entities;
    private final Array<WeaponJson> weapons;

    public EntityLoader() {
        animationClass = (Class<Animation<Texture>>) new Animation<Texture>(0, new Array<>()).getClass();
        assetManager = LKGame.getAssetManager();
        assetManager.setLoader(Texture.class, ".weapon", new WeaponTextureLoader());
        assetManager.setLoader(animationClass, ".entity", new EntityAnimationLoader());
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
            weapon.texture = new AssetDescriptor<>(WEAPON_FILE + "/" + weapon.id + ".weapon", Texture.class,
                    new WeaponTextureLoaderParameters(new int[]{Integer.parseInt(textureString[1]) + 1}));
        } else if (textureString[0].equals("tiles")) {
            String[] numberss = textureString[1].split("-");
            int[] numbers = new int[numberss.length];
            for (int i = 0; i < numberss.length; i++) {
                numbers[i] = Integer.parseInt(numberss[i]) + 1;
            }
            weapon.texture = new AssetDescriptor<>(WEAPON_FILE + "/" + weapon.id + ".weapon", Texture.class,
                    new WeaponTextureLoaderParameters(numbers));
        }
        return weapon;
    }

    private EntityJson createEntityFromJsonValue(JsonValue value) {
        EntityJson entity = new EntityJson();
        entity.id = value.getInt("id");
        entity.name = value.getString("name");
        entity.animation = new AssetDescriptor<>(ENTITY_FILE + "/" + entity.id + ".entity", animationClass, new EntityAnimationLoaderParameters(createAnimationArray(value.get("animation"))));
        return entity;
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
        public AssetDescriptor<Animation<Texture>> animation;
    }

    public static class WeaponJson {
        public int id;
        public String name;
        public AssetDescriptor<Texture> texture;
    }

    public class EntityAnimationLoader extends SynchronousAssetLoader<Animation<Texture>, EntityAnimationLoaderParameters>{

        public EntityAnimationLoader() {
            super(new FileHandleResolver() {
                @Override
                public FileHandle resolve(String fileName) {
                    return null;
                }
            });
        }

        @Override
        public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, EntityAnimationLoaderParameters parameter) {
            return null;
        }

        @Override
        public Animation<Texture> load(AssetManager assetManager, String fileName, FileHandle file, EntityAnimationLoaderParameters parameter) {
            return ImageUtils.loadAnimation(parameter.animation);
        }
    }

    public static class EntityAnimationLoaderParameters extends AssetLoaderParameters<Animation<Texture>>{
        public int[][][] animation;

        public EntityAnimationLoaderParameters(int[][][] animation) {
            this.animation = animation;
        }
    }

    public static class WeaponTextureLoader extends SynchronousAssetLoader<Texture, WeaponTextureLoaderParameters>{

        public WeaponTextureLoader() {
            super(new FileHandleResolver() {
                @Override
                public FileHandle resolve(String fileName) {
                    return null;
                }
            });
        }

        @Override
        public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, WeaponTextureLoaderParameters parameter) {
            return null;
        }

        @Override
        public Texture load(AssetManager assetManager, String fileName, FileHandle file, WeaponTextureLoaderParameters parameter) {
            TiledMapTileSets tileSets = LKGame.getMap().getTileSets();
            Pixmap tileSetPixmap = ImageUtils.getPixmapFromTileSet(tileSets);
            if (parameter.texture.length == 1){
                return ImageUtils.getSingleTileTexture(parameter.texture[0], tileSets, tileSetPixmap);
            } else {
                return ImageUtils.getMultipleTilesTextureHorizontal(parameter.texture, tileSets, tileSetPixmap);
            }
        }


    }
    public static class WeaponTextureLoaderParameters extends AssetLoaderParameters<Texture>{
        public int[] texture;

        public WeaponTextureLoaderParameters(int[] texture) {
            this.texture = texture;
        }
    }
}
