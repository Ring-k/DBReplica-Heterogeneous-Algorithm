package heterogeneous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayTransform {

  private ArrayTransform() {
  }

  /**
   * Transform an array of Integer to an array of int
   *
   * @param a, an array of Integer
   * @return an array of int
   */
  private static int[] integerToInt(Integer[] a) {
    int[] res = new int[a.length];
    for (int i = 0; i < a.length; i++) res[i] = a[i];
    return res;
  }

  /**
   * Transform an array of int to an array of Integer
   *
   * @param a, an array of int
   * @return an array of integer
   */
  private static Integer[] intToInteger(int[] a) {
    Integer[] res = new Integer[a.length];
    for (int i = 0; i < a.length; i++) res[i] = a[i];
    return res;
  }

  /**
   * totally shuffle the array
   *
   * @param arr, the original array
   * @return an array after shuffling
   */
  public static int[] shuffle(int[] arr) {
    List<Integer> ls = Arrays.asList(intToInteger(arr));
    Collections.shuffle(ls);
    return integerToInt(ls.toArray(new Integer[0]));
  }

  /**
   * Shuffle part of array
   *
   * @param a,      array
   * @param p,      start position
   * @param length, length of the part
   * @return shuffled array
   */
  public static int[] shuffle(int[] a, int p, int length) {
    Integer[] arr = intToInteger(a);
    if (length < 1 || length + p > arr.length || p < 0 || p >= arr.length)
      throw new IllegalArgumentException();
    if (length == 1) return integerToInt(arr);
    Integer[] temp = new Integer[length];
    System.arraycopy(arr, p, temp, 0, length);
    temp = intToInteger(shuffle(integerToInt(temp)));
    System.arraycopy(temp, 0, arr, p, length);
    return integerToInt(arr);
  }

  /**
   * Generate a random order
   *
   * @param n, the number of integer in the array, or upper bound {exclusive}
   * @return an array in random order
   */
  public static int[] random(int n) {
    List<Integer> ls = new ArrayList<>();
    for (int i = 0; i < n; i++) ls.add(i);
    Collections.shuffle(ls);
    return integerToInt(ls.toArray(new Integer[0]));
  }

  /**
   * swap the i-th and j-th element in the array
   *
   * @param a, a array of integer
   * @param i, position i
   * @param j, position j
   * @return new array
   */
  public static int[] swap(int[] a, int i, int j) {
    if (i < 0 || j < 0 || i >= a.length || j >= a.length)
      throw new IllegalArgumentException();
    if (i == j) return a;
    int[] arr = new int[a.length];
    System.arraycopy(a, 0, arr, 0, a.length);
    Integer temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
    return arr;
  }


  /**
   * Move index-i element before index-j element
   *
   * @param a, an array of integer
   * @param i, index i
   * @param j, index j
   * @return new array
   */
  public static int[] insertBefore(int[] a, int i, int j) {
    int[] arr = new int[a.length];
    System.arraycopy(a, 0, arr, 0, a.length);
    if (i < 0 || j < 0 || i >= arr.length || j >= arr.length)
      throw new IllegalArgumentException();
    if (i - j == -1 || i == j) return arr;
    if (i - j == 1) return swap(arr, i, j);
    Integer temp = arr[i];
    if (i > j) {
      System.arraycopy(arr, j, arr, j + 1, i - j);
      arr[j] = temp;
    } else {
      return insertAfter(arr, i, j - 1);
    }
    return arr;
  }

  /**
   * Insert the index-i element before index-j element
   *
   * @param a, the array
   * @param i, index i
   * @param j, index j
   * @return new array
   */
  public static int[] insertAfter(int[] a, int i, int j) {
    int[] arr = new int[a.length];
    System.arraycopy(a, 0, arr, 0, a.length);
    if (i < 0 || j < 0 || i >= arr.length || j >= arr.length)
      throw new IllegalArgumentException();
    if (i - j == 1 || i == j) return arr;
    Integer temp = arr[i];
    if (i > j)
      return insertBefore(arr, i, j + 1);
    else {
      System.arraycopy(arr, i + 1, arr, i, j - i);
      arr[j] = temp;
    }
    return arr;
  }

  /**
   * Totally reverse the array
   *
   * @param arr, original array
   * @return new array after reversing
   */
  public static int[] reverse(int[] arr) {
    List<Integer> list = Arrays.asList(intToInteger(arr));
    Collections.reverse(list);
    return integerToInt(list.toArray(new Integer[0]));
  }

  /**
   * Reverse part of the array
   *
   * @param a,      original array
   * @param p,      index that start reversing
   * @param length, length of reversing range
   * @return new array after part reversed
   */
  public static int[] reverse(int[] a, int p, int length) {
    Integer[] arr = intToInteger(a);
    if (length < 1 || length + p > arr.length || p < 0 || p >= arr.length)
      throw new IllegalArgumentException();
    if (length == 1) return integerToInt(arr);
    Integer[] temp = new Integer[length];
    System.arraycopy(arr, p, temp, 0, length);
    temp = intToInteger(reverse(integerToInt(temp)));
    System.arraycopy(temp, 0, arr, p, length);
    return integerToInt(arr);
  }


}
