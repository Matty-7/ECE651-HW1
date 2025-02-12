package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class RectangleShipTest {
  @Test
  public void test_makeCoords() {
    Coordinate upperLeft = new Coordinate(1, 2);
    HashSet<Coordinate> coords = RectangleShip.makeCoords(upperLeft, 1, 3);
    
    assertTrue(coords.contains(new Coordinate(1, 2)));
    assertTrue(coords.contains(new Coordinate(2, 2)));
    assertTrue(coords.contains(new Coordinate(3, 2)));
    assertEquals(3, coords.size());
  }

  @Test
  public void test_hit_functions() {
    Coordinate c1 = new Coordinate(1, 2);
    Coordinate c2 = new Coordinate(1, 3);
    RectangleShip<Character> ship = new RectangleShip<Character>("testship", c1, 2, 1, 's', '*');
    
    assertFalse(ship.wasHitAt(c1));
    ship.recordHitAt(c1);
    assertTrue(ship.wasHitAt(c1));
    assertFalse(ship.wasHitAt(c2));
    
    assertFalse(ship.isSunk());
    ship.recordHitAt(c2);
    assertTrue(ship.isSunk());
  }

  @Test
  public void test_getDisplayInfoAt() {
    Coordinate c1 = new Coordinate(1, 2);
    RectangleShip<Character> ship = new RectangleShip<Character>("testship", c1, 1, 1, 's', '*');
    
    assertEquals('s', ship.getDisplayInfoAt(c1, true));
    ship.recordHitAt(c1);
    assertEquals('*', ship.getDisplayInfoAt(c1, true));
  }

  @Test
  public void test_invalid_coordinates() {
    Coordinate c1 = new Coordinate(1, 2);
    Coordinate c2 = new Coordinate(0, 0);
    RectangleShip<Character> ship = new RectangleShip<Character>("testship", c1, 1, 1, 's', '*');
    
    assertThrows(IllegalArgumentException.class, () -> ship.recordHitAt(c2));
    assertThrows(IllegalArgumentException.class, () -> ship.wasHitAt(c2));
    assertThrows(IllegalArgumentException.class, () -> ship.getDisplayInfoAt(c2, true));
  }
} 
