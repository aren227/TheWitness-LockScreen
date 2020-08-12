package com.aren.thewitnesspuzzle.graphics.shape;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class Shape {

    public Vector3 center;
    public float scale;
    public int color;

    private List<Vector3> verticies;

    public Shape(Vector3 center, float scale, int color){
        this.center = center;
        this.scale = scale;
        this.color = color;
        verticies = new ArrayList<>();
    }

    public abstract void draw();

    public void addVertex(Vector3 vector3){
        verticies.add(vector3);
    }

    public void addVertex(Vector2 vector2){
        verticies.add(new Vector3(vector2.x, vector2.y, 0));
    }

    public void fillVertexBuffer(FloatBuffer buffer){
        for(Vector3 vector3 : verticies){
            buffer.put(vector3.x * scale + center.x);
            buffer.put(vector3.y * scale + center.y);
            buffer.put(vector3.z * scale + center.z);
        }
    }

    public void fillColorBuffer(FloatBuffer buffer){
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
        return verticies.size();
    }

}
