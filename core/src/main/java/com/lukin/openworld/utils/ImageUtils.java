package com.lukin.openworld.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.lukin.openworld.LKGame;

public class ImageUtils {

    private ImageUtils(){

    }

    public static Animation<Texture> loadAnimation(int[][][] tilesId) {
        return loadAnimation(tilesId, LKGame.getMap().getTileSets(), 0.25f);
    }

    public static Animation<Texture> loadAnimation(int[][][] tilesID, TiledMapTileSets tileSets, float frameDuration) {
        Texture[] frames = new Texture[tilesID.length];
        Pixmap tileSetPixmap = getPixmapFromTileSet(tileSets);
        for (int i = 0; i < tilesID.length; i++) {
            if (tilesID[i].length == 1) {
                if (tilesID[i][0].length == 1) {
                    frames[i] = getSingleTileTexture(tilesID[i][0][0], tileSets, tileSetPixmap);
                } else {
                    frames[i] = getMultipleTilesTexture(tilesID[i][0], tileSets, tileSetPixmap);
                }
            }
        }
        return new Animation<>(frameDuration, frames);
    }

    public static Pixmap getPixmapFromTileSet(TiledMapTileSets tileSets) {
        TextureData temp = tileSets.getTile(1).getTextureRegion().getTexture().getTextureData();
        temp.prepare();
        return temp.consumePixmap();
    }

    public static Texture getSingleTileTexture(int tileID, TiledMapTileSets tileSets, Pixmap tileSetPixmap) {
        TextureRegion tileTexture = tileSets.getTile(tileID).getTextureRegion();
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(tileSetPixmap, 0, 0, tileTexture.getRegionX(), tileTexture.getRegionY(), tileTexture.getRegionWidth(), tileTexture.getRegionHeight());
        Texture texture = new Texture(pixmap);
        return texture;
    }

    public static Texture getMultipleTilesTexture(int[] tilesID, TiledMapTileSets tileSets, Pixmap tileSetPixmap) {
        Pixmap pixmap = new Pixmap(16, tilesID.length * 16, Pixmap.Format.RGBA8888);
        for (int j = 0; j < tilesID.length; j++) {
            TextureRegion tileTexture = tileSets.getTile(tilesID[j]).getTextureRegion();
            pixmap.drawPixmap(tileSetPixmap,
                    tileTexture.getRegionX(), tileTexture.getRegionY(),
                    16, 16,
                    0, (tilesID.length - j - 1) * 16, 16, 16);
        }
        Texture texture = new Texture(pixmap);
        return texture;
    }

    public static Texture getMultipleTilesTextureHorizontal(int[] tilesID, TiledMapTileSets tileSets, Pixmap tileSetPixmap){
        Pixmap pixmap = new Pixmap(tilesID.length * 16, 16, Pixmap.Format.RGBA8888);
        for (int j = 0; j < tilesID.length; j++) {
            TextureRegion tileTexture = tileSets.getTile(tilesID[j]).getTextureRegion();
            pixmap.drawPixmap(tileSetPixmap,
                    tileTexture.getRegionX(), tileTexture.getRegionY(),
                    16, 16,
                    (tilesID.length - j - 1) * 16, 0, 16, 16);
        }
        return new Texture(pixmap);
    }

    public static Texture makeTextureDarker(Texture texture, float factor) {
        Pixmap pixmap = new Pixmap(texture.getWidth(), texture.getHeight(), texture.getTextureData().getFormat());
        texture.getTextureData().prepare();
        pixmap.drawPixmap(texture.getTextureData().consumePixmap(), 0, 0);

        for (int x = 0; x < pixmap.getWidth(); x++) {
            for (int y = 0; y < pixmap.getHeight(); y++) {
                Color color = new Color(pixmap.getPixel(x, y));
                color.mul(factor);
                pixmap.setColor(color);
                pixmap.drawPixel(x, y);
            }
        }

        Texture newTexture = new Texture(pixmap);

        pixmap.dispose();

        return newTexture;
    }
}
