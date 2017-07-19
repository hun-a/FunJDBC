package com.cubrid.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hun on 19/07/2017.
 */
public class StringUtil {

    public static int countBindingParams(String sql) {
        final String regex = "[^\"']\\?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(sql);

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }
}
