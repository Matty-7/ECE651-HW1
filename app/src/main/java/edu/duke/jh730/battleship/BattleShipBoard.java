package edu.duke.jh730.battleship;


public class BattleShipBoard implements Board {
  private final int width;
  private final int height;

  /**
   * Constructs a BattleShipBoard with the specified width and height
   * @param w is the width 
   * @param h is the height
   * @throws IllegalArgumentException if the width or height are less than or equal to zero.
   */
  public BattleShipBoard(int w, int h) {
    if (w <= 0) {
      throw new IllegalArgumentException("BattleShipBoard's width must be positive but is " + w);
    }
    if (h <= 0) {
      throw new IllegalArgumentException("BattleShipBoard's height must be positive but is " + h);
    }
    this.width = w;
    this.height = h;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
} 
