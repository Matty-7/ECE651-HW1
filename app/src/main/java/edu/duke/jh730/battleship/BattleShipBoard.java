package edu.duke.jh730.battleship;

import java.util.ArrayList;
import java.util.HashSet;

public class BattleShipBoard<T> implements Board<T> {
  private final int width;
  private final int height;
  private final ArrayList<Ship<T>> myShips;
  private final PlacementRuleChecker<T> placementChecker;
  private final HashSet<Coordinate> enemyMisses;
  private final T missInfo;

  /**
   * Constructor for BattleShipBoard
   * @param w is the width of the board
   * @param h is the height of the board
   * @param placementChecker is the rule checker for placement
   * @param missInfo is the information to display for a missed shot
   * @throws IllegalArgumentException if the width or height are less than or equal to 0
   */
  public BattleShipBoard(int w, int h, PlacementRuleChecker<T> placementChecker, T missInfo) {
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
    this.enemyMisses = new HashSet<Coordinate>();
    this.missInfo = missInfo;
  }

  public BattleShipBoard(int w, int h, T missInfo) {
    this(w, h, new InBoundsRuleChecker<T>(new NoCollisionRuleChecker<T>(null)), missInfo);
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
        return s.getDisplayInfoAt(where, true);
      }
    }
    return null;
  }

  @Override
  public T whatIsAtForSelf(Coordinate where) {
    return whatIsAt(where, true);
  }

  @Override
  public T whatIsAtForEnemy(Coordinate where) {
    return whatIsAt(where, false);
  }

  @Override
  public Ship<T> fireAt(Coordinate c) {
    for (Ship<T> s : myShips) {
      if (s.occupiesCoordinates(c)) {
        s.recordHitAt(c);
        return s;
      }
    }
    enemyMisses.add(c);
    return null;
  }

  protected T whatIsAt(Coordinate where, boolean isSelf) {
    for (Ship<T> s: myShips) {
      if (s.occupiesCoordinates(where)) {
        return s.getDisplayInfoAt(where, isSelf);
      }
    }
    if (!isSelf && enemyMisses.contains(where)) {
      return missInfo;
    }
    return null;
  }

  @Override
  public boolean isAllSunk() {
    for (Ship<T> s : myShips) {
      if (!s.isSunk()) {
        return false;
      }
    }
    return true;
  }
} 
