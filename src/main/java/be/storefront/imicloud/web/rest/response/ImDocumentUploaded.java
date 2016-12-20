package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.service.dto.ImDocumentDTO;

/**
 * Created by wouter on 20/12/2016.
 */
public class ImDocumentUploaded {

    private ImDocumentDTO imDocument;

    public ImDocumentUploaded(ImDocumentDTO imDocumentDTO){
        this.imDocument = imDocument;
    }

    public Long getDocumentId(){
        return this.imDocument.getId();
    }

    public String getLoginAndGotoUrl(){
        // https://publishing.informationmapping.com/goto/?document=12345678&access_token=XYZ
        return "https://...";
    }

    public String getPublicShareUrl(){
        // https://publishing.informationmapping.com/document/12345678/
        return "https://...";
    }
}
