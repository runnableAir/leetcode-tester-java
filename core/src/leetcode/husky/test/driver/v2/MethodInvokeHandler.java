package leetcode.husky.test.driver.v2;

import java.util.Objects;

public class MethodInvokeHandler<T> {
    private final MethodInvocation<T> methodInvocation;
    private final ArgumentResolver argumentResolver;
    private MethodInvokeContext<T> methodInvokeContext;


    public MethodInvokeHandler(MethodInvocation<T> methodInvocation, ArgumentResolver argumentResolver) {
        this.methodInvocation = methodInvocation;
        this.argumentResolver = argumentResolver;
    }

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

    void register(MethodInvokeContext<T> context) {
        this.methodInvokeContext = context;
    }

    public interface ArgumentResolver {

        Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest);
    }
}
