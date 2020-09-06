package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

public class SunShape extends Shape {

    public float radius;

    public SunShape(Vector3 center, float radius, int color) {
        super(center, 1, color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        super.draw();

        Vector2 a = new Vector2(0, radius);
        Vector2 b = new Vector2(radius, 0);
        Vector2 c = new Vector2(0, -radius);
        Vector2 d = new Vector2(-radius, 0);

        Vector2 e = new Vector2(radius * 0.7f, radius * 0.7f);
        Vector2 f = new Vector2(radius * 0.7f, -radius * 0.7f);
        Vector2 g = new Vector2(-radius * 0.7f, -radius * 0.7f);
        Vector2 h = new Vector2(-radius * 0.7f, radius * 0.7f);

        int idx = addVertex(a);
        addVertex(b);
        addVertex(c);
        addVertex(d);

        addTriangle(idx, idx + 1, idx + 2);
        addTriangle(idx, idx + 2, idx + 3);

        idx = addVertex(e);
        addVertex(f);
        addVertex(g);
        addVertex(h);

        addTriangle(idx, idx + 1, idx + 2);
        addTriangle(idx, idx + 2, idx + 3);
    }
}
