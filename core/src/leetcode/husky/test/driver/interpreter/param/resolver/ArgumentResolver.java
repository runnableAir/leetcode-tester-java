package leetcode.husky.test.driver.interpreter.param.resolver;

/**
 * 参数解析器, 将一个或多个字符串表示的参数值解析为一个新的值
 *
 * @param <T> 解析结果的类型
 */
public interface ArgumentResolver<T> {

    T resolve(String... args);

    /**
     * @return 返回该解析器明确指定需要的参数个数
     */
    int argumentCount();
}
