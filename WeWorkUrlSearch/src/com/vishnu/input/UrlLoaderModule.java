package com.vishnu.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Module to load the S3 link, parse the text and load all the
 * urls from the link into the memory
 *
 * @author vishnu
 */

public class UrlLoaderModule {

    private static final String S3_LINK = "https://s3.amazonaws.com/fieldlens-public/urls.txt";
    private final HttpsURLConnection httpsURLConnection;
    private static UrlLoaderModule urlLoaderModule;
    private final List<String> urls;
    private UrlLoaderModule() throws IOException {
        URL url = new URL(S3_LINK);
        this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setRequestMethod("GET");
        this.urls = new ArrayList<>();
    }

    public static UrlLoaderModule getInstance() throws IOException {
        if (urlLoaderModule == null) {
            urlLoaderModule = new UrlLoaderModule();
        }
        return urlLoaderModule;
    }

    public void loadUrls() {
        try (BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(httpsURLConnection.getInputStream()))) {
            String inputLine;
            /*
            "Rank","URL","Linking Root Domains","External Links","mozRank","mozTrust"
             1,"facebook.com/",9616487,1688316928,9.54,9.34

             2nd entry in each line is a url
             */
            while ((inputLine = bufferedReader.readLine()) != null) {
                String word[] = inputLine.split(",");
                String url = word[1];
                url = url.substring(1, url.length() - 1); // start and end are ' " ' (quotes)
                urls.add(String.format("https://%s",url));
            }
        } catch (IOException e) {
            System.out.println("Unable to load url data: " + e);
        }
    }

    public List<String> getUrls() {
        List<String> filteredUrls = new ArrayList<>(urls);
        filteredUrls.remove(0);
        return filteredUrls;
    }
}
