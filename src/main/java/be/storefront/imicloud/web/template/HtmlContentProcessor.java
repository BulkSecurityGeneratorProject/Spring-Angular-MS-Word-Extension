package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.service.UrlHelperService;
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

    @Inject
    private UrlHelperService urlHelperService;

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
                    String imgSrc = urlHelperService.getImageUrl(image);
                    img.attr("src", imgSrc);
                }

            } catch (NumberFormatException ex) {
            }


        }

        // Make links open in new tab
        for (Match a : root.find("a").each()) {
            a.attr("target", "_blank");
        }

        String r = DomHelper.domToString(root);


        return r;
    }

}
