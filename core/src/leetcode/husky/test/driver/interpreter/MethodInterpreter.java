package leetcode.husky.test.driver.interpreter;

import leetcode.husky.test.cmd.Command;

/**
 * The MethodInterpreter class is an abstract class that is used
 * to interpret a Command as making a method call on a specific
 * instance. It uses the {@link MethodProxy} to execute the method
 * specified by the command.
 *
 * @param <T> the type of instance
 */
public abstract class MethodInterpreter<T> {

    /**
     * Processes the given command by invoking the appropriate method
     * on the specified instance.
     *
     * @param instance The instance on which the method should be invoked.
     * @param command  The command that specifies the method to be invoked.
     * @return The result of invoking the method.
     */
    public Object process(T instance, Command command) {
        MethodProxy<T> methodProxy = getMethodProxy(command);
        Object[] methodArgs = getMethodArgs(command);
        return methodProxy.invoke(instance, methodArgs);
    }

    /**
     * Return a {@link MethodProxy} that wraps the target method specified by
     * the command.
     * <p>
     * The returned MethodProxy is to be invoked with appropriate arguments
     * in the {@link MethodInterpreter#process(T, Command)} method.
     *
     * @param command command
     * @return MethodProxy
     */
    protected abstract MethodProxy<T> getMethodProxy(Command command);

    /**
     * Return an array of the arguments that are conveyed to the target
     * method specified by the {@linkplain Command} object.
     * <p>
     * This method takes the arguments from the {@code Command} object
     * as input and converts them into arguments that match the target
     * method's argument list.
     *
     * @param command command
     * @return an array of objects representing the arguments for the
     * target method
     */
    protected abstract Object[] getMethodArgs(Command command);
}
