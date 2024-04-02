package leetcode.husky.test.runner;

import org.junit.Test;

public class MethodProxyAutoCreatorTest {

    @Test
    public void testCreateMethodProxyBySignature() throws NoSuchMethodException {
        var creator = new MethodProxyAutoCreator<>(Solution.class);
        var hello = creator.createMethod(
                new MethodSignature("hello", "String", "String"));
        System.out.println(hello);

        var foo = creator.createMethod(
                new MethodSignature("foo", "int[]", "int", "int", "int[][]"));
        System.out.println(foo);
    }

}

@SuppressWarnings("unused")
class Solution {

    public int[] foo(int a, int b, int[][] c) {
        return new int[1];
    }

    public String hello(String name) {
        return "hello " + name;
    }
}