package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class TextPlayerTest {
  private TextPlayer createTextPlayer(int w, int h, String inputData, ByteArrayOutputStream bytes) {
    BufferedReader input = new BufferedReader(new StringReader(inputData));
    PrintStream output = new PrintStream(bytes, true);
    Board<Character> board = new BattleShipBoard<Character>(w, h);
    V1ShipFactory shipFactory = new V1ShipFactory();
    return new TextPlayer("A", board, input, output, shipFactory);
  }

  @Test
  public void test_read_placement() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer player = createTextPlayer(10, 20, "B2V\n", bytes);
    String prompt = "Please enter a location for a ship:";
    Placement p = player.readPlacement(prompt);
    assertEquals(new Placement(new Coordinate(1, 2), 'V'), p);
    assertEquals(prompt + "\n", bytes.toString());
  }

  @Test
  public void test_do_one_placement() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    String inputData = "A0V\nB1V\nC2V\nD3V\nE4V\nF5V\nG6V\nH7V\nI8V\nJ9V\n";
    TextPlayer player = createTextPlayer(10, 20, inputData, bytes);
    player.doOnePlacement();
    String expected = "Player A where do you want to place a Submarine?";
    assertTrue(bytes.toString().contains(expected));
  }

  @Test
  public void test_do_placement_phase() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    String inputData = "A0V\nB1V\nC2V\nD3V\nE4V\nF5V\nG6V\nH7V\nI8V\nJ9V\n";
    TextPlayer player = createTextPlayer(10, 20, inputData, bytes);
    player.doPlacementPhase();
    String expected = "  0|1|2|3|4|5|6|7|8|9\n";
    assertTrue(bytes.toString().startsWith(expected));
    assertTrue(bytes.toString().contains("Player A: you are going to place"));
  }

  @Test
  public void test_is_placement_done() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    String inputData = "A0V\nB1V\nC2V\nD3V\nE4V\nF5V\nG6V\nH7V\nI8V\nJ9V\n";
    TextPlayer player = createTextPlayer(10, 20, inputData, bytes);
    assertFalse(player.isPlacementDone());
    
    // Place all ships
    while (!player.isPlacementDone()) {
      player.doOnePlacement();
    }
    assertTrue(player.isPlacementDone());
  }
} 
