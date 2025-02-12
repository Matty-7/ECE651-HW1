package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class V1ShipFactoryTest {
  private void checkShip(Ship<Character> testShip, String expectedName, char expectedLetter, Coordinate... expectedLocs) {
    assertEquals(expectedName, testShip.getName());
    for (Coordinate c: expectedLocs) {
      assertEquals(expectedLetter, testShip.getDisplayInfoAt(c, true));
      assertTrue(testShip.occupiesCoordinates(c));
    }
  }

  @Test
  public void test_createShips() {
    V1ShipFactory f = new V1ShipFactory();
    Placement v1_2 = new Placement(new Coordinate(1, 2), 'V');
    Ship<Character> sub = f.makeSubmarine(v1_2);
    checkShip(sub, "Submarine", 's', new Coordinate(1, 2), new Coordinate(2, 2));

    Placement h1_2 = new Placement(new Coordinate(1, 2), 'H');
    Ship<Character> dest = f.makeDestroyer(h1_2);
    checkShip(dest, "Destroyer", 'd', new Coordinate(1, 2), new Coordinate(1, 3), new Coordinate(1, 4));

    Ship<Character> battle = f.makeBattleship(v1_2);
    checkShip(battle, "Battleship", 'b', 
             new Coordinate(1, 2), new Coordinate(2, 2), 
             new Coordinate(3, 2), new Coordinate(4, 2));

    Ship<Character> carrier = f.makeCarrier(h1_2);
    checkShip(carrier, "Carrier", 'c',
             new Coordinate(1, 2), new Coordinate(1, 3), 
             new Coordinate(1, 4), new Coordinate(1, 5),
             new Coordinate(1, 6), new Coordinate(1, 7));
  }

  @Test
  public void test_invalid_orientation() {
    V1ShipFactory f = new V1ShipFactory();
    Placement p = new Placement(new Coordinate(1, 2), 'X');
    assertThrows(IllegalArgumentException.class, () -> f.makeSubmarine(p));
  }
} 
