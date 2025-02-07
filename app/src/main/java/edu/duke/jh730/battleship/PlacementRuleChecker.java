package edu.duke.jh730.battleship;

public abstract class PlacementRuleChecker<T> {
  private final PlacementRuleChecker<T> next;

  public PlacementRuleChecker(PlacementRuleChecker<T> next) {
    this.next = next;
  }

  protected abstract boolean checkMyRule(Ship<T> theShip, Board<T> theBoard);

  public boolean checkPlacement(Ship<T> theShip, Board<T> theBoard) {
    if (!checkMyRule(theShip, theBoard)) {
      return false;
    }
    if (next != null) {
      return next.checkPlacement(theShip, theBoard);
    }
    return true;
  }
} 
