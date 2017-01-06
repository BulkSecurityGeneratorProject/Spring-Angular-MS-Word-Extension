package be.storefront.imicloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A ImDocument.
 */
@Entity
@Table(name = "im_document")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "imdocument")
public class ImDocument extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "language")
    private String language;

    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "document_name", nullable = false)
    private String documentName;

    @NotNull
    @Lob
    @Column(name = "original_xml", nullable = false)
    private String originalXml;

    @NotNull
    @Size(min = 10, max = 10)
    @Column(name = "secret", length = 10, nullable = false)
    private String secret;

    @Column(name = "default_template")
    private String defaultTemplate;

    @Column(name = "upload_complete")
    private Boolean uploadComplete;

    @ManyToOne
    private Folder folder;

    @OneToMany(mappedBy = "imDocument", cascade=CascadeType.REMOVE)
    @OrderBy("position ASC")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ImMap> maps = new HashSet<>();

    @ManyToOne
    @NotNull
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public ImDocument language(String language) {
        this.language = language;
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPassword() {
        return password;
    }

    public ImDocument password(String password) {
        this.password = password;
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocumentName() {
        return documentName;
    }

    public ImDocument documentName(String documentName) {
        this.documentName = documentName;
        return this;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getOriginalXml() {
        return originalXml;
    }

    public ImDocument originalXml(String originalXml) {
        this.originalXml = originalXml;
        return this;
    }

    public void setOriginalXml(String originalXml) {
        this.originalXml = originalXml;
    }

    public String getSecret() {
        return secret;
    }

    public ImDocument secret(String secret) {
        this.secret = secret;
        return this;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public ImDocument defaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
        return this;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public Boolean isUploadComplete() {
        return uploadComplete;
    }

    public ImDocument uploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
        return this;
    }

    public void setUploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public Folder getFolder() {
        return folder;
    }

    public ImDocument folder(Folder folder) {
        this.folder = folder;
        return this;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Set<ImMap> getMaps() {
        return maps;
    }

    public ImDocument maps(Set<ImMap> imMaps) {
        this.maps = imMaps;
        return this;
    }

    public ImDocument addMap(ImMap imMap) {
        maps.add(imMap);
        imMap.setImDocument(this);
        return this;
    }

    public ImDocument removeMap(ImMap imMap) {
        maps.remove(imMap);
        imMap.setImDocument(null);
        return this;
    }

    public void setMaps(Set<ImMap> imMaps) {
        this.maps = imMaps;
    }

    public User getUser() {
        return user;
    }

    public ImDocument user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImDocument imDocument = (ImDocument) o;
        if (imDocument.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, imDocument.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImDocument{" +
            "id=" + id +
            ", language='" + language + "'" +
            ", password='" + password + "'" +
            ", documentName='" + documentName + "'" +
            ", originalXml='" + originalXml + "'" +
            ", secret='" + secret + "'" +
            ", defaultTemplate='" + defaultTemplate + "'" +
            ", uploadComplete='" + uploadComplete + "'" +
            '}';
    }
}
