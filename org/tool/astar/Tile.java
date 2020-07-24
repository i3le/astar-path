package org.tool.astar;

import java.util.Objects;

public class Tile {

    public int x;
    public int y;

    public Tile() {
    }

    public Tile(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Tile(Tile v) {
        this.x = v.x;
        this.y = v.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GirdTile p = (GirdTile) o;
        return x == p.x && y == p.y;
    }

    public boolean equals(float x, float y) {
        return this.x == (int) x && this.y == (int) y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    @Override
    public Object clone() {
        return new Tile(this);
    }

}
