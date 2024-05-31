package com.tanks.game;

import com.tanks.IO.Input;
import com.tanks.game.level.Level;
import com.tanks.game.level.TileType;
import com.tanks.graphics.Sprite;
import com.tanks.graphics.SpriteSheet;
import com.tanks.graphics.TextureAtlas;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity {

    public static final int				SPRITE_SCALE		= 16;
    public static final int				SPRITES_PER_HEADING	= 1;

    public final EntityType				type;

    protected static final int			EVOLVING_TIME		= 1300;
    protected static final List<Sprite>	evolvingList		= new ArrayList<>();;

    protected float						x;
    protected float						y;
    protected float						scale;
    protected static Level				lvl;
    protected TextureAtlas				atlas;
    protected boolean					evolving;
    protected long						createdTime;
    protected int						animationCount;
    protected boolean					isAlive;

    protected Entity(EntityType type, float x, float y, float scale, TextureAtlas atlas, Level lvl) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.atlas = atlas;
        Entity.lvl = lvl;
        isAlive = true;

        createdTime = System.currentTimeMillis();
        animationCount = 0;
        evolving = true;
        evolvingList.add(
                new Sprite(new SpriteSheet(atlas.cut(16 * SPRITE_SCALE, 6 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));
        evolvingList.add(
                new Sprite(new SpriteSheet(atlas.cut(17 * SPRITE_SCALE, 6 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));
        evolvingList.add(
                new Sprite(new SpriteSheet(atlas.cut(18 * SPRITE_SCALE, 6 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));
        evolvingList.add(
                new Sprite(new SpriteSheet(atlas.cut(19 * SPRITE_SCALE, 6 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));
    }

    public abstract void update(Input input);

    public abstract void render(Graphics2D g);

    public abstract boolean isAlive();

    public Rectangle2D.Float getRectangle() {
        return new Rectangle2D.Float(x, y, SPRITE_SCALE * scale, SPRITE_SCALE * scale);
    }

    protected Rectangle2D.Float getRectangle(float newX, float newY) {
        return new Rectangle2D.Float(newX, newY, SPRITE_SCALE * scale, SPRITE_SCALE * scale);
    }

    protected void drawEvolving(Graphics2D g) {
        if (animationCount % 12 < 3)
            evolvingList.get(0).render(g, x, y);
        else if (animationCount % 12 >= 3 && animationCount % 12 < 6)
            evolvingList.get(1).render(g, x, y);
        else if (animationCount % 12 >= 6 && animationCount % 12 < 9)
            evolvingList.get(2).render(g, x, y);
        else if (animationCount % 12 >= 9)
            evolvingList.get(3).render(g, x, y);
        animationCount++;
        if (System.currentTimeMillis() > createdTime + EVOLVING_TIME)
            evolving = false;

    }

    protected boolean canMove(float newX, float newY, float centerX, float centerY, float bottomX, float bottomY) {
        int tileX = (int) (newX / Level.SCALED_TILE_SIZE);
        int tileY = (int) (newY / Level.SCALED_TILE_SIZE);
        int tileCenterX = (int) (centerX / Level.SCALED_TILE_SIZE);
        int tileCenterY = (int) (centerY / Level.SCALED_TILE_SIZE);
        int tileBottomX = bottomX % Level.SCALED_TILE_SIZE == 0 ? tileCenterX
                : (int) (bottomX / Level.SCALED_TILE_SIZE);
        int tileBottomY = bottomY % Level.SCALED_TILE_SIZE == 0 ? tileCenterY
                : (int) (bottomY / Level.SCALED_TILE_SIZE);

        Integer[][] tileMap = lvl.getTileMap();

        if (Integer.max(tileY, tileBottomY) >= tileMap.length || Integer.max(tileX, tileBottomX) >= tileMap[0].length
                || isImpassableTile(tileMap[tileY][tileX], tileMap[tileCenterY][tileCenterX],
                tileMap[tileBottomY][tileBottomX])) {

            return false;
        } else
            return true;

    }

    protected boolean isImpassableTile(Integer... tileNum) {
        for (int i = 0; i < tileNum.length; i++)
            if (tileNum[i] == TileType.BRICK.numeric() || tileNum[i] == TileType.METAL.numeric()
                    || tileNum[i] == TileType.DOWN_LEFT_EAGLE.numeric()
                    || tileNum[i] == TileType.DOWN_RIGHT_EAGLE.numeric()
                    || tileNum[i] == TileType.UP_LEFT_EAGLE.numeric() || tileNum[i] == TileType.UP_RIGHT_EAGLE.numeric()
                    || tileNum[i] == TileType.DOWN_LEFT_DEAD_EAGLE.numeric()
                    || tileNum[i] == TileType.DOWN_RIGHT_DEAD_EAGLE.numeric()
                    || tileNum[i] == TileType.UP_LEFT_DEAD_EAGLE.numeric()
                    || tileNum[i] == TileType.UP_RIGHT_DEAD_EAGLE.numeric() || tileNum[i] == TileType.WATER.numeric()
                    || tileNum[i] == TileType.OTHER_WATER.numeric()) {
                return true;
            }
        return false;
    }

    public void drawExplosion(Graphics2D g) {

        float adjustedX = x - SPRITE_SCALE;
        float adjustedY = y - SPRITE_SCALE;

        SpriteSheet expSheet = new SpriteSheet(
                atlas.cut(19 * SPRITE_SCALE, 8 * SPRITE_SCALE, 2 * SPRITE_SCALE, 2 * SPRITE_SCALE), SPRITES_PER_HEADING,
                2 * SPRITE_SCALE);
        Sprite expSprite = new Sprite(expSheet, scale);
        SpriteSheet bigExpSheet = new SpriteSheet(
                atlas.cut(21 * SPRITE_SCALE, 8 * SPRITE_SCALE, 2 * SPRITE_SCALE, 2 * SPRITE_SCALE), SPRITES_PER_HEADING,
                2 * SPRITE_SCALE);
        Sprite bigExpSprite = new Sprite(bigExpSheet, scale);
        long curTime = System.currentTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                while (time < curTime + 150) {
                    expSprite.render(g, adjustedX, adjustedY);
                    bigExpSprite.render(g, adjustedX, adjustedY);
                    time = System.currentTimeMillis();
                }
            }
        }).start();

    }

}
