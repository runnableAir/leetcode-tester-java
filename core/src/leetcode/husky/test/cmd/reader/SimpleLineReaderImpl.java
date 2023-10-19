package leetcode.husky.test.cmd.reader;

import java.io.Reader;
import java.util.Scanner;

public class SimpleLineReaderImpl implements LineReader {
    private final Scanner scanner;

    public SimpleLineReaderImpl(Reader reader) {
        scanner = new Scanner(reader);
    }

    @Override
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }
}
