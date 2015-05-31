/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package defs.dame;

import defs.dame.DameConstants.*;
import defs.general.GenericCell;
import defs.general.GenericColumn;
import defs.general.GameState;
import defs.general.Move;
import defs.general.Player;
import defs.general.GenericRow;
import interfaces.UsableAsDameViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thunderchild
 */
public class DameGameState extends GameState implements
		UsableAsDameViewModel<Piece> {

	private DameTable dameTable = null;

	public DameGameState() {
	}

	public DameGameState(GameState parentState, Move move) {
		super(parentState, move);

	}

	// Methods
	// Base overrides
	@Override
	public void findPossibleMoves() {
		boolean canCapturePiece = false;
		List<GenericRow<Piece>> rows = dameTable.getRows();
		for (GenericRow<Piece> row : rows) {
			List<GenericColumn<Piece>> columns = row.getColumns();
			for (GenericColumn<Piece> column : columns) {
				GenericCell<Piece> currentCell = row.getCellByColumn(column);
				// if(currentCell.getCellValue() == ownpiec)
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						GenericCell<Piece> cell = dameTable.findCell(
								rows.indexOf(row) + i, columns.indexOf(column)
										+ j);
						if (cell != null && cell.getCellValue() == Piece.EMPTY) {
							int[] movement = { rows.indexOf(row),
									rows.indexOf(column), i, j };
							super.getChildMoves().add(new Move(movement));
						}
						// else if(cell != null && cell.getCellValue() ==
						// opponentscolor &&
						// getCellByRowAndColumn(rows.indexOf(row) + i+i,
						// columns.indexOf(column) + j +j).getCellValue ==
						// DameGameStateEventConstants.Piece.EMPTY){
						// if(getCellByRowAndColumn(rows.indexOf(row) + i+i,
						// columns.indexOf(column) + j +j).getCellValue ==
						// DameGameStateEventConstants.Piece.EMPTY){
						// Move move(rows.indexOf(row), rows.indexOf(column),
						// i+i, j+j);
						// move.capturePiece(cell);
						// canCapturePiece = true;
						// super.getChildMoves().add(move);
					}
				}
			}
		}
		if (canCapturePiece) {
			for (Move move : super.getAllMoves()) {
				if (!((DameMove) move).capturePiece()) {
					super.getAllMoves().remove(move);
				}
			}
		}

	}

	@Override
	public void createChildStates() {
		for (Move move : this.getAllMoves()) {
			super.getChildStates().add(new GameState(this, move));
		}
	}

	@Override
	public void draw() {
		super.draw(); // To change body of generated methods, choose Tools |
		// Templates.
	}

	@Override
	public void secondPlayerToWin() {
		super.secondPlayerToWin(); // To change body of generated methods,
		// choose Tools | Templates.
	}

	@Override
	public void firstPlayerToWin() {
		super.firstPlayerToWin(); // To change body of generated methods, choose
		// Tools | Templates.
	}

	@Override
	public boolean isTerminal() {
		return (this.getAllMoves().isEmpty()); // To change body of generated
												// methods,
		// choose Tools | Templates.
	}

	@Override
	public void secondPlayerToMove() {
		super.secondPlayerToMove(); // To change body of generated methods,
		// choose Tools | Templates.
	}

	@Override
	public void firstPlayerToMove() {
		super.firstPlayerToMove(); // To change body of generated methods,
		// choose Tools | Templates.
	}

	@Override
	public void getChild() {
		super.getChild(); // To change body of generated methods, choose Tools |
		// Templates.
	}

	@Override
	public ArrayList<GameState> getChildStates() {
		return super.getChildStates(); // To change body of generated methods,
		// choose Tools | Templates.
	}

	@Override
	public GameState undoMove() {
		return super.undoMove();
	}

	@Override
	public GameState doMove(Move move) {
		return super.doMove(move); // To change body of generated methods,
		// choose Tools | Templates.
	}

	@Override
	public boolean possibleMove(Move move) {
		return super.possibleMove(move); // To change body of generated methods,
		// choose Tools | Templates.
	}

	@Override
	public ArrayList<Move> getAllMoves() {
		if (super.getAllMoves() == null) {
			this.findPossibleMoves();
		}
		return super.getAllMoves();
	}

	public void setStartState(Player firstPlayer, DameTable dameTable) {
		super.setStartState(firstPlayer);
		this.dameTable = dameTable;

		// Notify view for update
		setChanged();
		notifyObservers(DameEventConstants.STARTSTATESET);
	}

	// UsableAsDameViewModel<Character> (interface) methods
	@Override
	public DameTable getGameTable() {
		return this.dameTable;
	}

	@Override
	public void moveStone(int sourceRowIndex, int sourceColumnIndex,
			int targetRowIndex, int targetColumnIndex) {
		this.dameTable.moveValue(sourceRowIndex, sourceColumnIndex,
				targetRowIndex, targetColumnIndex, Piece.EMPTY);

		// Notify view for update
		setChanged();
		notifyObservers(DameEventConstants.STONEMOVED);
	}
}
