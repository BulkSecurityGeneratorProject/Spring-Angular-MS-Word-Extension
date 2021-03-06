package be.storefront.imicloud.service.dto;

import be.storefront.imicloud.domain.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import javax.persistence.Lob;


/**
 * A DTO for the ImBlock entity.
 */
public class ImBlockDTO extends AbstractAuditingDTO implements Serializable {

    private Long id;

    private String label;

    @Lob
    private String content;

    @NotNull
    private Float position;

    private String guid;

    private String labelImageSource;


    private Long imMapId;

    private Long labelImageId;

    private String labelImageFilename;

    @JsonIgnore
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

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Float getPosition() {
        return position;
    }

    public void setPosition(Float position) {
        this.position = position;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLabelImageSource() {
        return labelImageSource;
    }

    public void setLabelImageSource(String labelImageSource) {
        this.labelImageSource = labelImageSource;
    }

    public Long getImMapId() {
        return imMapId;
    }

    public void setImMapId(Long imMapId) {
        this.imMapId = imMapId;
    }

    public Long getLabelImageId() {
        return labelImageId;
    }

    public void setLabelImageId(Long imageId) {
        this.labelImageId = imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ImBlockDTO imBlockDTO = (ImBlockDTO) o;

        if (!Objects.equals(id, imBlockDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Set<Image> getImages() {
        return images;
    }


    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "ImBlockDTO{" +
            "id=" + id +
            ", label='" + label + "'" +
            ", content='" + content + "'" +
            ", position='" + position + "'" +
            ", guid='" + guid + "'" +
            ", labelImageSource='" + labelImageSource + "'" +
            '}';
    }

    public void setLabelImageFilename(String labelImageFilename) {
        this.labelImageFilename = labelImageFilename;
    }

    public String getLabelImageFilename() {
        return this.labelImageFilename;
    }
}
