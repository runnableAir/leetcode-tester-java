package leetcode.husky.test.cmd.reader;

import leetcode.husky.test.cmd.CommandSet;

/**
 * CommandReader 将原始文本解析为统一的 Command 对象
 */
public interface CommandReader {

    /**
     * 从 {@link LineReader} 中扫描输入文本并解析为一组 Command
     *
     * @param lineReader 逐行扫描并返回每行字符串的 Reader
     * @return 一组命令, 由 CommandSet 对象封装
     */
    CommandSet readCommandSet(LineReader lineReader);
}
