package com.vishnu.threading;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author vishnu
 */

public class NetworkCallableFactory {

    private Map<String, String> sharedCache;

    public NetworkCallableFactory(Map<String, String> sharedCache) {
        this.sharedCache = sharedCache;
    }

    public NetworkCallable getNetworkCallable(String url, Pattern searchTerm) {
        return new NetworkCallable(url, sharedCache, searchTerm);
    }
}
