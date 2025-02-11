package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NoCollisionRuleCheckerTest {
  @Test
  public void test_no_collision() {
    NoCollisionRuleChecker<Character> checker = new NoCollisionRuleChecker<>(null);
    Board<Character> b = new BattleShipBoard<Character>(10, 20, checker);
    V1ShipFactory f = new V1ShipFactory();
    
    // Test valid placement on empty board
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertTrue(checker.checkPlacement(s1, b));
    b.tryAddShip(s1);

    // Test collision
    Ship<Character> s2 = f.makeSubmarine(new Placement("A0H"));
    assertFalse(checker.checkPlacement(s2, b));

    // Test valid placement without collision
    Ship<Character> s3 = f.makeSubmarine(new Placement("C0V"));
    assertTrue(checker.checkPlacement(s3, b));
  }

  @Test
  public void test_rule_chain() {
    // Create rule chain: InBounds -> NoCollision -> null
    PlacementRuleChecker<Character> noCollision = new NoCollisionRuleChecker<>(null);
    PlacementRuleChecker<Character> checker = new InBoundsRuleChecker<>(noCollision);
    Board<Character> b = new BattleShipBoard<Character>(10, 20, checker);
    V1ShipFactory f = new V1ShipFactory();
    
    // Test valid placement through chain
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertTrue(checker.checkPlacement(s1, b));
    b.tryAddShip(s1);

    // Test collision but in bounds
    Ship<Character> s2 = f.makeSubmarine(new Placement("A0H"));
    assertFalse(checker.checkPlacement(s2, b));

    // Test out of bounds (should fail at first checker)
    Ship<Character> s3 = f.makeSubmarine(new Placement("Z0V"));
    assertFalse(checker.checkPlacement(s3, b));
  }
} 
