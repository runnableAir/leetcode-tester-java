package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.Command;

/**
 * A listener to listen what command is ready to be executed
 */
public interface BeforeExecuteListener {

    /**
     * Do something before executing the command
     *
     * @param command      command
     * @param commandIndex the index of the command in a set of commands
     *                     being processed
     */
    void beforeExecute(Command command, int commandIndex);
}
