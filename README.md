## Leetcode tester for java

This project provides a set of classes that create a utility environment,
allowing you to run and test your LeetCode code locally without the need
for manual handling of input and output, both of which are automated for
repeatability.

## Example

[leetcode.1. Two Sum](https://leetcode.cn/problems/two-sum/description/)

```java
import leetcode.husky.test.Tester;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

// Example LeetCode code
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int[] map = new int[100];
        Arrays.fill(map, -1);
        for (int i = 0; i < nums.length; i++) {
            if (map[target - nums[i]] != -1) {
                return new int[]{map[target - nums[i]], i};
            }
            map[nums[i]] = i;
        }
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        String input = """
                [1,2,3,5,8]
                10
                [1,2,3,5,8]
                6
                """;
        Reader reader = new StringReader(input);
        // Reader reader = new InputStreamReader(System.in);
        // config argument list and testing code and start testing
        Tester.testForMethod(reader, config -> config
                .addMethod("twoSum",
                        ParamType.INT_ARRAY,
                        ParamType.INT)
                .impl((__, params) -> new Solution()
                        .twoSum((int[]) params[0],
                                (int) params[1]))
        );
    }
}
```

[leetcode.146. LRU Cache](https://leetcode.cn/problems/lru-cache/description/)

```java
class LRUCache {
    public LRUCache(int capacity) {
        //...
    }

    public int get(int key) {
        //...
    }

    public void put(int key, int value) {
        //...
    }
}

public class Main {
    public static void main(String[] args) {
        // test case text
        String text = """
                ["LRUCache","put","put","get","put","get","put","get","get","get"]
                [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
                    """;
        Reader testData = new StringReader(text);

        long startTime = System.currentTimeMillis();

        // In the following you will register the methods and their implementations:
        // a. LRUCache(int) [Constructor]
        // b. void put(int, int)
        // c. int get(int)

        // TIPS: Explicitly specifying the generic type "T" of the method will help
        // compiler recognize it. This will greatly assist in "code completion"
        Tester.<LRUCache>testForCommands(testData, config -> config
                // add constructor: LRUCache(int)
                .addConstructor("LRUCache", ParamType.INT)
                .impl(params -> new DesignSolution.LRUCache((int) params[0]))
                // add method: void put(int, int)
                .addMethod("put",
                        ParamType.INT,
                        ParamType.INT)
                .voidImpl((lruCache, params) -> lruCache.put(
                        (int) params[0],
                        (int) params[1]))
                // add method: int get(int)
                .addMethod("get", ParamType.INT)
                .impl((lruCache, params) -> lruCache.get((int) params[0]))
        );

        System.out.printf("spend: %dms%n", System.currentTimeMillis() - startTime);
    }
}
```

note: You can find the full source code from `example/src/leetcode/husky/test/designsolution/Example.java`