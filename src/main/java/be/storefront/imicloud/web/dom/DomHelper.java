package be.storefront.imicloud.web.dom;

import org.joox.Match;

import static org.joox.JOOX.$;

/**
 * Created by wouter on 06/01/2017.
 */
public class DomHelper {

    private static final String rootNode = "TEMP_ROOT_NODE";

    public static Match getDomRoot(String html){
        Match root = $("<"+rootNode+">"+html+"</"+rootNode+">");
        return root;
    }

    public static String domToString(Match root) {
        String r = root.toString();
        r = r.replaceAll("<"+rootNode+">", "");
        r = r.replaceAll("</"+rootNode+">", "");
        return r;
    }
}
