package leetcode.husky.test.driver.interpreter.method;

import leetcode.husky.test.driver.interpreter.MethodProxy;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class ConstructorMethodProxy<T> implements MethodProxy<T> {
    /**
     * an instance injector
     */
    private final Consumer<T> instancePublisher;


    protected ConstructorMethodProxy(Consumer<T> instancePublisher) {
        Objects.requireNonNull(instancePublisher);
        this.instancePublisher = instancePublisher;
    }

    @Override
    public T invoke(T t, Object... params) {
        T instance = newInstance(params);
        instancePublisher.accept(instance);
        return null;
    }

    protected abstract T newInstance(Object... methodArguments);
}
