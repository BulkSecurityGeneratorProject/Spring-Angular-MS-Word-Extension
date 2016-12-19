package be.storefront.imicloud.domain.util;

/**
 * Created by wouter on 19/12/2016.
 */
public class UrlFilter {

    public static String forceUrlToEndWithSlash(String url){
        // Force ending with "/"
        if (!"/".equals(url.substring(url.length() - 1, url.length()))) {
            url += "/";
        }
        return url;
    }
}
