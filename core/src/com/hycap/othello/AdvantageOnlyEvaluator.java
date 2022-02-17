package com.hycap.othello;

public class AdvantageOnlyEvaluator implements Evaluator{
    @Override
    public float Evaluate(Board board) {
        return board.getWhiteAdvantage();
    }
}
