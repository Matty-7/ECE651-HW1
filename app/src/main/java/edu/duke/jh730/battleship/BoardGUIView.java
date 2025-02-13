package edu.duke.jh730.battleship;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BoardGUIView {
    private final Board<Character> board;
    private final JButton[][] gridButtons;
    private static final int BOARD_SIZE = 10;
    private static final Map<Character, Color> CELL_COLORS = new HashMap<>();
    private static final Map<String, Color> SHIP_PREVIEW_COLORS = new HashMap<>();
    private static final Map<String, String> SHIP_ICONS = new HashMap<>();
    private static final Map<Character, String> SHIP_LETTERS = new HashMap<>();
    private final GUIPlayer player;
    private final boolean isEnemy;
    private Coordinate previewStart;
    private int previewSize;
    private boolean previewVertical;
    private Color previewColor;
    private String currentShipIcon;
    
    static {
        CELL_COLORS.put('s', new Color(100, 100, 100));    // ship - dark gray
        CELL_COLORS.put('d', new Color(100, 100, 100));    // ship - dark gray
        CELL_COLORS.put('b', new Color(100, 100, 100));    // ship - dark gray
        CELL_COLORS.put('c', new Color(100, 100, 100));    // ship - dark gray
        CELL_COLORS.put('*', Color.RED);     // hit
        CELL_COLORS.put('X', Color.BLACK);   // miss
        CELL_COLORS.put(null, new Color(0, 100, 200));   // water - ocean blue
        
        SHIP_PREVIEW_COLORS.put("Submarine", new Color(150, 150, 150));    // light gray
        SHIP_PREVIEW_COLORS.put("Destroyer", new Color(100, 150, 100));    // green
        SHIP_PREVIEW_COLORS.put("Battleship", new Color(150, 100, 100));   // red
        SHIP_PREVIEW_COLORS.put("Carrier", new Color(100, 100, 150));      // blue

        SHIP_ICONS.put("Submarine", "S");     // Submarine
        SHIP_ICONS.put("Destroyer", "D");     // Destroyer
        SHIP_ICONS.put("Battleship", "B");    // Battleship
        SHIP_ICONS.put("Carrier", "C");       // Carrier

        SHIP_LETTERS.put('s', "S");     // Submarine
        SHIP_LETTERS.put('d', "D");     // Destroyer
        SHIP_LETTERS.put('b', "B");     // Battleship
        SHIP_LETTERS.put('c', "C");     // Carrier
    }
    
    public BoardGUIView(Board<Character> board, GUIPlayer player, boolean isEnemy) {
        this.board = board;
        this.player = player;
        this.isEnemy = isEnemy;
        this.gridButtons = new JButton[BOARD_SIZE][BOARD_SIZE];
        this.previewStart = null;
        this.previewSize = 0;
        this.previewVertical = true;
        this.previewColor = Color.GRAY;
        this.currentShipIcon = "";
    }
    
    public JPanel createBoardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(BOARD_SIZE + 1, BOARD_SIZE + 1));
        
        // Add column headers (A-J)
        gridPanel.add(new JLabel(""));  // Empty corner cell
        for (int col = 0; col < BOARD_SIZE; col++) {
            gridPanel.add(new JLabel(String.valueOf((char)('A' + col)), SwingConstants.CENTER));
        }
        
        // Add row numbers and grid buttons
        for (int row = 0; row < BOARD_SIZE; row++) {
            gridPanel.add(new JLabel(String.valueOf(row), SwingConstants.CENTER));
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setBackground(CELL_COLORS.get(null));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setFont(new Font("Dialog", Font.BOLD, 16));  // 使用粗体，稍微调小字号
                gridButtons[row][col] = button;
                
                final int finalRow = row;
                final int finalCol = col;
                
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        handleMouseEnter(finalRow, finalCol);
                    }
                    
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        handleMouseExit(finalRow, finalCol);
                    }
                });
                
                button.addActionListener(e -> handleCellClick(finalRow, finalCol));
                gridPanel.add(button);
            }
        }
        
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private void handleMouseEnter(int row, int col) {
        if (player.isInPlacementPhase()) {
            previewStart = new Coordinate(row, col);
            previewSize = player.getCurrentShipSize();
            String shipName = player.getCurrentShipName();
            previewColor = SHIP_PREVIEW_COLORS.get(shipName);
            currentShipIcon = SHIP_ICONS.get(shipName);
            updateBoardWithPreview();
        }
    }
    
    private void handleMouseExit(int row, int col) {
        if (player.isInPlacementPhase()) {
            previewStart = null;
            currentShipIcon = "";
            updateBoard();
        }
    }
    
    public void setPreviewOrientation(boolean vertical) {
        previewVertical = vertical;
        if (previewStart != null) {
            updateBoardWithPreview();
        }
    }
    
    private void updateBoardWithPreview() {
        updateBoard(); // First update with actual board state
        
        if (previewStart != null && previewSize > 0) {
            int row = previewStart.getRow();
            int col = previewStart.getColumn();
            
            // Check if placement would be valid
            boolean isValid = true;
            for (int i = 0; i < previewSize; i++) {
                int previewRow = previewVertical ? row + i : row;
                int previewCol = previewVertical ? col : col + i;
                
                if (previewRow >= BOARD_SIZE || previewCol >= BOARD_SIZE) {
                    isValid = false;
                    break;
                }
            }
            
            // Show preview with appropriate color
            Color displayColor = isValid ? previewColor : Color.RED;
            
            for (int i = 0; i < previewSize; i++) {
                int previewRow = previewVertical ? row + i : row;
                int previewCol = previewVertical ? col : col + i;
                
                if (previewRow >= 0 && previewRow < BOARD_SIZE && 
                    previewCol >= 0 && previewCol < BOARD_SIZE) {
                    JButton button = gridButtons[previewRow][previewCol];
                    button.setBackground(displayColor);
                    button.setText(currentShipIcon);  // 在每个格子都显示字母
                }
            }
        }
    }
    
    private void handleCellClick(int row, int col) {
        Coordinate c = new Coordinate(row, col);
        player.handleMove(c);
    }
    
    public void updateCell(int row, int col, Character content) {
        if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
            JButton button = gridButtons[row][col];
            Character displayContent = isEnemy ? board.whatIsAtForEnemy(new Coordinate(row, col)) 
                                             : board.whatIsAtForSelf(new Coordinate(row, col));
            Color color = CELL_COLORS.getOrDefault(displayContent, CELL_COLORS.get(null));
            button.setBackground(color);
            button.setText("");  // Clear any previous icons
            
            if (displayContent != null) {
                if (displayContent == '*') {
                    button.setText("X");  // X for hit
                } else if (displayContent == 'X') {
                    button.setText("O");  // O for miss
                } else if (!isEnemy) {
                    // 根据船的类型显示对应的字母
                    button.setText(SHIP_LETTERS.getOrDefault(displayContent, ""));
                }
            }
        }
    }
    
    public void updateBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Coordinate c = new Coordinate(row, col);
                Character content = isEnemy ? board.whatIsAtForEnemy(c) : board.whatIsAtForSelf(c);
                updateCell(row, col, content);
            }
        }
    }
} 
