package com.aren.thewitnesspuzzle.puzzle.base;

import com.aren.thewitnesspuzzle.puzzle.base.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.base.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.base.rules.StartingPointRule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideoRoomPuzzle extends PuzzleBase {

    public VideoRoomPuzzle(PuzzleColorPalette color) {
        super(color);

        List<Vertex> list = new ArrayList<>();

        float h = (float)(Math.sqrt(3) / 2);
        float el = 0.3f;

        // 0
        list.add(new Vertex(this, -2.5f, -h));
        list.add(new Vertex(this, -2.5f, h));

        // 2
        list.add(new Vertex(this, -2f, -h * 2));
        list.add(new Vertex(this, -2f, 0));
        list.add(new Vertex(this, -2f, h * 2));

        // 5
        list.add(new Vertex(this, -1f, -h * 2));
        list.add(new Vertex(this, -1f, 0));
        list.add(new Vertex(this, -1f, h * 2));

        // 8
        list.add(new Vertex(this, -0.5f, -h * 3));
        list.add(new Vertex(this, -0.5f, -h * 1));
        list.add(new Vertex(this, -0.5f, h * 1));
        list.add(new Vertex(this, -0.5f, h * 3));

        // 12
        list.add(new Vertex(this, 0.5f, -h * 3));
        list.add(new Vertex(this, 0.5f, -h * 1));
        list.add(new Vertex(this, 0.5f, h * 1));
        list.add(new Vertex(this, 0.5f, h * 3));

        // 16
        list.add(new Vertex(this, 1f, -h * 2));
        list.add(new Vertex(this, 1f, 0));
        list.add(new Vertex(this, 1f, h * 2));

        // 19
        list.add(new Vertex(this, 2f, -h * 2));
        list.add(new Vertex(this, 2f, 0));
        list.add(new Vertex(this, 2f, h * 2));

        // 22
        list.add(new Vertex(this, 2.5f, -h));
        list.add(new Vertex(this, 2.5f, h));

        // 24
        list.add(new Vertex(this, -2f - el * 0.5f, -2 * h - el * h));
        list.get(24).setRule(new EndingPointRule());

        // 25
        list.add(new Vertex(this, -1.5f, h * 2));
        list.get(25).setRule(new StartingPointRule());

        // 26
        list.add(new Vertex(this, -0.5f - el * 0.5f, h * 3 + el * h));
        list.get(26).setRule(new EndingPointRule());

        // 27
        list.add(new Vertex(this, 0, -h));
        list.get(27).setRule(new StartingPointRule());

        // 28
        list.add(new Vertex(this, 0.5f - el * 0.5f, h - el * h));
        list.get(28).setRule(new EndingPointRule());

        // 29
        list.add(new Vertex(this, 1.5f, -2 * h));
        list.add(new Vertex(this, 1.5f, -2 * h - el));
        list.get(30).setRule(new EndingPointRule());

        // 31
        list.add(new Vertex(this, 1.5f, 0));
        list.add(new Vertex(this, 1.5f, el));
        list.get(32).setRule(new EndingPointRule());

        // 33
        list.add(new Vertex(this, 2.5f + el, -h));
        list.get(33).setRule(new EndingPointRule());

        addEdge(24, 2);
        addEdge(2, 0);
        addEdge(0, 3);
        addEdge(3, 1);
        addEdge(1, 4);

        addEdge(2, 5);
        addEdge(3, 6);
        addEdge(4, 25);
        addEdge(25, 7);

        addEdge(8, 5);
        addEdge(5, 9);
        addEdge(9, 6);
        addEdge(6, 10);
        addEdge(10, 7);
        addEdge(7, 11);
        addEdge(11, 26);

        addEdge(8, 12);
        addEdge(9, 27);
        addEdge(27, 13);
        addEdge(10, 14);
        addEdge(11, 15);

        addEdge(12, 16);
        addEdge(16, 13);
        addEdge(13, 17);
        addEdge(17, 14);
        addEdge(14, 28);
        addEdge(14, 18);
        addEdge(18, 15);

        addEdge(16, 29);
        addEdge(29, 30);
        addEdge(29, 19);
        addEdge(17, 31);
        addEdge(31, 32);
        addEdge(31, 20);
        addEdge(18, 21);

        addEdge(19, 22);
        addEdge(22, 33);
        addEdge(22, 20);
        addEdge(20, 23);
        addEdge(23, 21);
    }

    public VideoRoomPuzzle(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public float getPathWidth() {
        return 0.26f;
    }
}
