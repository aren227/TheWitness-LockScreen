package com.aren.thewitnesspuzzle.puzzle.base;

import com.aren.thewitnesspuzzle.puzzle.base.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.StartingPointRule;

import org.json.JSONException;
import org.json.JSONObject;

public class JunglePuzzle extends PuzzleBase {

    protected int width;

    public JunglePuzzle(PuzzleColorPalette color, int width) {
        super(color);

        this.width = width;

        Vertex start = new Vertex(this, -0.5f, 0);
        Vertex end = new Vertex(this, width + 0.5f, 0);

        Vertex[] upper = new Vertex[width + 1];
        Vertex[] middle = new Vertex[width + 1];
        Vertex[] lower = new Vertex[width + 1];
        Vertex[] upperBevel = new Vertex[width * 2];
        Vertex[] lowerBevel = new Vertex[width * 2];

        for(int i = 0; i <= width; i++){
            upper[i] = new Vertex(this, i, 1);
            middle[i] = new Vertex(this, i, 0);
            lower[i] = new Vertex(this, i, -1);
        }

        for(int i = 0; i < width; i++){
            upperBevel[i * 2] = new Vertex(this, i + 0.2f, 1.2f);
            upperBevel[i * 2 + 1] = new Vertex(this, i + 0.8f, 1.2f);

            lowerBevel[i * 2] = new Vertex(this, i + 0.2f, -1.2f);
            lowerBevel[i * 2 + 1] = new Vertex(this, i + 0.8f, -1.2f);
        }

        new Edge(this, start, middle[0]);
        new Edge(this, middle[0], upper[0]);
        new Edge(this, middle[0], lower[0]);
        for(int i = 0; i < width; i++){
            new Edge(this, upper[i], upperBevel[i * 2]);
            new Edge(this, upperBevel[i * 2], upperBevel[i * 2 + 1]);
            new Edge(this, upperBevel[i * 2 + 1], upper[i + 1]);
            new Edge(this, upper[i + 1], middle[i + 1]);

            new Edge(this, middle[i], middle[i + 1]);

            new Edge(this, lower[i], lowerBevel[i * 2]);
            new Edge(this, lowerBevel[i * 2], lowerBevel[i * 2 + 1]);
            new Edge(this, lowerBevel[i * 2 + 1], lower[i + 1]);
            new Edge(this, lower[i + 1], middle[i + 1]);
        }
        new Edge(this, middle[width], end);

        start.setRule(new StartingPointRule());
        end.setRule(new EndingPointRule());
    }

    public JunglePuzzle(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        width = jsonObject.getInt("width");
    }

    @Override
    public float getPathWidth() {
        return width * 0.035f + 0.1f;
    }
}
