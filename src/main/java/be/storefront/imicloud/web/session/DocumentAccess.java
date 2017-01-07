package be.storefront.imicloud.web.session;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;

@Component
@Scope(value = "session")
public class DocumentAccess  implements Serializable{

    HashMap<Long, String> enteredDocumentPasswords = new HashMap<>();

    public void rememberDocumentPassword(Long documentId, String password) {
        enteredDocumentPasswords.put(documentId, password);
    }

    public String getRememberedDocumentPassword(Long documentId) {
        return enteredDocumentPasswords.get(documentId);
    }

}
