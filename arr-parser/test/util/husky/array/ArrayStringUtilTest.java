package util.husky.array;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static util.husky.array.ArrayStringUtil.*;

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

    @Test
    public void arrayNodeBuilding() {
        var intArrayStr = """
                [1,2,3]""";
        var intArrayNode = ArrayStringUtil.buildArrayNode(intArrayStr, 1, INT_TYPE);
        var intArrayNodeAsStr = intArrayNode.asString();
        System.out.println("=> " + intArrayStr);
        System.out.println("<= " + intArrayNode);
        System.out.println("(asString) <= " + intArrayNodeAsStr);
        assertEquals(intArrayStr, intArrayNodeAsStr);

        var strArrayStr = """
                ["aaa","bbb","ccc","what the \\"fuck\\""]""";
        var strArrayNode = ArrayStringUtil.buildArrayNode(strArrayStr, 1, STRING_TYPE);
        String strArrayNodeAsStr = strArrayNode.asString();
        System.out.println("=> " + strArrayStr);
        System.out.println("<= " + strArrayNode);
        System.out.println("(asString) <= " + strArrayNodeAsStr);
        assertEquals(strArrayStr, strArrayNodeAsStr);

        var int2dArrayStr = """
                [[1],[2,3],[3]]""";
        var int2dArrayNode = ArrayStringUtil.buildArrayNode(int2dArrayStr, 2, INT_TYPE);
        String int2dArrayNodeAsStr = int2dArrayNode.asString();
        System.out.println("=> " + int2dArrayStr);
        System.out.println("<= " + int2dArrayNode);
        System.out.println("(asString) <= " + int2dArrayNodeAsStr);
        assertEquals(int2dArrayStr, int2dArrayNodeAsStr);

        // [["a","b","c"],["def","G","[1,2,3,[4,5],[],[\"HI\"]]"]]
        var str2dArrayStr = """
                [["a","b","c"],["def","G","[1,2,3,[4,5],[],[\\"HI\\"]]"]]""";
        var str2dArrayNode = ArrayStringUtil.buildArrayNode(str2dArrayStr, 2, STRING_TYPE);
        String str2dArrayNodeAsStr = str2dArrayNode.asString();
        System.out.println("=> " + str2dArrayStr);
        System.out.println("<= " + str2dArrayNode);
        System.out.println("(asString) <= " + str2dArrayNodeAsStr);
        assertEquals(str2dArrayStr, str2dArrayNodeAsStr);
    }

    @Test
    public void anyArrayBuilding() {
        var sourceText = """
                []
                [1,2]
                [[1,2]]
                [[1,2],[3,4]]
                ["a1","b2"]
                [["a1","b2"]]
                [["a1","b2"],["c3","d4"]]
                ["1 + 2","System.out.println(\\"Hello world!\\")"]
                ["toArray(\\"[1,2,\\\\"java\\\\"]\\")"]
                """;
        var expectedOutputText = """
                []
                [1(Number), 2(Number)]
                [[1(Number), 2(Number)]]
                [[1(Number), 2(Number)], [3(Number), 4(Number)]]
                [a1(String), b2(String)]
                [[a1(String), b2(String)]]
                [[a1(String), b2(String)], [c3(String), d4(String)]]
                [1 + 2(String), System.out.println("Hello world!")(String)]
                [toArray("[1,2,\\"java\\"]")(String)]
                """;
        StringBuilder output = new StringBuilder();
        sourceText.lines()
                .forEach(s -> {
                    System.out.println("=> " + s);
                    ArrayNode arrayNode = buildAnyArrayNode(s);
                    System.out.println("<= " + arrayNode);
                    output.append(arrayNode).append("\n");
                });
        assertEquals(expectedOutputText, output.toString());
    }

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
                "[[\"a\", \"ba\"],[\"ad\", \"b\"],[\"print\", \"(\\\"\", \"hello world!\", \"\\\")\"]]",
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
        var arrayStrings = generateArrayStrings(10, 100,
                randomIntGenerator(-98765, 12458));
        assertDeserialization(arrayStrings, ArrayStringUtil::getIntList, this::listToString);
        assertDeserialization(arrayStrings, ArrayStringUtil::getIntArray, this::arrayToString);
    }

    @Test
    public void int2dArrayString() {
        var arrayStrings = generate2dArrayStrings(3, 20,
                randomIntGenerator(-10000, 50000));
        assertDeserialization(arrayStrings, ArrayStringUtil::getInt2dList, this::listListToString);
        assertDeserialization(arrayStrings, ArrayStringUtil::getInt2dArray, this::arrayToString);
    }

    @Test
    public void longArrayString() {
        var arrayStrings = generateArrayStrings(100, 200,
                randomLongGenerator(-100000000000L, 10000000000000L));
        assertDeserialization(arrayStrings, ArrayStringUtil::getLongList, this::listToString);
        assertDeserialization(arrayStrings, ArrayStringUtil::getLongArray, this::arrayToString);
    }

    @Test
    public void long2dArrayString() {
        var arrayStrings = generate2dArrayStrings(5, 10,
                randomLongGenerator(-(1 << 30), 1 << 30));
        assertDeserialization(arrayStrings, ArrayStringUtil::getLong2dList, this::listListToString);
        assertDeserialization(arrayStrings, ArrayStringUtil::getLong2dArray, this::arrayToString);
    }

    @Test
    public void stringArrayString() {
        var arrayStrings = generateArrayStrings(10, 10, randomStringGenerator(5));
        assertDeserialization(arrayStrings, ArrayStringUtil::getStringList, this::listToString);
        assertDeserialization(arrayStrings, ArrayStringUtil::getStringArray, this::arrayToString);
    }

    @Test
    public void string2dArrayString() {
        var arrayStrings = generate2dArrayStrings(10, 10, randomStringGenerator(5));
        assertDeserialization(arrayStrings, ArrayStringUtil::getString2dList, this::listListToString);
        assertDeserialization(arrayStrings, ArrayStringUtil::getString2dArray, this::arrayToString);
    }

    public String wrapStringWithQuotes(String s) {
        return "\"" + s + "\"";
    }


    long randomSeed = 54687491234L;


    public Supplier<String> randomStringGenerator(int maxLength) {
        Random random = new Random(randomSeed);
        return () -> {
            int n = random.nextInt(maxLength);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                char c = (char) ('a' + random.nextInt(26));
                sb.append(c);
            }
            return sb.toString();
        };
    }

    private Supplier<Long> randomLongGenerator(long origin, long bound) {
        Random random = new Random(randomSeed);
        return () -> random.nextLong(origin, bound);
    }

    private Supplier<Integer> randomIntGenerator(int origin, int bound) {
        Random random = new Random(randomSeed);
        return () -> random.nextInt(origin, bound);
    }


    private <T> void assertDeserialization(List<String> arrayStrings,
                                           Function<String, T> deserializer,
                                           Function<T, String> serializer) {
        for (String arrayString : arrayStrings) {
            assertDeserialization(arrayString, deserializer, serializer);
        }
    }

    private <T> void assertDeserialization(String input,
                                           Function<String, T> deserialize,
                                           Function<T, String> serialize) {
        assertEquals(input, serialize.apply(deserialize.apply(input)));
    }

    private <T> List<String> generate2dArrayStrings(int caseNum, int arrayMaxSize, Supplier<T> valueGenerator) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < caseNum; i++) {
            List<T> list = generateList(arrayMaxSize, valueGenerator);
            StringJoiner input = new StringJoiner(",", "[", "]");
            int toIndex = list.size();
            while (toIndex > 0) {
                int fromIndex = (int) (Math.random() * toIndex);
                List<T> sublist = list.subList(fromIndex, toIndex);
                input.add(listToString(sublist));
                toIndex = fromIndex;
            }
            result.add(input.toString());
        }
        return result;
    }

    private <T> List<String> generateArrayStrings(int caseNum, int arrayMaxSize, Supplier<T> valueGenerator) {
        List<String> result = new ArrayList<>(caseNum);
        for (int i = 0; i < caseNum; i++) {
            List<T> list = generateList(arrayMaxSize, valueGenerator);
            String input = listToString(list);
            result.add(input);
        }
        return result;
    }


    private <T> List<T> generateList(int limitSize, Supplier<T> elementValueGenerator) {
        Random random = new Random(randomSeed);
        int size = random.nextInt(limitSize);
        List<T> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T e = elementValueGenerator.get();
            if (e == null) {
                throw new IllegalStateException("generated element can not be null");
            }
            list.add(e);
        }
        return list;
    }

    private String arrayToString(Object arrObject) {
        return switch (arrObject) {
            case int[] intArray -> intArrayToString(intArray);
            case long[] longArray -> longArrayToString(longArray);
            case Object[] objectArray -> objectArrayToString(objectArray);
            case null, default -> throw new IllegalStateException("Unexpected value: " + arrObject);
        };
    }

    private String objectArrayToString(Object[] arr) {
        return Arrays.stream(arr)
                .map(this::objectArrayElementToString)
                .collect(joinStringAsArray());
    }

    private String objectArrayElementToString(Object obj) {
        if (obj.getClass().isArray()) {
            return arrayToString(obj);
        }
        if (obj instanceof String str) {
            return wrapStringWithQuotes(str);
        }
        return Objects.toString(obj);
    }


    private String longArrayToString(long[] arr) {
        return Arrays.stream(arr)
                .mapToObj(String::valueOf)
                .collect(joinStringAsArray());
    }

    private String intArrayToString(int[] arr) {
        return Arrays.stream(arr)
                .mapToObj(String::valueOf)
                .collect(joinStringAsArray());
    }

    private static Collector<CharSequence, ?, String> joinStringAsArray() {
        return Collectors.joining(",", "[", "]");
    }

    private <T> String listListToString(List<List<T>> list) {
        return listToString(list, this::listToString);
    }

    private String listToString(List<?> list) {
        return listToString(list, this::objectListElementToString);
    }

    private String objectListElementToString(Object obj) {
        return objectArrayElementToString(obj);
    }

    private <T> String listToString(List<T> list, Function<T, String> elementToStringFunc) {
        return list.stream()
                .map(elementToStringFunc)
                .collect(joinStringAsArray());
    }
}