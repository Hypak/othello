package com.hycap.othello;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class OthelloGame extends ApplicationAdapter {
	private Texture whitePiece;
	private Texture blackPiece;
	private Texture boardTex;
	private Texture highlight;
	private Texture whiteHighlight;
	private Texture blackHighlight;
	private Texture greyHighlight;
	private Texture playerHighlight;
	private Texture aiHighlight;
	private SpriteBatch batch;

	private Board board;
	private final boolean playerIsWhite = false;
	private final Player aiPlayer = new AIPlayer1(true);
	private boolean isPlayer = true;
	private int framesUntilCalculate = 0;
	IntPair lastAiMove = new IntPair(-1, -1);
	List<IntPair> lastFlips = new ArrayList<>();

	private final float scaleFactor = 15;
	private final int viewWidth = 1920;
	private final int viewHeight = 1080;

	@Override
	public void create () {
		whitePiece = new Texture(
				Gdx.files.internal("whitePiece.png"));
		blackPiece = new Texture(
				Gdx.files.internal("blackPiece.png"));
		boardTex = new Texture(
				Gdx.files.internal("board.png"));
		highlight = new Texture(
				Gdx.files.internal("highlight.png"));
		whiteHighlight = new Texture(
				Gdx.files.internal("whiteHighlight.png"));
		blackHighlight = new Texture(
				Gdx.files.internal("blackHighlight.png"));
		greyHighlight = new Texture(
				Gdx.files.internal("greyHighlight.png"));
		if (playerIsWhite) {
			playerHighlight = whiteHighlight;
			aiHighlight = blackHighlight;
		} else {
			playerHighlight = blackHighlight;
			aiHighlight = whiteHighlight;
		}


		batch = new SpriteBatch();
		board = new Board();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					Pair<Integer, Integer> boardPos = TransformCoords.GetBoardCoords(screenX, screenY);
					playerMove(boardPos.getL(), boardPos.getR());
				}
				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.ESCAPE) {
					Gdx.app.exit();
				}
				return false;
			}
		});
	}

	private void playerMove(int boardX, int boardY) {
		Board oldBoard = new Board(board);
		if (board.TryMove(boardX, boardY, playerIsWhite, false)) {
			lastFlips = Board.GetNewFlips(board, oldBoard);
			isPlayer = false;
			framesUntilCalculate = 1;
		}
	}

	private void renderTexOnBoard(Texture tex, int boardX, int boardY) {
		float squareSize = TransformCoords.squareSize;
		Vector2 worldPos = TransformCoords.GetWorldCoords(boardX, boardY);
		batch.draw(tex, worldPos.x - squareSize / 2f,
				worldPos.y - squareSize / 2f,
				squareSize, squareSize);
	}

	private void renderBoard() {
		final float boardWidth = TransformCoords.boardSize;
		final float boardHeight = TransformCoords.boardSize;
		int halfWidth = viewWidth / 2;
		int halfHeight = viewHeight / 2;
		batch.draw(boardTex, halfWidth - boardWidth / 2f, halfHeight - boardHeight / 2f,
				boardWidth, boardHeight);
	}

	private void renderPieces() {
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				SquareType type = board.GetSquareType(x, y);
				if (type != SquareType.VOID) {
					if (type == SquareType.WHITE) {
						renderTexOnBoard(whitePiece, x, y);
					} else {
						renderTexOnBoard(blackPiece, x, y);
					}
				}
			}
		}
	}

	@Override
	public void render () {
		int backgroundBlue = 156;
		int backgroundGreen = 147;
		int backgroundRed = 133;
		ScreenUtils.clear(backgroundRed / 255f,
				backgroundGreen / 255f,
				backgroundBlue / 255f, 1);

		batch.begin();

		renderBoard();

		renderPieces();
		renderTexOnBoard(aiHighlight, lastAiMove.getX(), lastAiMove.getY());
		for (IntPair flip : lastFlips) {
			renderTexOnBoard(greyHighlight, flip.getX(), flip.getY());
		}
		Pair<Integer, Integer> mouseBoardPos = TransformCoords.GetBoardCoords(
				Gdx.input.getX(), Gdx.input.getY());
		int mbX = mouseBoardPos.getL();
		int mbY = mouseBoardPos.getR();
		if (mbX >= 0 && mbX < 8 && mbY >= 0 && mbY < 8) {
			renderTexOnBoard(playerHighlight, mbX, mbY);
		}

		if (!isPlayer) {
			if (framesUntilCalculate < 1) {
				Board oldBoard = new Board(board);
				aiPlayer.PlayMove(board);
				lastAiMove = Board.GetNewMove(board, oldBoard);
				lastFlips = Board.GetNewFlips(board, oldBoard);
				isPlayer = true;
			}
			--framesUntilCalculate;
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
