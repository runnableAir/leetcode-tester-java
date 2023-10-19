package leetcode.husky.test.driver.interpreter;


import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.driver.interpreter.param.resolver.ArgumentResolver;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * CommonMethodInterpreter is a class that implement the
 * {@link MethodInterpreter} interface with a {@link MethodProxyRegistry}
 * class that provides {@link MethodProxyRegistration} to access method
 * proxies and their argument resolvers.
 *
 * @param <T> the type of the instance
 */
public class CommonMethodInterpreter<T> extends MethodInterpreter<T> {
    private final MethodProxyRegistry<T> methodProxyRegistry;


    public CommonMethodInterpreter(MethodProxyRegistry<T> methodProxyRegistry) {
        this.methodProxyRegistry = methodProxyRegistry;
    }

    private static <T> Object[] resolveMethodArgs(Command command, MethodProxyRegistration<T> methodProxyRegistration) {
        var argumentResolvers = methodProxyRegistration.argumentResolvers();
        int methodArgCnt = argumentResolvers.size();
        Object[] methodArgs = new Object[methodArgCnt];
        Iterator<String> cmdArgs = command.args().iterator();
        for (int i = 0; i < methodArgCnt; i++) {
            ArgumentResolver<?> resolver = argumentResolvers.get(i);
            methodArgs[i] = doResolve(cmdArgs, resolver);
        }
        return methodArgs;
    }

    private static Object doResolve(Iterator<String> cmdArgs, ArgumentResolver<?> argumentResolver) {
        int n = argumentResolver.argumentCount();
        String[] args = new String[n];
        for (int i = 0; i < n; i++) {
            args[i] = cmdArgs.next();
        }
        return argumentResolver.resolve(args);
    }

    @Override
    protected MethodProxy<T> getMethodProxy(Command command) {
        String name = command.name();
        MethodProxyRegistration<T> registration = methodProxyRegistry.getRegistration(name);
        if (registration == null) {
            throw new NoSuchElementException("no proxy registered for the \"" + command.name() + "\" method");
        }
        return registration.methodProxy();
    }

    @Override
    protected Object[] getMethodArgs(Command command) {
        MethodProxyRegistration<T> methodProxyRegistration = methodProxyRegistry.getRegistration(command.name());
        return resolveMethodArgs(command, methodProxyRegistration);
    }
}
