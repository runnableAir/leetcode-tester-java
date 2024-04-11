package test.leetcode.util;

import leetcode.husky.test.cmd.CommandShell;
import leetcode.husky.test.cmd.reader.MultiTaskCommandReader;
import leetcode.husky.test.driver.ObjectCommandDriver;
import leetcode.husky.test.driver.ObjectCommandDriverFactory;
import leetcode.husky.test.driver.interpreter.MethodProxy;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.NewInstanceFunc;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.io.BufferedReader;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * 用于运行和测试 "设计类" 风格 (功能设计) 的类。
 * <p>
 * 被测试的类具备一些功能, 每个功能对应一个 public 方法，
 * 该类的目标是根据测试输入的要求，执行相应的方法并检验结果。
 * <p>
 * 该类实现的思路是：通过反射将被测试的类中的所有 public 方法注册到
 * {@link ObjectCommandDriver} 对象中， 然后使用 {@link CommandShell}
 * 模型读取样例输入从而执行相应的方法。<br>
 *
 * @param <T> 需要被测试的类的类型
 */
public class DesignStyleTester<T> {
    private final MethodProxyRegistry<T> methodProxyRegistry;
    private final ObjectCommandDriver<T> objectCommandDriver;


    public DesignStyleTester() {
        ObjectCommandDriverFactory<T> objectCommandDriverFactory = new ObjectCommandDriverFactory<>();
        methodProxyRegistry = objectCommandDriverFactory.getMethodProxyRegistry();
        objectCommandDriver = objectCommandDriverFactory.getDriver();
    }

    /**
     * 从 BufferedReader 中读取样例输入文本
     *
     * @param caseInput 用于输入文本字符流的 BufferedReader
     */
    public void test(BufferedReader caseInput) {
        // todo 实现对运行结果的校验
        MultiTaskCommandReader multiTaskCommandReader = new MultiTaskCommandReader();
        CommandShell commandShell = new CommandShell(objectCommandDriver, multiTaskCommandReader);
        commandShell.process(caseInput);
    }

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
                method.setAccessible(true);
                return method.invoke(t, params);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("cannot invoke reflecting method: " + method.getName(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void registerReflectingConstructor(Constructor<T> constructor) {
        String constructorSimpleName = constructor.getDeclaringClass().getSimpleName();
        methodProxyRegistry.addConstructor(constructorSimpleName, resolveExecutableParameters(constructor))
                .impl(reflectingConstructorImplementer(constructor));
    }

    private static <T> NewInstanceFunc<T> reflectingConstructorImplementer(Constructor<T> constructor) {
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
}
