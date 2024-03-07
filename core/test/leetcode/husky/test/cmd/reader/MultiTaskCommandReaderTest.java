package leetcode.husky.test.cmd.reader;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandSet;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

public class MultiTaskCommandReaderTest {

    @Test
    public void readCommandSet() {
        String inputText = """
                ["Solution","hello","world"]
                [[],["java"],["go"]]
                """;
        CommandSet expected = new CommandSet(
                List.of(new Command("Solution", List.of()),
                        new Command("hello", List.of("\"java\"")),
                        new Command("world", List.of("\"go\""))
                )
        );
        LineReader lineReader = new SimpleLineReaderImpl(new StringReader(inputText));
        CommandSet actual = new MultiTaskCommandReader().readCommandSet(lineReader);
        Assert.assertEquals(expected, actual);
    }
}