package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SimpleShipDisplayInfoTest {
  @Test
  public void test_getInfo() {
    Coordinate c1 = new Coordinate(1, 2);
    SimpleShipDisplayInfo<Character> display = new SimpleShipDisplayInfo<>('s', '*');
    
    // not hit case
    assertEquals('s', display.getInfo(c1, false));
    
    // hit case
    assertEquals('*', display.getInfo(c1, true));
  }

  @Test
  public void test_constructor_with_different_types() {
    // Integer
    SimpleShipDisplayInfo<Integer> intDisplay = new SimpleShipDisplayInfo<>(1, 0);
    assertEquals(1, intDisplay.getInfo(new Coordinate(0, 0), false));
    assertEquals(0, intDisplay.getInfo(new Coordinate(0, 0), true));
    
    // String
    SimpleShipDisplayInfo<String> strDisplay = new SimpleShipDisplayInfo<>("not-hit", "hit");
    assertEquals("not-hit", strDisplay.getInfo(new Coordinate(0, 0), false));
    assertEquals("hit", strDisplay.getInfo(new Coordinate(0, 0), true));
  }
} 
