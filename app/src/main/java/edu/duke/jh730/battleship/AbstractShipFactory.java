package edu.duke.jh730.battleship;

public interface AbstractShipFactory<T> {
  public Ship<T> makeSubmarine(Placement where);
  public Ship<T> makeBattleship(Placement where);
  public Ship<T> makeCarrier(Placement where);
  public Ship<T> makeDestroyer(Placement where);
} 
