package leetcode.husky.test.runner;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static leetcode.husky.test.runner.MethodProxyAutoCreator.getSimpleTypeName;

// 通过反射自动创建相应的 MethodProxy
// 1. 提供方法名
// 2. 提供方法名+参数列表
// 3. 提供方法签名字符串
public class MethodProxyAutoCreator {

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
        String methodSignature = "public List method1(List,int,int[][],String,String[],List)";
        String className = "leetcode.husky.test.runner.MySolution";
        Class<?> cls = Class.forName(className);
        System.out.println("class name: " + className);
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.equals("method1")) {
                String genericString = method.toString();
                genericString =genericString.replaceAll("java\\.util\\.(\\w+)", "$1");
                genericString =genericString.replaceAll("java\\.lang\\.(\\w+)", "$1");
                genericString = genericString.replaceAll(className +  "\\.", "");
                if (genericString.equals(methodSignature)) {
                    System.out.print("==> ");
                }
                System.out.println(genericString);
            }
        }
    }
}
