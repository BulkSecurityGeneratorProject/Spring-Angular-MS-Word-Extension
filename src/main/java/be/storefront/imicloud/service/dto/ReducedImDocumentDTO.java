package be.storefront.imicloud.service.dto;

import be.storefront.imicloud.service.UrlHelperService;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the ImDocument entity.
 */
public class ReducedImDocumentDTO implements Serializable {

    private Long id;

    private String language;

    private String password;

    @NotNull
    private String documentName;

    private String defaultTemplate;

    private Long folderId;

    private Long userId;

    private String userEmail;

    private Long brandingId;
    private String brandingName;

    @JsonIgnore
    private UrlHelperService urlHelperService;

    public ReducedImDocumentDTO(){

    }

    public ReducedImDocumentDTO(ImDocumentDTO imDocumentDTO) {
        this.setBrandingId(imDocumentDTO.getBrandingId());
        this.setBrandingName(imDocumentDTO.getBrandingName());
        this.setDefaultTemplate(imDocumentDTO.getDefaultTemplate());
        this.setDocumentName(imDocumentDTO.getDocumentName());
        this.setId(imDocumentDTO.getId());
        this.setLanguage(imDocumentDTO.getLanguage());
        this.setPassword(imDocumentDTO.getPassword());
        this.setUrlHelperService(imDocumentDTO.getUrlHelperService());
        this.setUserEmail(imDocumentDTO.getUserEmail());
        this.setUserId(imDocumentDTO.getUserId());
    }

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



    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
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

        ReducedImDocumentDTO imDocumentDTO = (ReducedImDocumentDTO) o;

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
            ", documentName='" + documentName + "'" +
            ", defaultTemplate='" + defaultTemplate + "'" +
            '}';
    }
}
