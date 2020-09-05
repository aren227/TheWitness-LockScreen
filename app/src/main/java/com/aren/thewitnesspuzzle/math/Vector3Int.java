package com.aren.thewitnesspuzzle.math;

public class Vector3Int {

    public int x, y, z;

    public Vector3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector3Int) {
            Vector3Int e = (Vector3Int) obj;
            return x == e.x && y == e.y && z == e.z;
        }
        return false;
    }

}
