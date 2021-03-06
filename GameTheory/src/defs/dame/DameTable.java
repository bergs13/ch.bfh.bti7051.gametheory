package defs.dame;

import java.util.ArrayList;
import java.util.List;

import defs.dame.DameConstants.Piece;
import defs.general.GenericCell;
import defs.general.GenericColumn;
import defs.general.GenericRow;
import defs.general.GenericTable;
import defs.general.Move;

public class DameTable extends GenericTable<Piece> {

	public DameTable() {
		super();
	}

	public DameTable(DameTable tableToCopy) {
		super();
		for (int i = 0; i < DameConstants.SQUARESPERSIDE; i++) {
			GenericRow<Piece> row = new GenericRow<Piece>();
			for (int j = 0; j < DameConstants.SQUARESPERSIDE; j++) {
				GenericColumn<Piece> column = new GenericColumn<Piece>();
				Piece cellValue = tableToCopy.getRows().get(i).getCells()
						.get(j).getCellValue();
				row.addColumn(column, cellValue);
			}
			this.addRow(row);
		}
	}

	@Override
	public boolean moveAllowed(GenericCell<Piece> sourceCell,
			GenericCell<Piece> targetCell) {

		// continue only if base move allowed
		if (super.moveAllowed(sourceCell, targetCell)) {

			// Set the horizontal and vertical difference for checks
			int rowIndexDifference = Math.abs(sourceCell.getRowIndex()
					- targetCell.getRowIndex());
			int columnIndexDifference = Math.abs(sourceCell.getColumnIndex()
					- targetCell.getColumnIndex());

			// check dame move allowed
			// Base check: source piece not empty and target piece empty
			if (sourceCell.getCellValue() != Piece.EMPTY
					&& targetCell.getCellValue() == Piece.EMPTY) {

				// Case 1: Move one space
				if (rowIndexDifference <= 1 && columnIndexDifference <= 1) {
					return true;
				} // Case 2: capture piece
				else if (columnIndexDifference % 2 == 0
						&& rowIndexDifference % 2 == 0) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean moveValue(int sourceRowIndex, int sourceColumnIndex,
			int targetRowIndex, int targetColumnIndex, Piece emptyValue) {
		// Perform move from base
		super.moveValue(sourceRowIndex, sourceColumnIndex, targetRowIndex,
				targetColumnIndex, emptyValue);

		// Perform remove (stones in between)
		int rowIndexForRemove = Integer.MIN_VALUE;
		int columnIndexForRemove = Integer.MIN_VALUE;
		// Set the horizontal and vertical difference for checks
		int rowDifference = Math.abs(sourceRowIndex - targetRowIndex);
		int columnDifference = Math.abs(sourceColumnIndex - targetColumnIndex);
		// case 1: stone between in same row
		if (sourceRowIndex == targetRowIndex && columnDifference == 2) {
			// one of both rows
			rowIndexForRemove = sourceRowIndex;
			// index between columns
			columnIndexForRemove = sourceColumnIndex < targetColumnIndex ? sourceColumnIndex + 1
					: targetColumnIndex + 1;
		} // case 2: stone between in same column
		else if (sourceColumnIndex == targetColumnIndex && rowDifference == 2) {
			// one of both columns
			columnIndexForRemove = sourceColumnIndex;
			// index between rows
			rowIndexForRemove = sourceRowIndex < targetRowIndex ? sourceRowIndex + 1
					: targetRowIndex + 1;
		}// case 3: stone between  diagonal
                else if (rowDifference == 2 && columnDifference == 2){
                    // index between rows
			rowIndexForRemove = sourceRowIndex < targetRowIndex ? sourceRowIndex + 1
					: targetRowIndex + 1;
                        // index between columns
			columnIndexForRemove = sourceColumnIndex < targetColumnIndex ? sourceColumnIndex + 1
					: targetColumnIndex + 1;
                }
		// Remove piece if piece identified
		if (rowIndexForRemove >= 0 && columnIndexForRemove >= 0) {
			/* Piece removedPiece = */
			removeValue(rowIndexForRemove, columnIndexForRemove, emptyValue);
		}
                return true;
	}

	public List<Move> getAllPossibleMoves(Piece playerToMovePieces) {
		List<Move> allPossibleMoves = new ArrayList<Move>();
		boolean canCapture = false;
		List<GenericRow<Piece>> rows = getRows();
		for (GenericRow<Piece> row : rows) {
			List<GenericColumn<Piece>> columns = row.getColumns();
			for (GenericColumn<Piece> column : columns) {
				GenericCell<Piece> sourceCell = row.getCellByColumn(column);
				if ((sourceCell.getCellValue() == playerToMovePieces)) {
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							GenericCell<Piece> targetCell = findCell(
									rows.indexOf(row) + i,
									columns.indexOf(column) + j);
							if (moveAllowed(sourceCell, targetCell)) {
								int[] movement = { sourceCell.getRowIndex(),
										sourceCell.getColumnIndex(),
										targetCell.getRowIndex(),
										targetCell.getColumnIndex() };
								allPossibleMoves.add(new DameMove(movement));
							} else if (targetCell != null
									&& targetCell.getCellValue() != Piece.EMPTY
									&& targetCell.getCellValue() != playerToMovePieces) {
								GenericCell<Piece> cellToCapture = targetCell;
								targetCell = findCell(
										rows.indexOf(row) + i + i,
										columns.indexOf(column) + j + j);
								if (moveAllowed(sourceCell, targetCell)) {
									int[] movement = { rows.indexOf(row),
											columns.indexOf(column),
											targetCell.getRowIndex(),
											targetCell.getColumnIndex() };
									allPossibleMoves.add(new DameMove(movement,
											cellToCapture));
									canCapture = true;
								}

							}
						}
					}
				}
			}
		}
		if (canCapture) {
			allPossibleMoves.removeIf(e -> !((DameMove) e).canCapture());
		}
		return allPossibleMoves;
	}
    public int getNumberOfPieces(Piece pieceToCount){
         int pieceCount = 0;
        List<GenericRow<Piece>> rows = this.getRows();
        for (GenericRow<Piece> row : rows) {
            List<GenericCell<Piece>> cells = row.getCells();
            for (GenericCell<Piece> cell : cells) {
                Piece cellValue = cell.getCellValue();
                if (pieceToCount == cellValue) {
                    pieceCount++;
                }
            }
        }
        return pieceCount;
    }
}
