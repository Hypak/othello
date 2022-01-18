package com.hycap.othello;

public class AdvantageOnlyEvaluator implements Evaluator{
    @Override
    public int Evaluate(Board board) {
        return board.getWhiteAdvantage();
    }
}
