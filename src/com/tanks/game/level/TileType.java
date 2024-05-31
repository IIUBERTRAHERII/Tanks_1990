package com.tanks.game.level;

public enum TileType {

    EMPTY(0), BRICK(1), METAL(2), WATER(3), GRASS(4), OTHER_WATER(5), UP_LEFT_EAGLE(6), UP_RIGHT_EAGLE(7), DOWN_LEFT_EAGLE(8),
    DOWN_RIGHT_EAGLE(9), UP_LEFT_DEAD_EAGLE(10), UP_RIGHT_DEAD_EAGLE(11), DOWN_LEFT_DEAD_EAGLE(12),
    DOWN_RIGHT_DEAD_EAGLE(13);

    private int n;

    TileType(int n) {
        this.n = n;
    }

    public int numeric() {
        return n;
    }

    public static TileType fromNumeric(int n){
        switch(n){
            case 1:
                return BRICK;
            case 2:
                return METAL;
            case 3:
                return WATER;
            case 4:
                return GRASS;
            case 5:
                return OTHER_WATER;
            case 6:
                return UP_LEFT_EAGLE;
            case 7:
                return UP_RIGHT_EAGLE;
            case 8:
                return DOWN_LEFT_EAGLE;
            case 9:
                return DOWN_RIGHT_EAGLE;
            case 10:
                return UP_LEFT_DEAD_EAGLE;
            case 11:
                return UP_RIGHT_DEAD_EAGLE;
            case 12:
                return DOWN_LEFT_DEAD_EAGLE;
            case 13:
                return DOWN_RIGHT_DEAD_EAGLE;
            default:
                return EMPTY;
        }
    }

}
