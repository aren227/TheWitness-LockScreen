package com.aren.thewitnesspuzzle.puzzle.rules;

public class SymmetricColorable extends Rule {

    public SymmetricColor color;

    public SymmetricColorable() {
        super();
        this.color = null;
    }

    public SymmetricColorable(SymmetricColor color) {
        super();
        this.color = color;
    }

    public SymmetricColor getSymmetricColor() {
        return color;
    }

    public void setSymmetricColor(SymmetricColor color) {
        this.color = color;
    }

    public boolean hasSymmetricColor() {
        return color != null;
    }

}
