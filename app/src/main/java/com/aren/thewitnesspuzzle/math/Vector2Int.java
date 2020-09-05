package com.aren.thewitnesspuzzle.math;

public class Vector2Int {

    public int x, y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2Int) {
            Vector2Int e = (Vector2Int) obj;
            return x == e.x && y == e.y;
        }
        return false;
    }

}
