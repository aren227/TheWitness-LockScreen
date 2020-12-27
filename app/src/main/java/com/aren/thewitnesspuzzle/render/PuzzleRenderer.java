package com.aren.thewitnesspuzzle.render;

import android.util.Log;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.core.color.ColorUtils;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.BoundingBox;
import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector3;
import com.aren.thewitnesspuzzle.core.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.core.puzzle.PuzzleBase;
import com.aren.thewitnesspuzzle.core.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.RuleBase;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.core.rules.Symmetry;
import com.aren.thewitnesspuzzle.core.validation.PuzzleValidator;
import com.aren.thewitnesspuzzle.core.validation.ValidationResult;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.graphics.shape.CircleShape;
import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.puzzle.animation.Animation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorEndingPointReachedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorFailedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.CursorSucceededAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.EliminatedAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.ErrorAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.PuzzleAnimationManager;
import com.aren.thewitnesspuzzle.puzzle.animation.WaitForEliminationAnimation;
import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;
import com.aren.thewitnesspuzzle.puzzle.sound.Sounds;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PuzzleRenderer {

    protected Game game;

    protected PuzzleBase puzzleBase;

    protected RuleShape ruleShape;

    protected boolean staticShapesCalculated = false;
    protected ArrayList<Shape> staticShapes = new ArrayList<>();
    protected ArrayList<Shape> dynamicShapes = new ArrayList<>();

    protected Cursor cursor;

    protected PuzzleAnimationManager animation;

    protected boolean shadowPanel;

    protected List<Integer> customPattern;

    protected boolean untouchable = false;

    protected Value<Integer> actualCursorColor;

    protected Value<Float> fadeIntensity = new Value<>(1f);

    protected UUID uuid;

    protected boolean isFavorite = false;

    public PuzzleRenderer(Game game, PuzzleBase puzzleBase) {
        this(game, puzzleBase, game.isPlayMode() && game.getSettings().getShadowPanelEnabled());
    }

    public PuzzleRenderer(Game game, PuzzleBase puzzleBase, boolean shadowPanel) {
        this.game = game;
        this.puzzleBase = puzzleBase;
        this.shadowPanel = shadowPanel;

        ruleShape = new RuleShape(this);

        animation = new PuzzleAnimationManager(this);

        actualCursorColor = new Value<>(puzzleBase.getColorPalette().getCursorColor());

        uuid = UUID.randomUUID();
    }

    public Game getGame() {
        return game;
    }

    public PuzzleBase getPuzzleBase() {
        return puzzleBase;
    }

    public RuleShape getRuleShape() {
        return ruleShape;
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

    public void calcStaticShapes() {
        staticShapes.clear();

        ArrayList<Shape> shadow = new ArrayList<>();
        int shadowPathColor = ColorUtils.lerp(puzzleBase.getColorPalette().getBackgroundColor(),
                puzzleBase.getColorPalette().getPathColor(), 0.4f);

        for (Vertex vertex : puzzleBase.getVertices()) {
            staticShapes.add(new CircleShape(new Vector3(vertex.x, vertex.y, 0), puzzleBase.getPathWidth() * 0.5f,
                    puzzleBase.getColorPalette().getPathColor()));
            if (shadowPanel) {
                shadow.add(new CircleShape(new Vector3(vertex.x, vertex.y - puzzleBase.getBoundingBox().getHeight(), 0), puzzleBase.getPathWidth() * 0.5f, shadowPathColor));
            }
        }

        for (Edge edge : puzzleBase.getEdges()) {
            staticShapes.add(new RectangleShape(edge.getPosition().toVector3(), edge.getLength(),
                    puzzleBase.getPathWidth(), edge.getAngle(), puzzleBase.getColorPalette().getPathColor()));
            if (shadowPanel) {
                shadow.add(new RectangleShape(edge.getPosition().toVector3().add(new Vector3(0, -puzzleBase.getBoundingBox().getHeight(), 0)), edge.getLength(), puzzleBase.getPathWidth(), edge.getAngle(), shadowPathColor));
            }
        }

        for (Vertex vertex : puzzleBase.getVertices()) {
            if (vertex.getRule() != null) {
                // getShape() reads from cache, but some rules need to be updated when changing colors. (ex, StartingPoint, BrokenLine)
                if (vertex.getRule() instanceof StartingPointRule || vertex.getRule() instanceof BrokenLineRule) {
                    Shape shape = ruleShape.get(vertex.getRule(), true);
                    if(shape != null) staticShapes.add(shape);
                } else {
                    Shape shape = ruleShape.get(vertex.getRule());
                    if(shape != null) staticShapes.add(shape);
                }

                if (shadowPanel && vertex.getRule() instanceof StartingPointRule) {
                    Shape shape = ruleShape.get(vertex.getRule());
                    shape.center = shape.center.add(new Vector3(0, -puzzleBase.getBoundingBox().getHeight(), 0));
                    shape.color.set(shadowPathColor);
                    shadow.add(shape);
                }
            }
        }

        for (Edge edge : puzzleBase.getEdges()) {
            // BrokenLine
            if (edge.getRule() != null) {
                Shape shape = ruleShape.get(edge.getRule());
                if(shape != null) staticShapes.add(shape);
            }
        }

        for (Tile tile : puzzleBase.getTiles()) {
            if (tile.getRule() != null) {
                Shape shape = ruleShape.get(tile.getRule());
                if(shape != null) staticShapes.add(shape);
            }
        }

        if (shadowPanel) {
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

        int shadowCursorColor = ColorUtils.lerp(ColorUtils.lerp(puzzleBase.getColorPalette().getBackgroundColor(),
                puzzleBase.getColorPalette().getPathColor(), 0.4f),
                puzzleBase.getColorPalette().getCursorColor(), 0.4f);

        if (cursor != null) {
            int cursorColor = getCursorColor().get();
            int secondaryCursorColor = cursorColor;

            if (puzzleBase instanceof GridSymmetryPuzzle) {
                Symmetry symmetry = ((GridSymmetryPuzzle) puzzleBase).getSymmetry();

                if (symmetry.hasColor()) {
                    cursorColor = symmetry.getPrimaryColor().getRGB();
                    secondaryCursorColor = symmetry.getSecondaryColor().getRGB();
                }
            }

            dynamicShapes.add(new CircleShape(cursor.getFirstVisitedVertex().getPosition().toVector3(), ((StartingPointRule) cursor.getFirstVisitedVertex().getRule()).getRadius(), cursorColor));
            if (shadowPanel) {
                dynamicShapes.add(new CircleShape(cursor.getFirstVisitedVertex().getPosition().toVector3().add(new Vector3(0, -puzzleBase.getBoundingBox().getHeight(), 0)), ((StartingPointRule) cursor.getFirstVisitedVertex().getRule()).getRadius(), shadowCursorColor));
            }

            if(puzzleBase instanceof GridSymmetryPuzzle) {
                Vertex opVertex = ((GridSymmetryPuzzle) puzzleBase).getOppositeVertex(cursor.getFirstVisitedVertex());
                dynamicShapes.add(new CircleShape(opVertex.getPosition().toVector3(),
                        ((StartingPointRule) opVertex.getRule()).getRadius(), secondaryCursorColor));
            }

            ArrayList<EdgeProportion> visitedEdges = cursor.getVisitedEdgesWithProportion(true);
            if (visitedEdges.size() == 0) return;
            for (EdgeProportion edgeProportion : visitedEdges) {
                dynamicShapes.add(new CircleShape(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y, 0), puzzleBase.getPathWidth() * 0.5f, cursorColor));
                dynamicShapes.add(new RectangleShape(edgeProportion.getProportionMiddlePoint().toVector3(), edgeProportion.getProportionLength(), puzzleBase.getPathWidth(), edgeProportion.edge.getAngle(), cursorColor));

                if(puzzleBase instanceof GridSymmetryPuzzle) {
                    EdgeProportion opEdgeProportion = ((GridSymmetryPuzzle) puzzleBase).getOppositeEdgeProportion(edgeProportion);
                    dynamicShapes.add(new CircleShape(new Vector3(opEdgeProportion.getProportionPoint().x, opEdgeProportion.getProportionPoint().y, 0), puzzleBase.getPathWidth() * 0.5f, secondaryCursorColor));
                    dynamicShapes.add(new RectangleShape(opEdgeProportion.getProportionMiddlePoint().toVector3(), opEdgeProportion.getProportionLength(), puzzleBase.getPathWidth(), opEdgeProportion.edge.getAngle(), secondaryCursorColor));
                }

                if (shadowPanel) {
                    dynamicShapes.add(new CircleShape(new Vector3(edgeProportion.getProportionPoint().x, edgeProportion.getProportionPoint().y - puzzleBase.getBoundingBox().getHeight(), 0), puzzleBase.getPathWidth() * 0.5f, shadowCursorColor));
                    dynamicShapes.add(new RectangleShape(edgeProportion.getProportionMiddlePoint().toVector3().add(new Vector3(0, -puzzleBase.getBoundingBox().getHeight(), 0)), edgeProportion.getProportionLength(), puzzleBase.getPathWidth(), edgeProportion.edge.getAngle(), shadowCursorColor));
                }
            }
        }
    }

    public BoundingBox getBoundingBox() {
        BoundingBox boundingBox = puzzleBase.getBoundingBox().clone();
        if(shadowPanel) boundingBox.min.y -= boundingBox.getHeight();
        return boundingBox;
    }

    public float getPadding() {
        return Math.min(getBoundingBox().getWidth(), getBoundingBox().getHeight()) * 0.1f;
    }

    protected void startTracing(Vertex start) {
        resetAnimation();
        cursor = puzzleBase.createCursor(start);
        game.playSound(Sounds.START_TRACING);
    }

    protected void endTracing() {
        EdgeProportion currentCursorEdge = cursor.getCurrentCursorEdge();
        if (currentCursorEdge == null) return;
        Edge edge = currentCursorEdge.edge;
        if (currentCursorEdge.to().getRule() instanceof EndingPointRule && currentCursorEdge.proportion > 1 - puzzleBase.getPathWidth() * 0.5f / edge.getLength()) {
            resetAnimation();

            // Validation with custom pattern
            if(customPattern != null && customPattern.size() > 0){
                if(PuzzleValidator.validate(cursor, customPattern)) {
                    addAnimation(new CursorSucceededAnimation(this));
                    game.playSound(Sounds.SUCCESS);
                    game.solved();
                } else {
                    addAnimation(new CursorFailedAnimation(this));
                    game.playSound(Sounds.FAILURE);
                }
                return;
            }

            final ValidationResult result = PuzzleValidator.validate(cursor, puzzleBase);

            if (result.hasEliminatedRule()) {
                game.playSound(Sounds.POTENTIAL_FAILURE);
                for (RuleBase rule : result.getOriginalErrors()) {
                    addAnimation(new ErrorAnimation(ruleShape.get(rule), 2));
                }
                addAnimation(new WaitForEliminationAnimation(this, new Runnable() {
                    @Override
                    public void run() {
                        game.playSound(Sounds.ERASER_APPLY);
                        if (result.failed()) {
                            for (RuleBase rule : result.getNewErrors()) {
                                if (rule.eliminated) continue;
                                addAnimation(new ErrorAnimation(ruleShape.get(rule)));
                            }
                            for (RuleBase rule : result.getEliminatedRules()) {
                                addAnimation(new EliminatedAnimation(rule, PuzzleRenderer.this));
                            }
                            /*for (Rule rule : result.getEliminators()) {
                                addAnimation(new EliminatorActivatedAnimation(rule));
                            }*/
                            addAnimation(new CursorFailedAnimation(PuzzleRenderer.this));
                            game.playSound(Sounds.FAILURE);
                        } else {
                            for (RuleBase rule : result.getEliminatedRules()) {
                                addAnimation(new EliminatedAnimation(rule, PuzzleRenderer.this));
                            }
                            /*for (Rule rule : result.getEliminators()) {
                                addAnimation(new EliminatorActivatedAnimation(rule));
                            }*/
                            addAnimation(new CursorSucceededAnimation(PuzzleRenderer.this));
                            game.playSound(Sounds.SUCCESS);
                            game.solved();
                        }
                    }
                }));
            } else {
                if (result.failed()) {
                    for (RuleBase rule : result.getOriginalErrors()) {
                        addAnimation(new ErrorAnimation(ruleShape.get(rule)));
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
            pos.y += puzzleBase.getBoundingBox().getHeight();
        }

        if (action == MotionEvent.ACTION_DOWN) {
            Vertex start = null;
            for (Vertex vertex : puzzleBase.getVertices()) {
                if (vertex.getRule() instanceof StartingPointRule && pos.distance(vertex.getPosition()) <= ((StartingPointRule) vertex.getRule()).getRadius() * 1.3f) {
                    start = vertex;
                    break;
                }
            }
            if (start != null) {
                startTracing(start);
            } else if (cursor != null) {
                float padding = game.getDPScale(48);
                if (shadowPanel && !puzzleBase.getBoundingBox().test(pos) && (cursor.getCurrentCursorEdge() == null || cursor.getCurrentCursorEdge().getProportionPoint().distance(pos) > padding * 2)) {
                    cursor = null;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (cursor == null) return;

            Edge edge = puzzleBase.getNearestEdge(pos);
            EdgeProportion edgeProportion = new EdgeProportion(edge);
            edgeProportion.proportion = edgeProportion.getProportionFromPointOutside(pos);
            cursor.connectTo(edgeProportion);

            EdgeProportion cursorEdge = cursor.getCurrentCursorEdge();
            if (cursorEdge == null) return;
            if (cursorEdge.to().getRule() instanceof EndingPointRule && cursorEdge.proportion > 1 - puzzleBase.getPathWidth() * 0.5f / cursorEdge.edge.getLength()) {
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

            if (cursorEdge.to().getRule() instanceof EndingPointRule && cursorEdge.proportion > 1 - puzzleBase.getPathWidth() * 0.5f / cursorEdge.edge.getLength()) {
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

    // For debugging
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public Value<Integer> getCursorColor() {
        return actualCursorColor;
    }

    public boolean hasShadowPanel() {
        return shadowPanel;
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

    public void setUntouchable(boolean untouchable) {
        this.untouchable = untouchable;
    }

    public Value<Float> getFadeIntensity(){
        return fadeIntensity;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public UUID getUuid() {
        return uuid;
    }

}
