package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);

	}

	public String toString() {
		return "K";
	}

	public boolean canMove(Position positon) {
		ChessPiece p = (ChessPiece) getBoard().piece(positon);
		return p == null || p.getColor() != getColor();
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);

		// mover acima

		p.setValues(position.getRow() - 1, position.getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// mover abaixo
		p.setValues(position.getRow() + 1, position.getColumn());
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// mover esquerda
		p.setValues(position.getRow(), position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		// mover direita
		p.setValues(position.getRow(), position.getColumn() + 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}

		// mover noroeste

		p.setValues(position.getRow() - 1, position.getColumn() - 1);
		if (getBoard().positionExists(p) && canMove(p)) {
			mat[p.getRow()][p.getColumn()] = true;
		}
		//mover nordeste
		
		p.setValues(position.getRow() -1, position.getColumn()+1);
			if(getBoard().positionExists(p) && canMove(p)) {
				mat[p.getRow()][p.getColumn()]= true;
			}
			//mover sudoeste
			
			p.setValues(position.getRow() +1, position.getColumn()-1);
				if(getBoard().positionExists(p) && canMove(p)) {
					mat[p.getRow()][p.getColumn()]= true;
				}
				//mover sudeste
				
				p.setValues(position.getRow() +1, position.getColumn()+1);
					if(getBoard().positionExists(p) && canMove(p)) {
						mat[p.getRow()][p.getColumn()]= true;
					}
		
		return mat;
	}

}
