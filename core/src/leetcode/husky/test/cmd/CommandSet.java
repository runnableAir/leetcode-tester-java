package leetcode.husky.test.cmd;

import java.util.List;

public record CommandSet(List<Command> commands) {

    public boolean isEmpty() {
        return commands.isEmpty();
    }
}
