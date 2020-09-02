package com.aren.thewitnesspuzzle.graphics.shape;

import android.graphics.Color;
import android.util.Log;

import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class Shape {

    public Vector3 center;
    public Value<Float> scale;
    public Value<Float> zIndex;
    public Value<Integer> color;

    private List<Vector3> vertices;

    public Shape(Vector3 center, float scale, int color){
        this.center = center;
        this.scale = new Value<>(scale);
        this.zIndex = new Value<>(0f);
        this.color = new Value<>(color);
        vertices = new ArrayList<>();
    }

    public void draw(){
        vertices.clear();
    }

    public void addVertex(Vector3 vector3){
        vertices.add(vector3);
    }

    public void addVertex(Vector2 vector2){
        vertices.add(new Vector3(vector2.x, vector2.y, 0));
    }

    public void fillVertexBuffer(FloatBuffer buffer){
        for(Vector3 vector3 : vertices){
            buffer.put(vector3.x * scale.get() + center.x);
            buffer.put(vector3.y * scale.get() + center.y);
            buffer.put(vector3.z * scale.get() + center.z + zIndex.get());
        }
    }

    public void fillColorBuffer(FloatBuffer buffer){
        float r = Color.red(color.get()) / 255f;
        float g = Color.green(color.get()) / 255f;
        float b = Color.blue(color.get()) / 255f;
        for(int i = 0; i < getVertexCount(); i++){
            buffer.put(r);
            buffer.put(g);
            buffer.put(b);
        }
    }

    public int getVertexCount(){
        return vertices.size();
    }

}
