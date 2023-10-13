package leetcode.husky.test.driver.interpreter.method;

public interface NewInstanceFunc<T> {
    T apply(Object... params);
}