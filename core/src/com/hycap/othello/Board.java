package com.hycap.othello;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Board {
    private long isVoid;
    private long isWhite;
    private short freeSquares;
    public short getFreeSquares() {
        return freeSquares;
    }
    private short whiteAdvantage;
    public short getWhiteAdvantage() {
        return whiteAdvantage;
    }

    private static final List<IntPair> movementVectors;

    static {
        movementVectors = new ArrayList<>();
        movementVectors.add(new IntPair(1, 1));
        movementVectors.add(new IntPair(1, 0));
        movementVectors.add(new IntPair(1, -1));
        movementVectors.add(new IntPair(0, -1));
        movementVectors.add(new IntPair(-1, -1));
        movementVectors.add(new IntPair(-1, 0));
        movementVectors.add(new IntPair(-1, 1));
        movementVectors.add(new IntPair(0, 1));
    }

    public Board() {
        this.isVoid = -1;  // 2's complement for 111...111, all void
        this.isWhite = -1; // 2's complement for 111...111, all white
        this.isVoid ^= (1L << 27 | 1L << 28 | 1L << 35 | 1L << 36);
        this.isWhite ^= (1L << 28 | 1L << 35);
        this.freeSquares = 60;
    }

    public Board(Board board) {
        this.isVoid = board.isVoid;
        this.isWhite = board.isWhite;
        this.whiteAdvantage = board.whiteAdvantage;
        this.freeSquares = board.getFreeSquares();
    }

    public void SetAsBoard(Board board) {
        this.isVoid = board.isVoid;
        this.isWhite = board.isWhite;
        this.whiteAdvantage = board.whiteAdvantage;
        this.freeSquares = board.getFreeSquares();
    }

    public int GetWhiteCount() {
        int count = 0;
        long voidCopy = isVoid;
        long whiteCopy = isWhite;
        for (int i = 0; i < 64; ++i, voidCopy >>>= 1, whiteCopy >>>= 1) {
            if (voidCopy % 2 == 0 && whiteCopy % 2 != 0) {
                ++count;
            }
        }
        return count;
    }

    public int GetBlackCount() {
        int count = 0;
        long voidCopy = isVoid;
        long whiteCopy = isWhite;
        for (int i = 0; i < 64; ++i, voidCopy >>>= 1, whiteCopy >>>= 1) {
            if (voidCopy % 2 == 0 && whiteCopy % 2 == 0) {
                ++count;
            }
        }
        return count;
    }

    public SquareType GetSquareType(int x, int y) {
        int i = x + 8*y;
        if ((isVoid >>> i) % 2 != 0) {
            return SquareType.VOID;
        }
        if ((isWhite >>> i) % 2 != 0) {
            return SquareType.WHITE;
        }
        return SquareType.BLACK;
    }

    public static IntPair GetNewMove(Board newBoard, Board oldBoard) {
        long voidDiff = newBoard.isVoid ^ oldBoard.isVoid;
        int i;
        for (i = 0; voidDiff != 0 && i <= 64; ++i, voidDiff >>>=1) {
        }
        --i;
        return new IntPair(i % 8, i / 8);
    }

    public static List<IntPair> GetNewFlips(Board newBoard, Board oldBoard) {
        List<IntPair> newFlips = new ArrayList<>();
        long voidDiff = newBoard.isVoid ^ oldBoard.isVoid;
        long colourDiff = newBoard.isWhite ^ oldBoard.isWhite;
        long diff = colourDiff & ~voidDiff;
        for (int i = 0; diff != 0; ++i, diff >>=1) {
            if (diff % 2 != 0) {
                newFlips.add(new IntPair(i % 8, i / 8));
            }
        }
        return newFlips;
    }

    public boolean TryMove(int x, int y, boolean isWhite, boolean dryRun) {
        // Check if position is empty
        int i = x + 8*y;
        if ((isVoid >>> i) % 2 == 0) {
            return false;
        }

        // Perform relevant flips
        boolean performedFlips = false;
        SquareType sameType;
        SquareType oppositeType;
        if (isWhite) {
            sameType = SquareType.WHITE;
            oppositeType = SquareType.BLACK;
        } else {
            sameType = SquareType.BLACK;
            oppositeType = SquareType.WHITE;
        }
        for (IntPair vec : movementVectors) {
            int testX = x;
            int testY = y;
            int j;
            for (j = 0; j < 8; ++j) {
                testX += vec.getX();
                testY += vec.getY();
                if (testX < 0 || testX > 7 || testY < 0 || testY > 7) {
                    testX -= vec.getX();
                    testY -= vec.getY();
                }
                if (GetSquareType(testX, testY) != oppositeType) {
                    break;
                }
            }
            if (j < 1) {
                continue;
            }
            if (GetSquareType(testX, testY) != sameType) {
                continue;
            }
            performedFlips = true;
            if (dryRun) {
                break;
            }
            // Start flipping
            int flipX = x;
            int flipY = y;
            for (int k = 0; k < j; ++k) {
                flipX += vec.getX();
                flipY += vec.getY();
                int pos = flipX + 8*flipY;
                this.isWhite ^= 1L << pos;
            }
            if (isWhite) {
                this.whiteAdvantage += j;
            } else {
                this.whiteAdvantage -= j;
            }
        }
        if (performedFlips && !dryRun) {
            // Place original piece
            long mask = ~(1L << i);  // 111...101...111
            this.isVoid &= mask;  // Set isVoid[x, y] to False
            if (isWhite) {
                ++whiteAdvantage;
            } else {
                // Set square to black
                this.isWhite &= mask;
                --whiteAdvantage;
            }
            --this.freeSquares;
        }
        return performedFlips;
    }

    public List<Board> GetAllNextTurnBoards(boolean isWhite) {
        List<Board> res = new ArrayList<>();
        Board tempBoard = new Board(this);
        for (int y = 0; y < 8; ++y) {
            for (int x = 0; x < 8; ++x) {
                if (tempBoard.TryMove(x, y, isWhite, false)) {
                    res.add(tempBoard);
                    tempBoard = new Board(this);
                }
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int y = 0; y < 8; ++y) {
            res.append(8 - y);
            res.append(" ");
            for (int x = 0; x < 8; ++x) {
                SquareType sType = GetSquareType(x, y);
                res.append(sType.toString());
                res.append(" ");
            }
            res.append("\n");
        }
        res.append("  a b c d e f g h\n");

        return res.toString();
    }
}
