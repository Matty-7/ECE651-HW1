package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.*;

import java.io.*;

/**
 * JUnit test for the App class,
 * attempting to maximize coverage of main, doPlacementPhase, doAttackingPhase, etc.
 */
public class AppTest {

  /**
   * Helper to capture System.out from main method while providing inputData.
   */
  private String runMainWithInput(String inputData) throws IOException {
    InputStream origIn = System.in;
    PrintStream origOut = System.out;
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream testOut = new PrintStream(bytes, true);

    try {
      System.setIn(new ByteArrayInputStream(inputData.getBytes()));
      System.setOut(testOut);
      App.main(new String[0]);
    } finally {
      System.setIn(origIn);
      System.setOut(origOut);
    }
    return bytes.toString();
  }

  /**
   * Basic test to confirm we can construct an App object and call
   * doPlacementPhase / doAttackingPhase directly (unit-level coverage).
   */
  @Test
  public void test_app_methods_directly() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes, true);
    Board<Character> b1 = new BattleShipBoard<Character>(2, 2, 'X');
    Board<Character> b2 = new BattleShipBoard<Character>(2, 2, 'X');

    // Add ships to ensure attacking phase has something to work with
    V2ShipFactory factory = new V2ShipFactory();
    b1.tryAddShip(factory.makeSubmarine(new Placement("A0H")));
    b2.tryAddShip(factory.makeSubmarine(new Placement("A0H")));

    // Create computer players to avoid input requirements
    TextPlayer p1 = new TextPlayer("A", b1, new BufferedReader(new StringReader("")), out, factory, true);
    TextPlayer p2 = new TextPlayer("B", b2, new BufferedReader(new StringReader("")), out, factory, true);

    App app = new App(p1, p2);
    assertNotNull(app);

    app.doPlacementPhase();
    app.doAttackingPhase();

    String output = bytes.toString();
    assertTrue(output.contains("Player A's turn") || output.contains("Player B's turn"));
  }

  @Test
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = READ_WRITE)
  public void test_main_computer_vs_computer() throws IOException {
    String inputData = 
      "c\n" +  // Player A => computer
      "c\n";   // Player B => computer

    String output = runMainWithInput(inputData);
    System.err.println("=== test_main_computer_vs_computer Output ===");
    System.err.println(output);

    assertTrue(output.contains("Player A wins!") || output.contains("Player B wins!"),
               "Should see either Player A wins! or Player B wins!");
    assertTrue(output.contains("Thanks for playing!"), 
               "Should see the farewell message.");
  }

  @Test
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = READ_WRITE)
  public void test_main_human_vs_computer() throws IOException {
    String inputData = 
      "h\n" +   // Player A => human
      "c\n" +   // Player B => computer
      // Minimal ship placements for Player A
      "A0H\n" + // Submarine 1
      "B0H\n" + // Submarine 2
      "C0H\n" + // Destroyer 1
      "D0H\n" + // Destroyer 2
      "E0H\n" + // Destroyer 3
      "F0U\n" + // Battleship 1
      "G0U\n" + // Battleship 2
      "H0U\n" + // Battleship 3
      "I0U\n" + // Carrier 1
      "J0U\n" + // Carrier 2
      // Game actions - fire at all ships
      "F\nA0\n" +  // Fire at A0
      "F\nB0\n" +  // Fire at B0
      "F\nC0\n" +  // Fire at C0
      "F\nD0\n" +  // Fire at D0
      "F\nE0\n" +  // Fire at E0
      "F\nF0\n" +  // Fire at F0
      "F\nG0\n" +  // Fire at G0
      "F\nH0\n" +  // Fire at H0
      "F\nI0\n" +  // Fire at I0
      "F\nJ0\n" +  // Fire at J0
      // Additional actions to ensure game completion
      "F\nA1\n" +  // Fire at A1
      "F\nB1\n" +  // Fire at B1
      "F\nC1\n" +  // Fire at C1
      "F\nD1\n" +  // Fire at D1
      "F\nE1\n";   // Fire at E1

    String output = runMainWithInput(inputData);
    System.err.println("=== test_main_human_vs_computer Output ===");
    System.err.println(output);

    assertTrue(output.contains("Welcome to Battleship!"));
    assertTrue(output.contains("Is Player A human or computer? (h/c)"));
    assertTrue(output.contains("Is Player B human or computer? (h/c)"));
    assertTrue(output.contains("Player A: you are going to place the following ships"));
    assertTrue(output.contains("Player A's turn"));
    assertTrue(output.contains("F Fire at a square"));
    assertTrue(output.contains("You hit") || output.contains("You missed"));
    assertTrue(output.contains("Thanks for playing!"));
  }

  @Test
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = READ_WRITE)
  public void test_main_invalidInput() throws IOException {
    String inputData = "x\nxyz\n";  // Invalid player type inputs
    String output = runMainWithInput(inputData);
    System.err.println("=== test_main_invalidInput Output ===");
    System.err.println(output);

    assertTrue(output.contains("Welcome to Battleship!"));
    assertTrue(output.contains("Is Player A human or computer? (h/c)"));
    assertTrue(output.contains("Is Player B human or computer? (h/c)"));
    assertTrue(output.contains("Error:") || output.contains("No more input"), 
               "Should see an error message due to incomplete input");
    assertTrue(output.contains("Thanks for playing!"));
  }

  @Test
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = READ_WRITE)
  public void test_main_EOF() throws IOException {
    String inputData = "h\nc\n";  // Provide both player types, then EOF for placement phase
    String output = runMainWithInput(inputData);
    System.err.println("=== test_main_EOF Output ===");
    System.err.println(output);

    assertTrue(output.contains("Welcome to Battleship!"));
    assertTrue(output.contains("Is Player A human or computer? (h/c)"));
    assertTrue(output.contains("Is Player B human or computer? (h/c)"));
    assertTrue(output.contains("Error:") || output.contains("No more input"));
    assertTrue(output.contains("Thanks for playing!"));
  }

  @Test
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = READ_WRITE)
  public void test_copyPlacementFiles() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream testOut = new PrintStream(bytes, true);
    PrintStream origOut = System.out;
    System.setOut(testOut);
    
    try {
      String inputData = "c\nc\n";  // Computer vs Computer to minimize input needed
      System.setIn(new ByteArrayInputStream(inputData.getBytes()));
      App.main(new String[0]);
    } finally {
      System.setOut(origOut);
    }

    String output = bytes.toString();
    assertTrue(output.contains("Welcome to Battleship!"));
    assertTrue(output.contains("Thanks for playing!"));
  }
}
