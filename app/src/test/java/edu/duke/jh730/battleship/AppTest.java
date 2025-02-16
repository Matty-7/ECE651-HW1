package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

import org.junit.jupiter.api.Test;

public class AppTest {
    private ByteArrayOutputStream bytes;
    private StringReader input;
    private PrintStream output;

    private App createApp(String inputData) {
        bytes = new ByteArrayOutputStream();
        output = new PrintStream(bytes, true);
        input = new StringReader(inputData);
        BufferedReader br = new BufferedReader(input);

        Board<Character> b1 = new BattleShipBoard<>(10, 20, 'X');
        Board<Character> b2 = new BattleShipBoard<>(10, 20, 'X');
        V2ShipFactory shipFactory = new V2ShipFactory();
        TextPlayer p1 = new TextPlayer("A", b1, br, output, shipFactory);
        TextPlayer p2 = new TextPlayer("B", b2, br, output, shipFactory);

        return new App(p1, p2);
    }

    @Test
    public void test_constructor() {
        App app = createApp("");
        assertNotNull(app);
    }

    @Test
    public void test_doAttackingPhase() throws IOException {
        String inputData = 
            // First round
            "B0\n" +    // Player A hits B's submarine
            "A0\n" +    // Player B hits A's submarine
            // Second round
            "B1\n" +    // Player A sinks B's submarine
            "A1\n" +    // Player B hits A's submarine but game is already over
            // Add extra newline to prevent EOF
            "\n";

        App app = createApp(inputData);
        
        // Place ships
        V2ShipFactory shipFactory = new V2ShipFactory();
        // Place Player A's submarine at A0H (A0, A1)
        Ship<Character> shipA = shipFactory.makeSubmarine(new Placement("A0H"));
        // Place Player B's submarine at B0H (B0, B1)
        Ship<Character> shipB = shipFactory.makeSubmarine(new Placement("B0H"));
        app.player1.getBoard().tryAddShip(shipA);
        app.player2.getBoard().tryAddShip(shipB);

        // Run attacking phase
        app.doAttackingPhase();
        
        String output = bytes.toString();
        System.out.println("=== Actual Output ===");
        System.out.println(output);
        System.out.println("=== End Output ===");
        
        // Check turn announcements
        assertTrue(output.contains("Player A's turn"), "Missing Player A's turn announcement");
        assertTrue(output.contains("Player B's turn"), "Missing Player B's turn announcement");
        // Check hit messages (no exclamation marks)
        assertTrue(output.contains("You hit a Submarine"), "Missing hit message");
        // Check win message
        assertTrue(output.contains("Player A won!"), "Missing win message");
    }

    @Test
    public void test_doPlacementPhase() throws IOException {
        String inputData = 
            // Player A's placements
            "A0H\n" +    // Submarine 1
            "B0H\n" +    // Submarine 2
            "C0H\n" +    // Destroyer 1
            "D0H\n" +    // Destroyer 2
            "E0H\n" +    // Destroyer 3
            "F0U\n" +    // Battleship 1
            "G0U\n" +    // Battleship 2
            "H0U\n" +    // Battleship 3
            "I0U\n" +    // Carrier 1
            "J0U\n" +    // Carrier 2
            // Player B's placements
            "A0H\n" +    // Submarine 1
            "B0H\n" +    // Submarine 2
            "C0H\n" +    // Destroyer 1
            "D0H\n" +    // Destroyer 2
            "E0H\n" +    // Destroyer 3
            "F0U\n" +    // Battleship 1
            "G0U\n" +    // Battleship 2
            "H0U\n" +    // Battleship 3
            "I0U\n" +    // Carrier 1
            "J0U\n";     // Carrier 2

        App app = createApp(inputData);
        app.doPlacementPhase();
        
        String output = bytes.toString();
        // Check placement instructions
        assertTrue(output.contains("Player A: you are going to place the following ships"));
        assertTrue(output.contains("Player B: you are going to place the following ships"));
        // Check ship placement prompts
        assertTrue(output.contains("Player A where do you want to place a Submarine?"));
        assertTrue(output.contains("Player A where do you want to place a Destroyer?"));
        assertTrue(output.contains("Player A where do you want to place a Battleship?"));
        assertTrue(output.contains("Player A where do you want to place a Carrier?"));
        assertTrue(output.contains("Player B where do you want to place a Submarine?"));
        assertTrue(output.contains("Player B where do you want to place a Destroyer?"));
        assertTrue(output.contains("Player B where do you want to place a Battleship?"));
        assertTrue(output.contains("Player B where do you want to place a Carrier?"));
        // Check placement coordinates
        assertTrue(output.contains("A0H"));
        assertTrue(output.contains("B0H"));
        assertTrue(output.contains("C0H"));
        assertTrue(output.contains("D0H"));
        assertTrue(output.contains("E0H"));
        assertTrue(output.contains("F0U"));
        assertTrue(output.contains("G0U"));
        assertTrue(output.contains("H0U"));
        assertTrue(output.contains("I0U"));
        assertTrue(output.contains("J0U"));
    }

}
