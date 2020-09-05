package com.aren.thewitnesspuzzle.math;

public class Vector2 {

    public float x, y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 vec) {
        return new Vector2(x + vec.x, y + vec.y);
    }

    public Vector2 subtract(Vector2 vec) {
        return new Vector2(x - vec.x, y - vec.y);
    }

    public float dot(Vector2 vec) {
        return x * vec.x + y * vec.y;
    }

    public Vector2 middle(Vector2 another) {
        return new Vector2((x + another.x) / 2f, (y + another.y) / 2f);
    }

    public float distance(Vector2 another) {
        return (float) Math.sqrt((x - another.x) * (x - another.x) + (y - another.y) * (y - another.y));
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 projection(Vector2 to) {
        float len = dot(to) / to.dot(to);
        return new Vector2(len * to.x, len * to.y);
    }

    public Vector3 toVector3() {
        return toVector3(0);
    }

    public Vector3 toVector3(float z) {
        return new Vector3(x, y, z);
    }

    @Override
    public Vector2 clone() {
        return new Vector2(x, y);
    }

}
