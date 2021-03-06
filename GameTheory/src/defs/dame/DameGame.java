/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package defs.dame;

import interfaces.UsableAsDameViewModel;
import defs.dame.DameConstants.DameEventConstants;
import defs.dame.DameConstants.Piece;
import defs.general.Game;
import defs.general.GenericColumn;
import defs.general.GenericRow;

/**
 *
 * @author Thunderchild
 */
public class DameGame extends Game implements UsableAsDameViewModel<Piece> {
	// Members
	DameGameState dameGameState = null;
	DameEvaluator evaluator = new DameEvaluator(Piece.BLACK, Piece.WHITE);

	// Constructors
	public DameGame() {
		super("Dame-Spiel");
		// Set default players as initial players in game setup
		restartGame(true);
	}

	// Methods
	public boolean setupGame(boolean setDefaultPlayers) {
		// initialize table and set as start state
		DameTable table = new DameTable();
		// S S S S S
		// S S S S S
		// S S . W W
		// W W W W W
		// W W W W W
		for (int i = 0; i < DameConstants.SQUARESPERSIDE; i++) {
			GenericRow<Piece> row = new GenericRow<Piece>();
			for (int j = 0; j < DameConstants.SQUARESPERSIDE; j++) {
				GenericColumn<Piece> column = new GenericColumn<Piece>();
				Piece cellValue = Piece.EMPTY;
				if (i < 2) {
					cellValue = Piece.BLACK;
				} else if (i == 2) {
					if (j < 2) {
						cellValue = Piece.BLACK;
					} else if (j > 2) {
						cellValue = Piece.WHITE;
					}
				} else if (i > 2) {
					cellValue = Piece.WHITE;
				}
				row.addColumn(column, cellValue);
			}
			table.addRow(row);
		}

		if (setDefaultPlayers) {
			// Set the players to the game
			setFirstPlayer(new DamePlayer(true, Piece.WHITE));
			setSecondPlayer(new DamePlayer(false, Piece.BLACK));
		}
		((DamePlayer) getFirstPlayer()).setDepthToEvaluate(4);
		((DamePlayer) getSecondPlayer()).setDepthToEvaluate(4);

		this.dameGameState.setStartState(getFirstPlayer(), getSecondPlayer(),
				table);

		this.dameGameState.findPossibleMoves();

		// Notify view for update
		setChanged();
		notifyObservers(DameEventConstants.STARTSTATESET);

		return true;
	}

	// UsableAsDameViewModel<Piece> (interface) methods
	@Override
	public void restartGame(boolean setDefaultPlayers) {
		this.dameGameState = new DameGameState();
		setupGame(setDefaultPlayers);
	}

	@Override
	public DameTable getDameTable() {
		return this.dameGameState.getDameTable();
	}

	@Override
	public void applyPlayerSettings(boolean firstPlayerIsHuman,
			boolean secondPlayerIsHuman) {
		// update player types
		getFirstPlayer().setIsHuman(firstPlayerIsHuman);
		getSecondPlayer().setIsHuman(secondPlayerIsHuman);
		// use existing players as initial players in the game setup
		restartGame(false);
	}

	@Override
	public void performManualMove(int sourceRowIndex, int sourceColumnIndex,
			int targetRowIndex, int targetColumnIndex) {
		int[] movement = { sourceRowIndex, sourceColumnIndex, targetRowIndex,
				targetColumnIndex };
		DameMove dMove = new DameMove(movement);
		this.dameGameState = (DameGameState) this.dameGameState.doMove(dMove);

		// Notify view for update
		setChanged();
		notifyObservers(DameEventConstants.STONEMOVED);
	}

	@Override
	public void performCPUMove() {
		// Get next move vor CPU
		DameMove move = ((DamePlayer) this.dameGameState.getPlayerToMove())
				.getMove(this.dameGameState);
		// Perform move
		this.dameGameState = (DameGameState) this.dameGameState.doMove(move);

		// Notify view for update
		setChanged();
		notifyObservers(DameEventConstants.STONEMOVED);
	}

	public DameGameState getGameState() {
		return this.dameGameState;
	}
}
