package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BattleShipBoardTest {
  private <T> void checkWhatIsAtBoard(BattleShipBoard<T> b, T[][] expected) {
    for (int i = 0; i < b.getHeight(); i++) {
      for (int j = 0; j < b.getWidth(); j++) {
        assertEquals(expected[i][j], b.whatIsAt(new Coordinate(i, j)));
      }
    }
  }

  @Test
  public void test_invalid_dimensions() {
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(0, 2, 'X'));
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(2, 0, 'X'));
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(-1, 2, 'X'));
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(2, -1, 'X'));
  }

  @Test
  public void test_width_height() {
    Board<Character> b1 = new BattleShipBoard<Character>(10, 20, 'X');
    assertEquals(10, b1.getWidth());
    assertEquals(20, b1.getHeight());
  }

  @Test
  public void test_empty_board() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 2, 'X');
    Character[][] expected = new Character[2][2];
    checkWhatIsAtBoard(b, expected);
  }

  @Test
  public void test_add_ship() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 2, 'X');
    Character[][] expected = new Character[2][2];
    Coordinate c1 = new Coordinate(1, 0);
    Ship<Character> s1 = new RectangleShip<Character>(c1, 's', '*');
    assertNull(b.tryAddShip(s1));
    expected[1][0] = 's';
    checkWhatIsAtBoard(b, expected);

    // Try to add ship that collides
    Ship<Character> s2 = new RectangleShip<Character>(c1, 'd', '*');
    assertEquals("That placement is invalid: the ship overlaps another ship.", b.tryAddShip(s2));
    checkWhatIsAtBoard(b, expected);

    // Try to add ship out of bounds
    Ship<Character> s3 = new RectangleShip<Character>("Test", new Coordinate(-1, 0), 1, 1,
        new SimpleShipDisplayInfo<Character>('b', '*'),
        new SimpleShipDisplayInfo<Character>('b', 'b'));
    assertEquals("That placement is invalid: the ship goes off the top of the board.", b.tryAddShip(s3));
    checkWhatIsAtBoard(b, expected);
  }

  @Test
  public void test_what_is_at_for_enemy() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 2, 'X');
    Coordinate c1 = new Coordinate(1, 0);
    Ship<Character> s1 = new RectangleShip<Character>(c1, 's', '*');
    assertNull(b.tryAddShip(s1));
    
    // Before hitting, should see nothing
    assertNull(b.whatIsAtForEnemy(c1));
    assertNull(b.whatIsAtForEnemy(new Coordinate(0, 0)));

    // After hitting, should see the ship
    b.fireAt(c1);
    assertEquals('s', b.whatIsAtForEnemy(c1));
  }

  @Test
  public void test_fireAt() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(3, 3, 'X');
    Coordinate c1 = new Coordinate(1, 1);
    Ship<Character> s1 = new RectangleShip<Character>(c1, 's', '*');
    b.tryAddShip(s1);

    // Test hit
    Ship<Character> hitShip = b.fireAt(c1);
    assertSame(s1, hitShip);
    assertTrue(s1.wasHitAt(c1));
    assertTrue(s1.isSunk());

    // Test miss
    Coordinate c2 = new Coordinate(0, 0);
    assertNull(b.fireAt(c2));
  }

  @Test
  public void test_is_all_sunk() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(3, 3, 'X');
    
    // Empty board should return true
    assertTrue(b.isAllSunk());
    
    // Add one ship
    Ship<Character> s1 = new RectangleShip<Character>(new Coordinate(0, 0), 's', '*');
    b.tryAddShip(s1);
    assertFalse(b.isAllSunk());
    
    // Hit the ship
    b.fireAt(new Coordinate(0, 0));
    assertTrue(b.isAllSunk());
    
    // Add another ship
    Ship<Character> s2 = new RectangleShip<Character>(new Coordinate(1, 1), 'd', '*');
    b.tryAddShip(s2);
    assertFalse(b.isAllSunk());
    
    // Hit both ships
    b.fireAt(new Coordinate(1, 1));
    assertTrue(b.isAllSunk());
  }

  @Test
  public void test_repeat_fire() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(3, 3, 'X');
    Coordinate c1 = new Coordinate(1, 1);
    Ship<Character> s1 = new RectangleShip<Character>(c1, 's', '*');
    b.tryAddShip(s1);

    // First shot
    Ship<Character> hitShip = b.fireAt(c1);
    assertSame(s1, hitShip);
    assertTrue(b.wasAlreadyShot(c1));

    // Repeat shot should still hit the ship
    hitShip = b.fireAt(c1);
    assertNotNull(hitShip);
    assertTrue(hitShip.wasHitAt(c1));
    
    // Shot at empty space
    Coordinate c2 = new Coordinate(0, 0);
    assertNull(b.fireAt(c2));
    assertTrue(b.wasAlreadyShot(c2));
  }

  @Test
  public void test_moveShip() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    
    // Add a 2x1 rectangle ship at (1,1)
    Ship<Character> s = new RectangleShip<Character>("Destroyer",
        new Coordinate(1, 1), 2, 1,
        new SimpleShipDisplayInfo<Character>('d', '*'),
        new SimpleShipDisplayInfo<Character>('d', 'd'));
    assertNull(b.tryAddShip(s));

    // Fire at (1,1) => partial hit
    b.fireAt(new Coordinate(1, 1));
    assertTrue(s.wasHitAt(new Coordinate(1, 1)));
    assertFalse(s.wasHitAt(new Coordinate(1, 2)));

    // Move the ship to (2,0) horizontally
    Placement newP = new Placement(new Coordinate(2, 0), 'H');
    String moveErr = b.moveShip(s, newP);
    assertNull(moveErr);

    // Check new position
    Ship<Character> newShip = b.getShipAt(new Coordinate(2, 0));
    assertNotNull(newShip);
    assertTrue(newShip.wasHitAt(new Coordinate(2, 0)));
    assertFalse(newShip.wasHitAt(new Coordinate(2, 1)));

    // Check old position is cleared
    assertNull(b.getShipAt(new Coordinate(1, 1)));
    assertNull(b.getShipAt(new Coordinate(1, 2)));

    // Try invalid move
    moveErr = b.moveShip(newShip, new Placement(new Coordinate(4, 4), 'H'));
    assertNotNull(moveErr);
  }


  @Test
  public void test_moveShip_invalid_ship() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    Ship<Character> s = new RectangleShip<Character>("Test", new Coordinate(0, 0), 1, 1, 't', '*');
    
    // Try to move a ship that's not on the board
    String err = b.moveShip(s, new Placement(new Coordinate(1, 1), 'H'));
    assertEquals("That ship is not on this board!", err);
  }

} 
