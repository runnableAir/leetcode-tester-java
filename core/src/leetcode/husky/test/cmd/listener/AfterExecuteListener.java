package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.Command;

/**
 * 监听命令执行完毕的监听器.
 * 监听内容:
 * <ol>
 *     <li>执行的 Command</li>
 *     <li>该 Command 在所属 CommandSet 中的 index</li>
 *     <li>该 Command 所在的处理批次 (processId)</li>
 *     <li>执行后返回的结果</li>
 * </ol>
 */
public interface AfterExecuteListener {

    /**
     * 监听回调.
     *
     * @param command      执行完毕的 Command
     * @param processId    process id
     * @param commandIndex 命令在 CommandSet 中的 index
     * @param result       执行结果
     */
    void afterExecute(Command command, int processId, int commandIndex, Object result);
}
