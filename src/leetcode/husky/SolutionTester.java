package leetcode.husky;

import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.CommandReader;
import leetcode.husky.test.driver.v2.MethodInvokeHandler;
import leetcode.husky.test.driver.v2.ObjectCommandDriver;

import java.io.Reader;

public class SolutionTester<T> {
    final ObjectCommandDriver<T> objectCommandDriver = new ObjectCommandDriver<>();
    private CommandReader objectCommandReader;


    /**
     * Add a handler for invoking the target method with a specific key name.
     * <p>
     * The handler with the same key name will be overridden by the new one.
     *
     * @param targetMethodKey the specific key name of target method
     * @param handler the new handler for the target method
     */
    public void addMethodInvokeHandler(String targetMethodKey, MethodInvokeHandler<T> handler) {
        objectCommandDriver.addHandler(targetMethodKey, handler);
    }

    public void setCommandReader(CommandReader commandReader) {
        this.objectCommandReader = commandReader;
    }

    public void startTest(Reader commandInput) {
        CommandShell commandShell = new CommandShell(objectCommandDriver, objectCommandReader);
        commandShell.process(commandInput);
    }
}
