package columnchange;

import columnchange.algorithm.ArrayTransform;
import org.junit.Test;

import static columnchange.algorithm.ArrayTransform.*;
import static org.junit.Assert.*;

import java.util.Arrays;

public class TestArrayTransform {

  @Test
  public void testShuffle() {
    int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    shuffle(a);
    assertArrayEquals(b, a);
    a = shuffle(a);
    System.out.println(Arrays.toString(a));
    a = shuffle(a);
    System.out.println(Arrays.toString(a));
  }

  @Test
  public void testRangeShuffle() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    ArrayTransform.shuffle(a, 3, 5);
    assertArrayEquals(b, a);

    a = ArrayTransform.shuffle(a, 3, 5);
    for (int i = 0; i < 3; i++)
      assertEquals(b[i], a[i]);
    for (int i = 8; i < b.length; i++)
      assertEquals(b[i], a[i]);

    System.out.println(Arrays.toString(a));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRangeShuffleOutOfRange() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    ArrayTransform.shuffle(a, 3, 10);
  }


  @Test
  public void testRandom() {
    int[] arr = random(4);
    System.out.println(Arrays.toString(arr));
  }

  @Test
  public void testSwap() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    swap(a, 1, 2);
    assertArrayEquals(b, a);
    int[] c = {0, 1, 7, 3, 4, 5, 6, 2, 8, 9};
    a = swap(a, 2, 7);
    assertArrayEquals(c, a);
  }


  @Test
  public void testInsertBefore() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    insertBefore(a, 5, 1);
    assertArrayEquals(b, a);
    a = insertBefore(a, 1, 2);
    assertArrayEquals(b, a);
    a = insertBefore(a, 4, 4);
    assertArrayEquals(b, a);
    a = insertBefore(a, 5, 1);
    int[] c = {0, 5, 1, 2, 3, 4, 6, 7, 8, 9};
    assertArrayEquals(c, a);
    b = insertBefore(b, 6, 0);
    int[] d = {6, 0, 1, 2, 3, 4, 5, 7, 8, 9};
    assertArrayEquals(d, b);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInsertBeforeInvalidIndex() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    insertBefore(a, 5, 10);
  }

  @Test
  public void testInsertAfter() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    insertAfter(a, 5, 1);
    assertArrayEquals(b, a);
    a = insertAfter(a, 2, 1);
    assertArrayEquals(b, a);
    a = insertAfter(a, 4, 4);
    assertArrayEquals(b, a);
    a = insertAfter(a, 5, 1);
    int[] c = {0, 1, 5, 2, 3, 4, 6, 7, 8, 9};
    assertArrayEquals(c, a);
    b = insertAfter(b, 6, 9);
    int[] d = {0, 1, 2, 3, 4, 5, 7, 8, 9, 6};
    assertArrayEquals(d, b);
  }

  @Test
  public void testReverse() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    reverse(a);
    assertArrayEquals(b, a);
    int[] c = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
    a = reverse(a);
    assertArrayEquals(c, a);
  }

  @Test
  public void testRangeReverse() {
    int[] a = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    int[] b = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    reverse(a, 4, 4);
    assertArrayEquals(a, b);
    a = reverse(a, 4, 4);
    int[] c = {0, 1, 2, 3, 7, 6, 5, 4, 8, 9};
    assertArrayEquals(c, a);
  }
}
