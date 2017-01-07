package be.storefront.imicloud.web.session;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by wouter on 07/01/2017.
 */

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DocumentAccessImpl implements DocumentAccess {
    HashMap<Long, String> enteredDocumentPasswords;

    public void rememberDocumentPassword(Long documentId, String password) {
        if (enteredDocumentPasswords == null) {
            enteredDocumentPasswords = new HashMap<>();
        }
        enteredDocumentPasswords.put(documentId, password);
    }

    public String getRememberedDocumentPassword(Long documentId) {
        if (enteredDocumentPasswords == null) {
            enteredDocumentPasswords = new HashMap<>();
        }
        return enteredDocumentPasswords.get(documentId);
    }

}

