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
