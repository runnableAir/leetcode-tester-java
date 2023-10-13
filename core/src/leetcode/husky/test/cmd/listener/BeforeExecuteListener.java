package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.Command;

/**
 * 监听命令开始之前的监听器.
 * 监听内容:
 * <ol>
 *     <li>即将执行的 Command</li>
 *     <li>该 Command 在所属 CommandSet 中的 index</li>
 *     <li>该 Command 所在的处理批次 (processId)</li>
 * </ol>
 */
public interface BeforeExecuteListener {

    /**
     * 监听回调
     *
     * @param command      即将执行的 Command
     * @param processId    process id
     * @param commandIndex 命令在 CommandSet 中的 index
     */
    void beforeExecute(Command command, int processId, int commandIndex);
}
