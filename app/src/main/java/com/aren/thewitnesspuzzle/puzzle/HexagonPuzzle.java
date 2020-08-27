package com.aren.thewitnesspuzzle.puzzle;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

public class HexagonPuzzle extends Puzzle {

    public HexagonPuzzle(Game game, PuzzleColorPalette color){
        this(game, color, game.isPlayMode() && game.getSettings().getShadowPanelEnabled());
    }

    public HexagonPuzzle(Game game, PuzzleColorPalette color, boolean shadowPanel) {
        super(game, color, shadowPanel);

        Vertex center = addVertex(new Vertex(this, 0, 0));
        Vertex[] innerVertices = new Vertex[6];
        Vertex[] outerVertices = new Vertex[6];
        for(int i = 0; i < 6; i++){
            innerVertices[i] = addVertex(new Vertex(this, -(float)Math.sin(i / 6f * Math.PI * 2) * 6f, (float)Math.cos(i / 6f * Math.PI * 2) * 6f));
            outerVertices[i] = addVertex(new Vertex(this, -(float)Math.sin(i / 6f * Math.PI * 2) * 7.5f, (float)Math.cos(i / 6f * Math.PI * 2) * 7.5f));
        }

        for(int i = 0; i < 6; i++){
            addEdge(new Edge(center, innerVertices[i]));
        }

        for(int i = 0; i < 6; i++){
            addEdge(new Edge(innerVertices[i], innerVertices[(i + 1) % 6]));
        }

        for(int i = 0; i < 6; i++){
            addEdge(new Edge(innerVertices[i], outerVertices[i]));
        }

        center.setRule(new StartingPointRule());
        for(int i = 0; i < 6; i++){
            innerVertices[i].setRule(new StartingPointRule());
        }
        for(int i = 0; i < 6; i++){
            outerVertices[i].setRule(new EndingPointRule());
        }
    }
}
