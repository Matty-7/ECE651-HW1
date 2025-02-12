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

  @Test
  public void test_display_enemy_board() {
    Board<Character> b1 = new BattleShipBoard<Character>(2, 2, 'X');
    BoardTextView view = new BoardTextView(b1);
    String expectedHeader = "  0|1\n";
    assertEquals(expectedHeader + "A  |  A\n" + "B  |  B\n" + expectedHeader,
                view.displayEnemyBoard());
    
    Ship<Character> s1 = new RectangleShip<Character>(new Coordinate(0, 0), 's', '*');
    b1.tryAddShip(s1);
    assertEquals(expectedHeader + "A  |  A\n" + "B  |  B\n" + expectedHeader,
                view.displayEnemyBoard());
    
    b1.fireAt(new Coordinate(0, 0));
    assertEquals(expectedHeader + "A s|  A\n" + "B  |  B\n" + expectedHeader,
                view.displayEnemyBoard());
    
    b1.fireAt(new Coordinate(1, 1));
    assertEquals(expectedHeader + "A s|  A\n" + "B  |X B\n" + expectedHeader,
                view.displayEnemyBoard());
  }

  @Test
  public void test_display_my_board_with_enemy() {
    Board<Character> b1 = new BattleShipBoard<Character>(2, 2, 'X');
    Board<Character> b2 = new BattleShipBoard<Character>(2, 2, 'X');
    BoardTextView view1 = new BoardTextView(b1);
    BoardTextView view2 = new BoardTextView(b2);
    
    // Test empty boards
    String expected = "     Your ocean        Player B's ocean\n" +
                     "  0|1                    0|1\n" +
                     "A  |  A                A  |  A\n" +
                     "B  |  B                B  |  B\n" +
                     "  0|1                    0|1\n";
    assertEquals(expected, view1.displayMyBoardWithEnemyNextToIt(view2, "Your ocean", "Player B's ocean"));

    // Test different width
    Board<Character> b3 = new BattleShipBoard<Character>(3, 2, 'X');
    BoardTextView view3 = new BoardTextView(b3);
    assertThrows(IllegalArgumentException.class, 
                 () -> view1.displayMyBoardWithEnemyNextToIt(view3, "Your ocean", "Player B's ocean"));

    // Test different height
    Board<Character> b4 = new BattleShipBoard<Character>(2, 3, 'X');
    BoardTextView view4 = new BoardTextView(b4);
    assertThrows(IllegalArgumentException.class, 
                 () -> view1.displayMyBoardWithEnemyNextToIt(view4, "Your ocean", "Player B's ocean"));

    // Add ships and test
    Ship<Character> s1 = new RectangleShip<Character>(new Coordinate(0, 0), 's', '*');
    Ship<Character> s2 = new RectangleShip<Character>(new Coordinate(1, 1), 'd', '*');
    b1.tryAddShip(s1);
    b2.tryAddShip(s2);
    
    expected = "     Your ocean        Player B's ocean\n" +
              "  0|1                    0|1\n" +
              "A s|  A                A  |  A\n" +
              "B  |  B                B  |  B\n" +
              "  0|1\n";
    assertEquals(expected, view1.displayMyBoardWithEnemyNextToIt(view2, "Your ocean", "Player B's ocean"));

    // Test hits and misses
    b2.fireAt(new Coordinate(0, 0));  // miss
    b2.fireAt(new Coordinate(1, 1));  // hit
    
    expected = "     Your ocean        Player B's ocean\n" +
              "  0|1                    0|1\n" +
              "A s|  A                A X|  A\n" +
              "B  |  B                B  |d B\n" +
              "  0|1\n";
    assertEquals(expected, view1.displayMyBoardWithEnemyNextToIt(view2, "Your ocean", "Player B's ocean"));
  }
} 
