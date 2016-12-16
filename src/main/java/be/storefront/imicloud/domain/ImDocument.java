package be.storefront.imicloud.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
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
public class ImDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "language")
    private String language;

    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @NotNull
    @Lob
    @Column(name = "original_xml", nullable = false)
    private String originalXml;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @ManyToOne
    private Folder folder;

    @OneToMany(mappedBy = "imDocument")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ImMap> maps = new HashSet<>();

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

    public String getOriginalFilename() {
        return originalFilename;
    }

    public ImDocument originalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
        return this;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ImDocument createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
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
            ", originalFilename='" + originalFilename + "'" +
            ", originalXml='" + originalXml + "'" +
            ", createdAt='" + createdAt + "'" +
            '}';
    }
}
