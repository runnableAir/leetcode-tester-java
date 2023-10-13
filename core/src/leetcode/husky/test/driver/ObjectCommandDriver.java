package leetcode.husky.test.driver;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.driver.interpreter.MethodInterpreter;

public class ObjectCommandDriver<T> implements CommandDriver {
    private final MethodInterpreter<T> methodInterpreter;
    private T instance;


    public ObjectCommandDriver(MethodInterpreter<T> methodInterpreter) {
        this.methodInterpreter = methodInterpreter;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    @Override
    public Object execute(Command command) {
        return methodInterpreter.process(instance, command);
    }
}
