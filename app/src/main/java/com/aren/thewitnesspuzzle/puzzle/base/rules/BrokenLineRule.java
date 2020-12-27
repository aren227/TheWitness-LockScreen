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
