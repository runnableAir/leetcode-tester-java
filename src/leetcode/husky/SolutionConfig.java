package leetcode.husky;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandSet;
import leetcode.husky.test.cmd.reader.CommandReader;
import leetcode.husky.test.cmd.reader.LineReader;
import leetcode.husky.test.driver.v2.InitialInvocation;
import leetcode.husky.test.driver.v2.InitialTargetHandler;
import leetcode.husky.test.driver.v2.MethodInvocation;
import leetcode.husky.test.driver.v2.MethodInvokeHandler;
import leetcode.husky.test.driver.v2.MethodInvokeRequest;
import util.husky.array.ArrayStringUtil;

import java.util.*;

// Provide simple way to create MethodInvokeHandlers for MethodInvocation and InitialInvocation
// to config available method of a solution object
public class SolutionConfig<T> {
    private final Map<String, MethodInvokeHandler<T>> handlerMap = new HashMap<>();
    private static final String DEFAULT_METHOD_KEY = "default.method";
    private static final String DEFAULT_CONSTRUCTOR_KEY = "default.constructor";
    private int defaultMethodArgument = -1;


    public void addMethod(String key,
                          MethodInvocation<T> methodInvocation,
                          StringConverter... stringConverters) {
        var handler = new SimpleMethodInvokeHandler<>(methodInvocation, List.of(stringConverters));
        handlerMap.put(key, handler);
        // try to set "default" method mapping (if absent)
        if (defaultMethodArgument == -1) {
            handlerMap.put(DEFAULT_METHOD_KEY, handler);
            defaultMethodArgument = stringConverters.length;
        }
    }

    public void addConstructor(String key,
                               InitialInvocation<T> initialInvocation,
                               StringConverter... stringConverters) {
        var handler = new SimpleInitialTargetHandler<>(initialInvocation, List.of(stringConverters));
        handlerMap.put(key, handler);
        // try to set "default" constructor mapping (if absent)
        // "default" constructor should require no arguments/converters
        if (handlerMap.containsKey(DEFAULT_CONSTRUCTOR_KEY) || stringConverters.length > 0) {
            return;
        }
        handlerMap.put(DEFAULT_CONSTRUCTOR_KEY, handler);
    }


    public enum Mode {
        MAIN_INVOKE, MULTI_INVOKE
    }

    private Mode mode = Mode.MAIN_INVOKE;

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    void applyTo(SolutionTester<T> solutionTester) {
        for (var invokeHandlerEntry : handlerMap.entrySet()) {
            String targetMethodKey = invokeHandlerEntry.getKey();
            MethodInvokeHandler<T> handler = invokeHandlerEntry.getValue();
            solutionTester.addMethodInvokeHandler(targetMethodKey, handler);
        }
        // determine which command reader should be used for `solutionTester`
        CommandReader commandReader;
        if (mode == Mode.MAIN_INVOKE) {
            commandReader = new MainInvokeCommandReader();
        } else {
            commandReader = new MultiInvokeCommandReader();
        }
        solutionTester.setCommandReader(commandReader);
    }


    public interface StringConverter {

        Object convert(String argument);
    }

    static class SimpleMethodInvokeHandler<T> extends MethodInvokeHandler<T> {

        SimpleMethodInvokeHandler(MethodInvocation<T> methodInvocation,
                                  List<StringConverter> converterList) {
            super(methodInvocation, new SimpleArgumentResolver(converterList));
        }
    }

    static class SimpleInitialTargetHandler<T> extends InitialTargetHandler<T> {

        SimpleInitialTargetHandler(MethodInvocation<T> methodInvocation,
                                   List<StringConverter> converterList) {
            super(methodInvocation, new SimpleArgumentResolver(converterList));
        }
    }

    static class SimpleArgumentResolver implements MethodInvokeHandler.ArgumentResolver {
        private final List<StringConverter> converterList;

        SimpleArgumentResolver(List<StringConverter> converterList) {
            this.converterList = converterList;
        }

        @Override
        public Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest) {
            List<String> parameters = methodInvokeRequest.parameters();
            if (converterList.size() != parameters.size()) {
                throw new RuntimeException(
                        "The number of StringConverter doesn't match the number of request parameters");
            }
            var it = converterList.iterator();
            return parameters.stream()
                    .map(str -> it.next().convert(str))
                    .toArray();
        }
    }

    // read command for single main method invocation
    // main method is the "default" method from this config
    // 用于读取仅包含目标方法参数输入的命令, 目标方法为当前配置中的 "默认" 方法
    // 初始化方法为当前配置中的 "默认" 初始化方法
    class MainInvokeCommandReader implements CommandReader {
        final int invokeArgumentCount;
        final Command initialInvoke = new Command(DEFAULT_CONSTRUCTOR_KEY, List.of());

        MainInvokeCommandReader() {
            // throw Exception if no available "default" constructor. (we need it to prepend an "initial" command)
            if (!handlerMap.containsKey(DEFAULT_CONSTRUCTOR_KEY)) {
                throw new RuntimeException("No available \"no-arguments\" InitialTargetInvokeHandler");
            }
            this.invokeArgumentCount = defaultMethodArgument;
        }

        @Override
        public CommandSet readCommandSet(LineReader lineReader) {
            List<String> lines = readNLines(lineReader, invokeArgumentCount);
            if (lines.isEmpty()) {
                return EMPTY_COMMAND_SET;
            }
            Command mainInvoke = new Command(DEFAULT_METHOD_KEY, lines);
            return new CommandSet(List.of(initialInvoke, mainInvoke));
        }
    }

    // read command for multi invoke context (a sequence of method to invoke after
    // first initial method(constructor))
    static class MultiInvokeCommandReader implements CommandReader {

        @Override
        public CommandSet readCommandSet(LineReader lineReader) {
            List<String> twoLines = readNLines(lineReader, 2);
            if (twoLines.isEmpty()) {
                return EMPTY_COMMAND_SET;
            }
            List<String> targetMethodKeys = ArrayStringUtil.getStringList(twoLines.get(0));
            List<List<String>> invokeParameterList = ArrayStringUtil.parse2dArrayAsList(twoLines.get(1));
            if (targetMethodKeys.size() != invokeParameterList.size()) {
                throw new RuntimeException(
                        "The number of \"parameter list\" doesn't match the number of invoking method");
            }
            Iterator<List<String>> it = invokeParameterList.iterator();
            List<Command> commands = targetMethodKeys .stream()
                    .map(name -> new Command(name, it.next()))
                    .toList();
            return new CommandSet(commands);
        }
    }

    static List<String> readNLines(LineReader lineReader, int n) {
        List<String> lines = new ArrayList<>();
        while (n-- > 0) {
            String line = null;
            while ((line == null || line.isBlank()) && lineReader.hasNextLine()) {
                line = lineReader.nextLine();
            }
            if (line == null || line.isBlank()) {
                return List.of();
            }
            lines.add(line);
        }
        return lines;
    }

    static final CommandSet EMPTY_COMMAND_SET = new CommandSet(List.of());
}
