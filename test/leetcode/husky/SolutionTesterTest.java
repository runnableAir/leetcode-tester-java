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

    @Test
    public void multiModeTest() {
        // use CacheSolution as example
        SolutionTester<CacheSolution> solutionTester = new SolutionTester<>();
        SolutionConfig<CacheSolution> solutionConfig = new SolutionConfig<>();

        solutionConfig.addConstructor("newCacheObj",
                (Object... arguments) -> {
                    // new CacheSolution(int)
                    return new CacheSolution((int) arguments[0]);
                },
                resolveIntNum());

        solutionConfig.addMethod("putCache",
                (CacheSolution obj, Object... arguments) -> {
                    // put(String key, Object value)
                    obj.put((String) arguments[0], arguments[1]);
                    return null;
                },
                resolveString(),
                resolveString());

        solutionConfig.addMethod("getCache",
                (CacheSolution obj, Object... arguments) -> {
                    // get(String key)
                    return obj.get((String) arguments[0]);
                },
                resolveString());

        solutionConfig.addMethod("listAll",
                (CacheSolution obj, Object... ignored) -> {
                    // list()
                    return obj.list();
                });

        // change to "multi invoke" mode. with this mode, the input tells about a sequence of methods to be invoked
        solutionConfig.setMode(SolutionConfig.Mode.MULTI_INVOKE);
        solutionConfig.applyTo(solutionTester);

        String testcase = """
                ["newCacheObj", "putCache", "putCache", "putCache", "listAll", "getCache", "listAll", "putCache", "listAll"]
                [[3], ["Alice", "1"], ["Bob", "2"], ["Jack", "3"], [], ["Alice"], [], ["Home", "4"], []]
                """;
        solutionTester.startTest(new StringReader(testcase));
    }

    private static SolutionConfig.StringConverter resolveIntNum() {
        return ParamType.INT::resolveOneArg;
    }

    private static SolutionConfig.StringConverter resolveIntArray() {
        return ParamType.INT_ARRAY::resolveOneArg;
    }

    private static SolutionConfig.StringConverter resolveString() {
        return ParamType.STRING::resolveOneArg;
    }
}
