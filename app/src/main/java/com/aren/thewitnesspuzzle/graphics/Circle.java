package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class Circle implements Shape {

    private static final int TRIANGLES = 32;

    public Vector3 center;
    public float radius;
    public int color;

    public Circle(Vector3 center, float radius, int color){
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        for(int i = 0; i < TRIANGLES; i++){
            float a = 2 * (float)Math.PI * i / TRIANGLES;
            float b = 2 * (float)Math.PI * (i + 1) / TRIANGLES;

            Vector3 pa = center.add(new Vector3((float)Math.cos(a) * radius, (float)Math.sin(a) * radius, 0));
            Vector3 pb = center.add(new Vector3((float)Math.cos(b) * radius, (float)Math.sin(b) * radius, 0));

            buffer.put(pa.x);
            buffer.put(pa.y);
            buffer.put(pa.z);
            buffer.put(center.x);
            buffer.put(center.y);
            buffer.put(center.z);
            buffer.put(pb.x);
            buffer.put(pb.y);
            buffer.put(pb.z);
        }
    }

    public void drawColor(FloatBuffer buffer){
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
        return TRIANGLES * 3;
    }
}
