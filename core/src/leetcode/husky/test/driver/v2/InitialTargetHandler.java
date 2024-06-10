package leetcode.husky.test.driver.v2;

public class InitialTargetHandler<T> extends MethodInvokeHandler<T> {

    public InitialTargetHandler(MethodInvocation<T> methodInvocation, ArgumentResolver argumentResolver) {
        super(methodInvocation, argumentResolver);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(MethodInvokeRequest methodInvokeRequest) {
        T newInstance = (T) super.handle(methodInvokeRequest);
        getMethodInvokeContext().setTarget(newInstance);
        return newInstance;
    }
}
