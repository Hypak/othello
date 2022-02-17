package com.hycap.othello;

import java.util.List;

public class AIPlayer1 implements Player{
    private boolean isWhite;
    public boolean isWhite() {
        return isWhite;
    }
    public void setWhite(boolean white) {
        isWhite = white;
    }

    private Evaluator evaluator = new BestEvaluator();

    private int thinkTimeNS = 1000000000;
    public int getThinkTimeNS() {
        return thinkTimeNS;
    }
    public void setThinkTimeNS(int thinkTimeNS) {
        this.thinkTimeNS = thinkTimeNS;
    }


    public Evaluator getEvaluator() {
        return evaluator;
    }
    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public AIPlayer1(boolean isWhite) {
        this.isWhite = isWhite;
    }

    // Max => player is white
    public float AlphaBetaMax(Board board, float alpha, float beta, int depthLeft) {
        if (depthLeft <= 0) {
            return evaluator.Evaluate(board);
        }
        List<Board> nextBoards = board.GetAllNextTurnBoards(true);
        for (Board nextBoard : nextBoards) {
            float value = AlphaBetaMin(nextBoard, alpha, beta, depthLeft - 1);
            if (value >= beta) {
                return beta;
            }
            if (value > alpha) {
                alpha = value;
            }
        }
        if (nextBoards.size() == 0) {
            if (board.GetAllNextTurnBoards(true).size() == 0) {
                return Math.signum(board.getWhiteAdvantage()) * 100;
            } else {
                return AlphaBetaMin(board, alpha, beta, depthLeft - 1);
            }
        }
        return alpha;
    }

    // Min => player is black
    public float AlphaBetaMin(Board board, float alpha, float beta, int depthLeft) {
        if (depthLeft <= 0) {
            return evaluator.Evaluate(board);
        }
        List<Board> nextBoards = board.GetAllNextTurnBoards(false);
        for (Board nextBoard : nextBoards) {
            float value = AlphaBetaMax(nextBoard, alpha, beta, depthLeft - 1);
            if (value <= alpha) {
                return alpha;
            }
            if (value < beta) {
                beta = value;
            }
        }
        if (nextBoards.size() == 0) {
            if (board.GetAllNextTurnBoards(true).size() == 0) {
                return Math.signum(board.getWhiteAdvantage()) * 100;
            } else {
                return AlphaBetaMax(board, alpha, beta, depthLeft - 1);
            }
        }
        return beta;
    }

    @Override
    public boolean PlayMove(Board board) {
        final int minDepth = 0;

        long time = System.nanoTime();
        long maxTime = thinkTimeNS;

        boolean updatedBoard = false;
        Board bestBoard = board;  // If the original board is returned, then there are 0 legal moves
        List<Board> nextBoards = board.GetAllNextTurnBoards(isWhite);
        if (nextBoards.size() == 0) {
            return false;
        }
        for (int depth = minDepth; depth < board.getFreeSquares(); ++depth) {
            float bestEval = Integer.MIN_VALUE + 1;
            for (int i = 0; i < nextBoards.size(); ++i) {
                float evaluation;
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
                    // System.out.println("Depth " + depth +  " Eval: " + bestEval);
                    if (!updatedBoard) {
                        board.SetAsBoard(bestBoard);
                    }
                    return true;
                }
            }
            board.SetAsBoard(bestBoard);
            updatedBoard = true;
        }
        return true;
    }
}
