package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.domain.ImDocument;

/**
 * Created by wouter on 06/01/2017.
 */
public class ImDocumentCompletelyUploaded {

    private ImDocument imDocument;
    private String baseUrl;

    public ImDocumentCompletelyUploaded(ImDocument imDocument, String baseUrl){
        this.imDocument = imDocument;
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
