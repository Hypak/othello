package com.hycap.othello;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class TransformCoords {
    private static int scale = 1;
    public static float squareSize = 32 * scale;
    public static float boardSize = 68 * 4 * scale;
    public static float boardRimSize = (boardSize - 8 * squareSize) / 2;  // == 8 by default

    public static Vector2 GetWorldCoords(int boardX, int boardY) {
        float x = Gdx.graphics.getWidth() / 2f + (boardX - 3.5f) * squareSize;
        float y = Gdx.graphics.getHeight() / 2f + (3.5f - boardY) * squareSize;
        return new Vector2(x, y);
    }
    public static IntPair GetBoardCoords(float worldX, float worldY) {
        int x = (int) Math.floor((worldX - boardRimSize) / squareSize);
        int y = (int) Math.floor((worldY - boardRimSize) / squareSize);
        return new IntPair(x, y);
    }
}
