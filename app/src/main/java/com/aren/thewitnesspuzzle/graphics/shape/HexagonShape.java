package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

public class HexagonShape extends Shape {

    public float radius;

    public HexagonShape(Vector3 center, float radius, int color) {
        super(center, 1, color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        super.draw();
        for (int i = 0; i < 6; i++) {
            float a = 2 * (float) Math.PI * i / 6;

            Vector2 pa = new Vector2((float) Math.cos(a) * radius, (float) Math.sin(a) * radius);

            addVertex(pa);
        }
        addVertex(new Vector2(0, 0));

        for(int i = 0; i < 6; i++){
            addTriangle(6, i, (i + 1) % 6);
        }
    }
}