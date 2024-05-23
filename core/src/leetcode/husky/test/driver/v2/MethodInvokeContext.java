package leetcode.husky.test.driver.v2;

public interface MethodInvokeContext<T> {

    T getTarget();

    void setTarget(T t);
}
