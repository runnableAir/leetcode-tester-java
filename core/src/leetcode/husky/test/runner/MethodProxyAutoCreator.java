package leetcode.husky.test.runner;

import leetcode.husky.test.driver.interpreter.MethodProxy;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import static leetcode.husky.test.runner.MethodProxyAutoCreator.getSimpleTypeName;

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
     * @param paramTypes parameter types of the method
     */
    public MethodProxy<T> createMethod(String methodName, ParamType<?>... paramTypes) throws NoSuchMethodException {
        Class<?>[] formalParameterTypes = getFormalParameterTypes(paramTypes);
        // get public member method with specific name and parameter types
        Method method = declaredType.getDeclaredMethod(methodName, formalParameterTypes);
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

    private Class<?>[] getFormalParameterTypes(ParamType<?>[] paramTypes) {
        int len = paramTypes.length;
        Class<?>[] formalTypes = new Class[len];
        for (int i = 0; i < len; i++) {
            formalTypes[i] = getFormalParamType(paramTypes[i]);
        }
        return formalTypes;
    }

    private Class<?> getFormalParamType(ParamType<?> paramType) {
        throw new RuntimeException("unimplemented");
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

@SuppressWarnings("unused")
class MySolution {
    public List<List<Integer>> method1(
            List<Integer> param1,
            int param2,
            int[][] param3,
            String param4,
            String[] param5,
            List<String> param6) {
        throw new RuntimeException();
    }

    public List<List<Integer>> method1(int param1, int[][] param2) {
        throw new RuntimeException();
    }

    static void methodByReflecting(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> clazz = MySolution.class;
        Method m = clazz.getMethod(methodName, parameterTypes);
        Type returnType = m.getGenericReturnType();
        String returnTypename = getSimpleTypeName(returnType);
        System.out.println("Method Name = " + methodName);
        System.out.println("Return Type = " + returnTypename);

        Parameter[] parameters = m.getParameters();
        for (Parameter param : parameters) {
            Type type = param.getParameterizedType();
            String simpleName = getSimpleTypeName(type);
            System.out.println(simpleName + " " + param.getName());
        }

    }

    public static void main(String[] args) throws Exception {
        // 泛型擦除导致无法读取运行时的泛型...
        // 通过继承(匿名内部类也是继承)父类并声明父类泛型的具体类型可以保留泛型...
        ParamType<int[]> paramType = new ParamType<int[]>() {
            @Override
            public int[] resolveOneArg(String arg) {
                return ParamType.INT_ARRAY.resolve(arg);
            }
        };
        Class<?> clazz = paramType.getClass();
        System.out.println(clazz.getName());
        Arrays.stream(clazz.getMethods())
                .map(MySolution::simpleMethodSignature)
                .forEach(System.out::println);
    }

    private static String simpleMethodSignature(Method method) {
        // System.out.println(" debug: " + method);
        Class<?> declaringClass = method.getDeclaringClass();
        String className = declaringClass.getName();
        String simpleClassName = declaringClass.getSimpleName();
        String genericString = method.toGenericString();
        genericString = genericString.replace("java.util.", "");
        genericString = genericString.replace("java.lang.", "");
        if (simpleClassName.isEmpty()) {
            genericString = genericString.replace(className + ".", "");
        } else {
            genericString = genericString.replace(className, simpleClassName);
        }
        return genericString;
    }
}
