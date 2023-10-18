package leetcode.husky.test.driver.interpreter;

public interface NewInstanceFunc<T> {
    T apply(Object... params);
}