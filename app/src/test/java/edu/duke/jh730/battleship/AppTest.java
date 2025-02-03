package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

public class AppTest {
  @Test
  void test_read_placement() throws IOException {
    StringReader sr = new StringReader("B2V\nC8H\na4v\n");
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bytes, true);
    Board<Character> b = new BattleShipBoard<Character>(10, 20);
    App app = new App(b, sr, ps);

    String prompt = "Please enter a location for a ship:";
    Placement[] expected = new Placement[3];
    expected[0] = new Placement(new Coordinate(1, 2), 'V');
    expected[1] = new Placement(new Coordinate(2, 8), 'H');
    expected[2] = new Placement(new Coordinate(0, 4), 'V');

    for (int i = 0; i < expected.length; i++) {
      Placement p = app.readPlacement(prompt);
      assertEquals(expected[i], p);
      assertEquals(prompt + "\n", bytes.toString());
      bytes.reset();
    }
  }

  @Test
  void test_do_one_placement() throws IOException {
    StringReader sr = new StringReader("B2V\n");
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(bytes, true);
    Board<Character> b = new BattleShipBoard<Character>(10, 20);
    App app = new App(b, sr, ps);

    app.doOnePlacement();
    String expected = 
      "Where would you like to put your ship?\n" +
      "  0|1|2|3|4|5|6|7|8|9\n" +
      "A  | | | | | | | | |  A\n" +
      "B  | |s| | | | | | |  B\n" +
      "C  | | | | | | | | |  C\n" +
      "D  | | | | | | | | |  D\n" +
      "E  | | | | | | | | |  E\n" +
      "F  | | | | | | | | |  F\n" +
      "G  | | | | | | | | |  G\n" +
      "H  | | | | | | | | |  H\n" +
      "I  | | | | | | | | |  I\n" +
      "J  | | | | | | | | |  J\n" +
      "K  | | | | | | | | |  K\n" +
      "L  | | | | | | | | |  L\n" +
      "M  | | | | | | | | |  M\n" +
      "N  | | | | | | | | |  N\n" +
      "O  | | | | | | | | |  O\n" +
      "P  | | | | | | | | |  P\n" +
      "Q  | | | | | | | | |  Q\n" +
      "R  | | | | | | | | |  R\n" +
      "S  | | | | | | | | |  S\n" +
      "T  | | | | | | | | |  T\n" +
      "  0|1|2|3|4|5|6|7|8|9\n";
    assertEquals(expected, bytes.toString());
  }

  @Test
  @ResourceLock(value = Resources.SYSTEM_OUT, mode = ResourceAccessMode.READ_WRITE)
  void test_main() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes, true);
    
    String inputString = "B2V\n";
    InputStream input = new ByteArrayInputStream(inputString.getBytes());
    assertNotNull(input);
    
    InputStream oldIn = System.in;
    PrintStream oldOut = System.out;
    
    try {
        System.setIn(input);
        System.setOut(out);
        App.main(new String[0]);
    }
    finally {
        System.setIn(oldIn);
        System.setOut(oldOut);
    }
    
    String expected = 
      "Where would you like to put your ship?\n" +
      "  0|1|2|3|4|5|6|7|8|9\n" +
      "A  | | | | | | | | |  A\n" +
      "B  | |s| | | | | | |  B\n" +
      "C  | | | | | | | | |  C\n" +
      "D  | | | | | | | | |  D\n" +
      "E  | | | | | | | | |  E\n" +
      "F  | | | | | | | | |  F\n" +
      "G  | | | | | | | | |  G\n" +
      "H  | | | | | | | | |  H\n" +
      "I  | | | | | | | | |  I\n" +
      "J  | | | | | | | | |  J\n" +
      "K  | | | | | | | | |  K\n" +
      "L  | | | | | | | | |  L\n" +
      "M  | | | | | | | | |  M\n" +
      "N  | | | | | | | | |  N\n" +
      "O  | | | | | | | | |  O\n" +
      "P  | | | | | | | | |  P\n" +
      "Q  | | | | | | | | |  Q\n" +
      "R  | | | | | | | | |  R\n" +
      "S  | | | | | | | | |  S\n" +
      "T  | | | | | | | | |  T\n" +
      "  0|1|2|3|4|5|6|7|8|9\n";
    assertEquals(expected, bytes.toString());
  }
}
