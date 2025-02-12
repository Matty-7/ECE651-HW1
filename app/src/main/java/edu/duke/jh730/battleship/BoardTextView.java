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
} 
