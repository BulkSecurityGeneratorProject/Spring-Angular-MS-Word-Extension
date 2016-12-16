package be.storefront.imicloud.service.dto;

import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;


/**
 * A DTO for the ImDocument entity.
 */
public class ImDocumentDTO implements Serializable {

    private Long id;

    private String language;

    private String password;

    @NotNull
    private String originalFilename;

    @NotNull
    @Lob
    private String originalXml;

    private ZonedDateTime createdAt;


    private Long folderId;
    
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
    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    public String getOriginalXml() {
        return originalXml;
    }

    public void setOriginalXml(String originalXml) {
        this.originalXml = originalXml;
    }
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
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
            ", originalFilename='" + originalFilename + "'" +
            ", originalXml='" + originalXml + "'" +
            ", createdAt='" + createdAt + "'" +
            '}';
    }
}
