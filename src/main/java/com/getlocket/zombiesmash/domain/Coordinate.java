package com.getlocket.zombiesmash.domain;


public class Coordinate {
    private Integer x;
    private Integer y;

    private Integer previousX;
    private Integer previousY;


    public Coordinate(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != null ? !x.equals(that.x) : that.x != null) return false;
        if (y != null ? !y.equals(that.y) : that.y != null) return false;

        return true;
    }

    public void updateWithDeltas(int xDelta, int yDelta) {
        this.previousX = this.x;
        this.previousY = this.y;

        this.x += xDelta;
        this.y += yDelta;
    }

    public void undoLastUpdate() {
        this.x = this.previousX;
        this.y = this.previousY;
    }
}
