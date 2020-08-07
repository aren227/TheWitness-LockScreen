package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPoint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;

public class Puzzle {

    protected Game game;

    protected ArrayList<Shape> staticShapes = new ArrayList<>();
    protected ArrayList<Shape> dynamicShapes = new ArrayList<>();

    protected int backgroundColor;
    protected int pathColor;
    protected int cursorColor;

    protected float pathWidth;

    protected boolean touching = false;
    protected Cursor cursor;

    protected BoundingBox boundingBox = new BoundingBox();

    protected ArrayList<Vertex> vertices = new ArrayList<>();
    protected ArrayList<Edge> edges = new ArrayList<>();
    protected ArrayList<Tile> tiles = new ArrayList<>();

    protected HashSet<Class<? extends Rule>> appliedRules = new HashSet<>();

    public Puzzle(Game game){
        this.game = game;
    }

    public int getVertexCount(){
        int vertexCount = 0;
        for(Shape shape : staticShapes){
            vertexCount += shape.getVertexCount();
        }
        for(Shape shape : dynamicShapes){
            vertexCount += shape.getVertexCount();
        }
        Log.i("PUZZLE", "Vertex Count: " + vertexCount);
        return vertexCount;
    }

    public FloatBuffer getVertexBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        for(Shape shape : staticShapes){
            shape.draw(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.draw(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    public FloatBuffer getVertexColorBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        //FIXME: ConcurrentModificationException
        for(Shape shape : staticShapes){
            shape.drawColor(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.drawColor(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    public void calcStaticShapes(){
        if(pathWidth == 0) pathWidth = Math.min(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.05f + 0.05f;

        for(Vertex vertex : vertices){
            staticShapes.add(new Circle(new Vector3(vertex.x, vertex.y, 0), getPathWidth() * 0.5f, getPathColor()));
        }

        for(Edge edge : edges){
            staticShapes.add(new Rectangle(edge.getMiddlePoint().toVector3(), edge.getLength(), getPathWidth(), edge.getAngle(), getPathColor()));
        }

        for(Vertex vertex : vertices){
            if(vertex.getRule() != null && vertex.getRule().getShape() != null) staticShapes.add(vertex.getRule().getShape());
        }

        for(Edge edge : edges){
            if(edge.getRule() != null && edge.getRule().getShape() != null) staticShapes.add(edge.getRule().getShape());
        }

        for(Tile tile : tiles){
            if(tile.getRule() != null && tile.getRule().getShape() != null) staticShapes.add(tile.getRule().getShape());
        }
    }

    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(touching){
            // It is guaranteed that edges[i - 1].to == edges[i].from
            ArrayList<Edge> visitedEdges = cursor.getVisitedEdges();
            if(visitedEdges.size() == 0) return;
            for(Edge edge : visitedEdges){
                dynamicShapes.add(new Circle(new Vector3(edge.from.x, edge.from.y, 0), getPathWidth() * 0.5f, getCursorColor()));
                dynamicShapes.add(new Rectangle(edge.getProportionMiddlePoint().toVector3(), edge.getLength() * edge.proportion, getPathWidth(), edge.getAngle(), getCursorColor()));
            }
            Edge lastEdge = visitedEdges.get(visitedEdges.size() - 1);
            dynamicShapes.add(new Circle(new Vector3(lastEdge.getProportionPoint().x, lastEdge.getProportionPoint().y, 0), getPathWidth() * 0.5f, getCursorColor()));
        }
    }

    public void setBackgroundColor(int color){
        backgroundColor = color;
    }

    public int getBackgroundColor(){
        return backgroundColor;
    }

    public void setPathColor(int color){
        pathColor = color;
    }

    public int getPathColor(){
        return pathColor;
    }

    public void setCursorColor(int color){
        cursorColor = color;
    }

    public int getCursorColor(){
        return cursorColor;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public float getPadding(){
        return Math.min(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.1f;
    }

    public void setPathWidth(float pathWidth){
        this.pathWidth = pathWidth;
    }

    public float getPathWidth() {
        return pathWidth;
    }

    public boolean touchEvent(float x, float y, int action){
        Vector2 pos = new Vector2(x, y);
        if(action == MotionEvent.ACTION_DOWN){
            Vertex start = null;
            for(Vertex vertex : vertices){
                if(vertex.getRule() instanceof StartingPoint && pos.distance(vertex.getPosition()) <= ((StartingPoint)vertex.getRule()).getRadius()){
                    start = vertex;
                    break;
                }
            }
            if(start != null){
                touching = true;
                cursor = new Cursor(this, start);
            }
        }
        else if(action == MotionEvent.ACTION_MOVE){
            if(!touching) return false;
            Edge edge = getNearestEdge(pos).clone();
            edge.proportion = edge.getProportionFromPointOutside(pos);
            Log.i("PUZZLE", "Edge: " + edge.index + ", Calced Proportion: " + edge.proportion);
            cursor.connectTo(edge);
        }
        else if(action == MotionEvent.ACTION_UP){
            if(touching){
                Edge cursorEdge = cursor.getCurrentCursorEdge();
                if(cursorEdge.to.getRule() instanceof EndingPoint && cursorEdge.proportion > 1 - getPathWidth() * 0.5f / cursorEdge.getLength()){
                    return validate();
                }
                touching = false;
            }
        }
        return false;
    }

    public boolean validate(){
        return true;
    }

    public HashSet<Class<? extends Rule>> getAppliedRules(){
        return appliedRules;
    }

    public Vertex addVertex(Vertex vertex){
        vertex.index = vertices.size();
        vertices.add(vertex);
        boundingBox.addCircle(new Vector2(vertex.x, vertex.y), 0.5f);
        return vertex;
    }

    public Edge addEdge(Edge edge){
        edge.index = edges.size();
        edges.add(edge);
        return edge;
    }

    public Edge getNearestEdge(Vector2 pos){
        float minDist = Float.MAX_VALUE;
        Edge minEdge = null;
        for(Edge edge : edges){
            float dist = edge.getDistance(pos);
            if(dist < minDist){
                minDist = dist;
                minEdge = edge;
            }
        }
        return minEdge;
    }

    public ArrayList<Vertex> getVertices(){
        return vertices;
    }

    public ArrayList<Edge> getEdges(){
        return edges;
    }

    public ArrayList<Tile> getTiles(){
        return tiles;
    }

}
