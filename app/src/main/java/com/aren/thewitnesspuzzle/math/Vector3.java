package com.aren.thewitnesspuzzle.math;

public class Vector3 {

    public float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 vec) {
        return new Vector3(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vector3 middle(Vector3 another) {
        return new Vector3((x + another.x) / 2f, (y + another.y) / 2f, (z + another.z) / 2f);
    }

}
