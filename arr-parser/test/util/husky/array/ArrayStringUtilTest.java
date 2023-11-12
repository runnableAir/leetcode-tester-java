package util.husky.array;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static util.husky.array.ArrayStringUtil.INT_TYPE;
import static util.husky.array.ArrayStringUtil.STRING_TYPE;

public class ArrayStringUtilTest {

    List<String> invalidIntArray = List.of(
            "[,1,]",
            "[1,2,]",
            "[1,2,a]",
            "[\"1\",\"2\"]",
            "[0,-1,--1]",
            "[0,-1,001]"
    );

    List<String> invalidInt2dArray = List.of(
            "[[1,2],3]",
            "[1,[2,3]]",
            "[[1,2]",
            "[1,2]]",
            "",
            "[[,]]",
            "[[1,]]",
            "[[,1]]",
            "[[,1,]]",
            "[[][]]",
            "[[1,2],[]",
            "[[-1,2],[+1]]",
            "[[-1,2],[-+1]]"
    );

    List<String> invalidStringArray = List.of(
            "[\"test]",
            "[test\"]",
            "[test]",
            "[\"test\",]",
            "[\"test\"\"]",
            "[\"\",\"]",
            "[\"\",\"\"\"]",
            "[\"a\",]",
            "[,\"a\"]",
            "[,]",
            "[[a]]",
            "[\"a\",'c\"]",
            "[\"a\"\"b\"]",
            "[1,\"2]"
    );

    List<String> invalidString2dArray = List.of(
            "[[][]]",
            "[[],[]",
            "[[\"hello\",\"world\"][]]",
            "[[] [\"hello\",\"world\"]]",
            "[[\"1\", \"2], [\"hello\",\"world\"]]",
            "[[\"hello->\"world\"\"]]"
    );

    List<TestCase<List<String>>> stringArrayTestCases = List.of(
            new TestCase<>(
                    "[\"ab\", \"b\"]",
                    List.of("ab", "b")
            ),
            new TestCase<>(
                    "[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]",
                    List.of("print", "(\"", "hello world!", "\")")
            )
    );

    List<TestCase<List<List<String>>>> string2dArrayTestCases = List.of(
            new TestCase<>(
                    "[[\"a\", \"ba\"],[\"ad\", \"b\"]]",
                    List.of(List.of("a", "ba"), List.of("ad", "b"))
            ),
            new TestCase<>(
                    "[[\"a\", \"ba\"],[\"ad\", \"b\"],[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]]",
                    List.of(List.of("a", "ba"), List.of("ad", "b"), List.of("print", "(\"", "hello world!", "\")"))
            )
    );

    List<TestCase<List<Integer>>> intArrayTestCases = List.of(
            new TestCase<>(
                    "[1]",
                    List.of(1)
            ),
            new TestCase<>(
                    "[1,2,12]",
                    List.of(1, 2, 12)
            ),
            new TestCase<>(
                    "[1, 2,  12]",
                    List.of(1, 2, 12)
            ),
            new TestCase<>(
                    "[-1,-2,-3]",
                    List.of(-1, -2, -3)
            )
    );

    List<TestCase<List<List<Integer>>>> int2dArrayTestCases = List.of(
            new TestCase<>("[[1,2],[3,4]]",
                    List.of(List.of(1, 2), List.of(3, 4))),
            new TestCase<>("[[4,2,1,2]]",
                    List.of(List.of(4, 2, 1, 2))
            ),
            new TestCase<>("[[-1,-2,-3]]",
                    List.of(List.of(-1, -2, -3))
            ),
            new TestCase<>("[[-1,-2],[-3]]",
                    List.of(List.of(-1, -2), List.of(-3))
            ),
            new TestCase<>("[[-1],[-2,-3]]",
                    List.of(List.of(-1), List.of(-2, -3))
            )
    );

    @Test
    public void anyArray() {
        List<String> anyArray = List.of(
                "[\"abc\", \"abc\"]",
                "[\"abc\", \"abc\", 1]",
                "[]"
        );
        anyArray.forEach(input -> {
            System.out.println("input: " + input);
            var node = ArrayStringUtil.buildArrayNode(input, 1, INT_TYPE | STRING_TYPE);
            System.out.println("output: " + node + "\n");
        });
    }

