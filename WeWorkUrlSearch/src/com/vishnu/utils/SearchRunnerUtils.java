package com.vishnu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vishnu
 */

public class SearchRunnerUtils {


    public static final boolean matchSafely(Pattern pattern, String totalText) {
        try {
            Matcher matcher = pattern.matcher(totalText);
            return matcher.matches();
        } catch (Throwable t) {
            System.out.println("Error while matching:" + pattern.pattern() + t);
            return false;
        }
    }

    public static List<String> getSearchTerms() {
        List<String> searchTerms = new ArrayList<>();
        final String test =
            "WeWork transforms buildings beautiful collaborative workspaces";
        String words[] = test.split("\\s+");
        for (String word : words) {
            searchTerms.add(word);
        }
        return searchTerms;
    }
}
