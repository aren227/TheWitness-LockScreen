package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.graphics.shape.CircleShape;
import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.animation.Animation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorEndingPointReachedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorFailedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorSucceededAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.EliminatedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.EliminatorActivatedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.ErrorAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleAnimationManager;
import com.aren.thewitnesspuzzle.puzzle.animation.WaitForEliminationAnimation;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Puzzle {

    protected Game game;

    protected boolean staticShapesCalculated = false;
    protected ArrayList<Shape> staticShapes = new ArrayList<>();
    protected ArrayList<Shape> dynamicShapes = new ArrayList<>();

    protected PuzzleColorPalette color;

    protected float pathWidth;

    protected Cursor cursor;

    protected BoundingBox boundingBox = new BoundingBox();

    protected ArrayList<Vertex> vertices = new ArrayList<>();
    protected ArrayList<Edge> edges = new ArrayList<>();
    protected Edge[][] edgeTable; // Indexed by two vertices pair
    protected ArrayList<Tile> tiles = new ArrayList<>();

    protected PuzzleAnimationManager animation;

    public Puzzle(Game game, PuzzleColorPalette color){
        this.game = game;
        this.color = color;

        animation = new PuzzleAnimationManager(this);
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

    public PuzzleColorPalette getColorPalette(){
        return color;
    }

    public void calcStaticShapes(){
        if(pathWidth == 0) pathWidth = Math.min(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.05f + 0.05f;

        for(Vertex vertex : vertices){
            staticShapes.add(new CircleShape(new Vector3(vertex.x, vertex.y, 0), getPathWidth() * 0.5f, color.getPathColor()));
        }

        for(Edge edge : edges){
            staticShapes.add(new RectangleShape(edge.getMiddlePoint().toVector3(), edge.getLength(), getPathWidth(), edge.getAngle(), color.getPathColor()));
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
            dynamicShapes.add(new CircleShape(cursor.getFirstVisitedVertex().getPosition().toVector3(), ((StartingPointRule)cursor.getFirstVisitedVertex().getRule()).getRadius(), color.getCursorColor()));

            ArrayList<EdgeProportion> visitedEdges = cursor.getVisitedEdgesWithProportion(true);
            if(visitedEdges.size() == 0) return;
            for(EdgeProportion edgeProportion : visitedEdges){
                dynamicShapes.add(new CircleShape(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y, 0), getPathWidth() * 0.5f, color.getCursorColor()));
                dynamicShapes.add(new RectangleShape(edgeProportion.getProportionMiddlePoint().toVector3(), edgeProportion.getProportionLength(), getPathWidth(), edgeProportion.edge.getAngle(), color.getCursorColor()));
            }
        }
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
        cursor = createCursor(start);
        game.playSound(Sounds.START_TRACING);
    }

    protected void endTracing(){
        EdgeProportion currentCursorEdge = cursor.getCurrentCursorEdge();
        if(currentCursorEdge == null) return;
        Edge edge = currentCursorEdge.edge;
        if(currentCursorEdge.to().getRule() instanceof EndingPointRule && currentCursorEdge.proportion > 1 - getPathWidth() * 0.5f / edge.getLength()){
            resetAnimation();

            final ValidationResult result = validate();

            if(result.hasEliminatedRule()){
                game.playSound(Sounds.POTENTIAL_FAILURE);
                for(Rule rule : result.getOriginalErrors()){
                    addAnimation(new ErrorAnimation(rule, 2));
                }
                addAnimation(new WaitForEliminationAnimation(this, new Runnable() {
                    @Override
                    public void run() {
                        game.playSound(Sounds.ERASER_APPLY);
                        if(result.failed()){
                            for(Rule rule : result.getNewErrors()){
                                if(rule.eliminated) continue;
                                addAnimation(new ErrorAnimation(rule));
                            }
                            for(Rule rule : result.getEliminatedRules()){
                                addAnimation(new EliminatedAnimation(rule));
                            }
                            for(Rule rule : result.getEliminators()){
                                addAnimation(new EliminatorActivatedAnimation(rule));
                            }
                            addAnimation(new CursorFailedAnimation(Puzzle.this));
                            game.playSound(Sounds.FAILURE);
                        }
                        else{
                            for(Rule rule : result.getEliminatedRules()){
                                addAnimation(new EliminatedAnimation(rule));
                            }
                            for(Rule rule : result.getEliminators()){
                                addAnimation(new EliminatorActivatedAnimation(rule));
                            }
                            addAnimation(new CursorSucceededAnimation(Puzzle.this));
                            game.playSound(Sounds.SUCCESS);
                            game.solved();
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
                    game.playSound(Sounds.FAILURE);
                }
                else{
                    addAnimation(new CursorSucceededAnimation(this));
                    game.playSound(Sounds.SUCCESS);
                    game.solved();
                }
            }
        }
    }

    public void touchEvent(float x, float y, int action){
        Vector2 pos = new Vector2(x, y);
        if(action == MotionEvent.ACTION_DOWN){
            Vertex start = null;
            for(Vertex vertex : vertices){
                if(vertex.getRule() instanceof StartingPointRule && pos.distance(vertex.getPosition()) <= ((StartingPointRule)vertex.getRule()).getRadius()){
                    start = vertex;
                    break;
                }
            }
            if(start != null){
                startTracing(start);
            }
        }
        else if(action == MotionEvent.ACTION_MOVE){
            if(cursor == null) return;

            Edge edge = getNearestEdge(pos);
            EdgeProportion edgeProportion = new EdgeProportion(edge);
            edgeProportion.proportion = edgeProportion.getProportionFromPointOutside(pos);
            cursor.connectTo(edgeProportion);

            EdgeProportion cursorEdge = cursor.getCurrentCursorEdge();
            if(cursorEdge.to().getRule() instanceof EndingPointRule && cursorEdge.proportion > 1 - getPathWidth() * 0.5f / cursorEdge.edge.getLength()){
                if(!animation.isPlaying(CursorEndingPointReachedAnimation.class)){
                    animation.addAnimation(new CursorEndingPointReachedAnimation(this));
                }
            }
            else{
                animation.stopAnimation(CursorEndingPointReachedAnimation.class);
            }
        }
        else if(action == MotionEvent.ACTION_UP){
            EdgeProportion cursorEdge = cursor.getCurrentCursorEdge();
            if(cursorEdge.to().getRule() instanceof EndingPointRule && cursorEdge.proportion > 1 - getPathWidth() * 0.5f / cursorEdge.edge.getLength()){
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
        for(Rule rule : getAllRules()){
            if(!rule.validateLocally(cursor)){
                result.notOnAreaErrors.add(rule);
            }
        }
        return result;
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

    public List<Rule> getAllRules(){
        List<Rule> rules = new ArrayList<>();
        for(Vertex vertex : vertices){
            if(vertex.getRule() != null){
                rules.add(vertex.getRule());
            }
        }
        for(Edge edge : edges){
            if(edge.getRule() != null){
                rules.add(edge.getRule());
            }
        }
        for(Tile tile : tiles){
            if(tile.getRule() != null){
                rules.add(tile.getRule());
            }
        }
        return rules;
    }

    // For debugging
    public void setCursor(Cursor cursor){
        this.cursor = cursor;
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

        public List<Rule> getEliminators(){
            List<Rule> rules = new ArrayList<>();
            for(Area.AreaValidationResult result : areaValidationResults){
                for(Rule rule : result.area.getAllRules()){
                    if(rule instanceof EliminationRule){
                        rules.add(rule);
                    }
                }
            }
            return rules;
        }

        public List<Rule> getOriginalErrors(){
            List<Rule> rules = new ArrayList<>(notOnAreaErrors);
            for(Area.AreaValidationResult result : areaValidationResults){
                rules.addAll(result.originalErrors);
            }
            return rules;
        }

        public List<Rule> getNewErrors(){
            List<Rule> rules = new ArrayList<>(notOnAreaErrors);
            for(Area.AreaValidationResult result : areaValidationResults){
                if(result.eliminated) rules.addAll(result.newErrors);
                else rules.addAll(result.originalErrors);
            }
            return rules;
        }

    }

}
