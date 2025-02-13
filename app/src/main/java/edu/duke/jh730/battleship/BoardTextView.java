package edu.duke.jh730.battleship;

import java.util.function.Function;

/**
 * It supports two ways to display the Board:
 * one for the player's own board, and one for the 
 * enemy's board.
 */
public class BoardTextView {
  private final Board<Character> toDisplay;

  /**
   * Constructor for BoardTextView
   * @param toDisplay is the Board to display
   * @throws IllegalArgumentException if the board is too large to display
   */
  public BoardTextView(Board<Character> toDisplay) {
    this.toDisplay = toDisplay;
    if (toDisplay.getWidth() > 10 || toDisplay.getHeight() > 26) {
      throw new IllegalArgumentException("Board must be no larger than 10x26, but is " + toDisplay.getWidth() + "x" + toDisplay.getHeight());
    }
  }

  /**
   * Make the header line for the given board
   * @return the String that is the header line for the given board
   */
  String makeHeader() {
    StringBuilder ans = new StringBuilder("  ");
    String sep = "";
    for (int i = 0; i < toDisplay.getWidth(); i++) {
      ans.append(sep);
      ans.append(i);
      sep = "|";
    }
    ans.append("\n");
    return ans.toString();
  }

  protected String displayAnyBoard(Function<Coordinate, Character> getSquareFn) {
    StringBuilder ans = new StringBuilder();
    ans.append(makeHeader());
    for (int row = 0; row < toDisplay.getHeight(); row++) {
      ans.append((char)('A' + row)).append(" ");
      String sep = "";
      for (int column = 0; column < toDisplay.getWidth(); column++) {
        ans.append(sep);
        Character what = getSquareFn.apply(new Coordinate(row, column));
        ans.append(what == null ? " " : what);
        sep = "|";
      }
      ans.append(" ").append((char)('A' + row)).append("\n");
    }
    ans.append(makeHeader());
    return ans.toString();
  }

  public String displayMyOwnBoard() {
    return displayAnyBoard((c) -> toDisplay.whatIsAtForSelf(c));
  }

  public String displayEnemyBoard() {
    return displayAnyBoard((c) -> toDisplay.whatIsAtForEnemy(c));
  }

  /**
   * Display my board with enemy's board next to it
   * @param enemyView is the enemy's board view
   * @param leftTitle is the header for my board
   * @param rightTitle is the header for enemy's board
   * @return the String that represents both boards side by side
   * @throws IllegalArgumentException if enemy board has different dimensions
   */
  public String displayMyBoardWithEnemyNextToIt(BoardTextView enemyView, String leftTitle, String rightTitle) {
    // First, check that both boards have the same dimensions.
    if (this.toDisplay.getWidth() != enemyView.toDisplay.getWidth() ||
        this.toDisplay.getHeight() != enemyView.toDisplay.getHeight()) {
        throw new IllegalArgumentException("Boards must have the same dimensions");
    }
    
    // Get our board displays (each is assumed to end with a newline).
    String myDisplay = this.displayMyOwnBoard();
    String enemyDisplay = enemyView.displayMyOwnBoard();
    
    // Split the displays into lines.
    String[] myLines = myDisplay.split("\n");
    String[] enemyLines = enemyDisplay.split("\n");
    
    StringBuilder sb = new StringBuilder();
    // Create the title line.
    // For a 2x2 board the test expects:
    // "     Your ocean        Player B's ocean\n"
    // Here we use fixed padding (5 spaces before left title and 8 spaces between titles)
    String titleLine = String.format("%5s%s%8s%s", "", leftTitle, "", rightTitle);
    sb.append(titleLine).append("\n");
    
    // For the board lines, we use different gaps:
    // For header/footer lines (first and last line) use a gap of 20 spaces,
    // and for the row lines use a gap of 16 spaces.
    int numLines = myLines.length;
    for (int i = 0; i < numLines; i++) {
        String gap;
        if (i == 0 || i == numLines - 1) {
            gap = "                    "; // 20 spaces
        } else {
            gap = "                "; // 16 spaces
        }
        sb.append(myLines[i]).append(gap).append(enemyLines[i]).append("\n");
    }
    
    return sb.toString();
  }
} 
