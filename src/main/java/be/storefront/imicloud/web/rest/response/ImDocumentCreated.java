package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.service.dto.ImDocumentDTO;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wouter on 20/12/2016.
 */
public class ImDocumentCreated extends ImDocumentResponse{

    private ImDocumentDTO imDocument;

    public ImDocumentCreated(ImDocumentDTO imDocumentDTO){
        this.imDocument = imDocumentDTO;
    }

    public Long getDocumentId(){
        return this.imDocument.getId();
    }

}
