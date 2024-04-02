package leetcode.husky.test.runner;

import org.junit.Test;

import java.util.List;

public class MethodProxyAutoCreatorTest {

    @Test
    public void testCreateMethodProxyBySignature() throws NoSuchMethodException {
        var creator = new MethodProxyAutoCreator<>(Solution.class);
        var hello = creator.createMethod(
                new MethodSignature("hello", "String"));
        System.out.println(hello);

        var foo = creator.createMethod(
                new MethodSignature("foo", "int", "int", "int[][]"));
        System.out.println(foo);

        var bar = creator.createMethod(
                new MethodSignature("bar", "List"));
        System.out.println(bar);
    }

}

@SuppressWarnings("unused")
class Solution {

    public int[] foo(int a, int b, int[][] c) {
        return new int[1];
    }

    public int bar(List<Integer> list) {
        return 0;
    }

    public String hello(String name) {
        return "hello " + name;
    }
}