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
 * A ImBlock.
 */
@Entity
@Table(name = "im_block")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "imblock")
public class ImBlock extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "label")
    private String label;

    @Lob
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "position", nullable = false)
    private Float position;

    @ManyToOne
    private ImMap imMap;

    @OneToMany(mappedBy = "imBlock")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Image> images = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public ImBlock label(String label) {
        this.label = label;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public ImBlock content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getPosition() {
        return position;
    }

    public ImBlock position(Float position) {
        this.position = position;
        return this;
    }

    public void setPosition(Float position) {
        this.position = position;
    }

    public ImMap getImMap() {
        return imMap;
    }

    public ImBlock imMap(ImMap imMap) {
        this.imMap = imMap;
        return this;
    }

    public void setImMap(ImMap imMap) {
        this.imMap = imMap;
    }

    public Set<Image> getImages() {
        return images;
    }

    public ImBlock images(Set<Image> images) {
        this.images = images;
        return this;
    }

    public ImBlock addImage(Image image) {
        images.add(image);
        image.setImBlock(this);
        return this;
    }

    public ImBlock removeImage(Image image) {
        images.remove(image);
        image.setImBlock(null);
        return this;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImBlock imBlock = (ImBlock) o;
        if (imBlock.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, imBlock.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImBlock{" +
            "id=" + id +
            ", label='" + label + "'" +
            ", content='" + content + "'" +
            ", position='" + position + "'" +
            '}';
    }
}
