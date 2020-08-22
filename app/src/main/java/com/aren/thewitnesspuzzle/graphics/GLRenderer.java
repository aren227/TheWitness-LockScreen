package com.aren.thewitnesspuzzle.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
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

public class GLRenderer implements GLSurfaceView.Renderer {

    private Game game;
    private Context context;

    private int vertexShader, fragmentShader, fragmentShaderFrameBuffer;
    private int vertexShaderFrameBuffer, fragmentShaderFrameBuffer_boxblur_down, fragmentShaderFrameBuffer_boxblur_up, fragmentShaderFrameBuffer_additive;
    private int vertexShader_gaussian_v, vertexShader_gaussian_h, fragmentShader_gaussian;
    private int glProgram;
    private int glProgramFrameBuffer_boxblur_down;
    private int glProgramFrameBuffer_boxblur_up;
    private int glProgramFrameBuffer_additive;
    private int glProgram_gaussian_v;
    private int glProgram_gaussian_h;
    private int glProgram_draw;

    private int width, height;
    private int[] texWidth = new int[13], texHeight = new int[13];

    private int textureIds[] = new int[13];

    private IntBuffer frameBuffer = IntBuffer.allocate(13);

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

    public GLRenderer(Game game, Context context){
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
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex));
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment));

        vertexShaderFrameBuffer = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb));
        fragmentShaderFrameBuffer = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb));

        fragmentShaderFrameBuffer_boxblur_down = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_downscale));
        fragmentShaderFrameBuffer_boxblur_up = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_upscale));
        fragmentShaderFrameBuffer_additive = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_additive));

        vertexShader_gaussian_h = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb_gaussian_h));
        vertexShader_gaussian_v = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb_gaussian_v));
        fragmentShader_gaussian = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_gaussian));

        glProgram = GLES20.glCreateProgram();
        glProgramFrameBuffer_boxblur_down = GLES20.glCreateProgram();
        glProgramFrameBuffer_boxblur_up = GLES20.glCreateProgram();
        glProgramFrameBuffer_additive = GLES20.glCreateProgram();
        glProgram_draw = GLES20.glCreateProgram();

        glProgram_gaussian_h = GLES20.glCreateProgram();
        glProgram_gaussian_v = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, vertexShader);
        GLES20.glAttachShader(glProgram, fragmentShader);

        GLES20.glAttachShader(glProgramFrameBuffer_boxblur_down, vertexShaderFrameBuffer);
        GLES20.glAttachShader(glProgramFrameBuffer_boxblur_down, fragmentShaderFrameBuffer_boxblur_down);

        GLES20.glAttachShader(glProgramFrameBuffer_boxblur_up, vertexShaderFrameBuffer);
        GLES20.glAttachShader(glProgramFrameBuffer_boxblur_up, fragmentShaderFrameBuffer_boxblur_up);

        GLES20.glAttachShader(glProgramFrameBuffer_additive, vertexShaderFrameBuffer);
        GLES20.glAttachShader(glProgramFrameBuffer_additive, fragmentShaderFrameBuffer_additive);

        GLES20.glAttachShader(glProgram_gaussian_h, vertexShader_gaussian_h);
        GLES20.glAttachShader(glProgram_gaussian_h, fragmentShader_gaussian);

        GLES20.glAttachShader(glProgram_gaussian_v, vertexShader_gaussian_v);
        GLES20.glAttachShader(glProgram_gaussian_v, fragmentShader_gaussian);

        GLES20.glAttachShader(glProgram_draw, vertexShaderFrameBuffer);
        GLES20.glAttachShader(glProgram_draw, fragmentShaderFrameBuffer);

        GLES20.glLinkProgram(glProgram);
        GLES20.glLinkProgram(glProgramFrameBuffer_boxblur_down);
        GLES20.glLinkProgram(glProgramFrameBuffer_boxblur_up);
        GLES20.glLinkProgram(glProgramFrameBuffer_additive);
        GLES20.glLinkProgram(glProgram_gaussian_h);
        GLES20.glLinkProgram(glProgram_gaussian_v);
        GLES20.glLinkProgram(glProgram_draw);

        GLES20.glUseProgram(glProgram);

        texWidth[0] = texHeight[0] = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, width, height);

        ratio = (float) height / width;
    }

    public void setupTextures(){
        if(textureIds[0] != 0) {
            GLES20.glDeleteTextures(13, textureIds, 0);
            GLES20.glDeleteFramebuffers(13, frameBuffer);
        }

        GLES20.glGenTextures(13, textureIds, 0);
        GLES20.glGenFramebuffers(13, frameBuffer);

        int w = width;
        int h = height;
        for(int i = 0; i < 7; i++){
            texWidth[i] = w;
            texHeight[i] = h;
            w /= 2;
            h /= 2;
        }
        for(int i = 7; i < 13; i++){
            texWidth[i] = texWidth[13 - i - 1];
            texHeight[i] = texHeight[13 - i - 1];
        }

        for(int i = 0; i < 13; i++){
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[i]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth[i], texHeight[i], 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureIds[i], 0);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i("GL", game.getPuzzle() + " Rendered");

        if(game.getPuzzle() == null) {
            GLES20.glClearColor(1, 0, 1, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
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

        GLES20.glViewport(0, 0, width, height);

        // Draw the puzzle to the first frame buffer
        GLES20.glUseProgram(glProgram);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(0));

        int backgroundColor = game.getBackgroundColor();
        GLES20.glClearColor(Color.red(backgroundColor) / 255f, Color.green(backgroundColor) / 255f, Color.blue(backgroundColor) / 255f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //카메라 좌표
        Matrix.setLookAtM(mViewMatrix, 0, frustumBB.getCenter().x, frustumBB.getCenter().y, 1, frustumBB.getCenter().x, frustumBB.getCenter().y, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        int aPositionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");

        GLES20.glEnableVertexAttribArray(aPositionHandle);

        int COORD_PER_VERTEX = 3;
        int VERTEX_STRIDE = 3 * 4;
        GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, puzzle.getVertexBuffer());

        int aColorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");

        GLES20.glEnableVertexAttribArray(aColorHandle);

        GLES20.glVertexAttribPointer(aColorHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, puzzle.getVertexColorBuffer());

        int MVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "MVP");

        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, puzzle.getVertexCount());

        for(int i = 1; i < 7; i++){
            GLES20.glUseProgram(glProgramFrameBuffer_boxblur_down);
            GLES20.glViewport(0, 0, texWidth[i], texHeight[i]);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

            aPositionHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_boxblur_down, "aPosition");
            GLES20.glEnableVertexAttribArray(aPositionHandle);
            GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            int aTextureCoordHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_boxblur_down, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            int texHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur_down, "tex");
            GLES20.glUniform1i(texHandle, i - 1);

            int texelHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur_down, "texel");
            GLES20.glUniform2f(texelHandle, 1f / texWidth[i], 1f / texHeight[i]);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);
        }

        for(int i = 7; i < 13; i++){
            GLES20.glUseProgram(glProgramFrameBuffer_boxblur_up);
            GLES20.glViewport(0, 0, texWidth[i], texHeight[i]);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

            aPositionHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_boxblur_up, "aPosition");
            GLES20.glEnableVertexAttribArray(aPositionHandle);
            GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            int aTextureCoordHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_boxblur_up, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            int texHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur_up, "tex");
            GLES20.glUniform1i(texHandle, i - 1);

            int texHandle2 = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur_up, "source");
            GLES20.glUniform1i(texHandle2, 13 - i - 1);

            int texelHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur_up, "texel");
            GLES20.glUniform2f(texelHandle, 1f / texWidth[i], 1f / texHeight[i]);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);
        }

        /*for(int i = 0; i < 3; i++){
            // Gaussian Blur
            GLES20.glUseProgram(glProgram_gaussian_h);
            GLES20.glViewport(0, 0, texWidth[2], texHeight[2]);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(2));

            aPositionHandle = GLES20.glGetAttribLocation(glProgram_gaussian_h, "aPosition");
            GLES20.glEnableVertexAttribArray(aPositionHandle);
            GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            aTextureCoordHandle = GLES20.glGetAttribLocation(glProgram_gaussian_h, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            texHandle = GLES20.glGetUniformLocation(glProgram_gaussian_h, "tex");
            GLES20.glUniform1i(texHandle, 1);

            int texelHandle = GLES20.glGetUniformLocation(glProgram_gaussian_h, "texel");
            GLES20.glUniform2f(texelHandle, 1f / texWidth[1], 1f / texHeight[1]);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);

            // Gaussian blur
            GLES20.glUseProgram(glProgram_gaussian_v);
            GLES20.glViewport(0, 0, texWidth[1], texHeight[1]);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(1));

            aPositionHandle = GLES20.glGetAttribLocation(glProgram_gaussian_v, "aPosition");
            GLES20.glEnableVertexAttribArray(aPositionHandle);
            GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            aTextureCoordHandle = GLES20.glGetAttribLocation(glProgram_gaussian_v, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            texHandle = GLES20.glGetUniformLocation(glProgram_gaussian_v, "tex");
            GLES20.glUniform1i(texHandle, 2);

            texelHandle = GLES20.glGetUniformLocation(glProgram_gaussian_v, "texel");
            GLES20.glUniform2f(texelHandle, 1f / texWidth[2], 1f / texHeight[2]);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);

        }*/


        // Downsampling -> Upsampling with gaussian blur
        /*for(int i = 1; i < 4; i++){
            GLES20.glViewport(0, 0, texWidth[i], texHeight[i]);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

            aPositionHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_boxblur, "aPosition");
            GLES20.glEnableVertexAttribArray(aPositionHandle);
            GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

            int aTextureCoordHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_boxblur, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
            GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, quadUVBuffer);

            int texHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur, "tex");
            GLES20.glUniform1i(texHandle, i - 1);

            int texelHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_boxblur, "texel");
            GLES20.glUniform2f(texelHandle, 1f / texWidth[i - 1], 1f / texHeight[i - 1]);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);
        }*/

        // Add last result to screen
        GLES20.glUseProgram(glProgramFrameBuffer_additive);

        GLES20.glViewport(0, 0, width, height);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        aPositionHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_additive, "aPosition");
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, quadPosBuffer);

        int aTextureCoordHandle = GLES20.glGetAttribLocation(glProgramFrameBuffer_additive, "aTextureCoord");
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, quadUVBuffer);

        int bgHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_additive, "bg");
        GLES20.glUniform1i(bgHandle, 12);

        int overlayHandle = GLES20.glGetUniformLocation(glProgramFrameBuffer_additive, "overlay");
        GLES20.glUniform1i(overlayHandle, 12);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);

        //GLES20.glDisableVertexAttribArray(vPositionHandle);
        synchronized(this){
            this.notifyAll();
        }
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

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

        GLES20.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);

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
