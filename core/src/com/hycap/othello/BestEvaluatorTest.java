package com.hycap.othello;

import java.util.ArrayList;
import java.util.List;

public class BestEvaluatorTest implements Evaluator{

    static int cornerVal = 10;
    static int edgeVal = 2;

    static List<Pair<IntPair, IntPair>> cornersAndVecs;
    static {
        cornersAndVecs = new ArrayList<>();
        cornersAndVecs.add(new Pair<IntPair, IntPair>(
                new IntPair(0, 0), new IntPair(1, 1)));
        cornersAndVecs.add(new Pair<IntPair, IntPair>(
                new IntPair(0, 7), new IntPair(1, -1)));
        cornersAndVecs.add(new Pair<IntPair, IntPair>(
                new IntPair(7, 0), new IntPair(-1, 1)));
        cornersAndVecs.add(new Pair<IntPair, IntPair>(
                new IntPair(7, 7), new IntPair(-1, -1)));
    }

    private int GetMoveDiff(Board board) {
        int whiteCount = board.GetAllNextTurnBoards(true).size();
        int blackCount = board.GetAllNextTurnBoards(false).size();
        return whiteCount - blackCount;
    }

    private int GetSafeDiff(Board board) {
        int score = 0;
        for (Pair<IntPair, IntPair> cornerAndVec : cornersAndVecs) {
            int startX = cornerAndVec.getL().getX();
            int startY = cornerAndVec.getL().getY();
            int xInc = cornerAndVec.getR().getX();
            int yInc = cornerAndVec.getR().getY();
            SquareType type = board.GetSquareType(startX, startY);
            if (type == SquareType.VOID) {
                continue;
            }
            int scoreToAdd = cornerVal;

            int x = startX;
            for (int i = 0; i < 8; ++i) {
                x += xInc;
                if (board.GetSquareType(x, startY) == type) {
                    scoreToAdd += edgeVal;
                } else {
                    break;
                }
            }
            int y = startY;
            for (int i = 0; i < 8; ++i) {
                y += yInc;
                if (board.GetSquareType(startX, y) == type) {
                    scoreToAdd += edgeVal;
                } else {
                    break;
                }
            }
            if (type == SquareType.WHITE) {
                score += scoreToAdd;
            } else {
                score -= scoreToAdd;
            }
        }
        return score;
    }

    @Override
    public float Evaluate(Board board) {
        float score = 0;
        float freeSquareStart = 16;
        float moveDiffMult = (60 - board.getFreeSquares()) / 60f;
        moveDiffMult *= 1f;
        int safeDiffEnd = 8;
        score += GetMoveDiff(board) * moveDiffMult;
        if (board.getFreeSquares() < freeSquareStart) {
            float advantageMult = (board.getFreeSquares() - freeSquareStart) / freeSquareStart;
            advantageMult *= 4 * advantageMult;
            score += board.getWhiteAdvantage() * advantageMult;
        }
        if (board.getFreeSquares() > safeDiffEnd) {
            score += GetSafeDiff(board);
        }
        return score;
    }
}
