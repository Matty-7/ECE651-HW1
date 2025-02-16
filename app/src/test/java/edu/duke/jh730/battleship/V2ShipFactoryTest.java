package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class V2ShipFactoryTest {
    private void checkShip(Ship<Character> ship, String expectedName, char expectedLetter,
                          Coordinate... expectedLocs) {
        assertEquals(expectedName, ship.getName());
        for (Coordinate c : expectedLocs) {
            assertTrue(ship.occupiesCoordinates(c));
            assertEquals(expectedLetter, ship.getDisplayInfoAt(c, true));
        }
    }

    @Test
    public void test_createRectangleShip() {
        V2ShipFactory factory = new V2ShipFactory();
        Placement p1 = new Placement(new Coordinate(1, 2), 'V');
        Ship<Character> sub = factory.makeSubmarine(p1);
        checkShip(sub, "Submarine", 's', new Coordinate(1, 2), new Coordinate(2, 2));

        Placement p2 = new Placement(new Coordinate(1, 2), 'H');
        Ship<Character> dest = factory.makeDestroyer(p2);
        checkShip(dest, "Destroyer", 'd', new Coordinate(1, 2), new Coordinate(1, 3), new Coordinate(1, 4));
    }

    @Test
    public void test_createBattleship() {
        V2ShipFactory factory = new V2ShipFactory();
        
        // Test UP orientation
        Placement p1 = new Placement(new Coordinate(1, 2), 'U');
        Ship<Character> b1 = factory.makeBattleship(p1);
        checkShip(b1, "Battleship", 'b', 
                 new Coordinate(1, 3),      // 顶部的 b
                 new Coordinate(2, 2),      // 左边的 b
                 new Coordinate(2, 3),      // 中间的 b
                 new Coordinate(2, 4));     // 右边的 b

        // Test RIGHT orientation
        Placement p2 = new Placement(new Coordinate(2, 3), 'R');
        Ship<Character> b2 = factory.makeBattleship(p2);
        checkShip(b2, "Battleship", 'b',
                 new Coordinate(2, 3),      // 顶部的 b
                 new Coordinate(3, 3),      // 中间的 b
                 new Coordinate(3, 4),      // 右边的 b
                 new Coordinate(4, 3));     // 底部的 b

        // Test DOWN orientation
        Placement p3 = new Placement(new Coordinate(2, 3), 'D');
        Ship<Character> b3 = factory.makeBattleship(p3);
        checkShip(b3, "Battleship", 'b',
                 new Coordinate(2, 3),      // 左边的 b
                 new Coordinate(2, 4),      // 中间的 b
                 new Coordinate(2, 5),      // 右边的 b
                 new Coordinate(3, 4));     // 底部的 b

        // Test LEFT orientation
        Placement p4 = new Placement(new Coordinate(2, 3), 'L');
        Ship<Character> b4 = factory.makeBattleship(p4);
        checkShip(b4, "Battleship", 'b',
                 new Coordinate(2, 4),      // 顶部的 b
                 new Coordinate(3, 3),      // 左边的 b
                 new Coordinate(3, 4),      // 中间的 b
                 new Coordinate(4, 4));     // 底部的 b
    }

    @Test
    public void test_createCarrier() {
        V2ShipFactory factory = new V2ShipFactory();
        
        // Test UP orientation
        Placement p1 = new Placement(new Coordinate(1, 2), 'U');
        Ship<Character> c1 = factory.makeCarrier(p1);
        checkShip(c1, "Carrier", 'c',
                 new Coordinate(1, 2),      // 第一行
                 new Coordinate(2, 2),      // 第二行
                 new Coordinate(3, 2),      // 第三行
                 new Coordinate(4, 2),      // 第四行
                 new Coordinate(3, 3),      // 第三行右边
                 new Coordinate(4, 3),      // 第四行右边
                 new Coordinate(5, 3));     // 第五行右边

        // Test RIGHT orientation
        Placement p2 = new Placement(new Coordinate(2, 3), 'R');
        Ship<Character> c2 = factory.makeCarrier(p2);
        checkShip(c2, "Carrier", 'c',
                 new Coordinate(2, 4),      // 上排第二个
                 new Coordinate(2, 5),      // 上排第三个
                 new Coordinate(2, 6),      // 上排第四个
                 new Coordinate(2, 7),      // 上排第五个
                 new Coordinate(3, 3),      // 下排第一个
                 new Coordinate(3, 4),      // 下排第二个
                 new Coordinate(3, 5));     // 下排第三个

        // Test DOWN orientation
        Placement p3 = new Placement(new Coordinate(2, 3), 'D');
        Ship<Character> c3 = factory.makeCarrier(p3);
        checkShip(c3, "Carrier", 'c',
                 new Coordinate(2, 3),      // 第一行
                 new Coordinate(3, 3),      // 第二行
                 new Coordinate(4, 3),      // 第三行
                 new Coordinate(3, 4),      // 第二行右边
                 new Coordinate(4, 4),      // 第三行右边
                 new Coordinate(5, 4),      // 第四行右边
                 new Coordinate(6, 4));     // 第五行右边

        // Test LEFT orientation
        Placement p4 = new Placement(new Coordinate(2, 3), 'L');
        Ship<Character> c4 = factory.makeCarrier(p4);
        checkShip(c4, "Carrier", 'c',
                 new Coordinate(2, 5),      // 上排第三个
                 new Coordinate(2, 6),      // 上排第四个
                 new Coordinate(2, 7),      // 上排第五个
                 new Coordinate(3, 3),      // 下排第一个
                 new Coordinate(3, 4),      // 下排第二个
                 new Coordinate(3, 5),      // 下排第三个
                 new Coordinate(3, 6));     // 下排第四个
    }

    @Test
    public void test_invalid_orientation() {
        V2ShipFactory factory = new V2ShipFactory();
        Placement p = new Placement(new Coordinate(1, 2), 'X');
        assertThrows(IllegalArgumentException.class, () -> factory.makeBattleship(p));
        assertThrows(IllegalArgumentException.class, () -> factory.makeCarrier(p));
    }
} 
