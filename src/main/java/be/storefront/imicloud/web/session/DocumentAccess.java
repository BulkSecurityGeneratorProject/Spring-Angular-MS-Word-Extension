package be.storefront.imicloud.web.session;


public interface DocumentAccess {

    void rememberDocumentPassword(Long documentId, String password);

    String getRememberedDocumentPassword(Long documentId);
}

