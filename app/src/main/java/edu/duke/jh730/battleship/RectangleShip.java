package edu.duke.jh730.battleship;

import java.util.HashSet;

public class RectangleShip<T> extends BasicShip<T> {
  /**
   * Generate the set of coordinates for a rectangle starting at upperLeft
   * @param upperLeft is the upper left corner of the rectangle
   * @param width is the width of the rectangle
   * @param height is the height of the rectangle
   * @return a HashSet of the Coordinates that make up this rectangle
   */
  static HashSet<Coordinate> makeCoords(Coordinate upperLeft, int width, int height) {
    HashSet<Coordinate> coords = new HashSet<Coordinate>();
    int row = upperLeft.getRow();
    int col = upperLeft.getColumn();
    
    for (int r = row; r < row + height; r++) {
      for (int c = col; c < col + width; c++) {
        coords.add(new Coordinate(r, c));
      }
    }
    return coords;
  }

  public RectangleShip(Coordinate upperLeft, int width, int height, ShipDisplayInfo<T> displayInfo) {
    super(makeCoords(upperLeft, width, height), displayInfo);
  }

  public RectangleShip(Coordinate upperLeft, int width, int height, T data, T onHit) {
    this(upperLeft, width, height, new SimpleShipDisplayInfo<T>(data, onHit));
  }

  public RectangleShip(Coordinate upperLeft, T data, T onHit) {
    this(upperLeft, 1, 1, data, onHit);
  }
} 
