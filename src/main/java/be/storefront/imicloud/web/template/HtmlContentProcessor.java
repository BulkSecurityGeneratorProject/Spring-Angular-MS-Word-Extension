package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.web.dom.DomHelper;
import org.joox.Match;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static org.joox.JOOX.$;

@Service
public class HtmlContentProcessor {

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private ImageRepository imageRepository;

    public String process(String html) {

        // Resolve URLs to images
        Match root = DomHelper.getDomRoot(html);

        for (Match img : root.find("img").each()) {
            String imageIdString = img.attr("data-id");
            Long imageId = null;
            try {
                imageId = Long.parseLong(imageIdString);
                Image image = imageRepository.findOne(imageId);
                if (image != null) {
                    String imgSrc = imCloudProperties.getBaseUrl() + "image/" + image.getId()+"/"+image.getSecret();
                    img.attr("src", imgSrc);
                }

            } catch (NumberFormatException ex) {
            }


        }

        String r = DomHelper.domToString(root);


        return r;
    }

}
