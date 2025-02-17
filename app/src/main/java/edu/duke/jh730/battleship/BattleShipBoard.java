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
  private final HashSet<Coordinate> historicalHits;

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
    this.historicalHits = new HashSet<Coordinate>();
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
    // First check if any ship at this location is sunk
    for (Ship<T> s: myShips) {
      if (s.occupiesCoordinates(where) && s.isSunk()) {
        return s.getDisplayInfoAt(where, false);
      }
    }
    
    // Then check for historical hits
    if (historicalHits.contains(where)) {
      return (T)Character.valueOf('*');
    }
    
    // Then check for misses
    if (enemyMisses.contains(where)) {
      return missInfo;
    }
    
    // Finally, hide any non-sunk ships
    return null;
  }

  @Override
  public Ship<T> fireAt(Coordinate c) {
    Ship<T> hitShip = null;
    
    for (Ship<T> s : myShips) {
      if (s.occupiesCoordinates(c)) {
        s.recordHitAt(c);
        hitShip = s;
        historicalHits.add(c);
        break;
      }
    }
    
    if (hitShip == null) {
      enemyMisses.add(c);
    }
    
    allShots.add(c);
    return hitShip;
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
    return historicalHits.contains(where) || enemyMisses.contains(where);
  }

  @Override
  public Ship<T> getShipAt(Coordinate where) {
    for (Ship<T> s : myShips) {
      if (s.occupiesCoordinates(where)) {
        return s;
      }
    }
    return null;
  }

  @Override
  public Iterable<Ship<T>> getShips() {
    return myShips;
  }

  @Override
  public String moveShip(Ship<T> toMove, Placement newP) {
    if (!myShips.contains(toMove)) {
      return "That ship is not on this board!";
    }

    Ship<T> originalShip = toMove;
    HashSet<Coordinate> originalCoords = new HashSet<>();
    for (Coordinate c : toMove.getCoordinates()) {
      originalCoords.add(c);
    }

    myShips.remove(toMove);

    Ship<T> movedShip = null;
    try {
      if (toMove instanceof RectangleShip) {
        RectangleShip<T> rs = (RectangleShip<T>)toMove;
        int width = rs.getWidth();
        int height = rs.getHeight();
        if ((newP.getOrientation() == 'V' && rs.getWidth() > rs.getHeight()) ||
            (newP.getOrientation() == 'H' && rs.getHeight() > rs.getWidth())) {
          int temp = width;
          width = height;
          height = temp;
        }
        movedShip = new RectangleShip<>(rs.getName(), newP.getWhere(), 
                                       width, height,
                                       rs.getMyDisplayInfo(), rs.getEnemyDisplayInfo());
      } else if (toMove instanceof ComplexShip) {
        ComplexShip<T> cs = (ComplexShip<T>)toMove;
        HashSet<Coordinate> newCoords = new HashSet<>();
        for (Coordinate c : cs.getCoordinates()) {
          int relRow = c.getRow() - originalCoords.iterator().next().getRow();
          int relCol = c.getColumn() - originalCoords.iterator().next().getColumn();
          newCoords.add(new Coordinate(newP.getWhere().getRow() + relRow,
                                     newP.getWhere().getColumn() + relCol));
        }
        movedShip = new ComplexShip<T>(cs.getName(), newP.getWhere(),
                                     ShipDirection.fromChar(newP.getOrientation()),
                                     newCoords, cs.getMyDisplayInfo(),
                                     cs.getEnemyDisplayInfo());
      }
    } catch (IllegalArgumentException e) {
      myShips.add(originalShip);
      return e.getMessage();
    }

    String placementError = placementChecker.checkPlacement(movedShip, this);
    if (placementError != null) {
      myShips.add(originalShip);
      return placementError;
    }

    // Transfer hits to the new ship and update hit tracking
    for (Coordinate oldC : originalCoords) {
      // Remove old coordinates from historicalHits
      historicalHits.remove(oldC);
      // Add old coordinates to enemyMisses if they were hit
      if (originalShip.wasHitAt(oldC)) {
        enemyMisses.add(oldC);
        Coordinate relativeC = findRelativeCoordinate(oldC, originalShip, movedShip);
        if (relativeC != null) {
          movedShip.recordHitAt(relativeC);
          historicalHits.add(relativeC);
        }
      }
    }

    myShips.add(movedShip);
    return null;
  }

  private Coordinate findRelativeCoordinate(Coordinate oldC, Ship<T> oldShip, Ship<T> newShip) {
    Coordinate oldAnchor = null;
    Coordinate newAnchor = null;
    for (Coordinate c : oldShip.getCoordinates()) {
      if (oldAnchor == null || (c.getRow() <= oldAnchor.getRow() && c.getColumn() <= oldAnchor.getColumn())) {
        oldAnchor = c;
      }
    }
    for (Coordinate c : newShip.getCoordinates()) {
      if (newAnchor == null || (c.getRow() <= newAnchor.getRow() && c.getColumn() <= newAnchor.getColumn())) {
        newAnchor = c;
      }
    }

    int relativeRow = oldC.getRow() - oldAnchor.getRow();
    int relativeCol = oldC.getColumn() - oldAnchor.getColumn();

    for (Coordinate newC : newShip.getCoordinates()) {
      if (newC.getRow() - newAnchor.getRow() == relativeRow &&
          newC.getColumn() - newAnchor.getColumn() == relativeCol) {
        return newC;
      }
    }
    return null;
  }
} 
