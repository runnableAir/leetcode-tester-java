package leetcode.husky.test.driver.interpreter;

public interface VoidMethodProxy<T> extends MethodProxy<T> {

    void voidInvoke(T t, Object... params);

    @Override
    default Object invoke(T t, Object... params) {
        voidInvoke(t, params);
        return null;
    }
}