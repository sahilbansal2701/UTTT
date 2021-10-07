package edu.brown.cs.student.Pair;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

public class PairTest {
  private Pair<String, Double> pair1;
  private Pair<String, Double> pair2;
  private Pair<Pair<String, Double>, Double> pair21;
  private Pair<String, String> pair3;
  private Pair<Boolean, Integer> pair4;
  private String test1;
  private String test2;
  private Boolean test4;


  /**
   * Sets up some pairs.
   */
  @Before
  public void setUp() {
    test1 = "test1";
    test2 = "test2";
    test4 = true;
    pair1 = new Pair<>(test1, 60.0);
    pair2 = new Pair<>(test2, 132.0);
    pair21 = new Pair<>(pair2, 60.0);
    pair3 = new Pair<>(test2, "hi");
    pair4 = new Pair<>(test4, 5);
  }

  /**
   * Resets.
   */
  @After
  public void tearDown() {
    pair1 = null;
    pair2 = null;
    pair21 = null;
    pair3 = null;
    pair4 = null;
    test1 = null;
    test2 = null;
    test4 = null;
  }

  @Test
  public void testToString() {
    Pair<String, Double> p1 = new Pair<>("hi", 20.0);
    assertEquals(p1.toString(), "(hi , 20.0)");
    Pair<String, Integer> p2 = new Pair<>("hi", 40);
    assertEquals(p2.toString(), "(hi , 40)");
  }

  @Test
  public void testGetters() {
    setUp();
    assertEquals(pair1.getLeft(), test1);
    assertEquals(pair2.getLeft(), test2);
    assertEquals(pair3.getRight(), "hi");
    assertEquals(pair4.getLeft(), test4);
    tearDown();
  }

  @Test
  public void testEqualsAndHashcode() {
    setUp();
    assertTrue(pair1.equals(pair1));
    assertEquals(pair1.hashCode(), pair1.hashCode());

    assertFalse(pair1.equals(pair2));
    assertFalse(pair2.equals(pair1));
    assertNotEquals(pair1.hashCode(), pair2.hashCode());

    assertFalse(pair1.equals(pair21));
    assertFalse(pair21.equals(pair1));
    assertNotEquals(pair1.hashCode(), pair21.hashCode());

    assertFalse(pair2.equals(pair21));
    assertFalse(pair21.equals(pair2));
    assertNotEquals(pair2.hashCode(), pair21.hashCode());


    Pair<String, Double> pairAlpha = pair1;
    assertTrue(pairAlpha.equals(pair1));
    assertTrue(pair1.equals(pairAlpha));
    assertEquals(pairAlpha.hashCode(), pair1.hashCode());
    Pair<String, Double> pairBeta = pairAlpha;
    assertTrue(pairBeta.equals(pairAlpha));
    assertTrue(pairAlpha.equals(pairBeta));
    assertTrue(pair1.equals(pairBeta));
    assertEquals(pair1.hashCode(), pairAlpha.hashCode());
    assertEquals(pair1.hashCode(), pairBeta.hashCode());
    Pair<String, Double> pairDelta = new Pair<>(test1, 60.0);
    assertTrue(pair1.equals(pairDelta));
    assertEquals(pair1.hashCode(), pairDelta.hashCode());
  }
}
