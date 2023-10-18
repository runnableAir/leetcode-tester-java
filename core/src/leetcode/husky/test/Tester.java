package leetcode.husky.test;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.MultiTaskCommandReader;
import leetcode.husky.test.cmd.reader.SingleTaskCommandReader;
import leetcode.husky.test.driver.ObjectCommandDriverFactory;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistration;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.param.resolver.ArgumentResolver;

import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

public class Tester {

    public static <T> void testForCommands(Reader testData, Consumer<MethodProxyRegistry<T>> methodRegister) {
        var driverFactory = new ObjectCommandDriverFactory<T>();
        // apply provided config to MethodProxyRegistry
        var registry = driverFactory.getMethodProxyRegistry();
        methodRegister.accept(registry);

        var commandReader = new MultiTaskCommandReader();
        var commandDriver = driverFactory.getCommonClassDriver();
        var shell = new CommandShell(commandDriver, commandReader);
        shell.process(testData);
    }

    /**
     * This method is used to test a single target method.
     * <p>
     * It takes a {@linkplain Reader} to read input of test data and a Config
     * to define the argument list and testing code for the
     * target method.
     * <p>
     * The config is a {@linkplain Consumer} interface which takes an
     * {@linkplain MethodProxyRegistry<T>} object to provide methods
     * to define the argument list and testing code for the target
     * method.
     * The argument list tells that how it should convert input into
     * appropriate arguments for the target method.
     * The testing code tells that how the target method should be
     * tested with the arguments.
     * <p>
     * <h3>Example</h3>
     * <pre>
     * class Solution {
     *     public int[] twoSum(int[] nums, int target) {...}
     * }
     * ...
     * String input = &quot;&quot;&quot;
     *         [1,2,3,5,8]
     *         10
     *         [1,2,3,5,8]
     *         6
     *         &quot;&quot;&quot;;
     * // note: you can read input from standard input from console
     * Reader reader = new StringReader(input);
     * // config argument list and testing code and start testing
     * Tester.testForMethod(reader, config -&gt; config
     *         .addMethod(&quot;twoSum&quot;,
     *                 ParamType.INT_ARRAY,
     *                 ParamType.INT)
     *         .impl((__, params) -&gt; new Solution()
     *                 .twoSum((int[]) params[0],
     *                         (int) params[1]))
     * );
     * </pre>
     *
     * @param testData Reader to read test data
     * @param config Config to defined method argument list and testing code
     * @param <T> The type of the instance that may be used to invocation
     */
    public static <T> void testForMethod(Reader testData, Consumer<MethodProxyRegistry<T>> config) {
        var driverFactory = new ObjectCommandDriverFactory<T>();
        // apply provided config to MethodProxyRegistry
        var registry = driverFactory.getMethodProxyRegistry();
        config.accept(registry);

        // calculate the number of arguments separated by a line of input,
        // which is resolved by method parameters
        var method = registry.getMethodProxyDefinitionByDefault();
        if (method == null) {
            throw new IllegalStateException("No method is registered!");
        }
        int requiredInputLines = method.argumentResolvers().stream().mapToInt(ArgumentResolver::argumentCount).sum();
        var commandReader = getSingleTaskCommandReader(requiredInputLines, method, registry);
        var commandDriver = driverFactory.getCommonClassDriver();
        var shell = new CommandShell(commandDriver, commandReader);
        shell.process(testData);
    }

    private static <T> SingleTaskCommandReader getSingleTaskCommandReader(
            int requiredInputLines, MethodProxyRegistration<T> method, MethodProxyRegistry<T> registry) {
        var commandReader = new SingleTaskCommandReader(requiredInputLines, method.name());
        var constructor = registry.getConstructorRegistration();
        if (constructor != null) {
            List<ArgumentResolver<?>> argumentResolvers = constructor.argumentResolvers();
            if (!argumentResolvers.isEmpty()) {
                throw new IllegalStateException(
                        "Using a constructor with non-empty argument list to instantiate an object is not allowed."
                );
            }
            commandReader.setPreCommand(new Command(constructor.name(), List.of()));
        }
        return commandReader;
    }
}
