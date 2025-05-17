package leetcode.husky.test;

import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;
import leetcode.husky.test.driver.interpreter.param.ParamType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class LeetCodeDesignProblemTesterTests {

    @Test
    public void test01_configByReflecting() {
        Class<LRUCache> clazz = LRUCache.class;
        MethodProxyRegistry<LRUCache> registry = new MethodProxyRegistry<>(t -> {
        });
        LeetCodeDesignProblemTester.configByReflecting(registry, clazz);

        var constructor = registry.getConstructorRegistration();
        Assert.assertNotNull("constructor [public LRUCache(int)] does not exists", constructor);
        Assert.assertEquals("LRUCache", constructor.name());
        Assert.assertEquals(List.of(ParamType.INT), constructor.argumentResolvers());

        var get = registry.getRegistration("get");
        Assert.assertNotNull("method [public int get(int)] does not exists", get);
        Assert.assertEquals("get", get.name());
        Assert.assertEquals(List.of(ParamType.INT), get.argumentResolvers());

        var put = registry.getRegistration("put");
        Assert.assertNotNull("method [public void pub(int, int)] does not exists", put);
        Assert.assertEquals("put", put.name());
        Assert.assertEquals(List.of(ParamType.INT, ParamType.INT), put.argumentResolvers());
    }
}

class LRUCache {

    public LRUCache(int capacity) {

    }

    public int get(int key) {
        return -1;
    }

    public void put(int key, int value) {

    }
}

