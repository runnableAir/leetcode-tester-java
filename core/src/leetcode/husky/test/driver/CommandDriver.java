package leetcode.husky.test.driver;

import leetcode.husky.test.cmd.Command;

/**
 * CommandDriver 用于执行 Command 对象
 */
public interface CommandDriver {

    /**
     * 执行 Command
     *
     * @param command command
     * @return 执行返回结果, Object 表示任意类型
     */
    Object execute(Command command);
}
