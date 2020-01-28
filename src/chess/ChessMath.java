package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMath {
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private List<Piece> piecesOnTheBoad = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();

	public ChessMath() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public boolean getCheck() {
		return check;
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}

		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		ValidateSoucePosition(position);
		return board.piece(position).possibleMoves();
	}

	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		ValidateSoucePosition(source);
		ValidateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		if (testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("Você não pode se colocar em CHECK!");
		}
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		if (testCheck(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		return (ChessPiece) capturedPiece;

	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece CapturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		if (CapturedPiece != null) {
			piecesOnTheBoad.remove(CapturedPiece);
			capturedPieces.add(CapturedPiece);
		}

		return CapturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoad.add(capturedPiece);
		}
	}

	private void ValidateSoucePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("Não exite peça na posiçao de origem!");

		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("A peça escolhida não é sua!");
		}
		if (!board.piece(position).isTherePossibleMove()) {
			throw new ChessException("Não existem movimentos possiveis para esta peça!");

		}
	}

	private void ValidateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode ser movida para a posiçao de destino!");
		}
	}

	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoad.add(piece);
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoad.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("Não existe o rei da cor:" + color + "no tabuleiro!");

	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoad.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}

		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoad.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece) p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void initialSetup() {

		placeNewPiece('h', 7, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE));

		placeNewPiece('b', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 8, new King(board, Color.BLACK));

//		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
//		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
//		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
//		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
//		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
//		placeNewPiece('d', 1, new King(board, Color.WHITE));
//
//		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
//		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
//		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
//		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
//		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
//		placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
