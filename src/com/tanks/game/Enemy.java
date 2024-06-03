package com.tanks.game;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.tanks.IO.Input;
import com.tanks.game.level.Level;
import com.tanks.graphics.Sprite;
import com.tanks.graphics.SpriteSheet;
import com.tanks.graphics.TextureAtlas;
public abstract class Enemy extends Entity {

    private static final int DELAY = 2000;

    public enum EnemyHeading {
        NORTH, EAST, SOUTH, WEST;

        private int x, y, h, w;

        protected BufferedImage texture(TextureAtlas atlas) {
            return atlas.cut(x, y, w, h);
        }

        private EnemyHeading getFromNumber(int number) {
            switch (number) {
                case 0:
                    return NORTH;
                case 1:
                    return SOUTH;
                case 2:
                    return EAST;
                default:
                    return WEST;
            }
        }

        private void setCords(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;

        }
    }

    private EnemyHeading				enemyHeading;
    private Map<EnemyHeading, Sprite>	spriteMap;
    private float						speed;
    private float						bulletSpeed;
    private static Player				player;
    private Bullet						bullet;
    private Bonus						bonus;
    private int							lives;

    public Enemy(float x, float y, float scale, float speed, TextureAtlas atlas, Level lvl, int headX, int headY,
                 int lives) {
        super(EntityType.Enemy, x, y, scale, atlas, lvl);

        enemyHeading = EnemyHeading.NORTH;
        spriteMap = new HashMap<EnemyHeading, Sprite>();
        this.speed = speed;
        bulletSpeed = 4;
        this.lives = lives;

        Random rand = new Random();
        if (rand.nextInt(8) == 7) {
            bonus = Bonus.fromNumeric(rand.nextInt(6));
        }

        for (EnemyHeading eh : EnemyHeading.values()) {
            switch (eh) {
                case NORTH:
                    eh.setCords(headX * SPRITE_SCALE, headY * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE);
                    break;
                case EAST:
                    eh.setCords((headX + 6) * SPRITE_SCALE, headY * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE);
                    break;
                case SOUTH:
                    eh.setCords((headX + 4) * SPRITE_SCALE, headY * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE);
                    break;
                case WEST:
                    eh.setCords((headX + 2) * SPRITE_SCALE, headY * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE);
                    break;
            }
            SpriteSheet sheet = new SpriteSheet(eh.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale);
            spriteMap.put(eh, sprite);
        }

    }

    @Override
    public void update(Input input) {

        if (evolving || !isAlive)
            return;

        float newX = x;
        float newY = y;

        switch (enemyHeading) {
            case NORTH:
                newY -= speed;
                newX = (Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
                break;
            case EAST:
                newX += speed;
                newY = (Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
                break;
            case SOUTH:
                newY += speed;
                newX = (Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
                break;
            case WEST:
                newX -= speed;
                newY = (Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
                break;

        }

        if (newX < 0) {
            newX = 0;
            enemyHeading = changeEnemyHeading(enemyHeading);
        } else if (newX > Game.WIDTH - SPRITE_SCALE * scale) {
            newX = Game.WIDTH - SPRITE_SCALE * scale;
            enemyHeading = changeEnemyHeading(enemyHeading);
        }

        if (newY < 0) {
            newY = 0;
            enemyHeading = changeEnemyHeading(enemyHeading);
        } else if (newY > Game.HEIGHT - SPRITE_SCALE * scale) {
            newY = Game.HEIGHT - SPRITE_SCALE * scale;
            enemyHeading = changeEnemyHeading(enemyHeading);
        }

        switch (enemyHeading) {
            case NORTH:
                if (canMove(newX, newY, newX + (SPRITE_SCALE * scale / 2), newY, newX + (SPRITE_SCALE * scale), newY)
                        && !intersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading(enemyHeading);
                break;
            case SOUTH:
                if (canMove(newX, newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale / 2),
                        newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && !intersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading(enemyHeading);
                break;
            case EAST:
                if (canMove(newX + (SPRITE_SCALE * scale), newY, newX + (SPRITE_SCALE * scale),
                        newY + (SPRITE_SCALE * scale / 2), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && !intersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading(enemyHeading);
                break;
            case WEST:
                if (canMove(newX, newY, newX, newY + (SPRITE_SCALE * scale / 2), newX, newY + (SPRITE_SCALE * scale))
                        && !intersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading(enemyHeading);
                break;
        }

        if (bullet == null && System.currentTimeMillis() % DELAY < 50)
            bullet = new Bullet(x, y, scale, bulletSpeed, enemyHeading.toString().substring(0, 4), atlas, lvl, EntityType.Enemy);
        if (bullet != null && !bullet.isActive() && System.currentTimeMillis() % DELAY < 50) {
            bullet = new Bullet(x, y, scale, bulletSpeed, enemyHeading.toString().substring(0, 4), atlas, lvl, EntityType.Enemy);
        }

    }

    private EnemyHeading changeEnemyHeading(EnemyHeading enemyHeading2) {
        Random random = new Random();
        int direction = random.nextInt(4);
        EnemyHeading newEnemyHeading = enemyHeading.getFromNumber(direction);
        if (newEnemyHeading == enemyHeading)
            changeEnemyHeading(enemyHeading);
        return newEnemyHeading;
    }

    @Override
    public void render(Graphics2D g) {
        if (!isAlive) {
            return;
        }

        if (evolving) {
            drawEvolving(g);
            return;
        }
        spriteMap.get(enemyHeading).render(g, x, y);

    }

    public void setPlayer(Player player) {
        Enemy.player = player;
    }

    private boolean intersectsEnemy(float newX, float newY) {
        List<Enemy> enemyList = Game.getEnemies();
        Rectangle2D.Float rect = getRectangle(newX, newY);
        for (Enemy enemy : enemyList) {
            if (enemy != this && rect.intersects(enemy.getRectangle()))
                return true;
        }
        return false;
    }

    public boolean isEvolving() {
        return evolving;
    }

    @Override
    public boolean isAlive() {
        if (!isAlive && bonus != null)
            lvl.setBonus(bonus);
        return isAlive;
    }

    public void setDead() {
        isAlive = false;
    }

    public void fixHitting(int playerStrength) {
        lives -= playerStrength;
    }

    public boolean hasMoreLives() {
        return lives >= 0;
    }

}