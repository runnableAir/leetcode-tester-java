package util.husky.array;

import leetcode.husky.test.util.ArgumentParseUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;

public class ArrayStringUtil {
    static final int INT_TYPE = 0x1;
    static final int INT64_TYPE = 0x2;
    static final int NUMBER_TYPE = 0x3;
    static final int STRING_TYPE = 0x4;


    public static List<String> getStringList(String s) {
        ArrayNode arrayNode = buildArrayNode(s, 1, STRING_TYPE);
        return nodeToList(arrayNode, ArrayStringUtil::elementToString);
    }

    public static List<List<String>> getString2dList(String s) {
        ArrayNode arrayNode = buildArrayNode(s, 2, STRING_TYPE);
        return nodeTo2dList(arrayNode, ArrayStringUtil::elementToString);
    }

    public static List<Integer> getIntList(String s) {
        ArrayNode arrayNode = buildArrayNode(s, 1, INT_TYPE);
        return nodeToList(arrayNode, elementToNumberThen(Number::intValue));
    }

    public static List<List<Integer>> getInt2dList(String s) {
        ArrayNode arrayNode = buildArrayNode(s, 2, INT_TYPE);
        return nodeTo2dList(arrayNode, elementToNumberThen(Number::intValue));
    }

    public static List<Long> getLongList(String s) {
        ArrayNode arrayNode = buildArrayNode(s, 1, INT64_TYPE);
        return nodeToList(arrayNode, elementToNumberThen(Number::longValue));
    }

    public static List<List<Long>> getLong2dList(String s) {
        ArrayNode arrayNode = buildArrayNode(s, 2, INT64_TYPE);
        return nodeTo2dList(arrayNode, elementToNumberThen(Number::longValue));
    }

    public static String[] getStringArray(String s) {
        return stringListToArray(getStringList(s));
    }

    public static String[][] getString2dArray(String s) {
        return getString2dList(s).stream()
                .map(ArrayStringUtil::stringListToArray)
                .toArray(String[][]::new);
    }

    public static int[] getIntArray(String s) {
        return intListToArray(getIntList(s));
    }

    public static int[][] getInt2dArray(String s) {
        return getInt2dList(s).stream()
                .map(ArrayStringUtil::intListToArray)
                .toArray(int[][]::new);
    }

    public static long[] getLongArray(String s) {
        return longListToArray(getLongList(s));
    }

    public static long[][] getLong2dArray(String s) {
        return getLong2dList(s).stream()
                .map(ArrayStringUtil::longListToArray)
                .toArray(long[][]::new);
    }

    /**
     * Transforming a 2D array represented as a JSON string
     * into a list, where each inner list contains the literals
     * of the respective elements
     *
     * @param s string representing a 2D array
     * @return 2D list contains the literals of the respective elements
     */
    public static List<List<String>> parse2dArrayAsList(String s) {
        return mapNodeToXXList(
                buildAnyArrayNode(s, 2, 4, STRING_TYPE | NUMBER_TYPE),
                child -> mapNodeToXXList(child, ArrayNode::asString)
        );
    }

   private static <T> List<T> mapNodeToXXList(ArrayNode arrayNode, Function<ArrayNode, T> mapper) {
        return arrayNode.getChildren().stream().map(mapper).toList();
   }

    private static String[] stringListToArray(List<String> list) {
        return list.toArray(String[]::new);
    }

