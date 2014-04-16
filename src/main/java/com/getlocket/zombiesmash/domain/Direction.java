package com.getlocket.zombiesmash.domain;


public enum Direction {
    NORTH(1, 0),
    NE(1, 1),
    EAST(0, 1),
    SE(-1, 1),
    SOUTH(-1, 0),
    SW(-1, -1),
    WEST(0, -1),
    NW(1, -1);

    private final Integer xDeltaScale;
    private final Integer yDeltaScale;

    Direction(Integer xDeltaScale, Integer yDeltaScale) {
        this.xDeltaScale = xDeltaScale;
        this.yDeltaScale = yDeltaScale;
    }

    public Integer getXDeltaScale() {
        return xDeltaScale;
    }

    public Integer getYDeltaScale() {
        return yDeltaScale;
    }
}
