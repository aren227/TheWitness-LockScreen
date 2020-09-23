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
import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;
import com.aren.thewitnesspuzzle.puzzle.color.ColorUtils;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
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
    protected BoundingBox originalBoundingBox;
    protected BoundingBox shadowBoundingBox;

    protected ArrayList<Vertex> vertices = new ArrayList<>();
    protected ArrayList<Edge> edges = new ArrayList<>();
    protected Edge[][] edgeTable; // Indexed by two vertices pair
    protected ArrayList<Tile> tiles = new ArrayList<>();

    protected PuzzleAnimationManager animation;

    protected boolean shadowPanel;

    protected List<Integer> customPattern;

    protected boolean untouchable = false;

    protected Value<Float> fadeIntensity = new Value<>(1f);

    public Puzzle(Game game, PuzzleColorPalette color) {
        this(game, color, game.isPlayMode() && game.getSettings().getShadowPanelEnabled());
    }

    public Puzzle(Game game, PuzzleColorPalette color, boolean shadowPanel) {
        this.game = game;
        this.color = color;
        this.shadowPanel = shadowPanel;

        animation = new PuzzleAnimationManager(this);
    }

    public Game getGame() {
        return game;
    }

    public void updateDynamicShapes() {
        for (Shape shape : dynamicShapes) {
            shape.draw();
        }
    }

    public int getVertexCount() {
        int vertexCount = 0;
        for (Shape shape : staticShapes) {
            vertexCount += shape.getVertexCount();
        }
        for (Shape shape : dynamicShapes) {
            vertexCount += shape.getVertexCount();
        }
        //Log.i("PUZZLE", "Vertex Count: " + vertexCount);
        return vertexCount;
    }

    public FloatBuffer getVertexBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        for (Shape shape : staticShapes) {
            shape.fillVertexBuffer(vertexBuffer);
        }
        for (Shape shape : dynamicShapes) {
            shape.fillVertexBuffer(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    public int getIndexCount(){
        int indexCount = 0;
        for(Shape shape : staticShapes){
            indexCount += shape.getIndexCount();
        }
        for(Shape shape : dynamicShapes){
            indexCount += shape.getIndexCount();
        }
        return indexCount;
    }

    public ShortBuffer getIndexBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getIndexCount() * 2);
        bb.order(ByteOrder.nativeOrder());

        ShortBuffer indexBuffer = bb.asShortBuffer();

        for(Shape shape : staticShapes){
            shape.fillIndexBuffer(indexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.fillIndexBuffer(indexBuffer);
        }

        indexBuffer.position(0);

        return indexBuffer;
    }

    public FloatBuffer getVertexColorBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        //FIXME: ConcurrentModificationException
        for (Shape shape : staticShapes) {
            shape.fillColorBuffer(vertexBuffer);
        }
        for (Shape shape : dynamicShapes) {
            shape.fillColorBuffer(vertexBuffer);
        }

        vertexBuffer.position(0);
        float fi = fadeIntensity.get();
        for(int i = 0; i < vertexBuffer.limit(); i++){
            vertexBuffer.put(i, vertexBuffer.get(i) * fi);
        }

        return vertexBuffer;
    }

    public PuzzleColorPalette getColorPalette() {
        return color;
    }

    public void calcStaticShapes() {
        staticShapes.clear();

        if (pathWidth == 0)
            pathWidth = Math.max(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.05f + 0.05f;

        ArrayList<Shape> shadow = new ArrayList<>();
        int shadowPathColor = ColorUtils.lerp(color.getBackgroundColor(), color.getPathColor(), 0.4f);

        for (Vertex vertex : vertices) {
            staticShapes.add(new CircleShape(new Vector3(vertex.x, vertex.y, 0), getPathWidth() * 0.5f, color.getPathColor()));
            if (shadowPanel) {
                shadow.add(new CircleShape(new Vector3(vertex.x, vertex.y - boundingBox.getHeight(), 0), getPathWidth() * 0.5f, shadowPathColor));
            }
        }

        for (Edge edge : edges) {
            staticShapes.add(new RectangleShape(edge.getMiddlePoint().toVector3(), edge.getLength(), getPathWidth(), edge.getAngle(), color.getPathColor()));
            if (shadowPanel) {
                shadow.add(new RectangleShape(edge.getMiddlePoint().toVector3().add(new Vector3(0, -boundingBox.getHeight(), 0)), edge.getLength(), getPathWidth(), edge.getAngle(), shadowPathColor));
            }
        }

        for (Vertex vertex : vertices) {
            if (vertex.getRule() != null && vertex.getRule().getShape() != null) {
                // getShape() reads from cache, but some rules need to be updated when changing colors. (ex, StartingPoint, BrokenLine)
                if (vertex.getRule() instanceof StartingPointRule || vertex.getRule() instanceof BrokenLineRule) {
                    staticShapes.add(vertex.getRule().generateShape());
                } else {
                    staticShapes.add(vertex.getRule().getShape());
                }

                if (shadowPanel && vertex.getRule() instanceof StartingPointRule) {
                    Shape shape = vertex.getRule().generateShape(); // clone
                    shape.center = shape.center.add(new Vector3(0, -boundingBox.getHeight(), 0));
                    shape.color.set(shadowPathColor);
                    shadow.add(shape);
                }
            }
        }

        for (Edge edge : edges) {
            // BrokenLine
            if (edge.getRule() != null && edge.getRule().getShape() != null)
                staticShapes.add(edge.getRule().generateShape());
        }

        for (Tile tile : tiles) {
            if (tile.getRule() != null && tile.getRule().getShape() != null)
                staticShapes.add(tile.getRule().getShape());
        }

        if (shadowPanel) {
            originalBoundingBox = boundingBox.clone();

            shadowBoundingBox = boundingBox.clone();
            shadowBoundingBox.min.y -= boundingBox.getHeight();
            shadowBoundingBox.max.y -= boundingBox.getHeight();

            boundingBox.min.y -= boundingBox.getHeight();
            staticShapes.addAll(shadow);
        }

        for (Shape shape : staticShapes) {
            shape.draw();
        }
    }

    public void prepareForDrawing() {
        if (!staticShapesCalculated) {
            calcStaticShapes();
            staticShapesCalculated = true;
            Log.i("PUZZLE", "Static shapes calculated (" + staticShapes.size() + ")");
        }
        calcDynamicShapes();
    }

    public void calcDynamicShapes() {
        dynamicShapes.clear();

        int shadowCursorColor = ColorUtils.lerp(ColorUtils.lerp(color.getBackgroundColor(), color.getPathColor(), 0.4f), color.getCursorColor(), 0.4f);

        if (cursor != null) {
            dynamicShapes.add(new CircleShape(cursor.getFirstVisitedVertex().getPosition().toVector3(), ((StartingPointRule) cursor.getFirstVisitedVertex().getRule()).getRadius(), color.getCursorColor()));
            if (shadowPanel) {
                dynamicShapes.add(new CircleShape(cursor.getFirstVisitedVertex().getPosition().toVector3().add(new Vector3(0, -originalBoundingBox.getHeight(), 0)), ((StartingPointRule) cursor.getFirstVisitedVertex().getRule()).getRadius(), shadowCursorColor));
            }

            ArrayList<EdgeProportion> visitedEdges = cursor.getVisitedEdgesWithProportion(true);
            if (visitedEdges.size() == 0) return;
            for (EdgeProportion edgeProportion : visitedEdges) {
                dynamicShapes.add(new CircleShape(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y, 0), getPathWidth() * 0.5f, color.getCursorColor()));
                dynamicShapes.add(new RectangleShape(edgeProportion.getProportionMiddlePoint().toVector3(), edgeProportion.getProportionLength(), getPathWidth(), edgeProportion.edge.getAngle(), color.getCursorColor()));

                if (shadowPanel) {
                    dynamicShapes.add(new CircleShape(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y - originalBoundingBox.getHeight(), 0), getPathWidth() * 0.5f, shadowCursorColor));
                    dynamicShapes.add(new RectangleShape(edgeProportion.getProportionMiddlePoint().toVector3().add(new Vector3(0, -originalBoundingBox.getHeight(), 0)), edgeProportion.getProportionLength(), getPathWidth(), edgeProportion.edge.getAngle(), shadowCursorColor));
                }
            }
        }
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public float getPadding() {
        return Math.min(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.1f;
    }

    public void setPathWidth(float pathWidth) {
        this.pathWidth = pathWidth;
    }

    public float getPathWidth() {
        return pathWidth;
    }

    protected void startTracing(Vertex start) {
        resetAnimation();
        cursor = createCursor(start);
        game.playSound(Sounds.START_TRACING);
    }

    protected void endTracing() {
        EdgeProportion currentCursorEdge = cursor.getCurrentCursorEdge();
        if (currentCursorEdge == null) return;
        Edge edge = currentCursorEdge.edge;
        if (currentCursorEdge.to().getRule() instanceof EndingPointRule && currentCursorEdge.proportion > 1 - getPathWidth() * 0.5f / edge.getLength()) {
            resetAnimation();

            final ValidationResult result = validate();

            if (result.hasEliminatedRule()) {
                game.playSound(Sounds.POTENTIAL_FAILURE);
                for (Rule rule : result.getOriginalErrors()) {
                    addAnimation(new ErrorAnimation(rule, 2));
                }
                addAnimation(new WaitForEliminationAnimation(this, new Runnable() {
                    @Override
                    public void run() {
                        game.playSound(Sounds.ERASER_APPLY);
                        if (result.failed()) {
                            for (Rule rule : result.getNewErrors()) {
                                if (rule.eliminated) continue;
                                addAnimation(new ErrorAnimation(rule));
                            }
                            for (Rule rule : result.getEliminatedRules()) {
                                addAnimation(new EliminatedAnimation(rule));
                            }
                            for (Rule rule : result.getEliminators()) {
                                addAnimation(new EliminatorActivatedAnimation(rule));
                            }
                            addAnimation(new CursorFailedAnimation(Puzzle.this));
                            game.playSound(Sounds.FAILURE);
                        } else {
                            for (Rule rule : result.getEliminatedRules()) {
                                addAnimation(new EliminatedAnimation(rule));
                            }
                            for (Rule rule : result.getEliminators()) {
                                addAnimation(new EliminatorActivatedAnimation(rule));
                            }
                            addAnimation(new CursorSucceededAnimation(Puzzle.this));
                            game.playSound(Sounds.SUCCESS);
                            game.solved();
                        }
                    }
                }));
            } else {
                if (result.failed()) {
                    for (Rule rule : result.getOriginalErrors()) {
                        addAnimation(new ErrorAnimation(rule));
                    }
                    addAnimation(new CursorFailedAnimation(this));
                    game.playSound(Sounds.FAILURE);
                } else {
                    addAnimation(new CursorSucceededAnimation(this));
                    game.playSound(Sounds.SUCCESS);
                    game.solved();
                }
            }
        }
    }

    public void touchEvent(float x, float y, int action) {
        if (untouchable) return;

        Vector2 pos = new Vector2(x, y);
        if (shadowPanel) {
            pos.y += originalBoundingBox.getHeight();
        }

        if (action == MotionEvent.ACTION_DOWN) {
            Vertex start = null;
            for (Vertex vertex : vertices) {
                if (vertex.getRule() instanceof StartingPointRule && pos.distance(vertex.getPosition()) <= ((StartingPointRule) vertex.getRule()).getRadius() * 1.3f) {
                    start = vertex;
                    break;
                }
            }
            if (start != null) {
                startTracing(start);
            } else if (cursor != null) {
                float padding = game.getDPScale(48);
                if (shadowPanel && !originalBoundingBox.test(pos) && (cursor.getCurrentCursorEdge() == null || cursor.getCurrentCursorEdge().getProportionPoint().distance(pos) > padding * 2)
                        || !shadowPanel && !boundingBox.test(pos) && (cursor.getCurrentCursorEdge() == null || cursor.getCurrentCursorEdge().getProportionPoint().distance(pos) > padding * 2)) {
                    cursor = null;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (cursor == null) return;

            Edge edge = getNearestEdge(pos);
            EdgeProportion edgeProportion = new EdgeProportion(edge);
            edgeProportion.proportion = edgeProportion.getProportionFromPointOutside(pos);
            cursor.connectTo(edgeProportion);

            EdgeProportion cursorEdge = cursor.getCurrentCursorEdge();
            if (cursorEdge == null) return;
            if (cursorEdge.to().getRule() instanceof EndingPointRule && cursorEdge.proportion > 1 - getPathWidth() * 0.5f / cursorEdge.edge.getLength()) {
                if (!animation.isPlaying(CursorEndingPointReachedAnimation.class)) {
                    animation.addAnimation(new CursorEndingPointReachedAnimation(this));
                }
            } else {
                animation.stopAnimation(CursorEndingPointReachedAnimation.class);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (cursor == null) return;

            EdgeProportion cursorEdge = cursor.getCurrentCursorEdge();
            if (cursorEdge == null) return;

            if (cursorEdge.to().getRule() instanceof EndingPointRule && cursorEdge.proportion > 1 - getPathWidth() * 0.5f / cursorEdge.edge.getLength()) {
                endTracing();
            }
        }
    }

    public boolean shouldUpdateAnimation() {
        return animation.shouldUpdate();
    }

    public void updateAnimation() {
        animation.process();
    }

    public void resetAnimation() {
        animation.reset();
    }

    public void addAnimation(Animation animation) {
        this.animation.addAnimation(animation);
    }

    public void clearCursor() {
        cursor = null;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public ValidationResult validate() {
        if (customPattern != null && customPattern.size() > 0) {
            ValidationResult result = new ValidationResult();
            result.forceFail = true;
            List<Integer> visited = cursor.getVisitedVertexIndices();
            if (customPattern.size() != visited.size()) {
                return result;
            }
            for (int i = 0; i < customPattern.size(); i++) {
                if (!customPattern.get(i).equals(visited.get(i))) {
                    return result;
                }
            }
            result.forceFail = false;
            return result;
        }

        if (this instanceof GridPuzzle) {
            GridAreaSplitter splitter = new GridAreaSplitter(cursor);
            ValidationResult result = new ValidationResult();
            for (Area area : splitter.areaList) {
                result.areaValidationResults.add(area.validate(cursor));
            }

            //FIXME: Dirty code again. I think getVisitedVerticies() of SymmetryCursor should return with opposite vertices.
            List<Rule> rules = new ArrayList<>();
            for (Vertex vertex : cursor.getVisitedVertices()) {
                if (vertex.getRule() != null) rules.add(vertex.getRule());
                if (this instanceof GridSymmetryPuzzle) {
                    Vertex opposite = ((GridSymmetryPuzzle) this).getOppositeVertex(vertex);
                    if (opposite.getRule() != null) rules.add(opposite.getRule());
                }
            }
            for (Edge edge : cursor.getFullyVisitedEdges()) {
                if (edge.getRule() != null) rules.add(edge.getRule());
                if (this instanceof GridSymmetryPuzzle) {
                    Edge opposite = ((GridSymmetryPuzzle) this).getOppositeEdge(edge);
                    if (opposite.getRule() != null) rules.add(opposite.getRule());
                }
            }

            for (Rule rule : rules) {
                if (!rule.validateLocally(cursor)) {
                    result.notOnAreaErrors.add(rule);
                }
            }

            return result;
        } else {
            ValidationResult result = new ValidationResult();
            //TODO: Support area validation
            for (Rule rule : getAllRules()) {
                if (!rule.validateLocally(cursor)) {
                    result.notOnAreaErrors.add(rule);
                }
            }
            return result;
        }
    }

    public Vertex addVertex(Vertex vertex) {
        return addVertex(vertex, false);
    }

    public Vertex addVertex(Vertex vertex, boolean bypassBoundingBox) {
        vertex.index = vertices.size();
        vertices.add(vertex);
        if (!bypassBoundingBox) boundingBox.addCircle(new Vector2(vertex.x, vertex.y), 0.5f);
        return vertex;
    }

    public Vertex getVertex(int index) {
        for (Vertex vertex : vertices) {
            if (vertex.index == index) return vertex;
        }
        return null;
    }

    public List<Vertex> getConnectedVertices(Vertex vertex) {
        List<Vertex> result = new ArrayList<>();
        for (Edge edge : getEdges()) {
            if (edge.from == vertex) {
                result.add(edge.to);
            } else if (edge.to == vertex) {
                result.add(edge.from);
            }
        }
        return result;
    }

    public Edge addEdge(int va, int vb){
        Edge edge = new Edge(getVertex(va), getVertex(vb));
        return addEdge(edge);
    }

    public Edge addEdge(Edge edge) {
        edge.index = edges.size();
        edges.add(edge);
        edge.from.adj.add(edge.to);
        edge.to.adj.add(edge.from);
        return edge;
    }

    public Tile addTile(Tile tile) {
        tile.index = tiles.size();
        tiles.add(tile);
        return tile;
    }

    public Edge getNearestEdge(Vector2 pos) {
        float minDist = Float.MAX_VALUE;
        Edge minEdge = null;
        for (Edge edge : edges) {
            float dist = edge.getDistance(pos);
            if (dist < minDist) {
                minDist = dist;
                minEdge = edge;
            }
        }
        return minEdge;
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void calcEdgeTable() {
        edgeTable = new Edge[getVertices().size()][getVertices().size()];
        for (Edge edge : getEdges()) {
            edgeTable[edge.from.index][edge.to.index] = edge;
            edgeTable[edge.to.index][edge.from.index] = edge;
        }
    }

    public Edge getEdgeByVertex(Vertex from, Vertex to) {
        if (edgeTable == null) calcEdgeTable();
        return edgeTable[from.index][to.index];
    }

    protected Cursor createCursor(Vertex start) {
        return new Cursor(this, start);
    }

    public List<Rule> getAllRules() {
        List<Rule> rules = new ArrayList<>();
        for (Vertex vertex : vertices) {
            if (vertex.getRule() != null) {
                rules.add(vertex.getRule());
            }
        }
        for (Edge edge : edges) {
            if (edge.getRule() != null) {
                rules.add(edge.getRule());
            }
        }
        for (Tile tile : tiles) {
            if (tile.getRule() != null) {
                rules.add(tile.getRule());
            }
        }
        return rules;
    }

    // For debugging
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public boolean hasShadowPanel() {
        return shadowPanel;
    }

    public void setColorPalette(PuzzleColorPalette color) {
        this.color = color;
    }

    public void setCustomPattern(List<Integer> customPattern) {
        this.customPattern = customPattern;
    }

    public List<Integer> getCustomPattern() {
        return customPattern;
    }

    public void shouldUpdateStaticShapes() {
        staticShapesCalculated = false;
    }

    public class ValidationResult {

        public List<Rule> notOnAreaErrors = new ArrayList<>();
        public List<Area.AreaValidationResult> areaValidationResults = new ArrayList<>();
        public boolean forceFail = false;

        public boolean failed() {
            if (forceFail) return true;
            if (notOnAreaErrors.size() > 0) return true;
            for (Area.AreaValidationResult result : areaValidationResults) {
                if (!result.eliminated && result.originalErrors.size() > 0) return true;
                if (result.eliminated && result.newErrors.size() > 0) return true;
            }
            return false;
        }

        public boolean hasEliminatedRule() {
            for (Area.AreaValidationResult result : areaValidationResults) {
                if (result.eliminated) return true;
            }
            return false;
        }

        public List<Rule> getEliminatedRules() {
            List<Rule> rules = new ArrayList<>();
            for (Area.AreaValidationResult result : areaValidationResults) {
                for (Rule rule : result.originalErrors) {
                    if (rule.eliminated) {
                        rules.add(rule);
                    }
                }
            }
            return rules;
        }

        public List<Rule> getEliminators() {
            List<Rule> rules = new ArrayList<>();
            for (Area.AreaValidationResult result : areaValidationResults) {
                for (Rule rule : result.area.getAllRules()) {
                    if (rule instanceof EliminationRule) {
                        rules.add(rule);
                    }
                }
            }
            return rules;
        }

        public List<Rule> getOriginalErrors() {
            List<Rule> rules = new ArrayList<>(notOnAreaErrors);
            for (Area.AreaValidationResult result : areaValidationResults) {
                rules.addAll(result.originalErrors);
            }
            return rules;
        }

        public List<Rule> getNewErrors() {
            List<Rule> rules = new ArrayList<>(notOnAreaErrors);
            for (Area.AreaValidationResult result : areaValidationResults) {
                if (result.eliminated) rules.addAll(result.newErrors);
                else rules.addAll(result.originalErrors);
            }
            return rules;
        }

    }

    public void setUntouchable(boolean untouchable) {
        this.untouchable = untouchable;
    }

    public Value<Float> getFadeIntensity(){
        return fadeIntensity;
    }
}
