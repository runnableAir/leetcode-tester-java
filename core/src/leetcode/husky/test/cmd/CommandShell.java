package leetcode.husky.test.cmd;

import leetcode.husky.test.cmd.listener.EndProcessListener;
import leetcode.husky.test.driver.CommandDriver;
import leetcode.husky.test.cmd.listener.AfterExecuteListener;
import leetcode.husky.test.cmd.listener.BeforeExecuteListener;
import leetcode.husky.test.cmd.listener.StartProcessListener;
import leetcode.husky.test.cmd.reader.CommandReader;
import leetcode.husky.test.cmd.reader.LineReader;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * CommandsShell 用于从输入中读取一系列 Command (CommandSet) 并执行
 */
public class CommandShell {
    private final CommandDriver commandDriver;
    private final CommandReader commandReader;

    // 监听器
    private StartProcessListener startProcessListener;
    private EndProcessListener endProcessListener;
    private BeforeExecuteListener beforeExecuteListener;
    private AfterExecuteListener afterExecuteListener;

    // 当前处理批次
    private int processId;


    public CommandShell(CommandDriver commandDriver, CommandReader commandReader) {
        this.commandDriver = commandDriver;
        this.commandReader = commandReader;
        initDefaultListener();
    }

    private void initDefaultListener() {
        DefaultListener defaultListener = new DefaultListener();
        startProcessListener = defaultListener;
        beforeExecuteListener = defaultListener;
        afterExecuteListener = defaultListener;
        endProcessListener = defaultListener;
    }

    /**
     * 从字符流中读取输入的 Command 并执行.
     * <p>
     * 要获得输入的 Command, 需将读取的原始文本进行解析,
     * 这一步由该实例的 {@link CommandReader#readCommandSet(LineReader)} 实现,
     * 通过该方法, 可以获取每次要执行的一组 Command, 即 {@link CommandSet},
     * 然后通过该实例的 {@link CommandDriver} 逐个执行其中的每个 Command.
     * <p>
     * 在处理每组 Command 的过程中, 会触发一些监听器:
     * <ul>
     *     <li>{@link StartProcessListener}: 监听 "<b>命令集</b>执行前"</li>
     *     <li>{@link EndProcessListener}: 监听 "<b>命令集</b>执行后"</li>
     *     <li>{@link BeforeExecuteListener}: 监听 "命令执行前"</li>
     *     <li>{@link AfterExecuteListener}: 监听 "命令执行后"</li>
     * </ul>
     *
     * @param commandSource 用于读取 Command 的字符流
     */
    public void process(Reader commandSource) {
        LineReader lrd = new LineReaderImpl(new Scanner(commandSource));
        CommandSet set;
        while (!(set = commandReader.readCommandSet(lrd)).isEmpty()) {
            doProcess(set);
        }
    }

    private void doProcess(CommandSet commandSet) {
        startProcessListener.onStartProcess(commandSet, processId);
        List<Command> commands = commandSet.commands();
        for (int i = 0; i < commands.size(); i++) {
            Command cmd = commands.get(i);
            beforeExecuteListener.beforeExecute(cmd, processId, i);
            Object result = commandDriver.execute(cmd);
            afterExecuteListener.afterExecute(cmd, processId, i, result);
        }
        endProcessListener.onEndProcess(commandSet, processId);
        processId++;
    }

    static String readableResult(Object o) {
        // use the new feature "Pattern matching for Switch" from Java 21
        return switch (o) {
            case null -> "null";
            case boolean[] array -> Arrays.toString(array);
            case boolean[][] array -> Arrays.deepToString(array);
            case byte[] array -> Arrays.toString(array);
            case byte[][] array -> Arrays.deepToString(array);
            case char[] array -> Arrays.toString(array);
            case char[][] array -> Arrays.deepToString(array);
            case short[] array -> Arrays.toString(array);
            case short[][] array -> Arrays.deepToString(array);
            case int[] array -> Arrays.toString(array);
            case int[][] array -> Arrays.deepToString(array);
            case long[] array -> Arrays.toString(array);
            case long[][] array -> Arrays.deepToString(array);
            case float[] array -> Arrays.toString(array);
            case float[][] array -> Arrays.deepToString(array);
            case double[] array -> Arrays.toString(array);
            case double[][] array -> Arrays.deepToString(array);
            case Object[][] array -> Arrays.deepToString(array);
            case Object[] array -> Arrays.toString(array);
            default -> o.toString();
        };
    }

    public void setStartProcessListener(StartProcessListener startProcessListener) {
        this.startProcessListener = startProcessListener;
    }

    public void setBeforeExecuteListener(BeforeExecuteListener beforeExecuteListener) {
        this.beforeExecuteListener = beforeExecuteListener;
    }

    public void setAfterExecuteListener(AfterExecuteListener afterExecuteListener) {
        this.afterExecuteListener = afterExecuteListener;
    }

    public void setEndProcessListener(EndProcessListener endProcessListener) {
        this.endProcessListener = endProcessListener;
    }

    static class DefaultListener
            implements StartProcessListener,
            BeforeExecuteListener,
            AfterExecuteListener,
            EndProcessListener {
        // PrintWriter 用于缓冲输出内容
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(bos, false);
        int executeCount;
        long startTime;

        @Override
        public void afterExecute(Command command, int processId, int commandIndex, Object result) {
            out.printf("%2d out[%d]: %s%n%n", executeCount, commandIndex, readableResult(result));
            executeCount++;
        }

        @Override
        public void beforeExecute(Command command, int processId, int commandIndex) {
            out.printf("%2d in[%d]: %s%n", executeCount, commandIndex, command);
        }

        @Override
        public void onEndProcess(CommandSet commandSet, int processId) {
            long spendTime = System.currentTimeMillis() - startTime;
            out.printf("process command set[%d] finished, spend %dms%n", processId, spendTime);
            // print output from the current executed command set to stdout
            out.flush();
            System.out.println(bos);
            // reset to ready for the next command set execution
            bos.reset();
        }

        @Override
        public void onStartProcess(CommandSet commandSet, int processId) {
            out.printf("process command set[%d]:%n", processId);
            startTime = System.currentTimeMillis();
        }
    }

    static class LineReaderImpl implements LineReader {
        Scanner scanner;

        LineReaderImpl(Scanner scanner) {
            this.scanner = scanner;
        }

        public boolean hasNextLine() {
            return scanner.hasNextLine();
        }

        public String nextLine() {
            return scanner.nextLine();
        }
    }
}
