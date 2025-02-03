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
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(0, 2));
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(2, 0));
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(-1, 2));
    assertThrows(IllegalArgumentException.class, () -> new BattleShipBoard<Character>(2, -1));
  }

  @Test
  public void test_width_height() {
    Board<Character> b1 = new BattleShipBoard<Character>(10, 20);
    assertEquals(10, b1.getWidth());
    assertEquals(20, b1.getHeight());
  }

  @Test
  public void test_empty_board() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 2);
    Character[][] expected = new Character[2][2];
    checkWhatIsAtBoard(b, expected);
  }

  @Test
  public void test_add_ship() {
    BattleShipBoard<Character> b = new BattleShipBoard<Character>(2, 2);
    Character[][] expected = new Character[2][2];
    Coordinate c1 = new Coordinate(1, 0);
    Ship<Character> s1 = new BasicShip(c1);
    assertTrue(b.tryAddShip(s1));
    expected[1][0] = 's';
    checkWhatIsAtBoard(b, expected);
  }
} 
