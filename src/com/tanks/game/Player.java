package com.tanks.game;

import com.tanks.IO.Input;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tanks.game.level.Level;
import com.tanks.graphics.Sprite;
import com.tanks.graphics.SpriteSheet;
import com.tanks.graphics.TextureAtlas;
import com.tanks.main.Menu;

public class Player extends Entity {

    private static final int	PROTECTION_TIME	= 4000;
    private static final float	APPEARANCE_X	= Entity.SPRITE_SCALE * Game.SCALE * 4;
    private static final float	APPEARANCE_Y	= Entity.SPRITE_SCALE * Game.SCALE * 12;

    public enum Heading {
        NORTH_SIMPLE(0 * SPRITE_SCALE, 0 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), EAST_SIMPLE(
                6 * SPRITE_SCALE, 0 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), SOUTH_SIMPLE(4 * SPRITE_SCALE,
                0 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), WEST_SIMPLE(2 * SPRITE_SCALE,
                0 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE),

        NORTH_MEDIUM(0 * SPRITE_SCALE, 2 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), EAST_MEDIUM(
                6 * SPRITE_SCALE, 2 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), SOUTH_MEDIUM(4 * SPRITE_SCALE,
                2 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), WEST_MEDIUM(2 * SPRITE_SCALE,
                2 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE),

        NORTH_STRONG(0 * SPRITE_SCALE, 7 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), EAST_STRONG(
                6 * SPRITE_SCALE, 7 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), SOUTH_STRONG(4 * SPRITE_SCALE,
                7 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE), WEST_STRONG(2 * SPRITE_SCALE,
                7 * SPRITE_SCALE, 1 * SPRITE_SCALE, 1 * SPRITE_SCALE),;

        private int x, y, h, w;

        Heading(int x, int y, int h, int w) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        protected BufferedImage texture(TextureAtlas atlas) {
            return atlas.cut(x, y, w, h);
        }
    }

    private static int				lives;
    private static int				strength;

    private Heading					heading;
    private Map<Heading, Sprite>	spriteMap;
    private float					speed;
    private float					bulletSpeed;
    private Bullet					bullet;
    private boolean					isProtected;
    private List<Sprite>			protectionList;

    private long lastShootTime = 0;

