package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.CommandSet;

/**
 * A listener to listen where a set of commands is ready to process
 */
public interface StartProcessListener {

    /**
     * Do something before starting process the commands
     *
     * @param commandSet      a set of commands
     * @param commandSetIndex the index of the commands set
     */
    void onStartProcess(CommandSet commandSet, int commandSetIndex);
}
