package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.util.ArrayList;

public class RoundedSquareShape extends Shape {

    private static final int CORNER_TRIANGLE = 6;
    private static final float CORNER_RATE = 0.65f;

    public float radius;

    public RoundedSquareShape(Vector3 center, float radius, int color) {
        super(center, 1, color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        super.draw();

        int[] signX = {1, -1, -1, 1};
        int[] signY = {1, 1, -1, -1};

        ArrayList<Vector2> points = new ArrayList<>();

        // draw 4 rounded corners
        for (int i = 0; i < 4; i++) {
            float cx = radius - radius * CORNER_RATE;
            float cy = radius - radius * CORNER_RATE;
            cx *= signX[i];
            cy *= signY[i];
            Vector2 c = new Vector2(cx, cy);

            for (int j = 0; j < CORNER_TRIANGLE; j++) {
                float a = 2 * (float) Math.PI * (CORNER_TRIANGLE * i + j) / (CORNER_TRIANGLE * 4);
                float b = 2 * (float) Math.PI * (CORNER_TRIANGLE * i + j + 1) / (CORNER_TRIANGLE * 4);

                Vector2 pa = c.add(new Vector2((float) Math.cos(a) * radius * CORNER_RATE, (float) Math.sin(a) * radius * CORNER_RATE));
                Vector2 pb = c.add(new Vector2((float) Math.cos(b) * radius * CORNER_RATE, (float) Math.sin(b) * radius * CORNER_RATE));

                addVertex(pa);
                addVertex(new Vector2(0, 0));
                addVertex(pb);

                if (j == 0) points.add(pa);
                if (j == CORNER_TRIANGLE - 1) points.add(pb);
            }
        }

        // draw 4 triangles to fill center
        for (int i = 0; i < 4; i++) {
            Vector2 pa = points.get(i * 2 + 1);
            Vector2 pb = points.get((i * 2 + 2) % 8);

            addVertex(pa);
            addVertex(new Vector2(0, 0));
            addVertex(pb);
        }
    }
}
