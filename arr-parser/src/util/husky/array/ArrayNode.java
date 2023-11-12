package util.husky.array;

import java.util.ArrayList;
import java.util.List;

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
}