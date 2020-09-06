package com.aren.thewitnesspuzzle.graphics;

import android.opengl.GLES20;
import android.util.Log;

import java.util.HashMap;

public class Shader {

    private String name;
    private int type;
    private String code;
    private int id;

    private boolean error;
    private String errorLog;

    public Shader(String name, int type, String code){
        this.name = name + (type == GLES20.GL_FRAGMENT_SHADER ? "_F" : "_V");
        this.type = type;
        this.code = code;

        id = GLES20.glCreateShader(type);

        GLES20.glShaderSource(id, code);
        GLES20.glCompileShader(id);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(id, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            error = true;
            errorLog = GLES20.glGetShaderInfoLog(id);

            Log.e("GL", "Shader Compile Error!");
            Log.e("GL", name);
            Log.e("GL", errorLog);
        }
    }

    public int getId(){
        return id;
    }
}
