package com.hycap.othello;

import java.util.List;

public class AIPlayer1 implements Player{

    private final boolean isWhite;
    private final Evaluator evaluator = new BestEvaluator();

    public AIPlayer1(boolean isWhite) {
        this.isWhite = isWhite;
    }

    // Max => player is white
    public int AlphaBetaMax(Board board, int alpha, int beta, int depthLeft) {
        if (depthLeft <= 0) {
            return evaluator.Evaluate(board);
        }
        List<Board> nextBoards = board.GetAllNextTurnBoards(true);
        for (Board nextBoard : nextBoards) {
            int value = AlphaBetaMin(nextBoard, alpha, beta, depthLeft - 1);
            if (value >= beta) {
                return beta;
            }
            if (value > alpha) {
                alpha = value;
            }
        }
        if (nextBoards.size() == 0) {
            return evaluator.Evaluate(board);
        }
        return alpha;
    }

    // Min => player is black
    public int AlphaBetaMin(Board board, int alpha, int beta, int depthLeft) {
        if (depthLeft <= 0) {
            return evaluator.Evaluate(board);
        }
        List<Board> nextBoards = board.GetAllNextTurnBoards(false);
        for (Board nextBoard : nextBoards) {
            int value = AlphaBetaMax(nextBoard, alpha, beta, depthLeft - 1);
            if (value <= alpha) {
                return alpha;
            }
            if (value < beta) {
                beta = value;
            }
        }
        if (nextBoards.size() == 0) {
            return evaluator.Evaluate(board);
        }
        return beta;
    }

    @Override
    public boolean PlayMove(Board board) {
        final int minDepth = 2;

        long time = System.nanoTime();
        long maxTime = 300L * 1000000;

        Board bestBoard = board;  // If the original board is returned, then there are 0 legal moves
        List<Board> nextBoards = board.GetAllNextTurnBoards(isWhite);
        if (nextBoards.size() == 0) {
            return false;
        }
        for (int depth = minDepth; depth < board.getFreeSquares(); ++depth) {
            int bestEval = Integer.MIN_VALUE + 1;
            for (int i = 0; i < nextBoards.size(); ++i) {
                int evaluation;
                if (isWhite) {
                    evaluation = AlphaBetaMin(nextBoards.get(i), bestEval, Integer.MAX_VALUE, depth);
                } else {
                    evaluation = -AlphaBetaMax(nextBoards.get(i), Integer.MIN_VALUE, -bestEval, depth);
                }
                if (evaluation > bestEval) {
                    bestEval = evaluation;
                    bestBoard = nextBoards.get(i);
                    if (i > 0) {
                        nextBoards.remove(i);
                        nextBoards.add(0, bestBoard);
                    }
                }
                long timeElapsed = System.nanoTime() - time;
                if (timeElapsed > maxTime) {  // Timeout for searching
                    return true;
                }
            }
            board.SetAsBoard(bestBoard);
        }
        return true;
    }
}
