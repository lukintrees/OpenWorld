package com.lukin.openworld;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lukin.openworld.ui.DifficultyChangeScreen;
import com.lukin.openworld.ui.GameScreen;
import com.lukin.openworld.ui.MainScreen;
import com.lukin.openworld.ui.MultiplayerScreen;
import com.lukin.openworld.ui.ResultScreen;
import com.lukin.openworld.ui.ServerCreationScreen;
import com.lukin.openworld.utils.DifficultyManager;
import com.lukin.openworld.utils.MapManager;
import com.lukin.openworld.utils.MultiplayerManagerThread;
import com.lukin.openworld.utils.EntityLoader;
import com.lukin.openworld.utils.Multiplayer;
import com.lukin.openworld.utils.TCPMultiplayer;

import java.util.EnumMap;
import java.util.Map;


public class LKGame extends Game {
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;
    public static final String DEFAULT_CHARS_FOR_FONT = FreeTypeFontGenerator.DEFAULT_CHARS + "АБВГДЕЁЖЗИЙКЛМНОӨПРСТУҮФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуүфхцчшщъыьэюя";
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMap map;
    private Stage stage;
    private Engine engine;
    private EnumMap<Screen, com.badlogic.gdx.Screen> screens;
    private FreeTypeFontGenerator generator;
    private BitmapFont bitmapFont;
    private EntityLoader entityLoader;
    private Multiplayer multiplayer;
    private MultiplayerManagerThread multiplayerManagerThread;
    private AssetManager assetManager;
    private MapManager mapManager;
    private DifficultyManager difficultyManager;
    private static LKGame instance;


    @Override
    public void create() {
        instance = this;
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(15 * 16 * 1.78f, 15 * 16, camera);
        viewport.apply();
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader());
        entityLoader = new EntityLoader();
        difficultyManager = new DifficultyManager();
        map = new TmxMapLoader().load("map/map-1-dungeon.tmx");
        loadAllAssets();
        stage = new Stage(new FitViewport(20 * 16 * 1.78f, 20 * 16));
        Gdx.input.setInputProcessor(stage);
        engine = new Engine();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Black.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.characters = DEFAULT_CHARS_FOR_FONT;
        fontParameter.size = 16;
        fontParameter.color = Color.WHITE;
        bitmapFont = generator.generateFont(fontParameter);
        setMultiplayer(new TCPMultiplayer());
        screens = new EnumMap<>(Screen.class);
        MainScreen mainScreen = new MainScreen();
        screens.put(Screen.MAIN, mainScreen);
        setScreen(mainScreen);
    }

    private void loadAllAssets() {
        Array<EntityLoader.EntityJson> entities = entityLoader.getEntities();
        Array<EntityLoader.WeaponJson> weapons = entityLoader.getWeapons();
        for (EntityLoader.EntityJson entity : entities) {
            if (entity != null){
                assetManager.load(entity.animation);
            }

        }
        for (EntityLoader.WeaponJson weapon : weapons) {
            if(weapon != null) {
                assetManager.load(weapon.texture);
                if (weapon.bulletTexture != null){
                    assetManager.load(weapon.bulletTexture);
                }
            }
        }
        mapManager = new MapManager();
        assetManager.load("JoystickR.png", Texture.class);
        assetManager.load("KnobR.png", Texture.class);
        assetManager.load("popular_icons/play.png", Texture.class);
        assetManager.update();
        assetManager.finishLoading();
    }

    public static void setScreen(Screen screen) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                LKGame game = getInstance();
                if (screen == Screen.MAIN) {
                    if (game.screens.get(Screen.MAIN) == null) {
                        game.screens.put(Screen.MAIN, new MainScreen());
                    }
                    game.setScreen(game.screens.get(Screen.MAIN));
                } else if (screen == Screen.GAME) {
                    if (game.screens.get(Screen.GAME) == null) {
                        game.screens.put(Screen.GAME, new GameScreen());
                    }
                    game.setScreen(game.screens.get(Screen.GAME));
                } else if (screen == Screen.MULTIPLAYER){
                    if (game.screens.get(Screen.MULTIPLAYER) == null) {
                        game.screens.put(Screen.MULTIPLAYER, new MultiplayerScreen());
                    }
                    game.setScreen(game.screens.get(Screen.MULTIPLAYER));
                } else if (screen == Screen.SERVER_CREATION){
                    if (game.screens.get(Screen.SERVER_CREATION) == null) {
                        game.screens.put(Screen.SERVER_CREATION, new ServerCreationScreen());
                    }
                    game.setScreen(game.screens.get(Screen.SERVER_CREATION));
                } else if (screen == Screen.RESULT){
                    if (game.screens.get(Screen.RESULT) == null) {
                        //game.screens.put(Screen.RESULT, new ResultScreen(false, "undefined", null));
                        throw new IllegalStateException("Result screen is not defined");
                    }
                    game.setScreen(game.screens.get(Screen.RESULT));
                } else if (screen == Screen.DIFFICULTY_CHANGE) {
                    if (game.screens.get(Screen.DIFFICULTY_CHANGE) == null) {
                        game.screens.put(Screen.DIFFICULTY_CHANGE, new DifficultyChangeScreen());
                    }
                    game.setScreen(game.screens.get(Screen.DIFFICULTY_CHANGE));
                }
            }
        });
    }

    public static Map<Screen, com.badlogic.gdx.Screen> getScreens(){
        return getInstance().screens;
    }

    public static void invalidateScreen(Screen screen){
        LKGame game = getInstance();
        game.screens.remove(screen);
    }
    public static void setMultiplayer(Multiplayer multiplayer) {
        LKGame game = getInstance();
        game.multiplayer = multiplayer;
        game.multiplayerManagerThread = new MultiplayerManagerThread(multiplayer);
        game.multiplayerManagerThread.start();
    }
    public static MultiplayerManagerThread getMultiplayerManagerThread() {
        return getInstance().multiplayerManagerThread;
    }
    public static Multiplayer getMultiplayer(){
        return getInstance().multiplayer;
    }
    public static TiledMap getMap() {
        return getInstance().map;
    }
    public static EntityLoader getEntityLoader() {
        return getInstance().entityLoader;
    }

    public static SpriteBatch getBatch() {
        return getInstance().batch;
    }

    public static LKGame getInstance() {
        return instance;
    }

    public static Stage getStage() {
        return getInstance().stage;
    }
    public static Engine getEngine() {
        return getInstance().engine;
    }
    public static Viewport getViewport() {
        return getInstance().viewport;
    }
    public static FreeTypeFontGenerator getFontGenerator(){
        return getInstance().generator;
    }
    public static BitmapFont getDefaultFont(){
        return getInstance().bitmapFont;
    }
    public static AssetManager getAssetManager() {
        return getInstance().assetManager;
    }
    public static MapManager getMapManager() {
        return getInstance().mapManager;
    }
    public static DifficultyManager getDifficultyManager(){
        return getInstance().difficultyManager;
    }

    public static void setMap(TiledMap map){
        LKGame game = getInstance();
        game.map = map;
    }

    public enum Screen{
        MAIN,
        MULTIPLAYER,
        SERVER_CREATION,
        GAME,
        RESULT,
        DIFFICULTY_CHANGE
    }
}