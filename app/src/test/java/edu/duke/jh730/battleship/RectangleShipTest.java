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
} 
