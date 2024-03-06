package leetcode.husky.test.designsolution;

import leetcode.husky.test.Tester;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.io.Reader;
import java.io.StringReader;

public class Example {
    public static void main(String[] args) {
        // test case text
        String text = """
                ["LRUCache","put","put","get","put","get","put","get","get","get"]
                [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
                    """;
        Reader testData = new StringReader(text);

        long startTime = System.currentTimeMillis();

        // You should call the method "testForCommands" to test multi methods
        // in a sequence.
        // In the following you will register the methods and their implementations:
        // a. LRUCache(int) [Constructor]
        // b. void put(int, int)
        // c. int get(int)

        // TIPS: Explicitly specifying the generic type "T" of the method will help
        // compiler recognize it. This will greatly assist in "code completion"
        Tester.<DesignSolution.LRUCache>testForCommands(testData, config -> config
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
