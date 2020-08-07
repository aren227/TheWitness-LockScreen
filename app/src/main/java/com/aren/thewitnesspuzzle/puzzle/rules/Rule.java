package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Cursor;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;

public class Rule {

    private GraphElement graphElement;

    public Rule(){

    }

    public GraphElement getGraphElement(){
        return graphElement;
    }

    public void setGraphElement(GraphElement graphElement){
        this.graphElement = graphElement;
    }

    public Shape getShape(){
        return null;
    }

    public boolean validate(Cursor cursor){
        return true;
    }

    public Puzzle getPuzzle(){
        return graphElement.getPuzzle();
    }

}
