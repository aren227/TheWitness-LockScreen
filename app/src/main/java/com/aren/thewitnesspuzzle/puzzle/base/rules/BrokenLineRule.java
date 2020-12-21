package com.aren.thewitnesspuzzle.puzzle.base.rules;

import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByRate;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnSelector;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridTreeWalker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BrokenLineRule extends RuleBase {

    public static final String NAME = "brokenline";

    // Manipulate width
    private float overrideCollisionCircleRadius = 0f;

    public BrokenLineRule() {
        super();
    }

    public BrokenLineRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        overrideCollisionCircleRadius = (float) jsonObject.getDouble("collRadius");
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static void generate(Cursor solution, Random random, float blockRate) {
        generate(solution, random, new SpawnByRate(blockRate));
    }

    public static void generate(Cursor solution, Random random, SpawnSelector spawnSelector) {
        PuzzleBase puzzle = solution.getPuzzle();

        ArrayList<Edge> notSolutionEdges = new ArrayList<>();

        for (Edge edge : puzzle.getEdges()) {
            if (edge.getRule() == null && !edge.isEndingEdge() && !solution.containsEdge(edge)) {
                notSolutionEdges.add(edge);
            }
        }

        for (Edge edge : spawnSelector.select(notSolutionEdges, random)) {
            edge.setRule(new BrokenLineRule());
        }
    }

    // Generate more interesting maze
    public static void generate(GridPuzzle puzzle, final RandomGridTreeWalker walker, Random random, float blockRate) {
        ArrayList<Edge> blockEdges = new ArrayList<>();

        for (int i = 0; i <= puzzle.getWidth(); i++) {
            for (int j = 0; j <= puzzle.getHeight(); j++) {
                for (int k = 0; k < 4; k++) {
                    if (((walker.bidirection[i][j] >> k) & 1) == 0) {
                        if (i + walker.delta[k][0] < 0 || i + walker.delta[k][0] > puzzle.getWidth() || j + walker.delta[k][1] < 0 || j + walker.delta[k][1] > puzzle.getHeight())
                            continue;
                        Vertex from = puzzle.getVertexAt(i, j);
                        Vertex to = puzzle.getVertexAt(i + walker.delta[k][0], j + walker.delta[k][1]);
                        blockEdges.add(puzzle.getEdgeByVertex(from, to));
                    }
                }
            }
        }

        int brokenEdges = (int) (blockEdges.size() * blockRate);

        /*for(Edge edge : puzzle.getEdges()){
            if(edge.getRule() == null && !edge.isEndingEdge() && !solution.containsEdge(edge)){
                notSolutionEdges.add(edge);
            }
        }

        int brokenEdges = (int)(notSolutionEdges.size() * blockRate);
        Collections.sort(notSolutionEdges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return -Integer.compare(Math.abs(walker.dist[o1.from.gridPosition.x][o1.from.gridPosition.y] - walker.dist[o1.to.gridPosition.x][o1.to.gridPosition.y]),
                        Math.abs(walker.dist[o2.from.gridPosition.x][o2.from.gridPosition.y] - walker.dist[o2.to.gridPosition.x][o2.to.gridPosition.y]));
            }
        });*/
        Collections.shuffle(blockEdges, random);
        for (int i = 0; i < brokenEdges; i++) {
            blockEdges.get(i).setRule(new BrokenLineRule());
        }
    }

    public float getCollisionCircleRadius() {
        if(overrideCollisionCircleRadius > 0) return overrideCollisionCircleRadius;
        return 0.07f / getGraphElement().getPuzzleBase().getPathWidth() * 0.5f;
    }

    public void setOverrideCollisionCircleRadius(float collisionCircleRadius){
        overrideCollisionCircleRadius = collisionCircleRadius;
    }

    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);

        jsonObject.put("collRadius", overrideCollisionCircleRadius);
    }
}
