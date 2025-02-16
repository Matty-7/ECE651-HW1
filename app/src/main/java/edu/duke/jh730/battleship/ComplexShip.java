package edu.duke.jh730.battleship;

import java.util.HashSet;

public class ComplexShip<T> extends BasicShip<T> {
    private final String name;
    private final ShipDirection direction;
    
    public ComplexShip(String name, Coordinate upperLeft, 
                      ShipDirection direction, HashSet<Coordinate> occupiedCoordinates,
                      ShipDisplayInfo<T> myDisplayInfo, ShipDisplayInfo<T> enemyDisplayInfo) {
        super(occupiedCoordinates, myDisplayInfo, enemyDisplayInfo);
        this.name = name;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public ShipDirection getDirection() {
        return direction;
    }

    @Override
    public boolean occupiesCoordinates(Coordinate where) {
        return super.occupiesCoordinates(where);
    }

    @Override
    public boolean isSunk() {
        return super.isSunk();
    }

    @Override
    public void recordHitAt(Coordinate where) {
        super.recordHitAt(where);
    }

    @Override
    public boolean wasHitAt(Coordinate where) {
        return super.wasHitAt(where);
    }
} 
