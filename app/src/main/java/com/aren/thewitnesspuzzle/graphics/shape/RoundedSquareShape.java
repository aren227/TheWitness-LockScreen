package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector3;

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

        int[] signX = {1, 1, -1, -1};
        int[] signY = {1, -1, -1, 1};

        // draw 4 rounded corners
        for (int i = 0; i < 4; i++) {
            float cx = radius - radius * CORNER_RATE;
            float cy = radius - radius * CORNER_RATE;
            cx *= signX[i];
            cy *= signY[i];
            Vector2 c = new Vector2(cx, cy);

            for (int j = 0; j <= CORNER_TRIANGLE; j++) {
                float a = 2 * (float) Math.PI * (CORNER_TRIANGLE * i + j) / (CORNER_TRIANGLE * 4);

                Vector2 pa = c.add(new Vector2((float) Math.sin(a) * radius * CORNER_RATE, (float) Math.cos(a) * radius * CORNER_RATE));

                addVertex(pa);
            }
        }

        int idx = addVertex(new Vector2(0, 0));

        for(int i = 0; i < idx; i++){
            addTriangle(idx, i, (i + 1) % idx);
        }
    }
}
