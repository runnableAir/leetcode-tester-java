package test.leetcode.util;

import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;

public interface MethodProxyConfiguration<T> {

    void applyTo(MethodProxyRegistry<T> methodProxyRegistry);
}
