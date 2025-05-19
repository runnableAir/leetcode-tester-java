package leetcode.husky.test;

import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

public class LeetCodeNormalProblemTester<T> {
    private final Class<T> solutionClass;


    public LeetCodeNormalProblemTester(Class<T> solutionClass) {
        this.solutionClass = solutionClass;
    }

    public void test(Reader reader) {
        TestUtil.<T>testForMethod(reader, config -> configByReflecting(config, solutionClass));
    }

    static <T> void configByReflecting(MethodProxyRegistry<T> config, Class<T> targetClass) {
        Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
        if (constructors.length != 1) {
            throw new IllegalStateException(
                    "There must be only one constructor which is used as default constructor");
        }
        //noinspection unchecked
        Constructor<T> defaultConstructor = (Constructor<T>) constructors[0];
        Parameter[] initParameter = defaultConstructor.getParameters();
        if (initParameter.length != 0) {
            throw new IllegalStateException(
                    "There must be no argument declared on the default constructor");
        }
        config
                .addConstructor(targetClass.getSimpleName())
                .impl(args -> TestUtil.callConstructor(defaultConstructor));
        // find the target method if there is only one public method
        Method targetMethod = getTargetMethod(targetClass);
        Parameter[] targetParameter = targetMethod.getParameters();
        if (targetParameter.length == 0) {
            throw new IllegalStateException("The target method should have at least one argument declared");
        }
        config
                .addMethod(targetMethod.getName(), TestUtil.resolveParametersType(targetParameter))
                .impl((obj, args) -> TestUtil.callMethod(targetMethod, obj, args));
    }

    private static <T> Method getTargetMethod(Class<T> targetClass) {
        Method targetMethod = null;
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.getModifiers() != Modifier.PUBLIC) {
                continue;
            }
            if (targetMethod != null) {
                throw new IllegalStateException(
                        "There more than one public method so that it can not determine which is the target one");
            }
            targetMethod = method;
        }
        if (targetMethod == null) {
            throw new IllegalStateException("No any public method to be the target");
        }
        return targetMethod;
    }
}
