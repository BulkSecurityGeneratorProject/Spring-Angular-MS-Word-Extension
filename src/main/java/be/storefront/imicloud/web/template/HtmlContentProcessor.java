package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import org.joox.Match;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.joox.JOOX.$;

@Service
public class HtmlContentProcessor {

    @Inject
    ImCloudProperties imCloudProperties;

    public String process(String html){

        // Resolve URLs to images
        Match root = $(html);

        for(Match img : root.find("img").each()){
            String imgSrc = imCloudProperties.getBaseUrl()+
            img.attr("src", imgSrc);
        }



        return root.toString();
    }

}
