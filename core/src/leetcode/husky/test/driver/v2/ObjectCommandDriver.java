package leetcode.husky.test.driver.v2;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.driver.CommandDriver;

import java.util.HashMap;

public class ObjectCommandDriver<T> implements CommandDriver, MethodInvokeContext<T> {
    private T target;
    private final HashMap<String, MethodInvokeHandler<T>> handlerMap = new HashMap<>();


    @Override
    public Object execute(Command command) {
        MethodInvokeRequest methodInvokeRequest = formRequest(command);
        String key = methodInvokeRequest.targetMethodKey();
        MethodInvokeHandler<T> invokeHandler = handlerMap.get(key);
        if (invokeHandler == null) {
            throw new RuntimeException("Handler not found, targetMethodKey=" + key);
        }
        return invokeHandler.handle(methodInvokeRequest);
    }

    public void addHandler(String targetMethodKey, MethodInvokeHandler<T> handler) {
        handlerMap.put(targetMethodKey, handler);
        handler.register(this);
    }

    private MethodInvokeRequest formRequest(Command command) {
        return new MethodInvokeRequest(command.name(), command.args());
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public void setTarget(T t) {
        this.target = t;
    }
}