    @Test
    public void any2dArray() {
        List<String> any2dArray = List.of(
                "[[\"abc\", \"abc\"],[123,1234556789,-987]]",
                "[ [ \"abc\",  1, 0, -2 ],  [999999999999999], [] ]",
                "[]"
        );
        any2dArray.forEach(input -> {
            System.out.println("input: " + input);
            var node = ArrayStringUtil.buildArrayNode(input, 2, INT_TYPE | STRING_TYPE);
            System.out.println("output: " + node + "\n");
        });
    }

    @Test
    public void invalidArrayString() {
        for (String s : invalidIntArray) {
            var ex = assertThrows(IllegalArgumentException.class, () -> ArrayStringUtil.getIntList(s));
            System.out.println(s + " -> " + ex.getMessage());
        }

        for (String s : invalidInt2dArray) {
            var ex = assertThrows(IllegalArgumentException.class, () -> ArrayStringUtil.getInt2dList(s));
            System.out.println(s + " -> " + ex.getMessage());
        }

        for (String s : invalidStringArray) {
            var ex = assertThrows(IllegalArgumentException.class, () -> ArrayStringUtil.getStringList(s));
            System.out.println(s + " -> " + ex.getMessage());
        }

        for (String s : invalidString2dArray) {
            var ex = assertThrows(IllegalArgumentException.class, () -> ArrayStringUtil.getString2dList(s));
            System.out.println(s + " -> " + ex.getMessage());
        }
    }

    @Test
    public void intArrayString() {
        for (TestCase<List<Integer>> testcase : intArrayTestCases) {
            String input = testcase.input;
            List<Integer> expected = testcase.expected;
            List<Integer> actual = ArrayStringUtil.getIntList(input);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void int2dArrayString() {
        for (TestCase<List<List<Integer>>> testcase : int2dArrayTestCases) {
            String input = testcase.input;
            List<List<Integer>> expected = testcase.expected;
            List<List<Integer>> actual = ArrayStringUtil.getInt2dList(input);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void longArrayString() {
        Random random = new Random();
        int testCaseNum = random.nextInt(20);
        List<TestCase<List<Long>>> testCases = new ArrayList<>();
        for (int i = 0; i < testCaseNum; i++) {
            int amount = random.nextInt(100);
            List<Long> expected = randomLongList(amount);
            testCases.add(new TestCase<>(expected.toString(), expected));
        }

        for (TestCase<List<Long>> testCase : testCases) {
            String input = testCase.input;
            List<Long> expected = testCase.expected;
            List<Long> actual = ArrayStringUtil.getLongList(input);
            System.out.println(input);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void long2dArrayString() {
        Random random = new Random();
        int testCaseNum = random.nextInt(20);
        List<TestCase<List<List<Long>>>> testCases = new ArrayList<>();
        for (int i = 0; i < testCaseNum; i++) {
            int innerListCnt = random.nextInt(10);
            List<List<Long>> expected = new ArrayList<>();
            for (int j = 0; j < innerListCnt; j++) {
                int amount = random.nextInt(10);
                expected.add(randomLongList(amount));
            }
            testCases.add(new TestCase<>(expected.toString(), expected));
        }

        for (TestCase<List<List<Long>>> testCase : testCases) {
            String input = testCase.input;
            List<List<Long>> expected = testCase.expected;
            List<List<Long>> actual = ArrayStringUtil.getLong2dList(input);
            System.out.println(input);
            assertEquals(expected, actual);
        }
    }

    static List<Long> randomLongList(int n) {
        n = (int) (Math.random() * n);
        List<Long> list = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            list.add(r.nextLong(Integer.MAX_VALUE, Long.MAX_VALUE));
        }
        return list;
    }

    @Test
    public void stringArrayString() {
        for (TestCase<List<String>> testcase : stringArrayTestCases) {
            String input = testcase.input;
            List<String> expected = testcase.expected;
            List<String> actual = ArrayStringUtil.getStringList(input);
            assertEquals(expected, actual);
        }
    }

    @Test
    public void string2dArrayString() {
        for (TestCase<List<List<String>>> testcase : string2dArrayTestCases) {
            String input = testcase.input;
            List<List<String>> expected = testcase.expected;
            List<List<String>> actual = ArrayStringUtil.getString2dList(input);
            assertEquals(expected, actual);
        }
    }


    record TestCase<T>(String input, T expected) {
    }
}