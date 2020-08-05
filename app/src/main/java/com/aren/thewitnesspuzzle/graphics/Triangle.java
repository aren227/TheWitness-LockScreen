package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class Triangle implements Shape{

    public Vector3 a, b, c;
    public int color;

    public Triangle(Vector3 a, Vector3 b, Vector3 c, int color){
        this.a = a;
        this.b = b;
        this.c = c;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        buffer.put(a.x);
        buffer.put(a.y);
        buffer.put(a.z);
        buffer.put(b.x);
        buffer.put(b.y);
        buffer.put(b.z);
        buffer.put(c.x);
        buffer.put(c.y);
        buffer.put(c.z);
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

    public int getVertexCount(){
        return 3;
    }

}
