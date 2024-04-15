package test.leetcode.util;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.driver.interpreter.CommonMethodInterpreter;
import leetcode.husky.test.driver.interpreter.MethodProxyRegistry;

import java.lang.reflect.Method;
import java.util.List;

public class Test {
    public static void main(String[] args) throws NoSuchMethodException {
        var reflectedConfig = new ReflectedMethodProxyConfigure<Solution>();

        Class<Solution> clazz = Solution.class;
        Method binarySearch = clazz.getMethod("binarySearch", int[].class, int.class);
        reflectedConfig.addMethod(binarySearch);
        reflectedConfig.addConstructor(clazz.getDeclaredConstructor());

        var configuration = reflectedConfig.getMethodProxyConfiguration();
        var methodProxyRegistry = new MethodProxyRegistry<>((Solution t) -> {});
        configuration.applyTo(methodProxyRegistry);

        var interpreter = new CommonMethodInterpreter<>(methodProxyRegistry);
        var result = interpreter.process(
                new Solution(),
                new Command("binarySearch", List.of("[1,2,3,3,3,5,8]", "8"))
        );
        System.out.println(result);
    }
}

class Solution {
    public int binarySearch(int[] nums, int target) {
        int n = nums.length;
        int l = 0;
        int r = n - 1;
        while (l < r) {
            int mid = l + r >> 1;
            if (nums[mid] < target) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return nums[r] == target ? r : -1;
    }
}
