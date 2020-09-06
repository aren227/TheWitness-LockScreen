package com.aren.thewitnesspuzzle.graphics;

import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class Program {

    private Shader vertex, fragment;
    private int id;

    private HashMap<String, Integer> handle;

    public Program(Shader vertex, Shader fragment){
        this.vertex = vertex;
        this.fragment = fragment;

        id = GLES20.glCreateProgram();

        GLES20.glAttachShader(id, vertex.getId());
        GLES20.glAttachShader(id, fragment.getId());

        GLES20.glLinkProgram(id);

        handle = new HashMap<>();
    }

    public void use(){
        GLES20.glUseProgram(id);
    }

    public Shader getVertexShader(){
        return vertex;
    }

    public Shader getFragmentShader(){
        return fragment;
    }

    public void setVertexAttrib(String name, int dimension, FloatBuffer buffer){
        if(!handle.containsKey(name)){
            int loc = GLES20.glGetAttribLocation(id, name);
            GLES20.glEnableVertexAttribArray(loc);
            handle.put(name, loc);
        }
        GLES20.glVertexAttribPointer(handle.get(name), dimension, GLES20.GL_FLOAT, false, dimension * 4, buffer);
    }

    private int getUniformLocation(String name){
        if(!handle.containsKey(name)){
            int loc = GLES20.glGetUniformLocation(id, name);
            handle.put(name, loc);
        }
        return handle.get(name);
    }

    public void setUniformMatrix4fv(String name, float[] buffer){
        GLES20.glUniformMatrix4fv(getUniformLocation(name), 1, false, buffer, 0);
    }

    public void setUniform1i(String name, int i){
        GLES20.glUniform1i(getUniformLocation(name), i);
    }

    public void setUniform1f(String name, float f){
        GLES20.glUniform1f(getUniformLocation(name), f);
    }

    public void setUniform2f(String name, float x, float y){
        GLES20.glUniform2f(getUniformLocation(name), x, y);
    }

}
