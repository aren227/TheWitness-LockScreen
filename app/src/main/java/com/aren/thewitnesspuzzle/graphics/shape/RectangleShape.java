package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.core.math.Matrix2x2;
import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector3;

public class RectangleShape extends Shape {

    public float width, height;
    public float angle; // Radian

    public RectangleShape(Vector3 center, float width, float height, float angle, int color) {
        super(center, 1, color);
        this.width = width;
        this.height = height;
        this.angle = angle;
    }

    @Override
    public void draw() {
        super.draw();
        Vector2 lb = new Vector2(-width / 2, -height / 2);
        Vector2 lt = new Vector2(-width / 2, +height / 2);
        Vector2 rt = new Vector2(+width / 2, +height / 2);
        Vector2 rb = new Vector2(+width / 2, -height / 2);

        Matrix2x2 rot = Matrix2x2.getRotationMatrix(angle);

        lb = rot.multiply(lb);
        lt = rot.multiply(lt);
        rt = rot.multiply(rt);
        rb = rot.multiply(rb);

        Vector2 a = new Vector2(lb.x, lb.y);
        Vector2 b = new Vector2(lt.x, lt.y);
        Vector2 c = new Vector2(rt.x, rt.y);
        Vector2 d = new Vector2(rb.x, rb.y);

        int idx = addVertex(a);
        addVertex(b);
        addVertex(c);
        addVertex(d);

        addTriangle(idx, idx + 1, idx + 2);
        addTriangle(idx, idx + 2, idx + 3);
    }
}
