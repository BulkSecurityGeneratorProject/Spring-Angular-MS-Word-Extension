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
    @Column(name = "filename", nullable = false)
    private String filename;

    @NotNull
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "image_im_block",
               joinColumns = @JoinColumn(name="images_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="im_blocks_id", referencedColumnName="ID"))
    private Set<ImBlock> imBlocks = new HashSet<>();

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

    public String getOriginalName() {
        return originalName;
    }

    public Image originalName(String originalName) {
        this.originalName = originalName;
        return this;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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
            ", originalName='" + originalName + "'" +
            '}';
    }
}
