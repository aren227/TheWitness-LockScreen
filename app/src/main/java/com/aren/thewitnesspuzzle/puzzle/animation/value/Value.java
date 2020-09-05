package com.aren.thewitnesspuzzle.puzzle.animation.value;

import com.aren.thewitnesspuzzle.puzzle.animation.Animation;

public class Value<T> {

    private T value;
    private T animationValue;

    public Value(T defaultValue) {
        value = defaultValue;
    }

    public T get() {
        if (animationValue != null) return animationValue;
        return value;
    }

    public T getOriginalValue() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void setAnimationValue(Animation animation, T value) {
        this.animationValue = value;
        animation.registerValue(this);
    }

    public void removeAnimationValue() {
        this.animationValue = null;
    }

}
