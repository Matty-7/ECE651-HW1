package edu.duke.jh730.battleship;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NoCollisionRuleCheckerTest {
  @Test
  public void test_check_my_rule() {
    Board<Character> b = new BattleShipBoard<Character>(10, 20, 'X');
    V1ShipFactory f = new V1ShipFactory();
    NoCollisionRuleChecker<Character> checker = new NoCollisionRuleChecker<>(null);
    
    // Test valid placement on empty board
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertNull(checker.checkMyRule(s1, b));
    b.tryAddShip(s1);
    
    // Test collision
    Ship<Character> s2 = f.makeSubmarine(new Placement("A0H"));
    assertEquals("That placement is invalid: the ship overlaps another ship.",
                checker.checkMyRule(s2, b));
    
    // Test valid placement without collision
    Ship<Character> s3 = f.makeSubmarine(new Placement("C0V"));
    assertNull(checker.checkMyRule(s3, b));
  }

  @Test
  public void test_rule_chain() {
    PlacementRuleChecker<Character> noCollision = new NoCollisionRuleChecker<>(null);
    PlacementRuleChecker<Character> checker = new InBoundsRuleChecker<>(noCollision);
    Board<Character> b = new BattleShipBoard<Character>(10, 20, 'X');
    V1ShipFactory f = new V1ShipFactory();
    
    // Test valid placement through chain
    Ship<Character> s1 = f.makeSubmarine(new Placement("A0V"));
    assertNull(checker.checkPlacement(s1, b));
    b.tryAddShip(s1);
    
    // Test collision but in bounds
    Ship<Character> s2 = f.makeSubmarine(new Placement("A0H"));
    assertEquals("That placement is invalid: the ship overlaps another ship.",
                checker.checkPlacement(s2, b));
    
    // Test out of bounds (should fail at first checker)
    Ship<Character> s3 = f.makeSubmarine(new Placement("Z0V"));
    assertEquals("That placement is invalid: the ship goes off the bottom of the board.",
                checker.checkPlacement(s3, b));
  }
} 
