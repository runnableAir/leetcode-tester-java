package leetcode.husky.test.driver.interpreter;


import leetcode.husky.test.driver.interpreter.method.ConstructorMethodProxyImpl;
import leetcode.husky.test.driver.interpreter.method.NewInstanceFunc;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MethodProxyRegistry<T> {
    private final Map<String, MethodProxyRegistration<T>> methodProxyDefinitionMap = new HashMap<>();
    private final Consumer<T> instancePublisher;
    private String defaultMethodName;
    private String defaultConstructorName;


    /**
     * Create an instance of the <code>MethodProxyRegistry</code> class with a specific
     * instance publisher.
     * <p>
     * The instance publisher, declared as a Consumer interface, accepts the instance to
     * be published via its "accept" method, used by the constructor method proxy to
     * publish an instance after its creation, placing it in the current context to
     * fulfill the request of new instance.
     *
     * @param instancePublisher a {@link Consumer} to implement of publishing instance
     */
    public MethodProxyRegistry(Consumer<T> instancePublisher) {
        this.instancePublisher = instancePublisher;
    }

    /**
     * 根据方法名、参数列表注册一个方法, 并返回一个用于注册方法代理的接口
     * <p>
     * 该参数列表中的元素需实现 {@link ParamType} 接口
     *
     * @param name       方法名
     * @param paramTypes 参数列表
     * @return 返回一个接受下一步选项的接口
     */
    public ThenMethodImpl<T> addMethod(String name, ParamType<?>... paramTypes) {
        return methodProxy -> {
            MethodProxyRegistration<T> method = new MethodProxyRegistration<>(name, methodProxy, List.of(paramTypes));
            methodProxyDefinitionMap.put(name, method);
            if (defaultMethodName == null) {
                defaultMethodName = name;
            }
            return MethodProxyRegistry.this;
        };
    }

    public ThenConstructorImpl<T> addConstructor(String name, ParamType<?>... paramTypes) {
        return newInstanceFunc -> {
            MethodProxy<T> constructor = new ConstructorMethodProxyImpl<>(instancePublisher, newInstanceFunc);
            MethodProxyRegistration<T> method = new MethodProxyRegistration<>(name, constructor, List.of(paramTypes));
            methodProxyDefinitionMap.put(name, method);
            if (defaultConstructorName == null) {
                defaultConstructorName = name;
            }
            return MethodProxyRegistry.this;
        };
    }

    public void addRegistration(MethodProxyRegistration<T> registration) {
        methodProxyDefinitionMap.put(registration.name(), registration);
    }

    public MethodProxyRegistration<T> getRegistration(String name) {
        return methodProxyDefinitionMap.get(name);
    }

    public MethodProxyRegistration<T> getMethodProxyDefinitionByDefault() {
        if (defaultMethodName == null) {
            return null;
        }
        return methodProxyDefinitionMap.get(defaultMethodName);
    }

    public MethodProxyRegistration<T> getConstructorRegistration() {
        if (defaultConstructorName == null) {
            return null;
        }
        return methodProxyDefinitionMap.get(defaultConstructorName);
    }

    public interface ThenConstructorImpl<T> {
        MethodProxyRegistry<T> impl(NewInstanceFunc<T> newInstanceFunc);
    }

    public interface ThenMethodImpl<T> {
        /**
         * 为注册方法添加实现逻辑
         *
         * @param methodProxy methodProxy 对象. eg: (object, params) -> object.foo(params[0], params[1]...)
         * @return 当前 MethodProxyRegistry
         */
        MethodProxyRegistry<T> impl(MethodProxy<T> methodProxy);
    }
}