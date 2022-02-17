package com.hycap.othello;

// Always returns white's advantage
public interface Evaluator {
    float Evaluate(Board board);
}
