package com.lukin.openworld.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lukin.openworld.LKGame;
import com.lukin.openworld.components.EnemyHearingComponent;
import com.lukin.openworld.components.EntityComponent;
import com.lukin.openworld.components.HitboxComponent;
import com.lukin.openworld.entities.Enemy;
import com.lukin.openworld.utils.EntityLoader;

public class EnemySpawnSystem extends EntitySystem implements EntityListener {
    private final Array<EnemySpawnPoint> enemySpawnPoints = new Array<>();
    private final TiledMap tiledMap;

    public EnemySpawnSystem(){
        tiledMap = LKGame.getMap();
    }

    @Override
    public void addedToEngine(Engine engine) {
        Array<RectangleMapObject> rectangleMapObjects = tiledMap.getLayers().get("enemy").getObjects().getByType(RectangleMapObject.class);
        for(RectangleMapObject rectangleMapObject : rectangleMapObjects){
            EnemySpawnPoint enemySpawnPoint = new EnemySpawnPoint();
            enemySpawnPoint.rectangle = rectangleMapObject.getRectangle();
            enemySpawnPoint.enemyCount = rectangleMapObject.getProperties().get("enemyCount", Integer.class);
            enemySpawnPoint.currentEntity = createEnemy(enemySpawnPoint.rectangle);
            enemySpawnPoints.add(enemySpawnPoint);
        }
    }

    private Entity createEnemy(Rectangle rectangle){
        Enemy enemy = new Enemy(MathUtils.random(2, 4), 2);
        Vector2 point = new Vector2(MathUtils.random(rectangle.x, rectangle.x + 10), MathUtils.random(rectangle.y, rectangle.y + 10));
        enemy.setBounds(point.x, point.y, 16, 16);
        HitboxComponent hitboxComponent = enemy.getComponent(HitboxComponent.class);
        EnemyHearingComponent enemyHearingComponent = enemy.getComponent(EnemyHearingComponent.class);
        enemyHearingComponent.hearingRectangle.setCenter(hitboxComponent.x, hitboxComponent.y);
        if (getEngine() != null){
            getEngine().addEntity(enemy);
        }
        return enemy;
    }

    @Override
    public void entityAdded(Entity entity) {}

    @Override
    public void entityRemoved(Entity entity) {
        EntityComponent ec = entity.getComponent(EntityComponent.class);
        if(ec == null) return;
        if(ec.type == EntityComponent.EntityType.ENEMY){
            for (EnemySpawnPoint enemySpawnPoint : enemySpawnPoints) {
                if (entity == enemySpawnPoint.currentEntity) {
                    if (enemySpawnPoint.enemyCount == 0) {
                        enemySpawnPoints.removeValue(enemySpawnPoint, true);
                    }else {
                        enemySpawnPoint.currentEntity = createEnemy(enemySpawnPoint.rectangle);
                        enemySpawnPoint.enemyCount--;
                    }
                }
            }
        }
    }

    private static class EnemySpawnPoint{
        public Rectangle rectangle;
        public int enemyCount;
        public Entity currentEntity;
    }
}
