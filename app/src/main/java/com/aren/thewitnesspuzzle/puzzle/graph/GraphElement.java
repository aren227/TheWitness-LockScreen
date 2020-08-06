package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class GraphElement {

    public Puzzle puzzle;
    public int index;
    protected float x, y;

    private Rule rule;

    public GraphElement(Puzzle puzzle){
        this.puzzle = puzzle;
    }

    public void setRule(Rule rule){
        this.rule = rule;
        rule.setGraphElement(this);
        puzzle.getAppliedRules().add(rule.getClass());
    }

    public Rule getRule(){
        return rule;
    }

    public Vector2 getPosition(){
        return new Vector2(x, y);
    }

    public Puzzle getPuzzle(){
        return puzzle;
    }

}
