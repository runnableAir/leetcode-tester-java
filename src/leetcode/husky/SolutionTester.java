package leetcode.husky;

import leetcode.husky.test.cmd.CommandSet;
import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.CommandReader;
import leetcode.husky.test.cmd.reader.LineReader;
import leetcode.husky.test.driver.v2.MethodInvokeHandler;
import leetcode.husky.test.driver.v2.ObjectCommandDriver;

import java.io.Reader;

public class SolutionTester<T> {
    final ObjectCommandDriver<T> objectCommandDriver = new ObjectCommandDriver<>();


    public void addMethodInvokeHandler(String targetMethodKey, MethodInvokeHandler<T> handler) {
        objectCommandDriver.addHandler(targetMethodKey, handler);
    }

    public void startTest(Reader commandInput) {
        CommandShell commandShell = new CommandShell(objectCommandDriver, lineReader -> null);
        commandShell.process(commandInput);
    }
}
