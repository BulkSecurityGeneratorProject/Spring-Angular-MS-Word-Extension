package be.storefront.imicloud.service;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.Image;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UrlHelperService {

    @Inject
    private ImCloudProperties imCloudProperties;

    public String getDocumentPublicUrl(ImDocument imDocument) {
        return imCloudProperties.getBaseUrl() + "document/" + imDocument.getId() + "/" + imDocument.getSecret()+"/";
    }

    public String getImagePublicUrl(Image image) {
        return imCloudProperties.getBaseUrl() + "image/" + image.getId() + "/" + image.getSecret()+"/";
    }


    public String getLoginAndGotoDocumentUrl(ImDocument imDocument) {
        return imCloudProperties.getBaseUrl()+"loginAndRedirect/document/"+imDocument.getId()+"?access_token=";
    }
}
