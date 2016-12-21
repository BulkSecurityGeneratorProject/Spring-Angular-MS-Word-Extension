package be.storefront.imicloud.web.rest.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wouter on 20/12/2016.
 */
public class BaseUrlUtil {

    public static String getBaseUrl(HttpServletRequest request){
        String r = "";
        if(request.isSecure()){
            r += "https://";
        }else{
            r += "http://";
        }

        r += request.getLocalName()+"/";

        return r;
    }
}
