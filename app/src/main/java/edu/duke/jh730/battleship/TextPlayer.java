package edu.duke.jh730.battleship;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;

public class TextPlayer {
  private final String name;
  private final Board<Character> theBoard;
  private final BufferedReader input;
  private final PrintStream out;
  private final AbstractShipFactory<Character> shipFactory;
  private final BoardTextView view;

  /**
   * Constructs a TextPlayer with the specified parameters.
   *
   * @param name        the name of the player (e.g., "A" or "B")
   * @param theBoard    the Board this player plays on
   * @param input       the BufferedReader to read user input from
   * @param out         the PrintStream to write messages to
   * @param shipFactory the AbstractShipFactory to create ships
   */
  public TextPlayer(String name, Board<Character> theBoard, BufferedReader input, PrintStream out,
      AbstractShipFactory<Character> shipFactory) {
    this.name = name;
    this.theBoard = theBoard;
    this.input = input;
    this.out = out;
    this.shipFactory = shipFactory;
    this.view = new BoardTextView(this.theBoard);
  }

  /**
   * Reads a valid Placement from the user, printing a prompt beforehand.
   * Repeats until a valid Placement is obtained or if EOF is reached.
   *
   * @param prompt the message to display to the user
   * @return a valid Placement read from the user
   * @throws IOException if an I/O error occurs (including EOF)
   */
  public Placement readPlacement(String prompt) throws IOException {
    while (true) {
      out.println(prompt);
      String line = input.readLine();
      if (line == null) {
        throw new EOFException("No more input");
      }
      try {
        return new Placement(line);
      } catch (IllegalArgumentException e) {
        out.println("That placement is invalid: it does not have the correct format.");
      }
    }
  }

  /**
   * Asks the user for one Placement of a Destroyer, creates it, and places it
   * on the board. If the placement is invalid or collides with existing ships,
   * this function will print an error message and re-ask until a valid placement
   * is obtained.
   *
   * @throws IOException if an I/O error occurs (including EOF)
   */
  public void doOnePlacement() throws IOException {
    while (true) {
      Placement p = readPlacement("Player " + name + " where do you want to place a Destroyer?");
      Ship<Character> s;
      try {
        s = shipFactory.makeDestroyer(p);
      } catch (IllegalArgumentException e) {
        out.println("That placement is invalid: it does not have a valid orientation.");
        continue;
      }
      String addShipError = theBoard.tryAddShip(s);
      if (addShipError == null) {
        out.print(view.displayMyOwnBoard());
        break;
      } else {
        out.println("That placement is invalid: " + addShipError);
      }
    }
  }

  /**
   * Displays the board, prints a brief message, and then does exactly
   * one placement of a single Destroyer, for demonstration up to task18.
   *
   * @throws IOException if an I/O error occurs (including EOF)
   */
  public void doPlacementPhase() throws IOException {
    out.println("Player " + name + ": you are going to place a single Destroyer on your board.");
    out.println("Try placing it, and see if it is valid!");
    out.print(view.displayMyOwnBoard());
    doOnePlacement();
  }
}
