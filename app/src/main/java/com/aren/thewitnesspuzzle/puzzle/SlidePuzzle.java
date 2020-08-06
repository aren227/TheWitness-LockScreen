package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPoint;

public class SlidePuzzle extends Puzzle {

    public SlidePuzzle(Game game){
        super(game);

        ColorFactory.setRandomColor(this);

        vertices.add(new Vertex(this, 0, 0));
        vertices.add(new Vertex(this, 0, 1));
        edges.add(new Edge(vertices.get(0), vertices.get(1)));

        vertices.get(0).setRule(new StartingPoint());
        vertices.get(1).setRule(new EndingPoint());

        calcStaticShapes();
    }
}
