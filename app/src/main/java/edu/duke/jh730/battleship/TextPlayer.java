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
  private int moveActionsLeft;
  private int sonarActionsLeft;
  private final boolean isComputer;
  private int nextComputerShotRow;
  private int nextComputerShotCol;

  public TextPlayer(String name, Board<Character> theBoard, BufferedReader input, PrintStream out,
                    AbstractShipFactory<Character> shipFactory, boolean isComputer) {
    this.name = name;
    this.theBoard = theBoard;
    this.input = input;
    this.out = out;
    this.shipFactory = shipFactory;
    this.view = new BoardTextView(this.theBoard);
    this.moveActionsLeft = 3;
    this.sonarActionsLeft = 3;
    this.isComputer = isComputer;
    this.nextComputerShotRow = 0;
    this.nextComputerShotCol = 0;
  }

  public TextPlayer(String name, Board<Character> theBoard, BufferedReader input, PrintStream out,
                    AbstractShipFactory<Character> shipFactory) {
    this(name, theBoard, input, out, shipFactory, false);
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
    if (isComputer) {
      doComputerPlacement();
      return;
    }
    out.println("Player " + name + ": you are going to place the following ships.");
    out.println("For Submarines and Destroyers, type the coordinate of the upper left");
    out.println("side of the ship, followed by either H (for horizontal) or V (for");
    out.println("vertical). For example M4H would place a ship horizontally starting");
    out.println("at M4 and going to the right.");
    out.println("");
    out.println("For Battleships and Carriers, type the coordinate followed by");
    out.println("U (up), R (right), D (down), or L (left) for the orientation.");
    out.println("For example, M4U would place a ship pointing upward from M4.");
    out.println("");
    out.println("You have:");
    out.println("");
    out.println("2 \"Submarines\" ships that are 1x2 (H or V)");
    out.println("3 \"Destroyers\" that are 1x3 (H or V)");
    out.println("3 \"Battleships\" that are special shapes (U, R, D, or L)");
    out.println("2 \"Carriers\" that are special shapes (U, R, D, or L)");
    out.println("");
    out.print(view.displayMyOwnBoard());

    // Try to read from placement file first
    String userDir = System.getProperty("user.dir");
    String placementFile = userDir + "/placement_" + name + ".txt";
    out.println("Looking for placement file at: " + placementFile);
    
    try {
        if (readPlacementsFromFile(placementFile)) {
            return;  // Successfully read all placements
        }
    } catch (IOException e) {
        out.println("Error reading file: " + e.getMessage());
        out.println("Proceeding with manual placement.");
    }

    // If file reading fails, proceed with manual placement
    // Place 2 Submarines
    for (int i = 0; i < 2; i++) {
        doOnePlacement("Submarine");
    }
    
    // Place 3 Destroyers
    for (int i = 0; i < 3; i++) {
        doOnePlacement("Destroyer");
    }
    
    // Place 3 Battleships
    for (int i = 0; i < 3; i++) {
        doOnePlacement("Battleship");
    }
    
    // Place 2 Carriers
    for (int i = 0; i < 2; i++) {
        doOnePlacement("Carrier");
    }
  }

  /**
   * Reads ship placements from a file
   * @param filename the name of the file to read from
   * @return true if all placements were successful, false otherwise
   * @throws IOException if there's an error reading the file
   */
  private boolean readPlacementsFromFile(String filename) throws IOException {
    out.println("Attempting to read ship placements from " + filename);
    try (BufferedReader br = new BufferedReader(new java.io.FileReader(filename))) {
      String line;
      int lineNumber = 0;
      while ((line = br.readLine()) != null) {
        lineNumber++;
        // Skip empty lines and comments
        if (line.trim().isEmpty() || line.startsWith("#")) {
          continue;
        }
        
        // Each line should be: shipType placement
        // e.g., "Submarine A0H" or "Battleship B2U"
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 2) {
          out.println("Error on line " + lineNumber + ": Invalid format - " + line);
          return false;
        }
        
        String shipType = parts[0];
        String placement = parts[1];
        
        Ship<Character> s;
        try {
          Placement p = new Placement(placement);
          switch (shipType) {
            case "Submarine":
              s = shipFactory.makeSubmarine(p);
              break;
            case "Destroyer":
              s = shipFactory.makeDestroyer(p);
              break;
            case "Battleship":
              s = shipFactory.makeBattleship(p);
              break;
            case "Carrier":
              s = shipFactory.makeCarrier(p);
              break;
            default:
              out.println("Error on line " + lineNumber + ": Unknown ship type - " + shipType);
              return false;
          }
        } catch (IllegalArgumentException e) {
          out.println("Error on line " + lineNumber + ": Invalid placement - " + placement);
          out.println("Reason: " + e.getMessage());
          return false;
        }
        
        String addShipError = theBoard.tryAddShip(s);
        if (addShipError != null) {
          out.println("Error on line " + lineNumber + ": " + addShipError);
          return false;
        }
        out.print(view.displayMyOwnBoard());
      }
      out.println("Successfully read all ship placements from file.");
      return true;
    } catch (java.io.FileNotFoundException e) {
      out.println("Placement file not found: " + filename);
      out.println("Proceeding with manual placement.");
      return false;
    } catch (IOException e) {
      out.println("Error reading placement file: " + e.getMessage());
      out.println("Proceeding with manual placement.");
      return false;
    }
  }

  public void doOnePlacement(String shipName) throws IOException {
    while (true) {
        // Add detailed ship information
        out.println("\nNow placing a " + shipName + ":");
        switch (shipName) {
            case "Submarine":
                out.println("This ship is 1x2 in size.");
                out.println("Valid orientations are H (horizontal) or V (vertical)");
                out.println("Example: A0H places a submarine at A0 going right to A1");
                break;
            case "Destroyer":
                out.println("This ship is 1x3 in size.");
                out.println("Valid orientations are H (horizontal) or V (vertical)");
                out.println("Example: B2V places a destroyer at B2 going down to D2");
                break;
            case "Battleship":
                out.println("This ship has a special shape (Battleship).");
                out.println("Valid orientations are U (up), R (right), D (down), or L (left).");
                out.println("For the Up orientation, the ship looks like:");
                out.println("  *b");
                out.println("  bbb");
                out.println("Here, '*' indicates that the top-left cell of the enclosing rectangle is not part of the ship.");
                break;
            case "Carrier":
                out.println("This ship has a special shape (Carrier).");
                out.println("Valid orientations are U (up), R (right), D (down), or L (left).");
                out.println("For the Up orientation, the ship looks like:");
                out.println("  c");
                out.println("  c");
                out.println("  cc");
                out.println("  cc");
                out.println("   c");
                out.println("In this diagram, the top-left cell is part of the ship and is shown in lowercase.");
                break;
        }
        
        Placement p = readPlacement("Player " + name + " where do you want to place a " + shipName + "?");
        Ship<Character> s;
        try {
            switch (shipName) {
                case "Submarine":
                    s = shipFactory.makeSubmarine(p);
                    break;
                case "Destroyer":
                    s = shipFactory.makeDestroyer(p);
                    break;
                case "Battleship":
                    s = shipFactory.makeBattleship(p);
                    break;
                case "Carrier":
                    s = shipFactory.makeCarrier(p);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown ship type: " + shipName);
            }
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
   * Play one turn for this player
   * @param enemyBoard the enemy's board to attack
   * @param enemyView the enemy's board view
   * @throws IOException if an I/O error occurs
   */
  public void playOneTurn(Board<Character> enemyBoard, BoardTextView enemyView) throws IOException {
    String enemyName = name.equals("A") ? "B" : "A";
    
    if (isComputer) {
      // Computer's turn
      out.println("Player " + name + "'s turn");
      doComputerTurn(enemyBoard);
      return;
    }
    
    // Human player's turn
    out.println("Player " + name + "'s turn");
    out.println(view.displayMyBoardWithEnemyNextToIt(enemyView, "Your ocean", "Player " + enemyName + "'s ocean"));
    
    // Display available actions
    out.println("\nPossible actions for Player " + name + ":\n");
    out.println(" F Fire at a square");
    if (moveActionsLeft > 0) {
      out.println(" M Move a ship to another square (" + moveActionsLeft + " remaining)");
    }
    if (sonarActionsLeft > 0) {
      out.println(" S Sonar scan (" + sonarActionsLeft + " remaining)");
    }
    out.println("\nPlayer " + name + ", what would you like to do?");

    while (true) {
      String action = input.readLine().toUpperCase().trim();
      switch (action) {
        case "F":
          doFireAction(enemyBoard);
          return;
        case "M":
          if (moveActionsLeft > 0) {
            if (doMoveShip()) {
              moveActionsLeft--;
              return;
            }
          } else {
            out.println("No more move actions remaining!");
          }
          break;
        case "S":
          if (sonarActionsLeft > 0) {
            if (doSonarScan(enemyBoard)) {
              sonarActionsLeft--;
              return;
            }
          } else {
            out.println("No more sonar actions remaining!");
          }
          break;
        default:
          out.println("Invalid action! Please enter F, M, or S.");
      }
    }
  }

  private void doComputerTurn(Board<Character> enemyBoard) {
    // Simple random chance to use special action (10%)
    if (Math.random() < 0.1) {
      if (sonarActionsLeft > 0 || moveActionsLeft > 0) {
        out.println("Player " + name + " used a special action");
        if (sonarActionsLeft > 0) {
          sonarActionsLeft--;
        } else if (moveActionsLeft > 0) {
          moveActionsLeft--;
        }
        return;
      }
    }

    // Regular fire action
    Coordinate c = new Coordinate(nextComputerShotRow, nextComputerShotCol);
    
    // Update next shot position
    nextComputerShotCol++;
    if (nextComputerShotCol >= theBoard.getWidth()) {
      nextComputerShotCol = 0;
      nextComputerShotRow++;
      if (nextComputerShotRow >= theBoard.getHeight()) {
        // If we've covered the whole board, start over
        nextComputerShotRow = 0;
      }
    }
    
    // Skip if already shot here
    if (enemyBoard.wasAlreadyShot(c)) {
      doComputerTurn(enemyBoard);
      return;
    }
    
    Ship<Character> ship = enemyBoard.fireAt(c);
    if (ship != null) {
      out.println("Player " + name + " hit a " + ship.getName() + " at " + 
                  (char)('A' + c.getRow()) + c.getColumn() + "!");
      if (ship.isSunk()) {
        out.println("Player " + name + " has destroyed Player " + 
                   (name.equals("A") ? "B" : "A") + "'s " + ship.getName() + "!");
      }
    } else {
      out.println("Player " + name + " missed!");
    }
  }

  private void doFireAction(Board<Character> enemyBoard) throws IOException {
    out.println("Where would you like to fire at?");
    while (true) {
      Coordinate c = readCoordinate("Enter coordinate: ");
      if (enemyBoard.wasAlreadyShot(c)) {
        out.println("You have already fired at " + (char)('A' + c.getRow()) + c.getColumn() + "!");
        out.println("Please choose a different coordinate.");
        continue;
      }
      
      Ship<Character> ship = enemyBoard.fireAt(c);
      if (ship != null) {
        out.println("You hit a " + ship.getName() + "!");
        if (ship.isSunk()) {
          out.println("You have destroyed Player " + (name.equals("A") ? "B" : "A") + "'s " + ship.getName() + "!");
        }
      } else {
        out.println("You missed!");
      }
      break;
    }
  }

  private boolean doMoveShip() throws IOException {
    out.println("Which ship do you want to move?");
    out.println("Enter a coordinate that is part of your ship:");
    
    Coordinate shipCoord = readCoordinate("Enter coordinate: ");
    Ship<Character> shipToMove = null;
    
    // Find the ship at the given coordinate
    for (Ship<Character> s : theBoard.getShips()) {
      if (s.occupiesCoordinates(shipCoord)) {
        shipToMove = s;
        break;
      }
    }
    
    if (shipToMove == null) {
      out.println("No ship found at that coordinate!");
      return false;
    }
    
    out.println("Enter the new placement for your " + shipToMove.getName());
    if (shipToMove instanceof RectangleShip) {
      out.println("The format should be like 'A0H' or 'B1V'");
    } else {
      out.println("The format should be like 'A0U', 'B1R', 'C2D', or 'D3L'");
      if (shipToMove.getName().equals("Battleship")) {
        out.println("For the Up orientation, the ship looks like:");
        out.println("  *b");
        out.println("  bbb");
      } else if (shipToMove.getName().equals("Carrier")) {
        out.println("For the Up orientation, the ship looks like:");
        out.println("  c");
        out.println("  c");
        out.println("  cc");
        out.println("  cc");
        out.println("   c");
      }
    }
    
    try {
      Placement newPlacement = readPlacement("Enter new placement: ");
      
      // Try to move the ship
      String moveError = theBoard.moveShip(shipToMove, newPlacement);
      if (moveError != null) {
        out.println("Invalid move: " + moveError);
        return false;
      }
      
      out.println("Successfully moved " + shipToMove.getName());
      out.print(view.displayMyOwnBoard());
      return true;
      
    } catch (IllegalArgumentException e) {
      out.println("Invalid placement format!");
      return false;
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

  public PrintStream getOut() {
    return out;
  }

  private boolean doSonarScan(Board<Character> enemyBoard) throws IOException {
    if (sonarActionsLeft <= 0) {
      out.println("No more sonar scans remaining!");
      return false;
    }

    out.println("Where would you like to scan?");
    Coordinate center = readCoordinate("Enter coordinate for center of scan: ");
    
    // Check if the coordinate is valid for a sonar scan (not too close to edges)
    if (center.getRow() < 3 || center.getRow() >= theBoard.getHeight() - 3 ||
        center.getColumn() < 3 || center.getColumn() >= theBoard.getWidth() - 3) {
      out.println("Invalid scan location! The scan center must be at least 3 squares from the board edge.");
      return false;
    }

    // Initialize counters for different ship types
    int submarines = 0;
    int destroyers = 0;
    int battleships = 0;
    int carriers = 0;

    // Scan in a diamond shape
    for (int rowOffset = -3; rowOffset <= 3; rowOffset++) {
      // For each row offset, calculate the allowed column range
      int colRange = 3 - Math.abs(rowOffset);
      for (int colOffset = -colRange; colOffset <= colRange; colOffset++) {
        Coordinate scanCoord = new Coordinate(center.getRow() + rowOffset, 
                                            center.getColumn() + colOffset);
        // Skip if coordinate is outside board bounds
        if (scanCoord.getRow() < 0 || scanCoord.getRow() >= theBoard.getHeight() ||
            scanCoord.getColumn() < 0 || scanCoord.getColumn() >= theBoard.getWidth()) {
          continue;
        }
        
        Ship<Character> ship = enemyBoard.getShipAt(scanCoord);
        if (ship != null) {
          switch (ship.getName()) {
            case "Submarine":
              submarines++;
              break;
            case "Destroyer":
              destroyers++;
              break;
            case "Battleship":
              battleships++;
              break;
            case "Carrier":
              carriers++;
              break;
          }
        }
      }
    }

    // Display scan results in the required format
    out.println("Sonar scan report at " + (char)('A' + center.getRow()) + center.getColumn() + ":");
    out.println("Submarines occupy " + submarines + " squares");
    out.println("Destroyers occupy " + destroyers + " squares");
    out.println("Battleships occupy " + battleships + " squares");
    out.println("Carriers occupy " + carriers + " squares");

    return true;
  }

  private void doComputerPlacement() {
    // Predefined placements for computer
    String[] placements = {
      // 2 Submarines
      "Submarine A0H",
      "Submarine B0H",
      // 3 Destroyers
      "Destroyer C0H",
      "Destroyer D0H",
      "Destroyer E0H",
      // 3 Battleships
      "Battleship F0U",
      "Battleship G0U",
      "Battleship H0U",
      // 2 Carriers
      "Carrier I0U",
      "Carrier J0U"
    };

    for (String placement : placements) {
      String[] parts = placement.split(" ");
      String shipType = parts[0];
      String placementStr = parts[1];
      
      try {
        Placement p = new Placement(placementStr);
        Ship<Character> s = null;
        
        // Create ship based on type
        switch (shipType) {
          case "Submarine":
            s = shipFactory.makeSubmarine(p);
            break;
          case "Destroyer":
            s = shipFactory.makeDestroyer(p);
            break;
          case "Battleship":
            s = shipFactory.makeBattleship(p);
            break;
          case "Carrier":
            s = shipFactory.makeCarrier(p);
            break;
        }
        
        // Try to add ship, if fails, try next column
        String error = theBoard.tryAddShip(s);
        if (error != null) {
          int col = p.getWhere().getColumn();
          int row = p.getWhere().getRow();
          
          // Try moving right until we find a valid position or reach board edge
          while (error != null && col < theBoard.getWidth() - 4) {
            col++;
            Placement newP = new Placement(new Coordinate(row, col), p.getOrientation());
            switch (shipType) {
              case "Submarine":
                s = shipFactory.makeSubmarine(newP);
                break;
              case "Destroyer":
                s = shipFactory.makeDestroyer(newP);
                break;
              case "Battleship":
                s = shipFactory.makeBattleship(newP);
                break;
              case "Carrier":
                s = shipFactory.makeCarrier(newP);
                break;
            }
            error = theBoard.tryAddShip(s);
          }
          
          // If still can't place, try next row
          if (error != null && row < theBoard.getHeight() - 4) {
            row++;
            col = 0;
            Placement newP = new Placement(new Coordinate(row, col), p.getOrientation());
            switch (shipType) {
              case "Submarine":
                s = shipFactory.makeSubmarine(newP);
                break;
              case "Destroyer":
                s = shipFactory.makeDestroyer(newP);
                break;
              case "Battleship":
                s = shipFactory.makeBattleship(newP);
                break;
              case "Carrier":
                s = shipFactory.makeCarrier(newP);
                break;
            }
            error = theBoard.tryAddShip(s);
          }
        }
      } catch (IllegalArgumentException e) {
        // Skip if placement fails
        continue;
      }
    }
  }
}
