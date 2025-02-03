package edu.duke.jh730.battleship;

/**
 * It supports two ways to display the Board:
 * one for the player's own board, and one for the 
 * enemy's board.
 */
public class BoardTextView {
  private final Board toDisplay;

  /**
   * Constructs a BoardView, given the board it will display.
   * @param toDisplay is the Board to display
   * @throws IllegalArgumentException if the board is larger than 10x26.
   */
  public BoardTextView(Board toDisplay) {
    this.toDisplay = toDisplay;
    if (toDisplay.getWidth() > 10 || toDisplay.getHeight() > 26) {
      throw new IllegalArgumentException(
          "Board must be no larger than 10x26, but is " + toDisplay.getWidth() + "x" + toDisplay.getHeight());
    }
  }

  /**
   * This makes the header line,
   * @return the String that is the header line for the given board
   */
  String makeHeader() {
    StringBuilder ans = new StringBuilder("  ");
    String sep = ""; //start with nothing to separate, then switch to | to separate
    for (int i = 0; i < toDisplay.getWidth(); i++) {
      ans.append(sep);
      ans.append(i);
      sep = "|";
    }
    ans.append("\n");
    return ans.toString();
  }

  /**
   * This displays the board for the player (own board).
   * @return the String representation of the board.
   */
  public String displayMyOwnBoard() {
    String header = makeHeader();
    StringBuilder body = new StringBuilder();
    for (int row = 0; row < toDisplay.getHeight(); row++) {
      body.append((char)('A' + row));
      for (int col = 0; col < toDisplay.getWidth(); col++) {
        if (col == 0) {
          body.append(" ");
        }
        body.append(" ");
        if (col < toDisplay.getWidth() - 1) {
          body.append("|");
        }
      }
      body.append(" ");
      body.append((char)('A' + row));
      body.append("\n");
    }
    return header + body.toString() + header;
  }
} 
