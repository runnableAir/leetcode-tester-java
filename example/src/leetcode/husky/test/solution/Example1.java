package leetcode.husky.test.solution;

import leetcode.husky.test.Tester;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.io.Reader;
import java.io.StringReader;

public class Example1 {

    public static void main(String[] args) {
        // two test case
        String text = """
                [1,2,3]
                4
                [3,1,4]
                5
                """;
        Reader testData = new StringReader(text);

        long startTime = System.currentTimeMillis();

        Tester.<Solution>testForMethod(testData, config -> config
                .addMethod("method1", ParamType.INT_ARRAY, ParamType.INT)
                // note that "solution" is null
                .impl((solution, params) -> new Solution().method1((int[]) params[0], (int) params[1]))
        );

        System.out.printf("spend: %dms%n", System.currentTimeMillis() - startTime);
    }
}

