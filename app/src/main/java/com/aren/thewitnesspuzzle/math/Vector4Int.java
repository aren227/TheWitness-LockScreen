package com.aren.thewitnesspuzzle.math;

public class Vector4Int {

    public int x, y, z, w;

    public Vector4Int(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector4Int) {
            Vector4Int e = (Vector4Int) obj;
            return x == e.x && y == e.y && z == e.z && w == e.w;
        }
        return false;
    }

}
