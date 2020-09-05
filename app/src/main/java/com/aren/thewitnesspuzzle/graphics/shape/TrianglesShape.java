package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

public class TrianglesShape extends Shape {

    public float size; // radius
    public int amount;

    public TrianglesShape(Vector3 center, float size, int amount, int color) {
        super(center, 1, color);
        this.size = size;
        this.amount = amount;
    }

    @Override
    public void draw() {
        super.draw();

        for (int i = 0; i < amount; i++) {
            float x = -size * amount + size * (i * 2 + 1);

            addVertex(new Vector2(x - 0.866f * size, -0.5f * size));
            addVertex(new Vector2(x, size));
            addVertex(new Vector2(x + 0.866f * size, -0.5f * size));
        }
    }
}
