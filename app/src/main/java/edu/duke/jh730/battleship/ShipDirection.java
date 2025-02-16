package edu.duke.jh730.battleship;

public enum ShipDirection {
    UP('U'), RIGHT('R'), DOWN('D'), LEFT('L'), 
    HORIZONTAL('H'), VERTICAL('V');
    
    private final char value;
    
    ShipDirection(char value) {
        this.value = value;
    }
    
    public char getValue() {
        return value;
    }
    
    public static ShipDirection fromChar(char c) {
        for (ShipDirection d : values()) {
            if (d.value == Character.toUpperCase(c)) {
                return d;
            }
        }
        throw new IllegalArgumentException("Invalid direction: " + c);
    }
} 
