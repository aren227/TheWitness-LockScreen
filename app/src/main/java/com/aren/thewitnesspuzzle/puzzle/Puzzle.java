package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.graphics.shape.Circle;
import com.aren.thewitnesspuzzle.graphics.shape.Rectangle;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.animation.Animation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorFailedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.EliminatedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.ErrorAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.WaitAnimation;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
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
import java.util.List;

public class Puzzle {

    protected Game game;

    protected boolean staticShapesCalculated = false;
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
    protected Edge[][] edgeTable; // Indexed by two vertices pair
    protected ArrayList<Tile> tiles = new ArrayList<>();

    protected HashSet<Class<? extends Rule>> appliedRules = new HashSet<>();

    protected PuzzleAnimation animation;

    public Puzzle(Game game){
        this.game = game;

        animation = new PuzzleAnimation(this);
    }

    public void updateDynamicShapes(){
        for(Shape shape : dynamicShapes){
            shape.draw();
        }
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
            shape.fillVertexBuffer(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.fillVertexBuffer(vertexBuffer);
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
            shape.fillColorBuffer(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.fillColorBuffer(vertexBuffer);
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

        for(Shape shape : staticShapes){
            shape.draw();
        }
    }

    public void prepareForDrawing(){
        if(!staticShapesCalculated){
            calcStaticShapes();
            staticShapesCalculated = true;
        }
        calcDynamicShapes();
    }

    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(cursor != null){
            dynamicShapes.add(new Circle(cursor.getFirstVisitedVertex().getPosition().toVector3(), ((StartingPoint)cursor.getFirstVisitedVertex().getRule()).getRadius(), getCursorColor()));

            ArrayList<EdgeProportion> visitedEdges = cursor.getVisitedEdgesWithProportion(true);
            if(visitedEdges.size() == 0) return;
            for(EdgeProportion edgeProportion : visitedEdges){
                dynamicShapes.add(new Circle(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y, 0), getPathWidth() * 0.5f, getCursorColor()));
                dynamicShapes.add(new Rectangle(edgeProportion.getProportionMiddlePoint().toVector3(), edgeProportion.getProportionLength(), getPathWidth(), edgeProportion.edge.getAngle(), getCursorColor()));
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

    protected void startTracing(Vertex start){
        resetAnimation();
        touching = true;
        cursor = createCursor(start);

        for(Vertex vertex : vertices){
            if(vertex.getRule() != null){
                vertex.getRule().eliminated = false;
                if(vertex.getRule().getShape() != null) vertex.getRule().getShape().scale = 1f;
            }
        }
        for(Edge edge : edges){
            if(edge.getRule() != null){
                edge.getRule().eliminated = false;
                if(edge.getRule().getShape() != null) edge.getRule().getShape().scale = 1f;
            }
        }
        for(Tile tile : tiles){
            if(tile.getRule() != null){
                tile.getRule().eliminated = false;
                if(tile.getRule().getShape() != null) tile.getRule().getShape().scale = 1f;
            }
        }
    }

    protected void endTracing(){
        touching = false;
        EdgeProportion currentCursorEdge = cursor.getCurrentCursorEdge();
        Edge edge = currentCursorEdge.edge;
        if(currentCursorEdge.to().getRule() instanceof EndingPoint && currentCursorEdge.proportion > 1 - getPathWidth() * 0.5f / edge.getLength()){
            final ValidationResult result = validate();

            if(result.hasEliminatedRule()){
                for(Rule rule : result.getOriginalErrors()){
                    addAnimation(new ErrorAnimation(rule, 2));
                }
                addAnimation(new WaitAnimation(this, new Runnable() {
                    @Override
                    public void run() {
                        if(result.failed()){
                            for(Rule rule : result.getNewErrors()){
                                addAnimation(new ErrorAnimation(rule));
                            }
                            for(Rule rule : result.getEliminatedRules()){
                                addAnimation(new EliminatedAnimation(rule));
                            }
                            addAnimation(new CursorFailedAnimation(Puzzle.this));
                        }
                        else{
                            game.close();
                        }
                    }
                }));
            }
            else{
                if(result.failed()){
                    for(Rule rule : result.getOriginalErrors()){
                        addAnimation(new ErrorAnimation(rule));
                    }
                    addAnimation(new CursorFailedAnimation(this));
                }
                else{
                    game.close();
                }
            }
        }
    }

    public void touchEvent(float x, float y, int action){
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
                startTracing(start);
            }
        }
        else if(action == MotionEvent.ACTION_MOVE){
            if(!touching) return;

            Edge edge = getNearestEdge(pos);
            EdgeProportion edgeProportion = new EdgeProportion(edge);
            edgeProportion.proportion = edgeProportion.getProportionFromPointOutside(pos);
            cursor.connectTo(edgeProportion);
        }
        else if(action == MotionEvent.ACTION_UP){
            if(touching){
                endTracing();
            }
        }
    }

    public boolean shouldUpdateAnimation(){
        return animation.shouldUpdate();
    }

    public void updateAnimation(){
        animation.process();
    }

    public void resetAnimation(){
        animation.reset();
    }

    public void addAnimation(Animation animation){
        this.animation.addAnimation(animation);
    }

    public void clearCursor(){
        cursor = null;
    }

    public ValidationResult validate(){
        ValidationResult result = new ValidationResult();
        //TODO: Support area validation
        for(Vertex vertex : vertices){
            if(vertex.getRule() != null && !vertex.getRule().validateLocally(cursor)){
                result.notOnAreaErrors.add(vertex.getRule());
            }
        }
        for(Edge edge : edges){
            if(edge.getRule() != null && !edge.getRule().validateLocally(cursor)){
                result.notOnAreaErrors.add(edge.getRule());
            }
        }
        for(Tile tile : tiles){
            if(tile.getRule() != null && !tile.getRule().validateLocally(cursor)){
                result.notOnAreaErrors.add(tile.getRule());
            }
        }
        return result;
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

    public Tile addTile(Tile tile){
        tile.index = tiles.size();
        tiles.add(tile);
        return tile;
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

    public void calcEdgeTable(){
        edgeTable = new Edge[getVertices().size()][getVertices().size()];
        for(Edge edge : getEdges()){
            edgeTable[edge.from.index][edge.to.index] = edge;
            edgeTable[edge.to.index][edge.from.index] = edge;
        }
    }

    public Edge getEdgeByVertex(Vertex from, Vertex to){
        if(edgeTable == null) calcEdgeTable();
        return edgeTable[from.index][to.index];
    }

    protected Cursor createCursor(Vertex start){
        return new Cursor(this, start);
    }

    public class ValidationResult{

        public List<Rule> notOnAreaErrors = new ArrayList<>();
        public List<Area.AreaValidationResult> areaValidationResults = new ArrayList<>();

        public boolean failed(){
            if(notOnAreaErrors.size() > 0) return true;
            for(Area.AreaValidationResult result : areaValidationResults){
                if(!result.eliminated && result.originalErrors.size() > 0) return true;
                if(result.eliminated && result.newErrors.size() > 0) return true;
            }
            return false;
        }

        public boolean hasEliminatedRule(){
            for(Area.AreaValidationResult result : areaValidationResults){
                if(result.eliminated) return true;
            }
            return false;
        }

        public List<Rule> getEliminatedRules(){
            List<Rule> rules = new ArrayList<>();
            for(Area.AreaValidationResult result : areaValidationResults){
                for(Rule rule : result.originalErrors){
                    if(rule.eliminated){
                        rules.add(rule);
                    }
                }
            }
            return rules;
        }

        public List<Rule> getOriginalErrors(){
            List<Rule> rules = new ArrayList<>();
            for(Area.AreaValidationResult result : areaValidationResults){
                rules.addAll(result.originalErrors);
            }
            return rules;
        }

        public List<Rule> getNewErrors(){
            List<Rule> rules = new ArrayList<>();
            for(Area.AreaValidationResult result : areaValidationResults){
                rules.addAll(result.newErrors);
            }
            return rules;
        }

    }

}
