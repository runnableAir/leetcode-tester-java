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

    public static String getString(String val) {
        int len = val.length();
        if (val.charAt(0) != '"' && val.charAt(len - 1) != '"') {
            throw new IllegalStateException("can not parse '%s' to string val because it isn't wrapped by '\"'");
        }
        StringBuilder sb = new StringBuilder();
        len--;
        for (int i = 1; i < len; i++) {
            char ch = val.charAt(i);
            if (ch != '\\') {
                sb.append(ch);
                continue;
            }
            if (i == len - 1) {
                throw new IllegalStateException("For input String %s, invalid char at pos %d: %c"
                        .formatted(val, i, '\\'));
            }
            ch = val.charAt(++i);
            int codePoint = switch (ch) {
                case '"' -> '"';   // "
                case '\\' -> '\\'; // \
                case '/' -> '/';   // /
                case 'n' -> '\n';  // \n
                case 't' -> '\t';  // \t
                case 'b' -> '\b';  // \b
                case 'f' -> '\f';  // \f
                case 'r' -> '\r';  // \r
                case 'u' -> {      // unicode (eg: \u0000)
                    int hexVal = 0;
                    int end = i + 4; // 4 chars for hex digits
                    if (end >= len) {
                        yield -1;
                    }
                    for (int j = i + 1; j <= end; j++) {
                        char hexChar = val.charAt(j);
                        int d = Character.digit(hexChar, 16);
                        if (d == -1) {
                            yield -1;
                        }
                        hexVal = (hexVal << 4) | d;
                    }
                    i = end;
                    yield hexVal;
                }
                default -> -1;
            };
            if (codePoint == -1) {
                throw new IllegalStateException(
                        "For input String %s, invalid char from pos %d: \\"
                                .formatted(val, i)
                );
            }
            sb.appendCodePoint(codePoint);
        }
        return sb.toString();
    }
}
