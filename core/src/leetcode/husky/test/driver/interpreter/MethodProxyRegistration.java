package leetcode.husky.test.driver.interpreter;

import leetcode.husky.test.driver.interpreter.param.resolver.ParamResolver;

import java.util.List;

/**
 * 封装方法代理注册所需的信息
 *
 * @param name              方法名称
 * @param methodProxy       方法代理对象
 * @param paramResolvers 为方法代理对象提供所需参数的解析器列表
 * @param <T>               方法所属实例的类型
 */
public record MethodProxyRegistration<T>(
        String name,
        MethodProxy<T> methodProxy,
        List<ParamResolver<?>> paramResolvers) {

}
