package edu.duke.jh730.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class TextPlayer {
  private final String name;
  private final Board<Character> theBoard;
  private final BufferedReader inputReader;
  private final PrintStream out;
  private final AbstractShipFactory<Character> shipFactory;
  private final BoardTextView view;
  
  // add ship count
  private int numSubmarines = 2;
  private int numDestroyers = 3;
  private int numBattleships = 3;
  private int numCarriers = 2;

  public TextPlayer(String name, Board<Character> theBoard, BufferedReader inputReader, PrintStream out,
      AbstractShipFactory<Character> shipFactory) {
    this.name = name;
    this.theBoard = theBoard;
    this.inputReader = inputReader;
    this.out = out;
    this.shipFactory = shipFactory;
    this.view = new BoardTextView(theBoard);
  }

  public Placement readPlacement(String prompt) throws IOException {
    out.println(prompt);
    String s = inputReader.readLine();
    return new Placement(s);
  }

  public void doOnePlacement() throws IOException {
    Ship<Character> s = null;
    String shipName = "";
    String prompt = "";
    
    if (numSubmarines > 0) {
      shipName = "Submarine";
      prompt = String.format("Player %s where do you want to place a %s?", name, shipName);
      s = shipFactory.makeSubmarine(readPlacement(prompt));
      numSubmarines--;
    }
    else if (numDestroyers > 0) {
      shipName = "Destroyer";
      prompt = String.format("Player %s where do you want to place a %s?", name, shipName);
      s = shipFactory.makeDestroyer(readPlacement(prompt));
      numDestroyers--;
    }
    else if (numBattleships > 0) {
      shipName = "Battleship";
      prompt = String.format("Player %s where do you want to place a %s?", name, shipName);
      s = shipFactory.makeBattleship(readPlacement(prompt));
      numBattleships--;
    }
    else if (numCarriers > 0) {
      shipName = "Carrier";
      prompt = String.format("Player %s where do you want to place a %s?", name, shipName);
      s = shipFactory.makeCarrier(readPlacement(prompt));
      numCarriers--;
    }

    if (s != null) {
      theBoard.tryAddShip(s);
      out.print(view.displayMyOwnBoard());
    }
  }

  public boolean isPlacementDone() {
    return numSubmarines == 0 && numDestroyers == 0 && 
           numBattleships == 0 && numCarriers == 0;
  }

  public void doPlacementPhase() throws IOException {
    out.print(view.displayMyOwnBoard());
    out.println(String.format(
        "Player %s: you are going to place the following ships (which are all\n" +
        "rectangular). For each ship, type the coordinate of the upper left\n" +
        "side of the ship, followed by either H (for horizontal) or V (for\n" +
        "vertical).  For example M4H would place a ship horizontally starting\n" +
        "at M4 and going to the right.  You have\n\n" +
        "2 \"Submarines\" ships that are 1x2\n" +
        "3 \"Destroyers\" that are 1x3\n" +
        "3 \"Battleships\" that are 1x4\n" +
        "2 \"Carriers\" that are 1x6\n",
        name));
    
    while (!isPlacementDone()) {
      doOnePlacement();
    }
  }
} 
