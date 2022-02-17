package com.hycap.othello;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;

import java.util.ArrayList;
import java.util.List;

public class OthelloGame extends ApplicationAdapter {
	private Texture whitePiece;
	private Texture blackPiece;
	private Texture boardTex;
	private Texture blankSquare;
	private Texture highlight;
	private Texture whiteHighlight;
	private Texture blackHighlight;
	private Texture greyHighlight;
	private Texture playerHighlight;
	private Texture aiHighlight;
	private SpriteBatch batch;

	private Camera camera;
	private Viewport viewport;

	private Skin skin;
	private Stage uiStage;
	private Table scoreTable;
	private Label whiteScoreLabel;
	private Label blackScoreLabel;
	private Label blankSquareLabel;
	private Label evaluationLabel;

	private Board board;
	private final boolean playerIsWhite = false;
	private Player aiPlayer;
	private boolean isPlayer = true;
	private float timeUntilCalculate = 0;
	private float baseTimeUntilCalculate = 1f;
	private float doubleMoveTimeUntilCalculate = 0.75f;
	IntPair lastAiMove = new IntPair(-1, -1);
	List<IntPair> lastFlips = new ArrayList<>();

	@Override
	public void create () {
		skin = new Skin(Gdx.files.internal("gdx-skins-master/flat/skin/skin.json"));

		whitePiece = new Texture(
				Gdx.files.internal("whitePiece.png"));
		blackPiece = new Texture(
				Gdx.files.internal("blackPiece.png"));
		blankSquare = new Texture(
				Gdx.files.internal("blankSquare.png"));
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
		aiPlayer = new AIPlayer1(true);


		camera = new OrthographicCamera();
		viewport = new ExtendViewport(TransformCoords.boardSize, TransformCoords.boardSize, camera);
		scoreTable = new Table();
		scoreTable.setPosition(TransformCoords.boardSize, TransformCoords.boardSize / 2f - TransformCoords.squareSize);

		blankSquareLabel = new Label("N/A", skin);
		whiteScoreLabel = new Label("N/A", skin);
		blackScoreLabel = new Label("N/A", skin);
		evaluationLabel = new Label("N/A", skin);

		scoreTable.add(blankSquareLabel);
		scoreTable.add(new Label(" Free", skin));
		scoreTable.row();

		scoreTable.add(blackScoreLabel);
		scoreTable.add(new Image(blackPiece));
		scoreTable.row();

		scoreTable.add(whiteScoreLabel);
		scoreTable.add(new Image(whitePiece));
		scoreTable.row();

		// scoreTable.add(evaluationLabel);

		uiStage = new Stage(viewport);
		uiStage.addActor(scoreTable);

		batch = new SpriteBatch();
		board = new Board();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				Vector3 touchPos = new Vector3(screenX, screenY, 0);
				camera.unproject(touchPos);
				if (button == Input.Buttons.LEFT) {
					IntPair boardPos = TransformCoords.GetBoardCoords(touchPos.x, touchPos.y);
					playerMove(boardPos.getX(), boardPos.getY());
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

	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	private void playerMove(int boardX, int boardY) {
		Board oldBoard = new Board(board);
		if (board.TryMove(boardX, boardY, playerIsWhite, false)) {
			lastFlips = Board.GetNewFlips(board, oldBoard);
			if (board.GetAllNextTurnBoards(!playerIsWhite).size() == 0) {
				return;
			}
			isPlayer = false;
			timeUntilCalculate = baseTimeUntilCalculate;
		}
	}

	private void renderTexOnBoard(Texture tex, int boardX, int boardY) {
		float squareSize = TransformCoords.squareSize;
		batch.draw(tex, 8 + boardX * squareSize, 8 + boardY*squareSize, squareSize, squareSize);
	}

	private void renderBoard() {
		final float boardWidth = TransformCoords.boardSize;
		final float boardHeight = TransformCoords.boardSize;
		batch.draw(boardTex, 0,0,
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

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		blackScoreLabel.setText(Integer.toString(board.GetBlackCount()));
		whiteScoreLabel.setText(Integer.toString(board.GetWhiteCount()));
		blankSquareLabel.setText(Integer.toString(board.getFreeSquares()));
		evaluationLabel.setText(Float.toString(new BestEvaluator().Evaluate(board)));
		scoreTable.pack();


		renderBoard();

		renderPieces();
		renderTexOnBoard(aiHighlight, lastAiMove.getX(), lastAiMove.getY());
		for (IntPair flip : lastFlips) {
			renderTexOnBoard(greyHighlight, flip.getX(), flip.getY());
		}
		Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos);
		IntPair boardPos = TransformCoords.GetBoardCoords(mousePos.x, mousePos.y);
		int mbX = boardPos.getX();
		int mbY = boardPos.getY();
		if (mbX >= 0 && mbX < 8 && mbY >= 0 && mbY < 8) {
			renderTexOnBoard(playerHighlight, mbX, mbY);
		}

		if (!isPlayer) {
			if (timeUntilCalculate < 1) {
				Board oldBoard = new Board(board);
				aiPlayer.PlayMove(board);
				lastAiMove = Board.GetNewMove(board, oldBoard);
				lastFlips = Board.GetNewFlips(board, oldBoard);
				if (board.GetAllNextTurnBoards(playerIsWhite).size() == 0) {
					isPlayer = false;
					timeUntilCalculate = doubleMoveTimeUntilCalculate;
				} else {
					isPlayer = true;
				}
			}
			timeUntilCalculate -= Gdx.graphics.getDeltaTime();
		}
		batch.end();

		uiStage.act();
		uiStage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
