package searchall;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Permutation {
  private static List<int[]> permutations = new ArrayList<>();
  private static Stack<Integer> s = new Stack<Integer>();
  private static boolean[] used = new boolean[10000];

  private Permutation(){}

  /**
   * Getting a list of permutation (array)
   *
   * @param min,      minimum value
   * @param max,      maximum value
   * @param num,      number of value in a permutation
   * @param allowDup, allow duplication or not
   * @return a list of permutations
   */
  public static List<int[]> getPerm(int min, int max, int num, boolean allowDup) {
    permutations = new ArrayList<>();
    s = new Stack<>();
    used = new boolean[10000];
    if (allowDup)
      runPermAllowDup(min, max, 0, num);
    else
      runPermNoDup(min, max, 0, num);
    return permutations;
  }


  /**
   * recursively generate new permutations, allowing duplication, and add the permutations to
   * the permutation list.
   *
   * @param minv,   minimum value of value, inclusive
   * @param maxv,   maximum value of value, inclusive
   * @param curnum, current number of value having been considered
   * @param maxnum, total value need to be considered
   */
  private static void runPermAllowDup(int minv, int maxv, int curnum, int maxnum) {
    if (curnum == maxnum) {
      int[] arr = new int[s.size()];
      for (int i = 0; i < arr.length; i++)
        arr[i] = (int) s.toArray()[i];
      permutations.add(arr);
      return;
    }
    for (int i = minv; i <= maxv; i++) {
      s.push(i);
      runPermAllowDup(i, maxv, curnum + 1, maxnum);
      s.pop();
    }
  }

  /**
   * Recursively generate new permutations, no duplication, and add the permutations to the
   * permutation list.
   *
   * @param minv,   minimum value, inclusive
   * @param maxv,   maximum value, inclusive
   * @param curnum, current number of value having been considered
   * @param maxnum, total number of value need to be considered
   */
  private static void runPermNoDup(int minv, int maxv, int curnum, int maxnum) {
    if (curnum == maxnum) {
      int[] arr = new int[s.size()];
      for (int i = 0; i < arr.length; i++) {
        arr[i] = (int) s.toArray()[i];
      }
      permutations.add(arr);
      return;
    }
    for (int i = minv; i <= maxv; i++) {
      if (!used[i]) {
        s.push(i);
        used[i] = true;
        runPermNoDup(minv, maxv, curnum + 1, maxnum);
        s.pop();
        used[i] = false;
      }
    }
  }
}
