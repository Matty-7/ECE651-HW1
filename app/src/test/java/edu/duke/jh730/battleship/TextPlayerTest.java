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
      Board<Character> board) {
    BufferedReader input = new BufferedReader(new StringReader(inputData));
    PrintStream output = new PrintStream(bytes, true);
    AbstractShipFactory<Character> factory = new V1ShipFactory();
    return new TextPlayer(playerName, board, input, output, factory);
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
  public void test_doPlacementPhase() throws IOException {
    // 2 Submarines:
    // "A0V" places a submarine at A0 (covering A0 and B0)
    // "A2V" places a submarine at A2 (covering A2 and B2)
    //
    // 3 Destroyers (each is 1x3 when placed horizontally):
    // "C0H" places a destroyer at C0 (covering C0, C1, C2)
    // "C3H" places a destroyer at C3 (covering C3, C4, C5)
    // "C6H" places a destroyer at C6 (covering C6, C7, C8)
    //
    // 3 Battleships (each is 1x4 when placed vertically):
    // "D0V" places a battleship at D0 (covering D0, E0, F0, G0)
    // "D2V" places a battleship at D2 (covering D2, E2, F2, G2)
    // "D4V" places a battleship at D4 (covering D4, E4, F4, G4)
    //
    // 2 Carriers (each is 1x6 when placed horizontally):
    // "H0H" places a carrier at H0 (covering H0, H1, H2, H3, H4, H5)
    // "I0H" places a carrier at I0 (covering I0, I1, I2, I3, I4, I5)
    String inputData = "A0V\n" +   // Submarine #1
                       "A2V\n" +   // Submarine #2
                       "C0H\n" +   // Destroyer #1
                       "C3H\n" +   // Destroyer #2
                       "C6H\n" +   // Destroyer #3
                       "D0V\n" +   // Battleship #1
                       "D2V\n" +   // Battleship #2
                       "D4V\n" +   // Battleship #3
                       "H0H\n" +   // Carrier #1
                       "I0H\n";    // Carrier #2
    
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<Character>(10, 20, 'X');
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doPlacementPhase();
    String outString = bytes.toString();

    // Check that we prompted for each type of ship.
    assertTrue(outString.contains("Player A where do you want to place a Submarine?"));
    assertTrue(outString.contains("Player A where do you want to place a Destroyer?"));
    assertTrue(outString.contains("Player A where do you want to place a Battleship?"));
    assertTrue(outString.contains("Player A where do you want to place a Carrier?"));

    // Check that each input coordinate appears in the output (these are echoed)
    assertTrue(outString.contains("A0V"));
    assertTrue(outString.contains("A2V"));
    assertTrue(outString.contains("C0H"));
    assertTrue(outString.contains("C3H"));
    assertTrue(outString.contains("C6H"));
    assertTrue(outString.contains("D0V"));
    assertTrue(outString.contains("D2V"));
    assertTrue(outString.contains("D4V"));
    assertTrue(outString.contains("H0H"));
    assertTrue(outString.contains("I0H"));
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
  public void test_playOneTurn() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b1 = new BattleShipBoard<Character>(3, 3, 'X');
    Board<Character> b2 = new BattleShipBoard<Character>(3, 3, 'X');
    Ship<Character> s = new RectangleShip<Character>(new Coordinate(1, 1), 's', '*');
    b2.tryAddShip(s);
    
    // Test hit
    TextPlayer player = createTextPlayer("A", "B1\n", bytes, b1);
    BoardTextView enemyView = new BoardTextView(b2);
    player.playOneTurn(b2, enemyView);
    String output = bytes.toString();
    assertTrue(output.contains("You hit a "));
    
    // Test miss
    bytes = new ByteArrayOutputStream();
    player = createTextPlayer("A", "A0\n", bytes, b1);
    player.playOneTurn(b2, enemyView);
    output = bytes.toString();
    assertTrue(output.contains("You missed!"));
  }
}
