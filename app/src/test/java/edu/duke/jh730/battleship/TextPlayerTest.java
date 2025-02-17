package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream; 
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.EOFException;

import org.junit.jupiter.api.Test;

/**
 * A set of JUnit tests for TextPlayer class,
 * striving to cover readCoordinate, readPlacement,
 * doPlacement, playOneTurn logic, etc.
 */
public class TextPlayerTest {

  /**
   * Helper method to create a TextPlayer with a given input string,
   * capturing output in a ByteArrayOutputStream for assertions,
   * using a specified Board and optionally isComputer = true/false.
   */
  private TextPlayer createTextPlayer(String playerName, String inputData, ByteArrayOutputStream bytes,
      Board<Character> board, boolean isComputer) {
    BufferedReader input = new BufferedReader(new StringReader(inputData));
    PrintStream output = new PrintStream(bytes, true);
    AbstractShipFactory<Character> factory = new V2ShipFactory();
    return new TextPlayer(playerName, board, input, output, factory, isComputer);
  }

  private TextPlayer createTextPlayer(String playerName, String inputData, ByteArrayOutputStream bytes,
      Board<Character> board) {
    return createTextPlayer(playerName, inputData, bytes, board, false);
  }

  @Test
  public void test_readPlacement_valid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "A0V\n", bytes, b);

    Placement p = player.readPlacement("Enter a placement:");
    assertEquals(new Placement(new Coordinate(0, 0), 'V'), p);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("Enter a placement:"));
    assertTrue(prompt.contains("A0V"));
  }

  @Test
  public void test_readPlacement_EOF() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, b);

    assertThrows(EOFException.class, () -> player.readPlacement("Enter placement:"));
  }

  @Test
  public void test_readPlacement_invalid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "AA\nA0V\n", bytes, b);

    Placement p = player.readPlacement("Enter a placement:");
    assertEquals(new Placement(new Coordinate(0, 0), 'V'), p);
    String outContent = bytes.toString();
    assertTrue(outContent.contains("That placement is invalid: it does not have the correct format."));
    assertTrue(outContent.contains("A0V"));
  }

  @Test
  public void test_doOnePlacement_invalidOrientation() throws IOException {
    String inputData = "A0Q\nC2H\n";
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doOnePlacement("Destroyer");
    String output = bytes.toString();
    assertTrue(output.contains("That placement is invalid: it does not have a valid orientation."));
    Ship<Character> found = b.getShipAt(new Coordinate(2, 2));
    assertNotNull(found);
    assertEquals("Destroyer", found.getName());
  }

  @Test
  public void test_doOnePlacement_collision() throws IOException {
    String inputData = "A0H\nA0H\nB0H\n";
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doOnePlacement("Destroyer");
    player.doOnePlacement("Destroyer");
    String output = bytes.toString();
    assertTrue(output.contains("That placement is invalid: the ship overlaps another ship."));
    
    // Verify both ships are placed correctly
    Ship<Character> ship1 = b.getShipAt(new Coordinate(0, 0));
    Ship<Character> ship2 = b.getShipAt(new Coordinate(1, 0));
    assertNotNull(ship1);
    assertNotNull(ship2);
  }

  @Test
  public void test_readCoordinate_valid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(2, 2, 'X');
    TextPlayer player = createTextPlayer("A", "A0\n", bytes, b);

    Coordinate c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("Enter coordinate:"));
  }

  @Test
  public void test_readCoordinate_EOF() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(2, 2, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, b);

    assertThrows(EOFException.class, () -> player.readCoordinate("Enter coordinate:"));
  }

  @Test
  public void test_readCoordinate_invalid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(2, 2, 'X');
    TextPlayer player = createTextPlayer("A", "Z0\nA2\nA-1\n!0\nA0\n", bytes, b);

    Coordinate c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    String outContent = bytes.toString();
    assertTrue(outContent.contains("Please enter a valid coordinate (e.g., A0)"));
  }

  @Test
  void test_doPlacementPhase_human() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    String inputData = 
      "A0H\n" + // Submarine1
      "B0H\n" + // Submarine2
      "C0H\n" + // Destroyer1
      "D0H\n" + // Destroyer2
      "E0H\n" + // Destroyer3
      "F0U\n" + // Battleship1
      "G0U\n" + // Battleship2
      "H0U\n" + // Battleship3
      "I0U\n" + // Carrier1
      "J0U\n";  // Carrier2
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);

    player.doPlacementPhase(); 
    String output = bytes.toString();
    assertTrue(output.contains("Player A: you are going to place the following ships"));
    assertTrue(output.contains("2 \"Submarines\" ships that are 1x2"));
    assertTrue(output.contains("3 \"Destroyers\" that are 1x3"));
    assertTrue(output.contains("3 \"Battleships\" that are special shapes"));
    assertTrue(output.contains("2 \"Carriers\" that are special shapes"));

    // Verify all 10 ships were placed
    int count = 0;
    for (Ship<Character> s : board.getShips()) {
      count++;
    }
    assertEquals(10, count);
  }

  @Test
  void test_doPlacementPhase_computer() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, board, true);

    player.doPlacementPhase();
    int count = 0;
    for (Ship<Character> s: board.getShips()) {
      count++;
    }
    assertEquals(10, count);
    String output = bytes.toString();
    assertFalse(output.contains("you are going to place the following ships"));
  }

  @Test
  void test_playOneTurn_human_fire() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", "F\nA0\n", bytes, myBoard);

    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    V2ShipFactory factory = new V2ShipFactory();
    Ship<Character> s = factory.makeSubmarine(new Placement("A0H"));
    enemyBoard.tryAddShip(s);

    BoardTextView enemyView = new BoardTextView(enemyBoard);
    player.playOneTurn(enemyBoard, enemyView);
    
    String output = bytes.toString();
    assertTrue(output.contains("You hit a Submarine!"));
  }

  @Test
  void test_playOneTurn_human_fire_miss() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", "F\nA0\n", bytes, myBoard);

    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    V2ShipFactory factory = new V2ShipFactory();
    Ship<Character> s = factory.makeSubmarine(new Placement("B0H"));
    enemyBoard.tryAddShip(s);

    BoardTextView enemyView = new BoardTextView(enemyBoard);
    player.playOneTurn(enemyBoard, enemyView);
    
    String output = bytes.toString();
    assertTrue(output.contains("You missed!"));
  }

  @Test
  void test_playOneTurn_human_move() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 20, 'X');
    V2ShipFactory factory = new V2ShipFactory();
    Ship<Character> sub = factory.makeSubmarine(new Placement("A0H"));
    myBoard.tryAddShip(sub);

    String inputData = "M\nA0\nB0V\n";
    TextPlayer player = createTextPlayer("A", inputData, bytes, myBoard);

    BoardTextView enemyView = new BoardTextView(myBoard);
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    player.playOneTurn(enemyBoard, enemyView);

    String output = bytes.toString();
    assertTrue(output.contains("Successfully moved Submarine"));
    Ship<Character> found = myBoard.getShipAt(new Coordinate(1, 0));
    assertNotNull(found);
  }

  @Test
  void test_playOneTurn_human_move_invalid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 20, 'X');
    V2ShipFactory factory = new V2ShipFactory();
    Ship<Character> sub = factory.makeSubmarine(new Placement("A0H"));
    myBoard.tryAddShip(sub);

    // Try to move to an invalid location, then try fire action instead
    String inputData = "M\nA0\nZ0V\nF\nA0\n";
    TextPlayer player = createTextPlayer("A", inputData, bytes, myBoard);

    BoardTextView enemyView = new BoardTextView(myBoard);
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    player.playOneTurn(enemyBoard, enemyView);

    String output = bytes.toString();
    assertTrue(output.contains("Invalid move:"));
    // After invalid move, should switch to fire action
    assertTrue(output.contains("Where would you like to fire at?"));
  }

  @Test
  void test_playOneTurn_human_sonar() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", "S\nE5\n", bytes, myBoard);

    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    V2ShipFactory factory = new V2ShipFactory();
    enemyBoard.tryAddShip(factory.makeSubmarine(new Placement("E5H")));
    enemyBoard.tryAddShip(factory.makeBattleship(new Placement("F5U")));
    
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    player.playOneTurn(enemyBoard, enemyView);
    
    String output = bytes.toString();
    assertTrue(output.contains("Sonar scan report at E5:"));
    assertTrue(output.contains("Submarines occupy"));
    assertTrue(output.contains("Battleships occupy"));
  }

  @Test
  void test_playOneTurn_human_sonar_invalid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 20, 'X');
    // Try to scan at A0 (too close to edge), then try fire action
    TextPlayer player = createTextPlayer("A", "S\nA0\nF\nA0\n", bytes, myBoard);

    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);

    player.playOneTurn(enemyBoard, enemyView);
    String output = bytes.toString();
    assertTrue(output.contains("Invalid scan location!"));
    assertTrue(output.contains("Where would you like to fire at?"));
  }

  @Test
  void test_playOneTurn_human_invalidAction() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "Z\nF\nA0\n", bytes, myBoard);

    Board<Character> enemyBoard = new BattleShipBoard<>(5, 5, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);

    player.playOneTurn(enemyBoard, enemyView);
    String outContent = bytes.toString();
    assertTrue(outContent.contains("Invalid action! Please enter F, M, or S."));
    assertTrue(outContent.contains("Where would you like to fire at?"));
  }

  @Test
  void test_playOneTurn_computer() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> myBoard = new BattleShipBoard<>(10, 10, 'X');
    TextPlayer player = createTextPlayer("B", "", bytes, myBoard, true);

    Board<Character> enemyBoard = new BattleShipBoard<>(10, 10, 'X');
    V2ShipFactory factory = new V2ShipFactory();
    enemyBoard.tryAddShip(factory.makeSubmarine(new Placement("A0H")));
    BoardTextView enemyView = new BoardTextView(enemyBoard);

    // Test multiple turns to ensure computer follows pattern
    for (int i = 0; i < 3; i++) {
      player.playOneTurn(enemyBoard, enemyView);
    }

    String outContent = bytes.toString();
    assertTrue(outContent.contains("Player B's turn"));
    assertTrue(outContent.contains("hit") || outContent.contains("missed") || 
               outContent.contains("used a special action"));
  }

  @Test
  void test_actions_remaining() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "F\nA0\n", bytes, b);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(5, 5, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);

    player.playOneTurn(enemyBoard, enemyView);
    String output = bytes.toString();
    assertTrue(output.contains("M Move a ship") && output.contains("(3 remaining)"));
    assertTrue(output.contains("S Sonar scan") && output.contains("(3 remaining)"));
  }
}
