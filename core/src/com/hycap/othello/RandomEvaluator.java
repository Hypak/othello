package com.hycap.othello;

import java.util.Random;

public class RandomEvaluator implements Evaluator {
    private static Random random;
    static {
        random = new Random();
    }

    @Override
    public float Evaluate(Board board) {
        return random.nextFloat();
    }
}
