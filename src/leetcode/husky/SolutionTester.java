package leetcode.husky;

import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.CommandReader;
import leetcode.husky.test.driver.CommandDriver;

import java.io.Reader;

public class SolutionTester {
    private CommandDriver objectCommandDriver;
    private CommandReader objectCommandReader;


    public SolutionTester() {

    }

    void setCommandDriver(CommandDriver commandDriver) {
        this.objectCommandDriver = commandDriver;
    }

    void setCommandReader(CommandReader commandReader) {
        this.objectCommandReader = commandReader;
    }

    public void startTest(Reader commandInput) {
        CommandShell commandShell = new CommandShell(objectCommandDriver, objectCommandReader);
        commandShell.process(commandInput);
    }
}
