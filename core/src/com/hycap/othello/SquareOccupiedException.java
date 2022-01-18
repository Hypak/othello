package com.hycap.othello;

public class SquareOccupiedException extends Exception{
    public SquareOccupiedException(int x, int y) {
        super("Square is occupied at: " + x + ", " + y);
    }
}
