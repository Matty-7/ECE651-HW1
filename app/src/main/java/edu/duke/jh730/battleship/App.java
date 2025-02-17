package edu.duke.jh730.battleship;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

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

    public static void main(String[] args) {
        AbstractShipFactory<Character> shipFactory = new V2ShipFactory();
        Board<Character> b1 = new BattleShipBoard<Character>(10, 20, 'X');
        Board<Character> b2 = new BattleShipBoard<Character>(10, 20, 'X');
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = System.out;

        out.println("Welcome to Battleship!");
        out.println("Is Player A human or computer? (h/c)");
        boolean isPlayerAComputer = false;
        boolean isPlayerBComputer = false;
        
        try {
            String choice = input.readLine().toLowerCase();
            isPlayerAComputer = choice.equals("c");
            
            out.println("Is Player B human or computer? (h/c)");
            choice = input.readLine().toLowerCase();
            isPlayerBComputer = choice.equals("c");
            
            TextPlayer player1 = new TextPlayer("A", b1, input, out, shipFactory, isPlayerAComputer);
            TextPlayer player2 = new TextPlayer("B", b2, input, out, shipFactory, isPlayerBComputer);
            
            // Copy placement files if they exist
            copyPlacementFiles();
            
            // Do placement phase
            player1.doPlacementPhase();
            player2.doPlacementPhase();
            
            // Game loop
            while (true) {
                BoardTextView view1 = new BoardTextView(b1);
                BoardTextView view2 = new BoardTextView(b2);
                
                player1.playOneTurn(b2, view2);
                if (b2.isAllSunk()) {
                    out.println("Player A wins!");
                    break;
                }
                
                player2.playOneTurn(b1, view1);
                if (b1.isAllSunk()) {
                    out.println("Player B wins!");
                    break;
                }
            }
            
        } catch (IOException e) {
            out.println("Error: " + e.getMessage());
        } finally {
            out.println("Thanks for playing!");
        }
    }

    private static void copyPlacementFiles() throws IOException {
        // Implementation of copyPlacementFiles method
    }
} 
