package leetcode.husky.test.runner;

import leetcode.husky.test.driver.interpreter.MethodProxy;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// 通过反射自动创建相应的 MethodProxy
// 1. 提供方法名
// 2. 提供方法名+参数列表
// 3. 提供方法签名字符串
public class MethodProxyAutoCreator<T> {

    private final Class<T> declaredType;

    public MethodProxyAutoCreator(Class<T> declaredType) {
        this.declaredType = declaredType;
    }


    /**
     * Create a {@code MethodProxy} object according to specific
     * name and parameter types of method declared in the type {@code T}
     * <p>
     * This method is reflected through {@linkplain Class#getMethod(String, Class[])}
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

    public MethodProxy<T> createMethod(MethodSignature methodSignature) throws NoSuchMethodException {
        Method[] methods = declaredType.getDeclaredMethods();
        if (methods.length == 0) {
            throw new NoSuchMethodException();
        }

        String target = methodSignature.name();
        int parameterCount = methodSignature.getParameterCount();
        List<String> parameterTypes = methodSignature.parameterTypes();


        Optional<Method> first = Arrays.stream(methods)
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> getSimpleMethodName(m).equals(target))
                .filter(m -> m.getParameterCount() == parameterCount)
                .filter(m -> checkParametersList(m, parameterTypes))
                .findFirst();
        Method method = first.orElseThrow(() -> new NoSuchElementException("method for signature: " + methodSignature));

        return wrapReflectingMethodToProxy(method);
    }

    private boolean checkParametersList(Method method, List<String> parameterTypes) {
        return Arrays.stream(method.getGenericParameterTypes())
                .map(MethodProxyAutoCreator::getSimpleTypeName)
                .toList()
                .equals(parameterTypes);
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

    static String getSimpleMethodName(Method method) {
        var clazz = method.getDeclaringClass();
        var clazzName = clazz.getName();
        String methodName = method.getName();
        return methodName.replace(clazzName + ".", "");
    }

    static String getSimpleTypeName(Type type) {
        return switch (type) {
            case Class<?> clazz -> clazz.getSimpleName();
            case ParameterizedType parameterizedType -> getSimpleTypeName(parameterizedType);
            default -> throw new RuntimeException("Error for get simple name for type: " + type);
        };
    }

    static String getSimpleTypeName(ParameterizedType parameterizedType) {
        Type type = parameterizedType.getRawType();
        if (!(type instanceof Class<?>)) {
            throw new RuntimeException(
                    "Error for get simple name for parameterized type because its raw type " +
                            "is not a class or interface type");
        }
        String rawTypeName = ((Class<?>) type).getSimpleName();
        StringBuilder sb = new StringBuilder(rawTypeName);
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            return sb.toString();
        }
        sb.append('<');
        for (Type typeArgType : actualTypeArguments) {
            String argTypeName = getSimpleTypeName(typeArgType);
            sb.append(argTypeName).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append('>');
        return sb.toString();
    }
}

