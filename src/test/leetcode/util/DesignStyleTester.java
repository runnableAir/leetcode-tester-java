package test.leetcode.util;

import leetcode.husky.test.driver.interpreter.MethodProxy;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.NewInstanceFunc;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.function.Predicate;

public class DesignStyleTester<T> {
    private final MethodProxyRegistry<T> methodProxyRegistry = new MethodProxyRegistry<>(this::updateInstance);
    private T instance;


    /**
     * 反射获取所有 public 修饰的方法并注册
     * <p>
     * 方法类型: 成员方法和构造方法
     *
     * @param objClass 提供待注册 public 方法的类
     */
    public void registerAllPublicMethods(Class<T> objClass) {
        // for constructor
        Constructor<?>[] publicConstructors = Arrays.stream(objClass.getDeclaredConstructors())
                .filter(publicMethodFilter())
                .toArray(Constructor[]::new);
        int constNum = publicConstructors.length;
        if (constNum != 1) {
            throw new RuntimeException("required and only required 1 public constructor, found " + constNum);
        }
        // noinspection unchecked
        Constructor<T> constructor = (Constructor<T>) publicConstructors[0];
        registerReflectingConstructor(constructor);

        // for member methods
        Arrays.stream(objClass.getDeclaredMethods())
                .filter(publicMethodFilter())
                .forEach(this::registerReflectingMethod);
    }

    private static Predicate<Executable> publicMethodFilter() {
        return executable -> Modifier.isPublic(executable.getModifiers());
    }

    private void registerReflectingMethod(Method method) {
        System.out.println("[info] DesignStyleTester register method: " + method);
        methodProxyRegistry.addMethod(method.getName(), resolveExecutableParameters(method))
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

    private void registerReflectingConstructor(Constructor<T> constructor) {
        methodProxyRegistry.addConstructor(constructor.getName(), resolveExecutableParameters(constructor))
                .impl(reflectingConstructorImplementer(constructor));
    }

    private static <T> NewInstanceFunc<T> reflectingConstructorImplementer(Constructor<T> constructor) {
        return params -> {
            try {
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
    private ParamType<?>[] resolveExecutableParameters(Executable executable) {
        return Arrays.stream(executable.getParameters())
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
