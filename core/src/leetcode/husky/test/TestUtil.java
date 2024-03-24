package leetcode.husky.test;

import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.MultiTaskCommandReader;
import leetcode.husky.test.cmd.reader.SingleTaskCommandReader;
import leetcode.husky.test.driver.ObjectCommandDriverFactory;
import leetcode.husky.test.driver.interpreter.ConstructorMethodProxy;
import leetcode.husky.test.driver.interpreter.MethodProxy;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistration;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.param.resolver.ArgumentResolver;

import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

public class TestUtil {

    /**
     * This method is used to test multi methods in a sequence
     * <p>
     * It takes a {@linkplain Reader} to read input of test data and a Config
     * to define the argument lists and implementations of methods.
     * <p>
     * The config is a {@linkplain Consumer} interface which takes an
     * {@linkplain MethodProxyRegistry<T>} object to provide methods
     * to define the argument lists and implementations of methods.
     * The argument list tells that how it should convert input into
     * appropriate arguments for the target method.
     * The implementation tells that how a target method should be
     * tested with the arguments.
     * <p>
     * <h3>Example</h3>
     * <pre>
     * class LRUCache {
     *     public LRUCache(int capacity) {
     *         //...
     *     }
     *     public int get(int key) {
     *         //...
     *     }
     *     public void put(int key, int value) {
     *         //...
     *     }
     * }
     * ....
     * // test case text
     * String text = &quot;&quot;&quot;
     *         [&quot;LRUCache&quot;,&quot;put&quot;,&quot;put&quot;,&quot;get&quot;,&quot;put&quot;,&quot;get&quot;,&quot;put&quot;,&quot;get&quot;,&quot;get&quot;,&quot;get&quot;]
     *         [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
     *             &quot;&quot;&quot;;
     * // note: you can read input from standard input from console
     * Reader testData = new StringReader(text);
     * TestUtil.&lt;LRUCache&gt;testForCommands(testData, config -&gt; config
     *         // add constructor: LRUCache(int)
     *         .addConstructor(&quot;LRUCache&quot;, ParamType.INT)
     *         .impl(params -&gt; new DesignSolution.LRUCache((int) params[0]))
     *         // add method: void put(int, int)
     *         .addMethod(&quot;put&quot;,
     *                 ParamType.INT,
     *                 ParamType.INT)
     *         .voidImpl((lruCache, params) -&gt; lruCache.put(
     *                 (int) params[0],
     *                 (int) params[1]))
     *         // add method: int get(int)
     *         .addMethod(&quot;get&quot;, ParamType.INT)
     *         .impl((lruCache, params) -&gt; lruCache.get((int) params[0]))
     * );
     * </pre>
     * @param testData Reader to read test data
     * @param config Config to defined method argument list and testing code
     * @param <T> The type of the instance that may be used to invocation
     */
    public static <T> void testForCommands(Reader testData, Consumer<MethodProxyRegistry<T>> config) {
        var driverFactory = new ObjectCommandDriverFactory<T>();
        // apply provided config to MethodProxyRegistry
        var registry = driverFactory.getMethodProxyRegistry();
        config.accept(registry);

        var commandReader = new MultiTaskCommandReader();
        var commandDriver = driverFactory.getDriver();
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
     * TestUtil.testForMethod(reader, config -&gt; config
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

        // calculate the number of arguments separated by a line of input
        var method = registry.getMethodRegistrationByDefault();
        if (method == null) {
            throw new IllegalStateException("No method is registered!");
        }
        int requiredInputLines = method.argumentResolvers().stream().mapToInt(ArgumentResolver::argumentCount).sum();
        checkConstructor(registry, method);
        var commandReader = new SingleTaskCommandReader(requiredInputLines, method.name());
        var commandDriver = driverFactory.getDriver();
        var shell = new CommandShell(commandDriver, commandReader);
        shell.process(testData);
    }

    private static <T> void checkConstructor(MethodProxyRegistry<T> registry, MethodProxyRegistration<T> method) {
        var constructor = registry.getConstructorRegistration();
        if (constructor == null || !(constructor.methodProxy()
                instanceof ConstructorMethodProxy<T>)) {
            return;
        }
        MethodProxy<T> newProxy = combineMethodWithConstructor(constructor, method);
        registry.addRegistration(
                new MethodProxyRegistration<>(method.name(), newProxy, method.argumentResolvers())
        );
    }

    private static <T> MethodProxy<T> combineMethodWithConstructor(
            MethodProxyRegistration<T> constructor, MethodProxyRegistration<T> method) {
        List<ArgumentResolver<?>> argumentResolvers = constructor.argumentResolvers();
        if (!argumentResolvers.isEmpty()) {
            throw new IllegalStateException(
                    "Using a constructor with non-empty argument list to instantiate an object is not allowed."
            );
        }
        ConstructorMethodProxy<T> conProxy = (ConstructorMethodProxy<T>) constructor.methodProxy();
        return (t, params) -> {
            t = conProxy.newInstance();
            return method.methodProxy().invoke(t, params);
        };
    }
}
