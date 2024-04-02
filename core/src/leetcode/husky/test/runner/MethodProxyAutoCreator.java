package leetcode.husky.test.runner;

import leetcode.husky.test.driver.interpreter.MethodProxy;

import java.lang.reflect.*;
import java.util.List;
import java.util.function.Function;

public class MethodProxyAutoCreator<T> {

    private final Class<T> declaredType;

    public MethodProxyAutoCreator(Class<T> declaredType) {
        this.declaredType = declaredType;
    }


    /**
     * Create a {@code MethodProxy} object according to specific
     * name and parameter types of method declared in the type {@code T}
     * <p>
     * This method is reflected through {@linkplain Class#getDeclaredMethod(String, Class[])}
     * to find the appropriate {@linkplain Method} object
     *
     * @param methodName name of the method
     * @param formalParameterTypes parameter types of the method
     */
    public MethodProxy<T> createMethod(String methodName, Class<?>... formalParameterTypes) throws NoSuchMethodException {
        Method method = declaredType.getDeclaredMethod(methodName, formalParameterTypes);
        if (!Modifier.isPublic(method.getModifiers())) {
            throw new IllegalStateException("create proxy for method " + methodName +
                    " failed: method should be public");
        }
        return wrapReflectingMethodToProxy(method);
    }

    /**
     * Create a {@code MethodProxy} object according to specific
     * method signature (name and parameter types) of method declared in the type {@code T}
     * <p>
     * This method is reflected through {@linkplain Class#getDeclaredMethod(String, Class[])}}
     * to find the appropriate {@linkplain Method} object
     *
     * @param methodSignature method signature
     */
    public MethodProxy<T> createMethod(MethodSignature methodSignature) throws NoSuchMethodException {
        String target = methodSignature.name();
        List<String> parameterTypes = methodSignature.parameterTypes();
        var formalTypes = parameterTypes.stream()
                .map(simpleNameToFormalTypeMapper())
                .toArray(Class[]::new);
        return createMethod(target, formalTypes);
    }

    private static Function<String, Class<?>> simpleNameToFormalTypeMapper() {
        return typeName -> switch (typeName) {
            case "int" -> int.class;
            case "int[]" -> int[].class;
            case "int[][]" -> int[][].class;
            case "String" -> String.class;
            case "String[]" -> String[].class;
            case "List" -> List.class;
            default -> throw new IllegalStateException("no such type: " + typeName);
        };
    }

    private MethodProxy<T> wrapReflectingMethodToProxy(Method method) {
        return new MethodProxy<>() {
            private final Method reflectingMethod = method;

            @Override
            public Object invoke(T t, Object... params) {
                Object returnValue;
                try {
                    returnValue = reflectingMethod.invoke(t, params);
                } catch (InvocationTargetException e) {
                    throw new MethodReflectingInvokeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                return returnValue;
            }

            @Override
            public String toString() {
                return super.toString()
                        + " reflecting method: "
                        + reflectingMethod.toGenericString();
            }
        };
    }
}

