package edu.duke.jh730.battleship;

public interface Board<T> {
  public int getWidth();
  public int getHeight();
  
  /**
   * Try to add a ship to the board
   * @param toAdd is the ship to add
   * @return true if the ship was successfully added, false otherwise
   */
  public String tryAddShip(Ship<T> toAdd);

  /**
   * Get what is at a coordinate
   * @param where is the coordinate to check
   * @return what is at that coordinate
   */
  public T whatIsAtForSelf(Coordinate where);

  public T whatIsAtForEnemy(Coordinate where);

  public Ship<T> fireAt(Coordinate c);

  public boolean isAllSunk();

  /**
   * Check if a coordinate has already been fired at
   * @param where is the coordinate to check
   * @return true if this coordinate has already been fired at
   */
  public boolean wasAlreadyShot(Coordinate where);
} 
