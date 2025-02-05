package edu.duke.jh730.battleship;

public interface Ship<T> {
  /**
   * Get the name of this Ship
   * @return the name of this ship
   */
  public String getName();

  /** 
   * Check if the ship occupies a given coordinate
   * @param where is the Coordinate to check if this Ship occupies
   * @return true if where is inside this ship, false if not.
   */
  public boolean occupiesCoordinates(Coordinate where);

  /**
   * Check if the ship has been sunk
   * @return true if this ship has been sunk, false otherwise.
   */
  public boolean isSunk();

  /**
   * Record a hit at a given coordinate
   * @param where specifies the coordinates that were hit.
   * @throws IllegalArgumentException if where is not part of the Ship
   */
  public void recordHitAt(Coordinate where);

  /**
   * Check if the ship was hit at a given coordinate
   * @param where is the coordinates to check.
   * @return true if this ship as hit at the indicated coordinates, and false
   *         otherwise.
   * @throws IllegalArgumentException if the coordinates are not part of this
   *                                  ship.
   */
  public boolean wasHitAt(Coordinate where);

  /**
   * Get the display information for a given coordinate
   * @param where is the coordinate to return information for
   * @throws IllegalArgumentException if where is not part of the Ship
   * @return The view-specific information at that coordinate.
   */
  public T getDisplayInfoAt(Coordinate where);
} 
