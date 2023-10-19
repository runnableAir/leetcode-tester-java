package leetcode.husky.test.cmd.reader;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandSet;

import java.util.ArrayList;
import java.util.List;

public class SingleTaskCommandReader implements CommandReader {
    private final int argumentLineCount;
    private final String commandName;


    public SingleTaskCommandReader(int argumentLineCount, String commandName) {
        this.argumentLineCount = argumentLineCount;
        this.commandName = commandName;
    }

    @Override
    public CommandSet readCommandSet(LineReader lineReader) {
        List<String> lines = new ArrayList<>();
        while (notEnoughArgs(lines) && lineReader.hasNextLine()) {
            lines.add(lineReader.nextLine());
        }
        if (notEnoughArgs(lines)) {
            return new CommandSet(List.of());
        }
        Command command = new Command(commandName, lines);
        return new CommandSet(List.of(command));
    }

    private boolean notEnoughArgs(List<String> lines) {
        return lines.size() < argumentLineCount;
    }
}
