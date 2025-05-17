package leetcode.husky.test;

import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class LeetCodeDesignProblemTester<T> {
    private final Class<T> solutionClass;

    public LeetCodeDesignProblemTester(Class<T> solutionClass) {
        this.solutionClass = solutionClass;
    }

    public void test(Reader reader) {
        TestUtil.<T>testForCommands(reader, config -> configByReflecting(config, solutionClass));
    }

    @SuppressWarnings("unchecked")
    static <T> void configByReflecting(MethodProxyRegistry<T> config, Class<T> targetClass) {
        Constructor<?>[] publicConstructors = targetClass.getConstructors();
        if (publicConstructors.length != 1) {
            throw new IllegalStateException(
                    "There are more than one public constructor which is used as default constructor");
        }
        var defaultConstructor = (Constructor<T>) publicConstructors[0];
        config
                .addConstructor(targetClass.getSimpleName(), TestUtil.resolveParametersType(defaultConstructor.getParameters()))
                .impl(args -> TestUtil.callConstructor(defaultConstructor, args));

        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.getModifiers() == Modifier.PUBLIC) {
                config.addMethod(method.getName(), TestUtil.resolveParametersType(method.getParameters()))
                        .impl((obj, args) -> TestUtil.callMethod(method, obj, args));
            }
        }
    }
}
