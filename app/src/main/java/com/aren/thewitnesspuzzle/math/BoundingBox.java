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

    public void addCircle(Vector2 center, float radius){
        if(min == null || max == null){
            min = center.add(new Vector2(-radius, -radius));
            max = center.add(new Vector2(radius, radius));
        }
        else{
            min.x = Math.min(min.x, center.x - radius);
            min.y = Math.min(min.y, center.y - radius);
            max.x = Math.max(max.x, center.x + radius);
            max.y = Math.max(max.y, center.y + radius);
        }
    }

    public BoundingBox expand(float padding){
        BoundingBox bb = new BoundingBox();
        bb.min = min.add(new Vector2(-padding, -padding));
        bb.max = max.add(new Vector2(padding, padding));
        return bb;
    }

    public boolean test(Vector2 point){
        return min.x <= point.x && point.x <= max.x && min.y <= point.y && point.y <= max.y;
    }

}
