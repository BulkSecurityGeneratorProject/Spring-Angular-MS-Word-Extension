package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.service.dto.ImDocumentDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wouter on 20/12/2016.
 */
public class ImDocumentUploaded {

    private ImDocumentDTO imDocument;
    private String baseUrl;

    public ImDocumentUploaded(ImDocumentDTO imDocumentDTO, String baseUrl){
        this.imDocument = imDocumentDTO;
        this.baseUrl = baseUrl;
    }

    public Long getDocumentId(){
        return this.imDocument.getId();
    }

    public String getLoginAndGotoUrl(){
        // https://publishing.informationmapping.com/loginAndGoto/document/12345678/?access_token=
        return baseUrl+"loginAndRedirect/document/"+imDocument.getId()+"?access_token=";
    }

    public String getPublicShareUrl(){
        // https://publishing.informationmapping.com/document/12345678/
        return baseUrl+"document/"+imDocument.getId()+"/"+imDocument.getSecret()+"/";
    }
}
