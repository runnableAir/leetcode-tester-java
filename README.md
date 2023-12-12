## Leetcode tester for java

This project provides a set of classes that create a utility environment,
allowing you to run and test your LeetCode code locally without the need
for manual handling of input and output, both of which are automated for
repeatability.

## Example

[leetcode.1. Two Sum](https://leetcode.cn/problems/two-sum/description/)

```java
import leetcode.husky.test.Tester;
import leetcode.husky.test.driver.interpreter.param.ParamType;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

// Example LeetCode code
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int[] map = new int[100];
        Arrays.fill(map, -1);
        for (int i = 0; i < nums.length; i++) {
            if (map[target - nums[i]] != -1) {
                return new int[]{map[target - nums[i]], i};
            }
            map[nums[i]] = i;
        }
        return null;
    }
}

public class Main {
    public static void main(String[] args) {
        String input = """
                [1,2,3,5,8]
                10
                [1,2,3,5,8]
                6
                """;
        Reader reader = new StringReader(input);
        // Reader reader = new InputStreamReader(System.in);
        // config argument list and testing code and start testing
        Tester.testForMethod(reader, config -> config
                .addMethod("twoSum",
                        ParamType.INT_ARRAY,
                        ParamType.INT)
                .impl((__, params) -> new Solution()
                        .twoSum((int[]) params[0],
                                (int) params[1]))
        );
    }
}
```

