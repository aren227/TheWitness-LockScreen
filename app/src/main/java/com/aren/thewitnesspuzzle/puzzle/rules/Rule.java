package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Path;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.graph.GraphElement;

public class Rule {

    public GraphElement graphElement;

    public Rule(GraphElement graphElement){
        this.graphElement = graphElement;
    }

    public Shape getShape(){
        return null;
    }

    public boolean validate(Path path){
        return true;
    }

}
