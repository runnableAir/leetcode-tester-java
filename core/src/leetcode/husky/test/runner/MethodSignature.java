package leetcode.husky.test.runner;

import java.util.List;
import java.util.stream.Collectors;

public record MethodSignature(
        String name,
        List<String> parameterTypes) {

    public MethodSignature(String name, String... parameterTypes) {
        this(name, List.of(parameterTypes));
    }

    @Override
    public String toString() {
        return name + parameterTypes.stream()
                .collect(Collectors.joining(",", "(", ")"));
    }

    public int getParameterCount() {
        return parameterTypes.size();
    }
}
