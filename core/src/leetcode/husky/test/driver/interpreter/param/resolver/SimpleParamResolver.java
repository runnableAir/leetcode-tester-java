package leetcode.husky.test.driver.interpreter.param.resolver;

public interface SimpleParamResolver<T> extends ParamResolver<T> {

    T resolveOneArg(String arg);

    @Override
    default T resolve(String... args) {
        return resolveOneArg(args[0]);
    }

    @Override
    default int argumentCount() {
        return 1;
    }
}
