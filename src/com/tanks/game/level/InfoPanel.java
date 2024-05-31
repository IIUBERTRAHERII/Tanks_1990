package com.tanks.game.level;

import java.awt.Graphics2D;
import com.tanks.game.Game;
import com.tanks.game.Player;
import com.tanks.graphics.Sprite;
import com.tanks.graphics.SpriteSheet;
import com.tanks.graphics.TextureAtlas;
import com.tanks.utils.Utils;

public class InfoPanel {

    private TextureAtlas	atlas;
    private Sprite			enemySprite;
    private SpriteSheet		numbersToFour;
    private SpriteSheet		numbersToNine;
    private int				stage;

    InfoPanel(TextureAtlas atlas, int stage) {
        this.atlas = atlas;
        this.stage = stage%10;

        numbersToFour = new SpriteSheet(
                atlas.cut(41 * Level.TILE_SCALE, 23 * Level.TILE_SCALE, 5 * Level.TILE_SCALE, Level.TILE_SCALE), 5,
                Level.TILE_SCALE);
        numbersToNine = new SpriteSheet(
                atlas.cut(41 * Level.TILE_SCALE, 24 * Level.TILE_SCALE, 5 * Level.TILE_SCALE, Level.TILE_SCALE), 5,
                Level.TILE_SCALE);
        enemySprite = new Sprite(new SpriteSheet(
                atlas.cut(40 * Level.TILE_SCALE, 24 * Level.TILE_SCALE, Level.TILE_SCALE, Level.TILE_SCALE), 1,
                Level.TILE_SCALE), Level.TILE_IN_GAME_SCALE, 0, false);
    }

    public void renderInfoPanel(Graphics2D g) {
        g.drawImage(Utils.resize(atlas.cut(46 * Level.TILE_SCALE, 0, Level.TILE_SCALE, Level.TILE_SCALE),
                8 * Level.SCALED_TILE_SIZE, Game.HEIGHT), Game.WIDTH, 0, null);

        g.drawImage(
                Utils.resize(
                        atlas.cut(41 * Level.TILE_SCALE, 22 * Level.TILE_SCALE, 5 * Level.TILE_SCALE, Level.TILE_SCALE),
                        5 * Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE),
                Game.WIDTH + Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE, null);

        new Sprite(stage< 5 ? numbersToFour : numbersToNine, Level.TILE_IN_GAME_SCALE, stage%5, false).
                render(g, Game.WIDTH + 6 * Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE);

        g.drawImage(
                Utils.resize(
                        atlas.cut(47 * Level.TILE_SCALE, 17 * Level.TILE_SCALE, 2 * Level.TILE_SCALE, Level.TILE_SCALE),
                        2 * Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE),
                Game.WIDTH + 3 * Level.SCALED_TILE_SIZE, 15 * Level.SCALED_TILE_SIZE, null);

        g.drawImage(
                Utils.resize(
                        atlas.cut(47 * Level.TILE_SCALE, 18 * Level.TILE_SCALE, Level.TILE_SCALE, Level.TILE_SCALE),
                        Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE),
                Game.WIDTH + 3 * Level.SCALED_TILE_SIZE, 16 * Level.SCALED_TILE_SIZE, null);

        int playerLives = Player.getPlayerLives()<0?0:Player.getPlayerLives();

        new Sprite( playerLives< 5 ? numbersToFour : numbersToNine, Level.TILE_IN_GAME_SCALE,
                playerLives % 5, false).render(g, Game.WIDTH + 4 * Level.SCALED_TILE_SIZE,
                16 * Level.SCALED_TILE_SIZE);

        for (int i = 0; i < Game.getEnemyCount(); i++) {
            enemySprite.render(g, Game.WIDTH + 3 * Level.SCALED_TILE_SIZE + i % 2 * Level.SCALED_TILE_SIZE,
                    3 * Level.SCALED_TILE_SIZE + i / 2 * Level.SCALED_TILE_SIZE);
        }

    }

}
