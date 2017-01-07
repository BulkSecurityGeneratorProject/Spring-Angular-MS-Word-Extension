package be.storefront.imicloud.service;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UrlHelperService {

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private OrganizationRepository organizationRepository;
    private Branding branding;

    public String getDocumentPublicUrl(ImDocument imDocument) {
        return imCloudProperties.getBaseUrl() + "document/" + imDocument.getId() + "/" + imDocument.getSecret() + "/";
    }

    public Object getDocumentPublicUrl(ImDocument imDocument, String templateCode) {
        return getDocumentPublicUrl(imDocument) + templateCode;
    }

    public String getImagePublicUrl(Image image) {
        return imCloudProperties.getBaseUrl() + "image/" + image.getId() + "/" + image.getSecret() + "/";
    }


    public String getLoginAndGotoDocumentUrl(ImDocument imDocument) {
        return imCloudProperties.getBaseUrl() + "loginAndRedirect/document/" + imDocument.getId() + "?access_token=";
    }


    public Object getDocumentPasswordSubmitUrl() {
        return imCloudProperties.getBaseUrl() + "document/password/";
    }

    public Object getLogoUrl(User user) {

        Organization organization = organizationRepository.findByUserId(user.getId());
        Branding branding = organization.getBranding();


    }
}
