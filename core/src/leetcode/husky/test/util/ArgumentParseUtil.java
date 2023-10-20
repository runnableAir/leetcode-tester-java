package leetcode.husky.test.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArgumentParseUtil {

    public static List<String> getStringList(String arrayLiked) {
        String itemRegex = "\"((?:\\\\\"|[^\"])*)\"";
        checkArrayLiked(itemRegex, arrayLiked, "string");
        return getList(String.class, arrayLiked);
    }

    public static List<List<String>> getString2dList(String arrayLiked) {
        String itemRegex = makeArrayLikedRegex("\"((?:\\\\\"|[^\"])*)\"");
        checkArrayLiked(itemRegex, arrayLiked, "2d-string");
        return get2dList(String.class, arrayLiked);
    }

    public static List<Integer> getIntList(String arrayLiked) {
        checkArrayLiked("-?\\d+", arrayLiked, "int");
        return getList(Integer.class, arrayLiked);
    }

    public static List<List<Integer>> getInt2dList(String arrayLiked) {
        String itemRegex = makeArrayLikedRegex("-?\\d+");
        checkArrayLiked(itemRegex, arrayLiked, "2d-int");
        return get2dList(Integer.class, arrayLiked);
    }

    public static String[] getStringArray(String arrayLiked) {
        return getStringList(arrayLiked).toArray(String[]::new);
    }

    public static String[][] getString2dArray(String arrayLiked) {
        return getString2dList(arrayLiked)
                .stream()
                .map(list -> list.toArray(String[]::new))
                .toArray(String[][]::new);
    }

    public static int[] getIntArray(String arrayLiked) {
        return getIntList(arrayLiked)
                .stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    public static int[][] getInt2dArray(String arrayLiked) {
        return getInt2dList(arrayLiked)
                .stream()
                .map(List::stream)
                .map(stream -> stream.mapToInt(Integer::intValue))
                .map(IntStream::toArray)
                .toArray(int[][]::new);
    }

    static <T> List<T> getList(Class<T> elementType, String arrayLiked) {
        Objects.requireNonNull(elementType);
        Function<String, T> transformer = getTransformer(elementType);
        return splitArrayLiked(arrayLiked)
                .map(transformer)
                .toList();
    }

    static <T> List<List<T>> get2dList(Class<T> elementType, String _2dArrayLiked) {
        return splitArrayLiked(_2dArrayLiked)
                .map(a -> getList(elementType, a))
                .toList();
    }

    static void checkArrayLiked(String itemRegex, String arrayLiked, String expectedElementType) {
        if (arrayLiked == null) {
            throw new IllegalArgumentException("\"arrayLiked\" can not be null");
        }
        if (itemRegex == null) {
            throw new IllegalStateException("\"itemRegex\" can not be null");
        }
        String regex = makeArrayLikedRegex(itemRegex);
        if (!arrayLiked.matches(regex)) {
            throw new IllegalArgumentException(
                    "\"" + arrayLiked + "\" is a invalid string for " + expectedElementType + " array");
        }
    }

    /**
     * Return a regular expression of an array formed by the regular
     * expression of its elements.
     *
     * @param element the regular expression of the element
     * @return a regular expression of an array
     */
    static String makeArrayLikedRegex(String element) {
        return "\\[((" + element + ")?|(" + element + "(,\\s*" + element + ")+))]";
    }

    private static Stream<String> splitArrayLiked(String arrayLiked) {
        int prefix = 0;
        int length = arrayLiked.length();
        while (prefix < length) {
            if (arrayLiked.charAt(prefix) != '[') break;
            prefix++;
        }
        int innerBraceCnt = prefix - 1;
        arrayLiked = arrayLiked.substring(prefix, length - prefix);
        if (arrayLiked.isEmpty()) {
            return Stream.empty();
        }
        String[] split = arrayLiked.split("]{%d},\\s*\\[{%1$d}".formatted(innerBraceCnt));
        String left = "[".repeat(innerBraceCnt);
        String right = "]".repeat(innerBraceCnt);
        return Arrays.stream(split).map(item -> left + item + right);
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

    @SuppressWarnings("unchecked")
    static <T> Function<String, T> getTransformer(Class<T> elementType) {
        String simpleName = elementType.getSimpleName();
        // TODO add more enhance condition
        if ("String".equals(simpleName)) {
            return str -> (T) removeRedundantQuote(str);
        } else if ("Integer".equals(simpleName)) {
            return str -> (T) Integer.valueOf(str);
        }
        throw new IllegalStateException("unsupported element type : \"" + elementType + "\"");
    }
}
