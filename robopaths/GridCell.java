package sprax.robopaths;

public class GridCell
{
    final int row;
    final int col;
    
    GridCell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public String toString() {
        return String.format("<%d, %d>", row, col);
    }
}
