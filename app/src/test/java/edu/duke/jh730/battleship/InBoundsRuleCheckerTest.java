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
    assertTrue(checker.checkPlacement(s1, b));
    
    // Test invalid placements
    Ship<Character> s2 = f.makeSubmarine(new Placement("Z0V")); // off bottom
    assertFalse(checker.checkPlacement(s2, b));
    
    Ship<Character> s3 = f.makeSubmarine(new Placement("A9H")); // off right
    assertFalse(checker.checkPlacement(s3, b));
    
    Ship<Character> s4 = f.makeSubmarine(new Placement("A0H")); // valid
    assertTrue(checker.checkPlacement(s4, b));

    // Test negative row
    Placement p1 = new Placement(new Coordinate(-1, 0), 'V');
    Ship<Character> s5 = f.makeSubmarine(p1);
    assertFalse(checker.checkPlacement(s5, b));

    // Test negative column
    Placement p2 = new Placement(new Coordinate(0, -1), 'H');
    Ship<Character> s6 = f.makeSubmarine(p2);
    assertFalse(checker.checkPlacement(s6, b));
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
    assertTrue(firstChecker.checkPlacement(s1, b));
    
    // Test invalid placement through chain
    Ship<Character> s2 = f.makeSubmarine(new Placement("Z0V")); // off bottom
    assertFalse(firstChecker.checkPlacement(s2, b));
  }
} 
