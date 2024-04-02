package leetcode.husky.test.runner;

import leetcode.husky.test.driver.interpreter.MethodProxy;

import java.lang.reflect.*;

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

    private MethodProxy<T> wrapReflectingMethodToProxy(Method method) {
        return (t, params) -> {
            Object returnValue;
            try {
                returnValue = method.invoke(t, params);
            } catch (InvocationTargetException e) {
                throw new MethodReflectingInvokeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return returnValue;
        };
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

