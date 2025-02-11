package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;



public class InBoundsRuleCheckerTest {
  @Test
  public void test_check_in_bounds() {
    Board<Character> b = new BattleShipBoard<Character>(10, 20);
    V1ShipFactory f = new V1ShipFactory();
    InBoundsRuleChecker<Character> checker = new InBoundsRuleChecker<>(null);
    
    // Test valid placement
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertNull(checker.checkPlacement(s1, b));
    
    // Test invalid placements
    Ship<Character> s2 = f.makeSubmarine(new Placement("Z0V")); // off bottom
    assertEquals("That placement is invalid: the ship goes off the bottom of the board.",
                checker.checkPlacement(s2, b));
    
    Ship<Character> s3 = f.makeSubmarine(new Placement("A9H")); // off right
    assertEquals("That placement is invalid: the ship goes off the right of the board.",
                checker.checkPlacement(s3, b));
    
    Ship<Character> s4 = f.makeSubmarine(new Placement("A0H")); // valid
    assertNull(checker.checkPlacement(s4, b));

    // Test negative row
    Placement p1 = new Placement(new Coordinate(-1, 0), 'V');
    Ship<Character> s5 = f.makeSubmarine(p1);
    assertEquals("That placement is invalid: the ship goes off the top of the board.",
                checker.checkPlacement(s5, b));

    // Test negative column
    Placement p2 = new Placement(new Coordinate(0, -1), 'H');
    Ship<Character> s6 = f.makeSubmarine(p2);
    assertEquals("That placement is invalid: the ship goes off the left of the board.",
                checker.checkPlacement(s6, b));
  }

  @Test
  public void test_rule_chain() {
    Board<Character> b = new BattleShipBoard<Character>(10, 20);
    V1ShipFactory f = new V1ShipFactory();
    
    // Create a rule chain: InBoundsRuleChecker -> InBoundsRuleChecker
    InBoundsRuleChecker<Character> secondChecker = new InBoundsRuleChecker<>(null);
    InBoundsRuleChecker<Character> firstChecker = new InBoundsRuleChecker<>(secondChecker);
    
    // Test valid placement through chain
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertNull(firstChecker.checkPlacement(s1, b));
    
    // Test invalid placement through chain
    Ship<Character> s2 = f.makeSubmarine(new Placement("Z0V")); // off bottom
    assertEquals("That placement is invalid: the ship goes off the bottom of the board.",
                firstChecker.checkPlacement(s2, b));
  }

  @Test
  public void test_check_my_rule() {
    Board<Character> b = new BattleShipBoard<Character>(10, 20);
    V1ShipFactory f = new V1ShipFactory();
    InBoundsRuleChecker<Character> checker = new InBoundsRuleChecker<>(null);
    
    // valid 
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertNull(checker.checkMyRule(s1, b));
    
    // off top
    Placement p1 = new Placement(new Coordinate(-1, 0), 'V');
    Ship<Character> s2 = f.makeSubmarine(p1);
    assertEquals("That placement is invalid: the ship goes off the top of the board.",
                checker.checkMyRule(s2, b));
    
    // off bottom
    Ship<Character> s3 = f.makeSubmarine(new Placement("Z0V"));
    assertEquals("That placement is invalid: the ship goes off the bottom of the board.",
                checker.checkMyRule(s3, b));
    
    // off left
    Placement p2 = new Placement(new Coordinate(0, -1), 'H');
    Ship<Character> s4 = f.makeSubmarine(p2);
    assertEquals("That placement is invalid: the ship goes off the left of the board.",
                checker.checkMyRule(s4, b));
    
    // off right
    Ship<Character> s5 = f.makeSubmarine(new Placement("A9H"));
    assertEquals("That placement is invalid: the ship goes off the right of the board.",
                checker.checkMyRule(s5, b));
  }
} 
