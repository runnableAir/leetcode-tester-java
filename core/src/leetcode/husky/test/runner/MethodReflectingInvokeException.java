package leetcode.husky.test.runner;

import java.lang.reflect.InvocationTargetException;

public class MethodReflectingInvokeException extends RuntimeException {
    public MethodReflectingInvokeException(InvocationTargetException cause) {
        super("Runtime exception within user method[test].", cause);
    }
}
