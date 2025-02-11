package edu.duke.jh730.battleship;

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

  /**
   * This displays the board
   * @return the String that is the board
   */
  public String displayMyOwnBoard() {
    StringBuilder ans = new StringBuilder();
    ans.append(makeHeader());
    
    for (int row = 0; row < toDisplay.getHeight(); row++) {
      String sep = "";
      ans.append((char)('A' + row));
      ans.append(" ");
      for (int col = 0; col < toDisplay.getWidth(); col++) {
        ans.append(sep);
        Character c = toDisplay.whatIsAtForSelf(new Coordinate(row, col));
        if (c == null) {
          ans.append(" ");
        } else {
          ans.append(c);
        }
        sep = "|";
      }
      ans.append(" ");
      ans.append((char)('A' + row));
      ans.append("\n");
    }
    
    ans.append(makeHeader());
    return ans.toString();
  }
} 
