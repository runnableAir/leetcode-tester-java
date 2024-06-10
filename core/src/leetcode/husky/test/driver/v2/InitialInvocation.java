package leetcode.husky.test.driver.v2;

public interface InitialInvocation<T> extends MethodInvocation<T> {

    T invoke(Object... arguments);

    default Object invoke(T ignored, Object... arguments) {
        return invoke(arguments);
    }

    @Override
    default boolean ignoreNullInstance() {
        return true;
    }
}
