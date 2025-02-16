package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ShipDirectionTest {
    @Test
    public void test_getValue() {
        assertEquals('U', ShipDirection.UP.getValue());
        assertEquals('R', ShipDirection.RIGHT.getValue());
        assertEquals('D', ShipDirection.DOWN.getValue());
        assertEquals('L', ShipDirection.LEFT.getValue());
        assertEquals('H', ShipDirection.HORIZONTAL.getValue());
        assertEquals('V', ShipDirection.VERTICAL.getValue());
    }

    @Test
    public void test_fromChar() {
        assertEquals(ShipDirection.UP, ShipDirection.fromChar('U'));
        assertEquals(ShipDirection.UP, ShipDirection.fromChar('u'));
        assertEquals(ShipDirection.RIGHT, ShipDirection.fromChar('R'));
        assertEquals(ShipDirection.DOWN, ShipDirection.fromChar('D'));
        assertEquals(ShipDirection.LEFT, ShipDirection.fromChar('L'));
        assertEquals(ShipDirection.HORIZONTAL, ShipDirection.fromChar('H'));
        assertEquals(ShipDirection.VERTICAL, ShipDirection.fromChar('V'));
    }

    @Test
    public void test_fromChar_invalid() {
        assertThrows(IllegalArgumentException.class, () -> ShipDirection.fromChar('X'));
    }
} 
