package leetcode.husky.test.driver.interpreter.method;


import java.util.function.Consumer;

public class ConstructorMethodProxyImpl<T> extends ConstructorMethodProxy<T> {
    private final NewInstanceFunc<T> newInstanceFunc;


    public ConstructorMethodProxyImpl(Consumer<T> instancePublisher, NewInstanceFunc<T> newInstanceFunc) {
        super(instancePublisher);
        this.newInstanceFunc = newInstanceFunc;
    }

    @Override
    protected T newInstance(Object... methodArguments) {
        return newInstanceFunc.apply(methodArguments);
    }
}

