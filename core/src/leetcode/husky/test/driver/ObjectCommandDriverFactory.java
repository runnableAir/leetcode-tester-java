package leetcode.husky.test.driver;


import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.CommonMethodInterpreter;

public class ObjectCommandDriverFactory<T> {
    private final MethodProxyRegistry<T> methodProxyRegistry;
    private final ObjectCommandDriver<T> objectCommandDriver;


    public ObjectCommandDriverFactory() {
        methodProxyRegistry = new MethodProxyRegistry<>(t -> getDriver().setInstance(t));
        objectCommandDriver = new ObjectCommandDriver<>(new CommonMethodInterpreter<>(methodProxyRegistry));
    }

    public MethodProxyRegistry<T> getMethodProxyRegistry() {
        return methodProxyRegistry;
    }

    public ObjectCommandDriver<T> getDriver() {
        return objectCommandDriver;
    }
}
