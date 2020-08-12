package com.aren.thewitnesspuzzle.graphics.shape;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class Hexagon extends Shape {

    public float radius;

    public Hexagon(Vector3 center, float radius, int color){
        super(center, 1, color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        for(int i = 0; i < 6; i++){
            float a = 2 * (float)Math.PI * i / 6;
            float b = 2 * (float)Math.PI * (i + 1) / 6;

            Vector2 pa = new Vector2((float)Math.cos(a) * radius, (float)Math.sin(a) * radius);
            Vector2 pb = new Vector2((float)Math.cos(b) * radius, (float)Math.sin(b) * radius);

            addVertex(pa);
            addVertex(new Vector2(0, 0));
            addVertex(pb);
        }
    }
}