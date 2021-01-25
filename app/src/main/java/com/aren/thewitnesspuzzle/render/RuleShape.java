package com.aren.thewitnesspuzzle.render;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.GraphElement;
import com.aren.thewitnesspuzzle.core.math.Vector3;
import com.aren.thewitnesspuzzle.core.puzzle.PuzzleBase;
import com.aren.thewitnesspuzzle.core.rules.BlocksRule;
import com.aren.thewitnesspuzzle.core.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.core.rules.EliminationRule;
import com.aren.thewitnesspuzzle.core.rules.HexagonRule;
import com.aren.thewitnesspuzzle.core.rules.RuleBase;
import com.aren.thewitnesspuzzle.core.rules.SquareRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.core.rules.SunRule;
import com.aren.thewitnesspuzzle.core.rules.TrianglesRule;
import com.aren.thewitnesspuzzle.graphics.shape.BlocksShape;
import com.aren.thewitnesspuzzle.graphics.shape.CircleShape;
import com.aren.thewitnesspuzzle.graphics.shape.EliminatorShape;
import com.aren.thewitnesspuzzle.graphics.shape.HexagonShape;
import com.aren.thewitnesspuzzle.graphics.shape.RectangleShape;
import com.aren.thewitnesspuzzle.graphics.shape.RoundedSquareShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.graphics.shape.SunShape;
import com.aren.thewitnesspuzzle.graphics.shape.TrianglesShape;

import java.util.HashMap;
import java.util.Map;

public class RuleShape {

    public static final float Z_INDEX_NORMAL = 0f;
    public static final float Z_INDEX_FLOAT = 0.1f;

    private PuzzleRenderer puzzle;

    private Map<RuleBase, Shape> cache = new HashMap<>();

    public RuleShape(PuzzleRenderer puzzle) {
        this.puzzle = puzzle;
    }

    public Shape get(RuleBase ruleBase) {
        return get(ruleBase, false);
    }

    public Shape get(RuleBase ruleBase, boolean generate) {
        if(!generate && cache.containsKey(ruleBase))
            return cache.get(ruleBase);

        Shape shape = generateShape(ruleBase);
        cache.put(ruleBase, shape);
        return shape;
    }

    private Shape generateShape(RuleBase ruleBase) {
        GraphElement graphElement = ruleBase.getGraphElement();
        PuzzleBase puzzleBase = graphElement.getPuzzleBase();

        if(ruleBase instanceof BlocksRule) {
            BlocksRule blocksRule = (BlocksRule) ruleBase;
            return new BlocksShape(blocksRule.blocks, blocksRule.rotatable, blocksRule.subtractive,
                    graphElement.getPosition().toVector3(), blocksRule.color.getRGB());
        }
        else if(ruleBase instanceof BrokenLineRule) {
            /*BrokenLineRule brokenLineRule = (BrokenLineRule) ruleBase;
            Edge edge = (Edge) graphElement;
            return new RectangleShape(edge.getPosition().toVector3(),
                    brokenLineRule.getCollisionCircleRadius() * 2f / edge.getLength(),
                    puzzleBase.getPathWidth(),
                    edge.getAngle(),
                    puzzleBase.getColorPalette().getBackgroundColor());*/
        }
        else if(ruleBase instanceof EliminationRule) {
            EliminationRule eliminationRule = (EliminationRule) ruleBase;
            return new EliminatorShape(graphElement.getPosition().toVector3(),
                    eliminationRule.color.getRGB());
        }
        else if(ruleBase instanceof HexagonRule) {
            HexagonRule hexagonRule = (HexagonRule) ruleBase;
            if(hexagonRule.getOverrideColor() != 0)
                return new HexagonShape(new Vector3(graphElement.x, graphElement.y,
                        puzzle.getGame().isEditorMode() ? Z_INDEX_FLOAT : Z_INDEX_NORMAL),
                        puzzleBase.getPathWidth() * 0.5f,
                        hexagonRule.getOverrideColor());
            if (hexagonRule.hasSymmetricColor())
                return new HexagonShape(new Vector3(graphElement.x, graphElement.y,
                        puzzle.getGame().isEditorMode() ? Z_INDEX_FLOAT : Z_INDEX_NORMAL),
                        puzzleBase.getPathWidth() * 0.5f,
                        hexagonRule.getSymmetricColor().getRGB());
            return new HexagonShape(new Vector3(graphElement.x, graphElement.y,
                    puzzle.getGame().isEditorMode() ? Z_INDEX_FLOAT : Z_INDEX_NORMAL),
                    puzzleBase.getPathWidth() * 0.5f, Color.BLACK);
        }
        else if(ruleBase instanceof SquareRule) {
            SquareRule squareRule = (SquareRule) ruleBase;
            return new RoundedSquareShape(graphElement.getPosition().toVector3(), 0.18f,
                    squareRule.color.getRGB());
        }
        else if(ruleBase instanceof StartingPointRule) {
            StartingPointRule startingPointRule = (StartingPointRule) ruleBase;
            return new CircleShape(graphElement.getPosition().toVector3(),
                    startingPointRule.getRadius(), puzzleBase.getColorPalette().getPathColor());
        }
        else if(ruleBase instanceof SunRule) {
            SunRule sunRule = (SunRule) ruleBase;
            return new SunShape(graphElement.getPosition().toVector3(), 0.2f,
                    sunRule.color.getRGB());
        }
        else if(ruleBase instanceof TrianglesRule) {
            TrianglesRule trianglesRule = (TrianglesRule) ruleBase;
            return new TrianglesShape(graphElement.getPosition().toVector3(), 0.1f,
                    trianglesRule.count, Color.parseColor("#ffaa00"));
        }

        return null;
    }

}
