package leetcode.husky.test.designsolution;

public class DesignSolution {

    /**
     * leetcode.146. LRU Cache
     * link:
     * <a href="https://leetcode.cn/problems/lru-cache/description/">click</a>
     */
    public static class LRUCache {
        int capacity;
        int size;
        ListNode[] map = new ListNode[10001];

        // its next node is the latest recently used one
        ListNode dummyHead = new ListNode(-1, -1);
        ListNode dummyTail = new ListNode(-1, -1);

        public LRUCache(int capacity) {
            this.capacity = capacity;
            dummyHead.next = dummyTail;
            dummyTail.pre = dummyHead;
        }

        public int get(int key) {
            ListNode node = map[key];
            if (node == null) {
                return -1;
            }
            moveToHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            ListNode node = map[key];
            // if the key is a new key
            if (node == null) {
                // create a new node and insert it at the head
                // firstly check capacity and remove the tail if necessary
                if (size == capacity) {
                    ListNode tail = pop();
                    map[tail.key] = null;
                }
                node = new ListNode(key, value);
                map[key] = node;
                push(node);
                return;
            }
            // update the value of key
            node.value = value;
            moveToHead(node);
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
            int key;
            int value;
            ListNode next;
            ListNode pre;

            public ListNode(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }
    }
}
