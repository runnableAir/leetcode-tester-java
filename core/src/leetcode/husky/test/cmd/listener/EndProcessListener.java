package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.CommandSet;

/**
 * A listener that listens for the completion of a set of commands
 */
public interface EndProcessListener {

    /**
     * Do something when a set of commands is completed
     *
     * @param commandSet      a set of commands
     * @param commandSetIndex the index of the commands set
     * @param spendTime       the time spend of current process
     */
    void onEndProcess(CommandSet commandSet, int commandSetIndex, long spendTime);
}
