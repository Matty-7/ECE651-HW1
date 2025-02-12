package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

public class AppTest {
  private TextPlayer createTextPlayer(String name, String inputData, ByteArrayOutputStream bytes) {
    BufferedReader input = new BufferedReader(new StringReader(inputData));
    PrintStream output = new PrintStream(bytes, true);
    Board<Character> board = new BattleShipBoard<Character>(10, 20, 'X');
    V1ShipFactory shipFactory = new V1ShipFactory();
    return new TextPlayer(name, board, input, output, shipFactory);
  }

  @Test
  void test_app_init() {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    TextPlayer p1 = createTextPlayer("A", "B2V\n", bytes);
    TextPlayer p2 = createTextPlayer("B", "C3H\n", bytes);
    App app = new App(p1, p2);
    assertNotNull(app);
  }

  @Test
  void test_do_placement_phase() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    String inputData = "A0V\nB1V\nC2V\nD3V\nE4V\nF5V\nG6V\nH7V\nI8V\nJ9V\n";
    TextPlayer p1 = createTextPlayer("A", inputData, bytes);
    TextPlayer p2 = createTextPlayer("B", inputData, bytes);
    App app = new App(p1, p2);
    app.doPlacementPhase();
    String output = bytes.toString();
    assertTrue(output.contains("Player A: you are going to place"));
    assertTrue(output.contains("Player B: you are going to place"));
  }

  @Test
  void test_main() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(bytes));

    String inputDataA = "A0V\nB1\nC1\nD1\n";
    String inputDataB = "B1V\nA0\nA1\n";
    
    TextPlayer p1 = createTextPlayer("A", inputDataA, bytes);
    TextPlayer p2 = createTextPlayer("B", inputDataB, bytes);
    App app = new App(p1, p2);
    
    app.doPlacementPhase();
    app.doAttackingPhase();
    
    System.setOut(originalOut);

    String output = bytes.toString();
    assertTrue(output.contains("Player A: you are going to place"));
    assertTrue(output.contains("Player B: you are going to place"));
    assertTrue(output.contains("You hit a"));
    assertTrue(output.contains("Player A won!"));
  }
}
