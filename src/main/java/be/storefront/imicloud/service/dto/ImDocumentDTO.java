package be.storefront.imicloud.service.dto;

import be.storefront.imicloud.service.UrlHelperService;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;


/**
 * A DTO for the ImDocument entity.
 */
public class ImDocumentDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    private String language;

    private String password;

    private String tempPassword;

    @NotNull
    private String documentName;

    @Lob
    private String originalXml;

    @Lob
    private String tempXml;

    @NotNull
    private String secret;

    private String defaultTemplate;

    private String tempTemplate;

    private Boolean uploadComplete;


    private Long folderId;

    private Long userId;


    private String userEmail;

    private Long brandingId;
    private String brandingName;



    private UrlHelperService urlHelperService;

    public void setUrlHelperService(UrlHelperService urlHelperService){
        this.urlHelperService = urlHelperService;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getTempPassword() {
        return tempPassword;
    }

    public void setTempPassword(String tempPassword) {
        this.tempPassword = tempPassword;
    }
    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
    public String getOriginalXml() {
        return originalXml;
    }

    public void setOriginalXml(String originalXml) {
        this.originalXml = originalXml;
    }
    public String getTempXml() {
        return tempXml;
    }

    public void setTempXml(String tempXml) {
        this.tempXml = tempXml;
    }
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
    public String getTempTemplate() {
        return tempTemplate;
    }

    public void setTempTemplate(String tempTemplate) {
        this.tempTemplate = tempTemplate;
    }
    public Boolean getUploadComplete() {
        return uploadComplete;
    }

    public void setUploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPublicViewUrl(){
        if(urlHelperService == null){
            return null;
        }else {
            return urlHelperService.getDocumentPublicUrl(this);
        }
    }

    public Long getBrandingId() {
        return brandingId;
    }

    public void setBrandingId(Long brandingId) {
        this.brandingId = brandingId;
    }

    public String getBrandingName() {
        return brandingName;
    }

    public void setBrandingName(String brandingName) {
        this.brandingName = brandingName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImDocumentDTO imDocumentDTO = (ImDocumentDTO) o;

        if ( ! Objects.equals(id, imDocumentDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImDocumentDTO{" +
            "id=" + id +
            ", language='" + language + "'" +
            ", password='" + password + "'" +
            ", tempPassword='" + tempPassword + "'" +
            ", documentName='" + documentName + "'" +
            ", originalXml='" + originalXml + "'" +
            ", tempXml='" + tempXml + "'" +
            ", secret='" + secret + "'" +
            ", defaultTemplate='" + defaultTemplate + "'" +
            ", tempTemplate='" + tempTemplate + "'" +
            ", uploadComplete='" + uploadComplete + "'" +
            '}';
    }
}
