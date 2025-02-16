package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class ComplexShipTest {
    @Test
    public void test_constructor_and_getters() {
        Coordinate c1 = new Coordinate(1, 2);
        HashSet<Coordinate> coords = new HashSet<>();
        coords.add(c1);
        coords.add(new Coordinate(1, 3));
        coords.add(new Coordinate(2, 2));
        
        ShipDisplayInfo<Character> myInfo = new SimpleShipDisplayInfo<>('b', '*');
        ShipDisplayInfo<Character> enemyInfo = new SimpleShipDisplayInfo<>('b', '*');
        
        ComplexShip<Character> ship = new ComplexShip<>("Battleship", c1, ShipDirection.UP, coords, myInfo, enemyInfo);
        
        assertEquals("Battleship", ship.getName());
        assertEquals(ShipDirection.UP, ship.getDirection());
        assertTrue(ship.occupiesCoordinates(new Coordinate(1, 2)));
        assertTrue(ship.occupiesCoordinates(new Coordinate(1, 3)));
        assertTrue(ship.occupiesCoordinates(new Coordinate(2, 2)));
        assertFalse(ship.occupiesCoordinates(new Coordinate(0, 0)));
    }

    @Test
    public void test_hit_and_sink() {
        Coordinate c1 = new Coordinate(1, 2);
        HashSet<Coordinate> coords = new HashSet<>();
        coords.add(c1);
        coords.add(new Coordinate(1, 3));
        
        ShipDisplayInfo<Character> myInfo = new SimpleShipDisplayInfo<>('b', '*');
        ShipDisplayInfo<Character> enemyInfo = new SimpleShipDisplayInfo<>('b', '*');
        
        ComplexShip<Character> ship = new ComplexShip<>("Battleship", c1, ShipDirection.UP, coords, myInfo, enemyInfo);
        
        assertFalse(ship.isSunk());
        ship.recordHitAt(c1);
        assertFalse(ship.isSunk());
        assertTrue(ship.wasHitAt(c1));
        ship.recordHitAt(new Coordinate(1, 3));
        assertTrue(ship.isSunk());
    }

    
} 
