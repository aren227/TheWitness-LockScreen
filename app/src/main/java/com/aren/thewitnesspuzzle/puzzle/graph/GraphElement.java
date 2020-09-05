package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class GraphElement {

    public Puzzle puzzle;
    public int index;
    public float x, y;

    private Rule rule;

    public Vector2Int gridPosition; // Only for grid puzzle

    public GraphElement(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void setRule(Rule rule) {
        if (rule == null) return;
        this.rule = rule;
        rule.setGraphElement(this);
    }

    public Rule getRule() {
        return rule;
    }

    public void removeRule() {
        rule = null;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

}
