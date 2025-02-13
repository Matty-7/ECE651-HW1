package edu.duke.jh730.battleship;

import javax.swing.*;
import java.awt.*;

public class BattleshipGame {
    private final GUIPlayer player1;
    private final GUIPlayer player2;
    private GUIPlayer currentPlayer;
    private final BattleshipGUI gui;
    private boolean isGameOver;

    public BattleshipGame() {
        Board<Character> board1 = new BattleShipBoard<>(10, 10, 'X');
        Board<Character> board2 = new BattleShipBoard<>(10, 10, 'X');
        AbstractShipFactory<Character> factory = new V1ShipFactory();
        
        player1 = new GUIPlayer("Player 1", board1, factory);
        player2 = new GUIPlayer("Player 2", board2, factory);
        currentPlayer = player1;
        
        gui = new BattleshipGUI();
        gui.setPlayers(player1, player2);
        gui.setVisible(true);
        setupGame();
    }

    private void setupGame() {
        // Set up move handlers for both players
        player1.setMoveHandler(c -> handlePlayerMove(player1, player2, c));
        player2.setMoveHandler(c -> handlePlayerMove(player2, player1, c));
        
        // Set up placement handlers
        player1.setPlacementHandler(c -> handleShipPlacement(player1, c));
        player2.setPlacementHandler(c -> handleShipPlacement(player2, c));

        // Start with player 1's placement phase
        SwingUtilities.invokeLater(() -> {
            gui.showOnlyPlayer(player1);
            player1.startPlacementPhase();
        });
    }

    private void handleShipPlacement(GUIPlayer player, Coordinate c) {
        if (c == null) {
            // Placement phase complete for this player
            if (player == player1) {
                // Switch to player 2's placement phase
                gui.showOnlyPlayer(player2);
                player2.startPlacementPhase();
            } else {
                // Both players have completed placement, start the game
                gui.showBothPlayers();
                currentPlayer = player1;
                JOptionPane.showMessageDialog(null,
                    "All ships placed! Game starting.\n" + currentPlayer.getName() + "'s turn",
                    "Game Start",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void handlePlayerMove(GUIPlayer attacker, GUIPlayer defender, Coordinate c) {
        if (!isGameOver && attacker == currentPlayer && !defender.getBoard().wasAlreadyShot(c)) {
            attacker.playOneTurn(defender.getBoard(), c);
            updateBoards();
            
            if (defender.getBoard().isAllSunk()) {
                isGameOver = true;
                JOptionPane.showMessageDialog(null,
                    attacker.getName() + " wins!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Switch turns
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
                JOptionPane.showMessageDialog(null,
                    "It's " + currentPlayer.getName() + "'s turn",
                    "Turn Change",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void updateBoards() {
        gui.updateBoards();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BattleshipGame();
        });
    }
} 
