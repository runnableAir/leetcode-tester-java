package leetcode.husky.test.driver;

import leetcode.husky.test.cmd.Command;

/**
 * an interface to execute commands
 */
public interface CommandDriver {

    /**
     * execute the command and return an object representing the
     * result of execution
     *
     * @param command command
     * @return an object representing the result of execution
     */
    Object execute(Command command);
}