    private static int[] intListToArray(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private static long[] longListToArray(List<Long> list) {
        return list.stream().mapToLong(Long::longValue).toArray();
    }

    private static String elementToString(ElementNode<?> elementNode) {
        if (elementNode instanceof StringNode node) {
            return node.getVal();
        }
        throw new IllegalArgumentException(elementNode + " is not a StringNode");
    }

    private static <T> Function<ElementNode<?>, T> elementToNumberThen(Function<Number, T> thenFunc) {
        return elementNode -> thenFunc.apply(elementToNumber(elementNode));
    }

    private static Number elementToNumber(ElementNode<?> elementNode) {
        if (elementNode instanceof NumberNode node) {
            return node.getVal();
        }
        throw new IllegalStateException(elementNode + " is not a NumberNode");
    }

    private static <T> List<T> nodeToList(ArrayNode arrayNode, Function<ElementNode<?>, T> elementConverter) {
        return arrayNode.getChildren()
                .stream()
                .map(node -> (ElementNode<?>) node)
                .map(elementConverter)
                .toList();
    }

    private static <T> List<List<T>> nodeTo2dList(ArrayNode arrayNode, Function<ElementNode<?>, T> elementConverter) {
        return arrayNode.getChildren()
                .stream()
                .map(node -> nodeToList(node, elementConverter))
                .toList();
    }


    /**
     * Build & return an array node represented by the source
     * string {@code s}.
     * <p>
     * The returned array node may have an unspecified number of
     * dimensions and may contain elements of an unspecified type
     * (string or number), allowing for flexibility in representing
     * array data.
     *
     * @param s the source string to express an array structure data
     * @return an array node object represented by the source string
     * {@code s}
     */
    static ArrayNode buildAnyArrayNode(String s) {
        return buildAnyArrayNode(s, 1, 0x3f3f, STRING_TYPE | NUMBER_TYPE);
    }

    /**
     * Build & return an array node represented by the source
     * string {@code s}
     *
     * @param s the source string to express an array structure data
     * @param minDimension the mini dimension the array should have
     * @param maxDimension the max dimension the array should have
     * @param allowElementType the type of element which is allowed
     * @return an array node object represented by the source string
     * {@code s}
     */
    static ArrayNode buildAnyArrayNode(String s, int minDimension, int maxDimension, int allowElementType) {
        if (!(minDimension <= maxDimension)) {
            throw new IllegalArgumentException("minDimension(=%d) should be less than or equal to maxDimension(=%d)"
                    .formatted(minDimension, maxDimension));
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException("can not convert the string as an array because it is empty");
        }
        int len = s.length();
        if (s.charAt(0) != '[' || s.charAt(len - 1) != ']') {
            throw new IllegalArgumentException(
                    "can not convert the string as an array because it isn't wrapped in \"[]\""
            );
        }

        Deque<ArrayNode> stk = new ArrayDeque<>();
        ArrayNode root = new ArrayNode();
        ArrayNode cur = root;
        stk.push(root);
        int curDimension = 1;
        int i = 1;
        // each loop we scan and make a node
        while (i < len && curDimension > 0 && curDimension <= maxDimension) {
            char c;
            // skip white spaces
            if (Character.isWhitespace(c = s.charAt(i))) {
                i++;
                continue;
            }
            // into a new array (push node)
            if (c == '[') {
                curDimension++;
                // new child node
                ArrayNode child = new ArrayNode();
                cur.appendChild(child);
                cur = child;
                stk.push(child);
                i++;
                continue;
            }

            /*
             * handle string or number element node if the array
             * IS NOT empty.
             *
             * if the array IS NOT empty:
             * (1) we already scanned some element nodes of current node
             * (2) or we scanned no element node, and we don't meet the
             *     end char(']') which means no element node found
             */
            if (!cur.getChildren().isEmpty() || c != ']') {
                // ready to handle an element node but check
                // if current dimension is enough at first
                if (curDimension < minDimension) {
                    throw new IllegalArgumentException(
                            "expected '[' but found '%s' at %d pos, because the array should be %d dimension"
                                    .formatted(c, i, minDimension)
                    );
                }
                i = scanOneElement(i, s, cur, allowElementType);
            }

            // skip white spaces
            while (i < len && Character.isWhitespace(c = s.charAt(i))) {
                i++;
            }
            // expected ',' or ']'
            if (i == len) {
                throw new IllegalArgumentException("expected ',' or ']' but noting found");
            }
            if (c != ',' && c != ']') {
                throw new IllegalArgumentException("expected ',' or ']' at %d pos but found '%s'".formatted(i, c));
            }

            // handle all end char(']') if we meet
            // and go to next sep char(',')
            while (i < len && curDimension > 0 && c == ']') {
                curDimension--;
                i++;
                stk.pop();
                // cur = stk.peek();
                if (!stk.isEmpty()) {
                    // actually we don't need to check whether stack
                    // is empty, here is for IDE code checking for NPE
                    cur = stk.peek();
                }
                // skip white spaces
                while (i < len && Character.isWhitespace(c = s.charAt(i))) {
                    i++;
                }
            }
            if (i == len) {
                break;
            }
            // expected ','
            if (c != ',') {
                throw new IllegalArgumentException("expected ',' at %d pos but found '%s'".formatted(i, c));
            }
            i++;
        }

        if (curDimension > maxDimension) {
            throw new IllegalArgumentException("too many '[' at %d pos that make array dimension > limit(=%d)"
                    .formatted(i, maxDimension));
        }
        if (curDimension > 0) {
            throw new IllegalArgumentException("expected ']' but the array is end");
        }
        if (i < len) {
            throw new IllegalArgumentException("the array is end but found '%s'...".formatted(s.charAt(i)));
        }
        return root;
    }

    /**
     * Build & return an array node represented by the source
     * string {@code s}。
     * <p>
     * The returned array have a specified number of dimensions and
     * contain elements of specified type.
     *
     * @param s the source string to express an array structure data
     * @param dimension specified number of dimensions
     * @param allowElementType the specified type of elements (string or number)
     * @return an array node object represented by the source string
     * {@code s}
     */
    static ArrayNode buildArrayNode(String s, int dimension, int allowElementType) {
        return buildAnyArrayNode(s, dimension, dimension, allowElementType);
    }

    private static int scanOneElement(int begin, String s, ArrayNode parent, int allowElementType) {
        int i = begin;
        char c = s.charAt(i);
        if (c == '"') {
            if ((allowElementType & STRING_TYPE) == 0) {
                throw new IllegalArgumentException("the \"string\" element is not allowed");
            }
            String val = s.substring(i, (i = scanString(i, s)));
            parent.appendChild(new StringNode(val));
        } else if (c == '0' || c == '-' || ('1' <= c && c <= '9')) {
            if ((allowElementType & NUMBER_TYPE) == 0) {
                throw new IllegalArgumentException("the \"number\" element is not allowed");
            }
            String val = s.substring(i, (i = scanNumber(i, s)));
            parent.appendChild(new NumberNode(val));
        } else {
            throw new IllegalArgumentException(
                    "expected '\"' or '-' or '0'~'9' at %d pos but found '%s'".formatted(i, c)
            );
        }
        return i;
    }

    private static int scanNumber(int being, String s) {
        int i = being, len = s.length();
        char c = s.charAt(i);
        if (c == '-' && i < len - 1) {
            c = s.charAt(++i);
        }
        if (!Character.isDigit(c)) {
            throw new IllegalArgumentException("expected '0'~'9' at %d pos but found '%s'".formatted(i, c));
        }
        if (c == '0' && i < len - 1 && Character.isDigit(s.charAt(++i))) {
            throw new IllegalArgumentException("expected '1'~'9' at %d pos but found 0".formatted(i));
        }
        while (i < len && Character.isDigit(s.charAt(i))) {
            i++;
        }
        return i;
    }

    private static int scanString(int begin, String s) {
        int i = begin + 1, len = s.length();
        while (i < len && s.charAt(i) != '"') {
            if (s.charAt(i) == '\\') {
                i = scanEscapedChars(i, s);
            } else {
                i++;
            }
        }
        if (i == len) {
            throw new IllegalArgumentException(
                    "expected '%s' at %d pos when handling the string(%s) beginning at '%d' pos"
                            .formatted('"', i, s.substring(begin, i), begin)
            );
        }
        return i + 1;
    }

    private static int scanEscapedChars(int begin, String s) {
        int i = begin + 1, len = s.length();
        if (i == len) {
            throw new IllegalArgumentException(
                    "expected one char after char '\\' at pos " + i + " but nothing");
        }
        char ch = s.charAt(i);
        String specialChars = "\"/\\nturbf";
        if (specialChars.indexOf(ch) == -1) {
            throw new IllegalArgumentException(
                    "expected one char ('\"' or '/' or '\\' or 'n' or 't' or 'u' or 'r' or 'b' or 'f') " +
                            "after char '\\' at pos i");
        }
        if (ch != 'u') {
            return begin + 2;
        }
        if (begin >= len - 6) {
            throw new IllegalArgumentException(
                    "requires at least 4 chars to represent a valid hex code from pos " + i
            );
        }
        i++;
        int hexDigitsCnt = 0;
        while (i < len && hexDigitsCnt < 5) {
            ch = s.charAt(i);
            if (Character.digit(ch, 16) == -1) {
                break;
            }
            hexDigitsCnt++;
            i++;
        }
        if (hexDigitsCnt < 4) {
            throw new IllegalArgumentException(
                    "invalid hex char at pos " + i + ": " + ch
            );
        }
        if (hexDigitsCnt > 5) {
            throw new IllegalArgumentException("too many hex digits at pos " + i);
        }
        return i;
    }
}

class NumberNode extends ElementNode<Number> {

    NumberNode(String val) {
        super(val);
    }

    @Override
    protected Number parse(String val) {
        return Long.valueOf(val);
    }

    @Override
    public String toString() {
        return getVal().toString() + "(Number)";
    }

    @Override
    public String asString() {
        return getVal().toString();
    }
}

class StringNode extends ElementNode<String> {

    StringNode(String val) {
        super(val);
    }

    @Override
    protected String parse(String val) {
        return ArgumentParseUtil.getString(val);
    }

    @Override
    public String toString() {
        return getVal() + "(String)";
    }

    @Override
    public String asString() {
        String val = getVal();
        int len = val.length();
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i = 0; i < len; i++) {
            char ch = val.charAt(i);
            if ("\"\\\n\t\r\b\f".indexOf(ch) != -1) {
                sb.append('\\');
            }
            sb.append(ch);
        }
        return sb.append('"').toString();
    }
}
