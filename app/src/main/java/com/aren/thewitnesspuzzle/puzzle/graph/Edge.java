package com.aren.thewitnesspuzzle.puzzle.graph;

import android.util.Log;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Cursor;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public class Edge extends GraphElement{

    public Vertex from, to;
    public float proportion; //from -> to [0, 1]

    public Edge(Vertex from, Vertex to){
        super(from.puzzle);
        this.from = from;
        this.to = to;
    }

    public Edge reverse(){
        Edge edge = new Edge(to, from);
        edge.index = index;
        edge.proportion = 1 - proportion;
        edge.setRule(getRule());
        return edge;
    }

    @Override
    public Edge clone(){
        Edge edge = new Edge(to, from);
        edge.index = index;
        edge.proportion = proportion;
        edge.setRule(getRule());
        return edge;
    }

    public void updateProportion(Cursor cursor, Edge edge){
        if(!this.equals(edge)) return;

        float nextProportion;
        if(from == edge.from) nextProportion = edge.proportion;
        else nextProportion = 1 - edge.proportion;

        cursor.updateProportionWithCollision(this, proportion, nextProportion);
    }

    public boolean containsVertex(Vertex vertex){
        return from == vertex || to == vertex;
    }

    public Vertex getAnotherVertex(Vertex vertex){
        if(vertex == from) return to;
        else return from;
    }

    public float getLength(){
        return (float)Math.sqrt((from.x - to.x) * (from.x - to.x) + (from.y - to.y) * (from.y - to.y));
    }

    public Vector2 getMiddlePoint(){
        return new Vector2((from.x + to.x) / 2, (from.y + to.y) / 2);
    }

    public Vector2 getProportionPoint(){
        return new Vector2(MathUtils.lerp(from.x, to.x, proportion), MathUtils.lerp(from.y, to.y, proportion));
    }

    public Vector2 getProportionMiddlePoint(){
        return new Vector2(MathUtils.lerp(from.x, to.x, proportion * 0.5f), MathUtils.lerp(from.y, to.y, proportion * 0.5f));
    }

    public float getAngle(){
        return (float)Math.atan2(to.y - from.y, to.x - from.x);
    }

    public float getDistance(Vector2 point){
        Vector2 startToEnd = new Vector2(to.x - from.x, to.y - from.y);
        Vector2 startToPoint = new Vector2(point.x - from.x, point.y - from.y);
        Vector2 endToPoint = new Vector2(point.x - to.x, point.y - to.y);

        if(startToEnd.dot(startToPoint) < 0){
            return from.getPosition().distance(point);
        }
        if(startToEnd.dot(endToPoint) > 0){
            return to.getPosition().distance(point);
        }

        Vector2 proj = startToPoint.projection(startToEnd);
        return proj.subtract(startToPoint).length();
    }

    public float getProportionFromPointOutside(Vector2 point){
        Vector2 startToEnd = new Vector2(to.x - from.x, to.y - from.y);
        Vector2 startToPoint = new Vector2(point.x - from.x, point.y - from.y);
        if(startToEnd.dot(startToPoint) < 0) return 0;
        Vector2 proj = startToPoint.projection(startToEnd);
        return Math.min(proj.length() / startToEnd.length(), 1);
    }

    public float getProportionFromVertex(Vertex vertex){
        if(vertex == from) return 0;
        else return 1;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Edge){
            Edge e = (Edge) obj;
            return (from == e.from && to == e.to || from == e.to && to == e.from);
        }
        return false;
    }

    @Override
    public Vector2 getPosition(){
        return getMiddlePoint();
    }

}
