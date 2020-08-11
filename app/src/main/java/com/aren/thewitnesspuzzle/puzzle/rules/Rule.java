package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
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

    public boolean validateLocally(Cursor cursor){
        return true;
    }

    public boolean canValidateLocally(){
        return true;
    }

    public Puzzle getPuzzle(){
        return graphElement.getPuzzle();
    }

}
