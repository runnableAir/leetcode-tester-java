package leetcode.husky.test;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.MultiTaskCommandReader;
import leetcode.husky.test.cmd.reader.SingleTaskCommandReader;
import leetcode.husky.test.driver.ObjectCommandDriverFactory;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistration;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.param.resolver.ParamResolver;

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
        int requiredInputLines = method.paramResolvers().stream().mapToInt(ParamResolver::argumentCount).sum();
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
            List<ParamResolver<?>> paramResolvers = constructor.paramResolvers();
            if (!paramResolvers.isEmpty()) {
                throw new IllegalStateException(
                        "Using a constructor with non-empty argument list to instantiate an object is not allowed."
                );
            }
            commandReader.setPreCommand(new Command(constructor.name(), List.of()));
        }
        return commandReader;
    }
}
