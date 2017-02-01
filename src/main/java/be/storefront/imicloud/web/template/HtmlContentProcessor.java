package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.domain.document.ImDocumentStructure;
import be.storefront.imicloud.domain.document.structure.StructureMap;
import be.storefront.imicloud.domain.document.structure.TreeNode;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.service.UrlHelperService;
import be.storefront.imicloud.web.dom.DomHelper;
import org.joox.Match;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.joox.JOOX.$;

@Service
public class HtmlContentProcessor {

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private UrlHelperService urlHelperService;

    public String process(String html, String templateCode, ImDocumentStructure imDocumentStructure) {

        // Resolve URLs to images
        Match root = DomHelper.getDomRoot(html);

        for (Match img : root.find("img").each()) {
            String imageIdString = img.attr("data-id");
            Long imageId = null;
            try {
                imageId = Long.parseLong(imageIdString);
                Image image = imageRepository.findOne(imageId);
                if (image != null) {
                    String imgSrc = urlHelperService.getImageUrl(image);
                    img.attr("src", imgSrc);
                }

            } catch (NumberFormatException ex) {
            }
        }


        // Embed youtube instead of links - but only for websites...
        if (templateCode.startsWith("web")) {
            for (Match aNode : root.find("a[href]").each()) {
                String href = aNode.attr("href");

                if (href.startsWith("https://www.youtube.com") || href.startsWith("http://www.youtube.com")) {
                    try {
                        String youtubeUrl = href;
                        Map<String, String> youtubeParams = splitUrlQuery(youtubeUrl);

                        if(youtubeParams.containsKey("v")){
                            String videoId = youtubeParams.get("v");

                            long id = Math.round(Math.random() * 1000000);
                            aNode.attr("id", ""+id);

                            // We add the <span> to prevent the iframe being compacted to <iframe /> which we don't want. We need <iframe></iframe> exactly like this!
                            String youtubeEmbed = "<iframe width=\"640\" height=\"360\" src=\"https://www.youtube.com/embed/"+videoId+"\" frameborder=\"0\" allowfullscreen=\"true\"><span /></iframe>";

                            aNode.before(youtubeEmbed);

                            // Remove the original "a" node
                            aNode = root.find("#"+id);
                            aNode.remove();
                        }

                    }catch(Exception ex){
                        // Continue like nothing happened
                    }
                }
            }
        }


        // Make links to external sites open in new tab
        for (Match a : root.find("a").each()) {
            a.attr("target", "_blank");
        }

        // Clean up table of contents
        for (Match tocTable : root.find("table.topic-seepage").each()) {
            tocTable.find("tr > th:last-child").remove();
            tocTable.find("tr > td:last-child").remove();



            for (Match reference : tocTable.find("reference").each()) {
                Match td = reference.parentsUntil("td");
                String guidToLinkTo = reference.attr("address");

                // This guid can be a map, or not
                TreeNode treeNode = imDocumentStructure.getByGuid(guidToLinkTo);

                if(treeNode == null) {
                    // We cannot link to non-existant GUID
                    guidToLinkTo = null;

                }else{
                    guidToLinkTo = treeNode.getContentGuid();
//                    if (treeNode instanceof StructureMap) {
//                        // We can link to this
//                    } else {
//                        // We can not link to this GUID, find the nearest map inside it
//                        StructureMap firstMap = treeNode.findFirstMap();
//                        if (firstMap != null) {
//                            guidToLinkTo = firstMap.getGuid();
//                        }
//                    }
                }

                if(guidToLinkTo == null){
                    // Cannot link
                    reference.rename("span");
                }else{
                    // Can link
                    reference.rename("a").attr("data-viewid", guidToLinkTo).attr("href", "#");
                }

            }

            tocTable.find("tr > th p").rename("span");
            tocTable.find("tr > td p").rename("span");

        }

        String r = DomHelper.domToString(root);


        return r;
    }

    public static Map<String, String> splitUrlQuery(String urlString) throws UnsupportedEncodingException, MalformedURLException {
        URL url = new URL(urlString);
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

}
