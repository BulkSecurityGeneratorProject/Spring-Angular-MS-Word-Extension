package be.storefront.imicloud.domain;

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
 * A Image.
 */
@Entity
@Table(name = "image")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "image")
public class Image extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "filename", nullable = false, unique = true)
    private String filename;

    @NotNull
    @Column(name = "content_type", nullable = false)
    private String contentType;

    @NotNull
    @Column(name = "image_width", nullable = false)
    private Integer imageWidth;

    @NotNull
    @Column(name = "image_height", nullable = false)
    private Integer imageHeight;

    @NotNull
    @Column(name = "content_length", nullable = false)
    private Long contentLength;

    @NotNull
    @Column(name = "secret", nullable = false)
    private String secret;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "image_im_block",
               joinColumns = @JoinColumn(name="images_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="im_blocks_id", referencedColumnName="ID"))
    private Set<ImBlock> imBlocks = new HashSet<>();

    @ManyToOne
    @NotNull
    private User uploadedByUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public Image filename(String filename) {
        this.filename = filename;
        return this;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public Image contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public Image imageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
        return this;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public Image imageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
        return this;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public Image contentLength(Long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    public String getSecret() {
        return secret;
    }

    public Image secret(String secret) {
        this.secret = secret;
        return this;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Set<ImBlock> getImBlocks() {
        return imBlocks;
    }

    public Image imBlocks(Set<ImBlock> imBlocks) {
        this.imBlocks = imBlocks;
        return this;
    }

    public Image addImBlock(ImBlock imBlock) {
        imBlocks.add(imBlock);
        return this;
    }

    public Image removeImBlock(ImBlock imBlock) {
        imBlocks.remove(imBlock);
        return this;
    }

    public void setImBlocks(Set<ImBlock> imBlocks) {
        this.imBlocks = imBlocks;
    }

    public User getUploadedByUser() {
        return uploadedByUser;
    }

    public Image uploadedByUser(User user) {
        this.uploadedByUser = user;
        return this;
    }

    public void setUploadedByUser(User user) {
        this.uploadedByUser = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        if (image.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, image.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Image{" +
            "id=" + id +
            ", filename='" + filename + "'" +
            ", contentType='" + contentType + "'" +
            ", imageWidth='" + imageWidth + "'" +
            ", imageHeight='" + imageHeight + "'" +
            ", contentLength='" + contentLength + "'" +
            ", secret='" + secret + "'" +
            '}';
    }
}
