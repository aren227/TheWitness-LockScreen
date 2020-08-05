package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class Rectangle implements Shape{

    public Vector3 center;
    public float width, height;
    public int color;

    public Rectangle(Vector3 center, float width, float height, int color){
        this.center = center;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        Vector3 a = new Vector3(center.x - width / 2, center.y - height / 2, 0);
        Vector3 b = new Vector3(center.x - width / 2, center.y + height / 2, 0);
        Vector3 c = new Vector3(center.x + width / 2, center.y + height / 2, 0);
        Vector3 d = new Vector3(center.x + width / 2, center.y - height / 2, 0);

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
        return 6;
    }
}
