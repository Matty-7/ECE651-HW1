package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PlacementTest {
  @Test
  public void test_getters() {
    Coordinate c1 = new Coordinate(1, 2);
    Placement p1 = new Placement(c1, 'V');
    assertEquals(c1, p1.getWhere());
    assertEquals('V', p1.getOrientation());
  }

  @Test
  public void test_equals() {
    Coordinate c1 = new Coordinate(1, 2);
    Coordinate c2 = new Coordinate(1, 2);
    Coordinate c3 = new Coordinate(0, 0);
    Placement p1 = new Placement(c1, 'v');
    Placement p2 = new Placement(c2, 'V');
    Placement p3 = new Placement(c1, 'h');
    Placement p4 = new Placement(c3, 'v');
    assertEquals(p1, p1);   //equals should be reflexive
    assertEquals(p1, p2);   //different objects, same contents, case insensitive
    assertNotEquals(p1, p3);  //different orientation
    assertNotEquals(p1, p4);  //different coordinates
    assertNotEquals(p1, "(1, 2)");  //different types
    assertNotEquals(p1, null);
  }

  @Test
  public void test_hashCode() {
    Coordinate c1 = new Coordinate(1, 2);
    Coordinate c2 = new Coordinate(1, 2);
    Placement p1 = new Placement(c1, 'v');
    Placement p2 = new Placement(c2, 'V');
    Placement p3 = new Placement(c1, 'h');
    assertEquals(p1.hashCode(), p2.hashCode());
    assertNotEquals(p1.hashCode(), p3.hashCode());
  }

  @Test
  public void test_string_constructor_valid_cases() {
    Placement p1 = new Placement("B3V");
    assertEquals(new Coordinate(1, 3), p1.getWhere());
    assertEquals('V', p1.getOrientation());
    Placement p2 = new Placement("D5h");
    assertEquals(new Coordinate(3, 5), p2.getWhere());
    assertEquals('H', p2.getOrientation());
  }

  @Test
  public void test_string_constructor_error_cases() {
    assertThrows(IllegalArgumentException.class, () -> new Placement("AAA"));
    assertThrows(IllegalArgumentException.class, () -> new Placement("A12"));
    assertThrows(IllegalArgumentException.class, () -> new Placement("A"));
    assertThrows(IllegalArgumentException.class, () -> new Placement(null));
  }

  @Test
  public void test_toString() {
    Coordinate c1 = new Coordinate(1, 2);
    Placement p1 = new Placement(c1, 'V');
    assertEquals("((1, 2), V)", p1.toString());
  }
} 
