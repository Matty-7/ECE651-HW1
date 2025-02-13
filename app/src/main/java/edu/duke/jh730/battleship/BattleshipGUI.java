package edu.duke.jh730.battleship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BattleshipGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel player1Panel;
    private JPanel player2Panel;
    private JPanel controlPanel;
    private GUIPlayer player1;
    private GUIPlayer player2;
    
    public BattleshipGUI() {
        initializeFrame();
        createPanels();
        pack();
    }

    private void initializeFrame() {
        setTitle("Battleship Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 600));
        setLocationRelativeTo(null);
    }
    
    private void createPanels() {
        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout());
        
        // Create game boards panel
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Create player panels (will be populated later)
        player1Panel = new JPanel(new BorderLayout());
        player2Panel = new JPanel(new BorderLayout());
        player1Panel.setBorder(BorderFactory.createTitledBorder("Player 1"));
        player2Panel.setBorder(BorderFactory.createTitledBorder("Player 2"));
        
        boardsPanel.add(player1Panel);
        boardsPanel.add(player2Panel);
        
        // Create control panel
        controlPanel = new JPanel();
        JButton newGameBtn = new JButton("New Game");
        JButton surrenderBtn = new JButton("Surrender");
        controlPanel.add(newGameBtn);
        controlPanel.add(surrenderBtn);
        
        // Add components to main panel
        mainPanel.add(boardsPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    public void setPlayers(GUIPlayer p1, GUIPlayer p2) {
        this.player1 = p1;
        this.player2 = p2;
        updatePlayerPanels();
    }

    private void updatePlayerPanels() {
        if (player1 != null && player2 != null) {
            player1Panel.removeAll();
            player2Panel.removeAll();
            
            player1Panel.add(player1.getView().createBoardPanel(), BorderLayout.CENTER);
            player2Panel.add(player2.getView().createBoardPanel(), BorderLayout.CENTER);
            
            player1Panel.setBorder(BorderFactory.createTitledBorder(player1.getName()));
            player2Panel.setBorder(BorderFactory.createTitledBorder(player2.getName()));
            
            revalidate();
            repaint();
        }
    }
    
    public void showOnlyPlayer(GUIPlayer player) {
        player1Panel.setVisible(player == player1);
        player2Panel.setVisible(player == player2);
        pack();
        setLocationRelativeTo(null);
    }
    
    public void showBothPlayers() {
        player1Panel.setVisible(true);
        player2Panel.setVisible(true);
        pack();
        setLocationRelativeTo(null);
    }
    
    public void updateBoards() {
        if (player1 != null && player2 != null) {
            player1.getView().updateBoard();
            player2.getView().updateBoard();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleshipGUI gui = new BattleshipGUI();
            gui.setVisible(true);
        });
    }
} 
