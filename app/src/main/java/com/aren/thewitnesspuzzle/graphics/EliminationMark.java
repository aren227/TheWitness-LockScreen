package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Matrix2x2;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class EliminationMark implements Shape {

    public Vector3 center;
    public int color;

    public EliminationMark(Vector3 center, int color){
        this.center = center;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        float width = 0.1f, height = 0.17f;
        float[] angles = new float[]{(float)Math.toRadians(120f), 0, (float)Math.toRadians(-120f)};
        float[] cx = new float[]{-0.43f * height, 0, 0.43f * height};
        float[] cy = new float[]{-0.25f * height, 0.5f * height, -0.25f * height};
        for(int i = 0; i < 3; i++){
            Vector2 lb = new Vector2(- width / 2, - height / 2);
            Vector2 lt = new Vector2(- width / 2, + height / 2);
            Vector2 rt = new Vector2(+ width / 2, + height / 2);
            Vector2 rb = new Vector2(+ width / 2, - height / 2);

            Matrix2x2 rot = Matrix2x2.getRotationMatrix(angles[i]);

            lb = rot.multiply(lb);
            lt = rot.multiply(lt);
            rt = rot.multiply(rt);
            rb = rot.multiply(rb);

            Vector3 a = new Vector3(center.x + cx[i] + lb.x, center.y + cy[i] + lb.y, 0);
            Vector3 b = new Vector3(center.x + cx[i] + lt.x, center.y + cy[i] + lt.y, 0);
            Vector3 c = new Vector3(center.x + cx[i] + rt.x, center.y + cy[i] + rt.y, 0);
            Vector3 d = new Vector3(center.x + cx[i] + rb.x, center.y + cy[i] + rb.y, 0);

            buffer.put(a.x);
            buffer.put(a.y);
            buffer.put(a.z);
            buffer.put(b.x);
            buffer.put(b.y);
            buffer.put(b.z);
            buffer.put(c.x);
            buffer.put(c.y);
            buffer.put(c.z);

            buffer.put(a.x);
            buffer.put(a.y);
            buffer.put(a.z);
            buffer.put(c.x);
            buffer.put(c.y);
            buffer.put(c.z);
            buffer.put(d.x);
            buffer.put(d.y);
            buffer.put(d.z);
        }
    }

    @Override
    public void drawColor(FloatBuffer buffer) {
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        for(int i = 0; i < getVertexCount(); i++){
            buffer.put(r);
            buffer.put(g);
            buffer.put(b);
        }
    }

    @Override
    public int getVertexCount() {
        return 18;
    }
}
