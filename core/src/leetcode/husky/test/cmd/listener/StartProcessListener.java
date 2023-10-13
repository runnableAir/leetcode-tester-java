package leetcode.husky.test.cmd.listener;

import leetcode.husky.test.cmd.CommandSet;

/**
 * 监听 CommandSet 开始处理的监听器
 * 监听内容:
 * <ol>
 *     <li>即将开始处理的 CommandSet</li>
 *     <li>所在的处理批次 (processId)</li>
 * </ol>
 */
public interface StartProcessListener {

    /**
     * 监听回调
     *
     * @param commandSet 即将开始处理的命令集
     * @param processId  process id
     */
    void onStartProcess(CommandSet commandSet, int processId);
}
