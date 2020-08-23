package com.aren.thewitnesspuzzle.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer3 implements GLSurfaceView.Renderer {

    private Game game;
    private Context context;

    private int vertexShader, fragmentShader, fragmentShaderFrameBuffer;
    private int vertexShaderFrameBuffer, fragmentShaderFrameBuffer_boxblur_down, fragmentShaderFrameBuffer_boxblur_down_prelift, fragmentShaderFrameBuffer_boxblur_up, fragmentShaderFrameBuffer_boxblur_up_final, fragmentShaderFrameBuffer_additive, fragmentShaderFrameBuffer_tonemapping;
    private int vertexShader_gaussian_v, vertexShader_gaussian_h, fragmentShader_gaussian;
    private int glProgram;
    private int glProgramFrameBuffer_boxblur_down;
    private int glProgramFrameBuffer_boxblur_down_prelift;
    private int glProgramFrameBuffer_boxblur_up;
    private int glProgramFrameBuffer_boxblur_up_final;
    private int glProgramFrameBuffer_additive;
    private int glProgram_gaussian_v;
    private int glProgram_gaussian_h;
    private int glProgram_draw;
    private int glProgram_tonemapping;

    private int width, height;
    private int[] texWidth = new int[13], texHeight = new int[13];

    private int textureIds[] = new int[14];

    private IntBuffer frameBuffer = IntBuffer.allocate(14);

    private float ratio = 1;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private float quadPos[] = {
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };
    private FloatBuffer quadPosBuffer;

    private float quadUV[] = {
            0, 1,
            0, 0,
            1, 0,
            1, 1
    };
    private FloatBuffer quadUVBuffer;

    private short quadIndex[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private ShortBuffer quadIndexBuffer;

    private long lastUpdated;

    public GLRenderer3(Game game, Context context){
        this.game = game;
        this.context = context;

        ByteBuffer bb1 = ByteBuffer.allocateDirect(quadPos.length * 4);
        bb1.order(ByteOrder.nativeOrder());
        quadPosBuffer = bb1.asFloatBuffer();
        quadPosBuffer.put(quadPos);
        quadPosBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(quadUV.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        quadUVBuffer = bb2.asFloatBuffer();
        quadUVBuffer.put(quadUV);
        quadUVBuffer.position(0);

        ByteBuffer bb3 = ByteBuffer.allocateDirect(quadIndex.length * 2);
        bb3.order(ByteOrder.nativeOrder());
        quadIndexBuffer = bb3.asShortBuffer();
        quadIndexBuffer.put(quadIndex);
        quadIndexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, context.getString(R.string.vertex));
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment));

        vertexShaderFrameBuffer = loadShader(GLES30.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb));
        fragmentShaderFrameBuffer = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb));

        fragmentShaderFrameBuffer_boxblur_down = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_downscale));
        fragmentShaderFrameBuffer_boxblur_down_prelift = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_downscale_prelift));
        fragmentShaderFrameBuffer_boxblur_up = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_upscale));
        fragmentShaderFrameBuffer_boxblur_up_final = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_upscale_final));
        fragmentShaderFrameBuffer_additive = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_additive_hdr));

        vertexShader_gaussian_h = loadShader(GLES30.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb_gaussian_h));
        vertexShader_gaussian_v = loadShader(GLES30.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb_gaussian_v));
        fragmentShader_gaussian = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_gaussian));

        fragmentShaderFrameBuffer_tonemapping = loadShader(GLES30.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_tonemapping));

        glProgram = GLES30.glCreateProgram();
        glProgramFrameBuffer_boxblur_down = GLES30.glCreateProgram();
        glProgramFrameBuffer_boxblur_down_prelift = GLES30.glCreateProgram();
        glProgramFrameBuffer_boxblur_up = GLES30.glCreateProgram();
        glProgramFrameBuffer_boxblur_up_final = GLES30.glCreateProgram();
        glProgramFrameBuffer_additive = GLES30.glCreateProgram();
        glProgram_draw = GLES30.glCreateProgram();
        glProgram_tonemapping = GLES30.glCreateProgram();

        glProgram_gaussian_h = GLES30.glCreateProgram();
        glProgram_gaussian_v = GLES30.glCreateProgram();

        GLES30.glAttachShader(glProgram, vertexShader);
        GLES30.glAttachShader(glProgram, fragmentShader);

        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_down, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_down, fragmentShaderFrameBuffer_boxblur_down);

        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_down_prelift, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_down_prelift, fragmentShaderFrameBuffer_boxblur_down_prelift);

        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_up, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_up, fragmentShaderFrameBuffer_boxblur_up);

        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_up_final, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgramFrameBuffer_boxblur_up_final, fragmentShaderFrameBuffer_boxblur_up_final);

        GLES30.glAttachShader(glProgramFrameBuffer_additive, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgramFrameBuffer_additive, fragmentShaderFrameBuffer_additive);

        GLES30.glAttachShader(glProgram_gaussian_h, vertexShader_gaussian_h);
        GLES30.glAttachShader(glProgram_gaussian_h, fragmentShader_gaussian);

        GLES30.glAttachShader(glProgram_gaussian_v, vertexShader_gaussian_v);
        GLES30.glAttachShader(glProgram_gaussian_v, fragmentShader_gaussian);

        GLES30.glAttachShader(glProgram_draw, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgram_draw, fragmentShaderFrameBuffer);

        GLES30.glAttachShader(glProgram_tonemapping, vertexShaderFrameBuffer);
        GLES30.glAttachShader(glProgram_tonemapping, fragmentShaderFrameBuffer_tonemapping);

        GLES30.glLinkProgram(glProgram);
        GLES30.glLinkProgram(glProgramFrameBuffer_boxblur_down);
        GLES30.glLinkProgram(glProgramFrameBuffer_boxblur_down_prelift);
        GLES30.glLinkProgram(glProgramFrameBuffer_boxblur_up);
        GLES30.glLinkProgram(glProgramFrameBuffer_boxblur_up_final);
        GLES30.glLinkProgram(glProgramFrameBuffer_additive);
        GLES30.glLinkProgram(glProgram_gaussian_h);
        GLES30.glLinkProgram(glProgram_gaussian_v);
        GLES30.glLinkProgram(glProgram_draw);
        GLES30.glLinkProgram(glProgram_tonemapping);

        GLES30.glUseProgram(glProgram);

        texWidth[0] = texHeight[0] = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES30.glViewport(0, 0, width, height);

        ratio = (float) height / width;
    }

    public void setupTextures(){
        if(textureIds[0] != 0) {
            GLES30.glDeleteTextures(14, textureIds, 0);
            GLES30.glDeleteFramebuffers(14, frameBuffer);
        }

        GLES30.glGenTextures(14, textureIds, 0);
        GLES30.glGenFramebuffers(14, frameBuffer);

        int w = width;
        int h = height;
        for(int i = 0; i < 5; i++){
            texWidth[i] = w;
            texHeight[i] = h;
            w /= 2;
            h /= 2;
        }
        for(int i = 5; i < 11; i++){
            //texWidth[i] = texWidth[11 - i - 1];
            //texHeight[i] = texHeight[11 - i - 1];
            texWidth[i] = texWidth[0];
            texHeight[i] = texHeight[0];
        }

        for(int i = 0; i < 11; i++){
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(i));

            GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + i);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[i]);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB16F, texWidth[i], texHeight[i], 0, GLES30.GL_RGB, GLES30.GL_FLOAT, null);
            GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureIds[i], 0);
        }

        // Final
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(13));
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + 13);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[13]);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGB16F, width, height, 0, GLES30.GL_RGB, GLES30.GL_FLOAT, null);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, textureIds[13], 0);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i("GL", game.getPuzzle() + " Rendered");
        Log.i("GL", 1000f / (System.currentTimeMillis() - lastUpdated) + " fps");
        lastUpdated = System.currentTimeMillis();

        if(game.getPuzzle() == null) {
            GLES30.glClearColor(1, 0, 1, 1.0f);
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
            return;
        }

        if (texWidth[0] != width || texHeight[0] != height) {
            setupTextures();
        }

        Puzzle puzzle = game.getPuzzle();

        if(puzzle.shouldUpdateAnimation()){
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }
        else{
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        puzzle.prepareForDrawing();

        puzzle.updateAnimation();
        puzzle.updateDynamicShapes();

        BoundingBox frustumBB = getFrustumBoundingBox(puzzle);

        Matrix.frustumM(mProjectionMatrix, 0, -frustumBB.getWidth() / 2, frustumBB.getWidth() / 2, -frustumBB.getHeight() / 2, frustumBB.getHeight() / 2, 1, 100);

        GLES30.glViewport(0, 0, width, height);

        // Draw the puzzle to the first frame buffer
        GLES30.glUseProgram(glProgram);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(0));

        int backgroundColor = game.getBackgroundColor();
        GLES30.glClearColor(Color.red(backgroundColor) / 255f, Color.green(backgroundColor) / 255f, Color.blue(backgroundColor) / 255f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        //카메라 좌표
        Matrix.setLookAtM(mViewMatrix, 0, frustumBB.getCenter().x, frustumBB.getCenter().y, 1, frustumBB.getCenter().x, frustumBB.getCenter().y, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        int aPositionHandle = GLES30.glGetAttribLocation(glProgram, "aPosition");

        GLES30.glEnableVertexAttribArray(aPositionHandle);

        int COORD_PER_VERTEX = 3;
        int VERTEX_STRIDE = 3 * 4;
        GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, puzzle.getVertexBuffer());

        int aColorHandle = GLES30.glGetAttribLocation(glProgram, "aColor");

        GLES30.glEnableVertexAttribArray(aColorHandle);

        GLES30.glVertexAttribPointer(aColorHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, puzzle.getVertexColorBuffer());

        int MVPMatrixHandle = GLES30.glGetUniformLocation(glProgram, "MVP");

        GLES30.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, puzzle.getVertexCount());

        for(int i = 1; i < 5; i++){
            if(i == 1){
                GLES30.glUseProgram(glProgramFrameBuffer_boxblur_down_prelift);
                GLES30.glViewport(0, 0, texWidth[i], texHeight[i]);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(i));

                aPositionHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_down_prelift, "aPosition");
                GLES30.glEnableVertexAttribArray(aPositionHandle);
                GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

                int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_down_prelift, "aTextureCoord");
                GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
                GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

                int texHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_down_prelift, "tex");
                GLES30.glUniform1i(texHandle, i - 1);

                int texelHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_down_prelift, "texel");
                GLES30.glUniform2f(texelHandle, 1f / texWidth[i], 1f / texHeight[i]);

                GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);
            }
            else{
                GLES30.glUseProgram(glProgramFrameBuffer_boxblur_down);
                GLES30.glViewport(0, 0, texWidth[i], texHeight[i]);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(i));

                aPositionHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_down, "aPosition");
                GLES30.glEnableVertexAttribArray(aPositionHandle);
                GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

                int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_down, "aTextureCoord");
                GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
                GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

                int texHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_down, "tex");
                GLES30.glUniform1i(texHandle, i - 1);

                int texelHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_down, "texel");
                GLES30.glUniform2f(texelHandle, 1f / texWidth[i], 1f / texHeight[i]);

                GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);
            }

        }

        for(int i = 5; i < 10; i++){
            if(i == 9){
                GLES30.glUseProgram(glProgramFrameBuffer_boxblur_up_final);
                GLES30.glViewport(0, 0, texWidth[i], texHeight[i]);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(i));

                aPositionHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_up_final, "aPosition");
                GLES30.glEnableVertexAttribArray(aPositionHandle);
                GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

                int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_up_final, "aTextureCoord");
                GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
                GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

                int texHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up_final, "tex");
                GLES30.glUniform1i(texHandle, i - 1);

                int texHandle2 = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up_final, "source");
                GLES30.glUniform1i(texHandle2, 10 - i - 1);

                int texelHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up_final, "texel");
                GLES30.glUniform2f(texelHandle, 1f / texWidth[i], 1f / texHeight[i]);

                int amountHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up_final, "amount");
                GLES30.glUniform1f(amountHandle, 0.2f);

                GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);
            }
            else{
                GLES30.glUseProgram(glProgramFrameBuffer_boxblur_up);
                GLES30.glViewport(0, 0, texWidth[i], texHeight[i]);
                GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(i));

                aPositionHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_up, "aPosition");
                GLES30.glEnableVertexAttribArray(aPositionHandle);
                GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

                int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur_up, "aTextureCoord");
                GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
                GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

                int texHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up, "tex");
                GLES30.glUniform1i(texHandle, i - 1);

                int texHandle2 = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up, "source");
                GLES30.glUniform1i(texHandle2, 10 - i - 1);

                int texelHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur_up, "texel");
                GLES30.glUniform2f(texelHandle, 1f / texWidth[i], 1f / texHeight[i]);

                GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);
            }
        }

        /*for(int i = 0; i < 3; i++){
            // Gaussian Blur
            GLES30.glUseProgram(glProgram_gaussian_h);
            GLES30.glViewport(0, 0, texWidth[2], texHeight[2]);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(2));

            aPositionHandle = GLES30.glGetAttribLocation(glProgram_gaussian_h, "aPosition");
            GLES30.glEnableVertexAttribArray(aPositionHandle);
            GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            aTextureCoordHandle = GLES30.glGetAttribLocation(glProgram_gaussian_h, "aTextureCoord");
            GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            texHandle = GLES30.glGetUniformLocation(glProgram_gaussian_h, "tex");
            GLES30.glUniform1i(texHandle, 1);

            int texelHandle = GLES30.glGetUniformLocation(glProgram_gaussian_h, "texel");
            GLES30.glUniform2f(texelHandle, 1f / texWidth[1], 1f / texHeight[1]);

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);

            // Gaussian blur
            GLES30.glUseProgram(glProgram_gaussian_v);
            GLES30.glViewport(0, 0, texWidth[1], texHeight[1]);
            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(1));

            aPositionHandle = GLES30.glGetAttribLocation(glProgram_gaussian_v, "aPosition");
            GLES30.glEnableVertexAttribArray(aPositionHandle);
            GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            aTextureCoordHandle = GLES30.glGetAttribLocation(glProgram_gaussian_v, "aTextureCoord");
            GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            texHandle = GLES30.glGetUniformLocation(glProgram_gaussian_v, "tex");
            GLES30.glUniform1i(texHandle, 2);

            texelHandle = GLES30.glGetUniformLocation(glProgram_gaussian_v, "texel");
            GLES30.glUniform2f(texelHandle, 1f / texWidth[2], 1f / texHeight[2]);

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);

        }*/


        // Downsampling -> Upsampling with gaussian blur
        /*for(int i = 1; i < 4; i++){
            GLES30.glViewport(0, 0, texWidth[i], texHeight[i]);

            GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(i));

            aPositionHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur, "aPosition");
            GLES30.glEnableVertexAttribArray(aPositionHandle);
            GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_boxblur, "aTextureCoord");
            GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            int texHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur, "tex");
            GLES30.glUniform1i(texHandle, i - 1);

            int texelHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_boxblur, "texel");
            GLES30.glUniform2f(texelHandle, 1f / texWidth[i - 1], 1f / texHeight[i - 1]);

            GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);
        }*/

        /*GLES30.glUseProgram(glProgramFrameBuffer_additive);

        GLES30.glViewport(0, 0, width, height);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer.get(13));
        GLES30.glClearColor(0, 0, 0, 0);

        aPositionHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_additive, "aPosition");
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

        int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgramFrameBuffer_additive, "aTextureCoord");
        GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

        int bgHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_additive, "bg");
        GLES30.glUniform1i(bgHandle, 0);

        int overlayHandle = GLES30.glGetUniformLocation(glProgramFrameBuffer_additive, "overlay");
        GLES30.glUniform1i(overlayHandle, 11);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);
        */
        // Add last result to screen
        GLES30.glUseProgram(glProgram_tonemapping);
        GLES30.glViewport(0, 0, width, height);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        GLES30.glClearColor(0, 0, 0, 0);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);

        aPositionHandle = GLES30.glGetAttribLocation(glProgram_tonemapping, "aPosition");
        GLES30.glEnableVertexAttribArray(aPositionHandle);
        GLES30.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES30.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

        int aTextureCoordHandle = GLES30.glGetAttribLocation(glProgram_tonemapping, "aTextureCoord");
        GLES30.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES30.glVertexAttribPointer(aTextureCoordHandle, 2, GLES30.GL_FLOAT, false, 2 * 4, quadUVBuffer);

        int hdrHandle = GLES30.glGetUniformLocation(glProgram_tonemapping, "hdrTex");
        GLES30.glUniform1i(hdrHandle, 9);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, quadIndex.length, GLES30.GL_UNSIGNED_SHORT, quadIndexBuffer);

        //GLES30.glDisableVertexAttribArray(vPositionHandle);
        synchronized(this){
            this.notifyAll();
        }
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES30.glCreateShader(type);

        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }

    public float getFrustumWidth(Puzzle puzzle){
        BoundingBox boundingBox = puzzle.getBoundingBox();

        float bbWidth = boundingBox.getWidth() + puzzle.getPadding() * 2;
        float bbHeight = boundingBox.getHeight() + puzzle.getPadding() * 2;

        if(bbWidth * ratio > bbHeight) return bbWidth;
        return bbHeight / ratio;
    }

    public BoundingBox getFrustumBoundingBox(Puzzle puzzle){
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.min = new Vector2(puzzle.getBoundingBox().getCenter().x - getFrustumWidth(puzzle) * 0.5f, puzzle.getBoundingBox().getCenter().y - getFrustumWidth(puzzle) * ratio * 0.5f);
        boundingBox.max = new Vector2(puzzle.getBoundingBox().getCenter().x + getFrustumWidth(puzzle) * 0.5f, puzzle.getBoundingBox().getCenter().y + getFrustumWidth(puzzle) * ratio * 0.5f);
        return boundingBox;
    }

    // https://stackoverflow.com/questions/5514149/capture-screen-of-glsurfaceview-to-bitmap
    public Bitmap captureToBitmap(int x, int y, int w, int h){
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        GLES30.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);

        int offset1, offset2;
        for (int i = 0; i < h; i++) {
            offset1 = i * w;
            offset2 = (h - i - 1) * w;
            for (int j = 0; j < w; j++) {
                int texturePixel = bitmapBuffer[offset1 + j];
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;
                bitmapSource[offset2 + j] = pixel;
            }
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }
}
