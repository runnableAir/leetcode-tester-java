package leetcode.husky.test.solution;

import leetcode.husky.test.TestUtil;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.io.Reader;
import java.io.StringReader;

public class Example2 {

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

        TestUtil.<Solution>testForMethod(testData, config -> config
                // note that we add an implementation for invoking constructor
                .addConstructor("Solution")
                .proxy(params -> new Solution())
                .addMethod("method1", ParamType.INT_ARRAY, ParamType.INT)
                // An instance of the Solution class is automatically created using the constructor
                // above whenever this method is requested, and then the instance is passed as the
                // first parameter "solution" to the implementation.
                .proxy((solution, params) -> solution.method1((int[]) params[0], (int) params[1]))
        );

        System.out.printf("run in %dms%n", System.currentTimeMillis() - startTime);
    }
}
