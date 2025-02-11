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
   * @param orientation is a character (e.g. 'H', 'V', or anything else)
   */
  public Placement(Coordinate where, char orientation) {
    this.where = where;
    this.orientation = Character.toUpperCase(orientation);
  }

  /**
   * Creates a Placement from a string description like "A0V"
   * @param descr is the string description
   * @throws IllegalArgumentException if the string is invalid in length
   *         or doesn't parse into a valid row-column pair
   */
  public Placement(String descr) {
    if (descr == null || descr.length() != 3) {
      throw new IllegalArgumentException("Placement description must be exactly 3 characters");
    }
    String coordStr = descr.substring(0, 2);
    this.orientation = Character.toUpperCase(descr.charAt(2));
    // We do NOT check orientation here, so that doOnePlacement can handle
    // invalid orientations via the ship factory. The only check below
    // is that the row is a letter and the column is a number.
    this.where = new Coordinate(coordStr);
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
      return where.equals(p.where)
          && Character.toUpperCase(orientation) == Character.toUpperCase(p.orientation);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return where.hashCode() * 31 + Character.toUpperCase(orientation);
  }

  @Override
  public String toString() {
    return "(" + where.toString() + ", " + orientation + ")";
  }
}
