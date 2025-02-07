package edu.duke.jh730.battleship;

public class InBoundsRuleChecker<T> extends PlacementRuleChecker<T> {
  public InBoundsRuleChecker(PlacementRuleChecker<T> next) {
    super(next);
  }

  @Override
  protected boolean checkMyRule(Ship<T> theShip, Board<T> theBoard) {
    for (Coordinate c : theShip.getCoordinates()) {
      if (c.getRow() < 0 || c.getRow() >= theBoard.getHeight()) {
        return false;
      }
      if (c.getColumn() < 0 || c.getColumn() >= theBoard.getWidth()) {
        return false;
      }
    }
    return true;
  }
} 
