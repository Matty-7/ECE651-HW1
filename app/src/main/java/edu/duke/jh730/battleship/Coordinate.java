package edu.duke.jh730.battleship;

public class Coordinate {
    private final int row;
    private final int column;
  
    /**
     * Creates a new Coordinate with the specified row and column
     * @param row the row number
     * @param column the column number
     */
    public Coordinate(int row, int column) {
      this.row = row;
      this.column = column;
    }
  
    /**
     * Creates a new Coordinate from a string description like "B3" where
     * the letter represents the row (A=0, B=1, etc) and the number represents the column
     * @param descr the string description
     * @throws IllegalArgumentException if the string is invalid
     */
    public Coordinate(String descr) {
      if (descr == null || descr.length() != 2) {
        throw new IllegalArgumentException("Coordinate description must be exactly 2 characters");
      }
      
      descr = descr.toUpperCase();
      char rowLetter = descr.charAt(0);
      char colChar = descr.charAt(1);
      
      if (rowLetter < 'A' || rowLetter > 'Z') {
        throw new IllegalArgumentException("Row letter must be A-Z");
      }
      
      if (colChar < '0' || colChar > '9') {
        throw new IllegalArgumentException("Column must be 0-9");
      }
  
      this.row = rowLetter - 'A';
      this.column = colChar - '0';
    }
  
    /**
     * Gets the row number
     * @return the row number
     */
    public int getRow() {
      return row;
    }
  
    /**
     * Gets the column number
     * @return the column number
     */
    public int getColumn() {
      return column;
    }
  
    @Override
    public boolean equals(Object o) {
      if (o == null) {
        return false;
      }
      if (o.getClass().equals(getClass())) {
        Coordinate c = (Coordinate) o;
        return row == c.row && column == c.column;
      }
      return false;
    }
  
    @Override
    public String toString() {
      return "(" + row + ", " + column + ")";
    }
  
    @Override
    public int hashCode() {
      return toString().hashCode();
    }
  }
