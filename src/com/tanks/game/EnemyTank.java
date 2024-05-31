package com.tanks.game;

import com.tanks.game.level.Level;
import com.tanks.graphics.TextureAtlas;

public class EnemyTank extends Enemy {
    private static final int	NORTH_X	= 8;
    private static final int	NORTH_Y	= 0;
    private static final float	SPEED	= 1.8f;
    private static final int	LIVES	= 0;

    public EnemyTank(float x, float y, float scale, TextureAtlas atlas, Level lvl) {
        super(x, y, scale, SPEED, atlas, lvl, NORTH_X, NORTH_Y, LIVES);

    }

}

