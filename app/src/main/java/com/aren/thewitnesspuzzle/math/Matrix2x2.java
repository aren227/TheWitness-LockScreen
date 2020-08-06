package com.aren.thewitnesspuzzle.math;

public class Matrix2x2 {

    public float a = 1, b = 0, c = 0, d = 1;

    public Vector2 multiply(Vector2 vec){
        return new Vector2(a * vec.x + b * vec.y, c * vec.x + d * vec.y);
    }

    public static Matrix2x2 getRotationMatrix(float angle){
        Matrix2x2 mat = new Matrix2x2();
        mat.a = (float)Math.cos(angle);
        mat.b = (float)Math.sin(angle);
        mat.c = -(float)Math.sin(angle);
        mat.d = (float)Math.cos(angle);
        return mat;
    }

}
