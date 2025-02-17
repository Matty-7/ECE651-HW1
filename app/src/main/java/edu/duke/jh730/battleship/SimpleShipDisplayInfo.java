package edu.duke.jh730.battleship;

public class SimpleShipDisplayInfo<T> implements ShipDisplayInfo<T> {
  private final T myData;
  private final T onHit;
  private final boolean isSunk;

  public SimpleShipDisplayInfo(T myData, T onHit) {
    this(myData, onHit, false);
  }

  public SimpleShipDisplayInfo(T myData, T onHit, boolean isSunk) {
    this.myData = myData;
    this.onHit = onHit;
    this.isSunk = isSunk;
  }

  @Override
  public T getInfo(Coordinate where, boolean hit) {
    if (hit) {
      return isSunk ? myData : onHit;
    }
    return myData;
  }
} 
