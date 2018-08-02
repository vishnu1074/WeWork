package com.vishnu;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vishnu.input.UrlLoaderModule;
import com.vishnu.threading.NetworkCallableFactory;
import com.vishnu.threading.NetworkThreadPoolExecutor;

/**
 * Main module that launches the program, run the search and
 * exits safely.
 */
public class MainModule {

    public static void main(String[] args) throws IOException {

        /*
            Not sure if I can use Guice. So I did not use that library for
            dependency injection
         */

        // read all the urls into the memory, This is singleton
        UrlLoaderModule urlLoaderModule = UrlLoaderModule.getInstance();
        urlLoaderModule.loadUrls();

        // create the thread pool executor
        NetworkThreadPoolExecutor poolExecutor = new NetworkThreadPoolExecutor();

        // Setup shared cache. Guava has LoadingCache. Probably would have used it

        Map<String, String> sharedCache = new ConcurrentHashMap<>();

        // create NetworkCallable Factory
        NetworkCallableFactory factory = new NetworkCallableFactory(sharedCache);


        // Initialize search runner module
        SearchRunner runner = new SearchRunner(urlLoaderModule.getUrls(), poolExecutor, factory);
        runner.performSearch();

        System.exit(0);
    }
}
