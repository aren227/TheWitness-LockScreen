package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

public class JunglePuzzle extends Puzzle{

    protected int width;

    public JunglePuzzle(Game game, PuzzleColorPalette color, int width) {
        super(game, color);

        this.width = width;
        pathWidth = width * 0.035f + 0.1f;

        Vertex start = addVertex(new Vertex(this, -0.5f, 0));
        Vertex end = addVertex(new Vertex(this, width + 0.5f, 0));

        Vertex[] upper = new Vertex[width + 1];
        Vertex[] middle = new Vertex[width + 1];
        Vertex[] lower = new Vertex[width + 1];
        Vertex[] upperBevel = new Vertex[width * 2];
        Vertex[] lowerBevel = new Vertex[width * 2];

        for(int i = 0; i <= width; i++){
            upper[i] = addVertex(new Vertex(this, i, 1));
            middle[i] = addVertex(new Vertex(this, i, 0));
            lower[i] = addVertex(new Vertex(this, i, -1));
        }

        for(int i = 0; i < width; i++){
            upperBevel[i * 2] = addVertex(new Vertex(this, i + 0.2f, 1.2f));
            upperBevel[i * 2 + 1] = addVertex(new Vertex(this, i + 0.8f, 1.2f));

            lowerBevel[i * 2] = addVertex(new Vertex(this, i + 0.2f, -1.2f));
            lowerBevel[i * 2 + 1] = addVertex(new Vertex(this, i + 0.8f, -1.2f));
        }

        addEdge(new Edge(start, middle[0]));
        addEdge(new Edge(middle[0], upper[0]));
        addEdge(new Edge(middle[0], lower[0]));
        for(int i = 0; i < width; i++){
            addEdge(new Edge(upper[i], upperBevel[i * 2]));
            addEdge(new Edge(upperBevel[i * 2], upperBevel[i * 2 + 1]));
            addEdge(new Edge(upperBevel[i * 2 + 1], upper[i + 1]));
            addEdge(new Edge(upper[i + 1], middle[i + 1]));

            addEdge(new Edge(middle[i], middle[i + 1]));

            addEdge(new Edge(lower[i], lowerBevel[i * 2]));
            addEdge(new Edge(lowerBevel[i * 2], lowerBevel[i * 2 + 1]));
            addEdge(new Edge(lowerBevel[i * 2 + 1], lower[i + 1]));
            addEdge(new Edge(lower[i + 1], middle[i + 1]));
        }
        addEdge(new Edge(middle[width], end));

        start.setRule(new StartingPointRule());
        end.setRule(new EndingPointRule());
    }
}
