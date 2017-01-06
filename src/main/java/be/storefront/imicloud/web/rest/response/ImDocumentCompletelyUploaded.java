package be.storefront.imicloud.web.rest.response;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.service.UrlHelperService;

/**
 * Created by wouter on 06/01/2017.
 */
public class ImDocumentCompletelyUploaded {

    private ImDocument imDocument;
    private UrlHelperService urlHelperService;

    public ImDocumentCompletelyUploaded(ImDocument imDocument, UrlHelperService urlHelperService){
        this.imDocument = imDocument;
        this.urlHelperService = urlHelperService;
    }

    public Long getDocumentId(){
        return this.imDocument.getId();
    }

    public String getLoginAndGotoUrl(){
        // https://publishing.informationmapping.com/loginAndGoto/document/12345678/?access_token=
        return urlHelperService.getLoginAndGotoDocumentUrl(imDocument);
    }

    public String getPublicShareUrl(){
        return urlHelperService.getDocumentPublicUrl(imDocument);
    }
}
