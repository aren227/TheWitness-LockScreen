package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Matrix2x2;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class Rectangle implements Shape{

    public Vector3 center;
    public float width, height;
    public float angle; // Radian
    public int color;

    public Rectangle(Vector3 center, float width, float height, float angle, int color){
        this.center = center;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        Vector2 lb = new Vector2(- width / 2, - height / 2);
        Vector2 lt = new Vector2(- width / 2, + height / 2);
        Vector2 rt = new Vector2(+ width / 2, + height / 2);
        Vector2 rb = new Vector2(+ width / 2, - height / 2);

        Matrix2x2 rot = Matrix2x2.getRotationMatrix(angle);

        lb = rot.multiply(lb);
        lt = rot.multiply(lt);
        rt = rot.multiply(rt);
        rb = rot.multiply(rb);

        Vector3 a = new Vector3(center.x + lb.x, center.y + lb.y, 0);
        Vector3 b = new Vector3(center.x + lt.x, center.y + lt.y, 0);
        Vector3 c = new Vector3(center.x + rt.x, center.y + rt.y, 0);
        Vector3 d = new Vector3(center.x + rb.x, center.y + rb.y, 0);

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
