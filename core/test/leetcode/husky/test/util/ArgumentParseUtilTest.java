package leetcode.husky.test.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArgumentParseUtilTest {

    @Test
    public void testInvalidIntList() {
        List<String> invalidStrList = List.of(
                "[,1,]",
                "[1,2,]",
                "[1,2,a]",
                "[\"1\",\"2\"]",
                "[0,-1,--1]",
                "[0,-1,001]"
        );
        invalidStrList.forEach(testerOfInvalidArrayParsing(ArgumentParseUtil::getIntList));
    }

    @Test
    public void testInvalidInt2dList() {
        List<String> invalidStrList = List.of(
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
        invalidStrList.forEach(testerOfInvalidArrayParsing(ArgumentParseUtil::getInt2dList));
    }

    @Test
    public void testInvalidStringList() {
        List<String> invalidStrList = List.of(
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
        invalidStrList.forEach(testerOfInvalidArrayParsing(ArgumentParseUtil::getStringList));
    }

    @Test
    public void testInvalidString2dList() {
        List<String> invalidStrList = List.of(
                "[[][]]",
                "[[],[]",
                "[[\"hello\",\"world\"][]]",
                "[[] [\"hello\",\"world\"]]",
                "[[\"1\", \"2], [\"hello\",\"world\"]]",
                "[[\"hello->\"world\"\"]]"
        );
        invalidStrList.forEach(testerOfInvalidArrayParsing(ArgumentParseUtil::getString2dList));
    }

    @Test
    public void testStringList() {
        List<String> strList = List.of(
                "[\"ab\", \"b\"]",
                "[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]"
        );
        List<List<String>> expectedResults = List.of(
                List.of("ab", "b"),
                List.of("print", "(\"", "hello world!", "\")")
        );
        for (int i = 0; i < strList.size(); i++) {
            String str = strList.get(i);
            List<String> list = ArgumentParseUtil.getStringList(str);
            List<String> expected = expectedResults.get(i);
            Assert.assertEquals(expected, list);
        }
    }

    @Test
    public void testString2dList() {
        var strList = List.of(
                "[[\"a\", \"ba\"],[\"ad\", \"b\"]]",
                // [["a", "ba"],["ad", "b"],["print", "(\"", "hello world!", "\")"]]
                "[[\"a\", \"ba\"],[\"ad\", \"b\"],[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]]"
        );
        var expectedList = List.of(
                List.of(List.of("a", "ba"), List.of("ad", "b")),
                List.of(List.of("a", "ba"), List.of("ad", "b"), List.of("print", "(\"", "hello world!", "\")"))
        );
        strList.forEach(testerOfArrayParsing(expectedList, ArgumentParseUtil::getString2dList));
    }

    @Test
    public void testIntArrayLiked() {
        var strList = List.of(
                "[1]",
                "[1,2,12]",
                "[1, 2,  12]",
                "[-1,-2,-3]"
        );

        // toIntList
        var expectedList = List.of(
                List.of(1),
                List.of(1, 2, 12),
                List.of(1, 2, 12),
                List.of(-1, -2, -3)
        );
        strList.forEach(testerOfArrayParsing(expectedList, ArgumentParseUtil::getIntList));

        // toIntArray
        var expectedList2 = expectedList.stream()
                .map(l -> l.stream().mapToInt(Integer::intValue).toArray())
                .toList();
        strList.forEach(testerOfArrayParsing(
                expectedList2,
                ArgumentParseUtil::getIntArray,
                Assert::assertArrayEquals)
        );
    }

    @Test
    public void testInt2dList() {
        var strList = List.of(
                "[[1,2],[3,4]]",
                "[[4,2,1,2]]",
                "[[-1,-2,-3]]",
                "[[-1,-2],[-3]]",
                "[[-1],[-2,-3]]"
        );
        var expectedList = List.of(
                List.of(List.of(1, 2), List.of(3, 4)),
                List.of(List.of(4, 2, 1, 2)),
                List.of(List.of(-1, -2, -3)),
                List.of(List.of(-1, -2), List.of(-3)),
                List.of(List.of(-1), List.of(-2, -3))
        );
        strList.forEach(testerOfArrayParsing(expectedList, ArgumentParseUtil::getInt2dList));
    }

    @Test
    public void testStringArray() {
        var strList = List.of(
                "[\"ab\", \"b\"]",
                "[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]"
        );
        var expectedList = List.of(
                new String[]{"ab", "b"},
                new String[]{"print", "(\"", "hello world!", "\")"}
        );
        strList.forEach(testerOfArrayParsing(expectedList, ArgumentParseUtil::getStringArray, Assert::assertArrayEquals));
    }

    @Test
    public void testString2dArray() {
        var strList = List.of(
                "[[\"a\", \"ba\"],[\"ad\", \"b\"]]",
                // [["a", "ba"],["ad", "b"],["print", "(\"", "hello world!", "\")"]]
                "[[\"a\", \"ba\"],[\"ad\", \"b\"],[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]]"
        );
        var expectedList = List.of(
                new String[][]{{"a", "ba"}, {"ad", "b"}},
                new String[][]{{"a", "ba"}, {"ad", "b"}, {"print", "(\"", "hello world!", "\")"}}
        );
        strList.forEach(testerOfArrayParsing(expectedList, ArgumentParseUtil::getString2dArray, Assert::assertArrayEquals));
    }

    @Test
    public void testIntArray() {
        var str = "[1,17]";
        int[] array = ArgumentParseUtil.getIntArray(str);
        int[] expected = {1, 17};
        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void testInt2dArray() {
        var str = "[[1,1],[1,11]]";
        int[][] list = ArgumentParseUtil.getInt2dArray(str);
        int[][] expected = {
                {1, 1},
                {1, 11}
        };
        Assert.assertArrayEquals(expected, list);
    }

    private static Consumer<String> testerOfInvalidArrayParsing(Consumer<String> arrayLikedParser) {
        return str -> {
            System.out.println(str);
            var exception = Assert.assertThrows(
                    "%s should be invalid!".formatted(str),
                    IllegalArgumentException.class,
                    () -> arrayLikedParser.accept(str)
            );
            String message = exception.getMessage();
            System.err.println(message);
        };
    }

    private static <T> Consumer<String> testerOfArrayParsing(List<T> expectedResults, Function<String, T> arrayLikedParser) {
        return testerOfArrayParsing(expectedResults, arrayLikedParser, Assert::assertEquals);
    }

    private static <T> Consumer<String> testerOfArrayParsing(List<T> expectedResults, Function<String, T> arrayLikedParser, BiConsumer<T, T> doAssert) {
        var it = expectedResults.iterator();
        return str -> {
            T actual = arrayLikedParser.apply(str);
            T expected = it.next();
            doAssert.accept(expected, actual);
        };
    }
}
