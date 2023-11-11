package leetcode.husky.test.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ArgumentParseUtil {

    public static List<String> getStringList(String arrayLiked) {
        return getList(arrayLiked, STRING_TYPE);
    }

    public static List<List<String>> getString2dList(String arrayLiked) {
        return get2dList(arrayLiked, STRING_TYPE);
    }

    public static List<Integer> getIntList(String arrayLiked) {
        return getList(arrayLiked, INT_TYPE);
    }

    public static List<List<Integer>> getInt2dList(String arrayLiked) {
        return get2dList(arrayLiked, INT_TYPE);
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

    static <T> List<T> getList(String src, int elementType) {
        TreeNode treeNode = buildTree(src, 1, elementType);
        Function<LeafNode<?>, T> mapper = getLeafMapper(elementType);
        return getListFromTreeNode(treeNode, mapper);
    }

    static <T> List<List<T>> get2dList(String src, int elementType) {
        TreeNode treeNode = buildTree(src, 2, elementType);
        Function<LeafNode<?>, T> mapper = getLeafMapper(elementType);
        return treeNode.getChildren()
                .stream()
                .map(node -> getListFromTreeNode(node, mapper))
                .toList();
    }

    static <T> List<T> getListFromTreeNode(TreeNode node, Function<LeafNode<?>, T> leafMapper) {
        List<TreeNode> children = node.getChildren();
        try {
            return children.stream()
                    .map(treeNodeCastToLeafNodeFunc())
                    .map(leafMapper)
                    .toList();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("all child must be an instance of LeafNode<?>", e);
        }
    }

    static Function<TreeNode, LeafNode<?>> treeNodeCastToLeafNodeFunc() {
        return node -> {
            Objects.requireNonNull(node);
            if (node instanceof LeafNode<?> leaf) {
                return leaf;
            }
            throw new IllegalStateException("node is not a leaf node: " + node);
        };
    }

    static Function<LeafNode<?>, String> getStringNodeValueFunc() {
        return leafNode -> {
            Objects.requireNonNull(leafNode);
            Object val = leafNode.getVal();
            return val.toString();
        };
    }

    static Function<LeafNode<?>, Number> getNumberNodeValueFunc() {
        return leafNode -> {
            Objects.requireNonNull(leafNode);
            if (leafNode instanceof NumberNode node) {
                return node.getVal();
            }
            throw new IllegalStateException();
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> Function<LeafNode<?>, T> getLeafMapper(int elementType) {
        return switch (elementType) {
            case INT_TYPE -> (Function<LeafNode<?>, T>) getNumberNodeValueFunc().andThen(Number::intValue);
            case INT64_TYPE -> (Function<LeafNode<?>, T>) getNumberNodeValueFunc().andThen(Number::longValue);
            case STRING_TYPE -> (Function<LeafNode<?>, T>) getStringNodeValueFunc().andThen(ArgumentParseUtil::removeRedundantQuote);
            default -> throw new IllegalStateException("Unexpected value: " + elementType);
        };
    }

    public static final int INT_TYPE = 0x1;
    public static final int INT64_TYPE = 0x2;
    public static final int NUMBER_TYPE = 0x3;
    public static final int STRING_TYPE = 0x4;

    private static void arrayLikedValidate(String arrayLiked, int dimension) {
        if (dimension == 0) {
            throw new IllegalArgumentException("the dimension of the array can not be zero");
        }
        if (arrayLiked.isEmpty()) {
            throw new IllegalArgumentException("can not convert the string as an array because it is empty");
        }
        int len = arrayLiked.length();
        if (arrayLiked.charAt(0) != '[' || arrayLiked.charAt(len - 1) != ']') {
            throw new IllegalArgumentException(
                    "can not convert the string as an array because it isn't wrapped in \"[]\""
            );
        }
    }

    private static TreeNode buildTree(String arrayLiked, int dimension, int allowElementType) {
        arrayLikedValidate(arrayLiked = arrayLiked.strip(), dimension);
        int len = arrayLiked.length();
        String s = arrayLiked;
        Deque<TreeNode> stk = new ArrayDeque<>();
        TreeNode root = new TreeNode();
        TreeNode cur = root;
        stk.push(root);
        int curDimension = 1;
        int i = 1;
        // each loop we scan and make a node
        while (i < len && curDimension > 0 && curDimension <= dimension) {
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
                TreeNode child = new TreeNode();
                cur.appendChild(child);
                cur = child;
                stk.push(child);
                i++;
                continue;
            }

            /*
             * handle string or number leaf node if the array
             * IS NOT empty.
             *
             * if the array IS NOT empty:
             * (1) we already scanned some leaf nodes of current node
             * (2) or we scanned no leaf node, and we don't meet the
             *     end char(']') which means no leaf node found
             */
            if (!cur.getChildren().isEmpty() || c != ']') {
                // ready to handle a leaf node but check
                // if current dimension is enough at first
                if (curDimension < dimension) {
                    throw new IllegalArgumentException(
                            "expected '[' but found '%s' at %d pos, because the array should be %d dimension"
                                    .formatted(c, i, dimension)
                    );
                }
                i = scanOneLeafChild(i, s, cur, allowElementType);
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
            while (i < len && curDimension > 0 &&  c == ']') {
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

        if (curDimension > dimension) {
            throw new IllegalArgumentException("too many '[' at %d pos that make array dimension > limit".formatted(i));
        }
        if (curDimension > 0) {
            throw new IllegalArgumentException("expected ']' but the array is end");
        }
        if (i < len) {
            throw new IllegalArgumentException("the array is end but found '%s'...".formatted(s.charAt(i)));
        }
        return root;
    }

    private static int scanOneLeafChild(int begin, String s, TreeNode parent, int allowElementType) {
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

    static int scanNumber(int being, String s) {
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

    static int scanString(int begin, String s) {
        int i = begin + 1, len = s.length();
        while (i < len) {
            while (i < len && s.charAt(i) != '"') {
                i++;
            }
            if (s.charAt(i - 1) != '\\') {
                break;
            }
            i++;
        }
        if (i == len) {
            throw new IllegalArgumentException(
                    "expected '%s' at %d pos when handling the string(%s) beginning at '%d' pos"
                            .formatted('"', i, s.substring(begin, i), begin)
            );
        }
        return i + 1;
    }

    static String checkArrayLiked(String arrayLiked, int dimension, int allowElementType) {
        return buildTree(arrayLiked, dimension, allowElementType).toString();
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

class TreeNode {
    private final List<TreeNode> children = new ArrayList<>();

    public List<TreeNode> getChildren() {
        return children;
    }

    void appendChild(TreeNode treeNode) {
        children.add(treeNode);
    }

    @Override
    public String toString() {
        return children.toString();
    }
}

abstract class LeafNode<T> extends TreeNode {
    private final T val;

    LeafNode(String val) {
        this.val = parse(val);
    }

    protected abstract T parse(String val);

    public T getVal() {
        return val;
    }
}

class NumberNode extends LeafNode<Number> {

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
}

class StringNode extends LeafNode<String> {

    StringNode(String val) {
        super(val);
    }

    @Override
    protected String parse(String val) {
        int len = val.length();
        if (val.charAt(0) != '"' && val.charAt(len - 1) != '"') {
            throw new IllegalStateException("can not parse '%s' to string val because it isn't wrapped by '\"'");
        }
        val = val.substring(1, len - 1);
        return val.replaceAll("\\\\\"", "\"");
    }

    @Override
    public String toString() {
        return getVal() + "(String)";
    }
}
