package com.hycap.othello;

// Always returns white's advantage
public interface Evaluator {
    int Evaluate(Board board);
}
