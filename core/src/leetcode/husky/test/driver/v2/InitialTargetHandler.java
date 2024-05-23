package leetcode.husky.test.driver.v2;

public abstract class InitialTargetHandler<T> extends MethodInvokeHandler<T> {

    protected InitialTargetHandler(InitialInvocation<T> initialInvocation) {
        super(initialInvocation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(MethodInvokeRequest methodInvokeRequest) {
        T newInstance = (T) super.handle(methodInvokeRequest);
        getMethodInvokeContext().setTarget(newInstance);
        return newInstance;
    }

    @Override
    protected abstract Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest);
}
