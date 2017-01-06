package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.domain.Image;

/**
 * Created by wouter on 06/01/2017.
 */
public class ImageCreated {

    private Image image;

    public ImageCreated(Image image) {
        this.image = image;
    }

    public Long getImageId() {
        return image.getId();
    }

}
