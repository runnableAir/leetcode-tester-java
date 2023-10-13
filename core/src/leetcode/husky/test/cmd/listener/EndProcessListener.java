package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.CommandSet;

/**
 * 监听命令集执行完毕.
 * 监听内容:
 * <ol>
 *     <li>处理完毕的 CommandSet </li>
 *     <li>所在的处理批次 (processId)</li>
 * </ol>
 */
public interface EndProcessListener {

    /**
     * 监听回调
     *
     * @param commandSet commandSet
     * @param processId  processId
     */
    void onEndProcess(CommandSet commandSet, int processId);
}
