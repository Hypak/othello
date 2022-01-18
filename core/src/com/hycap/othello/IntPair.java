package com.hycap.othello;

public class IntPair{
    private final int x;
    private final int y;
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public IntPair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
