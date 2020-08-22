package com.aren.thewitnesspuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.graphics.GLRenderer;
import com.aren.thewitnesspuzzle.graphics.GLRenderer3;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.game.Game;

public class PuzzleGLSurfaceView extends GLSurfaceView {

    private Game game;

    public GLRenderer3 glRenderer;

    public Bitmap bitmap;
    public boolean bitmapRendered = false;

    public PuzzleGLSurfaceView(Game game, Context context) {
        super(context);

        this.game = game;

        setEGLContextClientVersion(3);

        glRenderer = new GLRenderer3(game, context);
        setRenderer(glRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    //터치 좌표계는 왼쪽 상단이 (0, 0)
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        if(e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE || e.getAction() == MotionEvent.ACTION_UP){
            float x = e.getX();
            float y = e.getY();

            float ratio = (float)getHeight() / getWidth();

            BoundingBox frustumBB = glRenderer.getFrustumBoundingBox(game.getPuzzle());

            x = MathUtils.lerp(frustumBB.min.x, frustumBB.max.x, x / getWidth());
            y = getHeight() - y; // Bottom-right should be (0, 0)
            y = MathUtils.lerp(frustumBB.min.y, frustumBB.max.y, y / getHeight());

            final float finalX = x;
            final float finalY = y;
            final int finalAction = e.getAction();
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    game.touchEvent(finalX, finalY, finalAction);
                }
            });
        }

        return true;
    }

    public void capture(){
        // Why It needs to draw several times to get a rendered result?
        // First one or two results are always black. wtf?
        // idk but i think it's buffer related issue.
        for(int i = 0; i < 5; i++){
            synchronized(glRenderer){
                requestRender();
                try {
                    glRenderer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                bitmap = glRenderer.captureToBitmap(0, 0, getWidth(), getHeight());
                bitmapRendered = true;

                synchronized (PuzzleGLSurfaceView.this){
                    PuzzleGLSurfaceView.this.notifyAll();
                }
            }
        });
    }

}
