package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

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
    Board<Character> b = new BattleShipBoard<>(5, 5);
    TextPlayer player = createTextPlayer("A", "A0V\n", bytes, b);

    Placement p = player.readPlacement("Enter a placement:");
    assertEquals(new Placement(new Coordinate(0, 0), 'V'), p);
    String prompt = bytes.toString();
    assertTrue(prompt.contains("Enter a placement:"));
  }

  @Test
  public void test_readPlacement_invalid_format_and_EOF() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5);

    // Provide an invalid placement, then a valid one, then simulate EOF
    String inputData = "QQQ\nA3H\n";
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    // First read: invalid format
    Placement p1 = player.readPlacement("Prompt1");
    String output1 = bytes.toString();
    assertTrue(output1.contains("Prompt1"));
    assertTrue(output1.contains("That placement is invalid: it does not have the correct format."));
    bytes.reset();

    // Second read: valid
    Placement p2 = player.readPlacement("Prompt2");
    assertEquals(new Placement(new Coordinate(3, 0), 'H'), p2);
    String output2 = bytes.toString();
    assertTrue(output2.contains("Prompt2"));
    bytes.reset();

    // Third read: should hit EOF
    assertThrows(EOFException.class, () -> player.readPlacement("Prompt3"));
    String output3 = bytes.toString();
    assertTrue(output3.contains("Prompt3"));
  }

  @Test
  public void test_doOnePlacement_valid_and_conflict() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5);
    TextPlayer player = createTextPlayer("A", "A0V\nA0V\nB0V\n", bytes, b);

    // 1) valid on empty board
    player.doOnePlacement();  
    String output1 = bytes.toString();
    assertTrue(output1.contains("where do you want to place a Destroyer?"));
    assertTrue(output1.contains("  0|1|2|3|4"));
    bytes.reset();

    // 2) conflict with existing ship --> must re-ask
    //    next input is B0V, which should succeed
    player.doOnePlacement();
    String output2 = bytes.toString();
    assertTrue(output2.contains("That placement is invalid: the ship overlaps another ship."));
    assertTrue(output2.contains("where do you want to place a Destroyer?"));
    bytes.reset();
  }

  @Test
  public void test_doOnePlacement_invalid_orientation() throws IOException {
    // Provide invalid orientation first, valid orientation next
    String inputData = "A0Q\nC2H\n";
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5);
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doOnePlacement();
    String output = bytes.toString();
    // Should have complained about orientation
    assertTrue(output.contains("That placement is invalid: it does not have a valid orientation."));
    assertTrue(output.contains("where do you want to place a Destroyer?"));
  }

  @Test
  public void test_doPlacementPhase() throws IOException {
    String inputData = "A0V\nB1H\n";
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    Board<Character> b = new BattleShipBoard<>(5, 5);
    TextPlayer player = createTextPlayer("A", inputData, bytes, b);

    player.doPlacementPhase();
    String outString = bytes.toString();
    assertTrue(outString.contains("Player A: you are going to place a single Destroyer on your board."));
    assertTrue(outString.contains("Try placing it, and see if it is valid!"));
    assertTrue(outString.contains("A0V"));
    assertTrue(outString.contains("where do you want to place a Destroyer?"));
  }
}