    public Player(float scale, float speed, TextureAtlas atlas, Level lvl) {
        super(EntityType.Player, APPEARANCE_X, APPEARANCE_Y, scale, atlas, lvl);

        heading = Heading.NORTH_SIMPLE;
        spriteMap = new HashMap<Heading, Sprite>();
        this.speed = speed;
        bulletSpeed = 6;
        lives = 2;
        strength = 1;

        isProtected = true;
        protectionList = new ArrayList<>();
        protectionList.add(
                new Sprite(new SpriteSheet(atlas.cut(16 * SPRITE_SCALE, 9 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));
        protectionList.add(
                new Sprite(new SpriteSheet(atlas.cut(17 * SPRITE_SCALE, 9 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));

        for (Heading h : Heading.values()) {
            SpriteSheet sheet = new SpriteSheet(h.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale);
            spriteMap.put(h, sprite);
        }

    }

    @Override
    public void update(Input input) {

        if (!lvl.isEagleAlive())
            return;

        if (evolving)
            return;

        if (System.currentTimeMillis() > createdTime + EVOLVING_TIME + PROTECTION_TIME)
            isProtected = false;

        float newX = x;
        float newY = y;

        if (input.getKey(KeyEvent.VK_W)) {
            newY -= speed;
            x = newX = (Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            heading = strength > 1 ? (strength > 2 ? Heading.NORTH_STRONG : Heading.NORTH_MEDIUM)
                    : Heading.NORTH_SIMPLE;
        } else if (input.getKey(KeyEvent.VK_D)) {
            newX += speed;
            y = newY = (Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            heading = strength > 1 ? (strength > 2 ? Heading.EAST_STRONG : Heading.EAST_MEDIUM) : Heading.EAST_SIMPLE;
        } else if (input.getKey(KeyEvent.VK_S)) {
            newY += speed;
            x = newX = (Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            heading = strength > 1 ? (strength > 2 ? Heading.SOUTH_STRONG : Heading.SOUTH_MEDIUM)
                    : Heading.SOUTH_SIMPLE;
        } else if (input.getKey(KeyEvent.VK_A)) {
            newX -= speed;
            y = newY = (Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            heading = strength > 1 ? (strength > 2 ? Heading.WEST_STRONG : Heading.WEST_MEDIUM) : Heading.WEST_SIMPLE;

        }

        if (newX < 0) {
            newX = 0;
        } else if (newX >= Game.WIDTH - SPRITE_SCALE * scale) {
            newX = Game.WIDTH - SPRITE_SCALE * scale;
        }

        if (newY < 0) {
            newY = 0;
        } else if (newY >= Game.HEIGHT - SPRITE_SCALE * scale) {
            newY = Game.HEIGHT - SPRITE_SCALE * scale;
        }

        switch (heading) {
            case NORTH_SIMPLE:
            case NORTH_MEDIUM:
            case NORTH_STRONG:
                if (canMove(newX, newY, newX + (SPRITE_SCALE * scale / 2), newY, newX + (SPRITE_SCALE * scale), newY)
                        && !intersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
            case SOUTH_SIMPLE:
            case SOUTH_MEDIUM:
            case SOUTH_STRONG:
                if (canMove(newX, newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale / 2),
                        newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && !intersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
            case EAST_SIMPLE:
            case EAST_MEDIUM:
            case EAST_STRONG:
                if (canMove(newX + (SPRITE_SCALE * scale), newY, newX + (SPRITE_SCALE * scale),
                        newY + (SPRITE_SCALE * scale / 2), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && !intersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
            case WEST_SIMPLE:
            case WEST_MEDIUM:
            case WEST_STRONG:
                if (canMove(newX, newY, newX, newY + (SPRITE_SCALE * scale / 2), newX, newY + (SPRITE_SCALE * scale))
                        && !intersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
        }

        List<Bullet> bullets = Game.getBullets(EntityType.Enemy);
        if (bullets != null) {
            for (Bullet enemyBullet : bullets) {
                if (getRectangle().intersects(enemyBullet.getRectangle()) && enemyBullet.isActive()) {
                    if (!isProtected)
                        isAlive = false;
                    enemyBullet.setInactive();
                }

            }

        }

        if (lvl.hasBonus() && getRectangle().intersects(lvl.getBonusRectangle())) {
            Bonus bonus = lvl.getBonus();
            switch (bonus) {
                case PROTECTION:
                    createdTime = System.currentTimeMillis();
                    isProtected = true;
                    break;
                case FREEZE:
                    Game.freezeEnemies();
                    break;
                case SHIELD:
                    lvl.protectEagle();
                    break;
                case STAR:
                    upgrade();
                    break;
                case DETONATION:
                    Game.detonateEnemies();
                    break;
                case LIFE:
                    if (++lives > 9)
                        lives = 9;
                    break;
            }
            lvl.removeBonus();

        }

        if (input.getKey(KeyEvent.VK_SPACE)) {

            long currentTime = System.currentTimeMillis();
            // стрельба с перезарядкой
            if (currentTime - lastShootTime >= 1000) {
                lastShootTime = currentTime;
                bullet = new Bullet(x, y, scale, bulletSpeed, heading.toString().substring(0, 4), atlas, lvl, EntityType.Player);
                bullet.setSpeed(20);
            }

            // тут пуля пока не столкнется
            /*if (bullet == null || !bullet.isActive()) {
                if (Game.getBullets(EntityType.Player).size() == 0) {
                    bullet = new Bullet(x, y, scale, bulletSpeed, heading.toString().substring(0, 4), atlas, lvl, EntityType.Player);

                }
            }*/
        }

        if (input.getKey(KeyEvent.VK_ESCAPE)) {

        }
    }

    private boolean intersectsEnemy(float newX, float newY) {
        List<Enemy> enemyList = Game.getEnemies();
        Rectangle2D.Float rect = getRectangle(newX, newY);
        for (Enemy enemy : enemyList) {
            if (rect.intersects(enemy.getRectangle()))
                return true;
        }
        return false;
    }

    @Override
    public void render(Graphics2D g) {
        if (evolving) {
            drawEvolving(g);
            return;
        }
        spriteMap.get(heading).render(g, x, y);

        if (isProtected)
            drawProtection(g);

    }

    private void drawProtection(Graphics2D g) {
        if (animationCount % 16 < 8)
            protectionList.get(0).render(g, x, y);
        else
            protectionList.get(1).render(g, x, y);
        animationCount++;

    }

    @Override
    public void drawExplosion(Graphics2D g) {
        super.drawExplosion(g);
        if (--lives >= 0)
            reset();
        else
            Game.setGameOver();
    }

    public void reset() {
        this.x = APPEARANCE_X;
        this.y = APPEARANCE_Y;
        isAlive = true;
        evolving = true;
        isProtected = true;
        createdTime = System.currentTimeMillis();
        strength = 1;
        heading = Heading.NORTH_SIMPLE;

    }

    public boolean hasMoreLives() {
        return lives >= 0;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    private void upgrade() {
        if (++strength > 3)
            strength = 3;

        switch (heading) {
            case NORTH_SIMPLE:
                heading = Heading.NORTH_MEDIUM;
                break;
            case EAST_SIMPLE:
                heading = Heading.EAST_MEDIUM;
                break;
            case SOUTH_SIMPLE:
                heading = Heading.SOUTH_MEDIUM;
                break;
            case WEST_SIMPLE:
                heading = Heading.WEST_MEDIUM;
                break;

            case NORTH_MEDIUM:
                heading = Heading.NORTH_STRONG;
                break;
            case EAST_MEDIUM:
                heading = Heading.EAST_STRONG;
                break;
            case SOUTH_MEDIUM:
                heading = Heading.SOUTH_STRONG;
                break;
            case WEST_MEDIUM:
                heading = Heading.WEST_STRONG;
                break;

            case NORTH_STRONG:
            case EAST_STRONG:
            case SOUTH_STRONG:
            case WEST_STRONG:
        }

    }

    public static int getPlayerLives() {
        return lives;
    }

    public static int getPlayerStrength() {
        return strength;
    }

    public void moveOnNextLevel() {
        this.x = APPEARANCE_X;
        this.y = APPEARANCE_Y;
        evolving = true;
        isProtected = true;
        bullet = null;
        createdTime = System.currentTimeMillis();

        switch (heading) {
            case EAST_SIMPLE:
            case SOUTH_SIMPLE:
            case WEST_SIMPLE:
            case NORTH_SIMPLE:
                heading = Heading.NORTH_SIMPLE;
                break;

            case EAST_MEDIUM:
            case SOUTH_MEDIUM:
            case WEST_MEDIUM:
            case NORTH_MEDIUM:
                heading = Heading.NORTH_MEDIUM;
                break;

            case EAST_STRONG:
            case SOUTH_STRONG:
            case WEST_STRONG:
            case NORTH_STRONG:
                heading = Heading.NORTH_STRONG;
                break;
        }

    }
}
