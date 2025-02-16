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
  private final HashSet<Coordinate> allShots;

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
    this.allShots = new HashSet<Coordinate>();
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
    if (allShots.contains(c)) {
        return null;
    }
    
    Ship<T> hitShip = null;
    for (Ship<T> s : myShips) {
        if (s.occupiesCoordinates(c)) {
            s.recordHitAt(c);
            hitShip = s;
            break;
        }
    }
    
    allShots.add(c);
    if (hitShip == null) {
        enemyMisses.add(c);
    }
    return hitShip;
  }

  protected T whatIsAt(Coordinate where, boolean isSelf) {
    for (Ship<T> s: myShips) {
      if (s.occupiesCoordinates(where)) {
        if (isSelf) {
          return s.getDisplayInfoAt(where, true);
        } else {
          // For enemy view
          if (!s.wasHitAt(where)) {
            return null;
          }
          // If the ship is hit at this location
          if (s.isSunk()) {
            // If the ship is sunk, show its actual symbol
            return s.getDisplayInfoAt(where, true);
          } else {
            // If hit but not sunk, show *
            return (T) Character.valueOf('*');
          }
        }
      }
    }
    if (!isSelf && enemyMisses.contains(where)) {
      return missInfo;
    }
    if (enemyMisses.contains(where)) {
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

  @Override
  public boolean wasAlreadyShot(Coordinate where) {
    return allShots.contains(where);
  }
} 
