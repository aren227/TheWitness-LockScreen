package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.math.Matrix2x2;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

public class EliminatorShape extends Shape {

    public EliminatorShape(Vector3 center, int color) {
        super(center, 1, color);
    }

    @Override
    public void draw() {
        super.draw();
        float width = 0.1f, height = 0.17f;
        float[] angles = new float[]{(float) Math.toRadians(120f), 0, (float) Math.toRadians(-120f)};
        float[] cx = new float[]{-0.43f * height, 0, 0.43f * height};
        float[] cy = new float[]{-0.25f * height, 0.5f * height, -0.25f * height};
        for (int i = 0; i < 3; i++) {
            Vector2 lb = new Vector2(-width / 2, -height / 2);
            Vector2 lt = new Vector2(-width / 2, +height / 2);
            Vector2 rt = new Vector2(+width / 2, +height / 2);
            Vector2 rb = new Vector2(+width / 2, -height / 2);

            Matrix2x2 rot = Matrix2x2.getRotationMatrix(angles[i]);

            lb = rot.multiply(lb);
            lt = rot.multiply(lt);
            rt = rot.multiply(rt);
            rb = rot.multiply(rb);

            Vector2 a = new Vector2(cx[i] + lb.x, cy[i] + lb.y);
            Vector2 b = new Vector2(cx[i] + lt.x, cy[i] + lt.y);
            Vector2 c = new Vector2(cx[i] + rt.x, cy[i] + rt.y);
            Vector2 d = new Vector2(cx[i] + rb.x, cy[i] + rb.y);

            int idx = addVertex(a);
            addVertex(b);
            addVertex(c);
            addVertex(d);

            addTriangle(idx, idx + 1, idx + 2);
            addTriangle(idx, idx + 2, idx + 3);
        }
    }
}
