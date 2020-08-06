package com.aren.thewitnesspuzzle.math;

public class Vector2 {

    public float x, y;

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 vec){
        return new Vector2(x + vec.x, y + vec.y);
    }

    public Vector2 middle(Vector2 another){
        return new Vector2((x + another.x) / 2f, (y + another.y) / 2f);
    }

    public Vector3 toVector3(){
        return new Vector3(x, y, 0);
    }

}
