package com.aren.thewitnesspuzzle.puzzle.animation;

public abstract class Animation {

    protected long duration;
    protected int repeat;
    protected long startTime;

    protected Runnable whenDone;

    public Animation(long duration, int repeat){
        this.duration = duration;
        this.repeat = repeat;
        startTime = System.currentTimeMillis();
    }

    public boolean process(){
        float rate = (float)Math.min((double)(System.currentTimeMillis() - startTime) / duration, repeat);
        update(rate % 1f);

        if(rate >= repeat){
            done();
            return false;
        }
        return true;
    }

    protected abstract void update(float rate);

    protected void done(){
        if(whenDone != null) whenDone.run();
    }

    public void whenDone(Runnable runnable){
        this.whenDone = runnable;
    }

}
