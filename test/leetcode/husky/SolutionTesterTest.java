package leetcode.husky;

import leetcode.husky.test.driver.interpreter.param.ParamType;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

public class SolutionTesterTest {

    @Test
    public void mainModeTest() {
        // use TwoSumSolution as example
        SolutionTester<TwoSumSolution> solutionTester = new SolutionTester<>();
        SolutionConfig<TwoSumSolution> solutionConfig = new SolutionConfig<>();

        solutionConfig.addConstructor("constructor",
                (Object[] ignoredAndEmpty) -> {
                    // this constructor will be "default" if it doesn't require arguments
                    return new TwoSumSolution();
                }/*, empty var args for converter*/);

        solutionConfig.addMethod("main",
                (TwoSumSolution obj, Object... arguments) -> {
                    // manually cast type to match arguments list of "twoSum" method.
                    // the `arguments` will store correct type value
                    // using converter [resolveIntArray(), resolveIntNum()] passing bellow
                    return obj.twoSum((int[]) arguments[0], (int) arguments[1]);
                }/* var args for converter */,
                resolveIntArray(),
                resolveIntNum());

        // apply to `solutionTester`. both constructor and method above will be known to it
        solutionConfig.applyTo(solutionTester);

        String testcase = """
                [2,7,11,15]
                9
                [3,2,4]
                6
                [3,3]
                6
                """;
        solutionTester.startTest(new StringReader(testcase));
    }

    @Test
    public void mainModeErrorWhenNoConstructor() {
        // use TwoSumSolution as example
        SolutionTester<TwoSumSolution> solutionTester = new SolutionTester<>();
        SolutionConfig<TwoSumSolution> solutionConfig = new SolutionConfig<>();

        solutionConfig.addMethod("main",
                (TwoSumSolution obj, Object... arguments) -> obj.twoSum((int[]) arguments[0], (int) arguments[1]),
                resolveIntArray(),
                resolveIntNum());

        // this should throw RuntimeException
        Assert.assertThrows(RuntimeException.class, () -> solutionConfig.applyTo(solutionTester));
    }

    private static SolutionConfig.StringConverter resolveIntNum() {
        return ParamType.INT::resolveOneArg;
    }

    private static SolutionConfig.StringConverter resolveIntArray() {
        return ParamType.INT_ARRAY::resolveOneArg;
    }
}
