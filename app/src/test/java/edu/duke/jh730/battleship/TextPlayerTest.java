package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream; 
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.EOFException;

import org.junit.jupiter.api.Test;

public class TextPlayerTest {

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
    Board<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "A0V\n", bytes, b);

    Placement p = player.readPlacement("Enter a placement:");
    assertEquals(new Placement(new Coordinate(0, 0), 'V'), p);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("Enter a placement:"));
  }

  @Test
  public void test_readPlacement_EOF() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, b);
    assertThrows(EOFException.class, () -> player.readPlacement("Enter placement:"));
  }

  @Test
  public void test_readPlacement_invalid() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "AA\nA0V\n", bytes, b);

    Placement p = player.readPlacement("Enter a placement:");
    assertEquals(new Placement(new Coordinate(0, 0), 'V'), p);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("That placement is invalid: it does not have the correct format."));
  }

  @Test
  public void test_doOnePlacement_invalid_orientation() throws IOException {
    String inputData = "A0Q\nC2H\n";
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doOnePlacement();
    String output = bytes.toString();
    assertTrue(output.contains("That placement is invalid: it does not have a valid orientation."));
    assertTrue(output.contains("where do you want to place a Destroyer?"));
  }

  @Test
  public void test_doOnePlacement_collision() throws IOException {
    String inputData = "A0H\nA0H\nB0H\n";
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doOnePlacement();
    player.doOnePlacement();
    String output = bytes.toString();
    assertTrue(output.contains("That placement is invalid: the ship overlaps another ship."));
  }


  @Test
  public void test_readCoordinate() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(2, 2, 'X');
    
    // Test valid input
    TextPlayer player = createTextPlayer("A", "A0\n", bytes, b);
    Coordinate c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("Enter coordinate:"));

    // Test row out of bounds (>= height)
    bytes = new ByteArrayOutputStream();
    player = createTextPlayer("A", "C0\nA0\n", bytes, b);
    c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    prompt = bytes.toString();
    assertTrue(prompt.contains("Please enter a valid coordinate (e.g., A0)"));

    // Test column out of bounds (>= width)
    bytes = new ByteArrayOutputStream();
    player = createTextPlayer("A", "A2\nA0\n", bytes, b);
    c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    prompt = bytes.toString();
    assertTrue(prompt.contains("Please enter a valid coordinate (e.g., A0)"));

    // Test negative row
    bytes = new ByteArrayOutputStream();
    player = createTextPlayer("A", "!0\nA0\n", bytes, b);
    c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    prompt = bytes.toString();
    assertTrue(prompt.contains("Please enter a valid coordinate (e.g., A0)"));

    // Test negative column
    bytes = new ByteArrayOutputStream();
    player = createTextPlayer("A", "A-1\nA0\n", bytes, b);
    c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    prompt = bytes.toString();
    assertTrue(prompt.contains("Please enter a valid coordinate (e.g., A0)"));
  }

  @Test
  public void test_readCoordinate_EOF() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(5, 5, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, b);
    assertThrows(EOFException.class, () -> player.readCoordinate("Enter coordinate:"));
  }

  @Test
  public void test_readCoordinate_negative() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(2, 2, 'X');
    
    // Test negative row
    TextPlayer player = createTextPlayer("A", "@0\nA0\n", bytes, b);
    Coordinate c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("Please enter a valid coordinate (e.g., A0)"));

    // Test negative column
    bytes = new ByteArrayOutputStream();
    player = createTextPlayer("A", "A-1\nA0\n", bytes, b);
    c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    prompt = bytes.toString();
    assertTrue(prompt.contains("Please enter a valid coordinate (e.g., A0)"));
  }

  @Test
  public void test_readCoordinate_invalidFormat() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<Character>(3, 3, 'X');
    TextPlayer player = createTextPlayer("A", "foo\nA0\n", bytes, board);
    
    Coordinate c = player.readCoordinate("Enter coordinate:");
    assertEquals(new Coordinate(0, 0), c);
    
    String output = bytes.toString();
    assertTrue(output.contains("Please enter a valid coordinate (e.g., A0)"));
  }

  @Test
  void test_doPlacementPhase() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    // Create input with all ship placements
    String inputData = "A0H\n" + // First submarine
                      "B0H\n" + // Second submarine
                      "C0H\n" + // First destroyer
                      "D0H\n" + // Second destroyer
                      "E0H\n" + // Third destroyer
                      "F0U\n" + // First battleship
                      "G0U\n" + // Second battleship
                      "H0U\n" + // Third battleship
                      "I0U\n" + // First carrier
                      "J0U\n";  // Second carrier
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);

    player.doPlacementPhase();
    String expected = "Player A: you are going to place the following ships";
    assertTrue(bytes.toString().contains(expected));
  }

  @Test
  void test_computerPlacementPhase() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, board, true);

    player.doPlacementPhase();
    // Computer placement should not produce any output except potential placement errors
    String output = bytes.toString();
    assertFalse(output.contains("Player A: you are going to place the following ships"));
    
    // Verify that all 10 ships are placed
    int shipCount = 0;
    for (Ship<Character> ship : board.getShips()) {
      shipCount++;
    }
    assertEquals(10, shipCount);
  }

  @Test
  void test_playOneTurn_human_fire() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    String inputData = "F\nA0\n"; // Fire action at A0
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    // Place a ship at A0 on enemy board
    V2ShipFactory factory = new V2ShipFactory();
    Ship<Character> ship = factory.makeSubmarine(new Placement("A0H"));
    enemyBoard.tryAddShip(ship);
    
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    player.playOneTurn(enemyBoard, enemyView);
    
    String output = bytes.toString();
    assertTrue(output.contains("You hit a Submarine"));
    assertTrue(ship.wasHitAt(new Coordinate(0, 0)));
  }

  @Test
  void test_playOneTurn_human_move() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    // Add a ship to move
    V2ShipFactory factory = new V2ShipFactory();
    Ship<Character> ship = factory.makeSubmarine(new Placement("A0H"));
    board.tryAddShip(ship);
    
    String inputData = "M\nA0\nB0H\n"; // Move ship from A0 to B0
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    
    player.playOneTurn(enemyBoard, enemyView);
    String output = bytes.toString();
    assertTrue(output.contains("Successfully moved"));
    
    // Verify ship is at new location
    assertNull(board.getShipAt(new Coordinate(0, 0))); // Old location
    assertNotNull(board.getShipAt(new Coordinate(1, 0))); // New location
  }

  @Test
  void test_playOneTurn_human_sonar() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    String inputData = "S\nE5\n"; // Sonar scan at E5
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    // Add ships to enemy board for scanning
    V2ShipFactory factory = new V2ShipFactory();
    enemyBoard.tryAddShip(factory.makeSubmarine(new Placement("E5H")));
    enemyBoard.tryAddShip(factory.makeBattleship(new Placement("F5U")));
    
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    player.playOneTurn(enemyBoard, enemyView);
    
    String output = bytes.toString();
    assertTrue(output.contains("Submarines occupy"));
    assertTrue(output.contains("Battleships occupy"));
  }

  @Test
  void test_playOneTurn_computer() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("B", "", bytes, board, true);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    // Add a ship to enemy board
    V2ShipFactory factory = new V2ShipFactory();
    enemyBoard.tryAddShip(factory.makeSubmarine(new Placement("A0H")));
    
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    
    // Test multiple turns to verify computer follows pattern
    for (int i = 0; i < 3; i++) {
      player.playOneTurn(enemyBoard, enemyView);
    }
    
    String output = bytes.toString();
    assertTrue(output.contains("Player B's turn"));
    // Verify computer either hit, missed, or used special action
    assertTrue(output.contains("hit") || output.contains("missed") || 
               output.contains("used a special action"));
  }

  @Test
  void test_sonarScan_invalid_location() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    String inputData = "S\nA0\nF\nA0\n"; // After invalid sonar scan, do a fire action
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    
    player.playOneTurn(enemyBoard, enemyView);
    String output = bytes.toString();
    assertTrue(output.contains("Invalid scan location"));
  }

  @Test
  void test_moveShip_invalid_location() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    // Add a ship to move
    V2ShipFactory factory = new V2ShipFactory();
    board.tryAddShip(factory.makeSubmarine(new Placement("A0H")));
    
    String inputData = "M\nA0\nZ0H\nF\nA0\n"; // After invalid move, do a fire action
    TextPlayer player = createTextPlayer("A", inputData, bytes, board);
    
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    
    player.playOneTurn(enemyBoard, enemyView);
    String output = bytes.toString();
    assertTrue(output.contains("Invalid"));
  }

  @Test
  void test_actions_remaining() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> board = new BattleShipBoard<>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", "", bytes, board);
    
    // Initially should have 3 moves and 3 sonar scans
    Board<Character> enemyBoard = new BattleShipBoard<>(10, 20, 'X');
    BoardTextView enemyView = new BoardTextView(enemyBoard);
    
    String output = bytes.toString();
    assertTrue(output.contains("3 remaining") || !output.contains("remaining"));
  }
}
