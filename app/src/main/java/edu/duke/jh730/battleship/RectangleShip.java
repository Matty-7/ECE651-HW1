package edu.duke.jh730.battleship;

import java.util.HashSet;

public class RectangleShip<T> extends BasicShip<T> {
  private final String name;
  private final int width;
  private final int height;

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

  public RectangleShip(String name, Coordinate upperLeft, int width, int height, 
                       ShipDisplayInfo<T> myDisplayInfo, ShipDisplayInfo<T> enemyDisplayInfo) {
    super(makeCoords(upperLeft, width, height), myDisplayInfo, enemyDisplayInfo);
    this.name = name;
    this.width = width;
    this.height = height;
  }

  public RectangleShip(String name, Coordinate upperLeft, int width, int height, T data, T onHit) {
    this(name, upperLeft, width, height, 
         new SimpleShipDisplayInfo<T>(data, onHit),
         new SimpleShipDisplayInfo<T>(null, data));
  }

  public RectangleShip(Coordinate upperLeft, T data, T onHit) {
    this("testship", upperLeft, 1, 1, data, onHit);
  }

  @Override
  public String getName() {
    return name;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
} 
