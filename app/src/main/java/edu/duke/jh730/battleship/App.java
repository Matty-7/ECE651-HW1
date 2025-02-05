package edu.duke.jh730.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

public class App {
  private final Board<Character> theBoard;
  private final BoardTextView view;
  private final BufferedReader inputReader;
  private final PrintStream out;

  /**
   * Creates an App with the specified board, input source and output stream for the program
   */
  public App(Board<Character> theBoard, Reader inputSource, PrintStream out) {
    this.theBoard = theBoard;
    this.view = new BoardTextView(theBoard);
    this.inputReader = new BufferedReader(inputSource);
    this.out = out;
  }

  /**
   * Reads a Placement from the user with the given prompt and returns it
   */
  public Placement readPlacement(String prompt) throws IOException {
    out.println(prompt);
    String s = inputReader.readLine();
    return new Placement(s);
  }

  /**
   * Does one placement of a ship on the board
   */
  public void doOnePlacement() throws IOException {
    Placement p = readPlacement("Where would you like to put your ship?");
    Ship<Character> s = new RectangleShip<Character>(p.getWhere(), 's', '*');
    theBoard.tryAddShip(s);
    out.print(view.displayMyOwnBoard());
  }

  /**
   * The main entry point for the program
   */
  public static void main(String[] args) throws IOException {
    Board<Character> b = new BattleShipBoard<Character>(10, 20);
    App app = new App(b, new InputStreamReader(System.in), System.out);
    app.doOnePlacement();
  }
} 
