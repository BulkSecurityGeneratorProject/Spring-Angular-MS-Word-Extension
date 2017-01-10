package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.service.UrlHelperService;

/**
 * Created by wouter on 06/01/2017.
 */
public class ImageCreated {

    private Image image;
    private UrlHelperService urlHelperService;

    public ImageCreated(Image image) {
        this.image = image;
    }

    public ImageCreated(Image image, UrlHelperService urlHelperService) {
        this.image = image;
        this.urlHelperService = urlHelperService;
    }

    public Long getImageId() {
        return image.getId();
    }

    public String getFilename() {
        return image.getFilename();
    }

    public String getUrl() {
        if (urlHelperService != null) {
            return urlHelperService.getImageUrl(image);
        } else {
            return null;
        }
    }

}
