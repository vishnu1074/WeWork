package com.vishnu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.vishnu.threading.NetworkCallableFactory;
import com.vishnu.threading.NetworkThreadPoolExecutor;
import com.vishnu.utils.FileUtils;
import com.vishnu.utils.SearchRunnerUtils;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * Activity to perform the search in multiple urls.
 * We randomly generate words (currently the words is fixed to
 * "WeWork transforms buildings beautiful collaborative workspaces"
 *
 * After every word, text is cached for faster performance and also not DDOS
 * the other websites.
 *
 * @author vishnu
 */

public class SearchRunner {

    private List<String> urls;
    private NetworkThreadPoolExecutor threadPoolExecutor;
    private NetworkCallableFactory factory;

    public SearchRunner(
        List<String> urls, NetworkThreadPoolExecutor threadPoolExecutor, NetworkCallableFactory factory
    ) {
        this.urls = urls;
        this.threadPoolExecutor = threadPoolExecutor;
        this.factory = factory;
        FileUtils.createFile();
    }

    public void performSearch() {
        for (String word : SearchRunnerUtils.getSearchTerms()) {
            long start = System.currentTimeMillis();

            StringBuilder result  = new StringBuilder();

            result.append("search term = " + word);
            result.append("\n");
            result.append(String.format("Searching %s, Results are %s", word,  search(word)));
            result.append("\n");
            result.append("\n");
            result.append("Completed in :" + (System.currentTimeMillis() - start) / 1000 +" secs");
            result.append("\n ---------------------------------- \n");
            FileUtils.appendTextToFile(result.toString());
        }
        threadPoolExecutor.stop();
    }

    /**
     * Search the search term and return list of urls that has the search term
     *
     * @return List of matched urls that contain the search term
     */
    private List<String> search(String searchTerm) {
        List<String> result = new ArrayList<>();
        List<Future<String>> allResult = new ArrayList<>();
        // super basic pattern matching.
        String regexPattern = String.format(".*%s.*", searchTerm);
        Pattern pattern = Pattern.compile(regexPattern, CASE_INSENSITIVE);
        for (String url : urls) {
            Future<String> data = threadPoolExecutor.submit(factory.getNetworkCallable(url, pattern));
            allResult.add(data);
        }
        for (Future<String> eachResult : allResult) {
            try {
                String value = eachResult.get();
                if (value == null) {
                    continue;
                }
                result.add(value);
            } catch (Exception e) {
                System.out.println("Unable to find in url");
            }
        }
        return result;
    }
}
