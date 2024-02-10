package util.husky.array;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ArrayNode {
    private final List<ArrayNode> children = new ArrayList<>();

    public List<ArrayNode> getChildren() {
        return children;
    }

    void appendChild(ArrayNode arrayNode) {
        children.add(arrayNode);
    }

    @Override
    public String toString() {
        return children.toString();
    }

    /**
     * Return the string representation (serialize form) of this array node.
     * <p>
     * The string will be form with a prefix {@code "["}, a suffix {@code "]"}
     * and a delimiter char {@code ","} joining strings returned by the children
     * nodes of this array node.
     *
     * @return the string representation (serialize form) of this array node.
     */
    public String asString() {
        return children.stream()
                .map(ArrayNode::asString)
                .collect(Collectors.joining(",", "[", "]"));
    }
}

abstract class ElementNode<T> extends ArrayNode {
    private final T val;

    ElementNode(String val) {
        this.val = parse(val);
    }

    protected abstract T parse(String val);

    public T getVal() {
        return val;
    }

    /**
     * @return the string representation (serialize form) of this element node
     */
    @Override
    public abstract String asString();
}