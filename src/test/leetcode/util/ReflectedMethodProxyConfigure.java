package test.leetcode.util;

import leetcode.husky.test.driver.interpreter.MethodProxy;
import leetcode.husky.test.driver.interpreter.NewInstanceFunc;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectedMethodProxyConfigure<T> {
    private final List<MethodProxyConfiguration<T>> configList = new ArrayList<>();

    public void addMethod(Method method) {
        configList.add(methodProxyRegistry -> {
            var methodName = method.getName();
            var parameterTypes = getParameterTypes(method);
            methodProxyRegistry.addMethod(methodName, parameterTypes)
                    .impl(methodProxyImplementer(method));
        });
    }

    public void addConstructor(Constructor<T> constructor) {
        configList.add(methodProxyRegistry -> {
            var methodName = getDeclaringClassSimpleName(constructor);
            var parameterTypes = getParameterTypes(constructor);
            methodProxyRegistry.addConstructor(methodName, parameterTypes)
                    .impl(newInstanceFuncImplementer(constructor));
        });
    }

    public MethodProxyConfiguration<T> getMethodProxyConfiguration() {
        return methodProxyRegistry -> configList.forEach(config -> config.applyTo(methodProxyRegistry));
    }

    private String getDeclaringClassSimpleName(Constructor<?> constructor) {
        return constructor.getDeclaringClass().getSimpleName();
    }


    private MethodProxy<T> methodProxyImplementer(Method method) {
        return (t, params) -> {
            try {
                method.setAccessible(true);
                return method.invoke(t, params);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("cannot invoke reflecting method: " + method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }


    public NewInstanceFunc<T> newInstanceFuncImplementer(Constructor<T> constructor) {
        return params -> {
            try {
                constructor.setAccessible(true);
                return constructor.newInstance(params);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("cannot get a new instance by invoking constructor: " + constructor, e);
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
     * @param executable 表示待解析方法的 Executable 对象
     * @return 包含对应 ParamType 对象的数组
     */
    private ParamType<?>[] getParameterTypes(Executable executable) {
        return Arrays.stream(executable.getParameters())
                .map(Parameter::getParameterizedType)
                .map(Type::getTypeName)
                .map(this::getParamTypeByGenericTypeName)
                .toArray(ParamType<?>[]::new);
    }

    private ParamType<?> getParamTypeByGenericTypeName(String typeName) {
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
}

