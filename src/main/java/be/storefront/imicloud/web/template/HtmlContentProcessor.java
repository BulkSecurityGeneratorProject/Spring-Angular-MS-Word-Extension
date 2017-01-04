package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.dto.ImageDTO;
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

    public String process(String html){

        // Resolve URLs to images
        Match root = $("<root>"+html+"</root>");

        for(Match img : root.find("img").each()){
            String source = img.attr("data-source");
            Image image = imageRepository.findByFilename(source);
            String imgSrc = imCloudProperties.getBaseUrl()+"image/"+image.getId();
            img.attr("src", imgSrc);
        }



        html =  root.find("root").toString();

        return html;
    }

}
