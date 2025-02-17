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

  /**
   * Get the ship at the specified coordinate
   * @param where is the Coordinate to check for a ship
   * @return the Ship at the specified coordinate, or null if no ship is present
   */
  public Ship<T> getShipAt(Coordinate where);

  /**
   * Get all ships on this board
   * @return Iterable of all ships
   */
  public Iterable<Ship<T>> getShips();
  
  /**
   * Move a ship to a new position
   * @param toMove the ship to move
   * @param newP the new placement for the ship
   * @return null if successful, or a string describing why the move failed
   */
  public String moveShip(Ship<T> toMove, Placement newP);
} 
