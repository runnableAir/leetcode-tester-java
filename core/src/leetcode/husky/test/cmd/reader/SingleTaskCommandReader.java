package leetcode.husky.test.cmd.reader;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandSet;

import java.util.ArrayList;
import java.util.List;

public class SingleTaskCommandReader implements CommandReader {
    private final int argumentLineCount;
    private final String commandName;
    private Command preCommand;


    public SingleTaskCommandReader(int argumentLineCount, String commandName) {
        this.argumentLineCount = argumentLineCount;
        this.commandName = commandName;
    }

    @Override
    public CommandSet readCommandSet(LineReader lineReader) {
        List<String> lines = new ArrayList<>();
        while (lineReader.hasNextLine() && notEnoughArgs(lines)) {
            lines.add(lineReader.nextLine());
        }
        if (notEnoughArgs(lines)) {
            return new CommandSet(List.of());
        }
        Command command = new Command(commandName, lines);
        if (preCommand != null) {
            return new CommandSet(List.of(preCommand, command));
        }
        return new CommandSet(List.of(command));
    }

    public void setPreCommand(Command preCommand) {
        this.preCommand = preCommand;
    }

    private boolean notEnoughArgs(List<String> lines) {
        return lines.size() < argumentLineCount;
    }
}
