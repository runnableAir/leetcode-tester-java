package leetcode.husky.test.driver.interpreter.method;

public interface NonReturnMethodProxy<T> extends MethodProxy<T> {

    void nonReturnInvoke(T t, Object... params);

    @Override
    default Object invoke(T t, Object... params) {
        nonReturnInvoke(t, params);
        return null;
    }
}
