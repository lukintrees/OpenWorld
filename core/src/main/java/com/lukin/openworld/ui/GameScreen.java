package com.lukin.openworld.ui;

import static com.lukin.openworld.LKGame.DEFAULT_SCREEN_HEIGHT;
import static com.lukin.openworld.LKGame.DEFAULT_SCREEN_WIDTH;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.InputComponent;
import com.lukin.openworld.entities.LocalPlayer;
import com.lukin.openworld.systems.AttackRenderSystem;
import com.lukin.openworld.systems.AttackSystem;
import com.lukin.openworld.systems.EnemySpawnSystem;
import com.lukin.openworld.systems.EnemySystem;
import com.lukin.openworld.systems.EntitiyRenderSystem;
import com.lukin.openworld.systems.LocalPlayerSystem;
import com.lukin.openworld.utils.MultiplayerManagerThread;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final Stage stage;
    private final Viewport viewport;
    private final AssetManager assetManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;
    private final Engine engine;
    private TiledMapTileLayer backgroundLayer;
    private TiledMapTileLayer wallLayer;
    private Touchpad touchpad;
    private Touchpad shootTouchpad;
    private HealthBar healthBar;
    private MultiplayerManagerThread multiplayerManagerThread;
    private boolean isServer = true;
    private boolean isMultiplayer;
    private boolean firstResize;


    public GameScreen() {
        this.batch = LKGame.getBatch();
        this.stage = LKGame.getStage();
        this.viewport = LKGame.getViewport();
        this.engine = LKGame.getEngine();
        this.assetManager = LKGame.getAssetManager();
    }

    @Override
    public void show() {
            this.map = LKGame.getMap();
            this.mapRenderer = new OrthogonalTiledMapRenderer(map, batch);
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    mapRenderer.setView(viewport.getCamera().combined, 0, 0, DEFAULT_SCREEN_WIDTH + 16f, DEFAULT_SCREEN_HEIGHT + 16f);
                }
            });
            backgroundLayer = (TiledMapTileLayer) map.getLayers().get("background");
            wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
            multiplayerManagerThread = LKGame.getMultiplayerManagerThread();
            isMultiplayer = multiplayerManagerThread.isMultiplayer();
            firstResize = true;
            loadAshleySystems();
            InputComponent inputComponent = loadUIButtons();
            LocalPlayer player = loadBasicEntities(inputComponent);
            loadLocalPlayerUI(player);
            stage.addActor(touchpad);
            stage.addActor(shootTouchpad);
            stage.addActor(healthBar);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        mapRenderer.renderTileLayer(backgroundLayer);
        mapRenderer.renderTileLayer(wallLayer);
        engine.update(Gdx.graphics.getDeltaTime());
        batch.end();
        stage.act();
        stage.draw();
        if (isMultiplayer){
            multiplayerManagerThread.act();
        }
    }

    @Override
    public void resize(int width, int height) {
        //Из-за особенностей libgdx при переключении экранов, если загружать на этот экран из другово потоко,
        //то на компьютере будет вылетать,тк нельзя запускать gl команды в другом потоке, а на телефоне используется gl es, поэтому можно(хоть и с непредвиденным резултатом)
        if (firstResize){
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    stage.getViewport().update(width, height, true);
                    viewport.update(width, height, true);
                }
            });
            firstResize = false;
        }else{
            stage.getViewport().update(width, height, true);
            viewport.update(width, height, true);
        }
    }

    private void loadAshleySystems(){
        if (isServer){
            LocalPlayerSystem localPlayerSystem = new LocalPlayerSystem((OrthographicCamera) viewport.getCamera());
            engine.addSystem(localPlayerSystem);
            engine.addEntityListener(localPlayerSystem);
            EnemySystem enemySystem = new EnemySystem();
            engine.addSystem(enemySystem);
            engine.addEntityListener(enemySystem);
            EntitiyRenderSystem entitiyRenderSystem = new EntitiyRenderSystem();
            engine.addSystem(entitiyRenderSystem);
            engine.addEntityListener(entitiyRenderSystem);
            AttackSystem attackSystem = new AttackSystem();
            engine.addSystem(attackSystem);
            engine.addEntityListener(attackSystem);
            AttackRenderSystem attackRenderSystem = new AttackRenderSystem();
            engine.addSystem(attackRenderSystem);
            engine.addEntityListener(attackRenderSystem);
            EnemySpawnSystem enemySpawnSystem = new EnemySpawnSystem();
            engine.addSystem(enemySpawnSystem);
            engine.addEntityListener(enemySpawnSystem);
        }else{
            LocalPlayerSystem localPlayerSystem = new LocalPlayerSystem((OrthographicCamera) viewport.getCamera());
            engine.addSystem(localPlayerSystem);
            engine.addEntityListener(localPlayerSystem);
            EntitiyRenderSystem entitiyRenderSystem = new EntitiyRenderSystem();
            engine.addSystem(entitiyRenderSystem);
            engine.addEntityListener(entitiyRenderSystem);
            AttackRenderSystem attackRenderSystem = new AttackRenderSystem();
            engine.addSystem(attackRenderSystem);
            engine.addEntityListener(attackRenderSystem);
        }
        if (isMultiplayer){
            engine.addEntityListener(multiplayerManagerThread);
        }
    }

    private InputComponent loadUIButtons(){
        Texture joystickBackground = assetManager.get("JoystickR.png", Texture.class);
        Texture joystickKnob = assetManager.get("KnobR.png", Texture.class);
        touchpad = new Touchpad(10, new Touchpad.TouchpadStyle(new TextureRegionDrawable(joystickBackground), new TextureRegionDrawable(joystickKnob)));
        shootTouchpad = new Touchpad(20, new Touchpad.TouchpadStyle(new TextureRegionDrawable(joystickBackground), new TextureRegionDrawable(joystickKnob)));
        stage.addActor(touchpad);
        stage.addActor(shootTouchpad);
        InputComponent inputComponent = new InputComponent();
        inputComponent.shootTouchpad = shootTouchpad;
        inputComponent.touchpad = touchpad;
        Vector3 touchpadPos = stage.getCamera().unproject(new Vector3(0, Gdx.graphics.getHeight(), 0));
        inputComponent.touchpad.setPosition(touchpadPos.x+10, touchpadPos.y+10);
        inputComponent.shootTouchpad.setPosition(touchpadPos.x + stage.getWidth() / 1.37f, touchpadPos.y + 10);
        return inputComponent;
    }
    private LocalPlayer loadBasicEntities(InputComponent inputComponent){
        LocalPlayer localPlayer = new LocalPlayer(1, 2, inputComponent);
        localPlayer.setBounds(map.getProperties().get("spawnX", Integer.class) * 16, (40 - map.getProperties().get("spawnY", Integer.class)) * 16, 16, 16);
        engine.addEntity(localPlayer);
        return localPlayer;
    }

    private void loadLocalPlayerUI(LocalPlayer localPlayer){
        healthBar = new HealthBar(localPlayer.getComponent(EntityComponent.class));
        healthBar.setBounds(10, stage.getHeight() - stage.getHeight() / 15, stage.getWidth() / 3f, stage.getHeight() / 2f);
        stage.addActor(healthBar);
    }

    public void setServer(boolean server){
        this.isServer = server;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stage.clear();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        engine.removeAllSystems();
        engine.removeAllEntities();
    }

    @Override
    public void dispose() {
    }
}
