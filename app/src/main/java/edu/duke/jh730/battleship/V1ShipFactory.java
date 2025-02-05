package edu.duke.jh730.battleship;

public class V1ShipFactory implements AbstractShipFactory<Character> {
  protected Ship<Character> createShip(Placement where, int w, int h, char letter, String name) {
    if (where.getOrientation() != 'V' && where.getOrientation() != 'H') {
      throw new IllegalArgumentException("Orientation must be 'V' or 'H' but is " + where.getOrientation());
    }
    int width = where.getOrientation() == 'V' ? w : h;
    int height = where.getOrientation() == 'V' ? h : w;
    return new RectangleShip<Character>(name, where.getWhere(), width, height, letter, '*');
  }

  @Override
  public Ship<Character> makeSubmarine(Placement where) {
    return createShip(where, 1, 2, 's', "Submarine");
  }

  @Override
  public Ship<Character> makeDestroyer(Placement where) {
    return createShip(where, 1, 3, 'd', "Destroyer");
  }

  @Override
  public Ship<Character> makeBattleship(Placement where) {
    return createShip(where, 1, 4, 'b', "Battleship");
  }

  @Override
  public Ship<Character> makeCarrier(Placement where) {
    return createShip(where, 1, 6, 'c', "Carrier");
  }
} 
