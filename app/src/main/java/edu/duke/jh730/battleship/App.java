package edu.duke.jh730.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
  final TextPlayer player1;
  final TextPlayer player2;

  public App(TextPlayer player1, TextPlayer player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  public void doPlacementPhase() throws IOException {
    player1.doPlacementPhase();
    player2.doPlacementPhase();
  }

  public static void main(String[] args) throws IOException {
    Board<Character> b1 = new BattleShipBoard<Character>(10, 20);
    Board<Character> b2 = new BattleShipBoard<Character>(10, 20);
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    V1ShipFactory shipFactory = new V1ShipFactory();
    TextPlayer p1 = new TextPlayer("A", b1, input, System.out, shipFactory);
    TextPlayer p2 = new TextPlayer("B", b2, input, System.out, shipFactory);
    App app = new App(p1, p2);
    app.doPlacementPhase();
  }
} 
