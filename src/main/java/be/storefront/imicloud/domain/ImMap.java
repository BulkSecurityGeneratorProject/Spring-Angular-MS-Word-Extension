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
 * A ImMap.
 */
@Entity
@Table(name = "im_map")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "immap")
public class ImMap extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "guid")
    private String guid;

    @NotNull
    @Column(name = "label", nullable = false)
    private String label;

    @NotNull
    @Column(name = "position", nullable = false)
    private Float position;

    @ManyToOne
    private ImDocument imDocument;

    @OneToMany(mappedBy = "imMap")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ImBlock> blocks = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public ImMap guid(String guid) {
        this.guid = guid;
        return this;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLabel() {
        return label;
    }

    public ImMap label(String label) {
        this.label = label;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getPosition() {
        return position;
    }

    public ImMap position(Float position) {
        this.position = position;
        return this;
    }

    public void setPosition(Float position) {
        this.position = position;
    }

    public ImDocument getImDocument() {
        return imDocument;
    }

    public ImMap imDocument(ImDocument imDocument) {
        this.imDocument = imDocument;
        return this;
    }

    public void setImDocument(ImDocument imDocument) {
        this.imDocument = imDocument;
    }

    public Set<ImBlock> getBlocks() {
        return blocks;
    }

    public ImMap blocks(Set<ImBlock> imBlocks) {
        this.blocks = imBlocks;
        return this;
    }

    public ImMap addBlock(ImBlock imBlock) {
        blocks.add(imBlock);
        imBlock.setImMap(this);
        return this;
    }

    public ImMap removeBlock(ImBlock imBlock) {
        blocks.remove(imBlock);
        imBlock.setImMap(null);
        return this;
    }

    public void setBlocks(Set<ImBlock> imBlocks) {
        this.blocks = imBlocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImMap imMap = (ImMap) o;
        if (imMap.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, imMap.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ImMap{" +
            "id=" + id +
            ", guid='" + guid + "'" +
            ", label='" + label + "'" +
            ", position='" + position + "'" +
            '}';
    }
}
