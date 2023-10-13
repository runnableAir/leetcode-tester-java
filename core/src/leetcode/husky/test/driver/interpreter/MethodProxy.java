package leetcode.husky.test.driver.interpreter;

/**
 * The MethodProxy interface provides an abstraction layer that
 * enables the generic invocation of any method.
 * <p>
 * It achieves this by wrapping the target method using the abstract
 * method {@link MethodProxy#invoke(T, Object...)}. This method
 * takes an instance from the current context and the arguments
 * required by the target method.
 * <p>
 * By providing this abstraction, the MethodProxy allows the
 * {@link MethodInterpreter} to invoke methods in a consistent and generic
 * manner, regardless of the specific method being invoked.
 *
 * @param <T> the type of the instance
 * @see MethodInterpreter
 */
public interface MethodProxy<T> {

    /**
     * invoke the method wrapped by this MethodProxy.
     *
     * @param t      the instance
     * @param params arguments of the method
     * @return the result returned by the method
     */
    Object invoke(T t, Object... params);
}
