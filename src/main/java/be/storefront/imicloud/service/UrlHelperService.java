package be.storefront.imicloud.service;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.repository.OrganizationRepository;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UrlHelperService {

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private OrganizationRepository organizationRepository;
    private Branding branding;

    @Inject
    private UserInfoRepository userInfoRepository;

    @Inject
    private ImDocumentMapper imDocumentMapper;

    @Inject private ImageRepository imageRepository;
    private String allDocumentsUrl;

    public String getDocumentPublicUrl(ImDocument imDocument) {
        return imCloudProperties.getBaseUrl() + "document/" + imDocument.getId() + "/" + imDocument.getSecret() + "/";
    }

    public String getDocumentPublicUrl(ImDocumentDTO imDocumentDto) {
        ImDocument imDocument = imDocumentMapper.imDocumentDTOToImDocument(imDocumentDto);
        return getDocumentPublicUrl(imDocument);
    }

    public Object getDocumentPublicUrl(ImDocument imDocument, String templateCode) {
        return getDocumentPublicUrl(imDocument) + templateCode;
    }

    public String getImageUrl(Image image) {
        if(image == null){
            return "";
        }
        return imCloudProperties.getBaseUrl() + "image/" + image.getId() + "/" + image.getSecret() + "/";
    }


    public String getLoginAndGotoDocumentUrl(ImDocument imDocument) {
        return imCloudProperties.getBaseUrl() + "loginAndRedirect/document/" + imDocument.getId() + "?access_token=";
    }


    public Object getDocumentPasswordSubmitUrl() {
        return imCloudProperties.getBaseUrl() + "document/password/";
    }

    public Object getLogoUrl(User u) {
        UserInfo ui = userInfoRepository.findByUserId(u.getId());
        Organization organization = ui.getOrganization();
        Branding branding = organization.getBranding();

        if(branding.getLogoImage() == null){
            return "";
        }else {
            return getImageUrl(branding.getLogoImage());
        }

    }

    public String getImageUrl(String imageFilename) {
        return getImageUrl(imageRepository.findByFilename(imageFilename));
    }

    public String getAllDocumentsUrl() {
        return imCloudProperties.getBaseUrl() + "#/im-documentroute";
    }
}
