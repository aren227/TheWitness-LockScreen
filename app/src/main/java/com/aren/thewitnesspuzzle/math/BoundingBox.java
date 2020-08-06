package com.aren.thewitnesspuzzle.math;

public class BoundingBox {

    public Vector2 min, max;

    public BoundingBox(){

    }

    public float getWidth(){
        return max.x - min.x;
    }

    public float getHeight(){
        return max.y - min.y;
    }

    public Vector2 getCenter(){
        return new Vector2((min.x + max.x) / 2, (min.y + max.y) / 2);
    }

    public void addPoint(Vector2 point){
        if(min == null || max == null){
            min = max = point;
        }
        else{
            min.x = Math.min(min.x, point.x);
            min.y = Math.min(min.y, point.y);
            max.x = Math.max(max.x, point.x);
            max.y = Math.max(max.y, point.y);
        }
    }

}
