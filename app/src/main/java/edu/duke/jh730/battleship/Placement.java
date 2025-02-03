package edu.duke.jh730.battleship;

/**
 * This class handles placement of a ship on the board,
 * consisting of a Coordinate and an orientation.
 */
public class Placement {
  private final Coordinate where;
  private final char orientation;

  /**
   * Constructs a Placement with the specified coordinate and orientation
   * @param where is the Coordinate where to place the ship
   * @param orientation is the orientation of the ship ('H' for horizontal, 'V' for vertical)
   */
  public Placement(Coordinate where, char orientation) {
    this.where = where;
    this.orientation = Character.toUpperCase(orientation);
  }

  /**
   * Creates a Placement from a string description like "A0V"
   * @param descr is the string description
   * @throws IllegalArgumentException if the string is invalid
   */
  public Placement(String descr) {
    if (descr == null || descr.length() != 3) {
      throw new IllegalArgumentException("Placement description must be exactly 3 characters");
    }
    
    String coordStr = descr.substring(0, 2);
    char orient = Character.toUpperCase(descr.charAt(2));
    
    if (orient != 'V' && orient != 'H') {
      throw new IllegalArgumentException("Orientation must be V or H");
    }
    
    this.where = new Coordinate(coordStr);
    this.orientation = orient;
  }

  /**
   * Gets the coordinate
   * @return the coordinate
   */
  public Coordinate getWhere() {
    return where;
  }

  /**
   * Gets the orientation
   * @return the orientation
   */
  public char getOrientation() {
    return orientation;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o.getClass().equals(getClass())) {
      Placement p = (Placement) o;
      return where.equals(p.where) && 
             Character.toUpperCase(orientation) == Character.toUpperCase(p.orientation);
    }
    return false;
  }

  @Override
  public String toString() {
    return "(" + where.toString() + ", " + orientation + ")";
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }
} 
