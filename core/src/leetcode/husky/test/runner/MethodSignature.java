package leetcode.husky.test.runner;

import java.util.List;
import java.util.stream.Collectors;

public record MethodSignature(
        String name,
        String returnType,
        List<String> parameterTypes) {

    public MethodSignature(String name, String returnType, String... parameterTypes) {
        this(name, returnType, List.of(parameterTypes));
    }

    @Override
    public String toString() {
        return returnType + " " + name + parameterTypes.stream()
                .collect(Collectors.joining(",", "(", ")"));
    }

    public int getParameterCount() {
        return parameterTypes.size();
    }
}
