package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector3;

public class CircleShape extends Shape {

    private static final int TRIANGLES = 32;

    public float radius;

    public CircleShape(Vector3 center, float radius, int color) {
        super(center, 1, color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        super.draw();
        for (int i = 0; i < TRIANGLES; i++) {
            float a = 2 * (float) Math.PI * i / TRIANGLES;

            Vector2 pa = new Vector2((float) Math.cos(a) * radius, (float) Math.sin(a) * radius);

            addVertex(pa);
        }
        addVertex(new Vector2(0, 0));

        for(int i = 0; i < TRIANGLES; i++){
            addTriangle(TRIANGLES, i, (i + 1) % TRIANGLES);
        }
    }
}
