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
   * Echos the user's response, and repeats until a valid Placement is obtained
   * or EOF is reached.
   *
   * @param prompt the message to display to the user
   * @return a valid Placement read from the user
   * @throws IOException if an I/O error occurs (including EOF)
   */
  public Placement readPlacement(String prompt) throws IOException {
    while(true) {
      out.println(prompt);
      String line = input.readLine();
      if (line == null) {
        throw new EOFException("No more input");
      }
      // Echo the line so tests can see e.g. "A0V" in the output
      out.println(line);
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
   * is obtained or EOF occurs.
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

  public void doPlacementPhase() throws IOException {
    out.println("Player " + name + ": you are going to place a single Destroyer on your board.");
    out.println("Try placing it, and see if it is valid!");
    out.print(view.displayMyOwnBoard());
    doOnePlacement();
  }

  /**
   * Play one turn for this player
   * @param enemyBoard the enemy's board to attack
   * @param enemyView the enemy's board view
   * @throws IOException if an I/O error occurs
   */
  public void playOneTurn(Board<Character> enemyBoard, BoardTextView enemyView) throws IOException {
    String prompt = name + "'s turn:\n";
    out.println(prompt);
    String enemyName = name.equals("A") ? "B" : "A";
    out.println(view.displayMyBoardWithEnemyNextToIt(enemyView, "Your ocean", "Player " + enemyName + "'s ocean"));
    
    Coordinate c = readCoordinate("Where would you like to fire at?");
    Ship<Character> ship = enemyBoard.fireAt(c);
    if (ship != null) {
        out.println("You hit a " + ship.getName() + "!");
    } else {
        out.println("You missed!");
    }
  }

  /**
   * Reads a Coordinate from the user, printing a prompt beforehand.
   * This version uses manual parsing of the input string so that if the calculated
   * row or column is negative, those branches are executed (allowing test coverage).
   *
   * Expected input format: A letter (for row) followed by a number (for column),
   * e.g., "A0", "B3", "A-1" (for negative column), or "!0" (for negative row).
   *
   * @param prompt the message to display to the user
   * @return the Coordinate read from the user
   * @throws IOException if an I/O error occurs
   */
  public Coordinate readCoordinate(String prompt) throws IOException {
    while (true) {
      out.println(prompt);
      String s = input.readLine();
      if (s == null) {
        throw new EOFException("No more input");
      }
      try {
        char rowChar = s.charAt(0);
        int row = rowChar - 'A';
        int col = Integer.parseInt(s.substring(1));
        if (row < 0) {
          out.println("Please enter a valid coordinate (e.g., A0)");
          continue;
        }
        if (row >= theBoard.getHeight()) {
          out.println("Please enter a valid coordinate (e.g., A0)");
          continue;
        }
        if (col < 0) {
          out.println("Please enter a valid coordinate (e.g., A0)");
          continue;
        }
        if (col >= theBoard.getWidth()) {
          out.println("Please enter a valid coordinate (e.g., A0)");
          continue;
        }
        return new Coordinate(row, col);
      } catch (Exception e) {
        out.println("Please enter a valid coordinate (e.g., A0)");
      }
    }
  }

  /**
   * Gets the player's board
   * @return the player's Board
   */
  public Board<Character> getBoard() {
    return theBoard;
  }
}
