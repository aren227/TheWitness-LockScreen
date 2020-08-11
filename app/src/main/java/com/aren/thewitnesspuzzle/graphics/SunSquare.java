package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class SunSquare implements Shape {

    public Vector3 center;
    public float radius;
    public int color;

    public SunSquare(Vector3 center, float radius, int color){
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        Vector3 a = new Vector3(center.x, center.y + radius, 0);
        Vector3 b = new Vector3(center.x + radius, center.y, 0);
        Vector3 c = new Vector3(center.x, center.y - radius, 0);
        Vector3 d = new Vector3(center.x - radius, center.y, 0);

        Vector3 e = new Vector3(center.x + radius * 0.7f, center.y + radius * 0.7f, 0);
        Vector3 f = new Vector3(center.x + radius * 0.7f, center.y - radius * 0.7f, 0);
        Vector3 g = new Vector3(center.x - radius * 0.7f, center.y - radius * 0.7f, 0);
        Vector3 h = new Vector3(center.x - radius * 0.7f, center.y + radius * 0.7f, 0);

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

        buffer.put(e.x);
        buffer.put(e.y);
        buffer.put(e.z);
        buffer.put(f.x);
        buffer.put(f.y);
        buffer.put(f.z);
        buffer.put(g.x);
        buffer.put(g.y);
        buffer.put(g.z);

        buffer.put(e.x);
        buffer.put(e.y);
        buffer.put(e.z);
        buffer.put(g.x);
        buffer.put(g.y);
        buffer.put(g.z);
        buffer.put(h.x);
        buffer.put(h.y);
        buffer.put(h.z);
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
        return 12;
    }


}
