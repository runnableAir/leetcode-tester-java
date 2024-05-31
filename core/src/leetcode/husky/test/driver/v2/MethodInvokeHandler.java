package leetcode.husky.test.driver.v2;

import java.util.Objects;

public abstract class MethodInvokeHandler<T> {
    private final MethodInvocation<T> methodInvocation;
    private MethodInvokeContext<T> methodInvokeContext;


    protected MethodInvokeHandler(MethodInvocation<T> methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    protected Object handle(MethodInvokeRequest methodInvokeRequest) {
        Object[] arguments = resolveArguments(methodInvokeRequest);
        T t = getMethodInvokeContext().getTarget();
        return methodInvocation.invoke(t, arguments);
    }

    protected MethodInvokeContext<T> getMethodInvokeContext() {
        return Objects.requireNonNull(methodInvokeContext,
                "The \"methodInvokeContext\" is null. It may indicate a MethodInvokeContext " +
                        "hava not been registered to this object");
    }

    protected abstract Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest);

    void register(MethodInvokeContext<T> context) {
        this.methodInvokeContext = context;
    }
}
