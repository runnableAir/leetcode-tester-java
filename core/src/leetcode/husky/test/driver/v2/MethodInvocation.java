package leetcode.husky.test.driver.v2;

/**
 * An object representing a method invocation
 *
 * @param <T> the type of class or interface that declares the method
 *            represented by this object
 */
public interface MethodInvocation<T> {

    Object invoke(T t, Object... arguments);

    default boolean ignoreNullInstance() {
        return false;
    }
}
