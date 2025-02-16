package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BoardTextViewTest {
  private void emptyBoardHelper(int w, int h, String expectedHeader, String body) {
    Board<Character> b1 = new BattleShipBoard<Character>(w, h, 'X');
    BoardTextView view = new BoardTextView(b1);
    assertEquals(expectedHeader, view.makeHeader());
    assertEquals(expectedHeader + body + expectedHeader, view.displayMyOwnBoard());
  }

  @Test
  public void test_invalid_board_size() {
    Board<Character> wideBoard = new BattleShipBoard<Character>(11,20, 'X');
    Board<Character> tallBoard = new BattleShipBoard<Character>(10,27, 'X');
    assertThrows(IllegalArgumentException.class, () -> new BoardTextView(wideBoard));
    assertThrows(IllegalArgumentException.class, () -> new BoardTextView(tallBoard));
  }

  @Test
  public void test_empty_board() {
    String expectedHeader = "  0|1\n";
    String body = "A  |  A\n" +
                 "B  |  B\n";
    emptyBoardHelper(2, 2, expectedHeader, body);
  }

  @Test
  public void test_display_with_ships() {
    Board<Character> b1 = new BattleShipBoard<Character>(4, 3, 'X');
    BoardTextView view = new BoardTextView(b1);
    
    Ship<Character> s1 = new RectangleShip<Character>(new Coordinate(0, 0), 's', '*');
    b1.tryAddShip(s1);
    String expectedHeader = "  0|1|2|3\n";
    String expectedBody = 
      "A s| | |  A\n" +
      "B  | | |  B\n" +
      "C  | | |  C\n";
    assertEquals(expectedHeader + expectedBody + expectedHeader, view.displayMyOwnBoard());

    Ship<Character> s2 = new RectangleShip<Character>(new Coordinate(1, 2), 's', '*');
    b1.tryAddShip(s2);
    expectedBody = 
      "A s| | |  A\n" +
      "B  | |s|  B\n" +
      "C  | | |  C\n";
    assertEquals(expectedHeader + expectedBody + expectedHeader, view.displayMyOwnBoard());
  }


} 
