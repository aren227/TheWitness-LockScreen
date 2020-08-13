package com.aren.thewitnesspuzzle.puzzle.animation;

import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Animation {

    protected long duration;
    protected int repeat;
    protected long startTime;
    protected boolean done;
    protected boolean remain;

    protected Runnable whenDone;

    private Set<Value> usedValues;

    public Animation(long duration, int repeat, boolean remain){
        this.duration = duration;
        this.repeat = repeat;
        this.remain = remain;
        startTime = System.currentTimeMillis();

        usedValues = new HashSet<>();
    }

    public boolean process(){
        float rate = (float)Math.min((double)(System.currentTimeMillis() - startTime) / duration, repeat);
        if(rate >= repeat){
            done();
            return remain;
        }
        else{
            update(rate % 1f);
        }
        return true;
    }

    protected abstract void update(float rate);

    private void done(){
        if(whenDone != null) whenDone.run();
        done = true;
    }

    public boolean isDone(){
        return done;
    }

    public boolean shouldRemain(){
        return remain;
    }

    public void whenDone(Runnable runnable){
        this.whenDone = runnable;
    }

    public void remove(){
        for(Value value : usedValues){
            value.removeAnimationValue();
        }
    }

    public void registerValue(Value value){
        usedValues.add(value);
    }

}
