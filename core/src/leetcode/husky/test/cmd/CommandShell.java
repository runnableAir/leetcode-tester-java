package leetcode.husky.test.cmd;

import leetcode.husky.test.cmd.listener.AfterExecuteListener;
import leetcode.husky.test.cmd.listener.BeforeExecuteListener;
import leetcode.husky.test.cmd.listener.EndProcessListener;
import leetcode.husky.test.cmd.listener.StartProcessListener;
import leetcode.husky.test.cmd.reader.CommandReader;
import leetcode.husky.test.cmd.reader.LineReader;
import leetcode.husky.test.driver.CommandDriver;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * CommandShell class is used to handle commands input from
 * a character stream.
 */
public class CommandShell {
    private final CommandDriver commandDriver;
    private final CommandReader commandReader;

    // listeners
    private final List<StartProcessListener> startProcessListeners = new ArrayList<>();
    private final List<EndProcessListener> endProcessListeners = new ArrayList<>();
    private final List<BeforeExecuteListener> beforeExecuteListeners = new ArrayList<>();
    private final List<AfterExecuteListener> afterExecuteListeners = new ArrayList<>();

    // the count of command set already processed
    private int processCount;


    public CommandShell(CommandDriver commandDriver, CommandReader commandReader) {
        this.commandDriver = commandDriver;
        this.commandReader = commandReader;
        // initial default listener
        DefaultListener defaultListener = new DefaultListener();
        startProcessListeners.add(defaultListener);
        beforeExecuteListeners.add(defaultListener);
        afterExecuteListeners.add(defaultListener);
        endProcessListeners.add(defaultListener);
    }

    /**
     * read commands input from a character stream and process them.
     *
     * @param commandSource input source
     */
    public void process(Reader commandSource) {
        LineReader lrd = new LineReaderImpl(new Scanner(commandSource));
        CommandSet set;
        while (!(set = commandReader.readCommandSet(lrd)).isEmpty()) {
            doProcess(set);
        }
    }

    private void doProcess(CommandSet commandSet) {
        // notify the StartProcessListener(s)
        startProcessListeners.forEach(lst -> lst.onStartProcess(commandSet, processCount));

        List<Command> commands = commandSet.commands();
        long spendTime = 0;
        for (int i = 0; i < commands.size(); i++) {
            Command cmd = commands.get(i);
            // notify the BeforeExecuteListener(s)
            for (var listener : beforeExecuteListeners) {
                listener.beforeExecute(cmd, i);
            }
            long start = System.currentTimeMillis();
            Object result = commandDriver.execute(cmd);
            spendTime += System.currentTimeMillis() - start;
            // notify the AfterExecuteListener(s)
            for (var listener : afterExecuteListeners) {
                listener.afterExecute(cmd, i, result);
            }
        }

        // notify the EndProcessListener(s)
        for (var endProcessListener : endProcessListeners) {
            endProcessListener.onEndProcess(commandSet, processCount, spendTime);
        }
        processCount++;
    }

    public void addStartProcessListener(StartProcessListener listener) {
        startProcessListeners.add(listener);
    }

    public void addBeforeExecuteListener(BeforeExecuteListener listener) {
        beforeExecuteListeners.add(listener);
    }

    public void addAfterExecuteListener(AfterExecuteListener listener) {
        afterExecuteListeners.add(listener);
    }

    public void addEndProcessListener(EndProcessListener listener) {
        endProcessListeners.add(listener);
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

    private static class DefaultListener
            implements StartProcessListener,
            BeforeExecuteListener,
            AfterExecuteListener,
            EndProcessListener {
        private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private final PrintWriter out = new PrintWriter(bos, false);
        private int executeCount;

        @Override
        public void afterExecute(Command command, int commandIndex, Object result) {
            out.printf("%2d out[%d]: %s%n%n", executeCount, commandIndex, readableResult(result));
            executeCount++;
        }

        @Override
        public void beforeExecute(Command command, int commandIndex) {
            out.printf("%2d in[%d]: %s%n", executeCount, commandIndex, command);
        }

        @Override
        public void onEndProcess(CommandSet commandSet, int commandSetIndex, long spendTime) {
            out.printf("process command set[%d] finished, spend %dms%n", commandSetIndex, spendTime);
            // print output from the current executed command set to stdout
            out.flush();
            System.out.println(bos);
            // reset to ready for the next command set execution
            bos.reset();
        }

        @Override
        public void onStartProcess(CommandSet commandSet, int commandSetIndex) {
            out.printf("process command set[%d]:%n", commandSetIndex);
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
