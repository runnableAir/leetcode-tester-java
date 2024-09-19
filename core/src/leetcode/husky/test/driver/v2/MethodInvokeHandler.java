package leetcode.husky.test.driver.v2;

import java.util.Objects;

/**
 * A handler for processing method invocation requests.
 *
 * @param <T> the type of the class or interface that declares the method invoked
 *            by this handler
 */
public class MethodInvokeHandler<T> {
    private final MethodInvocation<T> methodInvocation;
    private final ArgumentResolver argumentResolver;
    private MethodInvokeContext<T> methodInvokeContext;


    public MethodInvokeHandler(MethodInvocation<T> methodInvocation, ArgumentResolver argumentResolver) {
        this.methodInvocation = methodInvocation;
        this.argumentResolver = argumentResolver;
    }

    /**
     * Get the arguments from {@code methodInvokeRequest} and pass them to the
     * method being invoked. Invoke the method and return the result.
     *
     * @param methodInvokeRequest method invocation request
     * @return the result of the method invocation
     */
    public Object handle(MethodInvokeRequest methodInvokeRequest) {
        Object[] arguments = resolveArguments(methodInvokeRequest);
        T t = getMethodInvokeContext().getTarget();
        if (t == null && !methodInvocation.ignoreNullInstance()) {
            throw new NullPointerException("The target object in current invoking context is null and can not be " +
                    "ignored. It may need to be initialized at first!");
        }
        return methodInvocation.invoke(t, arguments);
    }

    protected MethodInvokeContext<T> getMethodInvokeContext() {
        return Objects.requireNonNull(methodInvokeContext,
                "The \"methodInvokeContext\" is null. It may indicate a MethodInvokeContext " +
                        "hava not been registered to this object");
    }

    protected Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest) {
        return argumentResolver.resolveArguments(methodInvokeRequest);
    }

    public void register(MethodInvokeContext<T> context) {
        this.methodInvokeContext = context;
    }

    public interface ArgumentResolver {

        Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest);
    }
}
