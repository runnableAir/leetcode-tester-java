package leetcode.husky.test.driver.interpreter.param;

import leetcode.husky.test.driver.interpreter.param.resolver.SimpleArgumentResolver;
import leetcode.husky.test.util.ArgumentParseUtil;

import java.util.List;

public interface ParamType<T> extends SimpleArgumentResolver<T> {
    // string
    ParamType<String> STRING = ArgumentParseUtil::getString;
    ParamType<String[]> STRING_ARRAY = ArgumentParseUtil::getStringArray;
    ParamType<List<String>> STRING_LIST = ArgumentParseUtil::getStringList;

    // int
    ParamType<Integer> INT = Integer::valueOf;
    ParamType<List<Integer>> INT_LIST = ArgumentParseUtil::getIntList;
    ParamType<int[]> INT_ARRAY = ArgumentParseUtil::getIntArray;
    ParamType<int[][]> INT_2D_ARRAY = ArgumentParseUtil::getInt2dArray;
}
