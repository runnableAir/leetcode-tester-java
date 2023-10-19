package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.Command;

/**
 * A listener to listen what command is executed with its result
 */
public interface AfterExecuteListener {

    /**
     * Do something after executing the command with its result
     *
     * @param command      command
     * @param commandIndex the index of the command in a set of commands
     *                     being processed
     * @param result       the result of the execution of the command
     */
    void afterExecute(Command command, int commandIndex, Object result);
}
