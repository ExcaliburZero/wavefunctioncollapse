package wavefunctioncollapse;

import java.util.Optional;
import java.util.function.Function;

public class TileConfiguration {
    /**
     * The tiles neighboring the target tile for this tile configuration.
     */
    //final int[][] neighbors;

    final int tileType;
    final Function<BoardPosition,Boolean> fitsFunction;

    public TileConfiguration(final int tileType, final Function<BoardPosition,Boolean> fitsFunction) {
        //this.neighbors = neighbors;

        //final int middleRow = neighbors.length / 2;
        //final int middleColumn = neighbors.length / 2;
        this.tileType = tileType;
        this.fitsFunction = fitsFunction;
    }

    /**
     * Returns true if the given board position could fit this tile
     * configuration.
     */
    public boolean fitsConfiguration(final Board board, int row, int column) {
        return fitsFunction.apply(new BoardPosition(board, row, column));
        /*final int lastRow = board.getNumRows() - 1;
        final int lastColumn = board.getNumColumns() - 1;

        final int n = neighbors.length / 2;
        final int m = neighbors[0].length / 2;

        final int startRow    = Math.max(0         , row    - n);
        final int endRow      = Math.min(lastRow   , row    + n);
        final int startColumn = Math.max(0         , column - m);
        final int endColumn   = Math.min(lastColumn, column + m);

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                final Optional<Integer> tileState = board.getTileState(i, j);

                final int i2 = i - row + n;
                final int j2 = j - column + n;

                if (tileState.isPresent() && tileState.get() != neighbors[i2][j2]) {
                    return false;
                }
            }
        }

        return true;*/
    }
}
