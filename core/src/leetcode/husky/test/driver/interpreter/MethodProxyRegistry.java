package leetcode.husky.test.driver.interpreter;


import leetcode.husky.test.driver.interpreter.param.ParamType;
import leetcode.husky.test.driver.interpreter.param.resolver.ArgumentResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * MethodProxyRegistry is a class that registers the method proxy of a specific
 * type instance.
 * <p>
 * The registration of a method proxy requires providing of its name for
 * retrieving itself and its parameter type(s) specified by {@link ParamType} object
 * which implements {@link ArgumentResolver} interface to convert the input provided
 * by caller to arguments that matches the target method's argument list.
 *
 * @param <T> the type of the instance
 */
public class MethodProxyRegistry<T> {
    private final Map<String, MethodProxyRegistration<T>> methodProxyDefinitionMap = new HashMap<>();
    private final Consumer<T> instancePublisher;
    private String defaultMethodName;
    private String defaultConstructorName;


    /**
     * Create an instance of this class with a specific instance publisher.
     * <p>
     * The instance publisher, declared as a Consumer interface, accepts the instance
     * to be published through its "accept" method. When a constructor method proxy
     * is invoked, this publisher will publish a new instance in the context, where
     * the instance will be stored and retrieved for subsequent method proxy invocations.
     *
     * @param instancePublisher a {@link Consumer} to implement of publishing instance
     */
    public MethodProxyRegistry(Consumer<T> instancePublisher) {
        this.instancePublisher = instancePublisher;
    }

    /**
     * Add a method.
     * <p>
     * By specifying the proxy name of the target method and a set of
     * parameter types that match the target method's parameter list,
     * obtain a register to register a {@link MethodProxy} object that implements
     * the proxy for the target method.
     *
     * @param name       The proxy name of the target method
     * @param paramTypes A set of parameter types that match the target method's parameter list
     * @return Method<T> The register used to register the {@link MethodProxy} object
     */
    public Method<T> addMethod(String name, ParamType<?>... paramTypes) {
        return methodProxy -> {
            MethodProxyRegistration<T> method = new MethodProxyRegistration<>(name, methodProxy, List.of(paramTypes));
            methodProxyDefinitionMap.put(name, method);
            if (defaultMethodName == null) {
                defaultMethodName = name;
            }
            return MethodProxyRegistry.this;
        };
    }

    /**
     * Add a constructor method.
     * <p>
     * By specifying the <b>proxy name</b> of the target method and a set of
     * <b>parameter types</b> that match the target method's parameter list,
     * obtain a register to register a {@link NewInstanceFunc} object that returns
     * a new instance of type T.
     *
     * @param name       The proxy name of the target method
     * @param paramTypes A set of parameter types that match the target method's parameter list
     * @return Constructor<T> The register used to register the
     * {@link NewInstanceFunc} object
     */
    public Constructor<T> addConstructor(String name, ParamType<?>... paramTypes) {
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

    public MethodProxyRegistration<T> getMethodRegistrationByDefault() {
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

    public interface Constructor<T> {
        MethodProxyRegistry<T> impl(NewInstanceFunc<T> newInstanceFunc);
    }

    public interface Method<T> {
        /**
         * 为注册方法添加实现逻辑
         *
         * @param methodProxy methodProxy 对象. eg: (object, params) -> object.foo(params[0], params[1]...)
         * @return 当前 MethodProxyRegistry
         */
        MethodProxyRegistry<T> impl(MethodProxy<T> methodProxy);

        default MethodProxyRegistry<T> voidImpl(VoidMethodProxy<T> methodProxy) {
            return impl(methodProxy);
        }
    }

    private static class ConstructorMethodProxyImpl<T> extends ConstructorMethodProxy<T> {
        private final NewInstanceFunc<T> newInstanceFunc;


        public ConstructorMethodProxyImpl(Consumer<T> instancePublisher, NewInstanceFunc<T> newInstanceFunc) {
            super(instancePublisher);
            this.newInstanceFunc = newInstanceFunc;
        }

        @Override
        public T newInstance(Object... methodArguments) {
            return newInstanceFunc.apply(methodArguments);
        }
    }
}