package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

public class SlidePuzzle extends Puzzle {

    public SlidePuzzle(Game game){
        super(game);

        ColorFactory.setRandomColor(this);

        addVertex(new Vertex(this, 0, 0));
        addVertex(new Vertex(this, 0, 1));
        addEdge(new Edge(vertices.get(0), vertices.get(1)));

        vertices.get(0).setRule(new StartingPointRule());
        vertices.get(1).setRule(new EndingPointRule());

        calcStaticShapes();
    }
}
