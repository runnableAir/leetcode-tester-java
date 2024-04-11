package test.leetcode.util;

import leetcode.husky.test.driver.interpreter.MethodProxy;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.function.Predicate;

public class DesignStyleTester<T> {
    private final MethodProxyRegistry<T> methodProxyRegistry = new MethodProxyRegistry<>(this::updateInstance);
    private T instance;


    /***
     * 反射获取所有 public 修饰的方法并注册
     * @param objClass 提供待注册 public 方法的类
     */
    void registerAllPublicMethods(Class<T> objClass) {
        Arrays.stream(objClass.getDeclaredMethods())
                .filter(publicMethodFilter())
                .forEach(this::registerReflectingMethod);
    }

    private static Predicate<Method> publicMethodFilter() {
        return method -> Modifier.isPublic(method.getModifiers());
    }

    private void registerReflectingMethod(Method method) {
        System.out.println("[info] DesignStyleTester register method: " + method);
        methodProxyRegistry.addMethod(method.getName(), resolveMethodParameters(method))
                .impl(reflectingMethodProxyImplementer(method));
    }

    private MethodProxy<T> reflectingMethodProxyImplementer(Method method) {
        return (t, params) -> {
            try {
                return method.invoke(t, params);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("cannot invoke reflecting method: " + method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 解析方法的参数列表并返回对应的 ParamType 对象
     * <p>
     * 该方法遍历参数列表的所有的参数类型，然后根据其名称(包含泛型信息)获取对应的 ParamType 对象
     *
     * @param method 表示待解析方法的 Method 对象
     * @return 包含对应 ParamType 对象的数组
     */
    private ParamType<?>[] resolveMethodParameters(Method method) {
        return Arrays.stream(method.getParameters())
                .map(Parameter::getParameterizedType)
                .map(Type::getTypeName)
                .map(this::resolveParameterTypeByName)
                .toArray(ParamType<?>[]::new);
    }

    private ParamType<?> resolveParameterTypeByName(String typeName) {
        return switch (typeName) {
            case "java.lang.String" -> ParamType.STRING;
            case "java.lang.String[]" -> ParamType.STRING_ARRAY;
            case "java.util.List<java.lang.String>" -> ParamType.STRING_LIST;
            case "int" -> ParamType.INT;
            case "java.util.List<java.lang.Integer>" -> ParamType.INT_LIST;
            case "int[]" -> ParamType.INT_ARRAY;
            case "int[][]" -> ParamType.INT_2D_ARRAY;
            default -> throw new IllegalStateException("Unsupported type name: " + typeName);
        };
    }

    private void updateInstance(T instance) {
        this.instance = instance;
    }
}