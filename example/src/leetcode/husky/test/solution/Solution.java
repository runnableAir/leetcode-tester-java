package leetcode.husky.test.solution;

import java.util.Arrays;

class Solution {
    public int method1(int[] param1, int param2) {
        return param2 + Arrays.stream(param1).sum();
    }
}
