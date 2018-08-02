package com.vishnu.threading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.vishnu.utils.SearchRunnerUtils;

/**
 * Custom thread that will make the http request to extract the
 * text from url and store in a shared cache
 *
 *
 * @author vishnu
 */

public final class NetworkCallable implements Callable<String> {
    private static final int CONNECTION_TIMEOUT_MILLIS = 500;
    private static final int READ_TIMEOUT_MILLIS = 1000;
    private String url;
    private Map<String, String> cache;
    private Pattern pattern;

    public NetworkCallable(String url, Map<String, String> cache, Pattern pattern) {
        this.url = url;
        this.cache = cache;
        this.pattern = pattern;
    }

    @Override
    public String call() {
        // If cache has the url, get the text and run the regex
        if (cache.containsKey(url)) {
            if (SearchRunnerUtils.matchSafely(pattern, cache.get(url))) {
                // System.out.println("Matched(cache):" + url);
                return url;
            }
        }
        try {
            URL connectionUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) connectionUrl.openConnection();
            connection.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
            connection.setReadTimeout(READ_TIMEOUT_MILLIS);
            try (
                BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()))
            ) {
                StringBuilder completeString = new StringBuilder();
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    completeString.append(inputLine);
                }
                cache.putIfAbsent(url, completeString.toString().toLowerCase());
                if (SearchRunnerUtils.matchSafely(pattern, completeString.toString())) {
                    // System.out.println("Matched:" + url);
                    return url;
                }
            } catch (IOException e) {
                //System.out.println("Unable to load url data: " + e);
            }
        } catch (Exception e) {
            System.out.println("Unable to setup connection with url: " + url + e);
        }
        return null;
    }
}