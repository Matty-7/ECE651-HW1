package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    public void test_app_creation() {
        App app = new App();
        assertNotNull(app);
    }
}
