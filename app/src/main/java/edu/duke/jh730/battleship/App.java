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

  public void doAttackingPhase() throws IOException {
    while (true) {
      player1.playOneTurn(player2.getBoard(), new BoardTextView(player2.getBoard()));
      if (player2.getBoard().isAllSunk()) {
        player1.getOut().println("Player A won!");
        break;
      }
      
      player2.playOneTurn(player1.getBoard(), new BoardTextView(player1.getBoard()));
      if (player1.getBoard().isAllSunk()) {
        player2.getOut().println("Player B won!");
        break;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Board<Character> b1 = new BattleShipBoard<Character>(10, 20, 'X');
    Board<Character> b2 = new BattleShipBoard<Character>(10, 20, 'X');
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    V2ShipFactory shipFactory = new V2ShipFactory();
    TextPlayer p1 = new TextPlayer("A", b1, input, System.out, shipFactory);
    TextPlayer p2 = new TextPlayer("B", b2, input, System.out, shipFactory);
    App app = new App(p1, p2);
    app.doPlacementPhase();
    app.doAttackingPhase();
  }
} 
