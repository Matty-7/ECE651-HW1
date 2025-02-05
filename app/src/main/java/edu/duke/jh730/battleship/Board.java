package edu.duke.jh730.battleship;

public interface Board<T> {
  public int getWidth();
  public int getHeight();
  
  /**
   * Try to add a ship to the board
   * @param toAdd is the ship to add
   * @return true if the ship was successfully added, false otherwise
   */
  public boolean tryAddShip(Ship<T> toAdd);

  /**
   * Get what is at a coordinate
   * @param where is the coordinate to check
   * @return what is at that coordinate
   */
  public T whatIsAt(Coordinate where);
} 
