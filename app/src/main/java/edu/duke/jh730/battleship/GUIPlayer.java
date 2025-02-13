package edu.duke.jh730.battleship;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public class GUIPlayer {
    private final Board<Character> theBoard;
    private final BoardGUIView view;
    private final String name;
    private final AbstractShipFactory<Character> shipFactory;
    private Consumer<Coordinate> moveHandler;
    private Consumer<Coordinate> placementHandler;
    private List<ShipType> shipsToPlace;
    private boolean isPlacementPhase;
    private Coordinate selectedCoordinate;

    private enum ShipType {
        SUBMARINE(2, "Submarine"),
        DESTROYER(3, "Destroyer"),
        BATTLESHIP(4, "Battleship"),
        CARRIER(6, "Carrier");

        private final int size;
        private final String name;

        ShipType(int size, String name) {
            this.size = size;
            this.name = name;
        }

        public int getSize() { return size; }
        public String getName() { return name; }
    }

    public GUIPlayer(String name, Board<Character> theBoard, AbstractShipFactory<Character> shipFactory) {
        this.name = name;
        this.theBoard = theBoard;
        this.shipFactory = shipFactory;
        this.view = new BoardGUIView(theBoard, this, false);
        this.isPlacementPhase = false;
        this.selectedCoordinate = null;
        initializeShipsToPlace();
    }

    private void initializeShipsToPlace() {
        shipsToPlace = new ArrayList<>();
        // Add ships according to requirements
        for (int i = 0; i < 2; i++) shipsToPlace.add(ShipType.SUBMARINE);  // 2 submarines
        for (int i = 0; i < 3; i++) shipsToPlace.add(ShipType.DESTROYER);  // 3 destroyers
        for (int i = 0; i < 3; i++) shipsToPlace.add(ShipType.BATTLESHIP); // 3 battleships
        for (int i = 0; i < 2; i++) shipsToPlace.add(ShipType.CARRIER);    // 2 carriers
    }

    public String getName() {
        return name;
    }

    public Board<Character> getBoard() {
        return theBoard;
    }

    public BoardGUIView getView() {
        return view;
    }

    public void setMoveHandler(Consumer<Coordinate> handler) {
        this.moveHandler = handler;
    }

    public void setPlacementHandler(Consumer<Coordinate> handler) {
        this.placementHandler = handler;
    }

    public boolean isInPlacementPhase() {
        return isPlacementPhase;
    }

    public int getCurrentShipSize() {
        if (!shipsToPlace.isEmpty()) {
            return shipsToPlace.get(0).getSize();
        }
        return 0;
    }

    public String getCurrentShipName() {
        if (!shipsToPlace.isEmpty()) {
            return shipsToPlace.get(0).getName();
        }
        return "";
    }

    public void handleMove(Coordinate c) {
        if (isPlacementPhase && placementHandler != null) {
            selectedCoordinate = c;
            showOrientationDialog();
        } else if (!isPlacementPhase && moveHandler != null) {
            moveHandler.accept(c);
        }
    }

    private void showOrientationDialog() {
        if (shipsToPlace.isEmpty() || selectedCoordinate == null) {
            return;
        }

        ShipType currentShip = shipsToPlace.get(0);
        JDialog dialog = new JDialog((Frame)null, "Ship Orientation", true);
        dialog.setLayout(new FlowLayout());
        
        JButton verticalBtn = new JButton("Vertical");
        JButton horizontalBtn = new JButton("Horizontal");
        
        verticalBtn.addActionListener(e -> {
            view.setPreviewOrientation(true);
            dialog.dispose();
            tryPlaceShipWithOrientation('V');
        });
        
        horizontalBtn.addActionListener(e -> {
            view.setPreviewOrientation(false);
            dialog.dispose();
            tryPlaceShipWithOrientation('H');
        });

        // Add mouse hover listeners for preview
        verticalBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                view.setPreviewOrientation(true);
            }
        });

        horizontalBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                view.setPreviewOrientation(false);
            }
        });
        
        dialog.add(verticalBtn);
        dialog.add(horizontalBtn);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void tryPlaceShipWithOrientation(char orientation) {
        if (shipsToPlace.isEmpty() || selectedCoordinate == null) {
            return;
        }

        ShipType currentShip = shipsToPlace.get(0);
        Placement p = new Placement(selectedCoordinate, orientation);
        Ship<Character> ship = null;

        try {
            switch (currentShip) {
                case SUBMARINE:
                    ship = shipFactory.makeSubmarine(p);
                    break;
                case DESTROYER:
                    ship = shipFactory.makeDestroyer(p);
                    break;
                case BATTLESHIP:
                    ship = shipFactory.makeBattleship(p);
                    break;
                case CARRIER:
                    ship = shipFactory.makeCarrier(p);
                    break;
            }

            String result = theBoard.tryAddShip(ship);
            if (result == null) {
                shipsToPlace.remove(0);
                view.updateBoard();
                if (!shipsToPlace.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                        "Next ship: " + shipsToPlace.get(0).name + " (size: " + shipsToPlace.get(0).size + ")",
                        "Next Ship",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                if (shipsToPlace.isEmpty()) {
                    isPlacementPhase = false;
                    selectedCoordinate = null;
                    JOptionPane.showMessageDialog(null,
                        "All ships placed successfully!",
                        "Placement Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                    placementHandler.accept(null);
                } else {
                    placementHandler.accept(selectedCoordinate);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                    "Invalid placement: " + result,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                "Invalid placement: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void playOneTurn(Board<Character> enemyBoard, Coordinate c) {
        Ship<Character> hitShip = enemyBoard.fireAt(c);
        view.updateBoard();
        String result = (hitShip == null) ? "missed" : "hit " + hitShip.getName();
        JOptionPane.showMessageDialog(null, 
            String.format("%s fired at %s and %s", name, c.toString(), result),
            "Shot Result",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void startPlacementPhase() {
        isPlacementPhase = true;
        selectedCoordinate = null;
        initializeShipsToPlace();
        JOptionPane.showMessageDialog(null,
            name + ": Place your ships!\nNext ship: " + shipsToPlace.get(0).name + " (size: " + shipsToPlace.get(0).size + ")",
            "Start Placement",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isPlacementComplete() {
        return !isPlacementPhase;
    }
} 
