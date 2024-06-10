package leetcode.husky;

import leetcode.husky.test.driver.v2.InitialInvocation;
import leetcode.husky.test.driver.v2.InitialTargetHandler;
import leetcode.husky.test.driver.v2.MethodInvocation;
import leetcode.husky.test.driver.v2.MethodInvokeHandler;
import leetcode.husky.test.driver.v2.MethodInvokeRequest;
import leetcode.husky.test.driver.v2.ObjectCommandDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Provide simple config for v2.ObjectCommandDriver by `config.apply(driver)`
public class ObjectCommandDriverSimpleConfig<T> {
    private final Map<String, MethodInvokeHandler<T>> handlerMap = new HashMap<>();


    public void addMethod(String key,
                          MethodInvocation<T> methodInvocation,
                          StringConverter... stringConverters) {
        var handler = new SimpleMethodInvokeHandler<>(methodInvocation, List.of(stringConverters));
        handlerMap.put(key, handler);
    }

    public void addConstructor(String key,
                               InitialInvocation<T> initialInvocation,
                               StringConverter... stringConverters) {
        var handler = new SimpleInitialTargetHandler<>(initialInvocation, List.of(stringConverters));
        handlerMap.put(key, handler);
    }

    void applyTo(ObjectCommandDriver<T> objectCommandDriver) {
        for (var invokeHandlerEntry : handlerMap.entrySet()) {
            String targetMethodKey = invokeHandlerEntry.getKey();
            MethodInvokeHandler<T> handler = invokeHandlerEntry.getValue();
            objectCommandDriver.addHandler(targetMethodKey, handler);
        }
    }


    public interface StringConverter {

        Object convert(String argument);
    }

    static class SimpleMethodInvokeHandler<T> extends MethodInvokeHandler<T> {

        SimpleMethodInvokeHandler(MethodInvocation<T> methodInvocation,
                                  List<StringConverter> converterList) {
            super(methodInvocation, new SimpleArgumentResolver(converterList));
        }
    }

    static class SimpleInitialTargetHandler<T> extends InitialTargetHandler<T> {

        SimpleInitialTargetHandler(MethodInvocation<T> methodInvocation,
                                   List<StringConverter> converterList) {
            super(methodInvocation, new SimpleArgumentResolver(converterList));
        }
    }

    static class SimpleArgumentResolver implements MethodInvokeHandler.ArgumentResolver {
        private final List<StringConverter> converterList;

        SimpleArgumentResolver(List<StringConverter> converterList) {
            this.converterList = converterList;
        }

        @Override
        public Object[] resolveArguments(MethodInvokeRequest methodInvokeRequest) {
            List<String> parameters = methodInvokeRequest.parameters();
            if (converterList.size() != parameters.size()) {
                throw new RuntimeException(
                        "The number of StringConverter doesn't match the number of request parameters");
            }
            var it = converterList.iterator();
            return parameters.stream()
                    .map(str -> it.next().convert(str))
                    .toArray();
        }
    }
}
