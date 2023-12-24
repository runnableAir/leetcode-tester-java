package leetcode.husky.test.util;

import util.husky.array.ArrayStringUtil;

import java.util.List;

public class ArgumentParseUtil {

    public static List<String> getStringList(String arrayLiked) {
        return ArrayStringUtil.getStringList(arrayLiked);
    }

    public static List<List<String>> getString2dList(String arrayLiked) {
        return ArrayStringUtil.getString2dList(arrayLiked);
    }

    public static List<Integer> getIntList(String arrayLiked) {
        return ArrayStringUtil.getIntList(arrayLiked);
    }

    public static List<List<Integer>> getInt2dList(String arrayLiked) {
        return ArrayStringUtil.getInt2dList(arrayLiked);
    }

    public static String[] getStringArray(String arrayLiked) {
        return ArrayStringUtil.getStringArray(arrayLiked);
    }

    public static String[][] getString2dArray(String arrayLiked) {
        return ArrayStringUtil.getString2dArray(arrayLiked);
    }

    public static int[] getIntArray(String arrayLiked) {
        return ArrayStringUtil.getIntArray(arrayLiked);
    }

    public static int[][] getInt2dArray(String arrayLiked) {
        return ArrayStringUtil.getInt2dArray(arrayLiked);
    }

    public static String removeRedundantQuote(String s) {
        if (s.length() < 2) {
            return s;
        }
        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
