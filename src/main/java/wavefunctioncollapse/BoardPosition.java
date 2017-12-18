package wavefunctioncollapse;

public class BoardPosition {
    public final Board board;
    public final int row;
    public final int column;

    public BoardPosition(final Board board, final int row, final int column) {
        this.board = board;
        this.row = row;
        this.column = column;
    }
}
