package leetcode.husky.test.cmd.reader;

import leetcode.husky.test.cmd.Command;
import leetcode.husky.test.cmd.CommandSet;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

public class SingleTaskCommandReaderTest {

    @Test
    public void readCommandSet() {
        String inputText = """
                [1,1,2]
                3
                [1,4,56,63]
                3""";
        String targetMethod = "method";
        var expectedCommandSets = List.of(
                new CommandSet(List.of(new Command(targetMethod, List.of("[1,1,2]", "3")))),
                new CommandSet(List.of(new Command(targetMethod, List.of("[1,4,56,63]", "3"))))
        );
        LineReader lineReader = new SimpleLineReaderImpl(new StringReader(inputText));
        for (CommandSet expected : expectedCommandSets) {
            CommandSet actual = new SingleTaskCommandReader(2, targetMethod)
                    .readCommandSet(lineReader);
            Assert.assertEquals(expected, actual);
        }
    }
}