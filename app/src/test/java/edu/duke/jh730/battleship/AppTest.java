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
    Board<Character> board = new BattleShipBoard<Character>(10, 20);
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
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = ResourceAccessMode.READ_WRITE)
  void test_main() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    String input = "A0V\nB1V\nC2V\nD3V\nE4V\nF5V\nG6V\nH7V\nI8V\nJ9V\n" +
                  "A0V\nB1V\nC2V\nD3V\nE4V\nF5V\nG6V\nH7V\nI8V\nJ9V\n";
    
    InputStream oldIn = System.in;
    PrintStream oldOut = System.out;
    
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes());
      System.setIn(bais);
      System.setOut(new PrintStream(bytes, true));
      
      App.main(new String[0]);
      
      String output = bytes.toString();
      assertTrue(output.contains("Player A: you are going to place"));
      assertTrue(output.contains("Player B: you are going to place"));
    } finally {
      System.setIn(oldIn);
      System.setOut(oldOut);
    }
  }
}
