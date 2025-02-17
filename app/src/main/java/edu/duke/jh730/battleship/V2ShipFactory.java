package edu.duke.jh730.battleship;

import java.util.HashSet;

public class V2ShipFactory implements AbstractShipFactory<Character> {
    protected Ship<Character> createRectangleShip(Placement where, int w, int h, char letter, String name) {
        if (where.getOrientation() != 'V' && where.getOrientation() != 'H') {
            throw new IllegalArgumentException("Orientation must be 'V' or 'H' but is " + where.getOrientation());
        }
        int width = where.getOrientation() == 'V' ? w : h;
        int height = where.getOrientation() == 'V' ? h : w;
        ShipDisplayInfo<Character> myDisplayInfo = new SimpleShipDisplayInfo<>(letter, '*');
        ShipDisplayInfo<Character> enemyDisplayInfo = new SimpleShipDisplayInfo<>(letter, '*', true);
        return new RectangleShip<Character>(name, where.getWhere(), width, height, myDisplayInfo, enemyDisplayInfo);
    }

    protected Ship<Character> createComplexShip(String name, Placement where, char letter, HashSet<Coordinate> coordinates) {
        ShipDisplayInfo<Character> myDisplayInfo = new SimpleShipDisplayInfo<>(letter, '*');
        ShipDisplayInfo<Character> enemyDisplayInfo = new SimpleShipDisplayInfo<>(letter, '*', true);
        return new ComplexShip<>(name, where.getWhere(), ShipDirection.fromChar(where.getOrientation()), coordinates, myDisplayInfo, enemyDisplayInfo);
    }

    protected HashSet<Coordinate> getBattleshipCoordinates(Placement where) {
        Coordinate c = where.getWhere();
        HashSet<Coordinate> coords = new HashSet<>();
        switch (ShipDirection.fromChar(where.getOrientation())) {
            case UP:    
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 2));
                break;
            case RIGHT:
                coords.add(new Coordinate(c.getRow(), c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 2, c.getColumn()));
                break;
            case DOWN:
                coords.add(new Coordinate(c.getRow(), c.getColumn()));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 2));
                break;
            case LEFT:
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 2, c.getColumn() + 1));
                break;
            default:
                throw new IllegalArgumentException("Invalid orientation for battleship: " + where.getOrientation());
        }
        return coords;
    }

    protected HashSet<Coordinate> getCarrierCoordinates(Placement where) {
        Coordinate c = where.getWhere();
        HashSet<Coordinate> coords = new HashSet<>();
        switch (ShipDirection.fromChar(where.getOrientation())) {
            case UP:
                coords.add(new Coordinate(c.getRow(), c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 2, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 3, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 2, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 3, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 4, c.getColumn() + 1));
                break;
            case RIGHT:
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 2));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 3));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 4));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 2));
                break;
            case DOWN:
                coords.add(new Coordinate(c.getRow(), c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 2, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 2, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 3, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 4, c.getColumn() + 1));
                break;
            case LEFT:
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 2));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 3));
                coords.add(new Coordinate(c.getRow(), c.getColumn() + 4));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn()));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 1));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 2));
                coords.add(new Coordinate(c.getRow() + 1, c.getColumn() + 3));
                break;
            default:
                throw new IllegalArgumentException("Invalid orientation for carrier: " + where.getOrientation());
        }
        return coords;
    }

    @Override
    public Ship<Character> makeSubmarine(Placement where) {
        return createRectangleShip(where, 1, 2, 's', "Submarine");
    }

    @Override
    public Ship<Character> makeDestroyer(Placement where) {
        return createRectangleShip(where, 1, 3, 'd', "Destroyer");
    }

    @Override
    public Ship<Character> makeBattleship(Placement where) {
        return createComplexShip("Battleship", where, 'b', getBattleshipCoordinates(where));
    }

    @Override
    public Ship<Character> makeCarrier(Placement where) {
        return createComplexShip("Carrier", where, 'c', getCarrierCoordinates(where));
    }
} 
