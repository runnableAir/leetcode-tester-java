package leetcode.husky.test.driver.v2;

public interface MethodInvocation<T> {

    Object invoke(T t, Object... arguments);

    default boolean ignoreNullInstance() {
        return false;
    }
}
