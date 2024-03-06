package leetcode.husky.test.cmd.reader;

import leetcode.husky.test.util.ArgumentParseUtil;
import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandSet;
import util.husky.array.ArrayStringUtil;

import java.util.ArrayList;
import java.util.List;

public class MultiTaskCommandReader implements CommandReader {

    @Override
    public CommandSet readCommandSet(LineReader lineReader) {
        if (!lineReader.hasNextLine()) {
            return new CommandSet(List.of());
        }
        List<String> methodNameList = ArgumentParseUtil.getStringList(lineReader.nextLine());
        List<List<String>> argsList = ArrayStringUtil.parse2dArrayAsList(lineReader.nextLine());
        List<Command> commandList = new ArrayList<>();
        int size = methodNameList.size();
        for (int i = 0; i < size; i++) {
            Command command = new Command(methodNameList.get(i), argsList.get(i));
            commandList.add(command);
        }
        return new CommandSet(commandList);
    }
}
