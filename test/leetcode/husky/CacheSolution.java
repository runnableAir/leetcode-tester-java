package leetcode.husky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Cache with using LRU strategy
public class CacheSolution {
    final int capacity;
    private int size;
    private final Map<String, ListNode> listNodeMap = new HashMap<>();

    // its next node is the latest recently used one
    private final ListNode dummyHead = new ListNode("NULL", -1);
    private final ListNode dummyTail = new ListNode("NULL", -1);

    public CacheSolution(int capacity) {
        this.capacity = capacity;
        dummyHead.next = dummyTail;
        dummyTail.pre = dummyHead;
    }

    public void put(String key, Object value) {
        ListNode node = listNodeMap.get(key);
        // if the key is a new key
        if (node == null) {
            // create a new node and insert it at the head
            // firstly check capacity and remove the tail if necessary
            if (size == capacity) {
                ListNode tail = pop();
                listNodeMap.remove(tail.key);
            }
            node = new ListNode(key, value);
            listNodeMap.put(key, node);
            push(node);
            return;
        }
        // update the value of key
        node.value = value;
        moveToHead(node);
    }

    public Object get(String key) {
        ListNode node = listNodeMap.get(key);
        if (node == null) {
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    public List<Entry> list() {
        ListNode node = dummyHead.next;
        List<Entry> result = new ArrayList<>();
        while (node != dummyTail) {
            result.add(new Entry(node.key, node.value));
            node = node.next;
        }
        return result;
    }

    public record Entry(String key, Object value) {
        @Override
        public String toString() {
            return "Entry{ %s => %s }".formatted(key, value);
        }
    }

    void moveToHead(ListNode node) {
        // if already at the head
        if (node == dummyHead.next) {
            return;
        }
        delete(node);
        push(node);
    }

    // delete a node from the link list
    void delete(ListNode node) {
        node.pre.next = node.next;
        node.next.pre = node.pre;
        size--;
    }

    // push a node at the head of link list
    void push(ListNode node) {
        node.next = dummyHead.next;
        node.pre = dummyHead;
        dummyHead.next.pre = node;
        dummyHead.next = node;
        size++;
    }

    // delete the node at the tail of the link list
    ListNode pop() {
        ListNode tail = dummyTail.pre;
        delete(tail);
        return tail;
    }

    static class ListNode {
        String key;
        Object value;
        ListNode next;
        ListNode pre;

        public ListNode(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
