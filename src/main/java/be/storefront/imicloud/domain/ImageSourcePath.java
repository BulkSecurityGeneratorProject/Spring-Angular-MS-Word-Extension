package be.storefront.imicloud.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ImageSourcePath.
 */
@Entity
@Table(name = "image_source_path", uniqueConstraints={
    @UniqueConstraint(columnNames = {"source", "im_document_id"})
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "imagesourcepath")
public class ImageSourcePath extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "upload_complete")
    private Boolean uploadComplete;

    @ManyToOne
    private Image image;

    @ManyToOne
    private ImDocument imDocument;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public ImageSourcePath source(String source) {
        this.source = source;
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean isUploadComplete() {
        return uploadComplete;
    }

    public ImageSourcePath uploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
        return this;
    }

    public void setUploadComplete(Boolean uploadComplete) {
        this.uploadComplete = uploadComplete;
    }

    public Image getImage() {
        return image;
    }

    public ImageSourcePath image(Image image) {
        this.image = image;
        return this;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ImDocument getImDocument() {
        return imDocument;
    }

    public ImageSourcePath imDocument(ImDocument imDocument) {
        this.imDocument = imDocument;
        return this;
    }

    public void setImDocument(ImDocument imDocument) {
        this.imDocument = imDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageSourcePath imageSourcePath = (ImageSourcePath) o;
        if (imageSourcePath.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, imageSourcePath.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImageSourcePath{" +
            "id=" + id +
            ", source='" + source + "'" +
            ", uploadComplete='" + uploadComplete + "'" +
            '}';
    }
}
