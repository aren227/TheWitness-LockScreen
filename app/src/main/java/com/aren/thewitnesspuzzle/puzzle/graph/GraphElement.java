package com.aren.thewitnesspuzzle.puzzle.graph;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;

public class GraphElement {

    public Puzzle puzzle;
    public int index;

    private Rule rule;

    public GraphElement(Puzzle puzzle){
        this.puzzle = puzzle;
    }

    public void setRule(Rule rule){
        this.rule = rule;
    }

}
