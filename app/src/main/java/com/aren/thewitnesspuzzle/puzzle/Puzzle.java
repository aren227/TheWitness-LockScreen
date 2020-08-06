package com.aren.thewitnesspuzzle.puzzle;

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

    private Game game;

    private ArrayList<Shape> staticShapes = new ArrayList<>();
    private ArrayList<Shape> dynamicShapes = new ArrayList<>();

    private int backgroundColor;
    private int pathColor;
    private int cursorColor;

    private float pathWidth;

    private boolean touching = false;
    private Cursor cursor;

    private BoundingBox boundingBox = new BoundingBox();

    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Tile> tiles = new ArrayList<>();

    HashSet<Class<? extends Rule>> appliedRules = new HashSet<>();

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
        for(Vertex vertex : vertices){
            staticShapes.add(new Circle(new Vector3(vertex.x, vertex.y, 0), getPathWidth() * 0.5f, getPathColor()));
        }

        for(Edge edge : edges){
            staticShapes.add(new Rectangle(edge.getMiddlePoint().toVector3(), edge.getLength(), getPathWidth(), edge.getAngle(), getPathColor()));
        }

        for(Vertex vertex : vertices){
            if(vertex.rule != null) staticShapes.add(vertex.rule.getShape());
        }

        for(Edge edge : edges){
            if(edge.rule != null) staticShapes.add(edge.rule.getShape());
        }

        for(Tile tile : tiles){
            if(tile.rule != null) staticShapes.add(tile.rule.getShape());
        }
    }

    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(touching){
            // It is guaranteed that edges[i - 1].to == edges[i].from
            for(Edge edge : cursor.getVisitedEdges()){
                dynamicShapes.add(new Circle(new Vector3(edge.from.x, edge.from.y, 0), getPathWidth() * 0.5f, getCursorColor()));
                dynamicShapes.add(new Rectangle(edge.getProportionMiddlePoint().toVector3(), edge.getLength() * edge.proportion, getPathWidth(), edge.getAngle(), getCursorColor()));
            }
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

    public void touchEvent(float x, float y, int action){

    }

    public boolean validate(){
        return true;
    }

    public void addRule(Rule rule){
        rule.graphElement.setRule(rule);
        appliedRules.add(rule.getClass());
    }

}
