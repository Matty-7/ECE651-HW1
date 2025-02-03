package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BasicShipTest {
  @Test
  public void test_constructor_and_getters() {
    Coordinate c1 = new Coordinate(1, 2);
    BasicShip s1 = new BasicShip(c1);
    assertTrue(s1.occupiesCoordinates(c1));
    Coordinate c2 = new Coordinate(1, 3);
    assertFalse(s1.occupiesCoordinates(c2));
    assertEquals('s', s1.getDisplayInfoAt(c1));
  }

  @Test
  public void test_hit_functions() {
    Coordinate c1 = new Coordinate(1, 2);
    BasicShip s1 = new BasicShip(c1);
    assertFalse(s1.isSunk());
    assertFalse(s1.wasHitAt(c1));
    s1.recordHitAt(c1);
    assertFalse(s1.wasHitAt(c1));
  }
} 
