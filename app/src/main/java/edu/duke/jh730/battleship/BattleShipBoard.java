package edu.duke.jh730.battleship;

import java.util.ArrayList;

public class BattleShipBoard<T> implements Board<T> {
  private final int width;
  private final int height;
  private final ArrayList<Ship<T>> myShips;
  private final PlacementRuleChecker<T> placementChecker;

  /**
   * Constructor for BattleShipBoard
   * @param w is the width of the board
   * @param h is the height of the board
   * @param placementChecker is the rule checker for placement
   * @throws IllegalArgumentException if the width or height are less than or equal to 0
   */
  public BattleShipBoard(int w, int h, PlacementRuleChecker<T> placementChecker) {
    if (w <= 0) {
      throw new IllegalArgumentException("BattleShipBoard's width must be positive but is " + w);
    }
    if (h <= 0) {
      throw new IllegalArgumentException("BattleShipBoard's height must be positive but is " + h);
    }
    this.width = w;
    this.height = h;
    this.myShips = new ArrayList<Ship<T>>();
    this.placementChecker = placementChecker;
  }

  public BattleShipBoard(int w, int h) {
    this(w, h, new InBoundsRuleChecker<T>(new NoCollisionRuleChecker<T>(null)));
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  @Override
  public String tryAddShip(Ship<T> toAdd) {
    if (placementChecker.checkPlacement(toAdd, this) != null) {
      return placementChecker.checkPlacement(toAdd, this);
    }
    myShips.add(toAdd);
    return null;
  }

  public T whatIsAt(Coordinate where) {
    for (Ship<T> s: myShips) {
      if (s.occupiesCoordinates(where)) {
        return s.getDisplayInfoAt(where);
      }
    }
    return null;
  }

  @Override
  public T whatIsAtForSelf(Coordinate where) {
    return whatIsAt(where);
  }

  @Override
  public T whatIsAtForEnemy(Coordinate where) {
    return whatIsAt(where);
  }
} 
