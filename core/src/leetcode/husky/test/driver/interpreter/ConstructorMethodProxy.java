package leetcode.husky.test.driver.interpreter;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class ConstructorMethodProxy<T> implements MethodProxy<T> {
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

    public abstract T newInstance(Object... methodArguments);
}
