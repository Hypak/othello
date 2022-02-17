package com.hycap.othello;

import java.util.Random;

public class RandomUntilEndgame implements Evaluator {
    private static Random random;
    static {
        random = new Random();
    }

    @Override
    public float Evaluate(Board board) {
        if (board.getFreeSquares() > 6) {
            return random.nextFloat();
        } else {
            return board.getWhiteAdvantage();
        }
    }
}
