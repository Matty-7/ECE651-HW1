package edu.duke.jh730.battleship;

import java.util.HashMap;

public abstract class BasicShip<T> implements Ship<T> {
  protected HashMap<Coordinate, Boolean> myPieces;
  protected ShipDisplayInfo<T> myDisplayInfo;

  public BasicShip(Iterable<Coordinate> where, ShipDisplayInfo<T> myDisplayInfo) {
    this.myPieces = new HashMap<Coordinate, Boolean>();
    this.myDisplayInfo = myDisplayInfo;
    for (Coordinate c : where) {
      myPieces.put(c, false);
    }
  }

  @Override
  public boolean occupiesCoordinates(Coordinate where) {
    return myPieces.containsKey(where);
  }

  @Override
  public boolean isSunk() {
    return false;
  }

  @Override
  public void recordHitAt(Coordinate where) {
  }

  @Override
  public boolean wasHitAt(Coordinate where) {
    return false;
  }

  @Override
  public T getDisplayInfoAt(Coordinate where) {
    //TODO: this is not right. We need to look up the hit status of this coordinate
    return myDisplayInfo.getInfo(where, false);
  }
} 
